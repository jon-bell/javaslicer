package de.unisb.cs.st.javaslicer.tracer;

public class LazyTraceLocator { 
    public static int engaged;
    private static native int _getTraceLocation(int stackDepth);
    public static int getTraceLocation(int stackDepth)
    {
        if(engaged != 1)
            throw new IllegalStateException("JVMTI Agent not yet loaded");
        return _getTraceLocation(stackDepth);
    }

    public static void trace1Deep()
    {
        final Tracer t = Tracer.getInstance();
        final ThreadTracer tt = t.getThreadTracer();
        if(tt.isPaused())
            return;
//        new Exception().printStackTrace();
//        if(t.debug)
            System.out.println("*DEBUG* Force pass instruction" + getTraceLocation(2));
        tt.passInstruction(getTraceLocation(2));
    }
}
