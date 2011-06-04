package org.rsbot.script.paint.methods;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.rsbot.Configuration;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.paint.PaintProvider;
import org.rsbot.script.util.Timer;

public class Utility extends PaintProvider {

	public Utility(MethodContext ctx) {
		super(ctx);
	}

	public Image getImage(final String fileName, final boolean save,
			final String url) {
		final Logger log = Logger.getLogger(this.getClass().getName());
		final File dir = new File(Configuration.Paths.getScriptCacheDirectory()
				+ "/Image_IO");
		try {
			if (!dir.exists()) {
				if (!dir.mkdir()) {
					return null;
				}
			}
		} catch (final Exception e) {
			return null;
		}
		ImageIO.setCacheDirectory(dir);
		if (save) {
			try {
				final File f = new File(
						Configuration.Paths.getScriptCacheDirectory() + "/"
								+ fileName);
				final File loc = new File(
						Configuration.Paths.getScriptCacheDirectory() + "/");
				if (loc.exists()) {
					if (f.exists()) {
						log.info("Successfully loaded Image from scripts folder.");
						return ImageIO.read(f.toURI().toURL());
					}
				}
				final Image img = ImageIO.read(new URL(url));
				if (img != null) {
					if (!loc.exists()) {
						loc.mkdir();
					}
					ImageIO.write((RenderedImage) img, "PNG", f);
					log.info("Saved Image to Scripts folder successfully.");
					return img;
				}
			} catch (final IOException e) {
				log.severe("No Internet Connection or Broken Image Link");
			}
		} else if (!save) {
			try {
				return ImageIO.read(new URL(url));
			} catch (final MalformedURLException e) {
			} catch (final IOException e) {
				log.severe("No Internet Connection or Broken Image Link");
			}
		}
		return null;
	}

	public String getRuntime(final long startTime) {
		return Timer.format(System.currentTimeMillis() - startTime);
	}

	public int getHourly(final int input, final long startTime) {
		final double millis = System.currentTimeMillis() - startTime;
		return (int) (input / millis * 3600000);
	}

	public Color getInverseColor(final Color color) {
		return new Color(255 - color.getRed(), 255 - color.getGreen(),
				255 - color.getBlue());
	}


}
