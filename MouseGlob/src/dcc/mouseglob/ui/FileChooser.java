/*******************************************************************************
 * Copyright (c) 2016 Daniel Coelho de Castro.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Daniel Coelho de Castro - initial API and implementation
 ******************************************************************************/
package dcc.mouseglob.ui;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

import dcc.mouseglob.FileType;
import dcc.mouseglob.PropertiesManager;

public class FileChooser {

	private static final String LAST_DIRECTORY_KEY = "last.directory";

	private static Component parent;

	private final FileType fileType;
	private final String name;
	private final String key;

	private JFileChooser chooser;

        public static void setParent(Component parent) {
                FileChooser.parent = parent;
        }

        public static Component getParent() {
                return parent;
        }

	public FileChooser(FileType fileType, String name) {
		this.fileType = fileType;
		this.name = name;
		key = LAST_DIRECTORY_KEY + "." + name;

		// Creating a JFileChooser takes time, so do it asynchronously
		new Thread(new Runnable() {
			@Override
			public void run() {
				chooser = new JFileChooser();
				chooser.setFileFilter(FileChooser.this.fileType.getFileFilter());
				chooser.setAcceptAllFileFilterUsed(false);
				String lastDirectory = PropertiesManager.getInstance().get(key);
				if (lastDirectory != null)
					chooser.setCurrentDirectory(new File(lastDirectory));
			}
		}).start();
	}

	public String open() {
		chooser.setDialogTitle("Open " + name + "...");

		if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			String currentDirectory = chooser.getCurrentDirectory().getPath();
			PropertiesManager.getInstance().set(key, currentDirectory);
			return chooser.getSelectedFile().getAbsolutePath();
		}

		else
			return null;
	}

	public String save() {
		chooser.setDialogTitle("Save " + name + "...");

		if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
			String currentDirectory = chooser.getCurrentDirectory().getPath();
			PropertiesManager.getInstance().set(key, currentDirectory);
			String file = chooser.getSelectedFile().getAbsolutePath();
			return fileType.appendExtension(file);
		}

		else
			return null;
	}

	public String save(String suggestedName) {
		if (suggestedName != null) {
			String lastDirectory = PropertiesManager.getInstance().get(key);
			chooser.setSelectedFile(new File(lastDirectory + File.separator
					+ suggestedName));
		}
		return save();
	}
}
