/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.bridge.game.GameVersion
 *  com.mojang.bridge.game.PackType
 */
package net.minecraft.resource;

import com.mojang.bridge.game.GameVersion;
import com.mojang.bridge.game.PackType;

public final class ResourceType
extends Enum<ResourceType> {
    public static final /* enum */ ResourceType CLIENT_RESOURCES = new ResourceType("assets", PackType.RESOURCE);
    public static final /* enum */ ResourceType SERVER_DATA = new ResourceType("data", PackType.DATA);
    private final String directory;
    private final PackType packType;
    private static final /* synthetic */ ResourceType[] field_14191;

    public static ResourceType[] values() {
        return (ResourceType[])field_14191.clone();
    }

    public static ResourceType valueOf(String string) {
        return Enum.valueOf(ResourceType.class, string);
    }

    private ResourceType(String name, PackType packType) {
        this.directory = name;
        this.packType = packType;
    }

    public String getDirectory() {
        return this.directory;
    }

    public int getPackVersion(GameVersion gameVersion) {
        return gameVersion.getPackVersion(this.packType);
    }

    private static /* synthetic */ ResourceType[] method_36582() {
        return new ResourceType[]{CLIENT_RESOURCES, SERVER_DATA};
    }

    static {
        field_14191 = ResourceType.method_36582();
    }
}

