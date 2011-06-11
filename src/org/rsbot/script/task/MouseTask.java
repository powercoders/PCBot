package org.rsbot.script.task;

import org.rsbot.script.callback.MouseMoveCallback;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.Methods;
import org.rsbot.script.wrappers.RSTarget;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jeroen
 * Date: 11-6-11
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
public class MouseTask extends AbstractTask {

	private RSTarget target;
	private MethodContext ctx;
	private MouseMoveCallback callback;
	protected List<ForceModifier> forceModifiers = new ArrayList<ForceModifier>(5);
	protected Vector2D velocity = new Vector2D();

	public MouseTask(RSTarget target, MouseMoveCallback callback, MethodContext ctx) {
		this.target = target;
		this.callback = callback;
		this.ctx = ctx;
	}

	public void run() {
		initForceModifiers();
		while (true) {
			Point p = target.getPoint();
			if (p.x == -1 || p.y == -1) {
				break;
			}

			if (target.contains(ctx.client.getMouse().getX(), ctx.client.getMouse().getY()) && callback.onMouseOver()) {
				break;
			}
			double deltaTime = Methods.random(8D, 10D) / 1000D;
			Vector2D force = new Vector2D();
			for (ForceModifier modifier : forceModifiers) {
				Vector2D f = modifier.apply(deltaTime, p);
				if (f == null) {
					continue;
				}
				force.add(f);
			}

			if (Double.isNaN(force.xUnits) || Double.isNaN(force.yUnits)) {
				return;
			}
			velocity.add(force.multiply(deltaTime));

			Vector2D deltaPosition = velocity.multiply(deltaTime);
			if (deltaPosition.xUnits != 0 && deltaPosition.yUnits != 0) {
				int x = ctx.client.getMouse().getX() + (int) deltaPosition.xUnits;
				int y = ctx.client.getMouse().getY() + (int) deltaPosition.yUnits;
				if (!ctx.client.getCanvas().contains(x, y)) {
					switch (ctx.inputManager.side) {
						case 1:
							x = 1;
							y = random(0, ctx.client.getCanvas().getHeight());
							break;
						case 2:
							x = random(0, ctx.client.getCanvas().getWidth());
							y = ctx.client.getCanvas().getHeight() + 1;
							break;
						case 3:
							x = ctx.client.getCanvas().getWidth() + 1;
							y = random(0, ctx.client.getCanvas().getHeight());
							break;
						case 4:
							x = random(0, ctx.client.getCanvas().getWidth());
							y = 1;
							break;
					}
				}
				ctx.mouse.hop(x, y);
			}

			try {
				Thread.sleep((long) (deltaTime * 1000));
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	public void initForceModifiers() {
		forceModifiers.add(new ForceModifier() {
			// TARGET GRAVITY
			public Vector2D apply(double deltaTime, Point pTarget) {
				Vector2D force = new Vector2D();

				Vector2D toTarget = new Vector2D();
				toTarget.xUnits = pTarget.x - ctx.client.getMouse().getX();
				toTarget.yUnits = pTarget.y - ctx.client.getMouse().getY();
				if (toTarget.xUnits == 0 && toTarget.yUnits == 0) {
					return null;
				}

				double alpha = toTarget.getAngle();
				double acc = random(1500, 2000);
				force.xUnits = Math.cos(alpha) * acc;
				force.yUnits = Math.sin(alpha) * acc;

				return force;
			}
		});

		forceModifiers.add(new ForceModifier() {
			// "friction"
			public Vector2D apply(double deltaTime, Point pTarget) {
				return velocity.multiply(-1);
			}
		});

		forceModifiers.add(new ForceModifier() {

			private int offset = random(300, 500);
			private double offsetAngle = -1;

			// Offset
			public Vector2D apply(double deltaTime, Point pTarget) {
				if (offsetAngle == -1) {
					offsetAngle = Methods.random(-Math.PI, Math.PI);
				}
				Vector2D toTarget = new Vector2D();
				toTarget.xUnits = pTarget.x - ctx.client.getMouse().getX();
				toTarget.yUnits = pTarget.y - ctx.client.getMouse().getY();
				if (offset > 0 && toTarget.getLength() > random(25, 55)) {
					Vector2D force = new Vector2D();
					force.xUnits = Math.cos(offsetAngle) * offset;
					force.yUnits = Math.sin(offsetAngle) * offset;
					offset -= random(0, 6);
					return force;
				}
				return null;
			}
		});

		forceModifiers.add(new ForceModifier() {
			// correction when close
			public Vector2D apply(double deltaTime, Point pTarget) {
				Vector2D toTarget = new Vector2D();
				toTarget.xUnits = pTarget.x - ctx.client.getMouse().getX();
				toTarget.yUnits = pTarget.y - ctx.client.getMouse().getY();
				double length = toTarget.getLength();
				if (length < random(75, 125)) {
					Vector2D force = new Vector2D();

					double speed = velocity.getLength();
					double rh = speed * speed;
					double s = toTarget.xUnits * toTarget.xUnits + toTarget.yUnits * toTarget.yUnits;
					if (s == 0) {
						return null;
					}
					double f = rh / s;
					f = Math.sqrt(f);
					Vector2D adjustedToTarget = toTarget.multiply(f);

					force.xUnits = (adjustedToTarget.xUnits - velocity.xUnits) / (deltaTime);
					force.yUnits = (adjustedToTarget.yUnits - velocity.yUnits) / (deltaTime);

					double v = 4D / length;
					if (v < 1D) {
						force = force.multiply(v);
					}
					if (length < 10) {
						force = force.multiply(0.5D);
					}
					return force;
				}
				return null;
			}
		});

		forceModifiers.add(new ForceModifier() {
			// correction when close
			public Vector2D apply(double deltaTime, Point pTarget) {
				int mouseX = ctx.client.getMouse().getX();
				int mouseY = ctx.client.getMouse().getY();
				//if(mouseX > pTarget.x-2 && mouseX < pTarget.x+2 && mouseY > pTarget.y-2 && mouseY < pTarget.y+2){
				if (mouseX == pTarget.x && mouseY == pTarget.y) {
					velocity.xUnits = 0;
					velocity.yUnits = 0;
				}
				return null;
			}
		});
	}

	/**
	 * Returns a linearly distributed pseudorandom integer.
	 *
	 * @param min The inclusive lower bound.
	 * @param max The exclusive upper bound.
	 * @return Random integer min <= n < max.
	 */
	public int random(final int min, final int max) {
		return min + (max == min ? 0 : ctx.random.nextInt(max - min));
	}

	interface ForceModifier {
		public Vector2D apply(double deltaTime, Point pTarget);
	}

	class Vector2D {
		public double xUnits;
		public double yUnits;

		public Vector2D sum(Vector2D vector) {
			Vector2D out = new Vector2D();
			out.xUnits = xUnits + vector.xUnits;
			out.yUnits = xUnits + vector.yUnits;
			return out;
		}

		public void add(Vector2D vector) {
			xUnits += vector.xUnits;
			yUnits += vector.yUnits;
		}

		public Vector2D multiply(double factor) {
			Vector2D out = new Vector2D();
			out.xUnits = xUnits * factor;
			out.yUnits = yUnits * factor;
			return out;
		}

		public double getLength() {
			return Math.sqrt(xUnits * xUnits + yUnits * yUnits);
		}

		public double getAngle() {
			return Math.atan2(yUnits, xUnits);
		}
	}

}
