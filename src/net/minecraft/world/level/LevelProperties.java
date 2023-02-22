/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.common.hash.Hashing
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  com.mojang.datafixers.types.JsonOps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.level;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.JsonOps;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.timer.Timer;
import net.minecraft.world.timer.TimerCallbackSerializer;
import org.jetbrains.annotations.Nullable;

public class LevelProperties {
    private String versionName;
    private int versionId;
    private boolean versionSnapshot;
    public static final Difficulty DEFAULT_DIFFICULTY = Difficulty.NORMAL;
    private long randomSeed;
    private LevelGeneratorType generatorType = LevelGeneratorType.DEFAULT;
    private CompoundTag generatorOptions = new CompoundTag();
    @Nullable
    private String legacyCustomOptions;
    private int spawnX;
    private int spawnY;
    private int spawnZ;
    private long time;
    private long timeOfDay;
    private long lastPlayed;
    private long sizeOnDisk;
    @Nullable
    private final DataFixer dataFixer;
    private final int playerWorldId;
    private boolean playerDataLoaded;
    private CompoundTag playerData;
    private String levelName;
    private int version;
    private int clearWeatherTime;
    private boolean raining;
    private int rainTime;
    private boolean thundering;
    private int thunderTime;
    private GameMode gameMode;
    private boolean structures;
    private boolean hardcore;
    private boolean commandsAllowed;
    private boolean initialized;
    private Difficulty difficulty;
    private boolean difficultyLocked;
    private double borderCenterX;
    private double borderCenterZ;
    private double borderSize = 6.0E7;
    private long borderSizeLerpTime;
    private double borderSizeLerpTarget;
    private double borderSafeZone = 5.0;
    private double borderDamagePerBlock = 0.2;
    private int borderWarningBlocks = 5;
    private int borderWarningTime = 15;
    private final Set<String> disabledDataPacks = Sets.newHashSet();
    private final Set<String> enabledDataPacks = Sets.newLinkedHashSet();
    private final Map<DimensionType, CompoundTag> worldData = Maps.newIdentityHashMap();
    private CompoundTag customBossEvents;
    private int wanderingTraderSpawnDelay;
    private int wanderingTraderSpawnChance;
    private UUID wanderingTraderId;
    private Set<String> field_21837 = Sets.newLinkedHashSet();
    private boolean field_21838;
    private final GameRules gameRules = new GameRules();
    private final Timer<MinecraftServer> scheduledEvents = new Timer<MinecraftServer>(TimerCallbackSerializer.INSTANCE);

    protected LevelProperties() {
        this.dataFixer = null;
        this.playerWorldId = SharedConstants.getGameVersion().getWorldVersion();
        this.setGeneratorOptions(new CompoundTag());
    }

    public LevelProperties(CompoundTag compoundTag, DataFixer dataFixer, int i, @Nullable CompoundTag compoundTag2) {
        this.dataFixer = dataFixer;
        ListTag listTag = compoundTag.getList("ServerBrands", 8);
        for (int j = 0; j < listTag.size(); ++j) {
            this.field_21837.add(listTag.getString(j));
        }
        this.field_21838 = compoundTag.getBoolean("WasModded");
        if (compoundTag.contains("Version", 10)) {
            CompoundTag compoundTag3 = compoundTag.getCompound("Version");
            this.versionName = compoundTag3.getString("Name");
            this.versionId = compoundTag3.getInt("Id");
            this.versionSnapshot = compoundTag3.getBoolean("Snapshot");
        }
        this.randomSeed = compoundTag.getLong("RandomSeed");
        if (compoundTag.contains("generatorName", 8)) {
            String string = compoundTag.getString("generatorName");
            this.generatorType = LevelGeneratorType.getTypeFromName(string);
            if (this.generatorType == null) {
                this.generatorType = LevelGeneratorType.DEFAULT;
            } else if (this.generatorType == LevelGeneratorType.CUSTOMIZED) {
                this.legacyCustomOptions = compoundTag.getString("generatorOptions");
            } else if (this.generatorType.isVersioned()) {
                int k = 0;
                if (compoundTag.contains("generatorVersion", 99)) {
                    k = compoundTag.getInt("generatorVersion");
                }
                this.generatorType = this.generatorType.getTypeForVersion(k);
            }
            this.setGeneratorOptions(compoundTag.getCompound("generatorOptions"));
        }
        this.gameMode = GameMode.byId(compoundTag.getInt("GameType"));
        if (compoundTag.contains("legacy_custom_options", 8)) {
            this.legacyCustomOptions = compoundTag.getString("legacy_custom_options");
        }
        this.structures = compoundTag.contains("MapFeatures", 99) ? compoundTag.getBoolean("MapFeatures") : true;
        this.spawnX = compoundTag.getInt("SpawnX");
        this.spawnY = compoundTag.getInt("SpawnY");
        this.spawnZ = compoundTag.getInt("SpawnZ");
        this.time = compoundTag.getLong("Time");
        this.timeOfDay = compoundTag.contains("DayTime", 99) ? compoundTag.getLong("DayTime") : this.time;
        this.lastPlayed = compoundTag.getLong("LastPlayed");
        this.sizeOnDisk = compoundTag.getLong("SizeOnDisk");
        this.levelName = compoundTag.getString("LevelName");
        this.version = compoundTag.getInt("version");
        this.clearWeatherTime = compoundTag.getInt("clearWeatherTime");
        this.rainTime = compoundTag.getInt("rainTime");
        this.raining = compoundTag.getBoolean("raining");
        this.thunderTime = compoundTag.getInt("thunderTime");
        this.thundering = compoundTag.getBoolean("thundering");
        this.hardcore = compoundTag.getBoolean("hardcore");
        this.initialized = compoundTag.contains("initialized", 99) ? compoundTag.getBoolean("initialized") : true;
        this.commandsAllowed = compoundTag.contains("allowCommands", 99) ? compoundTag.getBoolean("allowCommands") : this.gameMode == GameMode.CREATIVE;
        this.playerWorldId = i;
        if (compoundTag2 != null) {
            this.playerData = compoundTag2;
        }
        if (compoundTag.contains("GameRules", 10)) {
            this.gameRules.load(compoundTag.getCompound("GameRules"));
        }
        if (compoundTag.contains("Difficulty", 99)) {
            this.difficulty = Difficulty.byOrdinal(compoundTag.getByte("Difficulty"));
        }
        if (compoundTag.contains("DifficultyLocked", 1)) {
            this.difficultyLocked = compoundTag.getBoolean("DifficultyLocked");
        }
        if (compoundTag.contains("BorderCenterX", 99)) {
            this.borderCenterX = compoundTag.getDouble("BorderCenterX");
        }
        if (compoundTag.contains("BorderCenterZ", 99)) {
            this.borderCenterZ = compoundTag.getDouble("BorderCenterZ");
        }
        if (compoundTag.contains("BorderSize", 99)) {
            this.borderSize = compoundTag.getDouble("BorderSize");
        }
        if (compoundTag.contains("BorderSizeLerpTime", 99)) {
            this.borderSizeLerpTime = compoundTag.getLong("BorderSizeLerpTime");
        }
        if (compoundTag.contains("BorderSizeLerpTarget", 99)) {
            this.borderSizeLerpTarget = compoundTag.getDouble("BorderSizeLerpTarget");
        }
        if (compoundTag.contains("BorderSafeZone", 99)) {
            this.borderSafeZone = compoundTag.getDouble("BorderSafeZone");
        }
        if (compoundTag.contains("BorderDamagePerBlock", 99)) {
            this.borderDamagePerBlock = compoundTag.getDouble("BorderDamagePerBlock");
        }
        if (compoundTag.contains("BorderWarningBlocks", 99)) {
            this.borderWarningBlocks = compoundTag.getInt("BorderWarningBlocks");
        }
        if (compoundTag.contains("BorderWarningTime", 99)) {
            this.borderWarningTime = compoundTag.getInt("BorderWarningTime");
        }
        if (compoundTag.contains("DimensionData", 10)) {
            CompoundTag compoundTag3 = compoundTag.getCompound("DimensionData");
            for (String string2 : compoundTag3.getKeys()) {
                this.worldData.put(DimensionType.byRawId(Integer.parseInt(string2)), compoundTag3.getCompound(string2));
            }
        }
        if (compoundTag.contains("DataPacks", 10)) {
            CompoundTag compoundTag3 = compoundTag.getCompound("DataPacks");
            ListTag listTag2 = compoundTag3.getList("Disabled", 8);
            for (int l = 0; l < listTag2.size(); ++l) {
                this.disabledDataPacks.add(listTag2.getString(l));
            }
            ListTag listTag3 = compoundTag3.getList("Enabled", 8);
            for (int m = 0; m < listTag3.size(); ++m) {
                this.enabledDataPacks.add(listTag3.getString(m));
            }
        }
        if (compoundTag.contains("CustomBossEvents", 10)) {
            this.customBossEvents = compoundTag.getCompound("CustomBossEvents");
        }
        if (compoundTag.contains("ScheduledEvents", 9)) {
            this.scheduledEvents.fromTag(compoundTag.getList("ScheduledEvents", 10));
        }
        if (compoundTag.contains("WanderingTraderSpawnDelay", 99)) {
            this.wanderingTraderSpawnDelay = compoundTag.getInt("WanderingTraderSpawnDelay");
        }
        if (compoundTag.contains("WanderingTraderSpawnChance", 99)) {
            this.wanderingTraderSpawnChance = compoundTag.getInt("WanderingTraderSpawnChance");
        }
        if (compoundTag.contains("WanderingTraderId", 8)) {
            this.wanderingTraderId = UUID.fromString(compoundTag.getString("WanderingTraderId"));
        }
    }

    public LevelProperties(LevelInfo levelInfo, String levelName) {
        this.dataFixer = null;
        this.playerWorldId = SharedConstants.getGameVersion().getWorldVersion();
        this.loadLevelInfo(levelInfo);
        this.levelName = levelName;
        this.difficulty = DEFAULT_DIFFICULTY;
        this.initialized = false;
    }

    public void loadLevelInfo(LevelInfo levelInfo) {
        this.randomSeed = levelInfo.getSeed();
        this.gameMode = levelInfo.getGameMode();
        this.structures = levelInfo.hasStructures();
        this.hardcore = levelInfo.isHardcore();
        this.generatorType = levelInfo.getGeneratorType();
        this.setGeneratorOptions((CompoundTag)Dynamic.convert((DynamicOps)JsonOps.INSTANCE, (DynamicOps)NbtOps.INSTANCE, (Object)levelInfo.getGeneratorOptions()));
        this.commandsAllowed = levelInfo.allowCommands();
    }

    public CompoundTag cloneWorldTag(@Nullable CompoundTag playerTag) {
        this.loadPlayerData();
        if (playerTag == null) {
            playerTag = this.playerData;
        }
        CompoundTag compoundTag = new CompoundTag();
        this.updateProperties(compoundTag, playerTag);
        return compoundTag;
    }

    private void updateProperties(CompoundTag levelTag, CompoundTag playerTag) {
        ListTag listTag = new ListTag();
        this.field_21837.stream().map(StringTag::of).forEach(listTag::add);
        levelTag.put("ServerBrands", listTag);
        levelTag.putBoolean("WasModded", this.field_21838);
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("Name", SharedConstants.getGameVersion().getName());
        compoundTag.putInt("Id", SharedConstants.getGameVersion().getWorldVersion());
        compoundTag.putBoolean("Snapshot", !SharedConstants.getGameVersion().isStable());
        levelTag.put("Version", compoundTag);
        levelTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        levelTag.putLong("RandomSeed", this.randomSeed);
        levelTag.putString("generatorName", this.generatorType.getStoredName());
        levelTag.putInt("generatorVersion", this.generatorType.getVersion());
        if (!this.generatorOptions.isEmpty()) {
            levelTag.put("generatorOptions", this.generatorOptions);
        }
        if (this.legacyCustomOptions != null) {
            levelTag.putString("legacy_custom_options", this.legacyCustomOptions);
        }
        levelTag.putInt("GameType", this.gameMode.getId());
        levelTag.putBoolean("MapFeatures", this.structures);
        levelTag.putInt("SpawnX", this.spawnX);
        levelTag.putInt("SpawnY", this.spawnY);
        levelTag.putInt("SpawnZ", this.spawnZ);
        levelTag.putLong("Time", this.time);
        levelTag.putLong("DayTime", this.timeOfDay);
        levelTag.putLong("SizeOnDisk", this.sizeOnDisk);
        levelTag.putLong("LastPlayed", Util.getEpochTimeMs());
        levelTag.putString("LevelName", this.levelName);
        levelTag.putInt("version", this.version);
        levelTag.putInt("clearWeatherTime", this.clearWeatherTime);
        levelTag.putInt("rainTime", this.rainTime);
        levelTag.putBoolean("raining", this.raining);
        levelTag.putInt("thunderTime", this.thunderTime);
        levelTag.putBoolean("thundering", this.thundering);
        levelTag.putBoolean("hardcore", this.hardcore);
        levelTag.putBoolean("allowCommands", this.commandsAllowed);
        levelTag.putBoolean("initialized", this.initialized);
        levelTag.putDouble("BorderCenterX", this.borderCenterX);
        levelTag.putDouble("BorderCenterZ", this.borderCenterZ);
        levelTag.putDouble("BorderSize", this.borderSize);
        levelTag.putLong("BorderSizeLerpTime", this.borderSizeLerpTime);
        levelTag.putDouble("BorderSafeZone", this.borderSafeZone);
        levelTag.putDouble("BorderDamagePerBlock", this.borderDamagePerBlock);
        levelTag.putDouble("BorderSizeLerpTarget", this.borderSizeLerpTarget);
        levelTag.putDouble("BorderWarningBlocks", this.borderWarningBlocks);
        levelTag.putDouble("BorderWarningTime", this.borderWarningTime);
        if (this.difficulty != null) {
            levelTag.putByte("Difficulty", (byte)this.difficulty.getId());
        }
        levelTag.putBoolean("DifficultyLocked", this.difficultyLocked);
        levelTag.put("GameRules", this.gameRules.toNbt());
        CompoundTag compoundTag2 = new CompoundTag();
        for (Map.Entry<DimensionType, CompoundTag> entry : this.worldData.entrySet()) {
            compoundTag2.put(String.valueOf(entry.getKey().getRawId()), entry.getValue());
        }
        levelTag.put("DimensionData", compoundTag2);
        if (playerTag != null) {
            levelTag.put("Player", playerTag);
        }
        CompoundTag compoundTag3 = new CompoundTag();
        ListTag listTag2 = new ListTag();
        for (String string : this.enabledDataPacks) {
            listTag2.add(StringTag.of(string));
        }
        compoundTag3.put("Enabled", listTag2);
        ListTag listTag3 = new ListTag();
        for (String string2 : this.disabledDataPacks) {
            listTag3.add(StringTag.of(string2));
        }
        compoundTag3.put("Disabled", listTag3);
        levelTag.put("DataPacks", compoundTag3);
        if (this.customBossEvents != null) {
            levelTag.put("CustomBossEvents", this.customBossEvents);
        }
        levelTag.put("ScheduledEvents", this.scheduledEvents.toTag());
        levelTag.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
        levelTag.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
        if (this.wanderingTraderId != null) {
            levelTag.putString("WanderingTraderId", this.wanderingTraderId.toString());
        }
    }

    public long getSeed() {
        return this.randomSeed;
    }

    public static long sha256Hash(long seed) {
        return Hashing.sha256().hashLong(seed).asLong();
    }

    public int getSpawnX() {
        return this.spawnX;
    }

    public int getSpawnY() {
        return this.spawnY;
    }

    public int getSpawnZ() {
        return this.spawnZ;
    }

    public long getTime() {
        return this.time;
    }

    public long getTimeOfDay() {
        return this.timeOfDay;
    }

    private void loadPlayerData() {
        if (this.playerDataLoaded || this.playerData == null) {
            return;
        }
        if (this.playerWorldId < SharedConstants.getGameVersion().getWorldVersion()) {
            if (this.dataFixer == null) {
                throw Util.throwOrPause(new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded."));
            }
            this.playerData = NbtHelper.update(this.dataFixer, DataFixTypes.PLAYER, this.playerData, this.playerWorldId);
        }
        this.playerDataLoaded = true;
    }

    public CompoundTag getPlayerData() {
        this.loadPlayerData();
        return this.playerData;
    }

    @Environment(value=EnvType.CLIENT)
    public void setSpawnX(int spawnX) {
        this.spawnX = spawnX;
    }

    @Environment(value=EnvType.CLIENT)
    public void setSpawnY(int spawnY) {
        this.spawnY = spawnY;
    }

    @Environment(value=EnvType.CLIENT)
    public void setSpawnZ(int spawnZ) {
        this.spawnZ = spawnZ;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setTimeOfDay(long timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public void setSpawnPos(BlockPos blockPos) {
        this.spawnX = blockPos.getX();
        this.spawnY = blockPos.getY();
        this.spawnZ = blockPos.getZ();
    }

    public String getLevelName() {
        return this.levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Environment(value=EnvType.CLIENT)
    public long getLastPlayed() {
        return this.lastPlayed;
    }

    public int getClearWeatherTime() {
        return this.clearWeatherTime;
    }

    public void setClearWeatherTime(int clearWeatherTime) {
        this.clearWeatherTime = clearWeatherTime;
    }

    public boolean isThundering() {
        return this.thundering;
    }

    public void setThundering(boolean thundering) {
        this.thundering = thundering;
    }

    public int getThunderTime() {
        return this.thunderTime;
    }

    public void setThunderTime(int thunderTime) {
        this.thunderTime = thunderTime;
    }

    public boolean isRaining() {
        return this.raining;
    }

    public void setRaining(boolean raining) {
        this.raining = raining;
    }

    public int getRainTime() {
        return this.rainTime;
    }

    public void setRainTime(int rainTime) {
        this.rainTime = rainTime;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public boolean hasStructures() {
        return this.structures;
    }

    public void setStructures(boolean structures) {
        this.structures = structures;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public boolean isHardcore() {
        return this.hardcore;
    }

    public void setHardcore(boolean hardcore) {
        this.hardcore = hardcore;
    }

    public LevelGeneratorType getGeneratorType() {
        return this.generatorType;
    }

    public void setGeneratorType(LevelGeneratorType levelGeneratorType) {
        this.generatorType = levelGeneratorType;
    }

    public CompoundTag getGeneratorOptions() {
        return this.generatorOptions;
    }

    public void setGeneratorOptions(CompoundTag compoundTag) {
        this.generatorOptions = compoundTag;
    }

    public boolean areCommandsAllowed() {
        return this.commandsAllowed;
    }

    public void setCommandsAllowed(boolean commandsAllowed) {
        this.commandsAllowed = commandsAllowed;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public GameRules getGameRules() {
        return this.gameRules;
    }

    public double getBorderCenterX() {
        return this.borderCenterX;
    }

    public double getBorderCenterZ() {
        return this.borderCenterZ;
    }

    public double getBorderSize() {
        return this.borderSize;
    }

    public void setBorderSize(double borderSize) {
        this.borderSize = borderSize;
    }

    public long getBorderSizeLerpTime() {
        return this.borderSizeLerpTime;
    }

    public void setBorderSizeLerpTime(long borderSizeLerpTime) {
        this.borderSizeLerpTime = borderSizeLerpTime;
    }

    public double getBorderSizeLerpTarget() {
        return this.borderSizeLerpTarget;
    }

    public void setBorderSizeLerpTarget(double borderSizeLerpTarget) {
        this.borderSizeLerpTarget = borderSizeLerpTarget;
    }

    public void borderCenterZ(double borderCenterZ) {
        this.borderCenterZ = borderCenterZ;
    }

    public void setBorderCenterX(double borderCenterX) {
        this.borderCenterX = borderCenterX;
    }

    public double getBorderSafeZone() {
        return this.borderSafeZone;
    }

    public void setBorderSafeZone(double borderSafeZone) {
        this.borderSafeZone = borderSafeZone;
    }

    public double getBorderDamagePerBlock() {
        return this.borderDamagePerBlock;
    }

    public void setBorderDamagePerBlock(double borderDamagePerBlock) {
        this.borderDamagePerBlock = borderDamagePerBlock;
    }

    public int getBorderWarningBlocks() {
        return this.borderWarningBlocks;
    }

    public int getBorderWarningTime() {
        return this.borderWarningTime;
    }

    public void setBorderWarningBlocks(int borderWarningBlocks) {
        this.borderWarningBlocks = borderWarningBlocks;
    }

    public void setBorderWarningTime(int borderWarningTime) {
        this.borderWarningTime = borderWarningTime;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public boolean isDifficultyLocked() {
        return this.difficultyLocked;
    }

    public void setDifficultyLocked(boolean difficultyLocked) {
        this.difficultyLocked = difficultyLocked;
    }

    public Timer<MinecraftServer> getScheduledEvents() {
        return this.scheduledEvents;
    }

    public void populateCrashReport(CrashReportSection crashReportSection) {
        crashReportSection.add("Level name", () -> this.levelName);
        crashReportSection.add("Level seed", () -> String.valueOf(this.randomSeed));
        crashReportSection.add("Level generator", () -> String.format("ID %02d - %s, ver %d. Features enabled: %b", this.generatorType.getId(), this.generatorType.getName(), this.generatorType.getVersion(), this.structures));
        crashReportSection.add("Level generator options", () -> this.generatorOptions.toString());
        crashReportSection.add("Level spawn location", () -> CrashReportSection.createPositionString(this.spawnX, this.spawnY, this.spawnZ));
        crashReportSection.add("Level time", () -> String.format("%d game time, %d day time", this.time, this.timeOfDay));
        crashReportSection.add("Known server brands", () -> String.join((CharSequence)", ", this.field_21837));
        crashReportSection.add("Level was modded", () -> Boolean.toString(this.field_21838));
        crashReportSection.add("Level storage version", () -> {
            String string = "Unknown?";
            try {
                switch (this.version) {
                    case 19133: {
                        string = "Anvil";
                        break;
                    }
                    case 19132: {
                        string = "McRegion";
                    }
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            return String.format("0x%05X - %s", this.version, string);
        });
        crashReportSection.add("Level weather", () -> String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", this.rainTime, this.raining, this.thunderTime, this.thundering));
        crashReportSection.add("Level game mode", () -> String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.gameMode.getName(), this.gameMode.getId(), this.hardcore, this.commandsAllowed));
    }

    public CompoundTag getWorldData(DimensionType dimensionType) {
        CompoundTag compoundTag = this.worldData.get(dimensionType);
        if (compoundTag == null) {
            return new CompoundTag();
        }
        return compoundTag;
    }

    public void setWorldData(DimensionType type, CompoundTag compoundTag) {
        this.worldData.put(type, compoundTag);
    }

    @Environment(value=EnvType.CLIENT)
    public int getVersionId() {
        return this.versionId;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isVersionSnapshot() {
        return this.versionSnapshot;
    }

    @Environment(value=EnvType.CLIENT)
    public String getVersionName() {
        return this.versionName;
    }

    public Set<String> getDisabledDataPacks() {
        return this.disabledDataPacks;
    }

    public Set<String> getEnabledDataPacks() {
        return this.enabledDataPacks;
    }

    @Nullable
    public CompoundTag getCustomBossEvents() {
        return this.customBossEvents;
    }

    public void setCustomBossEvents(@Nullable CompoundTag customBossEvents) {
        this.customBossEvents = customBossEvents;
    }

    public int getWanderingTraderSpawnDelay() {
        return this.wanderingTraderSpawnDelay;
    }

    public void setWanderingTraderSpawnDelay(int wanderingTraderSpawnDelay) {
        this.wanderingTraderSpawnDelay = wanderingTraderSpawnDelay;
    }

    public int getWanderingTraderSpawnChance() {
        return this.wanderingTraderSpawnChance;
    }

    public void setWanderingTraderSpawnChance(int wanderingTraderSpawnChance) {
        this.wanderingTraderSpawnChance = wanderingTraderSpawnChance;
    }

    public void setWanderingTraderId(UUID wanderingTraderId) {
        this.wanderingTraderId = wanderingTraderId;
    }

    public void method_24285(String string, boolean bl) {
        this.field_21837.add(string);
        this.field_21838 |= bl;
    }
}

