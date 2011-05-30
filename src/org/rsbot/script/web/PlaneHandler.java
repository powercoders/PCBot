package org.rsbot.script.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.MethodProvider;

public class PlaneHandler extends MethodProvider {
	private class Traverse {
		public final PlaneTraverse example = new PlaneTraverse(0, null, null, methods);
	}

	List<PlaneTraverse> traverseList = new ArrayList<PlaneTraverse>();

	public PlaneHandler(final MethodContext ctx) {
		super(ctx);
		final Traverse traverse = new Traverse();
		traverseList.add(traverse.example);
	}

	public PlaneTraverse[] get(final int plane) {
		final List<PlaneTraverse> collectedTraverseList = new ArrayList<PlaneTraverse>();
		final Iterator<PlaneTraverse> planeTraverseIterator = traverseList.listIterator();
		while (planeTraverseIterator.hasNext()) {
			final PlaneTraverse planeTraverse = planeTraverseIterator.next();
			if (planeTraverse.plane() == plane) {
				collectedTraverseList.add(planeTraverse);
			}
		}
		return collectedTraverseList.toArray(new PlaneTraverse[collectedTraverseList.size()]);
	}
}
