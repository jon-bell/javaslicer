package de.unisb.cs.st.javaslicer.tracer;

public class LazyTraceLocator {
    public static int engaged;

    private static native int _getTraceLocation(int stackDepth);

    private static native long _getLabelLocation(int stackDepth);

    public static int getTraceLocation(int stackDepth) {
        if (engaged != 1)
            throw new IllegalStateException("JVMTI Agent not yet loaded");
        return _getTraceLocation(stackDepth);
    }

    public static long getLabelLocation(int stackDepth) {
        if (engaged != 1)
            throw new IllegalStateException("JVMTI Agent not yet loaded");
        return _getLabelLocation(stackDepth);
    }

    public static void trace1Deep() {
        final Tracer t = Tracer.getInstance();
        final ThreadTracer tt = t.getThreadTracer();
        if (tt.isPaused())
            return;
        int r = getTraceLocation(2);
        if (r >= 0) //If the instruction 2 insns before the thing that called this is NOT an int, then it was probably a NEW and will return -1
            tt.passInstruction(r);
    }

    public static void traceLabel1Deep() {
        final Tracer t = Tracer.getInstance();
        final ThreadTracer tt = t.getThreadTracer();
        if (tt.isPaused())
            return;
        long r = getLabelLocation(2);
        int lblInsn = (int) (r >> 32);
        int seq = (int) r;
        //        System.out.println(lblInsn + "  and " + seq);
        if (lblInsn >= 0) //If the instruction 2 insns before the thing that called this is NOT an int, then it was probably a NEW and will return -1
        {
            tt.traceLastInstructionIndex(seq);
            tt.passInstruction(lblInsn);
        }
    }
}
