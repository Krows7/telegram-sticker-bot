package net.krows_team.emojitext;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public abstract class EmojiFileParser {

	protected Map<String, Integer> emojiMap;

	protected EmojiFileParser(Path path) throws IOException {
		emojiMap = new HashMap<>();
		loadEmojis(path);
	}

	protected abstract void loadEmojis(Path path) throws IOException;

	public int getEmojiIndex(String emoji) {
		return emojiMap.get(emoji);
	}
}
