package net.krows_team.sticker_bot;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import net.krows_team.emojitext.ExtendedEmojiParser;
import net.krows_team.emojitext.RenderUtils;
import net.krows_team.emojitext.TextRenderer;
import net.krows_team.emojitext.ext.telegram.TelegramEmojiCache;

@Slf4j
public class StickerRenderer {

	private static final int MAX_WIDTH = 512;
	private static final int BACKGROUND_COLOR = 0x1c2028;
	private static final int TIME_COLOR = 0x787D87;
	private static final int AVATAR_DIAMETER = 53;
	private static final int TIME_HEIGHT = 15;
	private static final int TIME_BASELINE = 12;
	private static final int TIME_X = 59;
	private static final int MAX_TEXT_TIME_GAP = 13;
	private static final int MAX_TEXT_GAP = 20;
	private static final int PROFILE_LETTERS_BASELINE = 16;
	private static final int TIME_10_OFFSET = 14;
	private static final int DEFAULT_SCREEN_RESOLUTION = 120;
	private static final int SCREEN_RESOLUTION = getScreenResolution();

	private static final float DPI = 72.0F;
	private static final float FONT_SIZE = 12.25F;
	private static final float FONT_NAME_SIZE = 12F;
	private static final float FONT_PROFILE_SIZE = 17F;
	private static final float MESSAGE_BLOCK_X = AVATAR_DIAMETER + 15F;
	private static final float MESSAGE_BLOCK_ROUND_RADIUS = 17;
	private static final float MESSAGE_BLOCK_BEGIN_RADIUS = 12;
	private static final float MESSAGE_TEXT_X = MESSAGE_BLOCK_X + 21;
	private static final float MESSAGE_NAME_BASELINE = 35;
	private static final float MESSAGE_TEXT_BASELINE = 63;

	private static final String FONT_NAME = "Open Sans Medium";

	public static final Font NAME_FONT = new Font(FONT_NAME, Font.BOLD, 0).deriveFont(fitSize(FONT_NAME_SIZE));
	private static final Font TEXT_FONT = new Font(FONT_NAME, Font.PLAIN, 0).deriveFont(fitSize(FONT_SIZE));

	private StickerData data;

	private BufferedImage img;

	private Graphics2D g;

	private TextRenderer textRenderer;

	private boolean timeMore10 = false;

	private int height = 1;
	private int width = MAX_WIDTH;

	public StickerRenderer() throws IOException {
		textRenderer = new TextRenderer(new TelegramEmojiCache());
		ExtendedEmojiParser.updateEmojis();
	}

	private void init() {
		var calendar = Calendar.getInstance();
		calendar.setTime(new Date(data.timestamp));
		timeMore10 = calendar.get(Calendar.HOUR_OF_DAY) > 9;
	}

	public BufferedImage fitImage() {
		if (img.getWidth() != MAX_WIDTH)
			return RenderUtils.resizeImage(img, MAX_WIDTH, (int) (img.getHeight() * ((float) MAX_WIDTH / img.getWidth())));
		return img;
	}

	private void fixSize(String text) {
		var metrics = textRenderer.getMetrics(g, text, (int) (MAX_WIDTH - MESSAGE_TEXT_X - MAX_TEXT_GAP), data.formats);
		var timeAdjust = TIME_X + MAX_TEXT_TIME_GAP + metrics.getLastLineWidth() <= MAX_WIDTH - MESSAGE_TEXT_X
				- (timeMore10 ? TIME_10_OFFSET : 0);
		height = (int) (metrics.getHeight() + MESSAGE_NAME_BASELINE + 14
				+ (timeAdjust ? TIME_BASELINE / 2.0 : TIME_HEIGHT + TIME_BASELINE));

		var nameWidth = getNameWidth(g, data.renderName);
		var mx = MESSAGE_TEXT_X + TIME_X + (timeMore10 ? TIME_10_OFFSET : 0)
				+ Math.max(nameWidth + MESSAGE_TEXT_X - MESSAGE_BLOCK_X, metrics.getLastLineWidth() + MAX_TEXT_TIME_GAP);

		log.debug("fixSize1()");
		log.debug("Message Height: {}", metrics.getHeight());
		log.debug("Time Adjust: {}", timeAdjust);
		log.debug("Last line width: {}", metrics.getLastLineWidth());
		log.debug("Max Width: {}", mx);

		if (timeAdjust) width = (int) Math.min(MAX_WIDTH, mx);
	}

	private BufferedImage createImage() {
		return img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	private void createRender() {
		createImage();
		initGraphics(img);
	}

	public BufferedImage renderMessage(StickerData data) {
		this.data = data;
		init();
		createRender();
		fixSize(data.text);
		createRender();
		fillTransparent(g, width, height);
		drawAvatar(data.avatar.orElse(this::renderDefaultProfilePicture), g, height);
		drawMessageBlock(g, width, height);
		renderName(g, data.renderName);
		renderText(g, data.text);
		renderTime(g, data.timestamp, width, height);
		// TODO Fix if Height > 512
		return fitImage();
	}

	public int getNameWidth(Graphics2D g, String name) {
		g.setFont(NAME_FONT);
		return g.getFontMetrics().stringWidth(name);
	}

	public void renderText(Graphics2D g, String text) {
		g.setColor(new Color(0xEBEBEB));
		g.setFont(TEXT_FONT);
		textRenderer.renderText(g, text, (int) MESSAGE_TEXT_X, (int) MESSAGE_TEXT_BASELINE, (int) (width - MESSAGE_TEXT_X - MAX_TEXT_GAP),
				data.formats);
	}

	private void initGraphics(BufferedImage img) {
		g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g.setFont(TEXT_FONT);
	}

	private void fillTransparent(Graphics2D g, int width, int height) {
		g.setColor(new Color(0, true));
		g.fillRect(0, 0, width, height);
	}

	private void drawAvatar(BufferedImage avatar, Graphics2D g, int height) {
		float diameter = AVATAR_DIAMETER;
		var clip = new Ellipse2D.Float(0, height - diameter, diameter, diameter);
		g.setPaint(new TexturePaint(RenderUtils.resizeImage(avatar, AVATAR_DIAMETER, AVATAR_DIAMETER),
				new Rectangle(0, height - AVATAR_DIAMETER, AVATAR_DIAMETER, AVATAR_DIAMETER)));
		g.fill(clip);
	}

	private void drawMessageBlock(Graphics2D g, int width, int height) {
		g.setColor(new Color(BACKGROUND_COLOR));
		g.fillRoundRect((int) MESSAGE_BLOCK_X, 0, width - (int) MESSAGE_BLOCK_X, height, (int) MESSAGE_BLOCK_ROUND_RADIUS,
				(int) MESSAGE_BLOCK_ROUND_RADIUS);
		var diameter = MESSAGE_BLOCK_BEGIN_RADIUS * 2;
		var area = new Area(new Rectangle2D.Float(MESSAGE_BLOCK_X - MESSAGE_BLOCK_BEGIN_RADIUS, height - MESSAGE_BLOCK_BEGIN_RADIUS,
				diameter, MESSAGE_BLOCK_BEGIN_RADIUS));
		var clip = new Area(new Ellipse2D.Float(MESSAGE_BLOCK_X - diameter, height - diameter, diameter, diameter));
		area.subtract(clip);
		g.fill(area);
	}

	private void renderName(Graphics2D g, String name) {
		renderText(g, name, MESSAGE_TEXT_X, MESSAGE_NAME_BASELINE, data.color, NAME_FONT);
	}

	private void renderTime(Graphics2D g, long timestamp, int width, int height) {
		var time = new SimpleDateFormat("H:mm").format(new Date(timestamp));
		var w = width - TIME_X;
		if (timeMore10) w -= TIME_10_OFFSET;
		var h = height - TIME_BASELINE;
		renderText(g, time, w, h, new Color(TIME_COLOR));
	}

	public static int getXHeight(Graphics2D g) {
		var frc = g.getFontRenderContext();
		var gv = g.getFont().createGlyphVector(frc, "x");
		return gv.getPixelBounds(null, 0, 0).height;
	}

	private void renderText(Graphics2D g, String text, float x, float y, Color color) {
		renderText(g, text, x, y, color, TEXT_FONT);
	}

	private void renderText(Graphics2D g, String text, float x, float y, Color color, Font font) {
		g.setColor(color);
		g.setFont(font);
		g.drawString(text, x, y);
	}

	public static int getScreenResolution() {
		return GraphicsEnvironment.isHeadless() ? DEFAULT_SCREEN_RESOLUTION : Toolkit.getDefaultToolkit().getScreenResolution();
	}

	private static float fitSize(float pt) {
		return pt / (DPI / SCREEN_RESOLUTION);
	}

	private BufferedImage renderDefaultProfilePicture(String r) {
		var profile = new BufferedImage(AVATAR_DIAMETER, AVATAR_DIAMETER, BufferedImage.TYPE_INT_ARGB);
		var graphics = profile.createGraphics();
		graphics.setColor(data.color);
		graphics.fillRect(0, 0, AVATAR_DIAMETER, AVATAR_DIAMETER);
		graphics.setColor(Color.WHITE);
		graphics.setFont(TEXT_FONT.deriveFont(fitSize(FONT_PROFILE_SIZE)));
		var x = (int) graphics.getFontMetrics().getStringBounds(r, graphics).getWidth();
		graphics.drawString(r, (AVATAR_DIAMETER - x) / 2, profile.getHeight() - PROFILE_LETTERS_BASELINE);
		return profile;
	}
}