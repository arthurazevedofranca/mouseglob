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
package dcc.mouseglob;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author Daniel Coelho de Castro
 */
public enum FileType {

	/**
	 * File containing experiment information
	 */
	EXPERIMENT_FILE("MouseGlob Experiment", "mge"),

	/**
	 * File containing the recorded trajectories
	 */
	TRAJECTORIES_FILE("Trajectory File", "csv"),

	/** Report data export (CSV) */
	REPORT_CSV("Report CSV", "csv"),
	/** Report data export (Parquet) */
	REPORT_PARQUET("Report Parquet", "parquet"),
	/** Report vector image (SVG) */
	REPORT_SVG("Report SVG", "svg"),

	/**
	 * File containing maze information
	 */
	MAZE_FILE("Maze", "mgz"),

	/**
	 * File containing exported maze data in CSV format
	 */
	MAZE_CSV_FILE("Exported CSV Data", "csv"),

	/**
	 * Movie to be analyzed
	 */
	MOVIE_FILE("Movie", "avi", "mov", "mp4"),

	/**
	 * Exported report image
	 */
	REPORT_IMAGE("Image", "png", "jpg");

	private final String[] extensions;
	private final FileFilter fileFilter;

	private FileType(String description, String... extensions) {
		this.extensions = extensions;
		StringBuilder sb = new StringBuilder(description);
		sb.append(" (");
		sb.append("*.").append(extensions[0]);
		for (int i = 1; i < extensions.length; i++)
			sb.append(", *.").append(extensions[i]);
		sb.append(")");
		this.fileFilter = new FileNameExtensionFilter(sb.toString(), extensions);
	}

	/**
	 * Validates the extension of the given file name.
	 * 
	 * @param fileName
	 *            - the file name to be validated
	 * @return <code>true</code> if it has a valid extension, <code>false</code>
	 *         otherwise
	 */
	public boolean validateExtension(String fileName) {
		for (String extension : extensions)
			if (fileName.endsWith(extension))
				return true;
		return false;
	}

	/**
	 * Appends the file extension to the file name if it hasn't got one.
	 * 
	 * @param fileName
	 *            - the file name to which append the extension
	 * @return the file name with the appropriate extension
	 */
	public String appendExtension(String fileName) {
		if (!validateExtension(fileName)) {
			return fileName + "." + extensions[0];
		} else {
			return fileName;
		}
	}

	public String replaceExtension(String fileName) {
		return appendExtension(removeExtension(fileName));
	}

	/**
	 * Removes the file extension to recover the original file name.
	 * 
	 * @param fileName
	 * @return the trimmed file name
	 */
	public static String removeExtension(String fileName) {
		int dotIndex = fileName.lastIndexOf('.');
		if (dotIndex != -1)
			return fileName.substring(0, dotIndex);
		return fileName;
	}

	/**
	 * Gets a file name extension filter associated with this type of file.
	 * 
	 * @return the file filter
	 */
	public FileFilter getFileFilter() {
		return fileFilter;
	}

}
