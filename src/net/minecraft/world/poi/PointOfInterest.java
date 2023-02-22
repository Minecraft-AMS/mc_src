/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.poi;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Objects;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.poi.PointOfInterestType;

public class PointOfInterest
implements DynamicSerializable {
    private final BlockPos pos;
    private final PointOfInterestType type;
    private int freeTickets;
    private final Runnable updateListener;

    private PointOfInterest(BlockPos pos, PointOfInterestType type, int freeTickets, Runnable updateListener) {
        this.pos = pos.toImmutable();
        this.type = type;
        this.freeTickets = freeTickets;
        this.updateListener = updateListener;
    }

    public PointOfInterest(BlockPos pos, PointOfInterestType type, Runnable updateListener) {
        this(pos, type, type.getTicketCount(), updateListener);
    }

    public <T> PointOfInterest(Dynamic<T> dynamic, Runnable updateListener) {
        this(dynamic.get("pos").map(BlockPos::deserialize).orElse(new BlockPos(0, 0, 0)), Registry.POINT_OF_INTEREST_TYPE.get(new Identifier(dynamic.get("type").asString(""))), dynamic.get("free_tickets").asInt(0), updateListener);
    }

    @Override
    public <T> T serialize(DynamicOps<T> ops) {
        return (T)ops.createMap((Map)ImmutableMap.of((Object)ops.createString("pos"), this.pos.serialize(ops), (Object)ops.createString("type"), (Object)ops.createString(Registry.POINT_OF_INTEREST_TYPE.getId(this.type).toString()), (Object)ops.createString("free_tickets"), (Object)ops.createInt(this.freeTickets)));
    }

    protected boolean reserveTicket() {
        if (this.freeTickets <= 0) {
            return false;
        }
        --this.freeTickets;
        this.updateListener.run();
        return true;
    }

    protected boolean releaseTicket() {
        if (this.freeTickets >= this.type.getTicketCount()) {
            return false;
        }
        ++this.freeTickets;
        this.updateListener.run();
        return true;
    }

    public boolean hasSpace() {
        return this.freeTickets > 0;
    }

    public boolean isOccupied() {
        return this.freeTickets != this.type.getTicketCount();
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public PointOfInterestType getType() {
        return this.type;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        return Objects.equals(this.pos, ((PointOfInterest)obj).pos);
    }

    public int hashCode() {
        return this.pos.hashCode();
    }
}

