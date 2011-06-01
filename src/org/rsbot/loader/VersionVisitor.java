package org.rsbot.loader;

import org.rsbot.loader.asm.*;

/**
 */
public class VersionVisitor implements ClassVisitor {

	private int version;

	public int getVersion() {
		return version;
	}

	@Override
	public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {

	}

	@Override
	public void visitSource(final String source, final String debug) {

	}

	@Override
	public void visitOuterClass(final String owner, final String name, final String desc) {

	}

	@Override
	public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
		return null;
	}

	@Override
	public void visitAttribute(final Attribute attr) {

	}

	@Override
	public void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {

	}

	@Override
	public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
		return null;
	}

	@Override
	public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
		if (!name.equals("main")) {
			return null;
		}
		return new MethodVisitor() {

			@Override
			public AnnotationVisitor visitAnnotationDefault() {
				return null;
			}

			@Override
			public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
				return null;
			}

			@Override
			public AnnotationVisitor visitParameterAnnotation(final int parameter, final String desc, final boolean visible) {

				return null;
			}

			@Override
			public void visitAttribute(final Attribute attr) {

			}

			@Override
			public void visitCode() {

			}

			@Override
			public void visitFrame(final int type, final int nLocal, final Object[] local, final int nStack, final Object[] stack) {

			}

			@Override
			public void visitInsn(final int opcode) {

			}

			@Override
			public void visitIntInsn(final int opcode, final int operand) {
				if (opcode == Opcodes.SIPUSH && operand > 400 && operand < 768) {
					version = operand;
				}
			}

			@Override
			public void visitVarInsn(final int opcode, final int var) {

			}

			@Override
			public void visitTypeInsn(final int opcode, final String type) {

			}

			@Override
			public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {

			}

			@Override
			public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {

			}

			@Override
			public void visitJumpInsn(final int opcode, final Label label) {

			}

			@Override
			public void visitLabel(final Label label) {

			}

			@Override
			public void visitLdcInsn(final Object cst) {

			}

			@Override
			public void visitIincInsn(final int var, final int increment) {

			}

			@Override
			public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label[] labels) {

			}

			@Override
			public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {

			}

			@Override
			public void visitMultiANewArrayInsn(final String desc, final int dims) {

			}

			@Override
			public void visitTryCatchBlock(final Label start, final Label end, final Label handler, final String type) {

			}

			@Override
			public void visitLocalVariable(final String name, final String desc, final String signature, final Label start, final Label end, final int index) {

			}

			@Override
			public void visitLineNumber(final int line, final Label start) {

			}

			@Override
			public void visitMaxs(final int maxStack, final int maxLocals) {

			}

			@Override
			public void visitEnd() {

			}
		};
	}

	@Override
	public void visitEnd() {

	}

}