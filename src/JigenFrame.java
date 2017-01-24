
import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.text.*;


public class JigenFrame extends JInternalFrame {

	private static final long serialVersionUID = 1L;

	private final static int DEFAULT_FONT_SIZE = 24;
	private int fontSize = DEFAULT_FONT_SIZE;

	private KanjiDictionary parent;

	public JigenFrame(KanjiDictionary parent) {
		super("字源",
				true /* resizable */,
				false /* closable */,
				false /* maximizable */,
				true /* iconifiable */);
		this.parent = parent;
	}

	private int codePoint = 0;

	public void updateFonts() throws java.io.IOException {
		if (codePoint != 0) {
			setCharacter(codePoint);
		}
	}

	@SuppressWarnings("unchecked")
	public void setCharacter(int codePoint) throws java.io.IOException {
		LinkedList<String> sLineList = (LinkedList<String>) parent.jigen.characterMap.get(new Integer(codePoint));
		this.codePoint = codePoint;

		if (sLineList == null) {
			JPanel panel = new JPanel();
			JLabel label = new JLabel(String.format("information of U+%X was not found.", codePoint));
			panel.add(label);
			this.setContentPane(panel);
			this.pack();
			this.setVisible(true);
			try {
				this.setIcon(true);
			} catch (java.beans.PropertyVetoException e) {
				e.printStackTrace();
			}
			return;
		}

		JTextPane textPane = new JTextPane();
		textPane.setMargin(new Insets(8, 8, 8, 8));
		StyledDocument document = textPane.getStyledDocument();
		SimpleAttributeSet attributeSet0 = new SimpleAttributeSet();
		StyleConstants.setFontSize(attributeSet0, fontSize);
		StyleConstants.setLineSpacing(attributeSet0, (float)(2.0 / 5.0));
		textPane.setParagraphAttributes(attributeSet0, true);
		SimpleAttributeSet[] attributeSets = new SimpleAttributeSet[parent.jFonts.length];
		for (int i = 0;  i < attributeSets.length;  ++i) {
			attributeSets[i] = new SimpleAttributeSet(attributeSet0);
			StyleConstants.setFontFamily(attributeSets[i], parent.jFonts[i].getFamily());
		}

		Iterator<String> iter = sLineList.iterator();
		while (iter.hasNext()) {
			String line = iter.next();
			if (line.indexOf("音韻:") == 0) {
				line = "【音韻】" + line.substring(3);
			} else if (line.indexOf("字解註:") == 0) {
				line = "【字解註】" + line.substring(4);
				while (line.indexOf("<標識>") >= 0) {
					line = line.replaceFirst("<標識>([^<]*)</標識>", "[$1]");
				}
			} else if (line.indexOf("解:") == 0) {
				line = "【解】" + line.substring(2);
				while (line.indexOf("<音>") >= 0) {
					line = line.replaceFirst("<音>([^<]*)</音>", "($1)");
				}
				while (line.indexOf("<標識>") >= 0) {
					line = line.replaceFirst("<標識>([^<]*)</標識>", "[$1]");
				}
			} else if (line.indexOf("解字:") == 0) {
				line = "【解字】" + line.substring(3);
				while (line.indexOf("<標識>") >= 0) {
					line = line.replaceFirst("<標識>([^<]*)</標識>", "[$1]");
				}
			}
			if (line.charAt(line.length() - 1) != '\n') {
				line += "\n";
			}
			while (!line.equals("")) {
				codePoint = line.codePointAt(0);
				int index = 0;
				for (;  index < parent.jFonts.length;  ++index) {
					if (parent.jFonts[index].canDisplay(codePoint)) {
						break;
					}
				}
				AttributeSet attributeSet = attributeSet0;
				if (index < parent.jFonts.length) {
					attributeSet = attributeSets[index];
				}
				String sC = new String(new int[] { codePoint }, 0, 1);
				try {
					document.insertString(document.getLength(), sC, attributeSet);
				} catch (BadLocationException e) {
				}
				line = line.substring(sC.length());
			}
		}
		textPane.setEditable(false);

		this.setContentPane(new JScrollPane(textPane));
		this.setPreferredSize(new Dimension(480, 320));
		this.pack();
		this.setVisible(true);
	}
}
