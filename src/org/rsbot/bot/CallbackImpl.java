package org.rsbot.bot;

import org.rsbot.client.Callback;
import org.rsbot.client.Render;
import org.rsbot.client.RenderData;
import org.rsbot.event.events.CharacterMovedEvent;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.script.methods.MethodContext;

public class CallbackImpl implements Callback {

	private final Bot bot;

	public CallbackImpl(final Bot bot) {
		this.bot = bot;
	}

	@Override
	public Bot getBot() {
		return bot;
	}

	@Override
	public void notifyMessage(final int id, final String sender,
			final String msg) {
		final MessageEvent m = new MessageEvent(sender, id, msg);
		bot.getEventManager().dispatchEvent(m);
	}

	@Override
	public void rsCharacterMoved(final org.rsbot.client.RSCharacter c,
			final int i) {
		final CharacterMovedEvent e = new CharacterMovedEvent(bot.getMethodContext(), c, i);
		bot.getEventManager().dispatchEvent(e);
	}

	@Override
	public void updateRenderInfo(final Render r, final RenderData rd) {
		final MethodContext ctx = bot.getMethodContext();
		if (ctx != null) {
			ctx.calc.updateRenderInfo(r, rd);
		}
	}
}
