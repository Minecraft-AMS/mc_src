/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  org.apache.commons.io.IOUtils
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.TestClassArgumentType;
import net.minecraft.command.arguments.TestFunctionArgumentType;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.GameTest;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestFunctions;
import net.minecraft.test.TestListener;
import net.minecraft.test.TestManager;
import net.minecraft.test.TestSet;
import net.minecraft.test.TestUtil;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

public class TestCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("test").then(CommandManager.literal("runthis").executes(commandContext -> TestCommand.executeRunThis((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("runthese").executes(commandContext -> TestCommand.executeRunThese((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("run").then(CommandManager.argument("testName", TestFunctionArgumentType.testFunction()).executes(commandContext -> TestCommand.executeRun((ServerCommandSource)commandContext.getSource(), TestFunctionArgumentType.getFunction((CommandContext<ServerCommandSource>)commandContext, "testName")))))).then(((LiteralArgumentBuilder)CommandManager.literal("runall").executes(commandContext -> TestCommand.executeRunAll((ServerCommandSource)commandContext.getSource()))).then(CommandManager.argument("testClassName", TestClassArgumentType.method_22370()).executes(commandContext -> TestCommand.executeRunAll((ServerCommandSource)commandContext.getSource(), TestClassArgumentType.getTestClass((CommandContext<ServerCommandSource>)commandContext, "testClassName")))))).then(CommandManager.literal("export").then(CommandManager.argument("testName", StringArgumentType.word()).executes(commandContext -> TestCommand.executeExport((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"testName")))))).then(CommandManager.literal("import").then(CommandManager.argument("testName", StringArgumentType.word()).executes(commandContext -> TestCommand.executeImport((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"testName")))))).then(((LiteralArgumentBuilder)CommandManager.literal("pos").executes(commandContext -> TestCommand.executePos((ServerCommandSource)commandContext.getSource(), "pos"))).then(CommandManager.argument("var", StringArgumentType.word()).executes(commandContext -> TestCommand.executePos((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"var")))))).then(CommandManager.literal("create").then(((RequiredArgumentBuilder)CommandManager.argument("testName", StringArgumentType.word()).executes(commandContext -> TestCommand.executeCreate((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"testName"), 5, 5, 5))).then(((RequiredArgumentBuilder)CommandManager.argument("width", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.executeCreate((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"testName"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"width"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"width"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"width")))).then(CommandManager.argument("height", IntegerArgumentType.integer()).then(CommandManager.argument("depth", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.executeCreate((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString((CommandContext)commandContext, (String)"testName"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"width"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"height"), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"depth"))))))))).then(((LiteralArgumentBuilder)CommandManager.literal("clearall").executes(commandContext -> TestCommand.executeClearAll((ServerCommandSource)commandContext.getSource(), 200))).then(CommandManager.argument("radius", IntegerArgumentType.integer()).executes(commandContext -> TestCommand.executeClearAll((ServerCommandSource)commandContext.getSource(), IntegerArgumentType.getInteger((CommandContext)commandContext, (String)"radius"))))));
    }

    private static int executeCreate(ServerCommandSource source, String structure, int x, int y, int z) {
        if (x > 32 || y > 32 || z > 32) {
            throw new IllegalArgumentException("The structure must be less than 32 blocks big in each axis");
        }
        ServerWorld serverWorld = source.getWorld();
        BlockPos blockPos = new BlockPos(source.getPosition());
        BlockPos blockPos2 = new BlockPos(blockPos.getX(), source.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, blockPos).getY(), blockPos.getZ() + 3);
        StructureTestUtil.createTestArea(structure.toLowerCase(), blockPos2, new BlockPos(x, y, z), 2, serverWorld);
        for (int i = 0; i < x; ++i) {
            for (int j = 0; j < z; ++j) {
                BlockPos blockPos3 = new BlockPos(blockPos2.getX() + i, blockPos2.getY() + 1, blockPos2.getZ() + j);
                Block block = Blocks.POLISHED_ANDESITE;
                BlockStateArgument blockStateArgument = new BlockStateArgument(block.getDefaultState(), Collections.EMPTY_SET, null);
                blockStateArgument.setBlockState(serverWorld, blockPos3, 2);
            }
        }
        StructureTestUtil.placeStartButton(blockPos2.add(1, 0, -1), serverWorld);
        return 0;
    }

    private static int executePos(ServerCommandSource source, String variableName) throws CommandSyntaxException {
        ServerWorld serverWorld;
        BlockHitResult blockHitResult = (BlockHitResult)source.getPlayer().rayTrace(10.0, 1.0f, false);
        BlockPos blockPos = blockHitResult.getBlockPos();
        Optional<BlockPos> optional = StructureTestUtil.findContainingStructureBlock(blockPos, 15, serverWorld = source.getWorld());
        if (!optional.isPresent()) {
            optional = StructureTestUtil.findContainingStructureBlock(blockPos, 200, serverWorld);
        }
        if (!optional.isPresent()) {
            source.sendError(new LiteralText("Can't find a structure block that contains the targeted pos " + blockPos));
            return 0;
        }
        StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)serverWorld.getBlockEntity(optional.get());
        BlockPos blockPos2 = blockPos.subtract(optional.get());
        String string = blockPos2.getX() + ", " + blockPos2.getY() + ", " + blockPos2.getZ();
        String string2 = structureBlockBlockEntity.getStructurePath();
        Text text = new LiteralText(string).setStyle(new Style().setBold(true).setColor(Formatting.GREEN).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Click to copy to clipboard"))).setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "final BlockPos " + variableName + " = new BlockPos(" + string + ");")));
        source.sendFeedback(new LiteralText("Position relative to " + string2 + ": ").append(text), false);
        DebugInfoSender.addGameTestMarker(serverWorld, new BlockPos(blockPos), string, -2147418368, 10000);
        return 1;
    }

    private static int executeRunThis(ServerCommandSource source) {
        ServerWorld serverWorld;
        BlockPos blockPos = new BlockPos(source.getPosition());
        BlockPos blockPos2 = StructureTestUtil.findNearestStructureBlock(blockPos, 15, serverWorld = source.getWorld());
        if (blockPos2 == null) {
            TestCommand.sendMessage(serverWorld, "Couldn't find any structure block within 15 radius", Formatting.RED);
            return 0;
        }
        TestUtil.clearDebugMarkers(serverWorld);
        TestCommand.run(serverWorld, blockPos2, null);
        return 1;
    }

    private static int executeRunThese(ServerCommandSource source) {
        ServerWorld serverWorld;
        BlockPos blockPos2 = new BlockPos(source.getPosition());
        Collection<BlockPos> collection = StructureTestUtil.findStructureBlocks(blockPos2, 200, serverWorld = source.getWorld());
        if (collection.isEmpty()) {
            TestCommand.sendMessage(serverWorld, "Couldn't find any structure blocks within 200 block radius", Formatting.RED);
            return 1;
        }
        TestUtil.clearDebugMarkers(serverWorld);
        TestCommand.sendMessage(source, "Running " + collection.size() + " tests...");
        TestSet testSet = new TestSet();
        collection.forEach(blockPos -> TestCommand.run(serverWorld, blockPos, testSet));
        return 1;
    }

    private static void run(ServerWorld world, BlockPos pos, @Nullable TestSet tests) {
        StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)world.getBlockEntity(pos);
        String string = structureBlockBlockEntity.getStructurePath();
        TestFunction testFunction = TestFunctions.getTestFunctionOrThrow(string);
        GameTest gameTest = new GameTest(testFunction, pos, world);
        if (tests != null) {
            tests.add(gameTest);
            gameTest.addListener(new Listener(world, tests));
        }
        TestCommand.setWorld(testFunction, world);
        TestUtil.startTest(gameTest, TestManager.INSTANCE);
    }

    private static void onCompletion(ServerWorld world, TestSet tests) {
        if (tests.isDone()) {
            TestCommand.sendMessage(world, "GameTest done! " + tests.getTestCount() + " tests were run", Formatting.WHITE);
            if (tests.failed()) {
                TestCommand.sendMessage(world, "" + tests.getFailedRequiredTestCount() + " required tests failed :(", Formatting.RED);
            } else {
                TestCommand.sendMessage(world, "All required tests passed :)", Formatting.GREEN);
            }
            if (tests.hasFailedOptionalTests()) {
                TestCommand.sendMessage(world, "" + tests.getFailedOptionalTestCount() + " optional tests failed", Formatting.GRAY);
            }
        }
    }

    private static int executeClearAll(ServerCommandSource source, int radius) {
        ServerWorld serverWorld = source.getWorld();
        TestUtil.clearDebugMarkers(serverWorld);
        BlockPos blockPos = new BlockPos(source.getPosition().x, (double)source.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, new BlockPos(source.getPosition())).getY(), source.getPosition().z);
        TestUtil.clearTests(serverWorld, blockPos, TestManager.INSTANCE, MathHelper.clamp(radius, 0, 1024));
        return 1;
    }

    private static int executeRun(ServerCommandSource source, TestFunction testFunction) {
        ServerWorld serverWorld = source.getWorld();
        BlockPos blockPos = new BlockPos(source.getPosition());
        BlockPos blockPos2 = new BlockPos(blockPos.getX(), source.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, blockPos).getY(), blockPos.getZ() + 3);
        TestUtil.clearDebugMarkers(serverWorld);
        TestCommand.setWorld(testFunction, serverWorld);
        GameTest gameTest = new GameTest(testFunction, blockPos2, serverWorld);
        TestUtil.startTest(gameTest, TestManager.INSTANCE);
        return 1;
    }

    private static void setWorld(TestFunction testFunction, ServerWorld world) {
        Consumer<ServerWorld> consumer = TestFunctions.getWorldSetter(testFunction.getBatchId());
        if (consumer != null) {
            consumer.accept(world);
        }
    }

    private static int executeRunAll(ServerCommandSource source) {
        TestUtil.clearDebugMarkers(source.getWorld());
        TestCommand.run(source, TestFunctions.getTestFunctions());
        return 1;
    }

    private static int executeRunAll(ServerCommandSource source, String testClass) {
        Collection<TestFunction> collection = TestFunctions.getTestFunctions(testClass);
        TestUtil.clearDebugMarkers(source.getWorld());
        TestCommand.run(source, collection);
        return 1;
    }

    private static void run(ServerCommandSource source, Collection<TestFunction> testFunctions) {
        BlockPos blockPos = new BlockPos(source.getPosition());
        BlockPos blockPos2 = new BlockPos(blockPos.getX(), source.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, blockPos).getY(), blockPos.getZ() + 3);
        ServerWorld serverWorld = source.getWorld();
        TestCommand.sendMessage(source, "Running " + testFunctions.size() + " tests...");
        Collection<GameTest> collection = TestUtil.runTestFunctions(testFunctions, blockPos2, serverWorld, TestManager.INSTANCE);
        TestSet testSet = new TestSet(collection);
        testSet.addListener(new Listener(serverWorld, testSet));
    }

    private static void sendMessage(ServerCommandSource source, String message) {
        source.sendFeedback(new LiteralText(message), false);
    }

    private static int executeExport(ServerCommandSource source, String structure) {
        Path path = Paths.get(StructureTestUtil.testStructuresDirectoryName, new String[0]);
        Identifier identifier = new Identifier("minecraft", structure);
        Path path2 = source.getWorld().getStructureManager().getStructurePath(identifier, ".nbt");
        Path path3 = NbtProvider.convertNbtToSnbt(path2, structure, path);
        if (path3 == null) {
            TestCommand.sendMessage(source, "Failed to export " + path2);
            return 1;
        }
        try {
            Files.createDirectories(path3.getParent(), new FileAttribute[0]);
        }
        catch (IOException iOException) {
            TestCommand.sendMessage(source, "Could not create folder " + path3.getParent());
            iOException.printStackTrace();
            return 1;
        }
        TestCommand.sendMessage(source, "Exported to " + path3.toAbsolutePath());
        return 0;
    }

    private static int executeImport(ServerCommandSource source, String structure) {
        Path path = Paths.get(StructureTestUtil.testStructuresDirectoryName, structure + ".snbt");
        Identifier identifier = new Identifier("minecraft", structure);
        Path path2 = source.getWorld().getStructureManager().getStructurePath(identifier, ".nbt");
        try {
            BufferedReader bufferedReader = Files.newBufferedReader(path);
            String string = IOUtils.toString((Reader)bufferedReader);
            Files.createDirectories(path2.getParent(), new FileAttribute[0]);
            OutputStream outputStream = Files.newOutputStream(path2, new OpenOption[0]);
            NbtIo.writeCompressed(StringNbtReader.parse(string), outputStream);
            TestCommand.sendMessage(source, "Imported to " + path2.toAbsolutePath());
            return 0;
        }
        catch (CommandSyntaxException | IOException exception) {
            System.err.println("Failed to load structure " + structure);
            exception.printStackTrace();
            return 1;
        }
    }

    private static void sendMessage(ServerWorld world, String message, Formatting formatting) {
        world.getPlayers(serverPlayerEntity -> true).forEach(serverPlayerEntity -> serverPlayerEntity.sendMessage(new LiteralText((Object)((Object)formatting) + message)));
    }

    static class Listener
    implements TestListener {
        private final ServerWorld world;
        private final TestSet tests;

        public Listener(ServerWorld world, TestSet tests) {
            this.world = world;
            this.tests = tests;
        }

        @Override
        public void onStarted(GameTest test) {
        }

        @Override
        public void onFailed(GameTest test) {
            TestCommand.onCompletion(this.world, this.tests);
        }
    }
}

