package org.rsbot.script.provider;


/**
 * @author Jacmob
 */
public class ScriptDefinition implements Comparable<ScriptDefinition> {

	public int id;

	public String name;

	public double version;

	public String description;

	public String[] authors;

	public String[] keywords;

	public String website;

	public ScriptSource source;

	public String path;

	public int compareTo(final ScriptDefinition def) {
		return name.compareToIgnoreCase(def.name);
	}

}
