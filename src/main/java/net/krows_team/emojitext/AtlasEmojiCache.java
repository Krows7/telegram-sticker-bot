package net.krows_team.emojitext;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class AtlasEmojiCache extends EmojiCache {

	public AtlasEmojiCache(List<String> files, int emojiSize, EmojiFileParser parser) throws IOException {
		super(parser);
		for (String file : files)
			loadPage(new File(file), emojiSize);
	}

	protected void loadPage(File file, int emojiSize) throws IOException {
		BufferedImage atlas = ImageIO.read(file);
		int width = atlas.getWidth() / emojiSize;
		int height = atlas.getHeight() / emojiSize;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				emojis.add(atlas.getSubimage(x * emojiSize, y * emojiSize, emojiSize, emojiSize));
			}
		}
	}

	@Override
	protected List<BufferedImage> loadEmojis() {
		return new ArrayList<>();
	}
}
