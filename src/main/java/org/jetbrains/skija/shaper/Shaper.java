package org.jetbrains.skija.shaper;

import java.util.*;
import org.jetbrains.annotations.*;
import org.jetbrains.skija.*;
import org.jetbrains.skija.impl.*;

/**
 * Shapes text using HarfBuzz and places the shaped text into a
 * client-managed buffer.
 */
public class Shaper extends Managed {
    static { Library.load(); }
    
    @NotNull @Contract("-> new")
    public static Shaper makePrimitive() {
        Stats.onNativeCall();
        return new Shaper(_nMakePrimitive());
    }

    @NotNull @Contract("-> new")
    public static Shaper makeShaperDrivenWrapper() {
        return makeShaperDrivenWrapper(null);
    }

    @NotNull @Contract("_ -> new")
    public static Shaper makeShaperDrivenWrapper(@Nullable FontMgr fontMgr) {
        Stats.onNativeCall();
        return new Shaper(_nMakeShaperDrivenWrapper(Native.getPtr(fontMgr)));
    }

    @NotNull @Contract("-> new")
    public static Shaper makeShapeThenWrap() {
        return makeShapeThenWrap(null);
    }

    @NotNull @Contract("_ -> new")
    public static Shaper makeShapeThenWrap(@Nullable FontMgr fontMgr) {
        Stats.onNativeCall();
        return new Shaper(_nMakeShapeThenWrap(Native.getPtr(fontMgr)));
    }

    @NotNull @Contract("-> new")
    public static Shaper makeShapeDontWrapOrReorder() {
        return makeShapeDontWrapOrReorder(null);
    }

    @NotNull @Contract("_ -> new")
    public static Shaper makeShapeDontWrapOrReorder(@Nullable FontMgr fontMgr) {
        Stats.onNativeCall();
        return new Shaper(_nMakeShapeDontWrapOrReorder(Native.getPtr(fontMgr)));
    }

    /**
     * <p>Only works on macOS</p>
     * 
     * <p>WARN broken in m87 https://bugs.chromium.org/p/skia/issues/detail?id=10897</p>
     * 
     * @return  Shaper on macOS, throws UnsupportedOperationException elsewhere
     */
    @NotNull @Contract("-> new")
    public static Shaper makeCoreText() {
        Stats.onNativeCall();
        long ptr = _nMakeCoreText();
        if (ptr == 0)
            throw new UnsupportedOperationException("CoreText not available");
        return new Shaper(ptr);
    }

    @NotNull @Contract("-> new")
    public static Shaper make() {
        return make(null);
    }

    @NotNull @Contract("_ -> new")
    public static Shaper make(@Nullable FontMgr fontMgr) {
        Stats.onNativeCall();
        return new Shaper(_nMake(Native.getPtr(fontMgr)));
    }

    @NotNull @Contract("_, _ -> new")
    public TextBlob shape(String text, Font font) {
        return shape(text, font, null, true, Float.POSITIVE_INFINITY, Point.ZERO);
    }

    @NotNull @Contract("_, _, _ -> new")
    public TextBlob shape(String text, Font font, float width) {
        return shape(text, font, null, true, width, Point.ZERO);
    }

    @NotNull @Contract("_, _, _, _ -> new")
    public TextBlob shape(String text, Font font, float width, @NotNull Point offset) {
        return shape(text, font, null, true, width, offset);
    }

    @NotNull @Contract("_, _, _, _, _ -> new")
    public TextBlob shape(String text, Font font, boolean leftToRight, float width, @NotNull Point offset) {
        return shape(text, font, null, leftToRight, width, offset);
    }

    @NotNull @Contract("_, _, _, _, _, _ -> new")
    public TextBlob shape(String text, Font font, @Nullable FontFeature[] features, boolean leftToRight, float width, @NotNull Point offset) {
        var handler = new TextBlobBuilderRunHandler(text, offset);
        shape(text, font, null, features, leftToRight, width, handler);
        return handler.makeBlob();
    }

    @NotNull @Contract("_, _, _, _, _, _, _ -> this")
    public Shaper shape(String text, Font font, @Nullable FontMgr fontMgr, @Nullable FontFeature[] features, boolean leftToRight, float width, RunHandler runHandler) {
        shape(text,
            new FontMgrRunIterator(text, font, fontMgr),
            new IcuBidiRunIterator(text, leftToRight ? java.text.Bidi.DIRECTION_LEFT_TO_RIGHT : java.text.Bidi.DIRECTION_RIGHT_TO_LEFT),
            new HbIcuScriptRunIterator(text),
            new TrivialLanguageRunIterator(text, Locale.getDefault().toLanguageTag()),
            features,
            width,
            runHandler);

        return this;
    }

    @NotNull @Contract("_, _, _, _, _, _, _ -> this")
    public Shaper shape(String text, @NotNull Iterator<FontRun> fontIter, @NotNull Iterator<BidiRun> bidiIter, @NotNull Iterator<ScriptRun> scriptIter, @NotNull Iterator<LanguageRun> langIter,
                        @Nullable FontFeature[] features, float width, RunHandler runHandler) {
        assert fontIter != null : "FontRunIterator == null";
        assert bidiIter != null : "BidiRunIterator == null";
        assert scriptIter != null : "ScriptRunIterator == null";
        assert langIter != null : "LanguageRunIterator == null";
        Stats.onNativeCall();
        _nShape(_ptr, text, fontIter, bidiIter, scriptIter, langIter, features, width, runHandler);
        return this;
    }

    @ApiStatus.Internal
    public Shaper(long ptr) {
        super(ptr, _FinalizerHolder.PTR);
    }

    @ApiStatus.Internal
    public static class _FinalizerHolder {
        static { Stats.onNativeCall(); }
        public static final long PTR = _nGetFinalizer();
    }

    public static native long _nGetFinalizer();
    public static native long _nMakePrimitive();
    public static native long _nMakeShaperDrivenWrapper(long fontMgrPtr);
    public static native long _nMakeShapeThenWrap(long fontMgrPtr);
    public static native long _nMakeShapeDontWrapOrReorder(long fontMgrPtr);
    public static native long _nMakeCoreText();
    public static native long _nMake(long fontMgrPtr);
    public static native void _nShape(long ptr, String text, Iterator<FontRun> fontIter, Iterator<BidiRun> bidiIter, Iterator<ScriptRun> scriptIter, Iterator<LanguageRun> langIter,
                                      FontFeature[] features, float width, RunHandler runHandler);
}
