/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ClientResourcePackProfile
extends ResourcePackProfile {
    @Nullable
    private NativeImage icon;
    @Nullable
    private Identifier iconId;

    public ClientResourcePackProfile(String name, boolean notSorting, Supplier<ResourcePack> packCreator, ResourcePack pack, PackResourceMetadata metadata, ResourcePackProfile.InsertionPosition direction) {
        super(name, notSorting, packCreator, pack, metadata, direction);
        NativeImage nativeImage = null;
        try (InputStream inputStream = pack.openRoot("pack.png");){
            nativeImage = NativeImage.read(inputStream);
        }
        catch (IOException | IllegalArgumentException exception) {
            // empty catch block
        }
        this.icon = nativeImage;
    }

    public ClientResourcePackProfile(String name, boolean alwaysEnabled, Supplier<ResourcePack> packFactory, Text displayName, Text description, ResourcePackCompatibility compatibility, ResourcePackProfile.InsertionPosition insertionPosition, boolean pinned, @Nullable NativeImage icon) {
        super(name, alwaysEnabled, packFactory, displayName, description, compatibility, insertionPosition, pinned);
        this.icon = icon;
    }

    public void drawIcon(TextureManager manager) {
        if (this.iconId == null) {
            this.iconId = this.icon == null ? new Identifier("textures/misc/unknown_pack.png") : manager.registerDynamicTexture("texturepackicon", new NativeImageBackedTexture(this.icon));
        }
        manager.bindTexture(this.iconId);
    }

    @Override
    public void close() {
        super.close();
        if (this.icon != null) {
            this.icon.close();
            this.icon = null;
        }
    }
}

