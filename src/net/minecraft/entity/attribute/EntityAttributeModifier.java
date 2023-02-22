/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.entity.attribute;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class EntityAttributeModifier {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final double value;
    private final Operation operation;
    private final Supplier<String> nameGetter;
    private final UUID uuid;

    public EntityAttributeModifier(String name, double value, Operation operation) {
        this(MathHelper.randomUuid(Random.createLocal()), () -> name, value, operation);
    }

    public EntityAttributeModifier(UUID uuid, String name, double value, Operation operation) {
        this(uuid, () -> name, value, operation);
    }

    public EntityAttributeModifier(UUID uuid, Supplier<String> nameGetter, double value, Operation operation) {
        this.uuid = uuid;
        this.nameGetter = nameGetter;
        this.value = value;
        this.operation = operation;
    }

    public UUID getId() {
        return this.uuid;
    }

    public String getName() {
        return this.nameGetter.get();
    }

    public Operation getOperation() {
        return this.operation;
    }

    public double getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)o;
        return Objects.equals(this.uuid, entityAttributeModifier.uuid);
    }

    public int hashCode() {
        return this.uuid.hashCode();
    }

    public String toString() {
        return "AttributeModifier{amount=" + this.value + ", operation=" + this.operation + ", name='" + this.nameGetter.get() + "', id=" + this.uuid + "}";
    }

    public NbtCompound toNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("Name", this.getName());
        nbtCompound.putDouble("Amount", this.value);
        nbtCompound.putInt("Operation", this.operation.getId());
        nbtCompound.putUuid("UUID", this.uuid);
        return nbtCompound;
    }

    @Nullable
    public static EntityAttributeModifier fromNbt(NbtCompound nbt) {
        try {
            UUID uUID = nbt.getUuid("UUID");
            Operation operation = Operation.fromId(nbt.getInt("Operation"));
            return new EntityAttributeModifier(uUID, nbt.getString("Name"), nbt.getDouble("Amount"), operation);
        }
        catch (Exception exception) {
            LOGGER.warn("Unable to create attribute: {}", (Object)exception.getMessage());
            return null;
        }
    }

    public static final class Operation
    extends Enum<Operation> {
        public static final /* enum */ Operation ADDITION = new Operation(0);
        public static final /* enum */ Operation MULTIPLY_BASE = new Operation(1);
        public static final /* enum */ Operation MULTIPLY_TOTAL = new Operation(2);
        private static final Operation[] VALUES;
        private final int id;
        private static final /* synthetic */ Operation[] field_6333;

        public static Operation[] values() {
            return (Operation[])field_6333.clone();
        }

        public static Operation valueOf(String string) {
            return Enum.valueOf(Operation.class, string);
        }

        private Operation(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        public static Operation fromId(int id) {
            if (id < 0 || id >= VALUES.length) {
                throw new IllegalArgumentException("No operation with value " + id);
            }
            return VALUES[id];
        }

        private static /* synthetic */ Operation[] method_36614() {
            return new Operation[]{ADDITION, MULTIPLY_BASE, MULTIPLY_TOTAL};
        }

        static {
            field_6333 = Operation.method_36614();
            VALUES = new Operation[]{ADDITION, MULTIPLY_BASE, MULTIPLY_TOTAL};
        }
    }
}

