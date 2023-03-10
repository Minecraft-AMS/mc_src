/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.properties.Property
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block.entity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.StringHelper;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SkullBlockEntity
extends BlockEntity {
    public static final String SKULL_OWNER_KEY = "SkullOwner";
    @Nullable
    private static UserCache userCache;
    @Nullable
    private static MinecraftSessionService sessionService;
    @Nullable
    private static Executor executor;
    @Nullable
    private GameProfile owner;
    private int ticksPowered;
    private boolean powered;

    public SkullBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.SKULL, pos, state);
    }

    public static void setServices(UserCache userCache, MinecraftSessionService sessionService, Executor executor) {
        SkullBlockEntity.userCache = userCache;
        SkullBlockEntity.sessionService = sessionService;
        SkullBlockEntity.executor = executor;
    }

    public static void clearServices() {
        userCache = null;
        sessionService = null;
        executor = null;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.owner != null) {
            NbtCompound nbtCompound = new NbtCompound();
            NbtHelper.writeGameProfile(nbtCompound, this.owner);
            nbt.put(SKULL_OWNER_KEY, nbtCompound);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        String string;
        super.readNbt(nbt);
        if (nbt.contains(SKULL_OWNER_KEY, 10)) {
            this.setOwner(NbtHelper.toGameProfile(nbt.getCompound(SKULL_OWNER_KEY)));
        } else if (nbt.contains("ExtraType", 8) && !StringHelper.isEmpty(string = nbt.getString("ExtraType"))) {
            this.setOwner(new GameProfile(null, string));
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, SkullBlockEntity blockEntity) {
        if (world.isReceivingRedstonePower(pos)) {
            blockEntity.powered = true;
            ++blockEntity.ticksPowered;
        } else {
            blockEntity.powered = false;
        }
    }

    public float getTicksPowered(float tickDelta) {
        if (this.powered) {
            return (float)this.ticksPowered + tickDelta;
        }
        return this.ticksPowered;
    }

    @Nullable
    public GameProfile getOwner() {
        return this.owner;
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setOwner(@Nullable GameProfile owner) {
        SkullBlockEntity skullBlockEntity = this;
        synchronized (skullBlockEntity) {
            this.owner = owner;
        }
        this.loadOwnerProperties();
    }

    private void loadOwnerProperties() {
        SkullBlockEntity.loadProperties(this.owner, owner -> {
            this.owner = owner;
            this.markDirty();
        });
    }

    public static void loadProperties(@Nullable GameProfile owner, Consumer<GameProfile> callback) {
        if (owner == null || StringHelper.isEmpty(owner.getName()) || owner.isComplete() && owner.getProperties().containsKey((Object)"textures") || userCache == null || sessionService == null) {
            callback.accept(owner);
            return;
        }
        userCache.findByNameAsync(owner.getName(), profile -> Util.getMainWorkerExecutor().execute(() -> Util.ifPresentOrElse(profile, profile -> {
            Property property = (Property)Iterables.getFirst((Iterable)profile.getProperties().get((Object)"textures"), null);
            if (property == null) {
                profile = sessionService.fillProfileProperties(profile, true);
            }
            GameProfile gameProfile = profile;
            executor.execute(() -> {
                userCache.add(gameProfile);
                callback.accept(gameProfile);
            });
        }, () -> executor.execute(() -> callback.accept(owner)))));
    }

    public /* synthetic */ Packet toUpdatePacket() {
        return this.toUpdatePacket();
    }
}

