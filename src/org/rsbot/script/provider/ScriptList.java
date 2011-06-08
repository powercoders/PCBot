package org.rsbot.script.provider;

import java.io.File;
import java.io.PrintStream;

import org.rsbot.util.io.IOHelper;

public class ScriptList {
	public static final String DELIMITER = ",";

	public static void main(final String[] args) {
		if (args.length == 0) {
			return;
		}
		final PrintStream out = System.out;
		final FileScriptSource source = new FileScriptSource(new File(args[0]));
		for (final ScriptDefinition item : source.list()) {
			out.print("[");
			final File file = new File(item.path);
			out.print(file.getName());
			out.println("]");
			out.print("id=");
			out.println(Integer.toString(item.id));
			out.print("crc32=");
			out.println(Long.toString(IOHelper.crc32(file)));
			printValue(out, "name", item.name);
			out.print("version=");
			out.println(Double.toString(item.version));
			printValue(out, "description", item.description);
			printValue(out, "authors", item.authors);
			printValue(out, "keywords", item.keywords);
			printValue(out, "website", item.website);
		}
	}

	private static void printValue(final PrintStream out, final String key, final String... texts) {
		out.print(key);
		out.print("=");
		for (int i = 0; i < texts.length; i++) {
			if (i != 0) {
				out.print(DELIMITER);
			}
			out.print(stripNewline(texts[i]));
		}
		out.println();
	}

	private static String stripNewline(String text) {
		text = text.replace("\r\n", " ");
		text = text.replace("\r", " ");
		text = text.replace("\n", " ");
		return text;
	}
}
