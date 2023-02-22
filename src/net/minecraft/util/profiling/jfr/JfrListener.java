/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.util.profiling.jfr;

import com.mojang.logging.LogUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Supplier;
import net.minecraft.Bootstrap;
import net.minecraft.util.profiling.jfr.JfrProfile;
import net.minecraft.util.profiling.jfr.JfrProfileRecorder;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class JfrListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Runnable stopCallback;

    protected JfrListener(Runnable stopCallback) {
        this.stopCallback = stopCallback;
    }

    public void stop(@Nullable Path dumpPath) {
        JfrProfile jfrProfile;
        if (dumpPath == null) {
            return;
        }
        this.stopCallback.run();
        JfrListener.log(() -> "Dumped flight recorder profiling to " + dumpPath);
        try {
            jfrProfile = JfrProfileRecorder.readProfile(dumpPath);
        }
        catch (Throwable throwable) {
            JfrListener.warn(() -> "Failed to parse JFR recording", throwable);
            return;
        }
        try {
            JfrListener.log(jfrProfile::toJson);
            Path path = dumpPath.resolveSibling("jfr-report-" + StringUtils.substringBefore((String)dumpPath.getFileName().toString(), (String)".jfr") + ".json");
            Files.writeString(path, (CharSequence)jfrProfile.toJson(), StandardOpenOption.CREATE);
            JfrListener.log(() -> "Dumped recording summary to " + path);
        }
        catch (Throwable throwable) {
            JfrListener.warn(() -> "Failed to output JFR report", throwable);
        }
    }

    private static void log(Supplier<String> supplier) {
        if (LogUtils.isLoggerActive()) {
            LOGGER.info(supplier.get());
        } else {
            Bootstrap.println(supplier.get());
        }
    }

    private static void warn(Supplier<String> supplier, Throwable throwable) {
        if (LogUtils.isLoggerActive()) {
            LOGGER.warn(supplier.get(), throwable);
        } else {
            Bootstrap.println(supplier.get());
            throwable.printStackTrace(Bootstrap.SYSOUT);
        }
    }
}

