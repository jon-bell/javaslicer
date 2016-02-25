package de.unisb.cs.st.javaslicer.tracer.mt;

public class SFAccess {
    @Override
    public String toString() {
        return "SFAccess [clazz=" + clazz + ", field=" + field + ", thread=" + thread + ", read=" + read + "]";
    }
    public final String clazz;
    public final String field;
    public final long thread;
    public final boolean read;
    
    public String getField() {
        return field;
    }
    public String getClazz() {
        return clazz;
    }
    public long getThread() {
        return thread;
    }
    public SFAccess(String clazz, String field, long thread, boolean read) {
        this.clazz = clazz;
        this.field = field;
        this.thread = thread;
        this.read = read;
    }
    
}
