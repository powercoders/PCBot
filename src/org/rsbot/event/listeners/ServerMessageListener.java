package org.rsbot.event.listeners;

import java.util.EventListener;
import org.rsbot.event.events.ServerMessageEvent;

@Deprecated
public abstract interface ServerMessageListener extends EventListener
{
  public abstract void serverMessageRecieved(ServerMessageEvent paramServerMessageEvent);
}