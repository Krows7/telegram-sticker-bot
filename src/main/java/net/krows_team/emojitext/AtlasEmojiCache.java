package net.krows_team.emojitext;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class AtlasEmojiCache extends EmojiCache {

	public AtlasEmojiCache(List<String> files, int emojiSize, EmojiFileParser parser) throws IOException {
		super(parser);
		for (String file : files)
			loadPage(file, emojiSize);
	}

	protected void loadPage(String file, int emojiSize) throws IOException {
		var atlas = ImageIO.read(FileUtils.getResource(file));
		var width = atlas.getWidth() / emojiSize;
		var height = atlas.getHeight() / emojiSize;
		for (var y = 0; y < height; y++) {
			for (var x = 0; x < width; x++) {
				emojis.add(atlas.getSubimage(x * emojiSize, y * emojiSize, emojiSize, emojiSize));
			}
		}
	}

	@Override
	protected List<BufferedImage> loadEmojis() {
		return new ArrayList<>();
	}
}
