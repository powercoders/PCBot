package org.rsbot.client;

import java.util.Arrays;

/**
 * Implementation of the Model interface used to
 * store model data by injection in transform
 * methods where model implementations in the
 * client are reused.
 *
 */
public class ModelCapture implements Model {

	private int[] vertex_x;
	private int[] vertex_y;
	private int[] vertex_z;

	private short[] face_a;
	private short[] face_b;
	private short[] face_c;

	public ModelCapture(final Model model) {
		if (model == null) {
			return;
		}
        update(model);
	}

    public static Model updateModel(final Model model, final Model container)
    {
        if(container == null || !(container instanceof ModelCapture))
            return new ModelCapture(model);
        ((ModelCapture)container).update(model);
        return container;
    }

    private void update(Model model) {
        if (model == null) {
			return;
		}
        int[] vertices = model.getXPoints();
		vertex_x = Arrays.copyOf(vertices, vertices.length);
		vertices = model.getYPoints();
		vertex_y = Arrays.copyOf(vertices, vertices.length);
		vertices = model.getZPoints();
		vertex_z = Arrays.copyOf(vertices, vertices.length);

		short[] faces = model.getIndices1();
		face_a = Arrays.copyOf(faces, faces.length);
		faces = model.getIndices2();
		face_b = Arrays.copyOf(faces, faces.length);
		faces = model.getIndices3();
		face_c = Arrays.copyOf(faces, faces.length);
    }

    @Override
	public int[] getXPoints() {
		return vertex_x;
	}

	@Override
	public int[] getYPoints() {
		return vertex_y;
	}

	@Override
	public int[] getZPoints() {
		return vertex_z;
	}

	@Override
	public short[] getIndices1() {
		return face_a;
	}

	@Override
	public short[] getIndices2() {
		return face_b;
	}

	@Override
	public short[] getIndices3() {
		return face_c;
	}

}
