/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.ObjectArraySet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.attribute;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class EntityAttributeInstance {
    private final EntityAttribute type;
    private final Map<EntityAttributeModifier.Operation, Set<EntityAttributeModifier>> operationToModifiers = Maps.newEnumMap(EntityAttributeModifier.Operation.class);
    private final Map<UUID, EntityAttributeModifier> idToModifiers = new Object2ObjectArrayMap();
    private final Set<EntityAttributeModifier> persistentModifiers = new ObjectArraySet();
    private double baseValue;
    private boolean dirty = true;
    private double value;
    private final Consumer<EntityAttributeInstance> updateCallback;

    public EntityAttributeInstance(EntityAttribute type, Consumer<EntityAttributeInstance> updateCallback) {
        this.type = type;
        this.updateCallback = updateCallback;
        this.baseValue = type.getDefaultValue();
    }

    public EntityAttribute getAttribute() {
        return this.type;
    }

    public double getBaseValue() {
        return this.baseValue;
    }

    public void setBaseValue(double baseValue) {
        if (baseValue == this.baseValue) {
            return;
        }
        this.baseValue = baseValue;
        this.onUpdate();
    }

    public Set<EntityAttributeModifier> getModifiers(EntityAttributeModifier.Operation operation2) {
        return this.operationToModifiers.computeIfAbsent(operation2, operation -> Sets.newHashSet());
    }

    public Set<EntityAttributeModifier> getModifiers() {
        return ImmutableSet.copyOf(this.idToModifiers.values());
    }

    @Nullable
    public EntityAttributeModifier getModifier(UUID uuid) {
        return this.idToModifiers.get(uuid);
    }

    public boolean hasModifier(EntityAttributeModifier modifier) {
        return this.idToModifiers.get(modifier.getId()) != null;
    }

    private void addModifier(EntityAttributeModifier modifier) {
        EntityAttributeModifier entityAttributeModifier = this.idToModifiers.putIfAbsent(modifier.getId(), modifier);
        if (entityAttributeModifier != null) {
            throw new IllegalArgumentException("Modifier is already applied on this attribute!");
        }
        this.getModifiers(modifier.getOperation()).add(modifier);
        this.onUpdate();
    }

    public void addTemporaryModifier(EntityAttributeModifier modifier) {
        this.addModifier(modifier);
    }

    public void addPersistentModifier(EntityAttributeModifier modifier) {
        this.addModifier(modifier);
        this.persistentModifiers.add(modifier);
    }

    protected void onUpdate() {
        this.dirty = true;
        this.updateCallback.accept(this);
    }

    public void removeModifier(EntityAttributeModifier modifier) {
        this.getModifiers(modifier.getOperation()).remove(modifier);
        this.idToModifiers.remove(modifier.getId());
        this.persistentModifiers.remove(modifier);
        this.onUpdate();
    }

    public void removeModifier(UUID uuid) {
        EntityAttributeModifier entityAttributeModifier = this.getModifier(uuid);
        if (entityAttributeModifier != null) {
            this.removeModifier(entityAttributeModifier);
        }
    }

    public boolean tryRemoveModifier(UUID uuid) {
        EntityAttributeModifier entityAttributeModifier = this.getModifier(uuid);
        if (entityAttributeModifier != null && this.persistentModifiers.contains(entityAttributeModifier)) {
            this.removeModifier(entityAttributeModifier);
            return true;
        }
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public void clearModifiers() {
        for (EntityAttributeModifier entityAttributeModifier : this.getModifiers()) {
            this.removeModifier(entityAttributeModifier);
        }
    }

    public double getValue() {
        if (this.dirty) {
            this.value = this.computeValue();
            this.dirty = false;
        }
        return this.value;
    }

    private double computeValue() {
        double d = this.getBaseValue();
        for (EntityAttributeModifier entityAttributeModifier : this.getModifiersByOperation(EntityAttributeModifier.Operation.ADDITION)) {
            d += entityAttributeModifier.getValue();
        }
        double e = d;
        for (EntityAttributeModifier entityAttributeModifier2 : this.getModifiersByOperation(EntityAttributeModifier.Operation.MULTIPLY_BASE)) {
            e += d * entityAttributeModifier2.getValue();
        }
        for (EntityAttributeModifier entityAttributeModifier2 : this.getModifiersByOperation(EntityAttributeModifier.Operation.MULTIPLY_TOTAL)) {
            e *= 1.0 + entityAttributeModifier2.getValue();
        }
        return this.type.clamp(e);
    }

    private Collection<EntityAttributeModifier> getModifiersByOperation(EntityAttributeModifier.Operation operation) {
        return this.operationToModifiers.getOrDefault((Object)operation, Collections.emptySet());
    }

    public void setFrom(EntityAttributeInstance other) {
        this.baseValue = other.baseValue;
        this.idToModifiers.clear();
        this.idToModifiers.putAll(other.idToModifiers);
        this.persistentModifiers.clear();
        this.persistentModifiers.addAll(other.persistentModifiers);
        this.operationToModifiers.clear();
        other.operationToModifiers.forEach((operation, modifiers) -> this.getModifiers((EntityAttributeModifier.Operation)((Object)operation)).addAll((Collection<EntityAttributeModifier>)modifiers));
        this.onUpdate();
    }

    public NbtCompound toNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("Name", Registry.ATTRIBUTE.getId(this.type).toString());
        nbtCompound.putDouble("Base", this.baseValue);
        if (!this.persistentModifiers.isEmpty()) {
            NbtList nbtList = new NbtList();
            for (EntityAttributeModifier entityAttributeModifier : this.persistentModifiers) {
                nbtList.add(entityAttributeModifier.toNbt());
            }
            nbtCompound.put("Modifiers", nbtList);
        }
        return nbtCompound;
    }

    public void readNbt(NbtCompound nbt) {
        this.baseValue = nbt.getDouble("Base");
        if (nbt.contains("Modifiers", 9)) {
            NbtList nbtList = nbt.getList("Modifiers", 10);
            for (int i = 0; i < nbtList.size(); ++i) {
                EntityAttributeModifier entityAttributeModifier = EntityAttributeModifier.fromNbt(nbtList.getCompound(i));
                if (entityAttributeModifier == null) continue;
                this.idToModifiers.put(entityAttributeModifier.getId(), entityAttributeModifier);
                this.getModifiers(entityAttributeModifier.getOperation()).add(entityAttributeModifier);
                this.persistentModifiers.add(entityAttributeModifier);
            }
        }
        this.onUpdate();
    }
}

