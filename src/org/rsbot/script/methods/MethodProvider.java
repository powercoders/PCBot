package org.rsbot.script.methods;

/**
 * A class that provides methods that use data from the game client. For
 * internal use.
 *
 * @author Jacmob
 */
public abstract class MethodProvider {

	protected final MethodContext methods;

	public MethodProvider(final MethodContext ctx) {
		methods = ctx;
	}

	/**
	 * Returns a linearly distributed pseudorandom integer.
	 *
	 * @param min The inclusive lower bound.
	 * @param max The exclusive upper bound.
	 * @return Random integer min <= n < max.
	 */
	public int random(final int min, final int max) {
		return min + (max == min ? 0 : methods.random.nextInt(max - min));
	}

	/**
	 * Returns a normally distributed pseudorandom integer about a mean centered
	 * between min and max with a provided standard deviation.
	 *
	 * @param min The inclusive lower bound.
	 * @param max The exclusive upper bound.
	 * @param sd  The standard deviation. A higher value will increase the
	 *            probability of numbers further from the mean being returned.
	 * @return Random integer min <= n < max from the normal distribution
	 *         described by the parameters.
	 */
	public int random(final int min, final int max, final int sd) {
		final int mean = min + (max - min) / 2;
		int rand;
		do {
			rand = (int) (methods.random.nextGaussian() * sd + mean);
		} while (rand < min || rand >= max);
		return rand;
	}

	/**
	 * Returns a normally distributed pseudorandom integer with a provided
	 * standard deviation about a provided mean.
	 *
	 * @param min  The inclusive lower bound.
	 * @param max  The exclusive upper bound.
	 * @param mean The mean (>= min and < max).
	 * @param sd   The standard deviation. A higher value will increase the
	 *             probability of numbers further from the mean being returned.
	 * @return Random integer min <= n < max from the normal distribution
	 *         described by the parameters.
	 */
	public int random(final int min, final int max, final int mean, final int sd) {
		int rand;
		do {
			rand = (int) (methods.random.nextGaussian() * sd + mean);
		} while (rand < min || rand >= max);
		return rand;
	}

	/**
	 * Returns a linearly distributed pseudorandom <code>double</code>.
	 *
	 * @param min The inclusive lower bound.
	 * @param max The exclusive upper bound.
	 * @return Random min <= n < max.
	 */
	public double random(final double min, final double max) {
		return min + methods.random.nextDouble() * (max - min);
	}

	/**
	 * @param toSleep The time to sleep in milliseconds.
	 */
	public void sleep(final int toSleep) {
		try {
			final long start = System.currentTimeMillis();
			Thread.sleep(toSleep);
			long now; // Guarantee minimum sleep
			while (start + toSleep > (now = System.currentTimeMillis())) {
				Thread.sleep(start + toSleep - now);
			}
		} catch (final InterruptedException ignored) {
		}
	}

}
