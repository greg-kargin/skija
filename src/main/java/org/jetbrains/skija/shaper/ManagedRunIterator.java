package org.jetbrains.skija.shaper;

import java.util.*;
import org.jetbrains.annotations.*;
import org.jetbrains.skija.*;
import org.jetbrains.skija.impl.*;

public abstract class ManagedRunIterator<T> extends Managed implements Iterator<T> {
    static { Library.load(); }

    @ApiStatus.Internal
    public final ManagedString _text;

    @ApiStatus.Internal
    public ManagedRunIterator(long ptr, ManagedString text) {
        super(ptr, _FinalizerHolder.PTR);
        _text = text;
    }

    @ApiStatus.Internal
    public int _getEndOfCurrentRun() {
        return _nGetEndOfCurrentRun(_ptr, Native.getPtr(_text));
    }

    @Override
    public boolean hasNext() {
        return !_nIsAtEnd(_ptr);
    }

    @ApiStatus.Internal
    public static class _FinalizerHolder {
        static { Stats.onNativeCall(); }
        public static final long PTR = _nGetFinalizer();
    }

    @ApiStatus.Internal public static native long _nGetFinalizer();
    @ApiStatus.Internal public static native void _nConsume(long ptr);
    @ApiStatus.Internal public static native int  _nGetEndOfCurrentRun(long ptr, long textPtr);
    @ApiStatus.Internal public static native boolean _nIsAtEnd(long ptr);
}