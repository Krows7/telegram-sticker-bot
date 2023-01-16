package net.krows_team.emojitext;

import java.awt.Graphics2D;

public class TextNode implements Node {

	private String text;

	public TextNode(String text) {
		this.text = text;
	}

	@Override
	public int getWidth(Graphics2D g) {
		return g.getFontMetrics().stringWidth(text);
	}

	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return String.format("TextNode[%s]", text);
	}
}
