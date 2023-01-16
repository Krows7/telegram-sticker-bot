package net.krows_team.emojitext;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

public abstract class EmojiCache {

	protected List<BufferedImage> emojis;

	protected EmojiFileParser parser;

	protected EmojiCache(EmojiFileParser parser) {
		this.parser = parser;
		emojis = Objects.requireNonNull(loadEmojis(), "Emoji List can't be null");
	}

	protected abstract List<BufferedImage> loadEmojis();

	public BufferedImage getEmoji(String emoji) {
		return emojis.get(parser.getEmojiIndex(emoji));
	}
}
