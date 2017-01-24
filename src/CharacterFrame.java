

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.border.*;


public class CharacterFrame extends JInternalFrame implements ActionListener, MouseListener, ClipboardOwner {

	private static final long serialVersionUID = 1L;

	private KanjiDictionary parent;

	private final static int DEFAULT_FONT_SIZE = 200;
	private int fontSize = DEFAULT_FONT_SIZE;

	public CharacterFrame(KanjiDictionary parent) {
		super("Character",
				false /* resizable */,
				false /* closable */,
				false /* maximizable */,
				true /* iconifiable */);
		this.parent = parent;
	}

	private int codePoint = 0;

	public void updateFonts() {
		if (codePoint != 0) {
			setCharacter(codePoint);
		}
	}

	private JLabel characterLabel;

	public void setCharacter(int codePoint) {
		this.codePoint = codePoint;
		JPanel panel = new JPanel(new BorderLayout());
		{
			JLabel label = new JLabel("U+" + Integer.toString(codePoint, 16).toUpperCase());
			label.setBorder(new EtchedBorder());
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setFont(label.getFont().deriveFont((float)label.getFont().getSize() * 2));
			panel.add(label, BorderLayout.PAGE_START);
		}
		Font font;
		int fontSize;
		{
			String s = new String(new int[] { codePoint }, 0, 1);
			characterLabel = new JLabel(s);
			characterLabel.setBorder(new EtchedBorder());
			font = characterLabel.getFont();
			fontSize = this.fontSize;
			for (int i = 0;  i < parent.cFonts.length;  ++i) {
				if (parent.cFonts[i].canDisplay(codePoint)) {
					font = parent.cFonts[i];
					if (font.getName().equals("ZYSongKX-16_24_32_52")) {
						// このフォントだけサイズ固定。
						int[] fontSizes = new int[] { 52, 32, 24, 16 };
						for (int j = 0;  j < fontSizes.length;  ++j) {
							if (fontSize > fontSizes[j]) {
								fontSize = fontSizes[j];
								break;
							}
						}
					}
					break;
				}
			}
			characterLabel.setFont(font.deriveFont((float)fontSize));
			characterLabel.setHorizontalAlignment(JLabel.CENTER);
			characterLabel.addMouseListener(this);
			panel.add(characterLabel, BorderLayout.CENTER);
		}
		{
			JPanel panel2 = new JPanel();
			panel2.setBorder(new EtchedBorder());
			BoxLayout layout = new BoxLayout(panel2, BoxLayout.PAGE_AXIS);
			panel2.setLayout(layout);
			{
				JLabel label = new JLabel("font name: " + font.getName());
				label.setHorizontalAlignment(JLabel.CENTER);
				panel2.add(label);
			}
			{
				JLabel label = new JLabel("font size: " + Integer.toString(fontSize) + "pt");
				label.setHorizontalAlignment(JLabel.CENTER);
				panel2.add(label);
			}
			panel.add(panel2, BorderLayout.PAGE_END);
		}
		this.setContentPane(panel);
		this.pack();
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		if (command == null) {
			return;
		}
		if (command.equals("copyImage")) {
			Image image = new BufferedImage(characterLabel.getWidth(), characterLabel.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
			Color foreground = characterLabel.getForeground();
			Color background = characterLabel.getBackground();
			characterLabel.setForeground(Color.WHITE);
			characterLabel.setBackground(Color.BLACK);
			characterLabel.update(image.getGraphics());
			characterLabel.setForeground(foreground);
			characterLabel.setBackground(background);
			Clipboard clipboard = getToolkit().getSystemClipboard();
			clipboard.setContents(new ImageSelection(image), this);
			JOptionPane.showMessageDialog(null, "copied image data to clipboard.");
		} else if (command.indexOf("copy") == 0) {
			String s = command.substring("copy".length());
			Clipboard clipboard = getToolkit().getSystemClipboard();
			StringSelection stringSelection = new StringSelection(s);
			clipboard.setContents(stringSelection, this);
			JOptionPane.showMessageDialog(null, "copied \"" + s + "\" to clipboard.");
		} else if (command.equals("changeSize")) {
			JPanel panel = new JPanel();
			JLabel label = new JLabel("font size:");
			panel.add(label);
			JTextField sizeText = new JTextField(Integer.toString(fontSize), 4);
			panel.add(sizeText);
			label = new JLabel("pt");
			panel.add(label);
			switch (JOptionPane.showConfirmDialog(null, panel, "Change Font Size", JOptionPane.OK_CANCEL_OPTION)) {
			case JOptionPane.OK_OPTION:
				try {
					fontSize = Integer.parseInt(sizeText.getText());
					if (fontSize <= 0) {
						fontSize = DEFAULT_FONT_SIZE;
					}
					setCharacter(codePoint);
				} catch (NumberFormatException e) {
				}
				break;
			}
		}
	}

	public void mouseEntered(MouseEvent event) {
	}
	public void mouseExited(MouseEvent event) {
	}
	public void mouseClicked(MouseEvent event) {
		if (SwingUtilities.isRightMouseButton(event)) {
			JPopupMenu menu = new JPopupMenu();
			{
				JMenuItem item = new JMenuItem("Copy Character To Clipboard");
				item.setActionCommand("copy" + new String(new int[] { codePoint }, 0, 1));
				item.addActionListener(this);
				menu.add(item);
			}
			{
				int[] codePoints = convertCodePoints(codePoint);
				String s = "";
				switch (codePoints.length) {
				case 1:
					s = String.format("\\u%04X", codePoints[0]);
					break;
				case 2:
					s = String.format("\\u%04X\\u%04X", codePoints[0], codePoints[1]);
					break;
				}
				JMenuItem item = new JMenuItem("Copy \"" + s + "\" To Clipboard");
				item.setActionCommand("copy" + s);
				item.addActionListener(this);
				menu.add(item);
			}
			{
				int[] codePoints = convertCodePoints(codePoint);
				String s = "";
				switch (codePoints.length) {
				case 1:
					s = String.format("&#x%04X;", codePoints[0]);
					break;
				case 2:
					s = String.format("&#x%04X;&#x%04X;", codePoints[0], codePoints[1]);
					break;
				}
				JMenuItem item = new JMenuItem("Copy \"" + s + "\" To Clipboard");
				item.setActionCommand("copy" + s);
				item.addActionListener(this);
				menu.add(item);
			}
			menu.addSeparator();
			{
				JMenuItem item = new JMenuItem("Copy Image Data To Clipboard");
				item.setActionCommand("copyImage");
				item.addActionListener(this);
				menu.add(item);
			}
			menu.addSeparator();
			{
				JMenuItem item = new JMenuItem("Change Font Size...");
				item.setActionCommand("changeSize");
				item.addActionListener(this);
				menu.add(item);
			}
			menu.show(event.getComponent(), event.getX(), event.getY());
		} else if (SwingUtilities.isLeftMouseButton(event)) {
			ActionEvent actionEvent = new ActionEvent(event.getSource(), ActionEvent.ACTION_PERFORMED, "copy" + new String(new int[] { codePoint }, 0, 1));
			actionPerformed(actionEvent);
		}
	}
	public void mousePressed(MouseEvent event) {
	}
	public void mouseReleased(MouseEvent event) {
	}
	
	private int[] convertCodePoints(int codePoint) {
		if (codePoint < 0x10000) {
			return new int[] { codePoint };
		} else {
			return new int[] {
					0xd800 + ((codePoint - 0x10000) >> 10),
					0xdc00 + ((codePoint - 0x10000) & 0x3ff)
			};
		}
	}

	public void lostOwnership(Clipboard clipboard, Transferable transferable) {
	}
}


class ImageSelection implements Transferable {

	private Image image = null;

	public ImageSelection(Image image) {
		this.image = image;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return DataFlavor.imageFlavor.equals(flavor);
	}

	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { DataFlavor.imageFlavor };
	}

	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException {
		if (DataFlavor.imageFlavor.equals(flavor)) {
			return image;
		}
		throw new UnsupportedFlavorException(flavor);
	}

	public void lostOwnership(Clipboard clipboard, Transferable transferable) {
		image = null;
	}
}
