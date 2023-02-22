/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DefaultSkinHelper {
    private static final Skin[] SKINS = new Skin[]{new Skin("textures/entity/player/slim/alex.png", Model.SLIM), new Skin("textures/entity/player/slim/ari.png", Model.SLIM), new Skin("textures/entity/player/slim/efe.png", Model.SLIM), new Skin("textures/entity/player/slim/kai.png", Model.SLIM), new Skin("textures/entity/player/slim/makena.png", Model.SLIM), new Skin("textures/entity/player/slim/noor.png", Model.SLIM), new Skin("textures/entity/player/slim/steve.png", Model.SLIM), new Skin("textures/entity/player/slim/sunny.png", Model.SLIM), new Skin("textures/entity/player/slim/zuri.png", Model.SLIM), new Skin("textures/entity/player/wide/alex.png", Model.WIDE), new Skin("textures/entity/player/wide/ari.png", Model.WIDE), new Skin("textures/entity/player/wide/efe.png", Model.WIDE), new Skin("textures/entity/player/wide/kai.png", Model.WIDE), new Skin("textures/entity/player/wide/makena.png", Model.WIDE), new Skin("textures/entity/player/wide/noor.png", Model.WIDE), new Skin("textures/entity/player/wide/steve.png", Model.WIDE), new Skin("textures/entity/player/wide/sunny.png", Model.WIDE), new Skin("textures/entity/player/wide/zuri.png", Model.WIDE)};

    public static Identifier getTexture() {
        return SKINS[6].texture();
    }

    public static Identifier getTexture(UUID uuid) {
        return DefaultSkinHelper.getSkin((UUID)uuid).texture;
    }

    public static String getModel(UUID uuid) {
        return DefaultSkinHelper.getSkin((UUID)uuid).model.name;
    }

    private static Skin getSkin(UUID uuid) {
        return SKINS[Math.floorMod(uuid.hashCode(), SKINS.length)];
    }

    @Environment(value=EnvType.CLIENT)
    static final class Skin
    extends Record {
        final Identifier texture;
        final Model model;

        public Skin(String texture, Model model) {
            this(new Identifier(texture), model);
        }

        private Skin(Identifier identifier, Model model) {
            this.texture = identifier;
            this.model = model;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Skin.class, "texture;model", "texture", "model"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Skin.class, "texture;model", "texture", "model"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Skin.class, "texture;model", "texture", "model"}, this, object);
        }

        public Identifier texture() {
            return this.texture;
        }

        public Model model() {
            return this.model;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class Model
    extends Enum<Model> {
        public static final /* enum */ Model SLIM = new Model("slim");
        public static final /* enum */ Model WIDE = new Model("default");
        final String name;
        private static final /* synthetic */ Model[] field_41125;

        public static Model[] values() {
            return (Model[])field_41125.clone();
        }

        public static Model valueOf(String string) {
            return Enum.valueOf(Model.class, string);
        }

        private Model(String name) {
            this.name = name;
        }

        private static /* synthetic */ Model[] method_47439() {
            return new Model[]{SLIM, WIDE};
        }

        static {
            field_41125 = Model.method_47439();
        }
    }
}

