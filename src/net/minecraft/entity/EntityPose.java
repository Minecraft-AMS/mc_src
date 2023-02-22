/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

public final class EntityPose
extends Enum<EntityPose> {
    public static final /* enum */ EntityPose STANDING = new EntityPose();
    public static final /* enum */ EntityPose FALL_FLYING = new EntityPose();
    public static final /* enum */ EntityPose SLEEPING = new EntityPose();
    public static final /* enum */ EntityPose SWIMMING = new EntityPose();
    public static final /* enum */ EntityPose SPIN_ATTACK = new EntityPose();
    public static final /* enum */ EntityPose CROUCHING = new EntityPose();
    public static final /* enum */ EntityPose LONG_JUMPING = new EntityPose();
    public static final /* enum */ EntityPose DYING = new EntityPose();
    private static final /* synthetic */ EntityPose[] field_18083;

    public static EntityPose[] values() {
        return (EntityPose[])field_18083.clone();
    }

    public static EntityPose valueOf(String string) {
        return Enum.valueOf(EntityPose.class, string);
    }

    private static /* synthetic */ EntityPose[] method_36612() {
        return new EntityPose[]{STANDING, FALL_FLYING, SLEEPING, SWIMMING, SPIN_ATTACK, CROUCHING, LONG_JUMPING, DYING};
    }

    static {
        field_18083 = EntityPose.method_36612();
    }
}

