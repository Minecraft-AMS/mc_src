/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.item.map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

public class MapState
extends PersistentState {
    public int xCenter;
    public int zCenter;
    public DimensionType dimension;
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

    public MapState(String key) {
        super(key);
    }

    public void init(int x, int z, int scale, boolean showIcons, boolean unlimitedTracking, DimensionType dimension) {
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
        this.xCenter = j * i + i / 2 - 64;
        this.zCenter = k * i + i / 2 - 64;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        int i = tag.getInt("dimension");
        DimensionType dimensionType = DimensionType.byRawId(i);
        if (dimensionType == null) {
            throw new IllegalArgumentException("Invalid map dimension: " + i);
        }
        this.dimension = dimensionType;
        this.xCenter = tag.getInt("xCenter");
        this.zCenter = tag.getInt("zCenter");
        this.scale = (byte)MathHelper.clamp(tag.getByte("scale"), 0, 4);
        this.showIcons = !tag.contains("trackingPosition", 1) || tag.getBoolean("trackingPosition");
        this.unlimitedTracking = tag.getBoolean("unlimitedTracking");
        this.locked = tag.getBoolean("locked");
        this.colors = tag.getByteArray("colors");
        if (this.colors.length != 16384) {
            this.colors = new byte[16384];
        }
        ListTag listTag = tag.getList("banners", 10);
        for (int j = 0; j < listTag.size(); ++j) {
            MapBannerMarker mapBannerMarker = MapBannerMarker.fromNbt(listTag.getCompound(j));
            this.banners.put(mapBannerMarker.getKey(), mapBannerMarker);
            this.addIcon(mapBannerMarker.getIconType(), null, mapBannerMarker.getKey(), mapBannerMarker.getPos().getX(), mapBannerMarker.getPos().getZ(), 180.0, mapBannerMarker.getName());
        }
        ListTag listTag2 = tag.getList("frames", 10);
        for (int k = 0; k < listTag2.size(); ++k) {
            MapFrameMarker mapFrameMarker = MapFrameMarker.fromTag(listTag2.getCompound(k));
            this.frames.put(mapFrameMarker.getKey(), mapFrameMarker);
            this.addIcon(MapIcon.Type.FRAME, null, "frame-" + mapFrameMarker.getEntityId(), mapFrameMarker.getPos().getX(), mapFrameMarker.getPos().getZ(), mapFrameMarker.getRotation(), null);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt("dimension", this.dimension.getRawId());
        tag.putInt("xCenter", this.xCenter);
        tag.putInt("zCenter", this.zCenter);
        tag.putByte("scale", this.scale);
        tag.putByteArray("colors", this.colors);
        tag.putBoolean("trackingPosition", this.showIcons);
        tag.putBoolean("unlimitedTracking", this.unlimitedTracking);
        tag.putBoolean("locked", this.locked);
        ListTag listTag = new ListTag();
        for (MapBannerMarker mapBannerMarker : this.banners.values()) {
            listTag.add(mapBannerMarker.getNbt());
        }
        tag.put("banners", listTag);
        ListTag listTag2 = new ListTag();
        for (MapFrameMarker mapFrameMarker : this.frames.values()) {
            listTag2.add(mapFrameMarker.toTag());
        }
        tag.put("frames", listTag2);
        return tag;
    }

    public void copyFrom(MapState state) {
        this.locked = true;
        this.xCenter = state.xCenter;
        this.zCenter = state.zCenter;
        this.banners.putAll(state.banners);
        this.icons.putAll(state.icons);
        System.arraycopy(state.colors, 0, this.colors, 0, state.colors.length);
        this.markDirty();
    }

    public void update(PlayerEntity player, ItemStack stack) {
        CompoundTag compoundTag;
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
            if (stack.isInFrame() || playerUpdateTracker2.player.dimension != this.dimension || !this.showIcons) continue;
            this.addIcon(MapIcon.Type.PLAYER, playerUpdateTracker2.player.world, string, playerUpdateTracker2.player.x, playerUpdateTracker2.player.z, playerUpdateTracker2.player.yaw, null);
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
        if ((compoundTag = stack.getTag()) != null && compoundTag.contains("Decorations", 9)) {
            ListTag listTag = compoundTag.getList("Decorations", 10);
            for (int j = 0; j < listTag.size(); ++j) {
                CompoundTag compoundTag2 = listTag.getCompound(j);
                if (this.icons.containsKey(compoundTag2.getString("id"))) continue;
                this.addIcon(MapIcon.Type.byId(compoundTag2.getByte("type")), player.world, compoundTag2.getString("id"), compoundTag2.getDouble("x"), compoundTag2.getDouble("z"), compoundTag2.getDouble("rot"), null);
            }
        }
    }

    public static void addDecorationsTag(ItemStack stack, BlockPos pos, String id, MapIcon.Type type) {
        ListTag listTag;
        if (stack.hasTag() && stack.getTag().contains("Decorations", 9)) {
            listTag = stack.getTag().getList("Decorations", 10);
        } else {
            listTag = new ListTag();
            stack.putSubTag("Decorations", listTag);
        }
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putByte("type", type.getId());
        compoundTag.putString("id", id);
        compoundTag.putDouble("x", pos.getX());
        compoundTag.putDouble("z", pos.getZ());
        compoundTag.putDouble("rot", 180.0);
        listTag.add(compoundTag);
        if (type.hasTintColor()) {
            CompoundTag compoundTag2 = stack.getOrCreateSubTag("display");
            compoundTag2.putInt("MapColor", type.getTintColor());
        }
    }

    private void addIcon(MapIcon.Type type, @Nullable IWorld world, String key, double x, double z, double rotation, @Nullable Text text) {
        byte d;
        int i = 1 << this.scale;
        float f = (float)(x - (double)this.xCenter) / (float)i;
        float g = (float)(z - (double)this.zCenter) / (float)i;
        byte b = (byte)((double)(f * 2.0f) + 0.5);
        byte c = (byte)((double)(g * 2.0f) + 0.5);
        int j = 63;
        if (f >= -63.0f && g >= -63.0f && f <= 63.0f && g <= 63.0f) {
            d = (byte)((rotation += rotation < 0.0 ? -8.0 : 8.0) * 16.0 / 360.0);
            if (this.dimension == DimensionType.THE_NETHER && world != null) {
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

    public void addBanner(IWorld world, BlockPos pos) {
        float f = (float)pos.getX() + 0.5f;
        float g = (float)pos.getZ() + 0.5f;
        int i = 1 << this.scale;
        float h = (f - (float)this.xCenter) / (float)i;
        float j = (g - (float)this.zCenter) / (float)i;
        int k = 63;
        boolean bl = false;
        if (h >= -63.0f && j >= -63.0f && h <= 63.0f && j <= 63.0f) {
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
                this.addIcon(mapBannerMarker.getIconType(), world, mapBannerMarker.getKey(), f, g, 180.0, mapBannerMarker.getName());
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

