/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  org.slf4j.Logger
 */
package net.minecraft.data;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraft.Bootstrap;
import net.minecraft.GameVersion;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataProvider;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class DataGenerator {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Collection<Path> inputs;
    private final Path output;
    private final List<DataProvider> providers = Lists.newArrayList();
    private final List<DataProvider> runningProviders = Lists.newArrayList();
    private final GameVersion gameVersion;
    private final boolean ignoreCache;

    public DataGenerator(Path output, Collection<Path> inputs, GameVersion gameVersion, boolean ignoreCache) {
        this.output = output;
        this.inputs = inputs;
        this.gameVersion = gameVersion;
        this.ignoreCache = ignoreCache;
    }

    public Collection<Path> getInputs() {
        return this.inputs;
    }

    public Path getOutput() {
        return this.output;
    }

    public Path resolveRootDirectoryPath(OutputType outputType) {
        return this.getOutput().resolve(outputType.path);
    }

    public void run() throws IOException {
        DataCache dataCache = new DataCache(this.output, this.providers, this.gameVersion);
        Stopwatch stopwatch = Stopwatch.createStarted();
        Stopwatch stopwatch2 = Stopwatch.createUnstarted();
        for (DataProvider dataProvider : this.runningProviders) {
            if (!this.ignoreCache && !dataCache.isVersionDifferent(dataProvider)) {
                LOGGER.debug("Generator {} already run for version {}", (Object)dataProvider.getName(), (Object)this.gameVersion.getName());
                continue;
            }
            LOGGER.info("Starting provider: {}", (Object)dataProvider.getName());
            stopwatch2.start();
            dataProvider.run(dataCache.getOrCreateWriter(dataProvider));
            stopwatch2.stop();
            LOGGER.info("{} finished after {} ms", (Object)dataProvider.getName(), (Object)stopwatch2.elapsed(TimeUnit.MILLISECONDS));
            stopwatch2.reset();
        }
        LOGGER.info("All providers took: {} ms", (Object)stopwatch.elapsed(TimeUnit.MILLISECONDS));
        dataCache.write();
    }

    public void addProvider(boolean shouldRun, DataProvider provider) {
        if (shouldRun) {
            this.runningProviders.add(provider);
        }
        this.providers.add(provider);
    }

    public PathResolver createPathResolver(OutputType outputType, String directoryName) {
        return new PathResolver(this, outputType, directoryName);
    }

    static {
        Bootstrap.initialize();
    }

    public static final class OutputType
    extends Enum<OutputType> {
        public static final /* enum */ OutputType DATA_PACK = new OutputType("data");
        public static final /* enum */ OutputType RESOURCE_PACK = new OutputType("assets");
        public static final /* enum */ OutputType REPORTS = new OutputType("reports");
        final String path;
        private static final /* synthetic */ OutputType[] field_39371;

        public static OutputType[] values() {
            return (OutputType[])field_39371.clone();
        }

        public static OutputType valueOf(String string) {
            return Enum.valueOf(OutputType.class, string);
        }

        private OutputType(String path) {
            this.path = path;
        }

        private static /* synthetic */ OutputType[] method_44109() {
            return new OutputType[]{DATA_PACK, RESOURCE_PACK, REPORTS};
        }

        static {
            field_39371 = OutputType.method_44109();
        }
    }

    public static class PathResolver {
        private final Path rootPath;
        private final String directoryName;

        PathResolver(DataGenerator dataGenerator, OutputType outputType, String directoryName) {
            this.rootPath = dataGenerator.resolveRootDirectoryPath(outputType);
            this.directoryName = directoryName;
        }

        public Path resolve(Identifier id, String fileExtension) {
            return this.rootPath.resolve(id.getNamespace()).resolve(this.directoryName).resolve(id.getPath() + "." + fileExtension);
        }

        public Path resolveJson(Identifier id) {
            return this.rootPath.resolve(id.getNamespace()).resolve(this.directoryName).resolve(id.getPath() + ".json");
        }
    }
}

