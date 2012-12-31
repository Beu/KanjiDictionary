
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class SelectingFrame extends JInternalFrame
		implements ActionListener {

	private static final long serialVersionUID = 1L;

	final static int DEFAULT_FONT_SIZE = 32;
	int fontSize = DEFAULT_FONT_SIZE;
	
	KanjiDictionary parent;
	
	public SelectingFrame(KanjiDictionary parent) {
		super("Selecting",
				true /* resizable */,
				false /* closable */,
				false /* maximizable */,
				true /* iconifiable */);
		this.parent = parent;
	}
	
	int[] codePoints = null;

	public void updateFonts() {
		if (codePoints != null) {
			setCharacters(codePoints);
		}
	}
	
	public void setCharacters(int[] codePoints) {
		this.codePoints = new int[codePoints.length];
		for (int i = 0;  i < codePoints.length;  ++i) {
			this.codePoints[i] = codePoints[i];
		}

		Font[] fonts = new Font[parent.cFonts.length];
		for (int i = 0;  i < fonts.length;  ++i) {
			fonts[i] = parent.cFonts[i].deriveFont((float)fontSize);
		}
		JPanel panel = new JPanel(new GridLayout((codePoints.length - 1) / 16 + 1, 16));
		for (int i = 0;  i < codePoints.length;  ++i) {
			JButton button = new JButton(new String(codePoints, i, 1));
			Font font = null;
			for (int j = 0;  j < fonts.length;  ++j) {
				if (fonts[j].canDisplay(codePoints[i])) {
					font = fonts[j];
					break;
				}
			}
			if (font == null) {
				font = button.getFont().deriveFont((float)fontSize);
System.err.printf("U+%X could not be displayed.\n", codePoints[i]);
			}
			button.setFont(font);
			button.setMargin(new Insets(2, 2, 2, 2));
			button.setToolTipText("U+" + Integer.toString(codePoints[i], 16).toUpperCase());
			button.setActionCommand("U+" + Integer.toString(codePoints[i], 16).toUpperCase());
			button.addActionListener(this);
			panel.add(button);
		}
		if (codePoints.length % 16 > 0) {
			for (int i = 16 - codePoints.length % 16;  i > 0;  --i) {
				panel.add(new JLabel(""));
			}
		}
		// 一枚噛まさないとボタンが縦長になる。
		JPanel panel2 = new JPanel();
		panel2.add(panel);
		JScrollPane scrollPane = new JScrollPane(panel2);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//		int height = panel.getHeight();
		this.setContentPane(scrollPane);
		this.pack();
		scrollPane.setPreferredSize(new Dimension(scrollPane.getWidth(), 200));
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.pack();
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent event) {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		String sCommand = event.getActionCommand();
		if (sCommand != null && sCommand.indexOf("U+") == 0) {
			parent.selectCharacter(Integer.parseInt(sCommand.substring(2), 16));
		}
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
}
