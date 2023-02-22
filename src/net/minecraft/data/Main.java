/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  joptsimple.AbstractOptionSpec
 *  joptsimple.ArgumentAcceptingOptionSpec
 *  joptsimple.OptionParser
 *  joptsimple.OptionSet
 *  joptsimple.OptionSpec
 *  joptsimple.OptionSpecBuilder
 */
package net.minecraft.data;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import net.minecraft.GameVersion;
import net.minecraft.SharedConstants;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.SnbtProvider;
import net.minecraft.data.client.ModelProvider;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.data.report.BlockListProvider;
import net.minecraft.data.report.CommandSyntaxProvider;
import net.minecraft.data.report.RegistryDumpProvider;
import net.minecraft.data.report.WorldgenProvider;
import net.minecraft.data.server.AdvancementProvider;
import net.minecraft.data.server.BannerPatternTagProvider;
import net.minecraft.data.server.BiomeParametersProvider;
import net.minecraft.data.server.BiomeTagProvider;
import net.minecraft.data.server.BlockTagProvider;
import net.minecraft.data.server.CatVariantTagProvider;
import net.minecraft.data.server.EntityTypeTagProvider;
import net.minecraft.data.server.FlatLevelGeneratorPresetTagProvider;
import net.minecraft.data.server.FluidTagProvider;
import net.minecraft.data.server.GameEventTagProvider;
import net.minecraft.data.server.InstrumentTagProvider;
import net.minecraft.data.server.ItemTagProvider;
import net.minecraft.data.server.LootTableProvider;
import net.minecraft.data.server.PaintingVariantTagProvider;
import net.minecraft.data.server.PointOfInterestTypeTagProvider;
import net.minecraft.data.server.RecipeProvider;
import net.minecraft.data.server.StructureTagProvider;
import net.minecraft.data.server.WorldPresetTagProvider;
import net.minecraft.data.validate.StructureValidatorProvider;
import net.minecraft.obfuscate.DontObfuscate;

public class Main {
    @DontObfuscate
    public static void main(String[] args) throws IOException {
        SharedConstants.createGameVersion();
        OptionParser optionParser = new OptionParser();
        AbstractOptionSpec optionSpec = optionParser.accepts("help", "Show the help menu").forHelp();
        OptionSpecBuilder optionSpec2 = optionParser.accepts("server", "Include server generators");
        OptionSpecBuilder optionSpec3 = optionParser.accepts("client", "Include client generators");
        OptionSpecBuilder optionSpec4 = optionParser.accepts("dev", "Include development tools");
        OptionSpecBuilder optionSpec5 = optionParser.accepts("reports", "Include data reports");
        OptionSpecBuilder optionSpec6 = optionParser.accepts("validate", "Validate inputs");
        OptionSpecBuilder optionSpec7 = optionParser.accepts("all", "Include all generators");
        ArgumentAcceptingOptionSpec optionSpec8 = optionParser.accepts("output", "Output folder").withRequiredArg().defaultsTo((Object)"generated", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec optionSpec9 = optionParser.accepts("input", "Input folder").withRequiredArg();
        OptionSet optionSet = optionParser.parse(args);
        if (optionSet.has((OptionSpec)optionSpec) || !optionSet.hasOptions()) {
            optionParser.printHelpOn((OutputStream)System.out);
            return;
        }
        Path path = Paths.get((String)optionSpec8.value(optionSet), new String[0]);
        boolean bl = optionSet.has((OptionSpec)optionSpec7);
        boolean bl2 = bl || optionSet.has((OptionSpec)optionSpec3);
        boolean bl3 = bl || optionSet.has((OptionSpec)optionSpec2);
        boolean bl4 = bl || optionSet.has((OptionSpec)optionSpec4);
        boolean bl5 = bl || optionSet.has((OptionSpec)optionSpec5);
        boolean bl6 = bl || optionSet.has((OptionSpec)optionSpec6);
        DataGenerator dataGenerator = Main.create(path, optionSet.valuesOf((OptionSpec)optionSpec9).stream().map(input -> Paths.get(input, new String[0])).collect(Collectors.toList()), bl2, bl3, bl4, bl5, bl6, SharedConstants.getGameVersion(), true);
        dataGenerator.run();
    }

    public static DataGenerator create(Path output, Collection<Path> inputs, boolean includeClient, boolean includeServer, boolean includeDev, boolean includeReports, boolean validate, GameVersion gameVersion, boolean ignoreCache) {
        DataGenerator dataGenerator = new DataGenerator(output, inputs, gameVersion, ignoreCache);
        dataGenerator.addProvider(includeClient || includeServer, new SnbtProvider(dataGenerator).addWriter(new StructureValidatorProvider()));
        dataGenerator.addProvider(includeClient, new ModelProvider(dataGenerator));
        dataGenerator.addProvider(includeServer, new AdvancementProvider(dataGenerator));
        dataGenerator.addProvider(includeServer, new LootTableProvider(dataGenerator));
        dataGenerator.addProvider(includeServer, new RecipeProvider(dataGenerator));
        BlockTagProvider blockTagProvider = new BlockTagProvider(dataGenerator);
        dataGenerator.addProvider(includeServer, blockTagProvider);
        dataGenerator.addProvider(includeServer, new ItemTagProvider(dataGenerator, blockTagProvider));
        dataGenerator.addProvider(includeServer, new BannerPatternTagProvider(dataGenerator));
        dataGenerator.addProvider(includeServer, new BiomeTagProvider(dataGenerator));
        dataGenerator.addProvider(includeServer, new CatVariantTagProvider(dataGenerator));
        dataGenerator.addProvider(includeServer, new EntityTypeTagProvider(dataGenerator));
        dataGenerator.addProvider(includeServer, new FlatLevelGeneratorPresetTagProvider(dataGenerator));
        dataGenerator.addProvider(includeServer, new FluidTagProvider(dataGenerator));
        dataGenerator.addProvider(includeServer, new GameEventTagProvider(dataGenerator));
        dataGenerator.addProvider(includeServer, new InstrumentTagProvider(dataGenerator));
        dataGenerator.addProvider(includeServer, new PaintingVariantTagProvider(dataGenerator));
        dataGenerator.addProvider(includeServer, new PointOfInterestTypeTagProvider(dataGenerator));
        dataGenerator.addProvider(includeServer, new StructureTagProvider(dataGenerator));
        dataGenerator.addProvider(includeServer, new WorldPresetTagProvider(dataGenerator));
        dataGenerator.addProvider(includeDev, new NbtProvider(dataGenerator));
        dataGenerator.addProvider(includeReports, new BiomeParametersProvider(dataGenerator));
        dataGenerator.addProvider(includeReports, new BlockListProvider(dataGenerator));
        dataGenerator.addProvider(includeReports, new CommandSyntaxProvider(dataGenerator));
        dataGenerator.addProvider(includeReports, new RegistryDumpProvider(dataGenerator));
        dataGenerator.addProvider(includeReports, new WorldgenProvider(dataGenerator));
        return dataGenerator;
    }
}

