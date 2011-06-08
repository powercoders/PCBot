package org.rsbot.gui;

import org.rsbot.Configuration;
import org.rsbot.bot.Bot;
import org.rsbot.gui.component.JComboCheckBox;
import org.rsbot.script.Script;
import org.rsbot.script.internal.ScriptHandler;
import org.rsbot.script.internal.event.ScriptListener;
import org.rsbot.script.provider.*;
import org.rsbot.service.Preferences;
import org.rsbot.service.ServiceException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Paris
 */
public class ScriptSelector extends JDialog implements ScriptListener {
	private static final long serialVersionUID = 5475451138208522511L;
	private static final Logger log = Logger.getLogger(ScriptSelector.class.getName());
	private static final String[] COLUMN_NAMES = new String[]{"", "Name", "Description"};

	private static final ScriptSource SRC_SOURCES;
	private static final ScriptSource SRC_PRE_COMPILED;
	private static final ScriptSource SRC_NETWORK;
	private final BotGUI frame;
	private final Bot bot;
	private JTable table;
	private JTextField search;
	private final static Color searchAltColor = Color.GRAY;
	private JComboBox accounts;
	private final JComboCheckBox categories = new JComboCheckBox();
	private final ScriptTableModel model;
	private final List<ScriptDefinition> scripts;
	private JButton submit;
	private boolean connected = true;

	static {
		SRC_SOURCES = new FileScriptSource(new File(Configuration.Paths.getScriptsSourcesDirectory()));
		SRC_PRE_COMPILED = new FileScriptSource(new File(Configuration.Paths.getScriptsPrecompiledDirectory()));
		SRC_NETWORK = ScriptDeliveryNetwork.getInstance();
	}

	public ScriptSelector(final BotGUI frame, final Bot bot) {
		super(frame, "Scripts", true);
		this.frame = frame;
		this.bot = bot;
		scripts = new ArrayList<ScriptDefinition>();
		connected = Preferences.getInstance().sdnShow;
		model = new ScriptTableModel(scripts);
		ScriptLikes.load();
	}

	public void showGUI() {
		init();
		update();
		load();
		setVisible(true);
	}

	void update() {
		final boolean available = bot.getScriptHandler().getRunningScripts().size() == 0;
		submit.setEnabled(available && table.getSelectedRow() != -1);
		table.setEnabled(available);
		search.setEnabled(available);
		accounts.setEnabled(available);
		table.clearSelection();
	}

	private void load() {
		scripts.clear();
		if (connected) {
			final List<ScriptDefinition> net = SRC_NETWORK.list();
			if (net != null) {
				scripts.addAll(net);
			}
		}
		Preferences.getInstance().sdnShow = connected;
		scripts.addAll(SRC_PRE_COMPILED.list());
		scripts.addAll(SRC_SOURCES.list());
		Collections.sort(scripts);

		populateCategories();
		filter();
		table.revalidate();
	}

	private void unload() {
		ScriptLikes.save();
	}

	private void populateCategories() {
		final LinkedHashSet<String> keywords = new LinkedHashSet<String>(scripts.size());
		for (final ScriptDefinition def : scripts) {
			keywords.addAll(def.getKeywords());
		}
		final String[] array = new String[keywords.size()];
		keywords.toArray(array);
		final List<String> list = Arrays.asList(array);
		Collections.sort(list);
		categories.populate(list, false);
		categories.setEnabled(list.size() != 0);
	}

	private void init() {
		setIconImage(Configuration.getImage(Configuration.Paths.Resources.ICON_SCRIPT));
		setLayout(new BorderLayout());
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		bot.getScriptHandler().addScriptListener(ScriptSelector.this);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				bot.getScriptHandler().removeScriptListener(ScriptSelector.this);
				setVisible(false);
				unload();
				dispose();
			}
		});
		final JButton refresh = new JButton(new ImageIcon(Configuration.getImage(Configuration.Paths.Resources.ICON_REFRESH)));
		refresh.setToolTipText("Refresh");
		refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				refresh.setEnabled(false);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						new Thread() {
							@Override
							public void run() {
								ScriptDeliveryNetwork.getInstance().refresh(true);
								load();
								refresh.setEnabled(true);
							}
						}.start();
					}
				});
			}
		});
		table = new JTable(model) {
			private static final long serialVersionUID = 6969410339933692133L;

			@Override
			public String getToolTipText(MouseEvent e) {
				int row = rowAtPoint(e.getPoint());
				ScriptDefinition def = model.getDefinition(row);
				return def.toString();
			}

			@Override
			public Component prepareRenderer(final TableCellRenderer renderer, final int row, final int column) {
				final Component comp = super.prepareRenderer(renderer, row, column);
				final ScriptDefinition def = model.getDefinition(row);
				final Color color;
				if (ScriptLikes.isLiked(def)) {
					color = new Color(0xffffcc);
				} else if (row % 2 == 0) {
					color = new Color(0xf8f8f8);
				} else {
					color = Color.WHITE;
				}
				comp.setBackground(isCellSelected(row, column) ? comp.getBackground() : color);
				return comp;
			}
		};
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent e) {
				if ((e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) {
					final int row = table.rowAtPoint(e.getPoint());
					table.getSelectionModel().setSelectionInterval(row, row);
					showMenu(e);
				}
			}

			private void showMenu(final MouseEvent e) {
				final int row = table.rowAtPoint(e.getPoint());
				final ScriptDefinition def = model.getDefinition(row);

				final JPopupMenu contextMenu = new JPopupMenu();
				final JMenuItem visit = new JMenuItem();
				visit.setText("Visit Site");
				visit.setIcon(new ImageIcon(Configuration.getImage(Configuration.Paths.Resources.ICON_WEBLINK)));
				visit.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(final MouseEvent e) {
						BotGUI.openURL(def.website);
					}
				});

				final JMenuItem start = new JMenuItem();
				start.setText(submit.getText());
				start.setIcon(new ImageIcon(Configuration.getImage(Configuration.Paths.Resources.ICON_PLAY)));
				start.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						submit.doClick();
					}
				});
				start.setEnabled(submit.isEnabled());

				final JMenuItem like = new JMenuItem();
				like.setText(ScriptLikes.isLiked(def) ? "Unlike" : "Like");
				like.setIcon(new ImageIcon(Configuration.getImage(
						ScriptLikes.isLiked(def) ? Configuration.Paths.Resources.ICON_UNLIKE : Configuration.Paths.Resources.ICON_LIKE)));
				like.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ScriptLikes.flip(def);
					}
				});

				final JMenuItem delete = new JMenuItem();
				delete.setText("Delete");
				delete.setIcon(new ImageIcon(Configuration.getImage(Configuration.Paths.Resources.ICON_CLOSE)));
				delete.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						final File path = def.path == null || def.path.isEmpty() ? null : new File(def.path);
						if (path != null && path.exists() && path.delete()) {
							log.info("Removed script " + def.getName());
						} else {
							log.warning("Could not remove " + def.getName());
						}
						scripts.remove(def);
						if (ScriptLikes.isLiked(def)) {
							ScriptLikes.flip(def);
						}
						load();
					}
				});

				if (def.website == null || def.website.isEmpty()) {
					visit.setEnabled(false);
				}

				contextMenu.add(start);
				contextMenu.add(like);
				contextMenu.add(visit);
				contextMenu.add(delete);
				contextMenu.show(table, e.getX(), e.getY());
			}
		});
		//table.setAutoCreateRowSorter(true);
		table.setRowHeight(20);
		table.setIntercellSpacing(new Dimension(1, 1));
		table.setShowGrid(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(new TableSelectionListener());
		setColumnWidths(table, 30, 200);
		final JToolBar toolBar = new JToolBar();
		toolBar.setMargin(new Insets(1, 1, 1, 1));
		toolBar.setFloatable(false);
		search = new JTextField();
		final Color searchDefaultColor = search.getForeground();
		final String searchDefaultText = "Search";
		search.setText(searchDefaultText);
		search.setForeground(searchAltColor);
		search.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(final FocusEvent e) {
				if (search.getForeground() == searchAltColor) {
					search.setText("");
					search.setForeground(searchDefaultColor);
				}
				table.clearSelection();
			}

			@Override
			public void focusLost(final FocusEvent e) {
				if (search.getText().isEmpty()) {
					search.setText(searchDefaultText);
					search.setForeground(searchAltColor);
				}
			}
		});
		search.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(final KeyEvent e) {
				filter();
				table.revalidate();
			}

			@Override
			public void keyReleased(final KeyEvent e) {
				keyTyped(e);
			}
		});
		submit = new JButton("Start", new ImageIcon(Configuration.getImage(Configuration.Paths.Resources.ICON_PLAY)));
		final JButton connect = new JButton(new ImageIcon(Configuration.getImage(connected ? Configuration.Paths.Resources.ICON_CONNECT : Configuration.Paths.Resources.ICON_DISCONNECT)));
		connect.setToolTipText("Show network scripts");
		submit.setEnabled(false);
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {
				final ScriptDefinition def = model.getDefinition(table.getSelectedRow());
				setVisible(false);
				final String account = (String) accounts.getSelectedItem();
				bot.getScriptHandler().removeScriptListener(ScriptSelector.this);
				unload();
				dispose();
				Script script = null;
				try {
					script = def.source.load(def);
				} catch (final ServiceException e) {
					log.severe(e.getMessage());
				}
				if (script != null) {
					bot.setAccount(account);
					bot.getScriptHandler().runScript(script);
					frame.updateScriptControls();
				}
			}
		});
		connect.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				final String icon = connected ? Configuration.Paths.Resources.ICON_DISCONNECT : Configuration.Paths.Resources.ICON_CONNECT;
				connect.setIcon(new ImageIcon(Configuration.getImage(icon)));
				connect.repaint();
				connected = !connected;
				load();
			}
		});
		accounts = new JComboBox(AccountManager.getAccountNames());
		accounts.setPreferredSize(new Dimension(125, 20));
		categories.setPreferredSize(new Dimension(150, 20));
		categories.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				final String[] selected = categories.getSelectedItems();
				final StringBuilder s = new StringBuilder(16);
				switch (selected.length) {
					case 0:
						s.append("Showing all");
						break;
					case 1:
						s.append(selected[0]);
						break;
					case 2:
						s.append(selected[0]);
						s.append(" & ");
						s.append(selected[1]);
						break;
					default:
						s.append("Showing ");
						s.append(selected.length);
						s.append(" types");
						break;
				}
				categories.setText(s.toString());
				filter();
			}
		});
		toolBar.add(search);
		toolBar.add(Box.createHorizontalStrut(5));
		toolBar.add(categories);
		toolBar.add(Box.createHorizontalStrut(5));
		toolBar.add(accounts);
		toolBar.add(Box.createHorizontalStrut(5));
		toolBar.add(refresh);
		toolBar.add(Box.createHorizontalStrut(5));
		toolBar.add(connect);
		toolBar.add(Box.createHorizontalStrut(5));
		toolBar.add(submit);
		final JPanel center = new JPanel();
		center.setLayout(new BorderLayout());
		final JScrollPane pane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		center.add(pane, BorderLayout.CENTER);
		add(center, BorderLayout.CENTER);
		add(toolBar, BorderLayout.SOUTH);
		setSize(750, 400);
		setMinimumSize(getSize());
		setLocationRelativeTo(getParent());
		search.requestFocus();
	}

	private void filter() {
		model.search((search == null || search.getForeground() == searchAltColor) ? "" : search.getText(), categories.getSelectedItems());
	}

	private void setColumnWidths(final JTable table, final int... widths) {
		for (int i = 0; i < widths.length; ++i) {
			final TableColumn col = table.getColumnModel().getColumn(i);
			col.setPreferredWidth(widths[i]);
			col.setMinWidth(widths[i]);
			col.setMaxWidth(widths[i]);
		}
	}

	public void scriptStarted(final ScriptHandler handler, final Script script) {
		update();
	}

	public void scriptStopped(final ScriptHandler handler, final Script script) {
		update();
	}

	public void scriptResumed(final ScriptHandler handler, final Script script) {
	}

	public void scriptPaused(final ScriptHandler handler, final Script script) {
	}

	public void inputChanged(final Bot bot, final int mask) {
	}

	private class TableSelectionListener implements ListSelectionListener {
		public void valueChanged(final ListSelectionEvent evt) {
			if (!evt.getValueIsAdjusting()) {
				submit.setEnabled(table.getSelectedRow() != -1);
			}
		}
	}

	private static class ScriptTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		public static final ImageIcon ICON_SCRIPT_SRC = new ImageIcon(Configuration.getImage(Configuration.Paths.Resources.ICON_SCRIPT_CODE));
		public static final ImageIcon ICON_SCRIPT_PRE = new ImageIcon(Configuration.getImage(Configuration.Paths.Resources.ICON_SCRIPT_GEAR));
		public static final ImageIcon ICON_SCRIPT_NET = new ImageIcon(Configuration.getImage(Configuration.Paths.Resources.ICON_SCRIPT_LIVE));
		private final List<ScriptDefinition> scripts;
		private final List<ScriptDefinition> matches;

		public ScriptTableModel(final List<ScriptDefinition> scripts) {
			this.scripts = scripts;
			matches = new ArrayList<ScriptDefinition>();
		}

		public void search(final String find, final String[] keys) {
			matches.clear();
			for (final ScriptDefinition def : scripts) {
				if (find.length() != 0 && !def.name.toLowerCase().contains(find)) {
					continue;
				}
				final List<String> keywords = def.getKeywords();
				final ArrayList<String> list = new ArrayList<String>(keywords.size());
				for (final String key : keywords) {
					list.add(key.toLowerCase());
				}
				boolean hit = true;
				for (final String key : keys) {
					if (!list.contains(key)) {
						hit = false;
						break;
					}
				}
				if (hit) {
					matches.add(def);
				}
			}
			fireTableDataChanged();
		}

		public ScriptDefinition getDefinition(final int rowIndex) {
			return matches.get(rowIndex);
		}

		public int getRowCount() {
			return matches.size();
		}

		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		public Object getValueAt(final int rowIndex, final int columnIndex) {
			if (rowIndex >= 0 && rowIndex < matches.size()) {
				final ScriptDefinition def = matches.get(rowIndex);
				switch (columnIndex) {
					case 0:
						if (def.source == SRC_SOURCES) {
							return ICON_SCRIPT_SRC;
						}
						if (def.source == SRC_PRE_COMPILED) {
							return ICON_SCRIPT_PRE;
						}
						return ICON_SCRIPT_NET;
					case 1:
						return def.getName();
					case 2:
						return def.getDescription();
				}
			}
			return null;
		}

		@Override
		public Class<?> getColumnClass(final int col) {
			if (col == 0) {
				return ImageIcon.class;
			}
			return String.class;
		}

		@Override
		public String getColumnName(final int col) {
			return COLUMN_NAMES[col];
		}
	}
}
