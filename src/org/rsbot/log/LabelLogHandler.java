package org.rsbot.log;

import java.awt.Color;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.JLabel;

public class LabelLogHandler extends Handler {

	public final JLabel label = new JLabel();

	@Override
	public void close() throws SecurityException {
	}

	@Override
	public void flush() {
	}

	@Override
	public void publish(final LogRecord record) {
		String msg = record.getMessage();
		if (record.getLevel().intValue() > Level.WARNING.intValue()) {
			label.setForeground(new Color(0xcc0000));
		} else {
			label.setForeground(new Color(0x0000cc));
			msg += " ...";
		}
		label.setText(msg);
	}

}
