/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ContiguousSet
 *  com.google.common.collect.DiscreteDomain
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Range
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.entity.boss.dragon;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonSpawnState;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.predicate.block.BlockPredicate;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.feature.EndConfiguredFeatures;
import net.minecraft.world.gen.feature.EndPortalFeature;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class EnderDragonFight {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int CHECK_DRAGON_SEEN_INTERVAL = 1200;
    private static final int CRYSTAL_COUNTING_INTERVAL = 100;
    private static final int field_31445 = 20;
    private static final int ISLAND_SIZE = 8;
    public static final int field_31441 = 9;
    private static final int PLAYER_COUNTING_INTERVAL = 20;
    private static final int field_31448 = 96;
    public static final int SPAWN_Y = 128;
    private static final Predicate<Entity> VALID_ENTITY = EntityPredicates.VALID_ENTITY.and(EntityPredicates.maxDistance(0.0, 128.0, 0.0, 192.0));
    private final ServerBossBar bossBar = (ServerBossBar)new ServerBossBar(Text.translatable("entity.minecraft.ender_dragon"), BossBar.Color.PINK, BossBar.Style.PROGRESS).setDragonMusic(true).setThickenFog(true);
    private final ServerWorld world;
    private final ObjectArrayList<Integer> gateways = new ObjectArrayList();
    private final BlockPattern endPortalPattern;
    private int dragonSeenTimer;
    private int endCrystalsAlive;
    private int crystalCountTimer;
    private int playerUpdateTimer;
    private boolean dragonKilled;
    private boolean previouslyKilled;
    @Nullable
    private UUID dragonUuid;
    private boolean doLegacyCheck = true;
    @Nullable
    private BlockPos exitPortalLocation;
    @Nullable
    private EnderDragonSpawnState dragonSpawnState;
    private int spawnStateTimer;
    @Nullable
    private List<EndCrystalEntity> crystals;

    public EnderDragonFight(ServerWorld world, long gatewaysSeed, NbtCompound nbt) {
        this.world = world;
        if (nbt.contains("NeedsStateScanning")) {
            this.doLegacyCheck = nbt.getBoolean("NeedsStateScanning");
        }
        if (nbt.contains("DragonKilled", 99)) {
            if (nbt.containsUuid("Dragon")) {
                this.dragonUuid = nbt.getUuid("Dragon");
            }
            this.dragonKilled = nbt.getBoolean("DragonKilled");
            this.previouslyKilled = nbt.getBoolean("PreviouslyKilled");
            if (nbt.getBoolean("IsRespawning")) {
                this.dragonSpawnState = EnderDragonSpawnState.START;
            }
            if (nbt.contains("ExitPortalLocation", 10)) {
                this.exitPortalLocation = NbtHelper.toBlockPos(nbt.getCompound("ExitPortalLocation"));
            }
        } else {
            this.dragonKilled = true;
            this.previouslyKilled = true;
        }
        if (nbt.contains("Gateways", 9)) {
            NbtList nbtList = nbt.getList("Gateways", 3);
            for (int i = 0; i < nbtList.size(); ++i) {
                this.gateways.add((Object)nbtList.getInt(i));
            }
        } else {
            this.gateways.addAll((Collection)ContiguousSet.create((Range)Range.closedOpen((Comparable)Integer.valueOf(0), (Comparable)Integer.valueOf(20)), (DiscreteDomain)DiscreteDomain.integers()));
            Util.shuffle(this.gateways, Random.create(gatewaysSeed));
        }
        this.endPortalPattern = BlockPatternBuilder.start().aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("  ###  ", " #   # ", "#     #", "#  #  #", "#     #", " #   # ", "  ###  ").aisle("       ", "  ###  ", " ##### ", " ##### ", " ##### ", "  ###  ", "       ").where('#', CachedBlockPosition.matchesBlockState(BlockPredicate.make(Blocks.BEDROCK))).build();
    }

    public NbtCompound toNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putBoolean("NeedsStateScanning", this.doLegacyCheck);
        if (this.dragonUuid != null) {
            nbtCompound.putUuid("Dragon", this.dragonUuid);
        }
        nbtCompound.putBoolean("DragonKilled", this.dragonKilled);
        nbtCompound.putBoolean("PreviouslyKilled", this.previouslyKilled);
        if (this.exitPortalLocation != null) {
            nbtCompound.put("ExitPortalLocation", NbtHelper.fromBlockPos(this.exitPortalLocation));
        }
        NbtList nbtList = new NbtList();
        ObjectListIterator objectListIterator = this.gateways.iterator();
        while (objectListIterator.hasNext()) {
            int i = (Integer)objectListIterator.next();
            nbtList.add(NbtInt.of(i));
        }
        nbtCompound.put("Gateways", nbtList);
        return nbtCompound;
    }

    public void tick() {
        this.bossBar.setVisible(!this.dragonKilled);
        if (++this.playerUpdateTimer >= 20) {
            this.updatePlayers();
            this.playerUpdateTimer = 0;
        }
        if (!this.bossBar.getPlayers().isEmpty()) {
            this.world.getChunkManager().addTicket(ChunkTicketType.DRAGON, new ChunkPos(0, 0), 9, Unit.INSTANCE);
            boolean bl = this.loadChunks();
            if (this.doLegacyCheck && bl) {
                this.convertFromLegacy();
                this.doLegacyCheck = false;
            }
            if (this.dragonSpawnState != null) {
                if (this.crystals == null && bl) {
                    this.dragonSpawnState = null;
                    this.respawnDragon();
                }
                this.dragonSpawnState.run(this.world, this, this.crystals, this.spawnStateTimer++, this.exitPortalLocation);
            }
            if (!this.dragonKilled) {
                if ((this.dragonUuid == null || ++this.dragonSeenTimer >= 1200) && bl) {
                    this.checkDragonSeen();
                    this.dragonSeenTimer = 0;
                }
                if (++this.crystalCountTimer >= 100 && bl) {
                    this.countAliveCrystals();
                    this.crystalCountTimer = 0;
                }
            }
        } else {
            this.world.getChunkManager().removeTicket(ChunkTicketType.DRAGON, new ChunkPos(0, 0), 9, Unit.INSTANCE);
        }
    }

    private void convertFromLegacy() {
        LOGGER.info("Scanning for legacy world dragon fight...");
        boolean bl = this.worldContainsEndPortal();
        if (bl) {
            LOGGER.info("Found that the dragon has been killed in this world already.");
            this.previouslyKilled = true;
        } else {
            LOGGER.info("Found that the dragon has not yet been killed in this world.");
            this.previouslyKilled = false;
            if (this.findEndPortal() == null) {
                this.generateEndPortal(false);
            }
        }
        List<? extends EnderDragonEntity> list = this.world.getAliveEnderDragons();
        if (list.isEmpty()) {
            this.dragonKilled = true;
        } else {
            EnderDragonEntity enderDragonEntity = list.get(0);
            this.dragonUuid = enderDragonEntity.getUuid();
            LOGGER.info("Found that there's a dragon still alive ({})", (Object)enderDragonEntity);
            this.dragonKilled = false;
            if (!bl) {
                LOGGER.info("But we didn't have a portal, let's remove it.");
                enderDragonEntity.discard();
                this.dragonUuid = null;
            }
        }
        if (!this.previouslyKilled && this.dragonKilled) {
            this.dragonKilled = false;
        }
    }

    private void checkDragonSeen() {
        List<? extends EnderDragonEntity> list = this.world.getAliveEnderDragons();
        if (list.isEmpty()) {
            LOGGER.debug("Haven't seen the dragon, respawning it");
            this.createDragon();
        } else {
            LOGGER.debug("Haven't seen our dragon, but found another one to use.");
            this.dragonUuid = list.get(0).getUuid();
        }
    }

    protected void setSpawnState(EnderDragonSpawnState spawnState) {
        if (this.dragonSpawnState == null) {
            throw new IllegalStateException("Dragon respawn isn't in progress, can't skip ahead in the animation.");
        }
        this.spawnStateTimer = 0;
        if (spawnState == EnderDragonSpawnState.END) {
            this.dragonSpawnState = null;
            this.dragonKilled = false;
            EnderDragonEntity enderDragonEntity = this.createDragon();
            for (ServerPlayerEntity serverPlayerEntity : this.bossBar.getPlayers()) {
                Criteria.SUMMONED_ENTITY.trigger(serverPlayerEntity, enderDragonEntity);
            }
        } else {
            this.dragonSpawnState = spawnState;
        }
    }

    private boolean worldContainsEndPortal() {
        for (int i = -8; i <= 8; ++i) {
            for (int j = -8; j <= 8; ++j) {
                WorldChunk worldChunk = this.world.getChunk(i, j);
                for (BlockEntity blockEntity : worldChunk.getBlockEntities().values()) {
                    if (!(blockEntity instanceof EndPortalBlockEntity)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    private BlockPattern.Result findEndPortal() {
        int j;
        int i;
        for (i = -8; i <= 8; ++i) {
            for (j = -8; j <= 8; ++j) {
                WorldChunk worldChunk = this.world.getChunk(i, j);
                for (BlockEntity blockEntity : worldChunk.getBlockEntities().values()) {
                    BlockPattern.Result result;
                    if (!(blockEntity instanceof EndPortalBlockEntity) || (result = this.endPortalPattern.searchAround(this.world, blockEntity.getPos())) == null) continue;
                    BlockPos blockPos = result.translate(3, 3, 3).getBlockPos();
                    if (this.exitPortalLocation == null) {
                        this.exitPortalLocation = blockPos;
                    }
                    return result;
                }
            }
        }
        for (j = i = this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, EndPortalFeature.ORIGIN).getY(); j >= this.world.getBottomY(); --j) {
            BlockPattern.Result result2 = this.endPortalPattern.searchAround(this.world, new BlockPos(EndPortalFeature.ORIGIN.getX(), j, EndPortalFeature.ORIGIN.getZ()));
            if (result2 == null) continue;
            if (this.exitPortalLocation == null) {
                this.exitPortalLocation = result2.translate(3, 3, 3).getBlockPos();
            }
            return result2;
        }
        return null;
    }

    private boolean loadChunks() {
        for (int i = -8; i <= 8; ++i) {
            for (int j = 8; j <= 8; ++j) {
                Chunk chunk = this.world.getChunk(i, j, ChunkStatus.FULL, false);
                if (!(chunk instanceof WorldChunk)) {
                    return false;
                }
                ChunkHolder.LevelType levelType = ((WorldChunk)chunk).getLevelType();
                if (levelType.isAfter(ChunkHolder.LevelType.TICKING)) continue;
                return false;
            }
        }
        return true;
    }

    private void updatePlayers() {
        HashSet set = Sets.newHashSet();
        for (ServerPlayerEntity serverPlayerEntity : this.world.getPlayers(VALID_ENTITY)) {
            this.bossBar.addPlayer(serverPlayerEntity);
            set.add(serverPlayerEntity);
        }
        HashSet set2 = Sets.newHashSet(this.bossBar.getPlayers());
        set2.removeAll(set);
        for (ServerPlayerEntity serverPlayerEntity2 : set2) {
            this.bossBar.removePlayer(serverPlayerEntity2);
        }
    }

    private void countAliveCrystals() {
        this.crystalCountTimer = 0;
        this.endCrystalsAlive = 0;
        for (EndSpikeFeature.Spike spike : EndSpikeFeature.getSpikes(this.world)) {
            this.endCrystalsAlive += this.world.getNonSpectatingEntities(EndCrystalEntity.class, spike.getBoundingBox()).size();
        }
        LOGGER.debug("Found {} end crystals still alive", (Object)this.endCrystalsAlive);
    }

    public void dragonKilled(EnderDragonEntity dragon) {
        if (dragon.getUuid().equals(this.dragonUuid)) {
            this.bossBar.setPercent(0.0f);
            this.bossBar.setVisible(false);
            this.generateEndPortal(true);
            this.generateNewEndGateway();
            if (!this.previouslyKilled) {
                this.world.setBlockState(this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, EndPortalFeature.ORIGIN), Blocks.DRAGON_EGG.getDefaultState());
            }
            this.previouslyKilled = true;
            this.dragonKilled = true;
        }
    }

    private void generateNewEndGateway() {
        if (this.gateways.isEmpty()) {
            return;
        }
        int i = (Integer)this.gateways.remove(this.gateways.size() - 1);
        int j = MathHelper.floor(96.0 * Math.cos(2.0 * (-Math.PI + 0.15707963267948966 * (double)i)));
        int k = MathHelper.floor(96.0 * Math.sin(2.0 * (-Math.PI + 0.15707963267948966 * (double)i)));
        this.generateEndGateway(new BlockPos(j, 75, k));
    }

    private void generateEndGateway(BlockPos pos) {
        this.world.syncWorldEvent(3000, pos, 0);
        EndConfiguredFeatures.END_GATEWAY_DELAYED.value().generate(this.world, this.world.getChunkManager().getChunkGenerator(), Random.create(), pos);
    }

    private void generateEndPortal(boolean previouslyKilled) {
        EndPortalFeature endPortalFeature = new EndPortalFeature(previouslyKilled);
        if (this.exitPortalLocation == null) {
            this.exitPortalLocation = this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.ORIGIN).down();
            while (this.world.getBlockState(this.exitPortalLocation).isOf(Blocks.BEDROCK) && this.exitPortalLocation.getY() > this.world.getSeaLevel()) {
                this.exitPortalLocation = this.exitPortalLocation.down();
            }
        }
        endPortalFeature.generateIfValid(FeatureConfig.DEFAULT, this.world, this.world.getChunkManager().getChunkGenerator(), Random.create(), this.exitPortalLocation);
    }

    private EnderDragonEntity createDragon() {
        this.world.getWorldChunk(new BlockPos(0, 128, 0));
        EnderDragonEntity enderDragonEntity = EntityType.ENDER_DRAGON.create(this.world);
        enderDragonEntity.getPhaseManager().setPhase(PhaseType.HOLDING_PATTERN);
        enderDragonEntity.refreshPositionAndAngles(0.0, 128.0, 0.0, this.world.random.nextFloat() * 360.0f, 0.0f);
        this.world.spawnEntity(enderDragonEntity);
        this.dragonUuid = enderDragonEntity.getUuid();
        return enderDragonEntity;
    }

    public void updateFight(EnderDragonEntity dragon) {
        if (dragon.getUuid().equals(this.dragonUuid)) {
            this.bossBar.setPercent(dragon.getHealth() / dragon.getMaxHealth());
            this.dragonSeenTimer = 0;
            if (dragon.hasCustomName()) {
                this.bossBar.setName(dragon.getDisplayName());
            }
        }
    }

    public int getAliveEndCrystals() {
        return this.endCrystalsAlive;
    }

    public void crystalDestroyed(EndCrystalEntity enderCrystal, DamageSource source) {
        if (this.dragonSpawnState != null && this.crystals.contains(enderCrystal)) {
            LOGGER.debug("Aborting respawn sequence");
            this.dragonSpawnState = null;
            this.spawnStateTimer = 0;
            this.resetEndCrystals();
            this.generateEndPortal(true);
        } else {
            this.countAliveCrystals();
            Entity entity = this.world.getEntity(this.dragonUuid);
            if (entity instanceof EnderDragonEntity) {
                ((EnderDragonEntity)entity).crystalDestroyed(enderCrystal, enderCrystal.getBlockPos(), source);
            }
        }
    }

    public boolean hasPreviouslyKilled() {
        return this.previouslyKilled;
    }

    public void respawnDragon() {
        if (this.dragonKilled && this.dragonSpawnState == null) {
            BlockPos blockPos = this.exitPortalLocation;
            if (blockPos == null) {
                LOGGER.debug("Tried to respawn, but need to find the portal first.");
                BlockPattern.Result result = this.findEndPortal();
                if (result == null) {
                    LOGGER.debug("Couldn't find a portal, so we made one.");
                    this.generateEndPortal(true);
                } else {
                    LOGGER.debug("Found the exit portal & saved its location for next time.");
                }
                blockPos = this.exitPortalLocation;
            }
            ArrayList list = Lists.newArrayList();
            BlockPos blockPos2 = blockPos.up(1);
            for (Direction direction : Direction.Type.HORIZONTAL) {
                List<EndCrystalEntity> list2 = this.world.getNonSpectatingEntities(EndCrystalEntity.class, new Box(blockPos2.offset(direction, 2)));
                if (list2.isEmpty()) {
                    return;
                }
                list.addAll(list2);
            }
            LOGGER.debug("Found all crystals, respawning dragon.");
            this.respawnDragon(list);
        }
    }

    private void respawnDragon(List<EndCrystalEntity> crystals) {
        if (this.dragonKilled && this.dragonSpawnState == null) {
            BlockPattern.Result result = this.findEndPortal();
            while (result != null) {
                for (int i = 0; i < this.endPortalPattern.getWidth(); ++i) {
                    for (int j = 0; j < this.endPortalPattern.getHeight(); ++j) {
                        for (int k = 0; k < this.endPortalPattern.getDepth(); ++k) {
                            CachedBlockPosition cachedBlockPosition = result.translate(i, j, k);
                            if (!cachedBlockPosition.getBlockState().isOf(Blocks.BEDROCK) && !cachedBlockPosition.getBlockState().isOf(Blocks.END_PORTAL)) continue;
                            this.world.setBlockState(cachedBlockPosition.getBlockPos(), Blocks.END_STONE.getDefaultState());
                        }
                    }
                }
                result = this.findEndPortal();
            }
            this.dragonSpawnState = EnderDragonSpawnState.START;
            this.spawnStateTimer = 0;
            this.generateEndPortal(false);
            this.crystals = crystals;
        }
    }

    public void resetEndCrystals() {
        for (EndSpikeFeature.Spike spike : EndSpikeFeature.getSpikes(this.world)) {
            List<EndCrystalEntity> list = this.world.getNonSpectatingEntities(EndCrystalEntity.class, spike.getBoundingBox());
            for (EndCrystalEntity endCrystalEntity : list) {
                endCrystalEntity.setInvulnerable(false);
                endCrystalEntity.setBeamTarget(null);
            }
        }
    }
}

