package org.rsbot.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.rsbot.Configuration;
import org.rsbot.bot.Bot;
import org.rsbot.gui.component.JComboCheckBox;
import org.rsbot.script.Script;
import org.rsbot.script.Script.Category;
import org.rsbot.script.internal.ScriptHandler;
import org.rsbot.script.internal.event.ScriptListener;
import org.rsbot.script.provider.FileScriptSource;
import org.rsbot.script.provider.ScriptDefinition;
import org.rsbot.script.provider.ScriptDeliveryNetwork;
import org.rsbot.script.provider.ScriptSource;
import org.rsbot.service.ServiceException;

/**
 * @author Paris
 * @author Jacmob
 * @author Fletch to 99
 */
public class ScriptSelector extends JDialog implements ScriptListener {
	private static class ScriptTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		public static final ImageIcon ICON_SCRIPT_SRC = new ImageIcon(
				Configuration
						.getImage(Configuration.Paths.Resources.ICON_SCRIPT_EDIT));
		public static final ImageIcon ICON_SCRIPT_PRE = new ImageIcon(
				Configuration
						.getImage(Configuration.Paths.Resources.ICON_SCRIPT_GEAR));
		public static final ImageIcon ICON_SCRIPT_NET = new ImageIcon(
				Configuration
						.getImage(Configuration.Paths.Resources.ICON_SCRIPT_LIVE));
		private final List<ScriptDefinition> scripts;
		private final List<ScriptDefinition> matches;

		public ScriptTableModel(final List<ScriptDefinition> scripts) {
			this.scripts = scripts;
			matches = new ArrayList<ScriptDefinition>();
		}

		@Override
		public Class<?> getColumnClass(final int col) {
			if (col == 0) {
				return ImageIcon.class;
			}
			return String.class;
		}

		@Override
		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		@Override
		public String getColumnName(final int col) {
			return COLUMN_NAMES[col];
		}

		public ScriptDefinition getDefinition(final int rowIndex) {
			return matches.get(rowIndex);
		}

		@Override
		public int getRowCount() {
			return matches.size();
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex) {
			if (rowIndex >= 0 && rowIndex < matches.size()) {
				final ScriptDefinition def = matches.get(rowIndex);
				switch (columnIndex) {
				case 0:
					if (def.source == SRC_SOURCES) {
						return ICON_SCRIPT_SRC;
					}
					if (def.source == SRC_PRECOMPILED) {
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

		public void filter(final String find, final String[] keys) {
			matches.clear();
			final String item = find.toLowerCase().trim();
			for (final ScriptDefinition def : scripts) {
				if (def.category == null) {
					def.category = Script.Category.OTHER;
				}
				final String name = def.name.toLowerCase().trim();
				final String[] keywords = def.getKeywords();
				boolean hit = false;
				final String category = def.category.description()
						.toLowerCase().trim();
				for (final String key : keywords) {
					final String keyword = key.toLowerCase().trim();
					if (keys.length == 0 && item.length() == 0
							&& keyword.length() != 0) {
						hit = true;
						break;
					} else if (keys.length != 0 && item.length() == 0) {
						for (final String string : keys) {
							if (string.toLowerCase().trim().contains(category)) {
								hit = true;
								break;
							}
						}
					} else if (item.length() != 0 && keys.length == 0
							&& keyword.length() != 0) {
						if (item.contains(keyword) || keyword.contains(item)
								|| name.contains(item) || item.contains(name)) {
							hit = true;
							break;
						}
					} else if (item.length() != 0 && keys.length != 0
							&& keyword.length() != 0) {
						for (final String string : keys) {
							if (string.toLowerCase().trim().contains(category)) {
								if (item.contains(keyword)
										|| keyword.contains(item)
										|| name.contains(item)
										|| item.toLowerCase().trim()
												.contains(name)) {
									hit = true;
									break;
								}
							}
						}
					}
				}
				if (hit) {
					matches.add(def);
				}
			}
			fireTableDataChanged();
		}
	}

	private class TableSelectionListener implements ListSelectionListener {
		@Override
		public void valueChanged(final ListSelectionEvent evt) {
			if (!evt.getValueIsAdjusting()) {
				submit.setEnabled(table.getSelectedRow() != -1);
			}
		}
	}

	private static final long serialVersionUID = 5475451138208522511L;

	private static final Logger log = Logger.getLogger(ScriptSelector.class
			.getName());
	private static final String[] COLUMN_NAMES = new String[] { "", "Name",
			"Description" };
	private static final ScriptSource SRC_SOURCES;
	private static final ScriptSource SRC_PRECOMPILED;
	private static final ScriptSource SRC_NETWORK;
	private final BotGUI frame;
	private final Bot bot;
	private JTable table;
	private JTextField search;
	private JComboBox accounts;
	private final JComboCheckBox categories = new JComboCheckBox();
	private final ScriptTableModel model;
	private final List<ScriptDefinition> scripts;

	private JButton submit;

	private boolean connected = true;

	static {
		SRC_SOURCES = new FileScriptSource(new File(
				Configuration.Paths.getScriptsSourcesDirectory()));
		SRC_PRECOMPILED = new FileScriptSource(new File(
				Configuration.Paths.getScriptsPrecompiledDirectory()));
		SRC_NETWORK = ScriptDeliveryNetwork.getInstance();
	}

	public ScriptSelector(final BotGUI frame, final Bot bot) {
		super(frame, "Script Selector", true);
		this.frame = frame;
		this.bot = bot;
		scripts = new ArrayList<ScriptDefinition>();
		model = new ScriptTableModel(scripts);
	}

	private void filter() {
		model.filter(search == null || search.getText().contains("\0") ? ""
				: search.getText(), categories.getSelectedItems());
	}

	private void init() {
		setIconImage(Configuration
				.getImage(Configuration.Paths.Resources.ICON_SCRIPT));
		setLayout(new BorderLayout());
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		bot.getScriptHandler().addScriptListener(ScriptSelector.this);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				bot.getScriptHandler()
						.removeScriptListener(ScriptSelector.this);
				dispose();
			}
		});
		final Color searchAltColor = Color.GRAY;
		final JButton refresh = new JButton(new ImageIcon(
				Configuration
						.getImage(Configuration.Paths.Resources.ICON_REFRESH)));
		refresh.setToolTipText("Refresh");
		refresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				refresh.setEnabled(false);
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						new Thread() {
							@Override
							public void run() {
								ScriptDeliveryNetwork.getInstance().refresh(
										true);
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
			public String getToolTipText(final MouseEvent e) {
				final int row = rowAtPoint(e.getPoint());
				final ScriptDefinition def = model.getDefinition(row);
				return def.toString();
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
				visit.setIcon(new ImageIcon(Configuration
						.getImage(Configuration.Paths.Resources.ICON_WEBLINK)));
				visit.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(final MouseEvent e) {
						BotGUI.openURL(def.website);
					}
				});

				final JMenuItem start = new JMenuItem();
				start.setText(submit.getText());
				start.setIcon(new ImageIcon(Configuration
						.getImage(Configuration.Paths.Resources.ICON_PLAY)));
				start.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						submit.doClick();
					}
				});
				start.setEnabled(submit.isEnabled());

				final JMenuItem delete = new JMenuItem();
				delete.setText("Delete");
				delete.setIcon(new ImageIcon(Configuration
						.getImage(Configuration.Paths.Resources.ICON_CLOSE)));
				delete.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						final File path = def.path == null
								|| def.path.isEmpty() ? null : new File(
								def.path);
						if (path != null && path.exists() && path.delete()) {
							log.info("Deleted script " + def.name + " ("
									+ def.path + ")");
						} else {
							log.warning("Could not delete " + def.name);
						}
						scripts.remove(def);
						load();
					}
				});

				if (def.website == null || def.website.isEmpty()) {
					visit.setEnabled(false);
				}

				contextMenu.add(start);
				contextMenu.add(visit);
				contextMenu.add(delete);
				contextMenu.show(table, e.getX(), e.getY());
			}
		});
		table.setRowHeight(20);
		table.setIntercellSpacing(new Dimension(1, 1));
		table.setShowGrid(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(
				new TableSelectionListener());
		setColumnWidths(table, 30, 200);
		final JToolBar toolBar = new JToolBar();
		toolBar.setMargin(new Insets(1, 1, 1, 1));
		toolBar.setFloatable(false);
		search = new JTextField();
		final Color searchDefaultColor = search.getForeground();
		final String searchDefaultText = "Type to filter...\0";
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
			public void keyReleased(final KeyEvent e) {
				keyTyped(e);
			}

			@Override
			public void keyTyped(final KeyEvent e) {
				filter();
				table.revalidate();
			}
		});
		submit = new JButton("Start",
				new ImageIcon(Configuration
						.getImage(Configuration.Paths.Resources.ICON_PLAY)));
		final JButton connect = new JButton(new ImageIcon(
				Configuration
						.getImage(Configuration.Paths.Resources.ICON_CONNECT)));
		connect.setToolTipText("Show network scripts");
		submit.setEnabled(false);
		submit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				final ScriptDefinition def = model.getDefinition(table
						.getSelectedRow());
				setVisible(false);
				final String account = (String) accounts.getSelectedItem();
				bot.getScriptHandler()
						.removeScriptListener(ScriptSelector.this);
				dispose();
				new Thread() {
					@Override
					public void run() {
						Script script = null;
						frame.updateScriptControls(true);
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
				}.start();
			}
		});
		if (connect.isEnabled()) {
			final ActionListener listenConnect = new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					final String icon = connected ? Configuration.Paths.Resources.ICON_DISCONNECT
							: Configuration.Paths.Resources.ICON_CONNECT;
					connect.setIcon(new ImageIcon(Configuration.getImage(icon)));
					connect.repaint();
					connected = !connected;
					load();
				}
			};
			connect.addActionListener(listenConnect);
		}
		accounts = new JComboBox(AccountManager.getAccountNames());
		accounts.setPreferredSize(new Dimension(125, 20));
		categories.setPreferredSize(new Dimension(150, 20));
		categories.addActionListener(new ActionListener() {
			@Override
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
		final JScrollPane pane = new JScrollPane(table,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		center.add(pane, BorderLayout.CENTER);
		add(center, BorderLayout.CENTER);
		add(toolBar, BorderLayout.SOUTH);
		setSize(750, 400);
		setMinimumSize(getSize());
		setLocationRelativeTo(getParent());
		search.requestFocus();
	}

	@Override
	public void inputChanged(final Bot bot, final int mask) {
	}

	private boolean firstRun = true;

	private void load() {
		scripts.clear();
		if (connected) {
			final List<ScriptDefinition> net = SRC_NETWORK.list();
			if (net != null) {
				scripts.addAll(net);
			}
		}
		scripts.addAll(SRC_PRECOMPILED.list());
		scripts.addAll(SRC_SOURCES.list());
		Collections.sort(scripts);
		if (firstRun) {
			ArrayList<String> keywords = new ArrayList<String>(
					Category.values().length);
			for (Category c : Category.values()) {
				String id = c.description().trim();
				if (id.length() > 0 && !keywords.contains(id)) {
					keywords.add(id);
				}
			}
			categories.populate(keywords, false);
			firstRun = false;
		}
		filter();
		table.revalidate();
	}

	@Override
	public void scriptPaused(final ScriptHandler handler, final Script script) {
	}

	@Override
	public void scriptResumed(final ScriptHandler handler, final Script script) {
	}

	@Override
	public void scriptStarted(final ScriptHandler handler, final Script script) {
		update();
	}

	@Override
	public void scriptStopped(final ScriptHandler handler, final Script script) {
		update();
	}

	private void setColumnWidths(final JTable table, final int... widths) {
		for (int i = 0; i < widths.length; ++i) {
			final TableColumn col = table.getColumnModel().getColumn(i);
			col.setPreferredWidth(widths[i]);
			col.setMinWidth(widths[i]);
			col.setMaxWidth(widths[i]);
		}
	}

	public void showGUI() {
		init();
		update();
		load();
		setVisible(true);
	}

	public void update() {
		final boolean available = bot.getScriptHandler().getRunningScripts()
				.size() == 0;
		submit.setEnabled(available && table.getSelectedRow() != -1);
		table.setEnabled(available);
		search.setEnabled(available);
		accounts.setEnabled(available);
		table.clearSelection();
	}
}
