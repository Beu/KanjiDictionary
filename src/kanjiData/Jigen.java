package kanjiData;


import java.util.LinkedList;

import jdbm.htree.HTree;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class Jigen extends KanjiData {

	final static String DATABASE_FILE = "jigen";
	final static String MAP_CHARACTER = "character";
	final static String DATA_FOLDER = "data/jigen";
	static String DATA_FILE(int radical) {
		// 001.xml - 214.xml
		String s = "00" + Integer.toString(radical + 1);
		return s.substring(s.length() - 3) + ".xml";
	}

	public Jigen() {
		super(DATABASE_FILE);
	}

	public HTree characterMap = null;

	public void getMaps() throws java.io.IOException {
		characterMap = super.getMapFromDatabase(MAP_CHARACTER);
	}

	public void createMaps()
			throws java.io.InvalidObjectException, java.io.IOException {

		characterMap = super.createMap(MAP_CHARACTER);

		createDocumentBuilder();
		for (int i = 0;  i < Unicode.NUM_RADICALS;  ++i) {
			Document document = null;
			try {
				document = documentBuilder.parse(
						DATA_FOLDER + "/" + DATA_FILE(i));
			} catch (org.xml.sax.SAXException e) {
				e.printStackTrace();
				System.exit(1);
			}
			String sRadical = null;
			Element radicalElement
					= document.getDocumentElement();
			for (Node child = radicalElement.getFirstChild();
					child != null;
					child = child.getNextSibling()) {
				if (child.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				String sTagName = ((Element)child).getTagName();
				if (sTagName.equals("部首字")) {
					sRadical = "" + child.getTextContent().charAt(0);
				} else if (sTagName.equals("筆畫")) {
					int strokes = -1;
					for (Node child2 = child.getFirstChild();
							child2 != null;
							child2 = child2.getNextSibling()) {
						if (child2.getNodeType() != Node.ELEMENT_NODE) {
							continue;
						}
						String sTagName2 = ((Element)child2).getTagName();
						if (sTagName2.equals("畫數")) {
							strokes = Integer.parseInt(child2.getTextContent());
						} else if (sTagName2.equals("漢字")) {
							String sCharacter = null;
							int codePoint = 0;
							
							LinkedList<String> infoList = new LinkedList<String>();

							for (Node child3 = child2.getFirstChild();
									child3 != null;
									child3 = child3.getNextSibling()) {
								if (child3.getNodeType() != Node.ELEMENT_NODE) {
									continue;
								}
								String sTagName3 = ((Element)child3).getTagName();
								if (sTagName3.equals("見出字")) {
									sCharacter = child3.getTextContent();
									if (!sCharacter.equals("？")) {
										codePoint = sCharacter.codePointAt(0);
									}
								} else if (sTagName3.equals("字解")) {
									for (Node child4 = child3.getFirstChild();
											child4 != null;
											child4 = child4.getNextSibling()) {
										if (child4.getNodeType() != Node.ELEMENT_NODE) {
											continue;
										}
										String sTagName4 = ((Element)child4).getTagName();
										if (sTagName4.equals("音韻")) {
											String sValue = "";
											for (Node child5 = child4.getFirstChild();
													child5 != null;
													child5 = child5.getNextSibling()) {
												if (child5.getNodeType() != Node.ELEMENT_NODE) {
													continue;
												}
												String sTagName5 = ((Element)child5).getTagName();
												if (sTagName5.equals("號")) {
													sValue += child5.getTextContent();
												} else if (sTagName5.equals("音")) {
													sValue += "「" + child5.getTextContent() + "」";
												} else if (sTagName5.equals("韻")) {
													sValue += "[" + child5.getTextContent() + "]";
												}
											}
											infoList.add("音韻:" + sValue);
										} else if (sTagName4.equals("字解註")) {
											// <標識>
											String sValue = "";
											for (Node child5 = child4.getFirstChild();  child5 != null;  child5 = child5.getNextSibling()) {
												switch (child5.getNodeType()) {
												case Node.ELEMENT_NODE:
													String sTagName5 = ((Element)child5).getTagName();
													if (sTagName5.equals("標識")) {
														sValue += "<標識>" + child5.getTextContent() + "</標識>";
													}
													break;
												case Node.TEXT_NODE:
													sValue += child5.getTextContent().trim();
													break;
												}
											}
											infoList.add("字解註:" + sValue);
										} else if (sTagName4.equals("解")) {
											String sValue = "";
											for (Node child5 = child4.getFirstChild();
													child5 != null;
													child5 = child5.getNextSibling()) {
												if (child5.getNodeType() != Node.ELEMENT_NODE) {
													continue;
												}
												String sTagName5 = ((Element)child5).getTagName();
												if (sTagName5.equals("號")) {
													sValue += child5.getTextContent();
												} else if (sTagName5.equals("義")) {
													String sValue2 = "";
													// <音>
													// <標識>
													// <返点 type="[レ一二三上中下]|一レ|上レ">
													for (Node child6 = child5.getFirstChild();  child6 != null;  child6 = child6.getNextSibling()) {
														switch (child6.getNodeType()) {
														case Node.ELEMENT_NODE:
															String sTagName6 = ((Element)child6).getTagName();
															if (sTagName6.equals("音")) {
																sValue2 += "<音>" + child6.getTextContent() + "</音>";
															} else if (sTagName6.equals("標識")) {
																sValue2 += "<標識>" + child6.getTextContent() + "</標識>";
															} else if (sTagName6.equals("返点")) {
																String sType = ((Element)child6).getAttribute("type");
																for (int j = 0;  j < sType.length();  ++j) {
																	switch (sType.charAt(j)) {
																	case 'レ':  sValue2 += "\u3191";  break;
																	case '一':  sValue2 += "\u3192";  break;
																	case '二':  sValue2 += "\u3193";  break;
																	case '三':  sValue2 += "\u3194";  break;
																	case '上':  sValue2 += "\u3196";  break;
																	case '中':  sValue2 += "\u3197";  break;
																	case '下':  sValue2 += "\u3198";  break;
																	}
																}
															}
															break;
														case Node.TEXT_NODE:
															sValue2 += child6.getTextContent().trim();
															break;
														}
													}
													sValue += sValue2 + "\n";
												}
											}
											infoList.add("解:" + sValue);
										} else if (sTagName4.equals("解字")) {
											// <標識>
											String sValue = "";
											for (Node child5 = child4.getFirstChild();  child5 != null;  child5 = child5.getNextSibling()) {
												switch (child5.getNodeType()) {
												case Node.ELEMENT_NODE:
//													String sTagName5 = ((Element)child5).getTagName();
													if (sTagName.equals("標識")) {
														sValue += "<標識>" + child5.getTextContent() + "</標識>";
													}
													break;
												case Node.TEXT_NODE:
													sValue += child5.getTextContent().trim();
													break;
												}
											}
											infoList.add("解字:" + sValue);
										}
									}
								}
							}
							if (codePoint == 0) {
								System.err.println(document.getDocumentURI() + ", radical:" + sRadical + ", strokes:" + Integer.toString(strokes) + " codePoint==0");
							} else {
								if (characterMap.get(new Integer(codePoint)) != null) {
									System.err.println(document.getDocumentURI() + ", radical:" + sRadical + ", strokes:" + Integer.toString(strokes) + " " + String.format("%c", codePoint));
								}
								characterMap.put(new Integer(codePoint), infoList);
							}
						}
					}
				}
			}
		}
		super.recordManager.commit();
	}
}
