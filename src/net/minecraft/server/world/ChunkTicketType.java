/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.world;

import java.util.Comparator;
import net.minecraft.util.Unit;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColumnPos;

public class ChunkTicketType<T> {
    private final String name;
    private final Comparator<T> argumentComparator;
    private final long field_19348;
    public static final ChunkTicketType<Unit> START = ChunkTicketType.create("start", (unit, unit2) -> 0);
    public static final ChunkTicketType<Unit> DRAGON = ChunkTicketType.create("dragon", (unit, unit2) -> 0);
    public static final ChunkTicketType<ChunkPos> PLAYER = ChunkTicketType.create("player", Comparator.comparingLong(ChunkPos::toLong));
    public static final ChunkTicketType<ChunkPos> FORCED = ChunkTicketType.create("forced", Comparator.comparingLong(ChunkPos::toLong));
    public static final ChunkTicketType<ChunkPos> LIGHT = ChunkTicketType.create("light", Comparator.comparingLong(ChunkPos::toLong));
    public static final ChunkTicketType<ColumnPos> PORTAL = ChunkTicketType.create("portal", Comparator.comparingLong(ColumnPos::toLong));
    public static final ChunkTicketType<Integer> POST_TELEPORT = ChunkTicketType.method_20628("post_teleport", Integer::compareTo, 5);
    public static final ChunkTicketType<ChunkPos> UNKNOWN = ChunkTicketType.method_20628("unknown", Comparator.comparingLong(ChunkPos::toLong), 1);

    public static <T> ChunkTicketType<T> create(String name, Comparator<T> comparator) {
        return new ChunkTicketType<T>(name, comparator, 0L);
    }

    public static <T> ChunkTicketType<T> method_20628(String string, Comparator<T> comparator, int i) {
        return new ChunkTicketType<T>(string, comparator, i);
    }

    protected ChunkTicketType(String name, Comparator<T> comparator, long expiryTicks) {
        this.name = name;
        this.argumentComparator = comparator;
        this.field_19348 = expiryTicks;
    }

    public String toString() {
        return this.name;
    }

    public Comparator<T> getArgumentComparator() {
        return this.argumentComparator;
    }

    public long method_20629() {
        return this.field_19348;
    }
}

