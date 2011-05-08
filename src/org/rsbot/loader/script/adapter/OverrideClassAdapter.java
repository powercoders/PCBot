package org.rsbot.loader.script.adapter;

import org.rsbot.loader.asm.AnnotationVisitor;
import org.rsbot.loader.asm.Attribute;
import org.rsbot.loader.asm.ClassAdapter;
import org.rsbot.loader.asm.ClassVisitor;
import org.rsbot.loader.asm.FieldVisitor;
import org.rsbot.loader.asm.Label;
import org.rsbot.loader.asm.MethodVisitor;

/**
 * @author Liang
 */
public class OverrideClassAdapter extends ClassAdapter {

	private final String old_clazz;
	private final String new_clazz;

	public OverrideClassAdapter(final ClassVisitor delegate, final String old_clazz, final String new_clazz) {
		super(delegate);
		this.old_clazz = old_clazz;
		this.new_clazz = new_clazz;
	}

	@Override
	public MethodVisitor visitMethod(
			final int access,
			final String name,
			final String desc,
			final String signature,
			final String[] exceptions) {
		return new MethodAdapter(cv.visitMethod(access, name, desc, signature, exceptions), old_clazz, new_clazz);
	}

	@Override
	public FieldVisitor visitField(final int access, final String name, String desc, final String signature, final Object value) {
		if (desc.equals("L" + old_clazz + ";")) {
			desc = "L" + new_clazz + ";";
		}

		return cv.visitField(access, name, desc, signature, value);
	}

	static class MethodAdapter implements MethodVisitor {

		private final MethodVisitor mv;
		private final String old_clazz;
		private final String new_clazz;

		MethodAdapter(
				final MethodVisitor delegate,
				final String old_clazz,
				final String new_clazz) {
			mv = delegate;
			this.old_clazz = old_clazz;
			this.new_clazz = new_clazz;
		}

		@Override
		public AnnotationVisitor visitAnnotationDefault() {
			return mv.visitAnnotationDefault();
		}

		@Override
		public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
			return mv.visitAnnotation(desc, visible);
		}

		@Override
		public AnnotationVisitor visitParameterAnnotation(final int parameter, final String desc, final boolean visible) {
			return mv.visitParameterAnnotation(parameter, desc, visible);
		}

		@Override
		public void visitAttribute(final Attribute attr) {
			mv.visitAttribute(attr);
		}

		@Override
		public void visitCode() {
			mv.visitCode();
		}

		@Override
		public void visitFrame(final int type, final int nLocal, final Object[] local, final int nStack, final Object[] stack) {

		}

		@Override
		public void visitInsn(final int opcode) {
			mv.visitInsn(opcode);
		}

		@Override
		public void visitIntInsn(final int opcode, final int operand) {
			mv.visitIntInsn(opcode, operand);
		}

		@Override
		public void visitVarInsn(final int opcode, final int var) {
			mv.visitVarInsn(opcode, var);
		}

		@Override
		public void visitTypeInsn(final int opcode, String type) {
			if (type.equals(old_clazz)) {
				type = new_clazz;
			}

			mv.visitTypeInsn(opcode, type);
		}

		@Override
		public void visitFieldInsn(final int opcode, final String owner, final String name, String desc) {
			if (desc.contains(old_clazz)) {
				desc = desc.replace("L" + old_clazz + ";", "L" + new_clazz + ";");
			}

			mv.visitFieldInsn(opcode, owner, name, desc);
		}

		@Override
		public void visitMethodInsn(final int opcode, String owner, final String name, String desc) {
			if (owner.equals(old_clazz)) {
				owner = new_clazz;
				desc = desc.replace("L" + old_clazz + ";", "L" + new_clazz + ";");
			}
			mv.visitMethodInsn(opcode, owner, name, desc);
		}

		@Override
		public void visitJumpInsn(final int opcode, final Label label) {
			mv.visitJumpInsn(opcode, label);
		}

		@Override
		public void visitLabel(final Label label) {
			mv.visitLabel(label);
		}

		@Override
		public void visitLdcInsn(final Object cst) {
			mv.visitLdcInsn(cst);
		}

		@Override
		public void visitIincInsn(final int var, final int increment) {
			mv.visitIincInsn(var, increment);
		}

		@Override
		public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label[] labels) {
			mv.visitTableSwitchInsn(min, max, dflt, labels);
		}

		@Override
		public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
			mv.visitLookupSwitchInsn(dflt, keys, labels);
		}

		@Override
		public void visitMultiANewArrayInsn(final String desc, final int dims) {
			mv.visitMultiANewArrayInsn(desc, dims);
		}

		@Override
		public void visitTryCatchBlock(final Label start, final Label end, final Label handler, final String type) {
			mv.visitTryCatchBlock(start, end, handler, type);
		}

		@Override
		public void visitLocalVariable(final String name, final String desc, final String signature, final Label start, final Label end, final int index) {
			mv.visitLocalVariable(name, desc, signature, start, end, index);
		}

		@Override
		public void visitLineNumber(final int line, final Label start) {

		}

		@Override
		public void visitMaxs(final int maxStack, final int maxLocals) {
			mv.visitMaxs(maxStack, maxLocals);
		}

		@Override
		public void visitEnd() {
			mv.visitEnd();
		}

	}

}
