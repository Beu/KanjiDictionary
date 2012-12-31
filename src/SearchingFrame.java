
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import jdbm.helper.FastIterator;
import jdbm.htree.HTree;

import kanjiData.Unicode;


public class SearchingFrame extends JInternalFrame
		implements TreeSelectionListener, ActionListener {

	private static final long serialVersionUID = 1L;

	KanjiDictionary parent;

	int[][] unicodeRadicalToCharacters;
	int[][] kangxiRadicalToCharacters;
	
	public SearchingFrame(KanjiDictionary parent)
			throws java.io.IOException {
		super("Searching",
				true /* resizable */,
				false /* closable */,
				false /* maximizable */,
				true /* iconifiable */);
		this.parent = parent;
		JTabbedPane pane = new JTabbedPane();
		pane.addTab("Unicode", createUnicodePane());
		pane.addTab("Kangxi", createKangxiPane());
		pane.addTab("Input", createInputPane());
		pane.addTab("Hand Writing", createHandWritingPane());
		this.setContentPane(pane);
		this.pack();
		this.setVisible(true);
	}
	
	public JComponent createUnicodePane() {
		DefaultMutableTreeNode rootNode
				= new DefaultMutableTreeNode("Unicode");
		{
			DefaultMutableTreeNode categoriesNode
				= new DefaultMutableTreeNode("categories");
			for (int i = 0;  i < Unicode.CATEGORY_INFOS.length;  ++i) {
				String sCategory = (String)Unicode.CATEGORY_INFOS[i][0];
				int first = (int)(Integer)Unicode.CATEGORY_INFOS[i][1];
				int last = (int)(Integer)Unicode.CATEGORY_INFOS[i][2];
				DefaultMutableTreeNode categoryNode
						= new DefaultMutableTreeNode(sCategory);
				for (int codePoint000 = (first & 0xff000);
						codePoint000 <= (last & 0xff000);
						codePoint000 += 0x1000) {
					int segmentFirst = codePoint000;
					if (segmentFirst < first) {
						segmentFirst = first;
					}
					int segmentLast = codePoint000 + 0xfff;
					if (segmentLast > last) {
						segmentLast = last;
					}
					DefaultMutableTreeNode node
							= new DefaultMutableTreeNode(
							"U+"
							+ Integer.toString(segmentFirst, 16).toUpperCase()
							+ " - U+"
							+ Integer.toString(segmentLast, 16).toUpperCase());
					categoryNode.add(node);
				}
				categoriesNode.add(categoryNode);
			}
			rootNode.add(categoriesNode);
		}
		{
			TreeMap<Integer, TreeSet<Integer>> strokesMap
					= new TreeMap<Integer, TreeSet<Integer>>();
			for (int i = 0;  i < Unicode.RADICAL_STROKES.length;  ++i) {
				int strokes = Unicode.RADICAL_STROKES[i];
				TreeSet<Integer> radicalSet = strokesMap.get(new Integer(strokes));
				if (radicalSet == null) {
					radicalSet = new TreeSet<Integer>();
					strokesMap.put(new Integer(strokes), radicalSet);
				}
				radicalSet.add(new Integer(i));
			}
			DefaultMutableTreeNode radicalsNode
					= new DefaultMutableTreeNode("radicals");
			Iterator<Integer> iter = strokesMap.keySet().iterator();
			while (iter.hasNext()) {
				int strokes = (int)iter.next();
				TreeSet<Integer> radicalSet = strokesMap.get(new Integer(strokes));
				String sRadicals = "";
				Iterator<Integer> iter2 = radicalSet.iterator();
				while (iter2.hasNext()) {
					int radical = (int)iter2.next();
					sRadicals += Unicode.RADICALS.charAt(radical);
				}
				DefaultMutableTreeNode strokesNode
						= new DefaultMutableTreeNode(
						Integer.toString(strokes) + " : " + sRadicals);
				for (int i = 0;  i < sRadicals.length();  ++i) {
					DefaultMutableTreeNode radicalNode
							= new DefaultMutableTreeNode(
							"" + sRadicals.charAt(i));
					strokesNode.add(radicalNode);
				}
				radicalsNode.add(strokesNode);
			}
			rootNode.add(radicalsNode);
		}
		JTree tree = new JTree(rootNode);
		tree.addTreeSelectionListener(this);
//		tree.setRootVisible(false);
		return new JScrollPane(tree);
	}

	@SuppressWarnings("unchecked")
	public JComponent createKangxiPane() throws java.io.IOException {

		ArrayList<String> sCategoryList = new ArrayList<String>();
		ArrayList<int[]> radicalsList = new ArrayList<int[]>();
		
		HTree categoryMap = parent.kangxi.categoryMap;
		FastIterator iter = categoryMap.keys();
		Integer id;
		while ((id = (Integer)iter.next()) != null) {
			HashMap<String, Object> infoMap
					= (HashMap<String, Object>)categoryMap.get(id);
			String sName = (String)infoMap.get("name");
			sCategoryList.add(sName);
			int[] radicals = (int[])infoMap.get("radicals");
			radicalsList.add(radicals);
		}
		DefaultMutableTreeNode rootNode
				= new DefaultMutableTreeNode("Kangxi");
		{
			DefaultMutableTreeNode node
					= new DefaultMutableTreeNode("字典");
			for (int i = 0;  i < 36;  ++i) {
				int[] radicals = radicalsList.get(i);
				String sRadicals = "";
				for (int j = 0;  j < radicals.length;  ++j) {
					sRadicals += Unicode.RADICALS.charAt(radicals[j]);
				}
				DefaultMutableTreeNode categoryNode
						= new DefaultMutableTreeNode(
						sCategoryList.get(i)
						+ " : " + sRadicals);
				for (int j = 0;  j < radicals.length;  ++j) {
					DefaultMutableTreeNode radicalNode
							= new DefaultMutableTreeNode(
							"" + Unicode.RADICALS.charAt(radicals[j]));
					categoryNode.add(radicalNode);
				}
				node.add(categoryNode);
			}
			rootNode.add(node);
		}
		{
			DefaultMutableTreeNode node
					= new DefaultMutableTreeNode("補遺");
			for (int i = 0;  i < 12;  ++i) {
				int[] radicals = radicalsList.get(36 + i);
				String sRadicals = "";
				for (int j = 0;  j < radicals.length;  ++j) {
					sRadicals += Unicode.RADICALS.charAt(radicals[j]);
				}
				DefaultMutableTreeNode categoryNode
						= new DefaultMutableTreeNode(
						sCategoryList.get(36 + i)
						+ " : " + sRadicals);
				for (int j = 0;  j < radicals.length;  ++j) {
					DefaultMutableTreeNode radicalNode
							= new DefaultMutableTreeNode(
							"" + Unicode.RADICALS.charAt(radicals[j]));
					categoryNode.add(radicalNode);
				}
				node.add(categoryNode);
			}
			rootNode.add(node);
		}
		{
			DefaultMutableTreeNode node
					= new DefaultMutableTreeNode("備考");
			for (int i = 0;  i < 12;  ++i) {
				int[] radicals = radicalsList.get(36 + i);
				String sRadicals = "";
				for (int j = 0;  j < radicals.length;  ++j) {
					sRadicals += Unicode.RADICALS.charAt(radicals[j]);
				}
				DefaultMutableTreeNode categoryNode
						= new DefaultMutableTreeNode(
						sCategoryList.get(36 + i)
						+ " : " + sRadicals);
				for (int j = 0;  j < radicals.length;  ++j) {
					DefaultMutableTreeNode radicalNode
							= new DefaultMutableTreeNode(
							"" + Unicode.RADICALS.charAt(radicals[j]));
					categoryNode.add(radicalNode);
				}
				node.add(categoryNode);
			}
			rootNode.add(node);
		}
		JTree tree = new JTree(rootNode);
		tree.addTreeSelectionListener(this);
		return new JScrollPane(tree);
	}

	JTextField charactersText;
	
	public JComponent createInputPane() {
		JPanel panel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.LINE_START;
		{
			JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel2.setBorder(new EtchedBorder());
			{
				JLabel label = new JLabel("characters : ");
				panel2.add(label);
				charactersText = new JTextField(16);
				Font font = charactersText.getFont();
				charactersText.setFont(font.deriveFont((float)(font.getSize() * 2)));
				panel2.add(charactersText);
				JButton button = new JButton("search");
				button.setActionCommand("searchWithCharacters");
				button.addActionListener(this);
				panel2.add(button);
			}
			layout.setConstraints(panel2, c);
			panel.add(panel2);
		}
		{
			JPanel panel2 = new JPanel();
			{
				JLabel label = new JLabel(
						"ex.: 検索例");
				panel2.add(label);
			}
			++c.gridy;
			layout.setConstraints(panel2, c);
			panel.add(panel2);
		}
		{
			JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel2.setBorder(new EtchedBorder());
			{
				JLabel label = new JLabel("code : ");
				panel2.add(label);
				JTextField text = new JTextField(16);
				Font font = text.getFont();
				text.setFont(font.deriveFont((float)(font.getSize() * 2)));
				panel2.add(text);
				JButton button = new JButton("search");
				button.setActionCommand("searchWithCode");
				button.addActionListener(this);
				panel2.add(button);
			}
			++c.gridy;
			layout.setConstraints(panel2, c);
			panel.add(panel2);
		}
		{
			JPanel panel2 = new JPanel();
			{
				JLabel label = new JLabel(
						"ex.: U+4E00, 3021 (hexadecimal value)");
				panel2.add(label);
			}
			++c.gridy;
			layout.setConstraints(panel2, c);
			panel.add(panel2);
		}
		{
			JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel2.setBorder(new EtchedBorder());
			{
				JLabel label = new JLabel("reading : ");
				panel2.add(label);
				JTextField text = new JTextField(16);
				Font font = text.getFont();
				text.setFont(font.deriveFont((float)(font.getSize() * 2)));
				panel2.add(text);
				JButton button = new JButton("search");
				button.setActionCommand("searchWithReading");
				button.addActionListener(this);
				panel2.add(button);
			}
			++c.gridy;
			layout.setConstraints(panel2, c);
			panel.add(panel2);
		}
		{
			JPanel panel2 = new JPanel();
			{
				JLabel label = new JLabel(
						"ex.: オン, xiong, 한");
				panel2.add(label);
			}
			++c.gridy;
			layout.setConstraints(panel2, c);
			panel.add(panel2);
		}
		{
			JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel2.setBorder(new EtchedBorder());
			{
				JLabel label = new JLabel("parts : ");
				panel2.add(label);
				JTextField text = new JTextField(16);
				Font font = text.getFont();
				text.setFont(font.deriveFont((float)(font.getSize() * 2)));
				panel2.add(text);
				JButton button = new JButton("search");
				button.setActionCommand("searchWithParts");
				button.addActionListener(this);
				panel2.add(button);
			}
			++c.gridy;
			layout.setConstraints(panel2, c);
			panel.add(panel2);
		}
		{
			JPanel panel2 = new JPanel();
			{
				JLabel label = new JLabel(
						"ex.: 虫工");
				panel2.add(label);
			}
			++c.gridy;
			layout.setConstraints(panel2, c);
			panel.add(panel2);
		}
		JPanel panel2 = new JPanel();
		panel2.add(panel);
		return panel2;
	}

	HandWritingPanel handWritingPanel;

	public JComponent createHandWritingPane() {
		JPanel panel = new JPanel(new BorderLayout());
		{
			handWritingPanel = new HandWritingPanel(parent);
			JPanel panel2 = new JPanel();
			panel2.add(handWritingPanel);
			panel.add(panel2, BorderLayout.CENTER);
		}
		{
			JPanel panel2 = new JPanel();
			{
				JButton button = new JButton("clear");
				button.setActionCommand("clearHandWritingPanel");
				button.addActionListener(this);
				panel2.add(button);
			}
			{
				JButton button = new JButton("cancel");
				button.setActionCommand("cancelHandWritingPanel");
				button.addActionListener(this);
				panel2.add(button);
			}
			panel.add(panel2, BorderLayout.PAGE_END);
		}
		return panel;
	}

	public void valueChanged(TreeSelectionEvent event) {
		TreePath path = event.getPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
		if (!node.isLeaf()) {
			return;
		}
		
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		String sRootNodeName = (String)((DefaultMutableTreeNode)node.getRoot()).getUserObject();
		if (sRootNodeName.equals("Unicode")) {
			String s = (String)node.getUserObject();
			if (s.indexOf("U+") == 0) {
				// 範囲指定
				String[] ss = s.split("-");
				int first = Integer.parseInt(ss[0].trim().substring(2), 16);
				int last = Integer.parseInt(ss[1].trim().substring(2), 16);
				int[] codePoints = new int[last - first + 1];
				for (int codePoint = first;  codePoint <= last;  ++codePoint) {
					codePoints[codePoint - first] = codePoint;
				}
				parent.setCharacters(codePoints);
			} else {
				// 部首指定
				ArrayList<Integer> iCodePointList = new ArrayList<Integer>();
				int radical = Unicode.RADICALS.indexOf(s);
				TreeMap<Integer, int[]> map = parent.unihan.radicalsMaps[radical];
				Iterator<Integer> iter = map.keySet().iterator();
				while (iter.hasNext()) {
					Integer iStrokes = iter.next();
					int[] codePoints2 = map.get(iStrokes);
					for (int i = 0;  i < codePoints2.length;  ++i) {
						iCodePointList.add(new Integer(codePoints2[i]));
					}
				}
				int[] codePoints = new int[iCodePointList.size()];
				for (int i = 0;  i < codePoints.length;  ++i) {
					codePoints[i] = iCodePointList.get(i);
				}
				parent.setCharacters(codePoints);
			}
		} else if (sRootNodeName.equals("Kangxi")) {
			String s = (String)node.getUserObject();
			// 部首指定
			int radical = Unicode.RADICALS.indexOf(s);
			String sGroup = (String)((DefaultMutableTreeNode)node.getParent().getParent()).getUserObject();
			int categoryType = 0;
			if (sGroup.equals("補遺")) {
				categoryType = 1;
			} else if (sGroup.equals("備考")) {
				categoryType = 2;
			}
			ArrayList<Integer> iCodePointList = new ArrayList<Integer>();
			TreeMap<Integer, ArrayList<Integer>> iCodePointListMap
					= parent.kangxi.radicalsMaps[categoryType][radical];
			Iterator<Integer> iter = iCodePointListMap.keySet().iterator();
			while (iter.hasNext()) {
				Integer iStrokes = iter.next();
				iCodePointList.addAll(iCodePointListMap.get(iStrokes));
			}
			int[] codePoints = new int[iCodePointList.size()];
			for (int i = 0;  i < codePoints.length;  ++i) {
				codePoints[i] = iCodePointList.get(i).intValue();
			}
			parent.setCharacters(codePoints);
		}
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	public void actionPerformed(ActionEvent event) {
		String sCommand = event.getActionCommand();
		if ("searchWithCharacters".equals(sCommand)) {
			String s = charactersText.getText();
			ArrayList<Integer> codePointList = new ArrayList<Integer>();
			for (int i = 0;  i < s.length();  ++i) {
				int codePoint = s.codePointAt(i);
				for (int j = 0;  j < Unicode.CATEGORY_INFOS.length;  ++j) {
					int first = (int)(Integer)Unicode.CATEGORY_INFOS[j][1];
					int last = (int)(Integer)Unicode.CATEGORY_INFOS[j][2];
					if (codePoint >= first && codePoint <= last) {
						codePointList.add(new Integer(codePoint));
						break;
					}
				}
			}
			if (codePointList.size() > 0) {
				int[] codePoints = new int[codePointList.size()];
				s = "";
				for (int i = 0;  i < codePoints.length;  ++i) {
					codePoints[i] = (int)codePointList.get(i);
					s += new String(codePoints, i, 1);
				}
				charactersText.setText(s);
				parent.setCharacters(codePoints);
			}
		} else if ("searchWithCode".equals(sCommand)) {
parent.putUnimplementedError();
		} else if ("searchWithReading".equals(sCommand)) {
parent.putUnimplementedError();
		} else if ("searchWithParts".equals(sCommand)) {
parent.putUnimplementedError();
		} else if ("clearHandWritingPanel".equals(sCommand)) {
			handWritingPanel.clearLines();
		} else if ("cancelHandWritingPanel".equals(sCommand)) {
			handWritingPanel.cancelLine();
		}
	}
}
