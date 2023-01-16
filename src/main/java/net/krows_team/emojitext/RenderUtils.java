package net.krows_team.emojitext;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class RenderUtils {

	public static double getXHeight(Graphics2D g) {
		return g.getFont().createGlyphVector(g.getFontRenderContext(), "x").getVisualBounds().getHeight();
	}

	public static BufferedImage resizeImage(BufferedImage img, int newW, int newH) {
		var tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		var dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		var g = dimg.createGraphics();
		g.drawImage(tmp, 0, 0, null);
		g.dispose();

		return dimg;
	}
}
