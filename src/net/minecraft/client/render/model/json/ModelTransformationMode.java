/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.client.render.model.json;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public final class ModelTransformationMode
extends Enum<ModelTransformationMode>
implements StringIdentifiable {
    public static final /* enum */ ModelTransformationMode NONE = new ModelTransformationMode(0, "none");
    public static final /* enum */ ModelTransformationMode THIRD_PERSON_LEFT_HAND = new ModelTransformationMode(1, "thirdperson_lefthand");
    public static final /* enum */ ModelTransformationMode THIRD_PERSON_RIGHT_HAND = new ModelTransformationMode(2, "thirdperson_righthand");
    public static final /* enum */ ModelTransformationMode FIRST_PERSON_LEFT_HAND = new ModelTransformationMode(3, "firstperson_lefthand");
    public static final /* enum */ ModelTransformationMode FIRST_PERSON_RIGHT_HAND = new ModelTransformationMode(4, "firstperson_righthand");
    public static final /* enum */ ModelTransformationMode HEAD = new ModelTransformationMode(5, "head");
    public static final /* enum */ ModelTransformationMode GUI = new ModelTransformationMode(6, "gui");
    public static final /* enum */ ModelTransformationMode GROUND = new ModelTransformationMode(7, "ground");
    public static final /* enum */ ModelTransformationMode FIXED = new ModelTransformationMode(8, "fixed");
    public static final Codec<ModelTransformationMode> CODEC;
    public static final IntFunction<ModelTransformationMode> FROM_INDEX;
    private final byte index;
    private final String name;
    private static final /* synthetic */ ModelTransformationMode[] field_4314;

    public static ModelTransformationMode[] values() {
        return (ModelTransformationMode[])field_4314.clone();
    }

    public static ModelTransformationMode valueOf(String string) {
        return Enum.valueOf(ModelTransformationMode.class, string);
    }

    private ModelTransformationMode(int index, String name) {
        this.name = name;
        this.index = (byte)index;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public byte getIndex() {
        return this.index;
    }

    public boolean isFirstPerson() {
        return this == FIRST_PERSON_LEFT_HAND || this == FIRST_PERSON_RIGHT_HAND;
    }

    private static /* synthetic */ ModelTransformationMode[] method_36922() {
        return new ModelTransformationMode[]{NONE, THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND, FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND, HEAD, GUI, GROUND, FIXED};
    }

    static {
        field_4314 = ModelTransformationMode.method_36922();
        CODEC = StringIdentifiable.createCodec(ModelTransformationMode::values);
        FROM_INDEX = ValueLists.createIdToValueFunction(ModelTransformationMode::getIndex, ModelTransformationMode.values(), ValueLists.OutOfBoundsHandling.ZERO);
    }
}

