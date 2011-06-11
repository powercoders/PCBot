package org.rsbot.locale;

import java.util.Locale;

public class Messages {
	private static Messages instance;

	protected Messages() {
	}

	public static Messages getInstance() {
		if (instance == null) {
			final String loc = Locale.getDefault().getLanguage();
			if (loc.startsWith("de")) {
				instance = new Messages_de();
			} else if (loc.startsWith("de")) {
				instance = new Messages_de();
			} else if (loc.startsWith("fr")) {
				instance = new Messages_fr();
			} else if (loc.startsWith("nl")) {
				instance = new Messages_nl();
			} else if (loc.startsWith("sv")) {
				instance = new Messages_sv();
                        } else if (loc.startsWith("hi")) {
                                instance = new Messages_hi();
			} else {
				instance = new Messages();
			}
		}
		return instance;
	}

	public final String LANGUAGE = "English";

	public final String FILE = "File";
	public final String EDIT = "Edit";
	public final String VIEW = "View";
	public final String TOOLS = "Tools";
	public final String HELP = "Help";

	public final String NEWBOT = "New Bot";
	public final String CLOSEBOT = "Close Bot";
	public final String HIDEBOT = "Hide";
	public final String ADDSCRIPT = "Add Script";
	public final String RUNSCRIPT = "Run Script";
	public final String RESUMESCRIPT = "Resume Script";
	public final String STOPSCRIPT = "Stop Script";
	public final String PAUSESCRIPT = "Pause Script";
	public final String SAVESCREENSHOT = "Screenshot";
	public final String EXIT = "Exit";

	public final String ACCOUNTS = "Accounts";
	public final String FORCEINPUT = "Force Input";
	public final String DISABLEANTIRANDOMS = "Disable Randoms";
	public final String DISABLEAUTOLOGIN = "Disable Login";
	public final String DISABLEADS = "Disable advertisements";
	public final String DISABLECONFIRMATIONS = "Disable confirmations";
	public final String BINDTO = "Bind to address:";
	public final String USEPASSWORD = "Use password:";
	public final String LESSCPU = "Less CPU";
	public final String DISABLECANVAS = "Disable Canvas";
	public final String EXTDVIEWS = "Extended views";
	public final String AUTOSHUTDOWN = "Shutdown (mins):";
	public final String BETAPATCH = "Use beta client patch";

	public final String HIDETOOLBAR = "Hide Toolbar";
	public final String HIDELOGPANE = "Hide Log Pane";
	public final String ALLDEBUGGING = "All Debugging";

	public final String CLEARCACHE = "Clear Cache";
	public final String OPTIONS = "Options";

	public final String SITE = "Site";
	public final String PROJECT = "Project";
	public final String ABOUT = "About";

	public final String TOGGLE = "Toggle";
	public final String TOGGLEFALSE = TOGGLE + "F ";
	public final String TOGGLETRUE = TOGGLE + "T ";
	public final String MENUSEPERATOR = "-";

	public final String TABDEFAULTTEXT = "Bot";
}
