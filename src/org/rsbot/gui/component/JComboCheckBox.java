package org.rsbot.gui.component;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * @author Paris
 */
public class JComboCheckBox extends JComboBox implements ActionListener {
	class ComboCheckRenderer implements ListCellRenderer {
		final JLabel none;
		final JCheckBox checkBox;

		public ComboCheckRenderer() {
			none = new JLabel();
			checkBox = new JCheckBox();
		}

		@Override
		public Component getListCellRendererComponent(final JList list,
				final Object value, final int index, final boolean isSelected,
				final boolean cellHasFocus) {
			if (index == -1) {
				return none;
			}
			final StatefulItem store = (StatefulItem) value;
			checkBox.setText(store.id);
			checkBox.setSelected(store.state);
			return checkBox;
		}

		public void setText(final String label) {
			none.setText(label);
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

	private static final long serialVersionUID = -3388586151789454096L;

	private ComboCheckRenderer renderer;

	public JComboCheckBox() {
		super.addActionListener(this);
		setRenderer(renderer = new ComboCheckRenderer());
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getModifiers() == 0) {
			return;
		}
		final JComboBox cb = (JComboBox) e.getSource();
		final StatefulItem store = (StatefulItem) cb.getSelectedItem();
		final ComboCheckRenderer ccr = (ComboCheckRenderer) cb.getRenderer();
		ccr.checkBox.setSelected(store.state = !store.state);
	}

	@Override
	public void addActionListener(final ActionListener l) {
		super.removeActionListener(this);
		super.addActionListener(l);
		super.addActionListener(this);
	}

	public String[] getSelectedItems() {
		final ArrayList<String> items = new ArrayList<String>();
		for (int i = 0; i < getItemCount(); i++) {
			final StatefulItem item = (StatefulItem) getItemAt(i);
			if (item.state) {
				items.add(item.id);
			}
		}
		final String[] list = new String[items.size()];
		items.toArray(list);
		return list;
	}

	public void populate(final Iterable<String> list, final boolean state) {
		removeAllItems();
		for (final String item : list) {
			addItem(new StatefulItem(item, state));
		}
	}

	public void setText(final String label) {
		renderer.setText(label);
	}
}
