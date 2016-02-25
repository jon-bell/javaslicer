package de.unisb.cs.st.javaslicer.tracer.mt;

import java.io.DataOutputStream;
import java.io.IOException;

import de.hammacher.util.StringCacheOutput;
import de.unisb.cs.st.javaslicer.tracer.ThreadTracer;
import de.unisb.cs.st.javaslicer.tracer.Tracer;
import de.unisb.cs.st.javaslicer.tracer.TracingThreadTracer;
import de.unisb.cs.st.javaslicer.tracer.mt.LinkedList.Node;
import de.unisb.cs.st.javaslicer.tracer.traceSequences.IdentifiableSharedObject;

public final class ObjectAccessLog {

    private final LinkedLongList accessLog = new LinkedLongList();
    private final LinkedLongList accessingThreadIds = new LinkedLongList();
    private LinkedList<SFAccess> accessedSFs;
    private LinkedLongList accessedSFThreadIds;

    public static int engaged = 0;

    private static native ObjectAccessLog _getTag(Object obj);

    private static native void _setTag(Object obj, ObjectAccessLog t);

    //    private static final ObjectAccessLog globalLock = new ObjectAccessLog(new Null);

    private static synchronized ObjectAccessLog getOrInitNative(Object obj) {
        if (engaged == 0)
            throw new IllegalStateException("Slicer JVMTI Agent not loaded");
        ObjectAccessLog ret = _getTag(obj);
        if (ret != null)
            return ret;
        ret = new ObjectAccessLog(Tracer.getInstance().getThreadTracer());
        _setTag(obj, ret);
        return ret;
    }

    public boolean conflict() {
        if (accessedSFThreadIds != null)
            return accessedSFThreadIds.getFirst() != null && accessedSFThreadIds.getFirst().next != null;
        return accessingThreadIds.getFirst() != null && accessingThreadIds.getFirst().next != null;
    }

    public ObjectAccessLog(ThreadTracer tr) {
        tr.pauseTracing();
        Tracer.getInstance().addObjectAccessLog(this);
        tr.resumeTracing();
    }

    public void read(ThreadTracer tr, int objSeq) {
        if (tr == null || tr.isPaused())
            return;
        if (tr instanceof TracingThreadTracer) {
            long id = ((TracingThreadTracer) tr).getThreadId();
            accessLog.add(id);
            accessingThreadIds.addUnique(id);
        }
    }

    public void write(ThreadTracer tr, int objSeq) {
        if (tr == null || tr.isPaused())
            return;
        if (tr instanceof TracingThreadTracer) {
            long id = ((TracingThreadTracer) tr).getThreadId();
            accessLog.add(id);
            accessingThreadIds.addUnique(id);
        }
    }

    public void read(ThreadTracer tr, String clazz, String field) {
        if (tr == null || tr.isPaused())
            return;
        if (tr instanceof TracingThreadTracer) {
            long id = ((TracingThreadTracer) tr).getThreadId();
            if (accessedSFs == null) {
                accessedSFs = new LinkedList<SFAccess>();
                accessedSFThreadIds = new LinkedLongList();
            }
            accessedSFs.add(new SFAccess(clazz, field, id, true));
            accessedSFThreadIds.addUnique(id);
        }
    }

    public void write(ThreadTracer tr, String clazz, String field) {
        if (tr == null || tr.isPaused())
            return;
        if (tr instanceof TracingThreadTracer) {
            long id = ((TracingThreadTracer) tr).getThreadId();
            if (accessedSFs == null) {
                accessedSFs = new LinkedList<SFAccess>();
                accessedSFThreadIds = new LinkedLongList();
            }
            accessedSFs.add(new SFAccess(clazz, field, id, false));
            accessedSFThreadIds.addUnique(id);
        }
    }

    public static ObjectAccessLog getAccessLog(Object obj) {
        if (obj instanceof IdentifiableSharedObject)
            return ((IdentifiableSharedObject) obj).$$getJavaSlicerObjectAccessLog();
        return getOrInitNative(obj);
    }

    public static ObjectAccessLog getNewId(Object ref) {
        return new ObjectAccessLog(Tracer.getInstance().getThreadTracer());
    }

    public LinkedList<SFAccess> getAccessedSFs() {
        return accessedSFs;
    }

    private static byte T_SF = 1;
    private static byte T_OBJ = 2;

    public void writeOut(DataOutputStream os, StringCacheOutput stringCache) throws IOException {
        if (accessedSFs == null) {
            //no SF's
            os.writeByte(T_OBJ);
        } else {
            os.writeByte(T_SF);
            os.writeInt(accessedSFs.getSize());
            Node<SFAccess> e = accessedSFs.getFirst();
            while (e != null) {
                stringCache.writeString(e.entry.clazz, os);
                stringCache.writeString(e.entry.field, os);
                os.writeLong(e.entry.thread);
                os.writeByte(e.entry.read ? 1 : 0);
                e = e.next;
            }
        }
    }
}
