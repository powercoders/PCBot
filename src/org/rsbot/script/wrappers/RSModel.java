package org.rsbot.script.wrappers;

import org.rsbot.client.Model;
import org.rsbot.client.ModelCapture;
import org.rsbot.script.methods.Calculations;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.MethodProvider;
import org.rsbot.script.util.Filter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A screen space model.
 *
 */
public abstract class RSModel extends MethodProvider implements RSTarget {

    /**
     * Returns a filter that matches against the array of point indices for the
     * A vertices of each triangle. Use in scripts is discouraged.
     *
     * @param vertex_a The array of indices for A vertices.
     * @return The vertex point index based model filter.
     */
    public static Filter<RSModel> newVertexFilter(final short[] vertex_a) {
        return new Filter<RSModel>() {
            public boolean accept(final RSModel m) {
                return Arrays.equals(m.indices1, vertex_a);
            }
        };
    }

    protected int[] xPoints;
    protected int[] yPoints;
    protected int[] zPoints;

    protected short[] indices1;
    protected short[] indices2;
    protected short[] indices3;

    protected int numVertices;
    protected int numFaces;

    public RSModel(final MethodContext ctx, final Model model) {
        super(ctx);
        xPoints = model.getXPoints();
        yPoints = model.getYPoints();
        zPoints = model.getZPoints();
        indices1 = model.getIndices1();
        indices2 = model.getIndices2();
        indices3 = model.getIndices3();
        if (model instanceof ModelCapture) {
            numVertices = ((ModelCapture) model).getNumVertices();
            numFaces = ((ModelCapture) model).getNumFaces();
        } else {
            numVertices = Math.min(xPoints.length, Math.min(yPoints.length, zPoints.length));
            numFaces = Math.min(indices1.length, Math.min(indices2.length, indices3.length));
        }
    }

    protected abstract int getLocalX();

    protected abstract int getLocalY();

    protected abstract void update();

    public boolean contains(final Point p) {
        return contains(p.x, p.y);
    }



    /**
     * @param x The x location to check
     * @param y The y location to check
     * @return true of the point is within the bounds of the model
     */
    public boolean contains(int x, int y) {
        final int[][] points = projectVertices();
        for (int i = 0; i < numFaces; i++) {
            int index1 = indices1[i];
            int index2 = indices2[i];
            int index3 = indices3[i];
            if (points[index1][2] + points[index2][2] + points[index3][2] == 3) {
                if (y < points[index1][1] && y < points[index2][1] && y < points[index3][1]) {
                    continue;
                }
                if (y > points[index1][1] && y > points[index2][1] && y > points[index3][1]) {
                    continue;
                }
                if (x < points[index1][0] && x < points[index2][0] && x < points[index3][0]) {
                    continue;
                }
                return x <= points[index1][0] || x <= points[index2][0] || x <= points[index3][0];
            }
        }
        return false;
    }

    /**
     * Clicks the RSModel.
     *
     * @param leftClick if true it left clicks.
     * @return true if clicked.
     */
    public boolean doClick(final boolean leftClick) {
        try {
            for (int i = 0; i < 10; i++) {
                methods.mouse.move(getPoint());
                if (contains(methods.mouse.getLocation())) {
                    methods.mouse.click(leftClick);
                    return true;
                }
            }
        } catch (final Exception ignored) {
        }
        return false;
    }

    /**
     * Clicks the RSModel and clicks the menu action
     *
     * @param action the action to be clicked in the menu
     * @param option the option of the action to be clicked in the menu
     * @return true if clicked, false if failed.
     */
    public boolean interact(final String action, final String option) {
        try {
            for (int i = 0; i < 10; i++) {
                methods.mouse.move(getPoint());
                if (contains(methods.mouse.getLocation())) {
                    if (methods.menu.doAction(action, option)) {
                        return true;
                    }
                }
            }
        } catch (final Exception ignored) {
        }
        return false;
    }

    /**
     * Clicks the RSModel and clicks the menu action
     *
     * @param action the action to be clicked in the menu
     * @param option the option of the action to be clicked in the menu
     * @return true if clicked, false if failed.
     * @see org.rsbot.script.wrappers.RSModel#interact(String, String)
     */
    @Deprecated
    public boolean doAction(final String action, final String option) {
        return interact(action, option);
    }

    /**
     * Clicks the RSModel and clicks the menu action
     *
     * @param action the action to be clicked in the menu
     * @return true if clicked, false if failed.
     */
    public boolean interact(final String action) {
        return interact(action, null);
    }

    /**
     * Clicks the RSModel and clicks the menu action
     *
     * @param action the action to be clicked in the menu
     * @return true if clicked, false if failed.
     * @see org.rsbot.script.wrappers.RSModel#interact(String)
     */
    @Deprecated
    public boolean doAction(final String action) {
        return interact(action);
    }

    /**
     * Returns a random screen point.
     *
     * @return A screen point, or Point(-1, -1) if the model is not on screen.
     * @see #getCentralPoint()
     * @see #getPointOnScreen()
     */
    public Point getPoint() {
        update();
        final int len = numFaces;
        final int sever = random(0, len);
        Point point = getPointInRange(sever, len);
        if (point != null) {
            return point;
        }
        point = getPointInRange(0, sever);
        if (point != null) {
            return point;
        }
        return new Point(-1, -1);
    }

    /**
     * Returns all the screen points.
     *
     * @return All the points that are on the screen, if the model is not on the
     *         screen it will return null.
     */
    public Point[] getPoints() {
        final ArrayList<Point> out = new ArrayList<Point>(numVertices); // Assume every vertex is on screen
        final int[][] points = projectVertices();
        for (int index = 0; index < numVertices; index++) {
            if (points[index][2] == 1) {
                out.add(new Point(points[index][0], points[index][1]));
            }
        }
        return out.toArray(new Point[out.size()]);
    }

    /**
     * Gets a point on a model that is on screen.
     *
     * @return First point that it finds on screen else a random point on screen
     *         of an object.
     */
    public Point getPointOnScreen() {
        final int[][] points = projectVertices();
        Point point = new Point();
        for (int index = 0; index < numVertices; index++) {
            if (points[index][2] == 1) {
                point.x = points[index][0];
                point.y = points[index][1];
                if (methods.calc.pointOnScreen(point)) {
                    return point;
                }
            }
        }
        int index = random(0, numVertices);
        point.x = points[index][0];
        point.y = points[index][1];
        return point;
    }

    /**
     * Generates a rough central point. Performs the calculation by first
     * generating a rough point, and then finding the point closest to the rough
     * point that is actually on the RSModel.
     *
     * @return The rough central point.
     */
    public Point getCentralPoint() {
        try {
            /* Add X and Y of all points, to get a rough central point */
            int x = 0, y = 0, total = 0;
            int[][] points = projectVertices();
            for (int index = 0; index < numVertices; index++) {
                if (points[index][2] == 1) {
                    x += points[index][0];
                    y += points[index][1];
                    total++;
                }
            }
            final Point central = new Point(x / total, y / total);
            final Point point = new Point();
            /*
                * Find a real point on the character that is closest to the central
                * point
                */
            Point curCentral = new Point();
            double dist = 20000;

            for (int index = 0; index < numVertices; index++) {
                if (points[index][2] == 1) {
                    point.x = points[index][0];
                    point.y = points[index][1];
                    if (!methods.calc.pointOnScreen(point)) {
                        continue;
                    }
                    final double dist2 = methods.calc.distanceBetween(central, point);
                    if (dist2 < dist) {
                        curCentral.x = point.x;
                        curCentral.y = point.y;
                        dist = dist2;
                    }
                }
            }
            return curCentral;
        } catch (final Exception ignored) {
        }
        return new Point(-1, -1);
    }

    /**
     * Returns an array of triangles containing the screen points of this model.
     *
     * @return The on screen triangles of this model.
     */
    public Polygon[] getTriangles() {
        int[][] points = projectVertices();
        ArrayList<Polygon> polys = new ArrayList<Polygon>(numFaces);
        for (int index = 0; index < numFaces; index++) {
            int index1 = indices1[index];
            int index2 = indices2[index];
            int index3 = indices3[index];

            int xPoints[] = new int[3];
            int yPoints[] = new int[3];

            xPoints[0] = points[index1][0];
            yPoints[0] = points[index1][1];
            xPoints[1] = points[index2][0];
            yPoints[1] = points[index2][1];
            xPoints[2] = points[index3][0];
            yPoints[2] = points[index3][1];

            if (points[index1][2] + points[index2][2] + points[index3][2] == 3) {
                polys.add(new Polygon(xPoints, yPoints, 3));
            }
        }
        return polys.toArray(new Polygon[polys.size()]);
    }

    /**
     * Moves the mouse onto the RSModel.
     */
    public void hover() {
        methods.mouse.move(getPoint());
    }

    /**
     * Returns true if the provided object is an RSModel with the same x, y and
     * z points as this model. This method compares all of the values in the
     * three vertex arrays.
     *
     * @return <tt>true</tt> if the provided object is a model with the same
     *         points as this.
     */
    @Override
    public boolean equals(final Object o) {
        if (o instanceof RSModel) {
            final RSModel m = (RSModel) o;
            return Arrays.equals(indices1, m.indices1)
                    && Arrays.equals(xPoints, m.xPoints)
                    && Arrays.equals(yPoints, m.yPoints)
                    && Arrays.equals(zPoints, m.zPoints);
        }
        return false;
    }

    private Point getPointInRange(final int start, final int end) {
        final int locX = getLocalX();
        final int locY = getLocalY();
        final int height = methods.calc.tileHeight(locX, locY);
        for (int i = start; i < end; ++i) {
            final Point one = methods.calc.worldToScreen(locX + xPoints[indices1[i]],
                    locY + zPoints[indices1[i]], height + yPoints[indices1[i]]);
            int x = -1, y = -1;
            if (one.x >= 0) {
                x = one.x;
                y = one.y;
            }
            final Point two = methods.calc.worldToScreen(locX + xPoints[indices2[i]],
                    locY + zPoints[indices2[i]], height + yPoints[indices2[i]]);
            if (two.x >= 0) {
                if (x >= 0) {
                    x = (x + two.x) / 2;
                    y = (y + two.y) / 2;
                } else {
                    x = two.x;
                    y = two.y;
                }
            }
            final Point three = methods.calc.worldToScreen(locX
                    + xPoints[indices3[i]], locY + zPoints[indices3[i]], height
                    + yPoints[indices3[i]]);
            if (three.x >= 0) {
                if (x >= 0) {
                    x = (x + three.x) / 2;
                    y = (y + three.y) / 2;
                } else {
                    x = three.x;
                    y = three.y;
                }
            }
            if (x >= 0) {
                return new Point(x, y);
            }
        }
        return null;
    }

    /**
     * Draws a wireeframe of the model. It is optimized for fast rendering.
     * Scripters should use this method instead of fetching every triangle and rendering them.
     *
     * @param graphics the graphics object to render on.
     */
    public void drawWireFrame(Graphics graphics) {
        int[][] screen = projectVertices();

        // That was it for the projection part
        for (int index = 0; index < numFaces; index++) {
            int index1 = indices1[index];
            int index2 = indices2[index];
            int index3 = indices3[index];

            int point1X = screen[index1][0];
            int point1Y = screen[index1][1];
            int point2X = screen[index2][0];
            int point2Y = screen[index2][1];
            int point3X = screen[index3][0];
            int point3Y = screen[index3][1];

            if (screen[index1][2] + screen[index2][2] + screen[index3][2] == 3) {
                graphics.drawLine(point1X, point1Y, point2X, point2Y);
                graphics.drawLine(point2X, point2Y, point3X, point3Y);
                graphics.drawLine(point3X, point3Y, point1X, point1Y);
            }
        }
    }

    /**
     * This projects all the models vertices to screen space.
     *
     * @return two dimensional array. The data format is
     *         posX = result[vertexIndex][0]
     *         posY = result[vertexIndex][1]
     *         visibleOnScreen = (result[vertexIndex][2] == 1);
     */
    private int[][] projectVertices() {
        Calculations.RenderData renderData = methods.calc.renderData;
        Calculations.Render render = methods.calc.render;

        update();

        final int locX = getLocalX();
        final int locY = getLocalY();

        int[][] screen = new int[numVertices][3];

        float xOff = renderData.xOff;
        float yOff = renderData.yOff;
        float zOff = renderData.zOff;

        float xX = renderData.xX;
        float xY = renderData.xY;
        float xZ = renderData.xZ;
        float yX = renderData.yX;
        float yY = renderData.yY;
        float yZ = renderData.yZ;
        float zX = renderData.zX;
        float zY = renderData.zY;
        float zZ = renderData.zZ;

        int xFactor = render.xMultiplier;
        int yFactor = render.yMultiplier;

        boolean isFixed = methods.game.isFixed();

        int height = methods.calc.tileHeight(locX, locY);
        for (int index = 0; index < numVertices; index++) {
            int vertexX = xPoints[index] + locX;
            int vertexY = yPoints[index] + height;
            int vertexZ = zPoints[index] + locY;

            final float _z = zOff + (int) (zX * vertexX + zY * vertexY + zZ * vertexZ);
            if (_z >= render.zNear && _z <= render.zFar) {
                final int _x = (int) (xFactor * ((int) xOff + (int) (xX * vertexX + xY
                        * vertexY + xZ * vertexZ)) / _z);
                final int _y = (int) (yFactor * ((int) yOff + (int) (yX * vertexX + yY
                        * vertexY + yZ * vertexZ)) / _z);
                if (_x >= render.absoluteX1 && _x <= render.absoluteX2 && _y >= render.absoluteY1 && _y <=
                        render.absoluteY2) {
                    if (isFixed) {
                        screen[index][0] = (int) (_x - render.absoluteX1) + 4;
                        screen[index][1] = (int) (_y - render.absoluteY1) + 4;
                        screen[index][2] = 1;
                    } else {
                        screen[index][0] = (int) (_x - render.absoluteX1);
                        screen[index][1] = (int) (_y - render.absoluteY1);
                        screen[index][2] = 1;
                    }
                } else {
                    screen[index][0] = -1;
                    screen[index][1] = -1;
                    screen[index][2] = 0;
                }
            } else {
                screen[index][0] = -1;
                screen[index][1] = -1;
                screen[index][2] = 0;
            }
        }
        return screen;
    }
}