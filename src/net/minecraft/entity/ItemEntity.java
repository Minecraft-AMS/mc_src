/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class ItemEntity
extends Entity {
    private static final TrackedData<ItemStack> STACK = DataTracker.registerData(ItemEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final int DESPAWN_AGE = 6000;
    private static final int CANNOT_PICK_UP_DELAY = Short.MAX_VALUE;
    private static final int NEVER_DESPAWN_AGE = Short.MIN_VALUE;
    private int itemAge;
    private int pickupDelay;
    private int health = 5;
    @Nullable
    private UUID thrower;
    @Nullable
    private UUID owner;
    public final float uniqueOffset;

    public ItemEntity(EntityType<? extends ItemEntity> entityType, World world) {
        super(entityType, world);
        this.uniqueOffset = this.random.nextFloat() * (float)Math.PI * 2.0f;
        this.setYaw(this.random.nextFloat() * 360.0f);
    }

    public ItemEntity(World world, double x, double y, double z, ItemStack stack) {
        this(world, x, y, z, stack, world.random.nextDouble() * 0.2 - 0.1, 0.2, world.random.nextDouble() * 0.2 - 0.1);
    }

    public ItemEntity(World world, double x, double y, double z, ItemStack stack, double velocityX, double velocityY, double velocityZ) {
        this((EntityType<? extends ItemEntity>)EntityType.ITEM, world);
        this.setPosition(x, y, z);
        this.setVelocity(velocityX, velocityY, velocityZ);
        this.setStack(stack);
    }

    private ItemEntity(ItemEntity entity) {
        super(entity.getType(), entity.world);
        this.setStack(entity.getStack().copy());
        this.copyPositionAndRotation(entity);
        this.itemAge = entity.itemAge;
        this.uniqueOffset = entity.uniqueOffset;
    }

    @Override
    public boolean occludeVibrationSignals() {
        return this.getStack().isIn(ItemTags.OCCLUDES_VIBRATION_SIGNALS);
    }

    @Override
    protected Entity.MoveEffect getMoveEffect() {
        return Entity.MoveEffect.NONE;
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(STACK, ItemStack.EMPTY);
    }

    @Override
    public void tick() {
        double d;
        int i;
        if (this.getStack().isEmpty()) {
            this.discard();
            return;
        }
        super.tick();
        if (this.pickupDelay > 0 && this.pickupDelay != Short.MAX_VALUE) {
            --this.pickupDelay;
        }
        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();
        Vec3d vec3d = this.getVelocity();
        float f = this.getStandingEyeHeight() - 0.11111111f;
        if (this.isTouchingWater() && this.getFluidHeight(FluidTags.WATER) > (double)f) {
            this.applyWaterBuoyancy();
        } else if (this.isInLava() && this.getFluidHeight(FluidTags.LAVA) > (double)f) {
            this.applyLavaBuoyancy();
        } else if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
        }
        if (this.world.isClient) {
            this.noClip = false;
        } else {
            boolean bl = this.noClip = !this.world.isSpaceEmpty(this, this.getBoundingBox().contract(1.0E-7));
            if (this.noClip) {
                this.pushOutOfBlocks(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.getZ());
            }
        }
        if (!this.onGround || this.getVelocity().horizontalLengthSquared() > (double)1.0E-5f || (this.age + this.getId()) % 4 == 0) {
            this.move(MovementType.SELF, this.getVelocity());
            float g = 0.98f;
            if (this.onGround) {
                g = this.world.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0, this.getZ())).getBlock().getSlipperiness() * 0.98f;
            }
            this.setVelocity(this.getVelocity().multiply(g, 0.98, g));
            if (this.onGround) {
                Vec3d vec3d2 = this.getVelocity();
                if (vec3d2.y < 0.0) {
                    this.setVelocity(vec3d2.multiply(1.0, -0.5, 1.0));
                }
            }
        }
        boolean bl = MathHelper.floor(this.prevX) != MathHelper.floor(this.getX()) || MathHelper.floor(this.prevY) != MathHelper.floor(this.getY()) || MathHelper.floor(this.prevZ) != MathHelper.floor(this.getZ());
        int n = i = bl ? 2 : 40;
        if (this.age % i == 0 && !this.world.isClient && this.canMerge()) {
            this.tryMerge();
        }
        if (this.itemAge != Short.MIN_VALUE) {
            ++this.itemAge;
        }
        this.velocityDirty |= this.updateWaterState();
        if (!this.world.isClient && (d = this.getVelocity().subtract(vec3d).lengthSquared()) > 0.01) {
            this.velocityDirty = true;
        }
        if (!this.world.isClient && this.itemAge >= 6000) {
            this.discard();
        }
    }

    private void applyWaterBuoyancy() {
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x * (double)0.99f, vec3d.y + (double)(vec3d.y < (double)0.06f ? 5.0E-4f : 0.0f), vec3d.z * (double)0.99f);
    }

    private void applyLavaBuoyancy() {
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x * (double)0.95f, vec3d.y + (double)(vec3d.y < (double)0.06f ? 5.0E-4f : 0.0f), vec3d.z * (double)0.95f);
    }

    private void tryMerge() {
        if (!this.canMerge()) {
            return;
        }
        List<ItemEntity> list = this.world.getEntitiesByClass(ItemEntity.class, this.getBoundingBox().expand(0.5, 0.0, 0.5), otherItemEntity -> otherItemEntity != this && otherItemEntity.canMerge());
        for (ItemEntity itemEntity : list) {
            if (!itemEntity.canMerge()) continue;
            this.tryMerge(itemEntity);
            if (!this.isRemoved()) continue;
            break;
        }
    }

    private boolean canMerge() {
        ItemStack itemStack = this.getStack();
        return this.isAlive() && this.pickupDelay != Short.MAX_VALUE && this.itemAge != Short.MIN_VALUE && this.itemAge < 6000 && itemStack.getCount() < itemStack.getMaxCount();
    }

    private void tryMerge(ItemEntity other) {
        ItemStack itemStack = this.getStack();
        ItemStack itemStack2 = other.getStack();
        if (!Objects.equals(this.getOwner(), other.getOwner()) || !ItemEntity.canMerge(itemStack, itemStack2)) {
            return;
        }
        if (itemStack2.getCount() < itemStack.getCount()) {
            ItemEntity.merge(this, itemStack, other, itemStack2);
        } else {
            ItemEntity.merge(other, itemStack2, this, itemStack);
        }
    }

    public static boolean canMerge(ItemStack stack1, ItemStack stack2) {
        if (!stack2.isOf(stack1.getItem())) {
            return false;
        }
        if (stack2.getCount() + stack1.getCount() > stack2.getMaxCount()) {
            return false;
        }
        if (stack2.hasNbt() ^ stack1.hasNbt()) {
            return false;
        }
        return !stack2.hasNbt() || stack2.getNbt().equals(stack1.getNbt());
    }

    public static ItemStack merge(ItemStack stack1, ItemStack stack2, int maxCount) {
        int i = Math.min(Math.min(stack1.getMaxCount(), maxCount) - stack1.getCount(), stack2.getCount());
        ItemStack itemStack = stack1.copy();
        itemStack.increment(i);
        stack2.decrement(i);
        return itemStack;
    }

    private static void merge(ItemEntity targetEntity, ItemStack stack1, ItemStack stack2) {
        ItemStack itemStack = ItemEntity.merge(stack1, stack2, 64);
        targetEntity.setStack(itemStack);
    }

    private static void merge(ItemEntity targetEntity, ItemStack targetStack, ItemEntity sourceEntity, ItemStack sourceStack) {
        ItemEntity.merge(targetEntity, targetStack, sourceStack);
        targetEntity.pickupDelay = Math.max(targetEntity.pickupDelay, sourceEntity.pickupDelay);
        targetEntity.itemAge = Math.min(targetEntity.itemAge, sourceEntity.itemAge);
        if (sourceStack.isEmpty()) {
            sourceEntity.discard();
        }
    }

    @Override
    public boolean isFireImmune() {
        return this.getStack().getItem().isFireproof() || super.isFireImmune();
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (!this.getStack().isEmpty() && this.getStack().isOf(Items.NETHER_STAR) && source.isExplosive()) {
            return false;
        }
        if (!this.getStack().getItem().damage(source)) {
            return false;
        }
        if (this.world.isClient) {
            return true;
        }
        this.scheduleVelocityUpdate();
        this.health = (int)((float)this.health - amount);
        this.emitGameEvent(GameEvent.ENTITY_DAMAGED, source.getAttacker());
        if (this.health <= 0) {
            this.getStack().onItemEntityDestroyed(this);
            this.discard();
        }
        return true;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putShort("Health", (short)this.health);
        nbt.putShort("Age", (short)this.itemAge);
        nbt.putShort("PickupDelay", (short)this.pickupDelay);
        if (this.getThrower() != null) {
            nbt.putUuid("Thrower", this.getThrower());
        }
        if (this.getOwner() != null) {
            nbt.putUuid("Owner", this.getOwner());
        }
        if (!this.getStack().isEmpty()) {
            nbt.put("Item", this.getStack().writeNbt(new NbtCompound()));
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        this.health = nbt.getShort("Health");
        this.itemAge = nbt.getShort("Age");
        if (nbt.contains("PickupDelay")) {
            this.pickupDelay = nbt.getShort("PickupDelay");
        }
        if (nbt.containsUuid("Owner")) {
            this.owner = nbt.getUuid("Owner");
        }
        if (nbt.containsUuid("Thrower")) {
            this.thrower = nbt.getUuid("Thrower");
        }
        NbtCompound nbtCompound = nbt.getCompound("Item");
        this.setStack(ItemStack.fromNbt(nbtCompound));
        if (this.getStack().isEmpty()) {
            this.discard();
        }
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (this.world.isClient) {
            return;
        }
        ItemStack itemStack = this.getStack();
        Item item = itemStack.getItem();
        int i = itemStack.getCount();
        if (this.pickupDelay == 0 && (this.owner == null || this.owner.equals(player.getUuid())) && player.getInventory().insertStack(itemStack)) {
            player.sendPickup(this, i);
            if (itemStack.isEmpty()) {
                this.discard();
                itemStack.setCount(i);
            }
            player.increaseStat(Stats.PICKED_UP.getOrCreateStat(item), i);
            player.triggerItemPickedUpByEntityCriteria(this);
        }
    }

    @Override
    public Text getName() {
        Text text = this.getCustomName();
        if (text != null) {
            return text;
        }
        return new TranslatableText(this.getStack().getTranslationKey());
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    @Nullable
    public Entity moveToWorld(ServerWorld destination) {
        Entity entity = super.moveToWorld(destination);
        if (!this.world.isClient && entity instanceof ItemEntity) {
            ((ItemEntity)entity).tryMerge();
        }
        return entity;
    }

    public ItemStack getStack() {
        return this.getDataTracker().get(STACK);
    }

    public void setStack(ItemStack stack) {
        this.getDataTracker().set(STACK, stack);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (STACK.equals(data)) {
            this.getStack().setHolder(this);
        }
    }

    @Nullable
    public UUID getOwner() {
        return this.owner;
    }

    public void setOwner(@Nullable UUID owner) {
        this.owner = owner;
    }

    @Nullable
    public UUID getThrower() {
        return this.thrower;
    }

    public void setThrower(@Nullable UUID thrower) {
        this.thrower = thrower;
    }

    public int getItemAge() {
        return this.itemAge;
    }

    public void setToDefaultPickupDelay() {
        this.pickupDelay = 10;
    }

    public void resetPickupDelay() {
        this.pickupDelay = 0;
    }

    public void setPickupDelayInfinite() {
        this.pickupDelay = Short.MAX_VALUE;
    }

    public void setPickupDelay(int pickupDelay) {
        this.pickupDelay = pickupDelay;
    }

    public boolean cannotPickup() {
        return this.pickupDelay > 0;
    }

    public void setNeverDespawn() {
        this.itemAge = Short.MIN_VALUE;
    }

    public void setCovetedItem() {
        this.itemAge = -6000;
    }

    public void setDespawnImmediately() {
        this.setPickupDelayInfinite();
        this.itemAge = 5999;
    }

    public float getRotation(float tickDelta) {
        return ((float)this.getItemAge() + tickDelta) / 20.0f + this.uniqueOffset;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    public ItemEntity copy() {
        return new ItemEntity(this);
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.AMBIENT;
    }
}

