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
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
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
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.MetadataProvider;
import net.minecraft.data.SnbtProvider;
import net.minecraft.data.client.ModelProvider;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.data.report.BlockListProvider;
import net.minecraft.data.report.CommandSyntaxProvider;
import net.minecraft.data.report.DynamicRegistriesProvider;
import net.minecraft.data.report.RegistryDumpProvider;
import net.minecraft.data.server.BiomeParametersProvider;
import net.minecraft.data.server.advancement.onetwenty.OneTwentyAdvancementProviders;
import net.minecraft.data.server.advancement.vanilla.VanillaAdvancementProviders;
import net.minecraft.data.server.loottable.onetwenty.OneTwentyLootTableProviders;
import net.minecraft.data.server.loottable.vanilla.VanillaLootTableProviders;
import net.minecraft.data.server.recipe.BundleRecipeProvider;
import net.minecraft.data.server.recipe.OneTwentyRecipeProvider;
import net.minecraft.data.server.recipe.VanillaRecipeProvider;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.data.server.tag.onetwenty.OneTwentyBiomeTagProvider;
import net.minecraft.data.server.tag.onetwenty.OneTwentyBlockTagProvider;
import net.minecraft.data.server.tag.onetwenty.OneTwentyItemTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaBannerPatternTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaBiomeTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaBlockTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaCatVariantTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaDamageTypeTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaEntityTypeTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaFlatLevelGeneratorPresetTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaFluidTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaGameEventTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaInstrumentTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaItemTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaPaintingVariantTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaPointOfInterestTypeTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaStructureTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaWorldPresetTagProvider;
import net.minecraft.data.validate.StructureValidatorProvider;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.OneTwentyBuiltinRegistries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

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

    private static <T extends DataProvider> DataProvider.Factory<T> toFactory(BiFunction<DataOutput, CompletableFuture<RegistryWrapper.WrapperLookup>, T> baseFactory, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        return output -> (DataProvider)baseFactory.apply(output, registryLookupFuture);
    }

    public static DataGenerator create(Path output2, Collection<Path> inputs, boolean includeClient, boolean includeServer, boolean includeDev, boolean includeReports, boolean validate, GameVersion gameVersion, boolean ignoreCache) {
        DataGenerator dataGenerator = new DataGenerator(output2, gameVersion, ignoreCache);
        DataGenerator.Pack pack = dataGenerator.createVanillaPack(includeClient || includeServer);
        pack.addProvider(output -> new SnbtProvider(output, inputs).addWriter(new StructureValidatorProvider()));
        CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture = CompletableFuture.supplyAsync(BuiltinRegistries::createWrapperLookup, Util.getMainWorkerExecutor());
        DataGenerator.Pack pack2 = dataGenerator.createVanillaPack(includeClient);
        pack2.addProvider(ModelProvider::new);
        DataGenerator.Pack pack3 = dataGenerator.createVanillaPack(includeServer);
        pack3.addProvider(Main.toFactory(DynamicRegistriesProvider::new, completableFuture));
        pack3.addProvider(Main.toFactory(VanillaAdvancementProviders::createVanillaProvider, completableFuture));
        pack3.addProvider(VanillaLootTableProviders::createVanillaProvider);
        pack3.addProvider(VanillaRecipeProvider::new);
        TagProvider tagProvider = pack3.addProvider(Main.toFactory(VanillaBlockTagProvider::new, completableFuture));
        TagProvider tagProvider2 = pack3.addProvider(output -> new VanillaItemTagProvider(output, completableFuture, tagProvider.getTagLookupFuture()));
        pack3.addProvider(Main.toFactory(VanillaBannerPatternTagProvider::new, completableFuture));
        pack3.addProvider(Main.toFactory(VanillaBiomeTagProvider::new, completableFuture));
        pack3.addProvider(Main.toFactory(VanillaCatVariantTagProvider::new, completableFuture));
        pack3.addProvider(Main.toFactory(VanillaDamageTypeTagProvider::new, completableFuture));
        pack3.addProvider(Main.toFactory(VanillaEntityTypeTagProvider::new, completableFuture));
        pack3.addProvider(Main.toFactory(VanillaFlatLevelGeneratorPresetTagProvider::new, completableFuture));
        pack3.addProvider(Main.toFactory(VanillaFluidTagProvider::new, completableFuture));
        pack3.addProvider(Main.toFactory(VanillaGameEventTagProvider::new, completableFuture));
        pack3.addProvider(Main.toFactory(VanillaInstrumentTagProvider::new, completableFuture));
        pack3.addProvider(Main.toFactory(VanillaPaintingVariantTagProvider::new, completableFuture));
        pack3.addProvider(Main.toFactory(VanillaPointOfInterestTypeTagProvider::new, completableFuture));
        pack3.addProvider(Main.toFactory(VanillaStructureTagProvider::new, completableFuture));
        pack3.addProvider(Main.toFactory(VanillaWorldPresetTagProvider::new, completableFuture));
        pack3 = dataGenerator.createVanillaPack(includeDev);
        pack3.addProvider(output -> new NbtProvider(output, inputs));
        pack3 = dataGenerator.createVanillaPack(includeReports);
        pack3.addProvider(Main.toFactory(BiomeParametersProvider::new, completableFuture));
        pack3.addProvider(BlockListProvider::new);
        pack3.addProvider(Main.toFactory(CommandSyntaxProvider::new, completableFuture));
        pack3.addProvider(RegistryDumpProvider::new);
        pack3 = dataGenerator.createVanillaSubPack(includeServer, "bundle");
        pack3.addProvider(BundleRecipeProvider::new);
        pack3.addProvider(output -> MetadataProvider.create(output, Text.translatable("dataPack.bundle.description"), FeatureSet.of(FeatureFlags.BUNDLE)));
        CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture2 = OneTwentyBuiltinRegistries.createWrapperLookup(completableFuture);
        DataGenerator.Pack pack4 = dataGenerator.createVanillaSubPack(includeServer, "update_1_20");
        pack4.addProvider(OneTwentyRecipeProvider::new);
        TagProvider tagProvider3 = pack4.addProvider(output -> new OneTwentyBlockTagProvider(output, completableFuture2, tagProvider.getTagLookupFuture()));
        pack4.addProvider(output -> new OneTwentyItemTagProvider(output, completableFuture2, tagProvider2.getTagLookupFuture(), tagProvider3.getTagLookupFuture()));
        pack4.addProvider(Main.toFactory(OneTwentyBiomeTagProvider::new, completableFuture2));
        pack4.addProvider(OneTwentyLootTableProviders::createOneTwentyProvider);
        pack4.addProvider(Main.toFactory(OneTwentyAdvancementProviders::createOneTwentyProvider, completableFuture2));
        pack4.addProvider(Main.toFactory(DynamicRegistriesProvider::new, completableFuture2));
        pack4.addProvider(output -> MetadataProvider.create(output, Text.translatable("dataPack.update_1_20.description"), FeatureSet.of(FeatureFlags.UPDATE_1_20)));
        return dataGenerator;
    }
}

