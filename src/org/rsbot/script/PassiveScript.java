package org.rsbot.script;

import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.Methods;

import java.util.EventListener;

/**
 * A passive script.
 *
 * @author Timer
 */
public abstract class PassiveScript extends Methods implements EventListener, Runnable {
	protected String name = "";
	private volatile boolean enabled = true;
	private volatile boolean running = false;
	private int id = -1;

	public abstract boolean activateCondition();

	public abstract int loop();

	public abstract int iterationSleep();

	public boolean onStart() {
		return true;
	}

	public void onFinish() {

	}

	@Override
	public final void init(MethodContext ctx) {
		super.init(ctx);
		onStart();
	}

	/**
	 * Sets if it's enabled.
	 *
	 * @param enabled Enabled or not.
	 */
	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Runs the passive script.
	 */
	public final void run() {
		name = getClass().getAnnotation(PassiveScriptManifest.class).name();
		ctx.bot.getEventManager().addListener(this);
		running = true;
		try {
			while (running) {
				if (activateCondition()) {
					boolean start = onStart();
					if (start) {
						while (running) {
							int timeOut = loop();
							if (timeOut == -1) {
								break;
							}
							Thread.sleep(timeOut);
						}
						onFinish();
					}
				}
				Thread.sleep(iterationSleep());
			}
		} catch (Exception ignored) {
		}
		ctx.bot.getEventManager().removeListener(this);
		running = false;
	}

	/**
	 * Removes the script.
	 *
	 * @param id The id to deactivate.
	 */
	public final void deactivate(int id) {
		if (id != this.id) {
			throw new IllegalStateException("Invalid id!");
		}
		this.running = false;
	}

	/**
	 * Gives the script an id.
	 *
	 * @param id The id.
	 */
	public final void setID(int id) {
		if (this.id != -1) {
			throw new IllegalStateException("Already added to pool!");
		}
		this.id = id;
	}

	/**
	 * Gets the id of the script.
	 *
	 * @return The ID.
	 */
	public final int getID() {
		return id;
	}

	/**
	 * Checks if the script is running.
	 *
	 * @return <tt>true</tt> if true.
	 */
	public final boolean isRunning() {
		return running;
	}
}
