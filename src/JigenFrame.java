
import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.text.*;


public class JigenFrame extends JInternalFrame {

	private static final long serialVersionUID = 1L;

	final static int DEFAULT_FONT_SIZE = 24;
	int fontSize = DEFAULT_FONT_SIZE;
	
	KanjiDictionary parent;
	
	public JigenFrame(KanjiDictionary parent) {
		super("字源",
				true /* resizable */,
				false /* closable */,
				false /* maximizable */,
				true /* iconifiable */);
		this.parent = parent;
	}

	int codePoint = 0;

	public void updateFonts() throws java.io.IOException {
		if (codePoint != 0) {
			setCharacter(codePoint);
		}
	}

	@SuppressWarnings("unchecked")
	public void setCharacter(int codePoint) throws java.io.IOException {
		LinkedList<String> sLineList = (LinkedList<String>)
				parent.jigen.characterMap.get(new Integer(codePoint));
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
		SimpleAttributeSet[] attributeSets
				= new SimpleAttributeSet[parent.jFonts.length];
		for (int i = 0;  i < attributeSets.length;  ++i) {
			attributeSets[i] = new SimpleAttributeSet(attributeSet0);
			StyleConstants.setFontFamily(attributeSets[i], parent.jFonts[i].getFamily());
		}

		Iterator<String> iter = sLineList.iterator();
		while (iter.hasNext()) {
			String sLine = iter.next();
			if (sLine.indexOf("音韻:") == 0) {
				sLine = "【音韻】" + sLine.substring(3);
			} else if (sLine.indexOf("字解註:") == 0) {
				sLine = "【字解註】" + sLine.substring(4);
				while (sLine.indexOf("<標識>") >= 0) {
					sLine = sLine.replaceFirst("<標識>([^<]*)</標識>", "[$1]");
				}
			} else if (sLine.indexOf("解:") == 0) {
				sLine = "【解】" + sLine.substring(2);
				while (sLine.indexOf("<音>") >= 0) {
					sLine = sLine.replaceFirst("<音>([^<]*)</音>", "($1)");
				}
				while (sLine.indexOf("<標識>") >= 0) {
					sLine = sLine.replaceFirst("<標識>([^<]*)</標識>", "[$1]");
				}
			} else if (sLine.indexOf("解字:") == 0) {
				sLine = "【解字】" + sLine.substring(3);
				while (sLine.indexOf("<標識>") >= 0) {
					sLine = sLine.replaceFirst("<標識>([^<]*)</標識>", "[$1]");
				}
			}
			if (sLine.charAt(sLine.length() - 1) != '\n') {
				sLine += "\n";
			}
			while (!sLine.equals("")) {
				codePoint = sLine.codePointAt(0);
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
				sLine = sLine.substring(sC.length());
			}
		}
		textPane.setEditable(false);
		
		this.setContentPane(new JScrollPane(textPane));
		this.setPreferredSize(new Dimension(480, 320));
		this.pack();
		this.setVisible(true);
	}
}
