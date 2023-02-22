/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import net.minecraft.SharedConstants;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class ResourcePackCompatibility
extends Enum<ResourcePackCompatibility> {
    public static final /* enum */ ResourcePackCompatibility TOO_OLD = new ResourcePackCompatibility("old");
    public static final /* enum */ ResourcePackCompatibility TOO_NEW = new ResourcePackCompatibility("new");
    public static final /* enum */ ResourcePackCompatibility COMPATIBLE = new ResourcePackCompatibility("compatible");
    private final Text notification;
    private final Text confirmMessage;
    private static final /* synthetic */ ResourcePackCompatibility[] field_14221;

    public static ResourcePackCompatibility[] values() {
        return (ResourcePackCompatibility[])field_14221.clone();
    }

    public static ResourcePackCompatibility valueOf(String string) {
        return Enum.valueOf(ResourcePackCompatibility.class, string);
    }

    private ResourcePackCompatibility(String translationSuffix) {
        this.notification = Text.translatable("pack.incompatible." + translationSuffix).formatted(Formatting.GRAY);
        this.confirmMessage = Text.translatable("pack.incompatible.confirm." + translationSuffix);
    }

    public boolean isCompatible() {
        return this == COMPATIBLE;
    }

    public static ResourcePackCompatibility from(int packVersion, ResourceType type) {
        int i = type.getPackVersion(SharedConstants.getGameVersion());
        if (packVersion < i) {
            return TOO_OLD;
        }
        if (packVersion > i) {
            return TOO_NEW;
        }
        return COMPATIBLE;
    }

    public static ResourcePackCompatibility from(PackResourceMetadata metadata, ResourceType type) {
        return ResourcePackCompatibility.from(metadata.getPackFormat(), type);
    }

    public Text getNotification() {
        return this.notification;
    }

    public Text getConfirmMessage() {
        return this.confirmMessage;
    }

    private static /* synthetic */ ResourcePackCompatibility[] method_36584() {
        return new ResourcePackCompatibility[]{TOO_OLD, TOO_NEW, COMPATIBLE};
    }

    static {
        field_14221 = ResourcePackCompatibility.method_36584();
    }
}

