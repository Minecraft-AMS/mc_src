/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class CloudRenderMode
extends Enum<CloudRenderMode> {
    public static final /* enum */ CloudRenderMode OFF = new CloudRenderMode("options.off");
    public static final /* enum */ CloudRenderMode FAST = new CloudRenderMode("options.clouds.fast");
    public static final /* enum */ CloudRenderMode FANCY = new CloudRenderMode("options.clouds.fancy");
    private final String translationKey;
    private static final /* synthetic */ CloudRenderMode[] field_18168;

    public static CloudRenderMode[] values() {
        return (CloudRenderMode[])field_18168.clone();
    }

    public static CloudRenderMode valueOf(String string) {
        return Enum.valueOf(CloudRenderMode.class, string);
    }

    private CloudRenderMode(String translationKey) {
        this.translationKey = translationKey;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    private static /* synthetic */ CloudRenderMode[] method_36860() {
        return new CloudRenderMode[]{OFF, FAST, FANCY};
    }

    static {
        field_18168 = CloudRenderMode.method_36860();
    }
}

