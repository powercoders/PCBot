package org.rsbot.script.provider;

import org.rsbot.Configuration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Paris
 */
public class ScriptLikes {
	private static final File cache = new File(Configuration.Paths.getSettingsDirectory(), "liked-scripts.txt");
	private static List<String> list = null;

	public static boolean isLiked(final ScriptDefinition def) {
		if (list == null) {
			load();
		}
		return list.contains(def.name);
	}

	public static void flip(final ScriptDefinition def) {
		if (list == null) {
			load();
		}
		if (isLiked(def)) {
			list.remove(def.name);
		} else {
			list.add(def.name);
		}
	}

	public static void load() {
		if (list == null) {
			list = new ArrayList<String>(8);
		} else {
			list.clear();
		}
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(cache);
			br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0 || line.startsWith("#")) {
					continue;
				}
				list.add(line);
			}
		} catch (final IOException ignored) {
			try {
				if (fr != null) {
					fr.close();
				}
				if (br != null) {
					br.close();
				}
			} catch (final IOException ignored1) {
			}
		}
	}

	public static void save() {
		if (list == null || list.size() == 0) {
			cache.delete();
		}
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(cache);
			bw = new BufferedWriter(fw);
			for (final String def : list) {
				bw.append(def);
				bw.newLine();
			}
			bw.close();
			fw.close();
		} catch (final IOException ignored) {
			try {
				if (fw != null) {
					fw.close();
				}
				if (bw != null) {
					bw.close();
				}
			} catch (final IOException ignored1) {
			}
		}
	}
}
