package kanjiData;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import jdbm.htree.HTree;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class Kangxi extends KanjiData {

	final static String DATABASE_FILE = "kangxi";
	final static String MAP_CATEGORY = "category";
	final static String MAP_RADICAL = "radical";
	final static String MAP_CHARACTER = "character";
	final static String MAP_CHARACTER_ORDER = "characterOrder";
	final static String DATA_FOLDER = "data/kangxi";
	final static String XML_CATEGORIES = "Tbl_Catalog.xml";
	final static String XML_RADICALS = "Tbl_BS.xml";
	final static String XML_INFOS = "Tbl_KX.xml";
	final static String XML_READINGS = "Tbl_PY.xml";
	final static String TEXT_INFOS = "FDetailCmps.txt";

	public Kangxi() {
		super(DATABASE_FILE);
	}

	public HTree categoryMap;
	public HTree radicalMap;
	public HTree characterMap;
	public TreeMap<Integer, ArrayList<Integer>>[][] radicalsMaps;

	@SuppressWarnings("unchecked")
	public void getMaps() throws java.io.IOException {
		categoryMap = super.getMapFromDatabase(MAP_CATEGORY);
		radicalMap = super.getMapFromDatabase(MAP_RADICAL);
		characterMap = super.getMapFromDatabase(MAP_CHARACTER);
		radicalsMaps = (TreeMap<Integer, ArrayList<Integer>>[][])characterMap.get(new Integer(0));
	}

	@SuppressWarnings("unchecked")
	public void createMaps() throws java.io.IOException {
		
		categoryMap = super.createMap(MAP_CATEGORY);
		radicalMap = super.createMap(MAP_RADICAL);
		characterMap = super.createMap(MAP_CHARACTER);
		
		createDocumentBuilder();
		Document document = null;
		try {
			document = documentBuilder.parse(
					DATA_FOLDER + "/" + XML_CATEGORIES);
		} catch (org.xml.sax.SAXException e) {
			e.printStackTrace();
			System.exit(1);
		}
		for (Node child = document.getDocumentElement().getFirstChild();
				child != null;
				child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			// <Tbl_Catalog>
			int id = 0;
			String sName = null;
			int[] radicals = null;
			for (Node child2 = child.getFirstChild();
					child2 != null;
					child2 = child2.getNextSibling()) {
				if (child2.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				String sTagName = ((Element)child2).getTagName();
				if (sTagName.equals("CatalogID")) {
					id = Integer.parseInt(((Element)child2).getTextContent()) - 1;
				} else if (sTagName.equals("CatalogVal")) {
					sName = ((Element)child2).getTextContent();
				} else if (sTagName.equals("BS")) {
					// 含まれる部首
					String s = ((Element)child2).getTextContent();
					radicals = new int[s.length() / 3];
					for (int i = 0;  i < radicals.length;  ++i) {
						radicals[i] = Integer.parseInt(s.substring(i * 3, i * 3 + 3)) - 1;
					}
				}
			}
			HashMap<String, Object> infoMap = new HashMap<String, Object>();
			infoMap.put("name", sName);
			infoMap.put("radicals", radicals);
			categoryMap.put(new Integer(id), infoMap);
		}
		document = null;
		try {
			document = documentBuilder.parse(
					DATA_FOLDER + "/" + XML_RADICALS);
		} catch (org.xml.sax.SAXException e) {
			e.printStackTrace();
			System.exit(1);
		}
		for (Node child = document.getDocumentElement().getFirstChild();
				child != null;
				child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			// <Tbl_BS>
			int id = 0;
			String sCharacter = "";
			int strokes = 0;
			for (Node child2 = child.getFirstChild();
					child2 != null;
					child2 = child2.getNextSibling()) {
				if (child2.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				String sTagName = ((Element)child2).getTagName();
				if (sTagName.equals("PartID")) {
					id = Integer.parseInt(child2.getTextContent()) - 1;
				} else if (sTagName.equals("WordPart")) {
					sCharacter = child2.getTextContent();
				} else if (sTagName.equals("PartCount")) {
					strokes = Integer.parseInt(child2.getTextContent())	;
				} else if (sTagName.equals("BWCount")) {
					// 部首外画数群
				} else if (sTagName.equals("BWNotBB")) {
					// 普通部首外画数群
				} else if (sTagName.equals("BWBK")) {
					// 備考部首外画数群
				} else if (sTagName.equals("BWBY")) {
					// 補遺部首外画数群
				}
			}
			HashMap<String, Object> infoMap = new HashMap<String, Object>();
			infoMap.put("character", sCharacter);
			infoMap.put("strokes", new Integer(strokes));
			radicalMap.put(new Integer(id), infoMap);
		}
		document = null;
		try {
			document = documentBuilder.parse(
					DATA_FOLDER + "/" + XML_INFOS);
		} catch (org.xml.sax.SAXException e) {
			e.printStackTrace();
			System.exit(1);
		}
		int[] codePoints = new int[47043];
		for (Node child = document.getDocumentElement().getFirstChild();
				child != null;
				child = child.getNextSibling()) {
			if (child.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			// <Tbl_KX>
			int id = 0;
			int categoryId = 0;
//			String sCharacter = null;
			int codePoint = 0;
			int radicalId = 0;
			HashMap<String, Object> infoMap = new HashMap<String, Object>();
			for (Node child2 = child.getFirstChild();
					child2 != null;
					child2 = child2.getNextSibling()) {
				if (child2.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				String sTagName = ((Element)child2).getTagName();
				if (sTagName.equals("WordID")) {
					id = Integer.parseInt(child2.getTextContent()) - 1;
//					infoMap.put("id", new Integer(id));
				} else if (sTagName.equals("CatalogID")) {
					categoryId = Integer.parseInt(child2.getTextContent()) - 1;
					infoMap.put("category", new Integer(categoryId));
				} else if (sTagName.equals("WordName")) {
//					sCharacter = child2.getTextContent();
				} else if (sTagName.equals("UCode")) {
					String s = child2.getTextContent();
					switch (s.length()) {
					case 4:
						codePoint = Integer.parseInt(s, 16);
						break;
					case 8:
						int code0 = Integer.parseInt(s.substring(0, 4), 16);
						int code1 = Integer.parseInt(s.substring(4, 8), 16);
						codePoint = (new String(new int[] { code0, code1 }, 0, 2)).codePointAt(0);
						break;
					}
//					codePoints[index] = codePoint;
				} else if (sTagName.equals("BBFlg")) {
					// categoryType
					String s = child2.getTextContent();
					// W ...
					// B ... 補遺
					// K ... 備考
					infoMap.put("categoryType", s);
				} else if (sTagName.equals("Word1")) {
				} else if (sTagName.equals("Word2")) {
				} else if (sTagName.equals("BW1")) {
				} else if (sTagName.equals("BW2")) {
				} else if (sTagName.equals("BWCount")) {
					// 部首外画数
					infoMap.put("strokes", new Integer(child2.getTextContent()));
				} else if (sTagName.equals("PartID")) {
					radicalId = Integer.parseInt(child2.getTextContent()) - 1;
					infoMap.put("radical", new Integer(radicalId));
				} else if (sTagName.equals("DetailCmp")) {
					while (child2.getFirstChild() != null) {
						child2.removeChild(child2.getFirstChild());
					}
				} else if (sTagName.equals("FDetailCmp")) {
					while (child2.getFirstChild() != null) {
						child2.removeChild(child2.getFirstChild());
					}
				}
			}
			characterMap.put(new Integer(codePoint), infoMap);
			codePoints[id] = codePoint;
		}
		radicalsMaps = new TreeMap[3][Unicode.NUM_RADICALS];
		for (int i = 0;  i < 3;  ++i) {
			for (int j = 0;  j < Unicode.NUM_RADICALS;  ++j) {
				radicalsMaps[i][j] = new TreeMap<Integer, ArrayList<Integer>>();
			}
		}
		BufferedReader in = new BufferedReader(
				new InputStreamReader(
				new FileInputStream(DATA_FOLDER + "/" + TEXT_INFOS), "UTF-8"));
		for (int i = 0;  i < 47043;  ++i) {
			int codePoint = codePoints[i];
			HashMap<String, Object> infoMap = (HashMap<String, Object>)
					characterMap.get(new Integer(codePoint));
			String sLine = in.readLine();
			infoMap.put("kangxi", sLine);
			characterMap.put(new Integer(codePoint), infoMap);
			
			int radical = (int)(Integer)infoMap.get("radical");
			String sCategoryType = (String)infoMap.get("categoryType");
			int categoryType = 0;
			if (sCategoryType.equals("B")) {
				categoryType = 1;
			} else if (sCategoryType.equals("K")) {
				categoryType = 2;
			}
			int strokes = (int)(Integer)infoMap.get("strokes");
			
			TreeMap<Integer, ArrayList<Integer>> iCodePointListMap
					= radicalsMaps[categoryType][radical];
			ArrayList<Integer> iCodePointList = iCodePointListMap.get(new Integer(strokes));
			if (iCodePointList == null) {
				iCodePointList = new ArrayList<Integer>();
				iCodePointListMap.put(new Integer(strokes), iCodePointList);
			}
			iCodePointList.add(new Integer(codePoint));
		}
		characterMap.put(new Integer(0), radicalsMaps);
		super.recordManager.commit();
	}
	
}
