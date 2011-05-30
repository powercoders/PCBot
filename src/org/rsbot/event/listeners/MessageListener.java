package org.rsbot.event.listeners;

import java.util.EventListener;

import org.rsbot.event.events.MessageEvent;

public interface MessageListener extends EventListener {
	abstract void messageReceived(MessageEvent e);
}
