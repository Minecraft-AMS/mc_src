/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.item.map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapBannerMarker;
import net.minecraft.item.map.MapFrameMarker;
import net.minecraft.item.map.MapIcon;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockView;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class MapState
extends PersistentState {
    private static final Logger field_25019 = LogManager.getLogger();
    public int centerX;
    public int centerZ;
    public RegistryKey<World> dimension;
    public boolean showIcons;
    public boolean unlimitedTracking;
    public byte scale;
    public byte[] colors = new byte[16384];
    public boolean locked;
    public final List<PlayerUpdateTracker> updateTrackers = Lists.newArrayList();
    private final Map<PlayerEntity, PlayerUpdateTracker> updateTrackersByPlayer = Maps.newHashMap();
    private final Map<String, MapBannerMarker> banners = Maps.newHashMap();
    public final Map<String, MapIcon> icons = Maps.newLinkedHashMap();
    private final Map<String, MapFrameMarker> frames = Maps.newHashMap();

    public MapState(String string) {
        super(string);
    }

    public void init(int x, int z, int scale, boolean showIcons, boolean unlimitedTracking, RegistryKey<World> dimension) {
        this.scale = (byte)scale;
        this.calculateCenter(x, z, this.scale);
        this.dimension = dimension;
        this.showIcons = showIcons;
        this.unlimitedTracking = unlimitedTracking;
        this.markDirty();
    }

    public void calculateCenter(double x, double z, int scale) {
        int i = 128 * (1 << scale);
        int j = MathHelper.floor((x + 64.0) / (double)i);
        int k = MathHelper.floor((z + 64.0) / (double)i);
        this.centerX = j * i + i / 2 - 64;
        this.centerZ = k * i + i / 2 - 64;
    }

    @Override
    public void fromTag(NbtCompound tag) {
        this.dimension = (RegistryKey)DimensionType.method_28521(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)tag.get("dimension"))).resultOrPartial(arg_0 -> ((Logger)field_25019).error(arg_0)).orElseThrow(() -> new IllegalArgumentException("Invalid map dimension: " + tag.get("dimension")));
        this.centerX = tag.getInt("xCenter");
        this.centerZ = tag.getInt("zCenter");
        this.scale = (byte)MathHelper.clamp(tag.getByte("scale"), 0, 4);
        this.showIcons = !tag.contains("trackingPosition", 1) || tag.getBoolean("trackingPosition");
        this.unlimitedTracking = tag.getBoolean("unlimitedTracking");
        this.locked = tag.getBoolean("locked");
        this.colors = tag.getByteArray("colors");
        if (this.colors.length != 16384) {
            this.colors = new byte[16384];
        }
        NbtList nbtList = tag.getList("banners", 10);
        for (int i = 0; i < nbtList.size(); ++i) {
            MapBannerMarker mapBannerMarker = MapBannerMarker.fromNbt(nbtList.getCompound(i));
            this.banners.put(mapBannerMarker.getKey(), mapBannerMarker);
            this.addIcon(mapBannerMarker.getIconType(), null, mapBannerMarker.getKey(), mapBannerMarker.getPos().getX(), mapBannerMarker.getPos().getZ(), 180.0, mapBannerMarker.getName());
        }
        NbtList nbtList2 = tag.getList("frames", 10);
        for (int j = 0; j < nbtList2.size(); ++j) {
            MapFrameMarker mapFrameMarker = MapFrameMarker.fromNbt(nbtList2.getCompound(j));
            this.frames.put(mapFrameMarker.getKey(), mapFrameMarker);
            this.addIcon(MapIcon.Type.FRAME, null, "frame-" + mapFrameMarker.getEntityId(), mapFrameMarker.getPos().getX(), mapFrameMarker.getPos().getZ(), mapFrameMarker.getRotation(), null);
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        Identifier.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.dimension.getValue()).resultOrPartial(arg_0 -> ((Logger)field_25019).error(arg_0)).ifPresent(nbtElement -> nbt.put("dimension", (NbtElement)nbtElement));
        nbt.putInt("xCenter", this.centerX);
        nbt.putInt("zCenter", this.centerZ);
        nbt.putByte("scale", this.scale);
        nbt.putByteArray("colors", this.colors);
        nbt.putBoolean("trackingPosition", this.showIcons);
        nbt.putBoolean("unlimitedTracking", this.unlimitedTracking);
        nbt.putBoolean("locked", this.locked);
        NbtList nbtList = new NbtList();
        for (MapBannerMarker mapBannerMarker : this.banners.values()) {
            nbtList.add(mapBannerMarker.getNbt());
        }
        nbt.put("banners", nbtList);
        NbtList nbtList2 = new NbtList();
        for (MapFrameMarker mapFrameMarker : this.frames.values()) {
            nbtList2.add(mapFrameMarker.toNbt());
        }
        nbt.put("frames", nbtList2);
        return nbt;
    }

    public void copyFrom(MapState state) {
        this.locked = true;
        this.centerX = state.centerX;
        this.centerZ = state.centerZ;
        this.banners.putAll(state.banners);
        this.icons.putAll(state.icons);
        System.arraycopy(state.colors, 0, this.colors, 0, state.colors.length);
        this.markDirty();
    }

    public void update(PlayerEntity player, ItemStack stack) {
        NbtCompound nbtCompound;
        if (!this.updateTrackersByPlayer.containsKey(player)) {
            PlayerUpdateTracker playerUpdateTracker = new PlayerUpdateTracker(player);
            this.updateTrackersByPlayer.put(player, playerUpdateTracker);
            this.updateTrackers.add(playerUpdateTracker);
        }
        if (!player.inventory.contains(stack)) {
            this.icons.remove(player.getName().getString());
        }
        for (int i = 0; i < this.updateTrackers.size(); ++i) {
            PlayerUpdateTracker playerUpdateTracker2 = this.updateTrackers.get(i);
            String string = playerUpdateTracker2.player.getName().getString();
            if (playerUpdateTracker2.player.removed || !playerUpdateTracker2.player.inventory.contains(stack) && !stack.isInFrame()) {
                this.updateTrackersByPlayer.remove(playerUpdateTracker2.player);
                this.updateTrackers.remove(playerUpdateTracker2);
                this.icons.remove(string);
                continue;
            }
            if (stack.isInFrame() || playerUpdateTracker2.player.world.getRegistryKey() != this.dimension || !this.showIcons) continue;
            this.addIcon(MapIcon.Type.PLAYER, playerUpdateTracker2.player.world, string, playerUpdateTracker2.player.getX(), playerUpdateTracker2.player.getZ(), playerUpdateTracker2.player.yaw, null);
        }
        if (stack.isInFrame() && this.showIcons) {
            ItemFrameEntity itemFrameEntity = stack.getFrame();
            BlockPos blockPos = itemFrameEntity.getDecorationBlockPos();
            MapFrameMarker mapFrameMarker = this.frames.get(MapFrameMarker.getKey(blockPos));
            if (mapFrameMarker != null && itemFrameEntity.getEntityId() != mapFrameMarker.getEntityId() && this.frames.containsKey(mapFrameMarker.getKey())) {
                this.icons.remove("frame-" + mapFrameMarker.getEntityId());
            }
            MapFrameMarker mapFrameMarker2 = new MapFrameMarker(blockPos, itemFrameEntity.getHorizontalFacing().getHorizontal() * 90, itemFrameEntity.getEntityId());
            this.addIcon(MapIcon.Type.FRAME, player.world, "frame-" + itemFrameEntity.getEntityId(), blockPos.getX(), blockPos.getZ(), itemFrameEntity.getHorizontalFacing().getHorizontal() * 90, null);
            this.frames.put(mapFrameMarker2.getKey(), mapFrameMarker2);
        }
        if ((nbtCompound = stack.getTag()) != null && nbtCompound.contains("Decorations", 9)) {
            NbtList nbtList = nbtCompound.getList("Decorations", 10);
            for (int j = 0; j < nbtList.size(); ++j) {
                NbtCompound nbtCompound2 = nbtList.getCompound(j);
                if (this.icons.containsKey(nbtCompound2.getString("id"))) continue;
                this.addIcon(MapIcon.Type.byId(nbtCompound2.getByte("type")), player.world, nbtCompound2.getString("id"), nbtCompound2.getDouble("x"), nbtCompound2.getDouble("z"), nbtCompound2.getDouble("rot"), null);
            }
        }
    }

    public static void addDecorationsNbt(ItemStack stack, BlockPos pos, String id, MapIcon.Type type) {
        NbtList nbtList;
        if (stack.hasTag() && stack.getTag().contains("Decorations", 9)) {
            nbtList = stack.getTag().getList("Decorations", 10);
        } else {
            nbtList = new NbtList();
            stack.putSubTag("Decorations", nbtList);
        }
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putByte("type", type.getId());
        nbtCompound.putString("id", id);
        nbtCompound.putDouble("x", pos.getX());
        nbtCompound.putDouble("z", pos.getZ());
        nbtCompound.putDouble("rot", 180.0);
        nbtList.add(nbtCompound);
        if (type.hasTintColor()) {
            NbtCompound nbtCompound2 = stack.getOrCreateSubTag("display");
            nbtCompound2.putInt("MapColor", type.getTintColor());
        }
    }

    private void addIcon(MapIcon.Type type, @Nullable WorldAccess world, String key, double x, double z, double rotation, @Nullable Text text) {
        byte d;
        int i = 1 << this.scale;
        float f = (float)(x - (double)this.centerX) / (float)i;
        float g = (float)(z - (double)this.centerZ) / (float)i;
        byte b = (byte)((double)(f * 2.0f) + 0.5);
        byte c = (byte)((double)(g * 2.0f) + 0.5);
        int j = 63;
        if (f >= -63.0f && g >= -63.0f && f <= 63.0f && g <= 63.0f) {
            d = (byte)((rotation += rotation < 0.0 ? -8.0 : 8.0) * 16.0 / 360.0);
            if (this.dimension == World.NETHER && world != null) {
                int k = (int)(world.getLevelProperties().getTimeOfDay() / 10L);
                d = (byte)(k * k * 34187121 + k * 121 >> 15 & 0xF);
            }
        } else if (type == MapIcon.Type.PLAYER) {
            int k = 320;
            if (Math.abs(f) < 320.0f && Math.abs(g) < 320.0f) {
                type = MapIcon.Type.PLAYER_OFF_MAP;
            } else if (this.unlimitedTracking) {
                type = MapIcon.Type.PLAYER_OFF_LIMITS;
            } else {
                this.icons.remove(key);
                return;
            }
            d = 0;
            if (f <= -63.0f) {
                b = -128;
            }
            if (g <= -63.0f) {
                c = -128;
            }
            if (f >= 63.0f) {
                b = 127;
            }
            if (g >= 63.0f) {
                c = 127;
            }
        } else {
            this.icons.remove(key);
            return;
        }
        this.icons.put(key, new MapIcon(type, b, c, d, text));
    }

    @Nullable
    public Packet<?> getPlayerMarkerPacket(ItemStack map, BlockView world, PlayerEntity pos) {
        PlayerUpdateTracker playerUpdateTracker = this.updateTrackersByPlayer.get(pos);
        if (playerUpdateTracker == null) {
            return null;
        }
        return playerUpdateTracker.getPacket(map);
    }

    public void markDirty(int x, int z) {
        this.markDirty();
        for (PlayerUpdateTracker playerUpdateTracker : this.updateTrackers) {
            playerUpdateTracker.markDirty(x, z);
        }
    }

    public PlayerUpdateTracker getPlayerSyncData(PlayerEntity player) {
        PlayerUpdateTracker playerUpdateTracker = this.updateTrackersByPlayer.get(player);
        if (playerUpdateTracker == null) {
            playerUpdateTracker = new PlayerUpdateTracker(player);
            this.updateTrackersByPlayer.put(player, playerUpdateTracker);
            this.updateTrackers.add(playerUpdateTracker);
        }
        return playerUpdateTracker;
    }

    public void addBanner(WorldAccess world, BlockPos pos) {
        double d = (double)pos.getX() + 0.5;
        double e = (double)pos.getZ() + 0.5;
        int i = 1 << this.scale;
        double f = (d - (double)this.centerX) / (double)i;
        double g = (e - (double)this.centerZ) / (double)i;
        int j = 63;
        boolean bl = false;
        if (f >= -63.0 && g >= -63.0 && f <= 63.0 && g <= 63.0) {
            MapBannerMarker mapBannerMarker = MapBannerMarker.fromWorldBlock(world, pos);
            if (mapBannerMarker == null) {
                return;
            }
            boolean bl2 = true;
            if (this.banners.containsKey(mapBannerMarker.getKey()) && this.banners.get(mapBannerMarker.getKey()).equals(mapBannerMarker)) {
                this.banners.remove(mapBannerMarker.getKey());
                this.icons.remove(mapBannerMarker.getKey());
                bl2 = false;
                bl = true;
            }
            if (bl2) {
                this.banners.put(mapBannerMarker.getKey(), mapBannerMarker);
                this.addIcon(mapBannerMarker.getIconType(), world, mapBannerMarker.getKey(), d, e, 180.0, mapBannerMarker.getName());
                bl = true;
            }
            if (bl) {
                this.markDirty();
            }
        }
    }

    public void removeBanner(BlockView world, int x, int z) {
        Iterator<MapBannerMarker> iterator = this.banners.values().iterator();
        while (iterator.hasNext()) {
            MapBannerMarker mapBannerMarker2;
            MapBannerMarker mapBannerMarker = iterator.next();
            if (mapBannerMarker.getPos().getX() != x || mapBannerMarker.getPos().getZ() != z || mapBannerMarker.equals(mapBannerMarker2 = MapBannerMarker.fromWorldBlock(world, mapBannerMarker.getPos()))) continue;
            iterator.remove();
            this.icons.remove(mapBannerMarker.getKey());
        }
    }

    public void removeFrame(BlockPos pos, int id) {
        this.icons.remove("frame-" + id);
        this.frames.remove(MapFrameMarker.getKey(pos));
    }

    public class PlayerUpdateTracker {
        public final PlayerEntity player;
        private boolean dirty = true;
        private int startX;
        private int startZ;
        private int endX = 127;
        private int endZ = 127;
        private int emptyPacketsRequested;
        public int field_131;

        public PlayerUpdateTracker(PlayerEntity playerEntity) {
            this.player = playerEntity;
        }

        @Nullable
        public Packet<?> getPacket(ItemStack stack) {
            if (this.dirty) {
                this.dirty = false;
                return new MapUpdateS2CPacket(FilledMapItem.getMapId(stack), MapState.this.scale, MapState.this.showIcons, MapState.this.locked, MapState.this.icons.values(), MapState.this.colors, this.startX, this.startZ, this.endX + 1 - this.startX, this.endZ + 1 - this.startZ);
            }
            if (this.emptyPacketsRequested++ % 5 == 0) {
                return new MapUpdateS2CPacket(FilledMapItem.getMapId(stack), MapState.this.scale, MapState.this.showIcons, MapState.this.locked, MapState.this.icons.values(), MapState.this.colors, 0, 0, 0, 0);
            }
            return null;
        }

        public void markDirty(int x, int z) {
            if (this.dirty) {
                this.startX = Math.min(this.startX, x);
                this.startZ = Math.min(this.startZ, z);
                this.endX = Math.max(this.endX, x);
                this.endZ = Math.max(this.endZ, z);
            } else {
                this.dirty = true;
                this.startX = x;
                this.startZ = z;
                this.endX = x;
                this.endZ = z;
            }
        }
    }
}

