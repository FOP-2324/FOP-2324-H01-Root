package h01;

import org.objectweb.asm.*;
import org.sourcegrade.jagr.api.testing.ClassTransformer;


public class MethodReplacerTransformer implements ClassTransformer {

    @Override
    public String getName() {
        return "MethodReplacerTransformer";
    }

    @Override
    public void transform(final ClassReader reader, final ClassWriter writer) {
        reader.accept(
            new ClassVisitor(Opcodes.ASM9, writer) {
                @Override
                public MethodVisitor visitMethod(
                    final int access,
                    final String name,
                    final String descriptor,
                    final String signature,
                    final String[] exceptions
                ) {
                    return new MethodVisitor(Opcodes.ASM9, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                        @Override
                        public void visitMethodInsn(
                            final int opcode,
                            final String owner,
                            final String name,
                            final String descriptor,
                            final boolean isInterface
                        ) {
                            if (owner.equals("java/lang/Math") && name.equals("random")) {
                                super.visitMethodInsn(
                                    opcode,
                                    "h01/MethodReplacements",
                                    "random",
                                    descriptor,
                                    isInterface
                                );
                                return;
                            }
                            if (owner.equals("java/lang/Thread") && name.equals("sleep")) {
                                super.visitMethodInsn(
                                    opcode,
                                    "h01/MethodReplacements",
                                    "sleep",
                                    descriptor,
                                    isInterface
                                );
                                return;
                            }
                            if (owner.equals("javax/swing/JOptionPane") && name.equals("showMessageDialog")) {
                                super.visitMethodInsn(
                                    opcode,
                                    "h01/MethodReplacements",
                                    "sleep",
                                    descriptor,
                                    isInterface
                                );
                                return;
                            }
                            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);

                        }
                    };
                }
            },
            ClassReader.SKIP_DEBUG
        );
    }
}
