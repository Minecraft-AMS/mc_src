/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Lifecycle
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.test;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import java.net.Proxy;
import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.datafixer.Schemas;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressLogger;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureSet;
import net.minecraft.test.GameTestBatch;
import net.minecraft.test.GameTestState;
import net.minecraft.test.TestFailureLogger;
import net.minecraft.test.TestManager;
import net.minecraft.test.TestSet;
import net.minecraft.test.TestUtil;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.SystemDetails;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class TestServer
extends MinecraftServer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int RESULT_STRING_LOG_INTERVAL = 20;
    private final List<GameTestBatch> batches;
    private final BlockPos pos;
    private static final GameRules GAME_RULES = Util.make(new GameRules(), gameRules -> {
        gameRules.get(GameRules.DO_MOB_SPAWNING).set(false, null);
        gameRules.get(GameRules.DO_WEATHER_CYCLE).set(false, null);
    });
    private static final LevelInfo TEST_LEVEL = new LevelInfo("Test Level", GameMode.CREATIVE, false, Difficulty.NORMAL, true, GAME_RULES, DataPackSettings.SAFE_MODE);
    @Nullable
    private TestSet testSet;

    public static TestServer create(Thread thread, LevelStorage.Session session, ResourcePackManager resourcePackManager, Collection<GameTestBatch> batches, BlockPos pos) {
        if (batches.isEmpty()) {
            throw new IllegalArgumentException("No test batches were given!");
        }
        SaveLoader.FunctionLoaderConfig functionLoaderConfig = new SaveLoader.FunctionLoaderConfig(resourcePackManager, CommandManager.RegistrationEnvironment.DEDICATED, 4, false);
        try {
            SaveLoader saveLoader = SaveLoader.ofLoaded(functionLoaderConfig, () -> DataPackSettings.SAFE_MODE, (resourceManager, dataPackSettings) -> {
                DynamicRegistryManager.Immutable immutable = DynamicRegistryManager.BUILTIN.get();
                Registry<Biome> registry = immutable.get(Registry.BIOME_KEY);
                Registry<StructureSet> registry2 = immutable.get(Registry.STRUCTURE_SET_KEY);
                Registry<DimensionType> registry3 = immutable.get(Registry.DIMENSION_TYPE_KEY);
                LevelProperties saveProperties = new LevelProperties(TEST_LEVEL, new GeneratorOptions(0L, false, false, GeneratorOptions.getRegistryWithReplacedOverworldGenerator(registry3, DimensionType.createDefaultDimensionOptions(immutable, 0L), new FlatChunkGenerator(registry2, FlatChunkGeneratorConfig.getDefaultConfig(registry, registry2)))), Lifecycle.stable());
                return Pair.of((Object)saveProperties, (Object)immutable);
            }, Util.getMainWorkerExecutor(), Runnable::run).get();
            saveLoader.refresh();
            return new TestServer(thread, session, resourcePackManager, saveLoader, batches, pos);
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to load vanilla datapack, bit oops", (Throwable)exception);
            System.exit(-1);
            throw new IllegalStateException();
        }
    }

    private TestServer(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, Collection<GameTestBatch> batches, BlockPos pos) {
        super(serverThread, session, dataPackManager, saveLoader, Proxy.NO_PROXY, Schemas.getFixer(), null, null, null, WorldGenerationProgressLogger::new);
        this.batches = Lists.newArrayList(batches);
        this.pos = pos;
    }

    @Override
    public boolean setupServer() {
        this.setPlayerManager(new PlayerManager(this, this.getRegistryManager(), this.saveHandler, 1){});
        this.loadWorld();
        ServerWorld serverWorld = this.getOverworld();
        serverWorld.setSpawnPos(this.pos, 0.0f);
        int i = 20000000;
        serverWorld.setWeather(20000000, 20000000, false, false);
        return true;
    }

    @Override
    public void tick(BooleanSupplier shouldKeepTicking) {
        super.tick(shouldKeepTicking);
        ServerWorld serverWorld = this.getOverworld();
        if (!this.isTesting()) {
            this.runTestBatches(serverWorld);
        }
        if (serverWorld.getTime() % 20L == 0L) {
            LOGGER.info(this.testSet.getResultString());
        }
        if (this.testSet.isDone()) {
            this.stop(false);
            LOGGER.info(this.testSet.getResultString());
            TestFailureLogger.stop();
            LOGGER.info("========= {} GAME TESTS COMPLETE ======================", (Object)this.testSet.getTestCount());
            if (this.testSet.failed()) {
                LOGGER.info("{} required tests failed :(", (Object)this.testSet.getFailedRequiredTestCount());
                this.testSet.getRequiredTests().forEach(test -> LOGGER.info("   - {}", (Object)test.getStructurePath()));
            } else {
                LOGGER.info("All {} required tests passed :)", (Object)this.testSet.getTestCount());
            }
            if (this.testSet.hasFailedOptionalTests()) {
                LOGGER.info("{} optional tests failed", (Object)this.testSet.getFailedOptionalTestCount());
                this.testSet.getOptionalTests().forEach(test -> LOGGER.info("   - {}", (Object)test.getStructurePath()));
            }
            LOGGER.info("====================================================");
        }
    }

    @Override
    public SystemDetails addExtraSystemDetails(SystemDetails details) {
        details.addSection("Type", "Game test server");
        return details;
    }

    @Override
    public void exit() {
        super.exit();
        System.exit(this.testSet.getFailedRequiredTestCount());
    }

    @Override
    public void setCrashReport(CrashReport report) {
        System.exit(1);
    }

    private void runTestBatches(ServerWorld world) {
        Collection<GameTestState> collection = TestUtil.runTestBatches(this.batches, new BlockPos(0, -60, 0), BlockRotation.NONE, world, TestManager.INSTANCE, 8);
        this.testSet = new TestSet(collection);
        LOGGER.info("{} tests are now running!", (Object)this.testSet.getTestCount());
    }

    private boolean isTesting() {
        return this.testSet != null;
    }

    @Override
    public boolean isHardcore() {
        return false;
    }

    @Override
    public int getOpPermissionLevel() {
        return 0;
    }

    @Override
    public int getFunctionPermissionLevel() {
        return 4;
    }

    @Override
    public boolean shouldBroadcastRconToOps() {
        return false;
    }

    @Override
    public boolean isDedicated() {
        return false;
    }

    @Override
    public int getRateLimit() {
        return 0;
    }

    @Override
    public boolean isUsingNativeTransport() {
        return false;
    }

    @Override
    public boolean areCommandBlocksEnabled() {
        return true;
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public boolean shouldBroadcastConsoleToOps() {
        return false;
    }

    @Override
    public boolean isHost(GameProfile profile) {
        return false;
    }
}

