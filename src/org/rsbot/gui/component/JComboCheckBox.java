package org.rsbot.gui.component;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * @author Paris
 */
public class JComboCheckBox extends JComboBox implements ActionListener {
	private static final long serialVersionUID = -3388586151789454096L;

	public JComboCheckBox(final String defaultText) {
		addActionListener(this);
		setRenderer(new ComboCheckRenderer(defaultText));
	}

	public void actionPerformed(ActionEvent e) {
		JComboBox cb = (JComboBox) e.getSource();
		StatefulItem store = (StatefulItem) cb.getSelectedItem();
		ComboCheckRenderer ccr = (ComboCheckRenderer) cb.getRenderer();
		ccr.checkBox.setSelected(store.state = !store.state);
	}

	public void populate(final Iterable<String> list, final boolean state) {
		removeAllItems();
		for (final String item : list) {
			addItem(new StatefulItem(item, state));
		}
	}

	class ComboCheckRenderer implements ListCellRenderer {
		final JLabel none;
		final JCheckBox checkBox;

		public ComboCheckRenderer(final String defaultText) {
			none = new JLabel(defaultText);
			checkBox = new JCheckBox();
		}

		public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
			if (index == -1) {
				return none;
			}
			StatefulItem store = (StatefulItem) value;
			checkBox.setText(store.id);
			checkBox.setSelected(store.state);
			return checkBox;
		}
	}

	class StatefulItem {
		public String id;
		public boolean state;

		public StatefulItem(final String id, final boolean state) {
			this.id = id;
			this.state = state;
		}
	}
}
