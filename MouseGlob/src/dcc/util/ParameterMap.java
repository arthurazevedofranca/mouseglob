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
package dcc.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ParameterMap {

	private final Map<String, String> map;

	public ParameterMap() {
		this.map = new LinkedHashMap<String, String>();
	}

	public void set(String key, Object value) {
		map.put(key, value.toString());
	}

	public String get(String key) {
		return map.get(key);
	}

	public int getInteger(String key) {
		return Integer.valueOf(map.get(key));
	}

	public double getDouble(String key) {
		return Double.valueOf(map.get(key));
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Entry<String, String> entry : map.entrySet()) {
			if (first)
				first = false;
			else
				sb.append('&');
			sb.append(String.format("%s=%s", entry.getKey(), entry.getValue()));
		}
		return sb.toString();
	}

	public static ParameterMap decode(String parameterString) {
		ParameterMap params = new ParameterMap();
		String[] pairs = parameterString.split("\\&");
		for (String pairString : pairs) {
			String[] pair = pairString.split("\\=");
			params.set(pair[0], pair[1]);
		}
		return params;
	}

	public static void main(String[] args) {
		ParameterMap params = new ParameterMap();
		params.set("foo", "abc");
		params.set("bar", 123.456);
		params.set("baz", 42);
		System.out.println(params);
		System.out.println(decode(params.toString()));
	}

}
