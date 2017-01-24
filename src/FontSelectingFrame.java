

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;


public class FontSelectingFrame extends JInternalFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private KanjiDictionary parent;

	private JList cFontList;
	private String[] sCFonts;
	private JList jFontList;
	private String[] sJFonts;
	private JList aFontList;
	private String[] sAFonts;

	public FontSelectingFrame(KanjiDictionary parent) {
		super("Font Selecting",
				true /* resizable */,
				false /* closable */,
				false /* maximizable */,
				true /* iconifiable */);
		this.parent = parent;

		JPanel panel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
//		c.anchor = GridBagConstraints.LINE_START;
		// Chinese
		{
			JPanel cOrderPanel = new JPanel();
			BoxLayout layout2 = new BoxLayout(cOrderPanel, BoxLayout.Y_AXIS);
			cOrderPanel.setLayout(layout2);
			{
				JButton button = new JButton("↑");
				button.setMargin(new Insets(2, 2, 2, 2));
				button.setActionCommand("cUpward");
				button.addActionListener(this);
				cOrderPanel.add(button);
			}
			{
				JButton button = new JButton("↓");
				button.setMargin(new Insets(2, 2, 2, 2));
				button.setActionCommand("cDownward");
				button.addActionListener(this);
				cOrderPanel.add(button);
			}
			c.gridx = 0;
			c.gridy = 0;
			layout.setConstraints(cOrderPanel, c);
			panel.add(cOrderPanel);
		}
		{
			JPanel cPanel = new JPanel();
			cPanel.setBorder(new TitledBorder("Chinese"));
			{
				cFontList = new JList();
				cFontList.setLayoutOrientation(JList.VERTICAL);
				cFontList.setVisibleRowCount(8);
				cPanel.add(new JScrollPane(cFontList));
			}
			c.gridx = 1;
			c.gridy = 0;
			layout.setConstraints(cPanel, c);
			panel.add(cPanel);
		}
		{
			JPanel cMovePanel = new JPanel();
			BoxLayout layout2 = new BoxLayout(cMovePanel, BoxLayout.Y_AXIS);
			cMovePanel.setLayout(layout2);
			{
				JButton button = new JButton("←");
				button.setMargin(new Insets(2, 2, 2, 2));
				button.setActionCommand("cAppend");
				button.addActionListener(this);
				cMovePanel.add(button);
			}
			{
				JButton button = new JButton("×");
				button.setMargin(new Insets(2, 2, 2, 2));
				button.setActionCommand("cDelete");
				button.addActionListener(this);
				cMovePanel.add(button);
			}
			c.gridx = 2;
			c.gridy = 0;
			layout.setConstraints(cMovePanel, c);
			panel.add(cMovePanel);
		}
		// Japanese
		{
			JPanel jOrderPanel = new JPanel();
			BoxLayout layout2 = new BoxLayout(jOrderPanel, BoxLayout.Y_AXIS);
			jOrderPanel.setLayout(layout2);
			{
				JButton button = new JButton("↑");
				button.setMargin(new Insets(2, 2, 2, 2));
				button.setActionCommand("jUpward");
				button.addActionListener(this);
				jOrderPanel.add(button);
			}
			{
				JButton button = new JButton("↓");
				button.setMargin(new Insets(2, 2, 2, 2));
				button.setActionCommand("jDownward");
				button.addActionListener(this);
				jOrderPanel.add(button);
			}
			c.gridx = 0;
			c.gridy = 1;
			layout.setConstraints(jOrderPanel, c);
			panel.add(jOrderPanel);
		}
		{
			JPanel jPanel = new JPanel();
			jPanel.setBorder(new TitledBorder("Japanese"));
			{
				jFontList = new JList();
				jFontList.setLayoutOrientation(JList.VERTICAL);
				jFontList.setVisibleRowCount(8);
				jPanel.add(new JScrollPane(jFontList));
			}
			c.gridx = 1;
			c.gridy = 1;
			layout.setConstraints(jPanel, c);
			panel.add(jPanel);
		}
		{
			JPanel jMovePanel = new JPanel();
			BoxLayout layout2 = new BoxLayout(jMovePanel, BoxLayout.Y_AXIS);
			jMovePanel.setLayout(layout2);
			{
				JButton button = new JButton("←");
				button.setMargin(new Insets(2, 2, 2, 2));
				button.setActionCommand("jAppend");
				button.addActionListener(this);
				jMovePanel.add(button);
			}
			{
				JButton button = new JButton("×");
				button.setMargin(new Insets(2, 2, 2, 2));
				button.setActionCommand("jDelete");
				button.addActionListener(this);
				jMovePanel.add(button);
			}
			c.gridx = 2;
			c.gridy = 1;
			layout.setConstraints(jMovePanel, c);
			panel.add(jMovePanel);
		}
		// available
		{
			JPanel uPanel = new JPanel();
			uPanel.setBorder(new TitledBorder("available"));
			{
				aFontList = new JList();
				aFontList.setLayoutOrientation(JList.VERTICAL);
				aFontList.setVisibleRowCount(20);
				uPanel.add(new JScrollPane(aFontList));
			}
			c.gridx = 3;
			c.gridy = 0;
			c.gridheight = 2;
			layout.setConstraints(uPanel, c);
			panel.add(uPanel);
		}
		{
			JPanel bPanel = new JPanel();
			{
				JButton button = new JButton("update");
				button.setActionCommand("update");
				button.addActionListener(this);
				bPanel.add(button);
			}
			{
				JButton button = new JButton("cancel");
				button.setActionCommand("cancel");
				button.addActionListener(this);
				bPanel.add(button);
			}
			{
				JButton button = new JButton("append font...");
				button.setActionCommand("appendFont");
				button.addActionListener(this);
				bPanel.add(button);
			}
			c.gridx = 0;
			c.gridy = 2;
			c.gridheight = 1;
			c.gridwidth = 3;
			layout.setConstraints(bPanel, c);
			panel.add(bPanel);
		}
cancelSelecting();
		this.setContentPane(panel);
		this.pack();
		this.setVisible(true);
	}
	
	public void cancelSelecting() {
		cFontList.removeAll();
		sCFonts = new String[parent.cFonts.length];
		for (int i = 0;  i < parent.cFonts.length;  ++i) {
			sCFonts[i] = parent.cFonts[i].getName();
		}
		cFontList.setListData(sCFonts);
		
		jFontList.removeAll();
		sJFonts = new String[parent.jFonts.length];
		for (int i = 0;  i < parent.jFonts.length;  ++i) {
			sJFonts[i] = parent.jFonts[i].getName();
		}
		jFontList.setListData(sJFonts);
		
		aFontList.removeAll();
		Font[] aFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		sAFonts = new String[aFonts.length];
		for (int i = 0;  i < aFonts.length;  ++i) {
			sAFonts[i] = aFonts[i].getName();
		}
		aFontList.setListData(sAFonts);
	}

	public void actionPerformed(ActionEvent event) {
		String sCommand = event.getActionCommand();
		if (sCommand == null) {
			return;
		}
		if (sCommand.equals("cancel")) {
			cancelSelecting();
			this.pack();
			this.setVisible(true);
		} else if (sCommand.equals("update")) {
			parent.cFonts = new Font[sCFonts.length];
			for (int i = 0;  i < sCFonts.length;  ++i) {
				parent.cFonts[i] = getFont(sCFonts[i]);
			}
			parent.jFonts = new Font[sJFonts.length];
			for (int i = 0;  i < sJFonts.length;  ++i) {
				parent.jFonts[i] = getFont(sJFonts[i]);
			}
			parent.updateFonts();
		} else if (sCommand.substring(1).equals("Upward")) {
			JList fontList = null;
			String[] sFonts = null;
			switch (sCommand.charAt(0)) {
			case 'c':
				fontList = cFontList;
				sFonts = sCFonts;
				break;
			case 'j':
				fontList = jFontList;
				sFonts = sJFonts;
				break;
			}
			int index = fontList.getSelectedIndex();
			if (index > 0 && index < sFonts.length) {
				String s = sFonts[index];
				sFonts[index] = sFonts[index - 1];
				sFonts[index - 1] = s;
				fontList.setListData(sFonts);
				fontList.setSelectedIndex(index - 1);
			}
		} else if (sCommand.substring(1).equals("Downward")) {
			JList fontList = null;
			String[] sFonts = null;
			switch (sCommand.charAt(0)) {
			case 'c':
				fontList = cFontList;
				sFonts = sCFonts;
				break;
			case 'j':
				fontList = jFontList;
				sFonts = sJFonts;
				break;
			}
			int index = fontList.getSelectedIndex();
			if (index >= 0 && index < sFonts.length - 1) {
				String s = sFonts[index];
				sFonts[index] = sFonts[index + 1];
				sFonts[index + 1] = s;
				fontList.setListData(sFonts);
				fontList.setSelectedIndex(index + 1);
			}
		} else if (sCommand.substring(1).equals("Append")) {
			int index = aFontList.getSelectedIndex();
			if (index >= 0 && index < sAFonts.length) {
				JList fontList = null;
				String[] sFonts = null;
				switch (sCommand.charAt(0)) {
				case 'c':
					fontList = cFontList;
					sFonts = sCFonts;
					break;
				case 'j':
					fontList = jFontList;
					sFonts = sJFonts;
					break;
				}
				for (int i = 0;  i < sFonts.length;  ++i) {
					if (sFonts[i].equals(sAFonts[index])) {
						// duplicated
						return;
					}
				}
				String[] sNewFonts = new String[sFonts.length + 1];
				for (int i = 0;  i < sFonts.length;  ++i) {
					sNewFonts[i] = sFonts[i];
				}
				sNewFonts[sFonts.length] = sAFonts[index];
				sFonts = sNewFonts;
				fontList.setListData(sFonts);
				switch (sCommand.charAt(0)) {
				case 'c':
					sCFonts = sFonts;
					break;
				case 'j':
					sJFonts = sFonts;
					break;
				}
			}
		} else if (sCommand.substring(1).equals("Delete")) {
			JList fontList = null;
			String[] sFonts = null;
			switch (sCommand.charAt(0)) {
			case 'c':
				fontList = cFontList;
				sFonts = sCFonts;
				break;
			case 'j':
				fontList = jFontList;
				sFonts = sJFonts;
				break;
			}
			int index = fontList.getSelectedIndex();
			if (index >= 0 && index < sFonts.length) {
				String[] sNewFonts = new String[sFonts.length - 1];
				for (int i = 0;  i < index;  ++i) {
					sNewFonts[i] = sFonts[i];
				}
				for (int i = index;  i < sNewFonts.length;  ++i) {
					sNewFonts[i] = sFonts[i + 1];
				}
				sFonts = sNewFonts;
				fontList.setListData(sFonts);
				switch (sCommand.charAt(0)) {
				case 'c':
					sCFonts = sFonts;
					break;
				case 'j':
					sJFonts = sFonts;
					break;
				}
			}
		} else if (sCommand.equals("appendFont")) {
			try {
				parent.appendFont();
			} catch (java.io.IOException e) {
				e.printStackTrace();
			}
			cancelSelecting();
		}
	}
	
	Font getFont(String sName) {
		Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		for (int i = 0;  i < fonts.length;  ++i) {
			if (fonts[i].getName().equals(sName)) {
				return fonts[i];
			}
		}
		return null;
	}
}
