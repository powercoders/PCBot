package org.rsbot.script.wrappers;

import org.rsbot.client.LDModel;
import org.rsbot.script.methods.MethodContext;

class RSStaticModel extends RSModel {
	private final int x, y;

	RSStaticModel(final MethodContext ctx, final LDModel model, final int x,
			final int y) {
		super(ctx, model);
		this.x = x;
		this.y = y;
	}

	@Override
	protected int getLocalX() {
		return x;
	}

	@Override
	protected int getLocalY() {
		return y;
	}

	@Override
	protected void update() {

	}
}
