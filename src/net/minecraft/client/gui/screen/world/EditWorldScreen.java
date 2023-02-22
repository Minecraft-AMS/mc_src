/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonIOException
 *  com.google.gson.stream.JsonWriter
 *  com.mojang.datafixers.util.Function4
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DataResult$PartialResult
 *  com.mojang.serialization.JsonOps
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.FileUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.screen.world;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonWriter;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.BackupPromptScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.world.OptimizeWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.dynamic.RegistryReadingOps;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class EditWorldScreen
extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
    private static final Text ENTER_NAME_TEXT = new TranslatableText("selectWorld.enterName");
    private ButtonWidget saveButton;
    private final BooleanConsumer callback;
    private TextFieldWidget levelNameTextField;
    private final LevelStorage.Session storageSession;

    public EditWorldScreen(BooleanConsumer callback, LevelStorage.Session storageSession) {
        super(new TranslatableText("selectWorld.edit.title"));
        this.callback = callback;
        this.storageSession = storageSession;
    }

    @Override
    public void tick() {
        this.levelNameTextField.tick();
    }

    @Override
    protected void init() {
        this.client.keyboard.setRepeatEvents(true);
        ButtonWidget buttonWidget2 = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 0 + 5, 200, 20, new TranslatableText("selectWorld.edit.resetIcon"), buttonWidget -> {
            FileUtils.deleteQuietly((File)this.storageSession.getIconFile());
            buttonWidget.active = false;
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 24 + 5, 200, 20, new TranslatableText("selectWorld.edit.openFolder"), buttonWidget -> Util.getOperatingSystem().open(this.storageSession.getDirectory(WorldSavePath.ROOT).toFile())));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 48 + 5, 200, 20, new TranslatableText("selectWorld.edit.backup"), buttonWidget -> {
            boolean bl = EditWorldScreen.backupLevel(this.storageSession);
            this.callback.accept(!bl);
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 72 + 5, 200, 20, new TranslatableText("selectWorld.edit.backupFolder"), buttonWidget -> {
            LevelStorage levelStorage = this.client.getLevelStorage();
            Path path = levelStorage.getBackupsDirectory();
            try {
                Files.createDirectories(Files.exists(path, new LinkOption[0]) ? path.toRealPath(new LinkOption[0]) : path, new FileAttribute[0]);
            }
            catch (IOException iOException) {
                throw new RuntimeException(iOException);
            }
            Util.getOperatingSystem().open(path.toFile());
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 5, 200, 20, new TranslatableText("selectWorld.edit.optimize"), buttonWidget -> this.client.openScreen(new BackupPromptScreen(this, (bl, bl2) -> {
            if (bl) {
                EditWorldScreen.backupLevel(this.storageSession);
            }
            this.client.openScreen(OptimizeWorldScreen.create(this.client, this.callback, this.client.getDataFixer(), this.storageSession, bl2));
        }, new TranslatableText("optimizeWorld.confirm.title"), new TranslatableText("optimizeWorld.confirm.description"), true))));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 5, 200, 20, new TranslatableText("selectWorld.edit.export_worldgen_settings"), buttonWidget -> {
            DataResult dataResult2;
            DynamicRegistryManager.Impl impl = DynamicRegistryManager.create();
            try (MinecraftClient.IntegratedResourceManager integratedResourceManager = this.client.method_29604(impl, MinecraftClient::method_29598, (Function4<LevelStorage.Session, DynamicRegistryManager.Impl, ResourceManager, DataPackSettings, SaveProperties>)((Function4)MinecraftClient::createSaveProperties), false, this.storageSession);){
                RegistryReadingOps dynamicOps = RegistryReadingOps.of(JsonOps.INSTANCE, impl);
                DataResult dataResult = GeneratorOptions.CODEC.encodeStart(dynamicOps, (Object)integratedResourceManager.getSaveProperties().getGeneratorOptions());
                dataResult2 = dataResult.flatMap(jsonElement -> {
                    Path path = this.storageSession.getDirectory(WorldSavePath.ROOT).resolve("worldgen_settings_export.json");
                    try (JsonWriter jsonWriter = GSON.newJsonWriter((Writer)Files.newBufferedWriter(path, StandardCharsets.UTF_8, new OpenOption[0]));){
                        GSON.toJson(jsonElement, jsonWriter);
                    }
                    catch (JsonIOException | IOException exception) {
                        return DataResult.error((String)("Error writing file: " + exception.getMessage()));
                    }
                    return DataResult.success((Object)path.toString());
                });
            }
            catch (InterruptedException | ExecutionException exception) {
                dataResult2 = DataResult.error((String)"Could not parse level data!");
            }
            LiteralText text = new LiteralText((String)dataResult2.get().map(Function.identity(), DataResult.PartialResult::message));
            TranslatableText text2 = new TranslatableText(dataResult2.result().isPresent() ? "selectWorld.edit.export_worldgen_settings.success" : "selectWorld.edit.export_worldgen_settings.failure");
            dataResult2.error().ifPresent(partialResult -> LOGGER.error("Error exporting world settings: {}", partialResult));
            this.client.getToastManager().add(SystemToast.create(this.client, SystemToast.Type.WORLD_GEN_SETTINGS_TRANSFER, text2, text));
        }));
        this.saveButton = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20, new TranslatableText("selectWorld.edit.save"), buttonWidget -> this.commit()));
        this.addButton(new ButtonWidget(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20, ScreenTexts.CANCEL, buttonWidget -> this.callback.accept(false)));
        buttonWidget2.active = this.storageSession.getIconFile().isFile();
        LevelSummary levelSummary = this.storageSession.getLevelSummary();
        String string2 = levelSummary == null ? "" : levelSummary.getDisplayName();
        this.levelNameTextField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 38, 200, 20, new TranslatableText("selectWorld.enterName"));
        this.levelNameTextField.setText(string2);
        this.levelNameTextField.setChangedListener(string -> {
            this.saveButton.active = !string.trim().isEmpty();
        });
        this.children.add(this.levelNameTextField);
        this.setInitialFocus(this.levelNameTextField);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.levelNameTextField.getText();
        this.init(client, width, height);
        this.levelNameTextField.setText(string);
    }

    @Override
    public void onClose() {
        this.callback.accept(false);
    }

    @Override
    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
    }

    private void commit() {
        try {
            this.storageSession.save(this.levelNameTextField.getText().trim());
            this.callback.accept(true);
        }
        catch (IOException iOException) {
            LOGGER.error("Failed to access world '{}'", (Object)this.storageSession.getDirectoryName(), (Object)iOException);
            SystemToast.addWorldAccessFailureToast(this.client, this.storageSession.getDirectoryName());
            this.callback.accept(true);
        }
    }

    public static void onBackupConfirm(LevelStorage storage, String levelName) {
        boolean bl = false;
        try (LevelStorage.Session session = storage.createSession(levelName);){
            bl = true;
            EditWorldScreen.backupLevel(session);
        }
        catch (IOException iOException) {
            if (!bl) {
                SystemToast.addWorldAccessFailureToast(MinecraftClient.getInstance(), levelName);
            }
            LOGGER.warn("Failed to create backup of level {}", (Object)levelName, (Object)iOException);
        }
    }

    public static boolean backupLevel(LevelStorage.Session storageSession) {
        long l = 0L;
        IOException iOException = null;
        try {
            l = storageSession.createBackup();
        }
        catch (IOException iOException2) {
            iOException = iOException2;
        }
        if (iOException != null) {
            TranslatableText text = new TranslatableText("selectWorld.edit.backupFailed");
            LiteralText text2 = new LiteralText(iOException.getMessage());
            MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.WORLD_BACKUP, text, text2));
            return false;
        }
        TranslatableText text = new TranslatableText("selectWorld.edit.backupCreated", storageSession.getDirectoryName());
        TranslatableText text2 = new TranslatableText("selectWorld.edit.backupSize", MathHelper.ceil((double)l / 1048576.0));
        MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.WORLD_BACKUP, text, text2));
        return true;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        EditWorldScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
        EditWorldScreen.drawTextWithShadow(matrices, this.textRenderer, ENTER_NAME_TEXT, this.width / 2 - 100, 24, 0xA0A0A0);
        this.levelNameTextField.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }
}

