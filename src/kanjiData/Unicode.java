package kanjiData;


public class Unicode {

	public static Object[][] CATEGORY_INFOS = new Object[][] {
		new Object[] {
			"CJK Unified Ideographs",
			0x4E00,
			0x9FBF
		},
		new Object[] {
			"CJK Unified Ideographs Extension A",
			0x3400,
			0x4DBF
		},
		new Object[] {
			"CJK Unified Ideographs Extension B",
			0x20000,
			0x2A6DF
		},
		new Object[] {
			"CJK Compatibility Ideographs",
			0xf900,
			0xfaff
		},
		new Object[] {
			"CJK Compatibility Ideographs Supplement",
			0x2f800,
			0x2fa1f
		}
	};

	public static String RADICALS
		= "一丨丶丿乙亅二亠人儿入八冂冖冫几"
		+ "凵刀力勹匕匚匸十卜卩厂厶又口囗土"
		+ "士夂夊夕大女子宀寸小尢尸屮山巛工"
		+ "己巾干幺广廴廾弋弓彐彡彳心戈戶手"
		+ "支攴文斗斤方无日曰月木欠止歹殳毋"
		+ "比毛氏气水火爪父爻爿片牙牛犬玄玉"
		+ "瓜瓦甘生用田疋疒癶白皮皿目矛矢石"
		+ "示禸禾穴立竹米糸缶网羊羽老而耒耳"
		+ "聿肉臣自至臼舌舛舟艮色艸虍虫血行"
		+ "衣襾見角言谷豆豕豸貝赤走足身車辛"
		+ "辰辵邑酉釆里金長門阜隶隹雨靑非面"
		+ "革韋韭音頁風飛食首香馬骨高髟鬥鬯"
		+ "鬲鬼魚鳥鹵鹿麥麻黃黍黑黹黽鼎鼓鼠"
		+ "鼻齊齒龍龜龠";

	public static int NUM_RADICALS = RADICALS.length();
	
	public static int[] RADICAL_STROKES = {
		1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
		2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3,
		3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
		3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4,
		4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
		4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5,
		5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
		5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
		6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
		6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
		7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 8, 9,
		9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 10, 10, 10, 10, 10, 10,
		10, 10, 11, 11, 11, 11, 11, 11, 12, 12, 12, 12, 13, 13, 13, 13,
		14, 14, 15, 16, 16, 17
	};
}
