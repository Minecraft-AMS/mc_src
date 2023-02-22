/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import java.util.function.IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

@Environment(value=EnvType.CLIENT)
public final class GraphicsMode
extends Enum<GraphicsMode>
implements TranslatableOption {
    public static final /* enum */ GraphicsMode FAST = new GraphicsMode(0, "options.graphics.fast");
    public static final /* enum */ GraphicsMode FANCY = new GraphicsMode(1, "options.graphics.fancy");
    public static final /* enum */ GraphicsMode FABULOUS = new GraphicsMode(2, "options.graphics.fabulous");
    private static final IntFunction<GraphicsMode> BY_ID;
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

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }

    public String toString() {
        return switch (this) {
            default -> throw new IncompatibleClassChangeError();
            case FAST -> "fast";
            case FANCY -> "fancy";
            case FABULOUS -> "fabulous";
        };
    }

    public static GraphicsMode byId(int id) {
        return BY_ID.apply(id);
    }

    private static /* synthetic */ GraphicsMode[] method_36861() {
        return new GraphicsMode[]{FAST, FANCY, FABULOUS};
    }

    static {
        field_25433 = GraphicsMode.method_36861();
        BY_ID = ValueLists.createIdToValueFunction(GraphicsMode::getId, GraphicsMode.values(), ValueLists.OutOfBoundsHandling.WRAP);
    }
}

