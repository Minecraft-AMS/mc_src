/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.util.profiler;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.profiler.DummyProfiler;

public interface Profiler {
    public void startTick();

    public void endTick();

    public void push(String var1);

    public void push(Supplier<String> var1);

    public void pop();

    public void swap(String var1);

    @Environment(value=EnvType.CLIENT)
    public void swap(Supplier<String> var1);

    public void visit(String var1);

    public void visit(Supplier<String> var1);

    public static Profiler union(final Profiler profiler, final Profiler profiler2) {
        if (profiler == DummyProfiler.INSTANCE) {
            return profiler2;
        }
        if (profiler2 == DummyProfiler.INSTANCE) {
            return profiler;
        }
        return new Profiler(){

            @Override
            public void startTick() {
                profiler.startTick();
                profiler2.startTick();
            }

            @Override
            public void endTick() {
                profiler.endTick();
                profiler2.endTick();
            }

            @Override
            public void push(String location) {
                profiler.push(location);
                profiler2.push(location);
            }

            @Override
            public void push(Supplier<String> locationGetter) {
                profiler.push(locationGetter);
                profiler2.push(locationGetter);
            }

            @Override
            public void pop() {
                profiler.pop();
                profiler2.pop();
            }

            @Override
            public void swap(String location) {
                profiler.swap(location);
                profiler2.swap(location);
            }

            @Override
            @Environment(value=EnvType.CLIENT)
            public void swap(Supplier<String> locationGetter) {
                profiler.swap(locationGetter);
                profiler2.swap(locationGetter);
            }

            @Override
            public void visit(String marker) {
                profiler.visit(marker);
                profiler2.visit(marker);
            }

            @Override
            public void visit(Supplier<String> markerGetter) {
                profiler.visit(markerGetter);
                profiler2.visit(markerGetter);
            }
        };
    }
}

