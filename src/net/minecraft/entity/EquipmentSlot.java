/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

public final class EquipmentSlot
extends Enum<EquipmentSlot> {
    public static final /* enum */ EquipmentSlot MAINHAND = new EquipmentSlot(Type.HAND, 0, 0, "mainhand");
    public static final /* enum */ EquipmentSlot OFFHAND = new EquipmentSlot(Type.HAND, 1, 5, "offhand");
    public static final /* enum */ EquipmentSlot FEET = new EquipmentSlot(Type.ARMOR, 0, 1, "feet");
    public static final /* enum */ EquipmentSlot LEGS = new EquipmentSlot(Type.ARMOR, 1, 2, "legs");
    public static final /* enum */ EquipmentSlot CHEST = new EquipmentSlot(Type.ARMOR, 2, 3, "chest");
    public static final /* enum */ EquipmentSlot HEAD = new EquipmentSlot(Type.ARMOR, 3, 4, "head");
    private final Type type;
    private final int entityId;
    private final int armorStandId;
    private final String name;
    private static final /* synthetic */ EquipmentSlot[] field_6176;

    public static EquipmentSlot[] values() {
        return (EquipmentSlot[])field_6176.clone();
    }

    public static EquipmentSlot valueOf(String string) {
        return Enum.valueOf(EquipmentSlot.class, string);
    }

    private EquipmentSlot(Type type, int entityId, int armorStandId, String name) {
        this.type = type;
        this.entityId = entityId;
        this.armorStandId = armorStandId;
        this.name = name;
    }

    public Type getType() {
        return this.type;
    }

    public int getEntitySlotId() {
        return this.entityId;
    }

    public int getOffsetEntitySlotId(int offset) {
        return offset + this.entityId;
    }

    public int getArmorStandSlotId() {
        return this.armorStandId;
    }

    public String getName() {
        return this.name;
    }

    public static EquipmentSlot byName(String name) {
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if (!equipmentSlot.getName().equals(name)) continue;
            return equipmentSlot;
        }
        throw new IllegalArgumentException("Invalid slot '" + name + "'");
    }

    public static EquipmentSlot fromTypeIndex(Type type, int index) {
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            if (equipmentSlot.getType() != type || equipmentSlot.getEntitySlotId() != index) continue;
            return equipmentSlot;
        }
        throw new IllegalArgumentException("Invalid slot '" + type + "': " + index);
    }

    private static /* synthetic */ EquipmentSlot[] method_36604() {
        return new EquipmentSlot[]{MAINHAND, OFFHAND, FEET, LEGS, CHEST, HEAD};
    }

    static {
        field_6176 = EquipmentSlot.method_36604();
    }

    public static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type HAND = new Type();
        public static final /* enum */ Type ARMOR = new Type();
        private static final /* synthetic */ Type[] field_6179;

        public static Type[] values() {
            return (Type[])field_6179.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private static /* synthetic */ Type[] method_36605() {
            return new Type[]{HAND, ARMOR};
        }

        static {
            field_6179 = Type.method_36605();
        }
    }
}

