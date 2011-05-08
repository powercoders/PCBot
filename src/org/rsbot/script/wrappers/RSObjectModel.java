package org.rsbot.script.wrappers;

import org.rsbot.client.Model;
import org.rsbot.client.RSObject;
import org.rsbot.script.methods.MethodContext;

class RSObjectModel extends RSModel {

	private final RSObject object;

	RSObjectModel(final MethodContext ctx, final Model model, final RSObject object) {
		super(ctx, model);
		this.object = object;
	}

	@Override
	protected void update() {

	}

	@Override
	protected int getLocalX() {
		return object.getX();
	}

	@Override
	protected int getLocalY() {
		return object.getY();
	}

}
