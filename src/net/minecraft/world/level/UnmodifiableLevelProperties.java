/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.level;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.timer.Timer;
import org.jetbrains.annotations.Nullable;

public class UnmodifiableLevelProperties
extends LevelProperties {
    private final LevelProperties properties;

    public UnmodifiableLevelProperties(LevelProperties levelProperties) {
        this.properties = levelProperties;
    }

    @Override
    public CompoundTag cloneWorldTag(@Nullable CompoundTag playerTag) {
        return this.properties.cloneWorldTag(playerTag);
    }

    @Override
    public long getSeed() {
        return this.properties.getSeed();
    }

    @Override
    public int getSpawnX() {
        return this.properties.getSpawnX();
    }

    @Override
    public int getSpawnY() {
        return this.properties.getSpawnY();
    }

    @Override
    public int getSpawnZ() {
        return this.properties.getSpawnZ();
    }

    @Override
    public long getTime() {
        return this.properties.getTime();
    }

    @Override
    public long getTimeOfDay() {
        return this.properties.getTimeOfDay();
    }

    @Override
    public CompoundTag getPlayerData() {
        return this.properties.getPlayerData();
    }

    @Override
    public String getLevelName() {
        return this.properties.getLevelName();
    }

    @Override
    public int getVersion() {
        return this.properties.getVersion();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public long getLastPlayed() {
        return this.properties.getLastPlayed();
    }

    @Override
    public boolean isThundering() {
        return this.properties.isThundering();
    }

    @Override
    public int getThunderTime() {
        return this.properties.getThunderTime();
    }

    @Override
    public boolean isRaining() {
        return this.properties.isRaining();
    }

    @Override
    public int getRainTime() {
        return this.properties.getRainTime();
    }

    @Override
    public GameMode getGameMode() {
        return this.properties.getGameMode();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void setSpawnX(int spawnX) {
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void setSpawnY(int spawnY) {
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void setSpawnZ(int spawnZ) {
    }

    @Override
    public void setTime(long time) {
    }

    @Override
    public void setTimeOfDay(long timeOfDay) {
    }

    @Override
    public void setSpawnPos(BlockPos blockPos) {
    }

    @Override
    public void setLevelName(String levelName) {
    }

    @Override
    public void setVersion(int version) {
    }

    @Override
    public void setThundering(boolean thundering) {
    }

    @Override
    public void setThunderTime(int thunderTime) {
    }

    @Override
    public void setRaining(boolean raining) {
    }

    @Override
    public void setRainTime(int rainTime) {
    }

    @Override
    public boolean hasStructures() {
        return this.properties.hasStructures();
    }

    @Override
    public boolean isHardcore() {
        return this.properties.isHardcore();
    }

    @Override
    public LevelGeneratorType getGeneratorType() {
        return this.properties.getGeneratorType();
    }

    @Override
    public void setGeneratorType(LevelGeneratorType levelGeneratorType) {
    }

    @Override
    public boolean areCommandsAllowed() {
        return this.properties.areCommandsAllowed();
    }

    @Override
    public void setCommandsAllowed(boolean commandsAllowed) {
    }

    @Override
    public boolean isInitialized() {
        return this.properties.isInitialized();
    }

    @Override
    public void setInitialized(boolean initialized) {
    }

    @Override
    public GameRules getGameRules() {
        return this.properties.getGameRules();
    }

    @Override
    public Difficulty getDifficulty() {
        return this.properties.getDifficulty();
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
    }

    @Override
    public boolean isDifficultyLocked() {
        return this.properties.isDifficultyLocked();
    }

    @Override
    public void setDifficultyLocked(boolean difficultyLocked) {
    }

    @Override
    public Timer<MinecraftServer> getScheduledEvents() {
        return this.properties.getScheduledEvents();
    }

    @Override
    public void setWorldData(DimensionType type, CompoundTag compoundTag) {
        this.properties.setWorldData(type, compoundTag);
    }

    @Override
    public CompoundTag getWorldData(DimensionType dimensionType) {
        return this.properties.getWorldData(dimensionType);
    }

    @Override
    public void populateCrashReport(CrashReportSection crashReportSection) {
        crashReportSection.add("Derived", true);
        this.properties.populateCrashReport(crashReportSection);
    }
}

