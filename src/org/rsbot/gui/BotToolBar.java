package org.rsbot.gui;

import org.rsbot.Configuration;
import org.rsbot.script.methods.Environment;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Paris
 */
public class BotToolBar extends JToolBar {

	private static final long serialVersionUID = -1861866523519184211L;

	public static final int RUN_SCRIPT = 0;
	public static final int PAUSE_SCRIPT = 1;
	public static final int RESUME_SCRIPT = 2;

	public static final ImageIcon ICON_HOME;
	public static final ImageIcon ICON_BOT;

	public static Image IMAGE_CLOSE;
	public static final Image IMAGE_CLOSE_OVER;

	private static final int TABINDEX = 1;
	private static final int BUTTONCOUNT = 6;
	private static final int OPTIONBUTTONS = 4;

	static {
		ICON_HOME = new ImageIcon(Configuration.getImage(Configuration.Paths.Resources.ICON_HOME));
		ICON_BOT = new ImageIcon(Configuration.getImage(Configuration.Paths.Resources.ICON_BOT));
		IMAGE_CLOSE_OVER = Configuration.getImage(Configuration.Paths.Resources.ICON_CLOSE);
	}

	private final AddButton addTabButton;
	private final JButton screenshotButton;
	private final JButton userInputButton;
	private final JButton runScriptButton;
	private final JButton stopScriptButton;

	private final ActionListener listener;
	private int idx;
	private int inputState = Environment.INPUT_KEYBOARD | Environment.INPUT_MOUSE;
	private boolean inputOverride = true;

	public BotToolBar(final ActionListener listener, final BotMenuBar menu) {
		try {
			IMAGE_CLOSE = getTransparentImage(Configuration.getResourceURL(Configuration.Paths.Resources.ICON_CLOSE), 0.5f);
		} catch (final MalformedURLException e) {
		}

		this.listener = listener;

		screenshotButton = new JButton("Screenshot", new ImageIcon(
				Configuration.getImage(Configuration.Paths.Resources.ICON_PHOTO)));
		screenshotButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				menu.doClick(Messages.SAVESCREENSHOT);
			}
		});
		screenshotButton.setFocusable(false);
		screenshotButton.setToolTipText(screenshotButton.getText());
		screenshotButton.setText("");

		stopScriptButton = new JButton("Stop", new ImageIcon(
				Configuration.getImage(Configuration.Paths.Resources.ICON_DELETE)));
		stopScriptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menu.doClick(Messages.STOPSCRIPT);
			}
		});
		stopScriptButton.setFocusable(false);
		stopScriptButton.setToolTipText(stopScriptButton.getText());
		stopScriptButton.setText("");

		userInputButton = new JButton("Input", new ImageIcon(getInputImage(inputOverride, inputState)));
		userInputButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				menu.doTick(Messages.FORCEINPUT);
			}
		});
		userInputButton.setFocusable(false);
		userInputButton.setToolTipText(userInputButton.getText());
		userInputButton.setText("");

		runScriptButton = new JButton("Run", new ImageIcon(
				Configuration.getImage(Configuration.Paths.Resources.ICON_PLAY)));
		runScriptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch (getScriptButton()) {
					case RUN_SCRIPT:
						menu.doClick(Messages.RUNSCRIPT);
						break;
					case RESUME_SCRIPT:
					case PAUSE_SCRIPT:
						menu.doClick(Messages.PAUSESCRIPT);
						break;
				}
			}
		});
		runScriptButton.setFocusable(false);
		runScriptButton.setToolTipText(runScriptButton.getText());
		runScriptButton.setText("");

		final HomeButton home = new HomeButton(ICON_HOME);

		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		setFloatable(false);
		add(home);
		add(addTabButton = new AddButton(listener));
		add(Box.createHorizontalGlue());
		add(screenshotButton);
		add(runScriptButton);
		add(stopScriptButton);
		add(userInputButton);
		updateSelection(false);
	}

	public void setAddTabVisible(final boolean visible) {
		addTabButton.setVisible(visible);
	}

	public void addTab() {
		final int idx = getComponentCount() - BUTTONCOUNT - TABINDEX + 1;
		add(new BotButton(Messages.TABDEFAULTTEXT, ICON_BOT), idx);
		validate();
		setSelection(idx);
	}

	public void removeTab(int idx) {
		final int current = getCurrentTab() + TABINDEX;
		final int select = idx == current ? idx - TABINDEX : current;
		idx += TABINDEX;
		remove(idx);
		revalidate();
		repaint();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setSelection(Math.max(0, select - 1));
			}
		});
	}

	public void setTabLabel(final int idx, final String label) {
		((BotButton) getComponentAtIndex(idx + TABINDEX)).setText(label);
	}

	public int getCurrentTab() {
		if (idx > -1 && idx < getComponentCount() - OPTIONBUTTONS) {
			return idx - TABINDEX;
		} else {
			return -1;
		}
	}

	public int getScriptButton() {
		final String label = runScriptButton.getToolTipText();
		if (label.equals("Run")) {
			return RUN_SCRIPT;
		} else if (label.equals("Pause")) {
			return PAUSE_SCRIPT;
		} else if (label.equals("Resume")) {
			return RESUME_SCRIPT;
		} else {
			throw new IllegalStateException("Illegal script button state!");
		}
	}

	public void setHome(final boolean home) {
		for (final JButton button : new JButton[]{screenshotButton, stopScriptButton, userInputButton, runScriptButton}) {
			button.setEnabled(!home);
			button.setVisible(!home);
		}
	}

	public void setInputState(final int state) {
		inputState = state;
	}

	public void setOverrideInput(final boolean selected) {
		inputOverride = selected;
	}

	public void updateInputButton() {
		userInputButton.setIcon(new ImageIcon(getInputImage(inputOverride, inputState)));
	}

	public void setScriptButton(final int state) {
		String text = null, pathResource = null;
		boolean running = true;

		switch (state) {
			case RUN_SCRIPT:
				text = "Run";
				pathResource = Configuration.Paths.Resources.ICON_PLAY;
				running = false;
				break;
			case PAUSE_SCRIPT:
				text = "Pause";
				pathResource = Configuration.Paths.Resources.ICON_PAUSE;
				break;
			case RESUME_SCRIPT:
				text = "Resume";
				pathResource = Configuration.Paths.Resources.ICON_START;
				break;
		}

		stopScriptButton.setVisible(running);
		runScriptButton.setToolTipText(text);
		runScriptButton.setIcon(new ImageIcon(Configuration.getImage(pathResource)));
		runScriptButton.repaint();
		revalidate();
	}

	private void setSelection(final int idx) {
		updateSelection(true);
		this.idx = idx;
		updateSelection(false);
		listener.actionPerformed(new ActionEvent(this, 0, "Tab"));
	}

	private void updateSelection(final boolean enabled) {
		final int idx = getCurrentTab() + TABINDEX;
		if (idx >= 0) {
			getComponent(idx).setEnabled(enabled);
			getComponent(idx).repaint();
		}
	}

	private Image getInputImage(final boolean override, final int state) {
		if (override || state == (Environment.INPUT_KEYBOARD | Environment.INPUT_MOUSE)) {
			return Configuration.getImage(Configuration.Paths.Resources.ICON_TICK);
		} else if (state == Environment.INPUT_KEYBOARD) {
			return Configuration.getImage(Configuration.Paths.Resources.ICON_KEYBOARD);
		} else if (state == Environment.INPUT_MOUSE) {
			return Configuration.getImage(Configuration.Paths.Resources.ICON_MOUSE);
		} else {
			return Configuration.getImage(Configuration.Paths.Resources.ICON_DELETE);
		}
	}

	private static Image getTransparentImage(final URL url, final float transparency) {
		BufferedImage loaded = null;
		try {
			loaded = ImageIO.read(url);
		} catch (final IOException e) {
		}
		final BufferedImage aimg = new BufferedImage(loaded.getWidth(), loaded.getHeight(), Transparency.TRANSLUCENT);
		final Graphics2D g = aimg.createGraphics();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
		g.drawImage(loaded, null, 0, 0);
		g.dispose();
		return aimg;
	}

	/**
	 * @author Jacmob
	 */
	private class HomeButton extends JPanel {

		private static final long serialVersionUID = 938456324328L;

		private final Image image;
		private boolean hovered;

		public HomeButton(final ImageIcon icon) {
			super(new BorderLayout());
			image = icon.getImage();
			setBorder(new EmptyBorder(3, 6, 2, 3));
			setPreferredSize(new Dimension(24, 22));
			setMaximumSize(new Dimension(24, 22));
			setFocusable(false);
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(final MouseEvent e) {
					setSelection(getComponentIndex(HomeButton.this));
				}

				@Override
				public void mouseEntered(final MouseEvent e) {
					hovered = true;
					repaint();
				}

				@Override
				public void mouseExited(final MouseEvent e) {
					hovered = false;
					repaint();
				}
			});
		}

		@Override
		public void paintComponent(final Graphics g) {
			super.paintComponent(g);
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			if (getComponentIndex(this) == idx) {
				g.setColor(new Color(255, 255, 255, 200));
				g.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 1, 4, 4);
				g.setColor(new Color(180, 180, 180, 200));
				g.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 1, 4, 4);
			} else if (hovered) {
				g.setColor(new Color(255, 255, 255, 150));
				g.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 1, 4, 4);
				g.setColor(new Color(180, 180, 180, 150));
				g.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 1, 4, 4);
			}
			g.drawImage(image, 3, 3, null);
		}

	}

	/**
	 * @author Tekk
	 */
	private class BotButton extends JPanel {

		private static final long serialVersionUID = 329845763420L;

		private final JLabel nameLabel;
		private boolean hovered;
		private boolean close;

		public BotButton(final String text, final Icon icon) {
			super(new BorderLayout());
			setBorder(new EmptyBorder(3, 6, 2, 3));
			nameLabel = new JLabel(text);
			nameLabel.setIcon(icon);
			nameLabel.setPreferredSize(new Dimension(85, 22));
			nameLabel.setMaximumSize(new Dimension(85, 22));
			add(nameLabel, BorderLayout.WEST);

			setPreferredSize(new Dimension(110, 22));
			setMaximumSize(new Dimension(110, 22));
			setFocusable(false);
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(final MouseEvent e) {
					if (hovered && close) {
						final int idx = getComponentIndex(BotButton.this) - TABINDEX;
						listener.actionPerformed(new ActionEvent(this,
								ActionEvent.ACTION_PERFORMED, Messages.CLOSEBOT + "." + idx));
					} else {
						setSelection(getComponentIndex(BotButton.this));
					}
				}

				@Override
				public void mouseEntered(final MouseEvent e) {
					hovered = true;
					repaint();
				}

				@Override
				public void mouseExited(final MouseEvent e) {
					hovered = false;
					repaint();
				}
			});
			addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseMoved(final MouseEvent e) {
					close = e.getX() > 95;
					repaint();
				}
			});
		}

		public void setText(final String label) {
			nameLabel.setText(label);
		}

		@Override
		public void paintComponent(final Graphics g) {
			super.paintComponent(g);
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			final int RGB = getComponentIndex(this) == idx ? 255 : hovered ? 230 : 215;
			g.setColor(new Color(RGB, RGB, RGB, 200));
			g.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 1, 4, 4);
			g.setColor(new Color(180, 180, 180, 200));
			g.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 1, 4, 4);
			g.drawImage(hovered && close ? IMAGE_CLOSE_OVER : IMAGE_CLOSE, 90, 3, null);
		}
	}

	private static class AddButton extends JComponent {

		private static final long serialVersionUID = 1L;

		private static Image ICON;
		private static Image ICON_OVER;
		private static final Image ICON_DOWN;
		private boolean hovered = false;
		private boolean pressed = false;

		static {
			ICON_DOWN = Configuration.getImage(Configuration.Paths.Resources.ICON_ADD);
		}

		public AddButton(final ActionListener listener) {
			URL src = null;
			try {
				src = Configuration.getResourceURL(Configuration.Paths.Resources.ICON_ADD);
			} catch (final MalformedURLException e) {
			}
			ICON = getTransparentImage(src, 0.3f);
			ICON_OVER = getTransparentImage(src, 0.7f);

			setPreferredSize(new Dimension(20, 20));
			setMaximumSize(new Dimension(20, 20));
			setFocusable(false);
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(final MouseEvent e) {
					hovered = true;
					repaint();
				}

				@Override
				public void mouseExited(final MouseEvent e) {
					hovered = false;
					repaint();
				}

				@Override
				public void mousePressed(final MouseEvent e) {
					pressed = true;
					repaint();
				}

				@Override
				public void mouseReleased(final MouseEvent e) {
					pressed = false;
					repaint();
					listener.actionPerformed(new ActionEvent(this, e.getID(), "File.New Bot"));
				}
			});
		}

		@Override
		public void paintComponent(final Graphics g) {
			super.paintComponent(g);
			if (pressed) {
				g.drawImage(ICON_DOWN, 2, 2, null);
			} else if (hovered) {
				g.drawImage(ICON_OVER, 2, 2, null);
			} else {
				g.drawImage(ICON, 2, 2, null);
			}
		}

	}

}