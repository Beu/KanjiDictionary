
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.border.EtchedBorder;


public class UnihanFrame extends JInternalFrame
		implements ActionListener {

	private static final long serialVersionUID = 1L;

	KanjiDictionary parent;
	
	public UnihanFrame(KanjiDictionary parent) {
		super("Unihan",
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
		this.codePoint = codePoint;

		HashMap<String, Object> infoMap = (HashMap<String, Object>)
				parent.unihan.characterMap.get(new Integer(codePoint));
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

		JPanel panel = new JPanel(new BorderLayout());
		{
			JLabel label = new JLabel("U+"
					+ Integer.toString(codePoint, 16).toUpperCase());
			label.setBorder(new EtchedBorder());
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setFont(label.getFont().deriveFont((float)label.getFont().getSize() * 2));
			panel.add(label, BorderLayout.PAGE_START);
		}
		{
			String[] sLanguageKeys = {
				"mandarins", "cantoneses", "tangs",
				"japaneses",
				"koreans", "hanguls",
				"vietnameses",
			};
			String[] sLanguages = {
				"官話", "廣東", "古唐",
				"日本",
				"韓國", "한글",
				"越南",
			};

			JPanel panel2 = new JPanel();
			BoxLayout layout = new BoxLayout(panel2, BoxLayout.Y_AXIS);
			panel2.setLayout(layout);
			panel2.setBorder(new EtchedBorder());
			for (int i = 0;  i < sLanguageKeys.length;  ++i) {
				String[] sValues = (String[])infoMap.get(sLanguageKeys[i]);
				if (sValues == null) {
					continue;
				}
				sValues = sValues.clone();
				String s = sLanguages[i] + " : ";
				for (int j = 0;  j < sValues.length;  ++j) {
					sValues[j] = sValues[j].toLowerCase();
					if (sLanguageKeys[i].equals("japaneses")) {
						sValues[j] += " (" + toKana(sValues[j]) + ")";
					}
				}
				s += sValues[0];
				for (int j = 1;  j < sValues.length;  ++j) {
					s += " , " + sValues[j];
				}
				JLabel label = new JLabel(s);
				label.setFont(label.getFont().deriveFont((float)(label.getFont().getSize() * 1.5)));
				panel2.add(label);
			}
			
			panel.add(panel2, BorderLayout.CENTER);
		}
		{
			String[] sVariantKeys = {
				"compatibilityVariants",
				"traditionalVariants",
				"simplifiedVariants",
				"semanticVariants",
				"specializedSemanticVariants",
				"ZVariants",
			};
			String[] sVariantNames = {
				"compatibility variants",
				"traditional variants",
				"simplified variants",
				"semantic variants",
				"specialized semantic variants",
				"Z variants",
			};

			JPanel panel2 = new JPanel();
			BoxLayout layout = new BoxLayout(panel2, BoxLayout.Y_AXIS);
			panel2.setLayout(layout);
			for (int i = 0;  i < sVariantKeys.length;  ++i) {
				Integer[] iCodePoints = (Integer[])infoMap.get(sVariantKeys[i]);
				if (iCodePoints == null) {
					continue;
				}
				JPanel panel3 = new JPanel();
				panel3.add(new JLabel(sVariantNames[i]));
				for (int j = 0;  j < iCodePoints.length;  ++j) {
					JButton button = new JButton(
							new String(new int[] { iCodePoints[j].intValue() }, 0, 1));
					Font font = button.getFont();
					for (int k = 0;  k < parent.cFonts.length;  ++k) {
						if (parent.cFonts[k].canDisplay(iCodePoints[j].intValue())) {
							font = parent.cFonts[k];
							break;
						}
					}
					button.setFont(font.deriveFont((float)24));
					button.setToolTipText(String.format("U+%X", iCodePoints[j].intValue()));
					button.setActionCommand(String.format("U+%X", iCodePoints[j].intValue()));
					button.addActionListener(this);
					panel3.add(button);
				}
				panel2.add(panel3);
			}
			panel.add(panel2, BorderLayout.PAGE_END);
		}
		this.setContentPane(panel);
		this.pack();
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent event) {
		String sCommand = event.getActionCommand();
		if (sCommand != null
				&& sCommand.matches("U\\+[0-9A-F]+")) {
			int codePoint = Integer.parseInt(sCommand.substring(2), 16);
			parent.selectCharacter(codePoint);
		}
	}
	
	static String[][] KANA_TABLE = {
		{"a", "ア"}, {"i", "イ"}, {"u", "ウ"}, {"e", "エ"}, {"o", "オ"},
		{"ka", "カ"}, {"ki", "キ"}, {"ku", "ク"}, {"ke", "ケ"}, {"ko", "コ"},
		{"kya", "キャ"}, {"kyu", "キュ"}, {"kyo", "キョ"},
		{"ga", "ガ"}, {"gi", "ギ"}, {"gu", "グ"}, {"ge", "ゲ"}, {"go", "ゴ"},
		{"gya", "ギャ"}, {"gyu", "ギュ"}, {"gyo", "ギョ"},
		{"sa", "サ"}, {"shi", "シ"}, {"su", "ス"}, {"se", "セ"}, {"so", "ソ"},
		{"sha", "シャ"}, {"shu", "シュ"}, {"sho", "ショ"},
		{"za", "ザ"}, {"ji", "ジ"}, {"zu", "ズ"}, {"ze", "ゼ"}, {"zo", "ゾ"},
		{"ja", "ジャ"}, {"ju", "ジュ"}, {"jo", "ジョ"},
		{"ta", "タ"}, {"chi", "チ"}, {"tsu", "ツ"}, {"te", "テ"}, {"to", "ト"},
		{"cha", "チャ"}, {"chu", "チュ"}, {"cho", "チョ"},
		{"da", "ダ"}, {"di", "ヂ"}, {"dzu", "ヅ"}, {"de", "デ"}, {"do", "ド"},
		{"na", "ナ"}, {"ni", "ニ"}, {"nu", "ヌ"}, {"ne", "ネ"}, {"no", "ノ"},
		{"nya", "ニャ"}, {"nyu", "ニュ"}, {"nyo", "ニョ"},
		{"ha", "ハ"}, {"hi", "ヒ"}, {"fu", "フ"}, {"he", "ヘ"}, {"ho", "ホ"},
		{"hya", "ヒャ"}, {"hyu", "ヒュ"}, {"hyo", "ヒョ"},
		{"ba", "バ"}, {"bi", "ビ"}, {"bu", "ブ"}, {"be", "ベ"}, {"bo", "ボ"},
		{"bya", "ビャ"}, {"byu", "ビュ"}, {"byo", "ビョ"},
		{"pa", "パ"}, {"pi", "ピ"}, {"pu", "プ"}, {"pe", "ペ"}, {"po", "ポ"},
		{"pya", "ピャ"}, {"pyu", "ピュ"}, {"pyo", "ピョ"},
		{"ma", "マ"}, {"mi", "ミ"}, {"mu", "ム"}, {"me", "メ"}, {"mo", "モ"},
		{"mya", "ミャ"}, {"myu", "ミュ"}, {"myo", "ミョ"},
		{"ya", "ヤ"}, {"yu", "ユ"}, {"yo", "ヨ"},
		{"ra", "ラ"}, {"ri", "リ"}, {"ru", "ル"}, {"re", "レ"}, {"ro", "ロ"},
		{"rya", "リャ"}, {"ryu", "リュ"}, {"ryo", "リョ"},
		{"wa", "ワ"}, {"wi", "ヰ"}, {"we", "ヱ"}, {"wo", "ヲ"},
		{"n", "ン"},
	};
	String toKana(String s) {
		String sResult = "";
		Loop: while (!s.equals("")) {
			for (int i = 0;  i < KANA_TABLE.length;  ++i) {
				if (s.indexOf(KANA_TABLE[i][0]) == 0) {
					sResult += KANA_TABLE[i][1];
					s = s.substring(KANA_TABLE[i][0].length());
					continue Loop;
				}
			}
			sResult += s.charAt(0);
			s = s.substring(1);
		}
		return sResult;
	}
}
