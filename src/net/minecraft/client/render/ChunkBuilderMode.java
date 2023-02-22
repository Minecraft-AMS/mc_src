/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public final class ChunkBuilderMode
extends Enum<ChunkBuilderMode>
implements TranslatableOption {
    public static final /* enum */ ChunkBuilderMode NONE = new ChunkBuilderMode(0, "options.prioritizeChunkUpdates.none");
    public static final /* enum */ ChunkBuilderMode PLAYER_AFFECTED = new ChunkBuilderMode(1, "options.prioritizeChunkUpdates.byPlayer");
    public static final /* enum */ ChunkBuilderMode NEARBY = new ChunkBuilderMode(2, "options.prioritizeChunkUpdates.nearby");
    private static final ChunkBuilderMode[] modes;
    private final int id;
    private final String name;
    private static final /* synthetic */ ChunkBuilderMode[] field_34794;

    public static ChunkBuilderMode[] values() {
        return (ChunkBuilderMode[])field_34794.clone();
    }

    public static ChunkBuilderMode valueOf(String string) {
        return Enum.valueOf(ChunkBuilderMode.class, string);
    }

    private ChunkBuilderMode(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getTranslationKey() {
        return this.name;
    }

    public static ChunkBuilderMode get(int id) {
        return modes[MathHelper.floorMod(id, modes.length)];
    }

    private static /* synthetic */ ChunkBuilderMode[] method_38526() {
        return new ChunkBuilderMode[]{NONE, PLAYER_AFFECTED, NEARBY};
    }

    static {
        field_34794 = ChunkBuilderMode.method_38526();
        modes = (ChunkBuilderMode[])Arrays.stream(ChunkBuilderMode.values()).sorted(Comparator.comparingInt(ChunkBuilderMode::getId)).toArray(ChunkBuilderMode[]::new);
    }
}

