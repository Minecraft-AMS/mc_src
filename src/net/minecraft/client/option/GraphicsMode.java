/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public final class GraphicsMode
extends Enum<GraphicsMode> {
    public static final /* enum */ GraphicsMode FAST = new GraphicsMode(0, "options.graphics.fast");
    public static final /* enum */ GraphicsMode FANCY = new GraphicsMode(1, "options.graphics.fancy");
    public static final /* enum */ GraphicsMode FABULOUS = new GraphicsMode(2, "options.graphics.fabulous");
    private static final GraphicsMode[] VALUES;
    private final int id;
    private final String translationKey;
    private static final /* synthetic */ GraphicsMode[] field_25433;

    public static GraphicsMode[] values() {
        return (GraphicsMode[])field_25433.clone();
    }

    public static GraphicsMode valueOf(String string) {
        return Enum.valueOf(GraphicsMode.class, string);
    }

    private GraphicsMode(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    public int getId() {
        return this.id;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public String toString() {
        switch (this) {
            case FAST: {
                return "fast";
            }
            case FANCY: {
                return "fancy";
            }
            case FABULOUS: {
                return "fabulous";
            }
        }
        throw new IllegalArgumentException();
    }

    public static GraphicsMode byId(int id) {
        return VALUES[MathHelper.floorMod(id, VALUES.length)];
    }

    private static /* synthetic */ GraphicsMode[] method_36861() {
        return new GraphicsMode[]{FAST, FANCY, FABULOUS};
    }

    static {
        field_25433 = GraphicsMode.method_36861();
        VALUES = (GraphicsMode[])Arrays.stream(GraphicsMode.values()).sorted(Comparator.comparingInt(GraphicsMode::getId)).toArray(GraphicsMode[]::new);
    }
}

