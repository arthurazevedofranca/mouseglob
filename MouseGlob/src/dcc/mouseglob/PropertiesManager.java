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

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesManager {

	private static final Logger log = LoggerFactory.getLogger(PropertiesManager.class);
	private static final String HEADER = "MouseGlob Properties File";
	private static PropertiesManager instance;

	public static PropertiesManager getInstance() {
		if (instance == null)
			instance = new PropertiesManager();
		return instance;
	}

	private final Properties properties;
	private final String profile;
	private final Path configDir;
	private final Path configFile;
	private final Path legacyFile;

	@SuppressWarnings("serial")
	private PropertiesManager() {
		properties = new Properties() {
			@Override
			public synchronized Enumeration<Object> keys() {
				return Collections.enumeration(new TreeSet<Object>(super.keySet()));
			}
		};
		this.profile = resolveProfile();
		this.configDir = Paths.get(System.getProperty("user.home"), ".mouseglob");
		this.configFile = configDir.resolve("config-" + profile + ".properties");
		this.legacyFile = Paths.get("config.properties");
	}

	private static String resolveProfile() {
		String p = System.getProperty("mouseglob.profile");
		if (p == null || p.isEmpty()) p = System.getenv("MOUSEGLOB_PROFILE");
		if (p == null || p.isEmpty()) p = "dev";
		return p.toLowerCase();
	}

	public String getProfile() { return profile; }
	public Path getConfigFile() { return configFile; }

	void load() {
		try {
			if (!Files.exists(configFile) && Files.exists(legacyFile)) {
				log.info("Loading legacy properties from {}", legacyFile.toAbsolutePath());
				try (Reader r = Files.newBufferedReader(legacyFile)) { properties.load(r); }
				return;
			}
			Files.createDirectories(configDir);
			if (Files.exists(configFile)) {
				try (Reader r = Files.newBufferedReader(configFile)) {
					properties.load(r);
				}
				log.info("Loaded properties (profile={}) from {}", profile, configFile.toAbsolutePath());
			} else {
				log.info("No properties file found. Creating defaults for profile={} at {}", profile, configFile.toAbsolutePath());
				setDefaults();
				store();
			}
		} catch (IOException e) {
			log.error("Failed to load properties: {}", e.toString());
		}
	}

	void store() {
		try {
			Files.createDirectories(configDir);
			try (Writer w = Files.newBufferedWriter(configFile)) {
				properties.store(w, HEADER + " (profile=" + profile + ")");
			}
			log.info("Saved properties to {}", configFile.toAbsolutePath());
		} catch (IOException e) {
			log.error("Failed to store properties: {}", e.toString());
		}
	}

	private void setDefaults() {
		// Example defaults can be added here as needed
		// e.g., set("lastDirectory", Paths.get(System.getProperty("user.home")).toString());
	}

	public void set(String key, String value) {
		properties.setProperty(key, value);
	}

	public void setInteger(String key, int value) {
		set(key, Integer.toString(value));
	}

	public String get(String key) {
		return properties.getProperty(key);
	}

	public String get(String key, String defaultValue) {
		String value = properties.getProperty(key);
		if (value == null) {
			set(key, defaultValue);
			return defaultValue;
		}
		return value;
	}

	public int getInteger(String key) {
		try {
			return Integer.parseInt(get(key));
		} catch (Exception e) {
			log.warn("Invalid integer for key '{}': {}", key, get(key));
			throw e;
		}
	}

	public int getInteger(String key, int defaultValue) {
		String value = get(key);
		if (value == null) {
			set(key, String.valueOf(defaultValue));
			return defaultValue;
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			log.warn("Invalid integer for key '{}': {}. Using default {}", key, value, defaultValue);
			setInteger(key, defaultValue);
			return defaultValue;
		}
	}

	public Path getPath(String key, Path defaultValue) {
		String v = get(key);
		if (v == null || v.isEmpty()) {
			set(key, defaultValue.toString());
			return defaultValue;
		}
		return Paths.get(v);
	}
}
