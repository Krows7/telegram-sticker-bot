package net.krows_team.emojitext;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.vdurmont.emoji.Emoji;

public class EmojiNode implements Node {

	private Emoji emoji;

	public EmojiNode(Emoji emoji) {
		this.emoji = emoji;
	}

	public void render(Graphics2D g, int x, int y, EmojiCache cache) {
		var img = getEmojiImage(g, cache);
		g.drawImage(img, x, y - (int) (1.5F * RenderUtils.getXHeight(g)), null);
	}

	@Override
	public int getWidth(Graphics2D g) {
		return (int) (RenderUtils.getXHeight(g) * 2);
	}

	private BufferedImage getEmojiImage(Graphics2D g, EmojiCache cache) {
		var height = getWidth(g);
		return RenderUtils.resizeImage(cache.getEmoji(emoji.getUnicode()), height, height);
	}

	@Override
	public String toString() {
		return String.format("EmojiNode[%s]", emoji);
	}
}
