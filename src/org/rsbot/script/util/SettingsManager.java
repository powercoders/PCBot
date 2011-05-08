package org.rsbot.script.util;

import java.util.*;
import javax.swing.*;
import java.io.*;

import org.rsbot.util.GlobalConfiguration;

/**
* @author NoEffex, icnhzabot
*
* Manages Reading/Writing GUI settings.
*/

public class SettingsManager {

        private String name;
        private LinkedList<Pair> pairs = new LinkedList<Pair>();

        public SettingsManager(MethodContext ctx, String scriptname) {
			name = GlobalConfiguration.Paths.getSettingsDirectory() + File.separator + scriptname + ".dat"; //Extension doesn't have to be .dat
        }
		
		/**
		* Add all of the components in your GUI that you want to save after you initialize your GUI.
		*
		* @param key The text to appear before the value in the saved file.
		* @param component The JComponent that you want to save.
		*/		
        public void add(String key, JComponent component) {
            pairs.add(new Pair(key, component));
        }
		
		/**
		*
		* Loads a saved settings file. Call on this once you have initialized your GUI and adding it's components. Nothing will happen if there is no settings file.
		*
		*/
        public void load() {
            try {
                File file = new File(name);
                if (!file.exists()) {
                    return;
                }
                FileReader rd = new FileReader(file);
                Properties prop = new Properties();
                prop.load(rd);
                for (Pair pair : pairs) {
                    String value = prop.getProperty(pair.key);
                    if (value == null) {
                        continue;
                    }
                    if (pair.component instanceof JComboBox) {
                        ((JComboBox) pair.component).setSelectedIndex(Integer.parseInt(value));
                    } else if (pair.component instanceof JCheckBox) {
                        ((JCheckBox) pair.component).setSelected(Boolean.parseBoolean(value));
                    } else if (pair.component instanceof JTextField) {
                        ((JTextField) pair.component).setText(value);
                    } else if (pair.component instanceof JTextArea) {
                        ((JTextArea) pair.component).setText(value);
                    } else if (pair.component instanceof JRadioButton) {
                        ((JRadioButton) pair.component).setSelected(Boolean.parseBoolean(value));
                    }
                }
                rd.close();
            } catch (Exception e) {
            }
        }
		
		/**
		*
		* Saves a settings file. Call on this once your GUI has closed.
		*
		*/
        public void save() {
            try {
                File file = new File(name);
                FileWriter wr = new FileWriter(file);
                Properties prop = new Properties();
                for (Pair pair : pairs) {
                    String value = "";
                    if (pair.component instanceof JComboBox) {
                        value = Integer.toString(((JComboBox) pair.component).getSelectedIndex());
                    } else if (pair.component instanceof JCheckBox) {
                        value = Boolean.toString(((JCheckBox) pair.component).isSelected());
                    } else if (pair.component instanceof JTextField) {
                        value = ((JTextField) pair.component).getText();
                    } else if (pair.component instanceof JTextArea) {
                        value = ((JTextArea) pair.component).getText();
                    } else if (pair.component instanceof JRadioButton) {
                        value = Boolean.toString(((JRadioButton) pair.component).isSelected());
                    }
                    prop.setProperty(pair.key, value);
                }
                prop.store(wr, "SettingsManager by NoEffex and icnhzabot.");
                wr.close();
            } catch (Exception e) {
            }
        }

        class Pair {

            String key;
            JComponent component;

            public Pair(String key, JComponent component) {
                this.key = key;
                this.component = component;
            }
        }
    }