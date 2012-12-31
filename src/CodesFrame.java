
import javax.swing.*;

public class CodesFrame extends JInternalFrame {

	private static final long serialVersionUID = 1L;

	KanjiDictionary parent;
	
	public CodesFrame(KanjiDictionary parent) {
		super("Codes",
				true /* resizable */,
				false /* closable */,
				false /* maximizable */,
				true /* iconifiable */);
		this.parent = parent;
	}

	final static String[] ENCODINGS = {
		"UTF-32BE", "UTF-16BE", "UTF-8",
		/* Chinese */
		"Big5", "Big5-HKSCS", "Cp950",
		"EUC-TW",
		"ISO-2022-CN-CNS",
		"Cp948",
		"GB2312", "GBK", "GB18030", "Cp1381",
		"ISO-2022-CN-GB",
		/* Japanese */
		"EUC-JP",
		"ISO-2022-JP",
		"Shift_JIS", "Windows-31J",
		/* Korean */
		"EUC-KR", "Cp949",
		"ISO-2022-KR",
		"Johab",
	};

	final static String[] COLUMN_NAMES = {
		"encoding name",
		"value"
	};

	public void setCharacter(int codePoint) {
		String sC = new String(new int[] { codePoint }, 0, 1);
		Object[][] data = new Object[ENCODINGS.length][2];
		for (int i = 0;  i < ENCODINGS.length;  ++i) {
			data[i][0] = ENCODINGS[i];
			try {
				byte[] bytes = sC.getBytes(ENCODINGS[i]);
				if (bytes.length == 1 && bytes[0] == 0x3f) {
					data[i][1] = "";
					continue;
				} else if (ENCODINGS[i].equals("ISO-2022-JP")
						&& bytes.length == 8
						&& bytes[3] == 0x21 && bytes[4] == 0x29) {
					data[i][1] = "";
					continue;
				}
				String s = "";
				for (int j = 0;  j < bytes.length;  ++j) {
					s += String.format("%02X", bytes[j] & 0xff);
				}
				data[i][1] = s;
			} catch (java.io.UnsupportedEncodingException e) {
				data[i][1] = "(unsupported)";
			}
		}
		JTable table = new JTable(data, COLUMN_NAMES) {
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setContentPane(new JScrollPane(table));
		this.pack();
		this.setVisible(true);
	}
	
}
