package de.unisb.cs.st.javaslicer.traceResult;

public class StaticFieldAccess {
    private final String clazz;
    private final String field;
    private final boolean read;
    private final long thread;
    public StaticFieldAccess(String clazz, String field, long thread, boolean read) {
        this.clazz = clazz;
        this.field = field;
        this.thread = thread;
        this.read = read;
    }
    public long getThread() {
        return thread;
    }
    public String getClazz() {
        return clazz;
    }
    public String getField() {
        return field;
    }
    public boolean isRead() {
        return read;
    }
    @Override
    public String toString() {
        return "StaticFieldAccess [clazz=" + clazz + ", field=" + field + ", read=" + read + ", thread=" + thread + "]";
    }
    
}
