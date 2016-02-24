package de.unisb.cs.st.javaslicer.tracer.instrumentation;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class LazyTraceTransformer implements Opcodes {

    public void transform(ClassNode cn) {
        if (cn.name.equals("java/lang/ArrayIndexOutOfBoundsException") || cn.name.equals("java/lang/NullPointerException") || cn.name.equals("java/lang/ArithmeticException")) {
            for (Object o : cn.methods) {
                MethodNode mn = (MethodNode) o;
                if (mn.name.equals("<init>")) {
                    mn.instructions.insert(new MethodInsnNode(INVOKESTATIC, "de/unisb/cs/st/javaslicer/tracer/LazyTraceLocator", "trace1Deep", "()V", false));
                }
            }
        }
        //classloader
        
        //clinit
        for (Object o : cn.methods) {
            MethodNode mn = (MethodNode) o;
            if (mn.name.equals("<clinit>")) {
                mn.instructions.insert(new MethodInsnNode(INVOKESTATIC, "de/unisb/cs/st/javaslicer/tracer/LazyTraceLocator", "trace1Deep", "()V", false));
            }
        }
    }

}
