/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.mojang.datafixers.util.Either;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public interface FontLoader {
    public Either<Loadable, Reference> build();

    @Environment(value=EnvType.CLIENT)
    public record Reference(Identifier id) {
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Loadable {
        public Font load(ResourceManager var1) throws IOException;
    }
}

