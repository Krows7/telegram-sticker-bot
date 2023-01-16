package net.krows_team.emojitext;

import java.awt.Graphics2D;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.text.AttributedString;
import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import com.pengrad.telegrambot.model.MessageEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.krows_team.sticker_bot.StickerRenderer;

@Slf4j
@AllArgsConstructor
public class TextRenderer {

	@Getter
	@AllArgsConstructor
	public static class TextMetrics {
		private int lineCount;
		private float lastLineWidth;
		private float height;
	}

	private EmojiCache cache;

	// TODO
	private Entry<TextAttribute, Object> get(MessageEntity entity) {
		TextAttribute key = null;
		Object value = null;
		switch (entity.type()) {
		case bold -> {
			key = TextAttribute.FONT;
			value = StickerRenderer.NAME_FONT;
		}
		case strikethrough -> {
			key = TextAttribute.STRIKETHROUGH;
			value = TextAttribute.STRIKETHROUGH_ON;
		}
		case underline -> {
			key = TextAttribute.UNDERLINE;
			value = TextAttribute.UNDERLINE_LOW_TWO_PIXEL;
		}
		default -> {
		}
		}
		return key == null ? null : new AbstractMap.SimpleEntry<>(key, value);
	}

	private Entry<TextLayout, Boolean> nextLayout(String text, LineBreakMeasurer measurer, int width, boolean requireNextWord) {
		var next = measurer.nextOffset(width);
		var limit = next;
		var found = false;
		if (limit <= text.length()) {
			for (var i = measurer.getPosition(); i < next; i++) {
				if (text.charAt(i) == '\n') {
					found = true;
					limit = i + 1;
					break;
				}
			}
		}
		return new AbstractMap.SimpleEntry<>(measurer.nextLayout(width, limit, requireNextWord), found);
	}

	private TextMetrics processText(Graphics2D g, String text, int x, int y, int width, BiConsumer<EmojiNode, Point2D.Float> emojiAction,
			BiConsumer<TextLayout, Point2D.Float> textAction, MessageEntity[] formats) {
		TextLayout layout = null;
		var nodes = ExtendedEmojiParser.parseToNodes(text);

		log.debug("getBounds {}", width);
		log.debug("Font Height: {}", g.getFontMetrics().getHeight());
		log.debug("Total Nodes: {}", nodes.size());

		var currentLineWidth = 0;
		var lineCount = 1;

		float heightUnit = g.getFontMetrics().getHeight();
		var height = heightUnit;

		for (Node node : nodes) {
			log.debug("Node: {}", node);
			var nodeWidth = node.getWidth(g);
			if (node instanceof EmojiNode emojiNode) {
				if (nodeWidth > width - currentLineWidth) {
					log.debug("EmojiNode width: {}; Left space: {}", nodeWidth, width - currentLineWidth);
					height += heightUnit;
					lineCount++;
					currentLineWidth = 0;
				}
				if (emojiAction != null) emojiAction.accept(emojiNode, new Point2D.Float(currentLineWidth + x, height + y));
				currentLineWidth += nodeWidth + 1;
			} else {
				currentLineWidth = 0;
				var nodeText = new AttributedString(text);
				nodeText.addAttribute(TextAttribute.FONT, g.getFont());
				if (formats != null) {
					for (var entity : formats) {
						var pairEntry = get(entity);
						if (pairEntry != null) nodeText.addAttribute(pairEntry.getKey(), pairEntry.getValue(), entity.offset(),
								entity.offset() + entity.length());
					}
				}
				var nodeTextIterator = nodeText.getIterator();
				var nodeBegin = nodeTextIterator.getBeginIndex();
				var nodeEnd = nodeTextIterator.getEndIndex();
				var lineMeasurer = new LineBreakMeasurer(nodeTextIterator, g.getFontRenderContext());
				lineMeasurer.setPosition(nodeBegin);

				var previousLineEnd = lineMeasurer.getPosition();
				var previousNull = false;

				while (lineMeasurer.getPosition() < nodeEnd) {
					var entry = nextLayout(text, lineMeasurer, width - currentLineWidth, !previousNull);
					layout = entry.getKey();
					boolean isNewLine = entry.getValue();
					if (layout != null && log.isDebugEnabled())
						log.debug("Next Node line: [{}]", text.substring(previousLineEnd, lineMeasurer.getPosition()));
					if (layout == null || (nodeWidth = layout.getBounds().getBounds().width) > width - currentLineWidth) {
						lineCount++;
						height += heightUnit;
						currentLineWidth = 0;
					}
					previousLineEnd = lineMeasurer.getPosition();
					if (layout != null) {
						log.debug("Layout Height: {}", layout.getAscent() + layout.getDescent() + layout.getLeading());
						if (textAction != null) textAction.accept(layout, new Point2D.Float(currentLineWidth + x, height + y));
						currentLineWidth += nodeWidth + 1;
					}
					previousNull = layout == null;
					if (isNewLine) {
						log.debug("New Line");
						lineCount++;
						height += heightUnit;
						currentLineWidth = 0;
					}
				}
			}
		}
		return new TextMetrics(lineCount, currentLineWidth, height);
	}

	public void renderText(Graphics2D g, String text, int x, int y, int width, MessageEntity[] formats) {
		processText(g, text, x, y - g.getFontMetrics().getHeight(), width,
				(emoji, point) -> emoji.render(g, (int) point.x, (int) point.y, cache), (layout, point) -> layout.draw(g, point.x, point.y),
				formats);
	}

	public TextMetrics getMetrics(Graphics2D g, String text, int width, MessageEntity[] formats) {
		return processText(g, text, 0, 0, width, null, null, formats);
	}
}
