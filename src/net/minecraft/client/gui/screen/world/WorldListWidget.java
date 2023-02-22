/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.Hashing
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.world;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.BackupPromptScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.FatalErrorScreen;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WorldListWidget
extends AlwaysSelectedEntryListWidget<Entry> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
    private static final Identifier UNKNOWN_SERVER_LOCATION = new Identifier("textures/misc/unknown_server.png");
    private static final Identifier WORLD_SELECTION_LOCATION = new Identifier("textures/gui/world_selection.png");
    private final SelectWorldScreen parent;
    @Nullable
    private List<LevelSummary> levels;

    public WorldListWidget(SelectWorldScreen parent, MinecraftClient client, int width, int height, int top, int bottom, int itemHeight, Supplier<String> searchFilter, @Nullable WorldListWidget list) {
        super(client, width, height, top, bottom, itemHeight);
        this.parent = parent;
        if (list != null) {
            this.levels = list.levels;
        }
        this.filter(searchFilter, false);
    }

    public void filter(Supplier<String> filter, boolean load) {
        this.clearEntries();
        LevelStorage levelStorage = this.minecraft.getLevelStorage();
        if (this.levels == null || load) {
            try {
                this.levels = levelStorage.getLevelList();
            }
            catch (LevelStorageException levelStorageException) {
                LOGGER.error("Couldn't load level list", (Throwable)levelStorageException);
                this.minecraft.openScreen(new FatalErrorScreen(new TranslatableText("selectWorld.unable_to_load", new Object[0]), levelStorageException.getMessage()));
                return;
            }
            Collections.sort(this.levels);
        }
        String string = filter.get().toLowerCase(Locale.ROOT);
        for (LevelSummary levelSummary : this.levels) {
            if (!levelSummary.getDisplayName().toLowerCase(Locale.ROOT).contains(string) && !levelSummary.getName().toLowerCase(Locale.ROOT).contains(string)) continue;
            this.addEntry(new Entry(this, levelSummary, this.minecraft.getLevelStorage()));
        }
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 20;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 50;
    }

    @Override
    protected boolean isFocused() {
        return this.parent.getFocused() == this;
    }

    @Override
    public void setSelected(@Nullable Entry entry) {
        super.setSelected(entry);
        if (entry != null) {
            LevelSummary levelSummary = entry.level;
            NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.select", new TranslatableText("narrator.select.world", levelSummary.getDisplayName(), new Date(levelSummary.getLastPlayed()), levelSummary.isHardcore() ? I18n.translate("gameMode.hardcore", new Object[0]) : I18n.translate("gameMode." + levelSummary.getGameMode().getName(), new Object[0]), levelSummary.hasCheats() ? I18n.translate("selectWorld.cheats", new Object[0]) : "", levelSummary.getVersion())).getString());
        }
    }

    @Override
    protected void moveSelection(int i) {
        super.moveSelection(i);
        this.parent.worldSelected(true);
    }

    public Optional<Entry> method_20159() {
        return Optional.ofNullable(this.getSelected());
    }

    public SelectWorldScreen getParent() {
        return this.parent;
    }

    @Override
    public /* synthetic */ void setSelected(@Nullable EntryListWidget.Entry entry) {
        this.setSelected((Entry)entry);
    }

    @Environment(value=EnvType.CLIENT)
    public final class Entry
    extends AlwaysSelectedEntryListWidget.Entry<Entry>
    implements AutoCloseable {
        private final MinecraftClient client;
        private final SelectWorldScreen screen;
        private final LevelSummary level;
        private final Identifier iconLocation;
        private File iconFile;
        @Nullable
        private final NativeImageBackedTexture icon;
        private long time;

        public Entry(WorldListWidget levelList, LevelSummary level, LevelStorage levelStorage) {
            this.screen = levelList.getParent();
            this.level = level;
            this.client = MinecraftClient.getInstance();
            this.iconLocation = new Identifier("worlds/" + Hashing.sha1().hashUnencodedChars((CharSequence)level.getName()) + "/icon");
            this.iconFile = levelStorage.resolveFile(level.getName(), "icon.png");
            if (!this.iconFile.isFile()) {
                this.iconFile = null;
            }
            this.icon = this.getIconTexture();
        }

        @Override
        public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            String string = this.level.getDisplayName();
            String string2 = this.level.getName() + " (" + DATE_FORMAT.format(new Date(this.level.getLastPlayed())) + ")";
            if (StringUtils.isEmpty((CharSequence)string)) {
                string = I18n.translate("selectWorld.world", new Object[0]) + " " + (i + 1);
            }
            String string3 = "";
            if (this.level.requiresConversion()) {
                string3 = I18n.translate("selectWorld.conversion", new Object[0]) + " " + string3;
            } else {
                string3 = I18n.translate("gameMode." + this.level.getGameMode().getName(), new Object[0]);
                if (this.level.isHardcore()) {
                    string3 = (Object)((Object)Formatting.DARK_RED) + I18n.translate("gameMode.hardcore", new Object[0]) + (Object)((Object)Formatting.RESET);
                }
                if (this.level.hasCheats()) {
                    string3 = string3 + ", " + I18n.translate("selectWorld.cheats", new Object[0]);
                }
                String string4 = this.level.getVersion().asFormattedString();
                string3 = this.level.isDifferentVersion() ? (this.level.isFutureLevel() ? string3 + ", " + I18n.translate("selectWorld.version", new Object[0]) + " " + (Object)((Object)Formatting.RED) + string4 + (Object)((Object)Formatting.RESET) : string3 + ", " + I18n.translate("selectWorld.version", new Object[0]) + " " + (Object)((Object)Formatting.ITALIC) + string4 + (Object)((Object)Formatting.RESET)) : string3 + ", " + I18n.translate("selectWorld.version", new Object[0]) + " " + string4;
            }
            this.client.textRenderer.draw(string, k + 32 + 3, j + 1, 0xFFFFFF);
            this.client.textRenderer.draw(string2, k + 32 + 3, j + this.client.textRenderer.fontHeight + 3, 0x808080);
            this.client.textRenderer.draw(string3, k + 32 + 3, j + this.client.textRenderer.fontHeight + this.client.textRenderer.fontHeight + 3, 0x808080);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.client.getTextureManager().bindTexture(this.icon != null ? this.iconLocation : UNKNOWN_SERVER_LOCATION);
            GlStateManager.enableBlend();
            DrawableHelper.blit(k, j, 0.0f, 0.0f, 32, 32, 32, 32);
            GlStateManager.disableBlend();
            if (this.client.options.touchscreen || bl) {
                int q;
                this.client.getTextureManager().bindTexture(WORLD_SELECTION_LOCATION);
                DrawableHelper.fill(k, j, k + 32, j + 32, -1601138544);
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                int p = n - k;
                int n2 = q = p < 32 ? 32 : 0;
                if (this.level.isDifferentVersion()) {
                    DrawableHelper.blit(k, j, 32.0f, q, 32, 32, 256, 256);
                    if (this.level.isLegacyCustomizedWorld()) {
                        DrawableHelper.blit(k, j, 96.0f, q, 32, 32, 256, 256);
                        if (p < 32) {
                            Text text = new TranslatableText("selectWorld.tooltip.unsupported", this.level.getVersion()).formatted(Formatting.RED);
                            this.screen.setTooltip(this.client.textRenderer.wrapStringToWidth(text.asFormattedString(), 175));
                        }
                    } else if (this.level.isFutureLevel()) {
                        DrawableHelper.blit(k, j, 96.0f, q, 32, 32, 256, 256);
                        if (p < 32) {
                            this.screen.setTooltip((Object)((Object)Formatting.RED) + I18n.translate("selectWorld.tooltip.fromNewerVersion1", new Object[0]) + "\n" + (Object)((Object)Formatting.RED) + I18n.translate("selectWorld.tooltip.fromNewerVersion2", new Object[0]));
                        }
                    } else if (!SharedConstants.getGameVersion().isStable()) {
                        DrawableHelper.blit(k, j, 64.0f, q, 32, 32, 256, 256);
                        if (p < 32) {
                            this.screen.setTooltip((Object)((Object)Formatting.GOLD) + I18n.translate("selectWorld.tooltip.snapshot1", new Object[0]) + "\n" + (Object)((Object)Formatting.GOLD) + I18n.translate("selectWorld.tooltip.snapshot2", new Object[0]));
                        }
                    }
                } else {
                    DrawableHelper.blit(k, j, 0.0f, q, 32, 32, 256, 256);
                }
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            WorldListWidget.this.setSelected(this);
            this.screen.worldSelected(WorldListWidget.this.method_20159().isPresent());
            if (mouseX - (double)WorldListWidget.this.getRowLeft() <= 32.0) {
                this.play();
                return true;
            }
            if (Util.getMeasuringTimeMs() - this.time < 250L) {
                this.play();
                return true;
            }
            this.time = Util.getMeasuringTimeMs();
            return false;
        }

        public void play() {
            if (this.level.isOutdatedLevel() || this.level.isLegacyCustomizedWorld()) {
                TranslatableText text = new TranslatableText("selectWorld.backupQuestion", new Object[0]);
                TranslatableText text2 = new TranslatableText("selectWorld.backupWarning", this.level.getVersion().asFormattedString(), SharedConstants.getGameVersion().getName());
                if (this.level.isLegacyCustomizedWorld()) {
                    text = new TranslatableText("selectWorld.backupQuestion.customized", new Object[0]);
                    text2 = new TranslatableText("selectWorld.backupWarning.customized", new Object[0]);
                }
                this.client.openScreen(new BackupPromptScreen(this.screen, (bl, bl2) -> {
                    if (bl) {
                        String string = this.level.getName();
                        EditWorldScreen.backupLevel(this.client.getLevelStorage(), string);
                    }
                    this.start();
                }, text, text2, false));
            } else if (this.level.isFutureLevel()) {
                this.client.openScreen(new ConfirmScreen(bl -> {
                    if (bl) {
                        try {
                            this.start();
                        }
                        catch (Exception exception) {
                            LOGGER.error("Failure to open 'future world'", (Throwable)exception);
                            this.client.openScreen(new NoticeScreen(() -> this.client.openScreen(this.screen), new TranslatableText("selectWorld.futureworld.error.title", new Object[0]), new TranslatableText("selectWorld.futureworld.error.text", new Object[0])));
                        }
                    } else {
                        this.client.openScreen(this.screen);
                    }
                }, new TranslatableText("selectWorld.versionQuestion", new Object[0]), new TranslatableText("selectWorld.versionWarning", this.level.getVersion().asFormattedString()), I18n.translate("selectWorld.versionJoinButton", new Object[0]), I18n.translate("gui.cancel", new Object[0])));
            } else {
                this.start();
            }
        }

        public void delete() {
            this.client.openScreen(new ConfirmScreen(bl -> {
                if (bl) {
                    this.client.openScreen(new ProgressScreen());
                    LevelStorage levelStorage = this.client.getLevelStorage();
                    levelStorage.deleteLevel(this.level.getName());
                    WorldListWidget.this.filter(() -> this.screen.searchBox.getText(), true);
                }
                this.client.openScreen(this.screen);
            }, new TranslatableText("selectWorld.deleteQuestion", new Object[0]), new TranslatableText("selectWorld.deleteWarning", this.level.getDisplayName()), I18n.translate("selectWorld.deleteButton", new Object[0]), I18n.translate("gui.cancel", new Object[0])));
        }

        public void edit() {
            this.client.openScreen(new EditWorldScreen(bl -> {
                if (bl) {
                    WorldListWidget.this.filter(() -> this.screen.searchBox.getText(), true);
                }
                this.client.openScreen(this.screen);
            }, this.level.getName()));
        }

        public void recreate() {
            try {
                this.client.openScreen(new ProgressScreen());
                CreateWorldScreen createWorldScreen = new CreateWorldScreen(this.screen);
                WorldSaveHandler worldSaveHandler = this.client.getLevelStorage().createSaveHandler(this.level.getName(), null);
                LevelProperties levelProperties = worldSaveHandler.readProperties();
                if (levelProperties != null) {
                    createWorldScreen.recreateLevel(levelProperties);
                    if (this.level.isLegacyCustomizedWorld()) {
                        this.client.openScreen(new ConfirmScreen(bl -> this.client.openScreen(bl ? createWorldScreen : this.screen), new TranslatableText("selectWorld.recreate.customized.title", new Object[0]), new TranslatableText("selectWorld.recreate.customized.text", new Object[0]), I18n.translate("gui.proceed", new Object[0]), I18n.translate("gui.cancel", new Object[0])));
                    } else {
                        this.client.openScreen(createWorldScreen);
                    }
                }
            }
            catch (Exception exception) {
                LOGGER.error("Unable to recreate world", (Throwable)exception);
                this.client.openScreen(new NoticeScreen(() -> this.client.openScreen(this.screen), new TranslatableText("selectWorld.recreate.error.title", new Object[0]), new TranslatableText("selectWorld.recreate.error.text", new Object[0])));
            }
        }

        private void start() {
            this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            if (this.client.getLevelStorage().levelExists(this.level.getName())) {
                this.client.startIntegratedServer(this.level.getName(), this.level.getDisplayName(), null);
            }
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Nullable
        private NativeImageBackedTexture getIconTexture() {
            boolean bl;
            boolean bl2 = bl = this.iconFile != null && this.iconFile.isFile();
            if (!bl) {
                this.client.getTextureManager().destroyTexture(this.iconLocation);
                return null;
            }
            try (FileInputStream inputStream = new FileInputStream(this.iconFile);){
                NativeImage nativeImage = NativeImage.read(inputStream);
                Validate.validState((nativeImage.getWidth() == 64 ? 1 : 0) != 0, (String)"Must be 64 pixels wide", (Object[])new Object[0]);
                Validate.validState((nativeImage.getHeight() == 64 ? 1 : 0) != 0, (String)"Must be 64 pixels high", (Object[])new Object[0]);
                NativeImageBackedTexture nativeImageBackedTexture2 = new NativeImageBackedTexture(nativeImage);
                this.client.getTextureManager().registerTexture(this.iconLocation, nativeImageBackedTexture2);
                NativeImageBackedTexture nativeImageBackedTexture = nativeImageBackedTexture2;
                return nativeImageBackedTexture;
            }
            catch (Throwable throwable6) {
                LOGGER.error("Invalid icon for world {}", (Object)this.level.getName(), (Object)throwable6);
                this.iconFile = null;
                return null;
            }
        }

        @Override
        public void close() {
            if (this.icon != null) {
                this.icon.close();
            }
        }
    }
}
