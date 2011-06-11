package org.rsbot.locale;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

import org.rsbot.Configuration;
import org.rsbot.util.io.IniParser;

public class Messages {
	private static HashMap<String, String> map;

	static {
		final String defaultLang = "en";
		final String lang = Locale.getDefault().getLanguage();
		try {
			URL src = Configuration.getResourceURL(Configuration.Paths.Resources.MESSAGES + defaultLang + ".txt");
			map = IniParser.deserialise(src.openStream()).get(IniParser.emptySection);
			if (!lang.startsWith(defaultLang)) {
				for (final String avail : new String[] {"de", "hi", "nl", "sv"}) {
					if (lang.startsWith(avail)) {
						src = Configuration.getResourceURL(Configuration.Paths.Resources.MESSAGES + avail + ".txt");
						final HashMap<String, String> mapNative = IniParser.deserialise(src.openStream()).get(IniParser.emptySection);
						for (final Entry<String, String> entry : mapNative.entrySet()) {
							map.put(entry.getKey(), entry.getValue());
						}
						break;
					}
				}
			}
		} catch (final IOException ignored) {
		}
	}

	public static final String LANGUAGE = map.get("LANGUAGE");

	public static final String FILE = map.get("FILE");
	public static final String EDIT = map.get("EDIT");
	public static final String VIEW = map.get("VIEW");
	public static final String TOOLS = map.get("TOOLS");
	public static final String HELP = map.get("HELP");

	public static final String NEWBOT = map.get("NEWBOT");
	public static final String CLOSEBOT = map.get("CLOSEBOT");
	public static final String HIDEBOT = map.get("HIDEBOT");
	public static final String ADDSCRIPT = map.get("ADDSCRIPT");
	public static final String RUNSCRIPT = map.get("RUNSCRIPT");
	public static final String RESUMESCRIPT = map.get("RESUMESCRIPT");
	public static final String STOPSCRIPT = map.get("STOPSCRIPT");
	public static final String PAUSESCRIPT = map.get("PAUSESCRIPT");
	public static final String SAVESCREENSHOT = map.get("SAVESCREENSHOT");
	public static final String EXIT = map.get("EXIT");

	public static final String ACCOUNTS = map.get("ACCOUNTS");
	public static final String FORCEINPUT = map.get("FORCEINPUT");
	public static final String DISABLEANTIRANDOMS = map.get("DISABLEANTIRANDOMS");
	public static final String DISABLEAUTOLOGIN = map.get("DISABLEAUTOLOGIN");
	public static final String DISABLEADS = map.get("DISABLEADS");
	public static final String DISABLECONFIRMATIONS = map.get("DISABLECONFIRMATIONS");
	public static final String BINDTO = map.get("BINDTO");
	public static final String USEPASSWORD = map.get("USEPASSWORD");
	public static final String LESSCPU = map.get("LESSCPU");
	public static final String DISABLECANVAS = map.get("DISABLECANVAS");
	public static final String EXTDVIEWS = map.get("EXTDVIEWS");
	public static final String AUTOSHUTDOWN = map.get("AUTOSHUTDOWN");
	public static final String BETAPATCH = map.get("BETAPATCH");

	public static final String HIDETOOLBAR = map.get("HIDETOOLBAR");
	public static final String HIDELOGPANE = map.get("HIDELOGPANE");
	public static final String ALLDEBUGGING = map.get("ALLDEBUGGING");

	public static final String CLEARCACHE = map.get("CLEARCACHE");
	public static final String OPTIONS = map.get("OPTIONS");

	public static final String SITE = map.get("SITE");
	public static final String PROJECT = map.get("PROJECT");
	public static final String ABOUT = map.get("ABOUT");

	public static final String TABDEFAULTTEXT = map.get("TABDEFAULTTEXT");

	public static final String TOGGLE = "Toggle";
	public static final String TOGGLEFALSE = TOGGLE + "F ";
	public static final String TOGGLETRUE = TOGGLE + "T ";
	public static final String MENUSEPERATOR = "-";
}
