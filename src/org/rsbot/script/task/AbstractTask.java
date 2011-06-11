package org.rsbot.script.task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class AbstractTask implements Task {

	private Future f;

	public boolean isDone() {
		return f.isDone();
	}

	public void stop() {
		f.cancel(true);
	}

	public void join() {
		try {
			f.get();
		} catch (InterruptedException ignored) {
		} catch (ExecutionException ignored) {
		}
	}

	public void init(Future f) {
		this.f = f;
	}
}