/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.mojang.authlib.properties.PropertyMap
 *  com.mojang.authlib.properties.PropertyMap$Serializer
 *  joptsimple.ArgumentAcceptingOptionSpec
 *  joptsimple.NonOptionArgumentSpec
 *  joptsimple.OptionParser
 *  joptsimple.OptionSet
 *  joptsimple.OptionSpec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.blaze3d.systems.RenderCallStorage;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.List;
import java.util.OptionalInt;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.util.GlException;
import net.minecraft.client.util.Session;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.UncaughtExceptionLogger;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class Main {
    private static final Logger LOGGER = LogManager.getLogger();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] args) {
        Thread thread2;
        MinecraftClient minecraftClient;
        OptionParser optionParser = new OptionParser();
        optionParser.allowsUnrecognizedOptions();
        optionParser.accepts("demo");
        optionParser.accepts("fullscreen");
        optionParser.accepts("checkGlErrors");
        ArgumentAcceptingOptionSpec optionSpec = optionParser.accepts("server").withRequiredArg();
        ArgumentAcceptingOptionSpec optionSpec2 = optionParser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo((Object)25565, (Object[])new Integer[0]);
        ArgumentAcceptingOptionSpec optionSpec3 = optionParser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo((Object)new File("."), (Object[])new File[0]);
        ArgumentAcceptingOptionSpec optionSpec4 = optionParser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec optionSpec5 = optionParser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        ArgumentAcceptingOptionSpec optionSpec6 = optionParser.accepts("proxyHost").withRequiredArg();
        ArgumentAcceptingOptionSpec optionSpec7 = optionParser.accepts("proxyPort").withRequiredArg().defaultsTo((Object)"8080", (Object[])new String[0]).ofType(Integer.class);
        ArgumentAcceptingOptionSpec optionSpec8 = optionParser.accepts("proxyUser").withRequiredArg();
        ArgumentAcceptingOptionSpec optionSpec9 = optionParser.accepts("proxyPass").withRequiredArg();
        ArgumentAcceptingOptionSpec optionSpec10 = optionParser.accepts("username").withRequiredArg().defaultsTo((Object)("Player" + Util.getMeasuringTimeMs() % 1000L), (Object[])new String[0]);
        ArgumentAcceptingOptionSpec optionSpec11 = optionParser.accepts("uuid").withRequiredArg();
        ArgumentAcceptingOptionSpec optionSpec12 = optionParser.accepts("accessToken").withRequiredArg().required();
        ArgumentAcceptingOptionSpec optionSpec13 = optionParser.accepts("version").withRequiredArg().required();
        ArgumentAcceptingOptionSpec optionSpec14 = optionParser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo((Object)854, (Object[])new Integer[0]);
        ArgumentAcceptingOptionSpec optionSpec15 = optionParser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo((Object)480, (Object[])new Integer[0]);
        ArgumentAcceptingOptionSpec optionSpec16 = optionParser.accepts("fullscreenWidth").withRequiredArg().ofType(Integer.class);
        ArgumentAcceptingOptionSpec optionSpec17 = optionParser.accepts("fullscreenHeight").withRequiredArg().ofType(Integer.class);
        ArgumentAcceptingOptionSpec optionSpec18 = optionParser.accepts("userProperties").withRequiredArg().defaultsTo((Object)"{}", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec optionSpec19 = optionParser.accepts("profileProperties").withRequiredArg().defaultsTo((Object)"{}", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec optionSpec20 = optionParser.accepts("assetIndex").withRequiredArg();
        ArgumentAcceptingOptionSpec optionSpec21 = optionParser.accepts("userType").withRequiredArg().defaultsTo((Object)"legacy", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec optionSpec22 = optionParser.accepts("versionType").withRequiredArg().defaultsTo((Object)"release", (Object[])new String[0]);
        NonOptionArgumentSpec optionSpec23 = optionParser.nonOptions();
        OptionSet optionSet = optionParser.parse(args);
        List list = optionSet.valuesOf((OptionSpec)optionSpec23);
        if (!list.isEmpty()) {
            System.out.println("Completely ignored arguments: " + list);
        }
        String string = (String)Main.getOption(optionSet, optionSpec6);
        Proxy proxy = Proxy.NO_PROXY;
        if (string != null) {
            try {
                proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(string, (int)((Integer)Main.getOption(optionSet, optionSpec7))));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        final String string2 = (String)Main.getOption(optionSet, optionSpec8);
        final String string3 = (String)Main.getOption(optionSet, optionSpec9);
        if (!proxy.equals(Proxy.NO_PROXY) && Main.isNotNullOrEmpty(string2) && Main.isNotNullOrEmpty(string3)) {
            Authenticator.setDefault(new Authenticator(){

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(string2, string3.toCharArray());
                }
            });
        }
        int i = (Integer)Main.getOption(optionSet, optionSpec14);
        int j = (Integer)Main.getOption(optionSet, optionSpec15);
        OptionalInt optionalInt = Main.toOptional((Integer)Main.getOption(optionSet, optionSpec16));
        OptionalInt optionalInt2 = Main.toOptional((Integer)Main.getOption(optionSet, optionSpec17));
        boolean bl = optionSet.has("fullscreen");
        boolean bl2 = optionSet.has("demo");
        String string4 = (String)Main.getOption(optionSet, optionSpec13);
        Gson gson = new GsonBuilder().registerTypeAdapter(PropertyMap.class, (Object)new PropertyMap.Serializer()).create();
        PropertyMap propertyMap = JsonHelper.deserialize(gson, (String)Main.getOption(optionSet, optionSpec18), PropertyMap.class);
        PropertyMap propertyMap2 = JsonHelper.deserialize(gson, (String)Main.getOption(optionSet, optionSpec19), PropertyMap.class);
        String string5 = (String)Main.getOption(optionSet, optionSpec22);
        File file = (File)Main.getOption(optionSet, optionSpec3);
        File file2 = optionSet.has((OptionSpec)optionSpec4) ? (File)Main.getOption(optionSet, optionSpec4) : new File(file, "assets/");
        File file3 = optionSet.has((OptionSpec)optionSpec5) ? (File)Main.getOption(optionSet, optionSpec5) : new File(file, "resourcepacks/");
        String string6 = optionSet.has((OptionSpec)optionSpec11) ? (String)optionSpec11.value(optionSet) : PlayerEntity.getOfflinePlayerUuid((String)optionSpec10.value(optionSet)).toString();
        String string7 = optionSet.has((OptionSpec)optionSpec20) ? (String)optionSpec20.value(optionSet) : null;
        String string8 = (String)Main.getOption(optionSet, optionSpec);
        Integer integer = (Integer)Main.getOption(optionSet, optionSpec2);
        CrashReport.method_24305();
        Session session = new Session((String)optionSpec10.value(optionSet), string6, (String)optionSpec12.value(optionSet), (String)optionSpec21.value(optionSet));
        RunArgs runArgs = new RunArgs(new RunArgs.Network(session, propertyMap, propertyMap2, proxy), new WindowSettings(i, j, optionalInt, optionalInt2, bl), new RunArgs.Directories(file, file3, file2, string7), new RunArgs.Game(bl2, string4, string5), new RunArgs.AutoConnect(string8, integer));
        Thread thread = new Thread("Client Shutdown Thread"){

            @Override
            public void run() {
                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                if (minecraftClient == null) {
                    return;
                }
                IntegratedServer integratedServer = minecraftClient.getServer();
                if (integratedServer != null) {
                    integratedServer.stop(true);
                }
            }
        };
        thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
        Runtime.getRuntime().addShutdownHook(thread);
        RenderCallStorage renderCallStorage = new RenderCallStorage();
        try {
            Thread.currentThread().setName("Render thread");
            RenderSystem.initRenderThread();
            RenderSystem.beginInitialization();
            minecraftClient = new MinecraftClient(runArgs);
            RenderSystem.finishInitialization();
        }
        catch (GlException glException) {
            LOGGER.warn("Failed to create window: ", (Throwable)glException);
            return;
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Initializing game");
            crashReport.addElement("Initialization");
            MinecraftClient.addSystemDetailsToCrashReport(null, runArgs.game.version, null, crashReport);
            MinecraftClient.printCrashReport(crashReport);
            return;
        }
        if (minecraftClient.shouldRenderAsync()) {
            thread2 = new Thread("Game thread"){

                @Override
                public void run() {
                    try {
                        RenderSystem.initGameThread(true);
                        minecraftClient.run();
                    }
                    catch (Throwable throwable) {
                        LOGGER.error("Exception in client thread", throwable);
                    }
                }
            };
            thread2.start();
            while (minecraftClient.isRunning()) {
            }
        } else {
            thread2 = null;
            try {
                RenderSystem.initGameThread(false);
                minecraftClient.run();
            }
            catch (Throwable throwable2) {
                LOGGER.error("Unhandled game exception", throwable2);
            }
        }
        try {
            minecraftClient.scheduleStop();
            if (thread2 != null) {
                thread2.join();
            }
        }
        catch (InterruptedException interruptedException) {
            LOGGER.error("Exception during client thread shutdown", (Throwable)interruptedException);
        }
        finally {
            minecraftClient.stop();
        }
    }

    private static OptionalInt toOptional(@Nullable Integer i) {
        return i != null ? OptionalInt.of(i) : OptionalInt.empty();
    }

    @Nullable
    private static <T> T getOption(OptionSet optionSet, OptionSpec<T> optionSpec) {
        try {
            return (T)optionSet.valueOf(optionSpec);
        }
        catch (Throwable throwable) {
            ArgumentAcceptingOptionSpec argumentAcceptingOptionSpec;
            List list;
            if (optionSpec instanceof ArgumentAcceptingOptionSpec && !(list = (argumentAcceptingOptionSpec = (ArgumentAcceptingOptionSpec)optionSpec).defaultValues()).isEmpty()) {
                return (T)list.get(0);
            }
            throw throwable;
        }
    }

    private static boolean isNotNullOrEmpty(@Nullable String s) {
        return s != null && !s.isEmpty();
    }

    static {
        System.setProperty("java.awt.headless", "true");
    }
}

