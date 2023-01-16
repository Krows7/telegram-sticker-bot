package net.krows_team.sticker_bot.util;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TransferableImage implements Transferable {

	private Image img;

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (flavor.equals(DataFlavor.imageFlavor) && img != null) return img;
		throw new UnsupportedFlavorException(flavor);
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { DataFlavor.imageFlavor };
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		for (var e : getTransferDataFlavors())
			if (flavor.equals(e)) return true;
		return false;
	}
}