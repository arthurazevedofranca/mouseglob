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
package dcc.inject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

final class ReadUtils {

	private ReadUtils() {
	}

	static List<String> readResource(InputStream in) {
		List<String> lines = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					in, "UTF-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
				lines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return lines;
	}

	static List<String> readResource(String resourceName) {
		List<String> lines = new ArrayList<String>();
		URL url = ReadUtils.class.getResource(resourceName);
		if (url != null) {
			System.out.println("Found resource at: " + url);
			try {
				lines = readResource(url.openStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Resource \"" + resourceName + "\" not found");
		}

		return lines;
	}

}
