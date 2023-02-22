/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.resource;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.DefaultResourcePackBuilder;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.VanillaResourcePackProvider;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataMap;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DefaultClientResourcePackProvider
extends VanillaResourcePackProvider {
    private static final PackResourceMetadata METADATA = new PackResourceMetadata(Text.translatable("resourcePack.vanilla.description"), ResourceType.CLIENT_RESOURCES.getPackVersion(SharedConstants.getGameVersion()));
    private static final ResourceMetadataMap METADATA_MAP = ResourceMetadataMap.of(PackResourceMetadata.SERIALIZER, METADATA);
    private static final Text VANILLA_NAME_TEXT = Text.translatable("resourcePack.vanilla.name");
    private static final Map<String, Text> PROFILE_NAME_TEXTS = Map.of("programmer_art", Text.translatable("resourcePack.programmer_art.name"));
    private static final Identifier ID = new Identifier("minecraft", "resourcepacks");
    @Nullable
    private final Path resourcePacksPath;

    public DefaultClientResourcePackProvider(Path assetsPath) {
        super(ResourceType.CLIENT_RESOURCES, DefaultClientResourcePackProvider.createDefaultPack(assetsPath), ID);
        this.resourcePacksPath = this.getResourcePacksPath(assetsPath);
    }

    @Nullable
    private Path getResourcePacksPath(Path path) {
        Path path2;
        if (SharedConstants.isDevelopment && path.getFileSystem() == FileSystems.getDefault() && Files.isDirectory(path2 = path.getParent().resolve("resourcepacks"), new LinkOption[0])) {
            return path2;
        }
        return null;
    }

    private static DefaultResourcePack createDefaultPack(Path assetsPath) {
        return new DefaultResourcePackBuilder().withMetadataMap(METADATA_MAP).withNamespaces("minecraft", "realms").runCallback().withDefaultPaths().withPath(ResourceType.CLIENT_RESOURCES, assetsPath).build();
    }

    @Override
    protected Text getProfileName(String id) {
        Text text = PROFILE_NAME_TEXTS.get(id);
        return text != null ? text : Text.literal(id);
    }

    @Override
    @Nullable
    protected ResourcePackProfile createDefault(ResourcePack pack) {
        return ResourcePackProfile.create("vanilla", VANILLA_NAME_TEXT, true, name -> pack, ResourceType.CLIENT_RESOURCES, ResourcePackProfile.InsertionPosition.BOTTOM, ResourcePackSource.BUILTIN);
    }

    @Override
    @Nullable
    protected ResourcePackProfile create(String name, ResourcePackProfile.PackFactory packFactory, Text displayName) {
        return ResourcePackProfile.create(name, displayName, false, packFactory, ResourceType.CLIENT_RESOURCES, ResourcePackProfile.InsertionPosition.TOP, ResourcePackSource.BUILTIN);
    }

    @Override
    protected void forEachProfile(BiConsumer<String, Function<String, ResourcePackProfile>> consumer) {
        super.forEachProfile(consumer);
        if (this.resourcePacksPath != null) {
            this.forEachProfile(this.resourcePacksPath, consumer);
        }
    }
}

