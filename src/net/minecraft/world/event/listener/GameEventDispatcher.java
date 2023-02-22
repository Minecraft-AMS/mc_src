/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.event.listener;

import java.util.function.BiConsumer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;

public interface GameEventDispatcher {
    public static final GameEventDispatcher EMPTY = new GameEventDispatcher(){

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public void addListener(GameEventListener listener) {
        }

        @Override
        public void removeListener(GameEventListener listener) {
        }

        @Override
        public boolean dispatch(GameEvent event, Vec3d pos, GameEvent.Emitter emitter, BiConsumer<GameEventListener, Vec3d> onListenerAccept) {
            return false;
        }
    };

    public boolean isEmpty();

    public void addListener(GameEventListener var1);

    public void removeListener(GameEventListener var1);

    public boolean dispatch(GameEvent var1, Vec3d var2, GameEvent.Emitter var3, BiConsumer<GameEventListener, Vec3d> var4);
}

