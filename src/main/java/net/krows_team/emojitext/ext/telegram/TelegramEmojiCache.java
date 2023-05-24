package net.krows_team.emojitext.ext.telegram;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import net.krows_team.emojitext.AtlasEmojiCache;

public class TelegramEmojiCache extends AtlasEmojiCache {

	private static final int EMOJI_SIZE = 72;
	private static final List<String> FILES = IntStream.rangeClosed(1, 7).mapToObj("emoji_%s.png"::formatted).toList();

	public TelegramEmojiCache() throws IOException {
		super(FILES, EMOJI_SIZE, new TelegramEmojiParser());
	}
}
