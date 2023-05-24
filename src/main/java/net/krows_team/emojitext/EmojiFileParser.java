package net.krows_team.emojitext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class EmojiFileParser {

	protected Map<String, Integer> emojiMap;

	protected EmojiFileParser(String path) throws IOException {
		emojiMap = new HashMap<>();
		loadEmojis(path);
	}

	protected abstract void loadEmojis(String path) throws IOException;

	public int getEmojiIndex(String emoji) {
		return emojiMap.get(emoji);
	}
}
