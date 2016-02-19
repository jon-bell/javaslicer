package de.unisb.cs.st.javaslicer.tracer.instrumentation;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class LazyTraceTransformer implements Opcodes {

    public void transform(ClassNode cn)
    {
        if(cn.name.equals("java/lang/ArrayIndexOutOfBoundsException"))
        {
            for(Object o : cn.methods)
            {
                MethodNode mn = (MethodNode) o;
                if(mn.name.equals("<init>"))
                {
                    System.out.println("Tracing in aioobe");
                    mn.instructions.insert(new MethodInsnNode(INVOKESTATIC, "de/unisb/cs/st/javaslicer/tracer/LazyTraceLocator", "trace1Deep", "()V",false));
                }
            }
        }
    }
}
