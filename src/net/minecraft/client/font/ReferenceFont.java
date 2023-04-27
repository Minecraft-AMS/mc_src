/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.google.gson.JsonObject;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.FontLoader;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ReferenceFont
implements FontLoader {
    public static final Codec<ReferenceFont> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Identifier.CODEC.fieldOf("id").forGetter(referenceFont -> referenceFont.referee)).apply((Applicative)instance, ReferenceFont::new));
    private final Identifier referee;

    private ReferenceFont(Identifier referee) {
        this.referee = referee;
    }

    public static FontLoader fromJson(JsonObject json) {
        return (FontLoader)CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)json).getOrThrow(false, string -> {});
    }

    @Override
    public Either<FontLoader.Loadable, FontLoader.Reference> build() {
        return Either.right((Object)new FontLoader.Reference(this.referee));
    }
}

