/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.hash.Hashing
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.pack;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class PackScreen
extends Screen {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_32395 = 200;
    private static final Text DROP_INFO = Text.translatable("pack.dropInfo").formatted(Formatting.GRAY);
    private static final Text FOLDER_INFO = Text.translatable("pack.folderInfo");
    private static final int field_32396 = 20;
    private static final Identifier UNKNOWN_PACK = new Identifier("textures/misc/unknown_pack.png");
    private final ResourcePackOrganizer organizer;
    @Nullable
    private DirectoryWatcher directoryWatcher;
    private long refreshTimeout;
    private PackListWidget availablePackList;
    private PackListWidget selectedPackList;
    private final Path file;
    private ButtonWidget doneButton;
    private final Map<String, Identifier> iconTextures = Maps.newHashMap();

    public PackScreen(ResourcePackManager resourcePackManager, Consumer<ResourcePackManager> applier, Path file, Text title) {
        super(title);
        this.organizer = new ResourcePackOrganizer(this::updatePackLists, this::getPackIconTexture, resourcePackManager, applier);
        this.file = file;
        this.directoryWatcher = DirectoryWatcher.create(file);
    }

    @Override
    public void close() {
        this.organizer.apply();
        this.closeDirectoryWatcher();
    }

    private void closeDirectoryWatcher() {
        if (this.directoryWatcher != null) {
            try {
                this.directoryWatcher.close();
                this.directoryWatcher = null;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    @Override
    protected void init() {
        this.availablePackList = new PackListWidget(this.client, this, 200, this.height, Text.translatable("pack.available.title"));
        this.availablePackList.setLeftPos(this.width / 2 - 4 - 200);
        this.addSelectableChild(this.availablePackList);
        this.selectedPackList = new PackListWidget(this.client, this, 200, this.height, Text.translatable("pack.selected.title"));
        this.selectedPackList.setLeftPos(this.width / 2 + 4);
        this.addSelectableChild(this.selectedPackList);
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("pack.openFolder"), button -> Util.getOperatingSystem().open(this.file.toUri())).dimensions(this.width / 2 - 154, this.height - 48, 150, 20).tooltip(Tooltip.of(FOLDER_INFO)).build());
        this.doneButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).dimensions(this.width / 2 + 4, this.height - 48, 150, 20).build());
        this.refresh();
    }

    @Override
    public void tick() {
        if (this.directoryWatcher != null) {
            try {
                if (this.directoryWatcher.pollForChange()) {
                    this.refreshTimeout = 20L;
                }
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to poll for directory {} changes, stopping", (Object)this.file);
                this.closeDirectoryWatcher();
            }
        }
        if (this.refreshTimeout > 0L && --this.refreshTimeout == 0L) {
            this.refresh();
        }
    }

    private void updatePackLists() {
        this.updatePackList(this.selectedPackList, this.organizer.getEnabledPacks());
        this.updatePackList(this.availablePackList, this.organizer.getDisabledPacks());
        this.doneButton.active = !this.selectedPackList.children().isEmpty();
    }

    private void updatePackList(PackListWidget widget, Stream<ResourcePackOrganizer.Pack> packs) {
        widget.children().clear();
        PackListWidget.ResourcePackEntry resourcePackEntry = (PackListWidget.ResourcePackEntry)widget.getSelectedOrNull();
        String string = resourcePackEntry == null ? "" : resourcePackEntry.getName();
        widget.setSelected(null);
        packs.forEach(pack -> {
            PackListWidget.ResourcePackEntry resourcePackEntry = new PackListWidget.ResourcePackEntry(this.client, widget, (ResourcePackOrganizer.Pack)pack);
            widget.children().add(resourcePackEntry);
            if (pack.getName().equals(string)) {
                widget.setSelected(resourcePackEntry);
            }
        });
    }

    public void switchFocusedList(PackListWidget listWidget) {
        PackListWidget packListWidget = this.selectedPackList == listWidget ? this.availablePackList : this.selectedPackList;
        this.switchFocus(GuiNavigationPath.of(packListWidget.getFirst(), new ParentElement[]{packListWidget, this}));
    }

    public void clearSelection() {
        this.selectedPackList.setSelected(null);
        this.availablePackList.setSelected(null);
    }

    private void refresh() {
        this.organizer.refresh();
        this.updatePackLists();
        this.refreshTimeout = 0L;
        this.iconTextures.clear();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(matrices);
        this.availablePackList.render(matrices, mouseX, mouseY, delta);
        this.selectedPackList.render(matrices, mouseX, mouseY, delta);
        PackScreen.drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        PackScreen.drawCenteredTextWithShadow(matrices, this.textRenderer, DROP_INFO, this.width / 2, 20, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }

    protected static void copyPacks(MinecraftClient client, List<Path> srcPaths, Path destPath) {
        MutableBoolean mutableBoolean = new MutableBoolean();
        srcPaths.forEach(src -> {
            try (Stream<Path> stream = Files.walk(src, new FileVisitOption[0]);){
                stream.forEach(toCopy -> {
                    try {
                        Util.relativeCopy(src.getParent(), destPath, toCopy);
                    }
                    catch (IOException iOException) {
                        LOGGER.warn("Failed to copy datapack file  from {} to {}", new Object[]{toCopy, destPath, iOException});
                        mutableBoolean.setTrue();
                    }
                });
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to copy datapack file from {} to {}", src, (Object)destPath);
                mutableBoolean.setTrue();
            }
        });
        if (mutableBoolean.isTrue()) {
            SystemToast.addPackCopyFailure(client, destPath.toString());
        }
    }

    @Override
    public void filesDragged(List<Path> paths) {
        String string = paths.stream().map(Path::getFileName).map(Path::toString).collect(Collectors.joining(", "));
        this.client.setScreen(new ConfirmScreen(confirmed -> {
            if (confirmed) {
                PackScreen.copyPacks(this.client, paths, this.file);
                this.refresh();
            }
            this.client.setScreen(this);
        }, Text.translatable("pack.dropConfirm"), Text.literal(string)));
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private Identifier loadPackIcon(TextureManager textureManager, ResourcePackProfile resourcePackProfile) {
        try (ResourcePack resourcePack = resourcePackProfile.createResourcePack();){
            Identifier identifier;
            block16: {
                InputSupplier<InputStream> inputSupplier = resourcePack.openRoot("pack.png");
                if (inputSupplier == null) {
                    Identifier identifier2 = UNKNOWN_PACK;
                    return identifier2;
                }
                String string = resourcePackProfile.getName();
                Identifier identifier3 = new Identifier("minecraft", "pack/" + Util.replaceInvalidChars(string, Identifier::isPathCharacterValid) + "/" + Hashing.sha1().hashUnencodedChars((CharSequence)string) + "/icon");
                InputStream inputStream = inputSupplier.get();
                try {
                    NativeImage nativeImage = NativeImage.read(inputStream);
                    textureManager.registerTexture(identifier3, new NativeImageBackedTexture(nativeImage));
                    identifier = identifier3;
                    if (inputStream == null) break block16;
                }
                catch (Throwable throwable) {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                inputStream.close();
            }
            return identifier;
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to load icon from pack {}", (Object)resourcePackProfile.getName(), (Object)exception);
            return UNKNOWN_PACK;
        }
    }

    private Identifier getPackIconTexture(ResourcePackProfile resourcePackProfile) {
        return this.iconTextures.computeIfAbsent(resourcePackProfile.getName(), profileName -> this.loadPackIcon(this.client.getTextureManager(), resourcePackProfile));
    }

    @Environment(value=EnvType.CLIENT)
    static class DirectoryWatcher
    implements AutoCloseable {
        private final WatchService watchService;
        private final Path path;

        public DirectoryWatcher(Path path) throws IOException {
            this.path = path;
            this.watchService = path.getFileSystem().newWatchService();
            try {
                this.watchDirectory(path);
                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);){
                    for (Path path2 : directoryStream) {
                        if (!Files.isDirectory(path2, LinkOption.NOFOLLOW_LINKS)) continue;
                        this.watchDirectory(path2);
                    }
                }
            }
            catch (Exception exception) {
                this.watchService.close();
                throw exception;
            }
        }

        @Nullable
        public static DirectoryWatcher create(Path path) {
            try {
                return new DirectoryWatcher(path);
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to initialize pack directory {} monitoring", (Object)path, (Object)iOException);
                return null;
            }
        }

        private void watchDirectory(Path path) throws IOException {
            path.register(this.watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        }

        public boolean pollForChange() throws IOException {
            WatchKey watchKey;
            boolean bl = false;
            while ((watchKey = this.watchService.poll()) != null) {
                List<WatchEvent<?>> list = watchKey.pollEvents();
                for (WatchEvent<?> watchEvent : list) {
                    Path path;
                    bl = true;
                    if (watchKey.watchable() != this.path || watchEvent.kind() != StandardWatchEventKinds.ENTRY_CREATE || !Files.isDirectory(path = this.path.resolve((Path)watchEvent.context()), LinkOption.NOFOLLOW_LINKS)) continue;
                    this.watchDirectory(path);
                }
                watchKey.reset();
            }
            return bl;
        }

        @Override
        public void close() throws IOException {
            this.watchService.close();
        }
    }
}

