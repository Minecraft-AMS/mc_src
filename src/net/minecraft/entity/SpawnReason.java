/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

public final class SpawnReason
extends Enum<SpawnReason> {
    public static final /* enum */ SpawnReason NATURAL = new SpawnReason();
    public static final /* enum */ SpawnReason CHUNK_GENERATION = new SpawnReason();
    public static final /* enum */ SpawnReason SPAWNER = new SpawnReason();
    public static final /* enum */ SpawnReason STRUCTURE = new SpawnReason();
    public static final /* enum */ SpawnReason BREEDING = new SpawnReason();
    public static final /* enum */ SpawnReason MOB_SUMMONED = new SpawnReason();
    public static final /* enum */ SpawnReason JOCKEY = new SpawnReason();
    public static final /* enum */ SpawnReason EVENT = new SpawnReason();
    public static final /* enum */ SpawnReason CONVERSION = new SpawnReason();
    public static final /* enum */ SpawnReason REINFORCEMENT = new SpawnReason();
    public static final /* enum */ SpawnReason TRIGGERED = new SpawnReason();
    public static final /* enum */ SpawnReason BUCKET = new SpawnReason();
    public static final /* enum */ SpawnReason SPAWN_EGG = new SpawnReason();
    public static final /* enum */ SpawnReason COMMAND = new SpawnReason();
    public static final /* enum */ SpawnReason DISPENSER = new SpawnReason();
    public static final /* enum */ SpawnReason PATROL = new SpawnReason();
    private static final /* synthetic */ SpawnReason[] field_16464;

    public static SpawnReason[] values() {
        return (SpawnReason[])field_16464.clone();
    }

    public static SpawnReason valueOf(String string) {
        return Enum.valueOf(SpawnReason.class, string);
    }

    private static /* synthetic */ SpawnReason[] method_36610() {
        return new SpawnReason[]{NATURAL, CHUNK_GENERATION, SPAWNER, STRUCTURE, BREEDING, MOB_SUMMONED, JOCKEY, EVENT, CONVERSION, REINFORCEMENT, TRIGGERED, BUCKET, SPAWN_EGG, COMMAND, DISPENSER, PATROL};
    }

    static {
        field_16464 = SpawnReason.method_36610();
    }
}

