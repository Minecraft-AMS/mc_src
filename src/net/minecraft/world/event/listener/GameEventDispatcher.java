/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.event.listener;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

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
        public void dispatch(GameEvent event, @Nullable Entity entity, BlockPos pos) {
        }
    };

    public boolean isEmpty();

    public void addListener(GameEventListener var1);

    public void removeListener(GameEventListener var1);

    public void dispatch(GameEvent var1, @Nullable Entity var2, BlockPos var3);
}

