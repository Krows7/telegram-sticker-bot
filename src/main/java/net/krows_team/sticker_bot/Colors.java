package net.krows_team.sticker_bot;

import java.awt.Color;

public enum Colors {
	VIOLET(0xB48BF2), PINK(0xFF5694), CYAN(0x57D4E3), BLUE(0x65BDF3), GREEN(0x63AA55), RED(0xFB6158), ORANGE(0xFAA351);

	private int color;

	Colors(int color) {
		this.color = color;
	}

	public static Colors getFixed(String seed) {
		return Colors.values()[Math.abs(seed.hashCode() % Colors.values().length)];
	}

	public static Colors random() {
		return Colors.values()[(int) (Colors.values().length * Math.random())];
	}

	public Color createColor() {
		return new Color(color);
	}
}
