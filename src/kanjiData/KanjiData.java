package kanjiData;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jdbm.htree.HTree;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;


public class KanjiData {

	final static String DATABASE_FOLDER = "database";

	RecordManager recordManager;
	
	public KanjiData(String sDatabaseFile) {

		boolean isJar = (System.getProperty("KanjiDictionary.jar") != null);
		try {
			if (isJar) {
				System.setProperty("user.dir", System.getProperty("user.home"));
			}
			String sDatabaseFolder = (new File(DATABASE_FOLDER)).getCanonicalPath();
			if (!(new File(sDatabaseFolder)).exists()) {
				if (!(new File(sDatabaseFolder)).mkdir()) {
					System.err.println("mkdir(" + sDatabaseFolder + ") was failed.");
				}
			}
			if (isJar) {
				boolean copyFlag = true;
				long jarTime = (new URL(System.getProperty("KanjiDictionary.jar"))).openConnection().getLastModified();
				File newFile = new File(sDatabaseFolder + File.separator + sDatabaseFile + ".db");
				if (newFile.exists()) {
					if (jarTime < newFile.lastModified()) {
						copyFlag = false;
					}
				}
				if (copyFlag) {
					InputStream is = KanjiData.class.getClassLoader().getResourceAsStream(
							DATABASE_FOLDER + "/" + sDatabaseFile + ".db");
					ProgressMonitorInputStream is2 = new ProgressMonitorInputStream(
							null,
							"Reading " + DATABASE_FOLDER + "/" + sDatabaseFile + ".db",
							is);
					FileOutputStream fos = new FileOutputStream(
							sDatabaseFolder + File.separator + sDatabaseFile + ".db");
					byte[] buffer = new byte[1024 * 1024];
					int size;
					while ((size = is2.read(buffer)) > 0) {
						fos.write(buffer, 0, size);
					}
					is.close();
					fos.flush();
					fos.close();
					System.err.println(
							(new File(sDatabaseFolder + File.separator + sDatabaseFile + ".db")).getCanonicalPath()
							+ " is created.");
				}
			}
			recordManager = RecordManagerFactory.createRecordManager(
					sDatabaseFolder + File.separator
					+ sDatabaseFile);
		} catch (java.io.IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.toString());
			System.exit(1);
		}
	}

	public static void deleteDatabase() {
		File folder = new File(DATABASE_FOLDER);
		File[] files = folder.listFiles();
		for (int i = 0;  i < files.length;  ++i) {
			if (files[i].isFile()) {
				files[i].delete();
			}
		}
	}

	HTree getMapFromDatabase(String sName)
			throws java.io.IOException {
		long recordId = recordManager.getNamedObject(sName);
		return HTree.load(recordManager, recordId);
	}

	HTree createMap(String sName)
			throws java.io.IOException {
		HTree map = HTree.createInstance(recordManager);
		recordManager.setNamedObject(sName, map.getRecid());
		return map;
	}
	
	void commitMap(String sName)
			throws java.io.IOException {
		recordManager.commit();
	}

	DocumentBuilder documentBuilder = null;

	void createDocumentBuilder() {
		DocumentBuilderFactory factory
				= DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(false);
		factory.setValidating(true);
		factory.setValidating(false);
		factory.setExpandEntityReferences(true);
		factory.setIgnoringElementContentWhitespace(true);
		factory.setIgnoringComments(true);
		factory.setCoalescing(false);
		try {
			documentBuilder = factory.newDocumentBuilder();
		} catch (javax.xml.parsers.ParserConfigurationException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.toString());
			System.exit(1);
		}
	}
	
	public void close() throws java.io.IOException {
		recordManager.close();
	}
}
