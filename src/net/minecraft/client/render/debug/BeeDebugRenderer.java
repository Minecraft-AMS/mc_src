/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.debug.NameGenerator;
import net.minecraft.client.render.debug.PathfindingDebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BeeDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;
    private final Map<BlockPos, Hive> hives = Maps.newHashMap();
    private final Map<UUID, Bee> bees = Maps.newHashMap();
    private UUID targetedEntity;

    public BeeDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void clear() {
        this.hives.clear();
        this.bees.clear();
        this.targetedEntity = null;
    }

    public void addHive(Hive hive) {
        this.hives.put(hive.pos, hive);
    }

    public void addBee(Bee bee) {
        this.bees.put(bee.uuid, bee);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        this.removeOutdatedHives();
        this.removeInvalidBees();
        this.render();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
        if (!this.client.player.isSpectator()) {
            this.updateTargetedEntity();
        }
    }

    private void removeInvalidBees() {
        this.bees.entrySet().removeIf(entry -> this.client.world.getEntityById(((Bee)entry.getValue()).id) == null);
    }

    private void removeOutdatedHives() {
        long l = this.client.world.getTime() - 20L;
        this.hives.entrySet().removeIf(entry -> ((Hive)entry.getValue()).time < l);
    }

    private void render() {
        BlockPos blockPos = this.getCameraPos().getBlockPos();
        this.bees.values().forEach(bee -> {
            if (this.isInRange((Bee)bee)) {
                this.drawBee((Bee)bee);
            }
        });
        this.drawFlowers();
        for (BlockPos blockPos22 : this.hives.keySet()) {
            if (!blockPos.isWithinDistance(blockPos22, 30.0)) continue;
            BeeDebugRenderer.drawHive(blockPos22);
        }
        Map<BlockPos, Set<UUID>> map = this.getBlacklistingBees();
        this.hives.values().forEach(hive -> {
            if (blockPos.isWithinDistance(hive.pos, 30.0)) {
                Set set = (Set)map.get(hive.pos);
                this.drawHiveInfo((Hive)hive, set == null ? Sets.newHashSet() : set);
            }
        });
        this.getBeesByHive().forEach((blockPos2, list) -> {
            if (blockPos.isWithinDistance((Vec3i)blockPos2, 30.0)) {
                this.drawHiveBees((BlockPos)blockPos2, (List<String>)list);
            }
        });
    }

    private Map<BlockPos, Set<UUID>> getBlacklistingBees() {
        HashMap map = Maps.newHashMap();
        this.bees.values().forEach(bee -> bee.blacklistedHives.forEach(blockPos -> BeeDebugRenderer.addToMap(map, bee, blockPos)));
        return map;
    }

    private void drawFlowers() {
        HashMap map = Maps.newHashMap();
        this.bees.values().stream().filter(Bee::hasFlower).forEach(bee -> {
            Set set = (Set)map.get(bee.flowerPos);
            if (set == null) {
                set = Sets.newHashSet();
                map.put(bee.flowerPos, set);
            }
            set.add(bee.getUuid());
        });
        map.entrySet().forEach(entry -> {
            BlockPos blockPos = (BlockPos)entry.getKey();
            Set set = (Set)entry.getValue();
            Set set2 = set.stream().map(NameGenerator::name).collect(Collectors.toSet());
            int i = 1;
            BeeDebugRenderer.drawString(set2.toString(), blockPos, i++, -256);
            BeeDebugRenderer.drawString("Flower", blockPos, i++, -1);
            float f = 0.05f;
            BeeDebugRenderer.drawBox(blockPos, 0.05f, 0.8f, 0.8f, 0.0f, 0.3f);
        });
    }

    private static String toString(Collection<UUID> bees) {
        if (bees.isEmpty()) {
            return "-";
        }
        if (bees.size() > 3) {
            return "" + bees.size() + " bees";
        }
        return bees.stream().map(NameGenerator::name).collect(Collectors.toSet()).toString();
    }

    private static void addToMap(Map<BlockPos, Set<UUID>> map, Bee arg, BlockPos pos) {
        HashSet set = map.get(pos);
        if (set == null) {
            set = Sets.newHashSet();
            map.put(pos, set);
        }
        set.add(arg.getUuid());
    }

    private static void drawHive(BlockPos pos) {
        float f = 0.05f;
        BeeDebugRenderer.drawBox(pos, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
    }

    private void drawHiveBees(BlockPos pos, List<String> bees) {
        float f = 0.05f;
        BeeDebugRenderer.drawBox(pos, 0.05f, 0.2f, 0.2f, 1.0f, 0.3f);
        BeeDebugRenderer.drawString("" + bees, pos, 0, -256);
        BeeDebugRenderer.drawString("Ghost Hive", pos, 1, -65536);
    }

    private static void drawBox(BlockPos pos, float expand, float red, float green, float blue, float alpha) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        DebugRenderer.drawBox(pos, expand, red, green, blue, alpha);
    }

    private void drawHiveInfo(Hive hive, Collection<UUID> blacklistingBees) {
        int i = 0;
        if (!blacklistingBees.isEmpty()) {
            BeeDebugRenderer.drawString("Blacklisted by " + BeeDebugRenderer.toString(blacklistingBees), hive, i++, -65536);
        }
        BeeDebugRenderer.drawString("Out: " + BeeDebugRenderer.toString(this.getBeesForHive(hive.pos)), hive, i++, -3355444);
        if (hive.beeCount == 0) {
            BeeDebugRenderer.drawString("In: -", hive, i++, -256);
        } else if (hive.beeCount == 1) {
            BeeDebugRenderer.drawString("In: 1 bee", hive, i++, -256);
        } else {
            BeeDebugRenderer.drawString("In: " + hive.beeCount + " bees", hive, i++, -256);
        }
        BeeDebugRenderer.drawString("Honey: " + hive.honeyLevel, hive, i++, -23296);
        BeeDebugRenderer.drawString(hive.field_21544 + (hive.sedated ? " (sedated)" : ""), hive, i++, -1);
    }

    private void drawPath(Bee bee) {
        if (bee.path != null) {
            PathfindingDebugRenderer.drawPath(bee.path, 0.5f, false, false, this.getCameraPos().getPos().getX(), this.getCameraPos().getPos().getY(), this.getCameraPos().getPos().getZ());
        }
    }

    private void drawBee(Bee bee) {
        boolean bl = this.isTargeted(bee);
        int i = 0;
        BeeDebugRenderer.drawString(bee.pos, i++, bee.toString(), -1, 0.03f);
        if (bee.hivePos == null) {
            BeeDebugRenderer.drawString(bee.pos, i++, "No hive", -98404, 0.02f);
        } else {
            BeeDebugRenderer.drawString(bee.pos, i++, "Hive: " + this.getPositionString(bee, bee.hivePos), -256, 0.02f);
        }
        if (bee.flowerPos == null) {
            BeeDebugRenderer.drawString(bee.pos, i++, "No flower", -98404, 0.02f);
        } else {
            BeeDebugRenderer.drawString(bee.pos, i++, "Flower: " + this.getPositionString(bee, bee.flowerPos), -256, 0.02f);
        }
        for (String string : bee.field_21542) {
            BeeDebugRenderer.drawString(bee.pos, i++, string, -16711936, 0.02f);
        }
        if (bl) {
            this.drawPath(bee);
        }
        if (bee.travellingTicks > 0) {
            int j = bee.travellingTicks < 600 ? -3355444 : -23296;
            BeeDebugRenderer.drawString(bee.pos, i++, "Travelling: " + bee.travellingTicks + " ticks", j, 0.02f);
        }
    }

    private static void drawString(String string, Hive hive, int line, int color) {
        BlockPos blockPos = hive.pos;
        BeeDebugRenderer.drawString(string, blockPos, line, color);
    }

    private static void drawString(String string, BlockPos pos, int line, int color) {
        double d = 1.3;
        double e = 0.2;
        double f = (double)pos.getX() + 0.5;
        double g = (double)pos.getY() + 1.3 + (double)line * 0.2;
        double h = (double)pos.getZ() + 0.5;
        DebugRenderer.drawString(string, f, g, h, color, 0.02f, true, 0.0f, true);
    }

    private static void drawString(Position pos, int line, String string, int color, float size) {
        double d = 2.4;
        double e = 0.25;
        BlockPos blockPos = new BlockPos(pos);
        double f = (double)blockPos.getX() + 0.5;
        double g = pos.getY() + 2.4 + (double)line * 0.25;
        double h = (double)blockPos.getZ() + 0.5;
        float i = 0.5f;
        DebugRenderer.drawString(string, f, g, h, color, size, false, 0.5f, true);
    }

    private Camera getCameraPos() {
        return this.client.gameRenderer.getCamera();
    }

    private String getPositionString(Bee bee, BlockPos pos) {
        float f = MathHelper.sqrt(pos.getSquaredDistance(bee.pos.getX(), bee.pos.getY(), bee.pos.getZ(), true));
        double d = (double)Math.round(f * 10.0f) / 10.0;
        return pos.toShortString() + " (dist " + d + ")";
    }

    private boolean isTargeted(Bee bee) {
        return Objects.equals(this.targetedEntity, bee.uuid);
    }

    private boolean isInRange(Bee bee) {
        ClientPlayerEntity playerEntity = this.client.player;
        BlockPos blockPos = new BlockPos(playerEntity.getX(), bee.pos.getY(), playerEntity.getZ());
        BlockPos blockPos2 = new BlockPos(bee.pos);
        return blockPos.isWithinDistance(blockPos2, 30.0);
    }

    private Collection<UUID> getBeesForHive(BlockPos hivePos) {
        return this.bees.values().stream().filter(bee -> bee.isHive(hivePos)).map(Bee::getUuid).collect(Collectors.toSet());
    }

    private Map<BlockPos, List<String>> getBeesByHive() {
        HashMap map = Maps.newHashMap();
        for (Bee bee : this.bees.values()) {
            if (bee.hivePos == null || this.hives.containsKey(bee.hivePos)) continue;
            List list = (List)map.get(bee.hivePos);
            if (list == null) {
                list = Lists.newArrayList();
                map.put(bee.hivePos, list);
            }
            list.add(bee.getName());
        }
        return map;
    }

    private void updateTargetedEntity() {
        DebugRenderer.getTargetedEntity(this.client.getCameraEntity(), 8).ifPresent(entity -> {
            this.targetedEntity = entity.getUuid();
        });
    }

    @Environment(value=EnvType.CLIENT)
    public static class Bee {
        public final UUID uuid;
        public final int id;
        public final Position pos;
        @Nullable
        public final Path path;
        @Nullable
        public final BlockPos hivePos;
        @Nullable
        public final BlockPos flowerPos;
        public final int travellingTicks;
        public final List<String> field_21542 = Lists.newArrayList();
        public final Set<BlockPos> blacklistedHives = Sets.newHashSet();

        public Bee(UUID uuid, int id, Position pos, Path path, BlockPos hivePos, BlockPos flowerPos, int travellingTicks) {
            this.uuid = uuid;
            this.id = id;
            this.pos = pos;
            this.path = path;
            this.hivePos = hivePos;
            this.flowerPos = flowerPos;
            this.travellingTicks = travellingTicks;
        }

        public boolean isHive(BlockPos pos) {
            return this.hivePos != null && this.hivePos.equals(pos);
        }

        public UUID getUuid() {
            return this.uuid;
        }

        public String getName() {
            return NameGenerator.name(this.uuid);
        }

        public String toString() {
            return this.getName();
        }

        public boolean hasFlower() {
            return this.flowerPos != null;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Hive {
        public final BlockPos pos;
        public final String field_21544;
        public final int beeCount;
        public final int honeyLevel;
        public final boolean sedated;
        public final long time;

        public Hive(BlockPos pos, String string, int beeCount, int honeyLevel, boolean sedated, long time) {
            this.pos = pos;
            this.field_21544 = string;
            this.beeCount = beeCount;
            this.honeyLevel = honeyLevel;
            this.sedated = sedated;
            this.time = time;
        }
    }
}

