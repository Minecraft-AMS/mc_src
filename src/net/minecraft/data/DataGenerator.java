/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  com.google.common.collect.Lists
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.data;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraft.Bootstrap;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataGenerator {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Collection<Path> inputs;
    private final Path output;
    private final List<DataProvider> providers = Lists.newArrayList();

    public DataGenerator(Path output, Collection<Path> collection) {
        this.output = output;
        this.inputs = collection;
    }

    public Collection<Path> getInputs() {
        return this.inputs;
    }

    public Path getOutput() {
        return this.output;
    }

    public void run() throws IOException {
        DataCache dataCache = new DataCache(this.output, "cache");
        dataCache.ignore(this.getOutput().resolve("version.json"));
        Stopwatch stopwatch = Stopwatch.createStarted();
        Stopwatch stopwatch2 = Stopwatch.createUnstarted();
        for (DataProvider dataProvider : this.providers) {
            LOGGER.info("Starting provider: {}", (Object)dataProvider.getName());
            stopwatch2.start();
            dataProvider.run(dataCache);
            stopwatch2.stop();
            LOGGER.info("{} finished after {} ms", (Object)dataProvider.getName(), (Object)stopwatch2.elapsed(TimeUnit.MILLISECONDS));
            stopwatch2.reset();
        }
        LOGGER.info("All providers took: {} ms", (Object)stopwatch.elapsed(TimeUnit.MILLISECONDS));
        dataCache.write();
    }

    public void install(DataProvider dataProvider) {
        this.providers.add(dataProvider);
    }

    static {
        Bootstrap.initialize();
    }
}

