/* $Id: KanjiDictionary.java 13 2007-07-01 12:59:21Z beu $ */

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import kanjiData.Jigen;
import kanjiData.Kangxi;
import kanjiData.Unihan;


public class KanjiDictionary implements ActionListener {

	final static String APPLICATION_NAME = "字典";
	final static String APPLICATION_VERSION = "0.018";
	final static String FONTS_FOLDER = "data/fonts";
	
	final static String[] DEFAULT_C_FONTS = {
		"MingLiU",
		"MingLiU-ExtB",
		"MingLiU_HKSCS",
		"MingLiU_HKSCS-ExtB",
		"NSimSun",
		"SimSun",
		"Simsun (Founder Extended)",
		"SimSun-ExtB",
		"ZYSongKX-16_24_32_52",
		"DFKai-SB",
	};
	final static String[] DEFAULT_J_FONTS = {
		"ＭＳ Ｐ明朝",
		"MS PMincho",
	};
	final static String[] DEFAULT_MISC_FONTS = {
		"Arial Unicode MS",
	};

	public static void main(String[] sArgs) {
		try {
			if (false) {
				try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
				} catch (Exception e) {
					try {
						UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
			new KanjiDictionary(sArgs);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.toString());
			System.exit(1);
		}
	}

	public Jigen jigen;
	public Kangxi kangxi;
	public Unihan unihan;
	
	public Font[] cFonts;
	public Font[] jFonts;
	public Font[] mFonts;
	
	JFrame frame;
	SelectingFrame selectingFrame;
	CharacterFrame characterFrame;
	CodesFrame codesFrame;
	JigenFrame jigenFrame;
	KangxiFrame kangxiFrame;
	UnihanFrame unihanFrame;

	public KanjiDictionary(String[] sArgs)
			throws java.io.IOException {

		if (System.getProperty("KanjiDictionary.jar") != null
				&& System.getProperty("KanjiDictionary.jar").equals("true")) {
			String s = KanjiDictionary.class.getClassLoader().getResource("KanjiDictionary.class").getPath();
			System.err.println(s);
			s = s.substring(0, s.indexOf("!/KanjiDictionary.class"));
			System.err.println(s);
			System.setProperty("KanjiDictionary.jar", s);
		}

		appendInitialFonts();
		initializeFonts();
		
		jigen = new Jigen();
		jigen.getMaps();
		kangxi = new Kangxi();
		kangxi.getMaps();
		unihan = new Unihan();
		unihan.getMaps();
		
		frame = new JFrame(APPLICATION_NAME);
		frame.setPreferredSize(new Dimension(800, 600));
		{
			JMenuBar menuBar = createMenuBar();
			frame.setJMenuBar(menuBar);
			JDesktopPane desktopPane = createDesktopPane();
			desktopPane.setBackground(Color.DARK_GRAY);
			{
				FontSelectingFrame fontSelectingFrame = new FontSelectingFrame(this);
				desktopPane.add(fontSelectingFrame);
				try {
					fontSelectingFrame.setIcon(true);
				} catch (java.beans.PropertyVetoException e) {
					e.printStackTrace();
				}
			}
			{
				SearchingFrame searchingFrame = new SearchingFrame(this);
				desktopPane.add(searchingFrame);
			}
			{
				selectingFrame = new SelectingFrame(this);
				desktopPane.add(selectingFrame);
			}
			{
				characterFrame = new CharacterFrame(this);
				desktopPane.add(characterFrame);
			}
			{
				codesFrame = new CodesFrame(this);
				desktopPane.add(codesFrame);
			}
			{
				jigenFrame = new JigenFrame(this);
				desktopPane.add(jigenFrame);
			}
			{
				kangxiFrame = new KangxiFrame(this);
				desktopPane.add(kangxiFrame);
			}
			{
				unihanFrame = new UnihanFrame(this);
				desktopPane.add(unihanFrame);
			}
			frame.setContentPane(desktopPane);
		}
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		{
			JMenu menu = new JMenu("Help");
			menu.setMnemonic(KeyEvent.VK_H);
			if (System.getProperty("KanjiDictionary.jar") == null) {
				JMenuItem item = new JMenuItem("ReadMe...", KeyEvent.VK_R);
				item.setActionCommand("readme");
				item.addActionListener(this);
				menu.add(item);
			}
			{
				JMenuItem item = new JMenuItem("About...", KeyEvent.VK_A);
				item.setActionCommand("about");
				item.addActionListener(this);
				menu.add(item);
			}
			menuBar.add(menu);
		}
		return menuBar;
	}

	JDesktopPane createDesktopPane() {
		JDesktopPane desktopPane = new JDesktopPane();
		return desktopPane;
	}
	
	public void actionPerformed(ActionEvent event) {
		String sCommand = event.getActionCommand();
		if ("about".equals(sCommand)) {

			JOptionPane.showMessageDialog(null,
					APPLICATION_NAME
					+ "  version " + APPLICATION_VERSION
					+ "  by Beu",
					"About",
					JOptionPane.INFORMATION_MESSAGE);

		} else if ("readme".equals(sCommand)) {

			try {
				JEditorPane editorPane = new JEditorPane();
				editorPane.setPreferredSize(new Dimension(640, 400));
				editorPane.setPage("file:data/readme.html");
				JOptionPane.showMessageDialog(null, new JScrollPane(editorPane));
			} catch (java.io.IOException e) {
				e.printStackTrace();
			}
		}
	}

	void appendInitialFonts() throws java.io.IOException {
		File[] files = (new File(FONTS_FOLDER)).listFiles();
		if (files == null) {
			return;
		}
		for (int i = 0;  i < files.length;  ++i) {
			if (!files[i].isFile()) {
				continue;
			}
			Font font = null;
			try {
				font = Font.createFont(Font.TRUETYPE_FONT, files[i]);
			} catch (FontFormatException e) {
			}
			if (font == null) {
				try {
					font = Font.createFont(Font.TYPE1_FONT, files[i]);
				} catch (FontFormatException e2) {
					continue;
				}
			}
			if (!GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font)) {
				// error
			}
		}
	}

	File directory = null;

	public void appendFont() throws java.io.IOException {
		JFileChooser chooser = new JFileChooser();
		FileFilter filter = new FileFilter() {
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return true;
				}
				String sName = file.getName();
				int index = sName.lastIndexOf(".");
				if (index >= 0 && index < sName.length() - 1) {
					String extension = sName.substring(index + 1).toLowerCase();
					if (extension.matches("ttf|ttc|otf|fon")) {
						return true;
					}
				}
				return false;
			}
			public String getDescription() {
				return "Font Files";
			}
		};
		chooser.setFileFilter(filter);
//		chooser.setMultiSelectionEnabled(true);
		if (directory != null) {
			chooser.setCurrentDirectory(directory);
		}
		switch (chooser.showOpenDialog(null)) {
		case JFileChooser.CANCEL_OPTION:
			return;
		case JFileChooser.ERROR_OPTION:
			(new Exception("JFileChooser.ERROR_OPTION")).printStackTrace();
			return;
		case JFileChooser.APPROVE_OPTION:
			break;
		}
		directory = chooser.getCurrentDirectory();
		File file = chooser.getSelectedFile();
		Font font = null;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, file);
		} catch (FontFormatException e) {
			try {
				font = Font.createFont(Font.TYPE1_FONT, file);
			} catch (FontFormatException e2) {
				return;
			}
		}
		if (GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font)) {
			JOptionPane.showMessageDialog(null,
					"\"" + font.getName() + "\" was appended.",
					"Font Appender",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	void initializeFonts() {
		Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		
		ArrayList<Font> cFontList = new ArrayList<Font>();
		for (int i = 0;  i < DEFAULT_C_FONTS.length;  ++i) {
			for (int j = 0;  j < fonts.length;  ++j) {
				if (fonts[j].getName().equals(DEFAULT_C_FONTS[i])) {
					cFontList.add(fonts[j]);
					break;
				}
			}
		}
		for (int i = 0;  i < DEFAULT_J_FONTS.length;  ++i) {
			for (int j = 0;  j < fonts.length;  ++j) {
				if (fonts[j].getName().equals(DEFAULT_J_FONTS[i])) {
					cFontList.add(fonts[j]);
					break;
				}
			}
		}
		for (int i = 0;  i < DEFAULT_MISC_FONTS.length;  ++i) {
			for (int j = 0;  j < fonts.length;  ++j) {
				if (fonts[j].getName().equals(DEFAULT_MISC_FONTS[i])) {
					cFontList.add(fonts[j]);
					break;
				}
			}
		}
		cFonts = cFontList.toArray(new Font[cFontList.size()]);
		
		ArrayList<Font> jFontList = new ArrayList<Font>();
		for (int i = 0;  i < DEFAULT_J_FONTS.length;  ++i) {
			for (int j = 0;  j < fonts.length;  ++j) {
				if (fonts[j].getName().equals(DEFAULT_J_FONTS[i])) {
					jFontList.add(fonts[j]);
					break;
				}
			}
		}
		for (int i = 0;  i < DEFAULT_C_FONTS.length;  ++i) {
			for (int j = 0;  j < fonts.length;  ++j) {
				if (fonts[j].getName().equals(DEFAULT_C_FONTS[i])) {
					jFontList.add(fonts[j]);
					break;
				}
			}
		}
		for (int i = 0;  i < DEFAULT_MISC_FONTS.length;  ++i) {
			for (int j = 0;  j < fonts.length;  ++j) {
				if (fonts[j].getName().equals(DEFAULT_MISC_FONTS[i])) {
					jFontList.add(fonts[j]);
					break;
				}
			}
		}
		jFonts = jFontList.toArray(new Font[jFontList.size()]);
	}
	
	public void setCharacters(int[] codePoints) {
		selectingFrame.setCharacters(codePoints);
		try {
			selectingFrame.setSelected(true);
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
		}
	}
	
	public void selectCharacter(int codePoint) {
		
		codesFrame.setCharacter(codePoint);

		characterFrame.setCharacter(codePoint);
		try {
			characterFrame.setSelected(true);
		} catch (java.beans.PropertyVetoException e) {
			e.printStackTrace();
		}


		try {
			jigenFrame.setCharacter(codePoint);
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}

		try {
			kangxiFrame.setCharacter(codePoint);
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}

		try {
			unihanFrame.setCharacter(codePoint);
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}
	
	public void updateFonts() {
		characterFrame.updateFonts();
		selectingFrame.updateFonts();
		try {
			unihanFrame.updateFonts();
			jigenFrame.updateFonts();
			kangxiFrame.updateFonts();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}
	
	public void putUnimplementedError() {
		JOptionPane.showMessageDialog(null,
				"Sorry, Unimplemented!",
				"Error",
				JOptionPane.ERROR_MESSAGE);
	}
}
