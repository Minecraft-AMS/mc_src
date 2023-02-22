/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 */
package net.minecraft.world.event.listener;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventDispatcher;
import net.minecraft.world.event.listener.GameEventListener;

public class SimpleGameEventDispatcher
implements GameEventDispatcher {
    private final List<GameEventListener> listeners = Lists.newArrayList();
    private final Set<GameEventListener> toRemove = Sets.newHashSet();
    private final List<GameEventListener> toAdd = Lists.newArrayList();
    private boolean dispatching;
    private final ServerWorld world;

    public SimpleGameEventDispatcher(ServerWorld world) {
        this.world = world;
    }

    @Override
    public boolean isEmpty() {
        return this.listeners.isEmpty();
    }

    @Override
    public void addListener(GameEventListener listener) {
        if (this.dispatching) {
            this.toAdd.add(listener);
        } else {
            this.listeners.add(listener);
        }
        DebugInfoSender.sendGameEventListener(this.world, listener);
    }

    @Override
    public void removeListener(GameEventListener listener) {
        if (this.dispatching) {
            this.toRemove.add(listener);
        } else {
            this.listeners.remove(listener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean dispatch(GameEvent event, Vec3d pos, GameEvent.Emitter emitter, BiConsumer<GameEventListener, Vec3d> onListenerAccept) {
        this.dispatching = true;
        boolean bl = false;
        try {
            Iterator<GameEventListener> iterator = this.listeners.iterator();
            while (iterator.hasNext()) {
                GameEventListener gameEventListener = iterator.next();
                if (this.toRemove.remove(gameEventListener)) {
                    iterator.remove();
                    continue;
                }
                Optional<Vec3d> optional = SimpleGameEventDispatcher.dispatchTo(this.world, pos, gameEventListener);
                if (!optional.isPresent()) continue;
                onListenerAccept.accept(gameEventListener, optional.get());
                bl = true;
            }
        }
        finally {
            this.dispatching = false;
        }
        if (!this.toAdd.isEmpty()) {
            this.listeners.addAll(this.toAdd);
            this.toAdd.clear();
        }
        if (!this.toRemove.isEmpty()) {
            this.listeners.removeAll(this.toRemove);
            this.toRemove.clear();
        }
        return bl;
    }

    private static Optional<Vec3d> dispatchTo(ServerWorld world, Vec3d listenerPos, GameEventListener listener) {
        int i;
        Optional<Vec3d> optional = listener.getPositionSource().getPos(world);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        double d = optional.get().squaredDistanceTo(listenerPos);
        if (d > (double)(i = listener.getRange() * listener.getRange())) {
            return Optional.empty();
        }
        return optional;
    }
}

