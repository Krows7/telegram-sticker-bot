package net.krows_team.emojitext.ext.telegram;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import net.krows_team.emojitext.EmojiFileParser;
import net.krows_team.emojitext.FileUtils;

// Latest version of a file: https://github.com/desktop-app/lib_ui/blob/master/emoji.txt
public class TelegramEmojiParser extends EmojiFileParser {

	private static final int TELEGRAM_LAST_EMOJI_LINE = 325;
	private static final int TELEGRAM_ADDITIONAL_EMOJI_LINE = 653;

	public TelegramEmojiParser() throws IOException {
		super(Path.of(FileUtils.getResourcePath("emoji.txt")));
	}

	@Override
	protected void loadEmojis(Path path) throws IOException {
		List<String> lines = Files.readAllLines(path);
		int index = 0;
		for (int i = 0; i < TELEGRAM_LAST_EMOJI_LINE; i++) {
			String[] emojis = lines.get(i).split(",");
			for (String em : emojis) {
				if (!em.isEmpty())
					emojiMap.put(em.substring(1, em.length() - 1), index++);
			}
		}
		String[] last = lines.get(TELEGRAM_ADDITIONAL_EMOJI_LINE).split(",");
		emojiMap.put(last[0].substring(1, last[0].length() - 1), index++);
		emojiMap.put(last[1].substring(1, last[1].length() - 1), index);
	}
}
