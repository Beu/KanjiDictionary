package kanjiData;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import jdbm.helper.FastIterator;
import jdbm.htree.HTree;


public class Unihan extends KanjiData {

	final static String DATABASE_FILE = "unihan";
	final static String MAP_CHARACTER = "character";
	final static String DATA_FOLDER = "data/unihan";
	final static String DATA_FILE = "Unihan.txt";

	public Unihan() {
		super(DATABASE_FILE);
	}

	public HTree characterMap;
	public TreeMap<Integer, int[]>[] radicalsMaps;
	
	@SuppressWarnings("unchecked")
	public void getMaps() throws java.io.IOException {
		characterMap = super.getMapFromDatabase(MAP_CHARACTER);
		radicalsMaps = (TreeMap<Integer, int[]>[])characterMap.get(new Integer(0));
	}
	
	@SuppressWarnings("unchecked")
	public void createMaps() throws java.io.IOException {
		
		characterMap = super.createMap(MAP_CHARACTER);
		
		BufferedReader in = new BufferedReader(
				new InputStreamReader(
				new FileInputStream(DATA_FOLDER + "/" + DATA_FILE), "UTF-8"));
		String sLine;
		while ((sLine = in.readLine()) != null) {
			if (sLine.length() < 1 || sLine.charAt(0) == '#') {
				continue;
			}
			String[] sColumns = sLine.split("\t");
			int codePoint = Integer.parseInt(sColumns[0].substring(2), 16);
			HashMap<String, Object> infoMap = (HashMap<String, Object>)
					characterMap.get(new Integer(codePoint));
			if (infoMap == null) {
				infoMap = new HashMap<String, Object>();
				characterMap.put(new Integer(codePoint), infoMap);
			}
			if (sColumns[1].equals("kCantonese")) {
				String[] ss = sColumns[2].trim().split(" ");
				for (int i = 0;  i < ss.length;  ++i) {
					if (!ss[i].trim().matches("[a-z]+[1-6]")) {
						continue;
					}
				}
				infoMap.put("cantoneses", ss);
			} else if (sColumns[1].equals("kHangul")) {
				String[] ss = sColumns[2].trim().split(" ");
				for (int i = 0;  i < ss.length;  ++i) {
				}
				infoMap.put("hanguls", ss);
			} else if (sColumns[1].equals("kJapaneseOn")) {
				String[] ss = sColumns[2].trim().split(" ");
				for (int i = 0;  i < ss.length;  ++i) {
					if (!ss[i].trim().matches("[A-Z]+")) {
						continue;
					}
				}
				infoMap.put("japaneses", ss);
			} else if (sColumns[1].equals("kKorean")) {
				String[] ss = sColumns[2].split(" ");
				for (int i = 0;  i < ss.length;  ++i) {
					if (!ss[i].trim().matches("[A-Z]+")) {
						continue;
					}
				}
				infoMap.put("koreans", ss);
			} else if (sColumns[1].equals("kMandarin")) {
				String[] ss = sColumns[2].split(" ");
				for (int i = 0;  i < ss.length;  ++i) {
					if (!ss[i].trim().matches("[A-ZÜ]+[1-5]")) {
						continue;
					}
				}
				infoMap.put("mandarins", ss);
			} else if (sColumns[1].equals("kTang")) {
				String[] ss = sColumns[2].split(" ");
				for (int i = 0;  i < ss.length;  ++i) {
				}
				infoMap.put("tangs", ss);
			} else if (sColumns[1].equals("kVietnamese")) {
				String[] ss = sColumns[2].split(" ");
				for (int i = 0;  i < ss.length;  ++i) {
				}
				infoMap.put("vietnameses", ss);
			} else if (sColumns[1].equals("kRSUnicode")) {
				ArrayList<Integer> intList = new ArrayList<Integer>();
				String[] ss = sColumns[2].split(" ");
				for (int i = 0;  i < ss.length;  ++i) {
					if (!ss[i].trim().matches("[0-9]+'?\\.[0-9]+")) {
						continue;
					}
					ss[i] = ss[i].trim().replaceAll("'", "");
					String[] ss2 = ss[i].split("\\.");
					int radical = Integer.parseInt(ss2[0]) - 1;
					intList.add(new Integer(radical));
					int strokes = Integer.parseInt(ss2[1]);
					intList.add(new Integer(strokes));
				}
				infoMap.put("radicalAndStrokes",
						intList.toArray(new Integer[intList.size()]));
			} else if (sColumns[1].equals("kTotalStrokes")) {
				ArrayList<Integer> intList = new ArrayList<Integer>();
				String[] ss = sColumns[2].split(" ");
				for (int i = 0;  i < ss.length;  ++i) {
					if (!ss[i].trim().matches("[0-9]+")) {
						continue;
					}
					ss[i] = ss[i].trim();
					int strokes = Integer.parseInt(ss[i]);
					intList.add(new Integer(strokes));
				}
				infoMap.put("strokess",
						intList.toArray(new Integer[intList.size()]));
			} else if (sColumns[1].equals("kCompatibilityVariant")) {
				ArrayList<Integer> intList = new ArrayList<Integer>();
				String[] ss = sColumns[2].split(" ");
				for (int i = 0;  i < ss.length;  ++i) {
					if (!ss[i].trim().matches("U\\+[0-9A-F]+")) {
						continue;
					}
					ss[i] = ss[i].trim();
					int codePoint2 = Integer.parseInt(ss[i].substring(2), 16);
					intList.add(codePoint2);
				}
				infoMap.put("compatibilityVariants",
						intList.toArray(new Integer[intList.size()]));
			} else if (sColumns[1].equals("kSemanticVariant")) {
				ArrayList<Integer> intList = new ArrayList<Integer>();
				String[] ss = sColumns[2].split(" ");
				for (int i = 0;  i < ss.length;  ++i) {
					if (!ss[i].trim().matches("U\\+[0-9A-F]+(<k[A-Za-z,:]+)?")) {
						continue;
					}
					ss[i] = ss[i].trim().replaceAll("<.*", "");
					int codePoint2 = Integer.parseInt(ss[i].substring(2), 16);
					intList.add(codePoint2);
				}
				infoMap.put("sematicVariants",
						intList.toArray(new Integer[intList.size()]));
			} else if (sColumns[1].equals("kSimplifiedVariant")) {
				ArrayList<Integer> intList = new ArrayList<Integer>();
				String[] ss = sColumns[2].split(" ");
				for (int i = 0;  i < ss.length;  ++i) {
					if (!ss[i].trim().matches("U\\+[0-9A-F]+")) {
						continue;
					}
					ss[i] = ss[i].trim();
					int codePoint2 = Integer.parseInt(ss[i].substring(2), 16);
					intList.add(codePoint2);
				}
				infoMap.put("simplifiedVariants",
						intList.toArray(new Integer[intList.size()]));
			} else if (sColumns[1].equals("kSpecializedSemanticVariant")) {
				ArrayList<Integer> intList = new ArrayList<Integer>();
				String[] ss = sColumns[2].split(" ");
				for (int i = 0;  i < ss.length;  ++i) {
					if (!ss[i].trim().matches("U\\+[0-9A-F]+(<k[A-Za-z]+)?")) {
						continue;
					}
					ss[i] = ss[i].trim().replaceAll("<.*", "");
					int codePoint2 = Integer.parseInt(ss[i].substring(2), 16);
					intList.add(codePoint2);
				}
				infoMap.put("specializedSemanticVariants",
						intList.toArray(new Integer[intList.size()]));
			} else if (sColumns[1].equals("kTraditionalVariant")) {
				ArrayList<Integer> intList = new ArrayList<Integer>();
				String[] ss = sColumns[2].split(" ");
				for (int i = 0;  i < ss.length;  ++i) {
					if (!ss[i].trim().matches("U\\+[0-9A-F]+")) {
						continue;
					}
					ss[i] = ss[i].trim();
					int codePoint2 = Integer.parseInt(ss[i].substring(2), 16);
					intList.add(codePoint2);
				}
				infoMap.put("traditionalVariants",
						intList.toArray(new Integer[intList.size()]));
			} else if (sColumns[1].equals("kZVariant")) {
				ArrayList<Integer> intList = new ArrayList<Integer>();
				String[] ss = sColumns[2].split(" ");
				for (int i = 0;  i < ss.length;  ++i) {
					if (!ss[i].trim().matches("U\\+[0-9A-F]+(:k[A-Za-z]+)?")) {
						continue;
					}
					ss[i] = ss[i].trim().replaceAll(":.*", "");
					int codePoint2 = Integer.parseInt(ss[i].substring(2), 16);
					intList.add(codePoint2);
				}
				infoMap.put("ZVariants",
						intList.toArray(new Integer[intList.size()]));
			}
			// put し直さないとダメみたい。
			characterMap.put(new Integer(codePoint), infoMap);
		}

		TreeMap<Integer, int[]>[] radicalsMaps = new TreeMap[Unicode.NUM_RADICALS];
		TreeMap<Integer, TreeSet<Integer>>[] radicalsMaps2 = new TreeMap[Unicode.NUM_RADICALS];
		for (int i = 0;  i < radicalsMaps2.length;  ++i) {
			radicalsMaps2[i] = new TreeMap<Integer, TreeSet<Integer>>();
		}
		FastIterator iter = characterMap.keys();
		Integer iCodePoint;
		while ((iCodePoint = (Integer)iter.next()) != null) {
			HashMap<String, Object> infoMap = (HashMap<String, Object>)
					characterMap.get(iCodePoint);
			Integer[] iRadicalAndStrokes = (Integer[])
					infoMap.get("radicalAndStrokes");
			if (iRadicalAndStrokes == null) {
				// この情報が無いものが結構存在する。
				continue;
			}
			for (int i = 0;  i < iRadicalAndStrokes.length;  i += 2) {
				Integer iRadical = iRadicalAndStrokes[i];
				Integer iStrokes = iRadicalAndStrokes[i + 1];
				TreeMap<Integer, TreeSet<Integer>> infoMap2
						= radicalsMaps2[iRadical.intValue()];
				TreeSet<Integer> iCodePointSet = infoMap2.get(iStrokes);
				if (iCodePointSet == null) {
					iCodePointSet = new TreeSet<Integer>(new CategoryComparator());
					infoMap2.put(iStrokes, iCodePointSet);
				}
				iCodePointSet.add(iCodePoint);
			}
		}
		for (int i = 0;  i < radicalsMaps2.length;  ++i) {
			radicalsMaps[i] = new TreeMap<Integer, int[]>();
			Iterator<Integer> iter2 = radicalsMaps2[i].keySet().iterator();
			while (iter2.hasNext()) {
				Integer iStrokes = iter2.next();
				TreeSet<Integer> iCodePointSet = radicalsMaps2[i].get(iStrokes);
				Integer[] iCodePoints = iCodePointSet.toArray(new Integer[iCodePointSet.size()]);
				int[] codePoints = new int[iCodePointSet.size()];
				for (int j = 0;  j < codePoints.length;  ++j) {
					codePoints[j] = (int)iCodePoints[j];
				}
				radicalsMaps[i].put(iStrokes, codePoints);
			}
		}
		characterMap.put(new Integer(0), radicalsMaps);

		super.recordManager.commit();
	}
	
	public class CategoryComparator implements Comparator<Integer> {

		public int compare(Integer o1, Integer o2) {
			int value1 = (int)o1;
			int value2 = (int)o2;
			int category1;
			int category2;
			for (category1 = 0;  category1 < Unicode.CATEGORY_INFOS.length;  ++category1) {
				int first = (int)(Integer)Unicode.CATEGORY_INFOS[category1][1];
				int last = (int)(Integer)Unicode.CATEGORY_INFOS[category1][2];
				if (value1 >= first && value1 <= last) {
					break;
				}
			}
			for (category2 = 0;  category2 < Unicode.CATEGORY_INFOS.length;  ++category2) {
				int first = (int)(Integer)Unicode.CATEGORY_INFOS[category2][1];
				int last = (int)(Integer)Unicode.CATEGORY_INFOS[category2][2];
				if (value2 >= first && value2 <= last) {
					break;
				}
			}
			if (category1 < category2) {
				return -1;
			} else if (category1 > category2) {
				return 1;
			}
			return value1 - value2;
		}
	}
}
