/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.network;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTrackerEntry {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ServerWorld field_18258;
    private final Entity entity;
    private final int tickInterval;
    private final boolean alwaysUpdateVelocity;
    private final Consumer<Packet<?>> field_18259;
    private long lastX;
    private long lastY;
    private long lastZ;
    private int lastYaw;
    private int lastPitch;
    private int lastHeadPitch;
    private Vec3d field_18278 = Vec3d.ZERO;
    private int field_14040;
    private int field_14043;
    private List<Entity> lastPassengers = Collections.emptyList();
    private boolean field_14051;
    private boolean lastOnGround;

    public EntityTrackerEntry(ServerWorld world, Entity entity, int tickInterval, boolean alwaysUpdateVelocity, Consumer<Packet<?>> receiver) {
        this.field_18258 = world;
        this.field_18259 = receiver;
        this.entity = entity;
        this.tickInterval = tickInterval;
        this.alwaysUpdateVelocity = alwaysUpdateVelocity;
        this.method_18761();
        this.lastYaw = MathHelper.floor(entity.yaw * 256.0f / 360.0f);
        this.lastPitch = MathHelper.floor(entity.pitch * 256.0f / 360.0f);
        this.lastHeadPitch = MathHelper.floor(entity.getHeadYaw() * 256.0f / 360.0f);
        this.lastOnGround = entity.onGround;
    }

    public void method_18756() {
        List<Entity> list = this.entity.getPassengerList();
        if (!list.equals(this.lastPassengers)) {
            this.lastPassengers = list;
            this.field_18259.accept(new EntityPassengersSetS2CPacket(this.entity));
        }
        if (this.entity instanceof ItemFrameEntity && this.field_14040 % 10 == 0) {
            ItemFrameEntity itemFrameEntity = (ItemFrameEntity)this.entity;
            ItemStack itemStack = itemFrameEntity.getHeldItemStack();
            if (itemStack.getItem() instanceof FilledMapItem) {
                MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, this.field_18258);
                for (ServerPlayerEntity serverPlayerEntity : this.field_18258.getPlayers()) {
                    mapState.update(serverPlayerEntity, itemStack);
                    Packet<?> packet = ((FilledMapItem)itemStack.getItem()).createSyncPacket(itemStack, this.field_18258, serverPlayerEntity);
                    if (packet == null) continue;
                    serverPlayerEntity.networkHandler.sendPacket(packet);
                }
            }
            this.method_14306();
        }
        if (this.field_14040 % this.tickInterval == 0 || this.entity.velocityDirty || this.entity.getDataTracker().isDirty()) {
            int i;
            if (this.entity.hasVehicle()) {
                boolean bl;
                i = MathHelper.floor(this.entity.yaw * 256.0f / 360.0f);
                int j = MathHelper.floor(this.entity.pitch * 256.0f / 360.0f);
                boolean bl2 = bl = Math.abs(i - this.lastYaw) >= 1 || Math.abs(j - this.lastPitch) >= 1;
                if (bl) {
                    this.field_18259.accept(new EntityS2CPacket.Rotate(this.entity.getEntityId(), (byte)i, (byte)j, this.entity.onGround));
                    this.lastYaw = i;
                    this.lastPitch = j;
                }
                this.method_18761();
                this.method_14306();
                this.field_14051 = true;
            } else {
                Vec3d vec3d2;
                double d;
                boolean bl4;
                ++this.field_14043;
                i = MathHelper.floor(this.entity.yaw * 256.0f / 360.0f);
                int j = MathHelper.floor(this.entity.pitch * 256.0f / 360.0f);
                Vec3d vec3d = new Vec3d(this.entity.x, this.entity.y, this.entity.z).subtract(EntityS2CPacket.decodePacketCoordinates(this.lastX, this.lastY, this.lastZ));
                boolean bl2 = vec3d.lengthSquared() >= 7.62939453125E-6;
                Packet<ClientPlayPacketListener> packet2 = null;
                boolean bl3 = bl2 || this.field_14040 % 60 == 0;
                boolean bl = bl4 = Math.abs(i - this.lastYaw) >= 1 || Math.abs(j - this.lastPitch) >= 1;
                if (this.field_14040 > 0 || this.entity instanceof ProjectileEntity) {
                    boolean bl5;
                    long l = EntityS2CPacket.encodePacketCoordinate(vec3d.x);
                    long m = EntityS2CPacket.encodePacketCoordinate(vec3d.y);
                    long n = EntityS2CPacket.encodePacketCoordinate(vec3d.z);
                    boolean bl6 = bl5 = l < -32768L || l > 32767L || m < -32768L || m > 32767L || n < -32768L || n > 32767L;
                    if (bl5 || this.field_14043 > 400 || this.field_14051 || this.lastOnGround != this.entity.onGround) {
                        this.lastOnGround = this.entity.onGround;
                        this.field_14043 = 0;
                        packet2 = new EntityPositionS2CPacket(this.entity);
                    } else if (bl3 && bl4 || this.entity instanceof ProjectileEntity) {
                        packet2 = new EntityS2CPacket.RotateAndMoveRelative(this.entity.getEntityId(), (short)l, (short)m, (short)n, (byte)i, (byte)j, this.entity.onGround);
                    } else if (bl3) {
                        packet2 = new EntityS2CPacket.MoveRelative(this.entity.getEntityId(), (short)l, (short)m, (short)n, this.entity.onGround);
                    } else if (bl4) {
                        packet2 = new EntityS2CPacket.Rotate(this.entity.getEntityId(), (byte)i, (byte)j, this.entity.onGround);
                    }
                }
                if ((this.alwaysUpdateVelocity || this.entity.velocityDirty || this.entity instanceof LivingEntity && ((LivingEntity)this.entity).isFallFlying()) && this.field_14040 > 0 && ((d = (vec3d2 = this.entity.getVelocity()).squaredDistanceTo(this.field_18278)) > 1.0E-7 || d > 0.0 && vec3d2.lengthSquared() == 0.0)) {
                    this.field_18278 = vec3d2;
                    this.field_18259.accept(new EntityVelocityUpdateS2CPacket(this.entity.getEntityId(), this.field_18278));
                }
                if (packet2 != null) {
                    this.field_18259.accept(packet2);
                }
                this.method_14306();
                if (bl3) {
                    this.method_18761();
                }
                if (bl4) {
                    this.lastYaw = i;
                    this.lastPitch = j;
                }
                this.field_14051 = false;
            }
            i = MathHelper.floor(this.entity.getHeadYaw() * 256.0f / 360.0f);
            if (Math.abs(i - this.lastHeadPitch) >= 1) {
                this.field_18259.accept(new EntitySetHeadYawS2CPacket(this.entity, (byte)i));
                this.lastHeadPitch = i;
            }
            this.entity.velocityDirty = false;
        }
        ++this.field_14040;
        if (this.entity.velocityModified) {
            this.method_18758(new EntityVelocityUpdateS2CPacket(this.entity));
            this.entity.velocityModified = false;
        }
    }

    public void stopTracking(ServerPlayerEntity player) {
        this.entity.onStoppedTrackingBy(player);
        player.onStoppedTracking(this.entity);
    }

    public void startTracking(ServerPlayerEntity player) {
        this.sendPackets(player.networkHandler::sendPacket);
        this.entity.onStartedTrackingBy(player);
        player.onStartedTracking(this.entity);
    }

    public void sendPackets(Consumer<Packet<?>> sender) {
        if (this.entity.removed) {
            LOGGER.warn("Fetching packet for removed entity " + this.entity);
        }
        Packet<?> packet = this.entity.createSpawnPacket();
        this.lastHeadPitch = MathHelper.floor(this.entity.getHeadYaw() * 256.0f / 360.0f);
        sender.accept(packet);
        if (!this.entity.getDataTracker().isEmpty()) {
            sender.accept(new EntityTrackerUpdateS2CPacket(this.entity.getEntityId(), this.entity.getDataTracker(), true));
        }
        boolean bl = this.alwaysUpdateVelocity;
        if (this.entity instanceof LivingEntity) {
            EquipmentSlot[] entityAttributeContainer = (EquipmentSlot[])((LivingEntity)this.entity).getAttributes();
            Collection<EntityAttributeInstance> collection = entityAttributeContainer.buildTrackedAttributesCollection();
            if (!collection.isEmpty()) {
                sender.accept(new EntityAttributesS2CPacket(this.entity.getEntityId(), collection));
            }
            if (((LivingEntity)this.entity).isFallFlying()) {
                bl = true;
            }
        }
        this.field_18278 = this.entity.getVelocity();
        if (bl && !(packet instanceof MobSpawnS2CPacket)) {
            sender.accept(new EntityVelocityUpdateS2CPacket(this.entity.getEntityId(), this.field_18278));
        }
        if (this.entity instanceof LivingEntity) {
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                ItemStack itemStack = ((LivingEntity)this.entity).getEquippedStack(equipmentSlot);
                if (itemStack.isEmpty()) continue;
                sender.accept(new EntityEquipmentUpdateS2CPacket(this.entity.getEntityId(), equipmentSlot, itemStack));
            }
        }
        if (this.entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)this.entity;
            for (StatusEffectInstance statusEffectInstance : livingEntity.getStatusEffects()) {
                sender.accept(new EntityStatusEffectS2CPacket(this.entity.getEntityId(), statusEffectInstance));
            }
        }
        if (!this.entity.getPassengerList().isEmpty()) {
            sender.accept(new EntityPassengersSetS2CPacket(this.entity));
        }
        if (this.entity.hasVehicle()) {
            sender.accept(new EntityPassengersSetS2CPacket(this.entity.getVehicle()));
        }
    }

    private void method_14306() {
        DataTracker dataTracker = this.entity.getDataTracker();
        if (dataTracker.isDirty()) {
            this.method_18758(new EntityTrackerUpdateS2CPacket(this.entity.getEntityId(), dataTracker, false));
        }
        if (this.entity instanceof LivingEntity) {
            EntityAttributeContainer entityAttributeContainer = (EntityAttributeContainer)((LivingEntity)this.entity).getAttributes();
            Set<EntityAttributeInstance> set = entityAttributeContainer.getTrackedAttributes();
            if (!set.isEmpty()) {
                this.method_18758(new EntityAttributesS2CPacket(this.entity.getEntityId(), set));
            }
            set.clear();
        }
    }

    private void method_18761() {
        this.lastX = EntityS2CPacket.encodePacketCoordinate(this.entity.x);
        this.lastY = EntityS2CPacket.encodePacketCoordinate(this.entity.y);
        this.lastZ = EntityS2CPacket.encodePacketCoordinate(this.entity.z);
    }

    public Vec3d method_18759() {
        return EntityS2CPacket.decodePacketCoordinates(this.lastX, this.lastY, this.lastZ);
    }

    private void method_18758(Packet<?> packet) {
        this.field_18259.accept(packet);
        if (this.entity instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity)this.entity).networkHandler.sendPacket(packet);
        }
    }
}
