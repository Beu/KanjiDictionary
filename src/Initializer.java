

import kanjiData.KanjiData;
import kanjiData.Jigen;
import kanjiData.Kangxi;
import kanjiData.Unihan;


public class Initializer {

	public static void main(String[] sArgs) {
		new Initializer();
	}
	
	public Initializer() {
		try {
			KanjiData.deleteDatabase();

			Jigen jigen = new Jigen();
			jigen.createMaps();
			jigen.close();
			jigen = null;
			System.out.println("Jigen read.");

			Kangxi kangxi = new Kangxi();
			kangxi.createMaps();
			kangxi.close();
			kangxi = null;
			System.out.println("Kangxi read.");
			
			Unihan unihan = new Unihan();
			unihan.createMaps();
			unihan.close();
			unihan = null;
			System.out.println("Unihan read.");

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Initializer completed.");
		System.exit(0);
	}
}
