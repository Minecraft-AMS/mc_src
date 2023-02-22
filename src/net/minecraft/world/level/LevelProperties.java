/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.level;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtString;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.dynamic.RegistryReadingOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.SaveVersionInfo;
import net.minecraft.world.timer.Timer;
import net.minecraft.world.timer.TimerCallbackSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class LevelProperties
implements ServerWorldProperties,
SaveProperties {
    private static final Logger LOGGER = LogManager.getLogger();
    private LevelInfo levelInfo;
    private final GeneratorOptions generatorOptions;
    private final Lifecycle lifecycle;
    private int spawnX;
    private int spawnY;
    private int spawnZ;
    private float spawnAngle;
    private long time;
    private long timeOfDay;
    @Nullable
    private final DataFixer dataFixer;
    private final int dataVersion;
    private boolean playerDataLoaded;
    @Nullable
    private NbtCompound playerData;
    private final int version;
    private int clearWeatherTime;
    private boolean raining;
    private int rainTime;
    private boolean thundering;
    private int thunderTime;
    private boolean initialized;
    private boolean difficultyLocked;
    private WorldBorder.Properties worldBorder;
    private NbtCompound dragonFight;
    @Nullable
    private NbtCompound customBossEvents;
    private int wanderingTraderSpawnDelay;
    private int wanderingTraderSpawnChance;
    @Nullable
    private UUID wanderingTraderId;
    private final Set<String> serverBrands;
    private boolean modded;
    private final Timer<MinecraftServer> scheduledEvents;

    private LevelProperties(@Nullable DataFixer dataFixer, int dataVersion, @Nullable NbtCompound playerData, boolean modded, int spawnX, int spawnY, int spawnZ, float spawnAngle, long time, long timeOfDay, int version, int clearWeatherTime, int rainTime, boolean raining, int thunderTime, boolean thundering, boolean initialized, boolean difficultyLocked, WorldBorder.Properties worldBorder, int wanderingTraderSpawnDelay, int wanderingTraderSpawnChance, @Nullable UUID wanderingTraderId, LinkedHashSet<String> serverBrands, Timer<MinecraftServer> scheduledEvents, @Nullable NbtCompound customBossEvents, NbtCompound dragonFight, LevelInfo levelInfo, GeneratorOptions generatorOptions, Lifecycle lifecycle) {
        this.dataFixer = dataFixer;
        this.modded = modded;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.spawnZ = spawnZ;
        this.spawnAngle = spawnAngle;
        this.time = time;
        this.timeOfDay = timeOfDay;
        this.version = version;
        this.clearWeatherTime = clearWeatherTime;
        this.rainTime = rainTime;
        this.raining = raining;
        this.thunderTime = thunderTime;
        this.thundering = thundering;
        this.initialized = initialized;
        this.difficultyLocked = difficultyLocked;
        this.worldBorder = worldBorder;
        this.wanderingTraderSpawnDelay = wanderingTraderSpawnDelay;
        this.wanderingTraderSpawnChance = wanderingTraderSpawnChance;
        this.wanderingTraderId = wanderingTraderId;
        this.serverBrands = serverBrands;
        this.playerData = playerData;
        this.dataVersion = dataVersion;
        this.scheduledEvents = scheduledEvents;
        this.customBossEvents = customBossEvents;
        this.dragonFight = dragonFight;
        this.levelInfo = levelInfo;
        this.generatorOptions = generatorOptions;
        this.lifecycle = lifecycle;
    }

    public LevelProperties(LevelInfo levelInfo, GeneratorOptions generatorOptions, Lifecycle lifecycle) {
        this(null, SharedConstants.getGameVersion().getWorldVersion(), null, false, 0, 0, 0, 0.0f, 0L, 0L, 19133, 0, 0, false, 0, false, false, false, WorldBorder.DEFAULT_BORDER, 0, 0, null, Sets.newLinkedHashSet(), new Timer<MinecraftServer>(TimerCallbackSerializer.INSTANCE), null, new NbtCompound(), levelInfo.withCopiedGameRules(), generatorOptions, lifecycle);
    }

    public static LevelProperties readProperties(Dynamic<NbtElement> dynamic2, DataFixer dataFixer, int dataVersion, @Nullable NbtCompound playerData, LevelInfo levelInfo, SaveVersionInfo saveVersionInfo, GeneratorOptions generatorOptions, Lifecycle lifecycle) {
        long l = dynamic2.get("Time").asLong(0L);
        NbtCompound nbtCompound = (NbtCompound)dynamic2.get("DragonFight").result().map(Dynamic::getValue).orElseGet(() -> (NbtElement)dynamic2.get("DimensionData").get("1").get("DragonFight").orElseEmptyMap().getValue());
        return new LevelProperties(dataFixer, dataVersion, playerData, dynamic2.get("WasModded").asBoolean(false), dynamic2.get("SpawnX").asInt(0), dynamic2.get("SpawnY").asInt(0), dynamic2.get("SpawnZ").asInt(0), dynamic2.get("SpawnAngle").asFloat(0.0f), l, dynamic2.get("DayTime").asLong(l), saveVersionInfo.getLevelFormatVersion(), dynamic2.get("clearWeatherTime").asInt(0), dynamic2.get("rainTime").asInt(0), dynamic2.get("raining").asBoolean(false), dynamic2.get("thunderTime").asInt(0), dynamic2.get("thundering").asBoolean(false), dynamic2.get("initialized").asBoolean(true), dynamic2.get("DifficultyLocked").asBoolean(false), WorldBorder.Properties.fromDynamic(dynamic2, WorldBorder.DEFAULT_BORDER), dynamic2.get("WanderingTraderSpawnDelay").asInt(0), dynamic2.get("WanderingTraderSpawnChance").asInt(0), dynamic2.get("WanderingTraderId").read(DynamicSerializableUuid.CODEC).result().orElse(null), dynamic2.get("ServerBrands").asStream().flatMap(dynamic -> Util.stream(dynamic.asString().result())).collect(Collectors.toCollection(Sets::newLinkedHashSet)), new Timer<MinecraftServer>(TimerCallbackSerializer.INSTANCE, dynamic2.get("ScheduledEvents").asStream()), (NbtCompound)dynamic2.get("CustomBossEvents").orElseEmptyMap().getValue(), nbtCompound, levelInfo, generatorOptions, lifecycle);
    }

    @Override
    public NbtCompound cloneWorldNbt(DynamicRegistryManager registryManager, @Nullable NbtCompound playerNbt) {
        this.loadPlayerData();
        if (playerNbt == null) {
            playerNbt = this.playerData;
        }
        NbtCompound nbtCompound = new NbtCompound();
        this.updateProperties(registryManager, nbtCompound, playerNbt);
        return nbtCompound;
    }

    private void updateProperties(DynamicRegistryManager registryManager, NbtCompound levelTag, @Nullable NbtCompound playerTag) {
        NbtList nbtList = new NbtList();
        this.serverBrands.stream().map(NbtString::of).forEach(nbtList::add);
        levelTag.put("ServerBrands", nbtList);
        levelTag.putBoolean("WasModded", this.modded);
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString("Name", SharedConstants.getGameVersion().getName());
        nbtCompound.putInt("Id", SharedConstants.getGameVersion().getWorldVersion());
        nbtCompound.putBoolean("Snapshot", !SharedConstants.getGameVersion().isStable());
        levelTag.put("Version", nbtCompound);
        levelTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        RegistryReadingOps<NbtElement> registryReadingOps = RegistryReadingOps.of(NbtOps.INSTANCE, registryManager);
        GeneratorOptions.CODEC.encodeStart(registryReadingOps, (Object)this.generatorOptions).resultOrPartial(Util.method_29188("WorldGenSettings: ", arg_0 -> ((Logger)LOGGER).error(arg_0))).ifPresent(nbtElement -> levelTag.put("WorldGenSettings", (NbtElement)nbtElement));
        levelTag.putInt("GameType", this.levelInfo.getGameMode().getId());
        levelTag.putInt("SpawnX", this.spawnX);
        levelTag.putInt("SpawnY", this.spawnY);
        levelTag.putInt("SpawnZ", this.spawnZ);
        levelTag.putFloat("SpawnAngle", this.spawnAngle);
        levelTag.putLong("Time", this.time);
        levelTag.putLong("DayTime", this.timeOfDay);
        levelTag.putLong("LastPlayed", Util.getEpochTimeMs());
        levelTag.putString("LevelName", this.levelInfo.getLevelName());
        levelTag.putInt("version", 19133);
        levelTag.putInt("clearWeatherTime", this.clearWeatherTime);
        levelTag.putInt("rainTime", this.rainTime);
        levelTag.putBoolean("raining", this.raining);
        levelTag.putInt("thunderTime", this.thunderTime);
        levelTag.putBoolean("thundering", this.thundering);
        levelTag.putBoolean("hardcore", this.levelInfo.isHardcore());
        levelTag.putBoolean("allowCommands", this.levelInfo.areCommandsAllowed());
        levelTag.putBoolean("initialized", this.initialized);
        this.worldBorder.toTag(levelTag);
        levelTag.putByte("Difficulty", (byte)this.levelInfo.getDifficulty().getId());
        levelTag.putBoolean("DifficultyLocked", this.difficultyLocked);
        levelTag.put("GameRules", this.levelInfo.getGameRules().toNbt());
        levelTag.put("DragonFight", this.dragonFight);
        if (playerTag != null) {
            levelTag.put("Player", playerTag);
        }
        DataPackSettings.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.levelInfo.getDataPackSettings()).result().ifPresent(nbtElement -> levelTag.put("DataPacks", (NbtElement)nbtElement));
        if (this.customBossEvents != null) {
            levelTag.put("CustomBossEvents", this.customBossEvents);
        }
        levelTag.put("ScheduledEvents", this.scheduledEvents.toNbt());
        levelTag.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
        levelTag.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
        if (this.wanderingTraderId != null) {
            levelTag.putUuid("WanderingTraderId", this.wanderingTraderId);
        }
    }

    @Override
    public int getSpawnX() {
        return this.spawnX;
    }

    @Override
    public int getSpawnY() {
        return this.spawnY;
    }

    @Override
    public int getSpawnZ() {
        return this.spawnZ;
    }

    @Override
    public float getSpawnAngle() {
        return this.spawnAngle;
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public long getTimeOfDay() {
        return this.timeOfDay;
    }

    private void loadPlayerData() {
        if (this.playerDataLoaded || this.playerData == null) {
            return;
        }
        if (this.dataVersion < SharedConstants.getGameVersion().getWorldVersion()) {
            if (this.dataFixer == null) {
                throw Util.throwOrPause(new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded."));
            }
            this.playerData = NbtHelper.update(this.dataFixer, DataFixTypes.PLAYER, this.playerData, this.dataVersion);
        }
        this.playerDataLoaded = true;
    }

    @Override
    public NbtCompound getPlayerData() {
        this.loadPlayerData();
        return this.playerData;
    }

    @Override
    public void setSpawnX(int spawnX) {
        this.spawnX = spawnX;
    }

    @Override
    public void setSpawnY(int spawnY) {
        this.spawnY = spawnY;
    }

    @Override
    public void setSpawnZ(int spawnZ) {
        this.spawnZ = spawnZ;
    }

    @Override
    public void setSpawnAngle(float angle) {
        this.spawnAngle = angle;
    }

    @Override
    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public void setTimeOfDay(long timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    @Override
    public void setSpawnPos(BlockPos pos, float angle) {
        this.spawnX = pos.getX();
        this.spawnY = pos.getY();
        this.spawnZ = pos.getZ();
        this.spawnAngle = angle;
    }

    @Override
    public String getLevelName() {
        return this.levelInfo.getLevelName();
    }

    @Override
    public int getVersion() {
        return this.version;
    }

    @Override
    public int getClearWeatherTime() {
        return this.clearWeatherTime;
    }

    @Override
    public void setClearWeatherTime(int clearWeatherTime) {
        this.clearWeatherTime = clearWeatherTime;
    }

    @Override
    public boolean isThundering() {
        return this.thundering;
    }

    @Override
    public void setThundering(boolean thundering) {
        this.thundering = thundering;
    }

    @Override
    public int getThunderTime() {
        return this.thunderTime;
    }

    @Override
    public void setThunderTime(int thunderTime) {
        this.thunderTime = thunderTime;
    }

    @Override
    public boolean isRaining() {
        return this.raining;
    }

    @Override
    public void setRaining(boolean raining) {
        this.raining = raining;
    }

    @Override
    public int getRainTime() {
        return this.rainTime;
    }

    @Override
    public void setRainTime(int rainTime) {
        this.rainTime = rainTime;
    }

    @Override
    public GameMode getGameMode() {
        return this.levelInfo.getGameMode();
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        this.levelInfo = this.levelInfo.withGameMode(gameMode);
    }

    @Override
    public boolean isHardcore() {
        return this.levelInfo.isHardcore();
    }

    @Override
    public boolean areCommandsAllowed() {
        return this.levelInfo.areCommandsAllowed();
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    @Override
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    @Override
    public GameRules getGameRules() {
        return this.levelInfo.getGameRules();
    }

    @Override
    public WorldBorder.Properties getWorldBorder() {
        return this.worldBorder;
    }

    @Override
    public void setWorldBorder(WorldBorder.Properties properties) {
        this.worldBorder = properties;
    }

    @Override
    public Difficulty getDifficulty() {
        return this.levelInfo.getDifficulty();
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
        this.levelInfo = this.levelInfo.withDifficulty(difficulty);
    }

    @Override
    public boolean isDifficultyLocked() {
        return this.difficultyLocked;
    }

    @Override
    public void setDifficultyLocked(boolean locked) {
        this.difficultyLocked = locked;
    }

    @Override
    public Timer<MinecraftServer> getScheduledEvents() {
        return this.scheduledEvents;
    }

    @Override
    public void populateCrashReport(CrashReportSection reportSection) {
        ServerWorldProperties.super.populateCrashReport(reportSection);
        SaveProperties.super.populateCrashReport(reportSection);
    }

    @Override
    public GeneratorOptions getGeneratorOptions() {
        return this.generatorOptions;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Lifecycle getLifecycle() {
        return this.lifecycle;
    }

    @Override
    public NbtCompound getDragonFight() {
        return this.dragonFight;
    }

    @Override
    public void setDragonFight(NbtCompound nbt) {
        this.dragonFight = nbt;
    }

    @Override
    public DataPackSettings getDataPackSettings() {
        return this.levelInfo.getDataPackSettings();
    }

    @Override
    public void updateLevelInfo(DataPackSettings dataPackSettings) {
        this.levelInfo = this.levelInfo.withDataPackSettings(dataPackSettings);
    }

    @Override
    @Nullable
    public NbtCompound getCustomBossEvents() {
        return this.customBossEvents;
    }

    @Override
    public void setCustomBossEvents(@Nullable NbtCompound nbt) {
        this.customBossEvents = nbt;
    }

    @Override
    public int getWanderingTraderSpawnDelay() {
        return this.wanderingTraderSpawnDelay;
    }

    @Override
    public void setWanderingTraderSpawnDelay(int wanderingTraderSpawnDelay) {
        this.wanderingTraderSpawnDelay = wanderingTraderSpawnDelay;
    }

    @Override
    public int getWanderingTraderSpawnChance() {
        return this.wanderingTraderSpawnChance;
    }

    @Override
    public void setWanderingTraderSpawnChance(int wanderingTraderSpawnChance) {
        this.wanderingTraderSpawnChance = wanderingTraderSpawnChance;
    }

    @Override
    public void setWanderingTraderId(UUID uuid) {
        this.wanderingTraderId = uuid;
    }

    @Override
    public void addServerBrand(String brand, boolean modded) {
        this.serverBrands.add(brand);
        this.modded |= modded;
    }

    @Override
    public boolean isModded() {
        return this.modded;
    }

    @Override
    public Set<String> getServerBrands() {
        return ImmutableSet.copyOf(this.serverBrands);
    }

    @Override
    public ServerWorldProperties getMainWorldProperties() {
        return this;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public LevelInfo getLevelInfo() {
        return this.levelInfo.withCopiedGameRules();
    }
}

