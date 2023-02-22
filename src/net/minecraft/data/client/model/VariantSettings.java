/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonPrimitive
 */
package net.minecraft.data.client.model;

import com.google.gson.JsonPrimitive;
import net.minecraft.data.client.model.VariantSetting;
import net.minecraft.util.Identifier;

public class VariantSettings {
    public static final VariantSetting<Rotation> X = new VariantSetting<Rotation>("x", rotation -> new JsonPrimitive((Number)rotation.degrees));
    public static final VariantSetting<Rotation> Y = new VariantSetting<Rotation>("y", rotation -> new JsonPrimitive((Number)rotation.degrees));
    public static final VariantSetting<Identifier> MODEL = new VariantSetting<Identifier>("model", identifier -> new JsonPrimitive(identifier.toString()));
    public static final VariantSetting<Boolean> UVLOCK = new VariantSetting<Boolean>("uvlock", JsonPrimitive::new);
    public static final VariantSetting<Integer> WEIGHT = new VariantSetting<Integer>("weight", JsonPrimitive::new);

    public static final class Rotation
    extends Enum<Rotation> {
        public static final /* enum */ Rotation R0 = new Rotation(0);
        public static final /* enum */ Rotation R90 = new Rotation(90);
        public static final /* enum */ Rotation R180 = new Rotation(180);
        public static final /* enum */ Rotation R270 = new Rotation(270);
        final int degrees;
        private static final /* synthetic */ Rotation[] field_22895;

        public static Rotation[] values() {
            return (Rotation[])field_22895.clone();
        }

        public static Rotation valueOf(String string) {
            return Enum.valueOf(Rotation.class, string);
        }

        private Rotation(int degrees) {
            this.degrees = degrees;
        }

        private static /* synthetic */ Rotation[] method_36941() {
            return new Rotation[]{R0, R90, R180, R270};
        }

        static {
            field_22895 = Rotation.method_36941();
        }
    }
}

