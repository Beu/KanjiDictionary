
import java.awt.*;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.text.*;


public class KangxiFrame extends JInternalFrame {

	private static final long serialVersionUID = 1L;

	private final static int DEFAULT_FONT_SIZE = 24;
	private int fontSize = DEFAULT_FONT_SIZE;

	private KanjiDictionary parent;

	public KangxiFrame(KanjiDictionary parent) {
		super("康熙字典",
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
		HashMap<String, Object> infoMap = (HashMap<String, Object>)
				parent.kangxi.characterMap.get(new Integer(codePoint));
		this.codePoint = codePoint;
	
		if (infoMap == null) {
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
		String sData = (String) infoMap.get("kangxi");

		JTextPane textPane = new JTextPane();
		textPane.setMargin(new Insets(8, 8, 8, 8));
		StyledDocument document = textPane.getStyledDocument();
		SimpleAttributeSet attributeSet0 = new SimpleAttributeSet();
		StyleConstants.setFontSize(attributeSet0, fontSize);
		StyleConstants.setLineSpacing(attributeSet0, (float)(2.0 / 5.0));
		textPane.setParagraphAttributes(attributeSet0, true);
		SimpleAttributeSet[] attributeSets = new SimpleAttributeSet[parent.cFonts.length];
		for (int i = 0;  i < attributeSets.length;  ++i) {
			attributeSets[i] = new SimpleAttributeSet(attributeSet0);
			StyleConstants.setFontFamily(attributeSets[i], parent.cFonts[i].getFamily());
		}

		while (!sData.equals("")) {
			if (sData.charAt(0) == '　') {
				sData = sData.substring(1);
				continue;
			}
			codePoint = sData.codePointAt(0);
			int index = 0;
			for (;  index < parent.cFonts.length;  ++index) {
				if (parent.cFonts[index].canDisplay(codePoint)) {
					break;
				}
			}
			AttributeSet attributeSet = attributeSet0;
			if (index < parent.cFonts.length) {
				attributeSet = attributeSets[index];
			}
			String sC = new String(new int[] { codePoint }, 0, 1);
			try {
				document.insertString(document.getLength(), sC, attributeSet);
			} catch (BadLocationException e) {
			}
			sData = sData.substring(sC.length());
		}
		textPane.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(textPane);
		this.setContentPane(scrollPane);
		this.setPreferredSize(new Dimension(480, 320));
		this.pack();
		this.setVisible(true);
	}
}
