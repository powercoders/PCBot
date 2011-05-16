package org.rsbot.event.events;

import java.util.EventListener;
import org.rsbot.event.listeners.ServerMessageListener;

@Deprecated
public class ServerMessageEvent extends RSEvent
{
    private static final long serialVersionUID = -2786472026976811201L;
    private final String message;
    public ServerMessageEvent(String message)
    {
        this.message = message;
    }

    public void dispatch(EventListener el)
    {
        ((ServerMessageListener)el).serverMessageRecieved(this);
    }

    public long getMask()
    {
        return 1536L;
    }

    public String getMessage() {
        return this.message;
    }
}