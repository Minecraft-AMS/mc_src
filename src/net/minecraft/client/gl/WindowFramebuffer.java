/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;

@Environment(value=EnvType.CLIENT)
public class WindowFramebuffer
extends Framebuffer {
    public static final int DEFAULT_WIDTH = 854;
    public static final int DEFAULT_HEIGHT = 480;
    static final Size DEFAULT = new Size(854, 480);

    public WindowFramebuffer(int width, int height) {
        super(true);
        RenderSystem.assertOnRenderThreadOrInit();
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.initSize(width, height));
        } else {
            this.initSize(width, height);
        }
    }

    private void initSize(int width, int height) {
        RenderSystem.assertOnRenderThreadOrInit();
        Size size = this.findSuitableSize(width, height);
        this.fbo = GlStateManager.glGenFramebuffers();
        GlStateManager._glBindFramebuffer(36160, this.fbo);
        GlStateManager._bindTexture(this.colorAttachment);
        GlStateManager._texParameter(3553, 10241, 9728);
        GlStateManager._texParameter(3553, 10240, 9728);
        GlStateManager._texParameter(3553, 10242, 33071);
        GlStateManager._texParameter(3553, 10243, 33071);
        GlStateManager._glFramebufferTexture2D(36160, 36064, 3553, this.colorAttachment, 0);
        GlStateManager._bindTexture(this.depthAttachment);
        GlStateManager._texParameter(3553, 34892, 0);
        GlStateManager._texParameter(3553, 10241, 9728);
        GlStateManager._texParameter(3553, 10240, 9728);
        GlStateManager._texParameter(3553, 10242, 33071);
        GlStateManager._texParameter(3553, 10243, 33071);
        GlStateManager._glFramebufferTexture2D(36160, 36096, 3553, this.depthAttachment, 0);
        GlStateManager._bindTexture(0);
        this.viewportWidth = size.width;
        this.viewportHeight = size.height;
        this.textureWidth = size.width;
        this.textureHeight = size.height;
        this.checkFramebufferStatus();
        GlStateManager._glBindFramebuffer(36160, 0);
    }

    private Size findSuitableSize(int width, int height) {
        RenderSystem.assertOnRenderThreadOrInit();
        this.colorAttachment = TextureUtil.generateTextureId();
        this.depthAttachment = TextureUtil.generateTextureId();
        Attachment attachment = Attachment.NONE;
        for (Size size : Size.findCompatible(width, height)) {
            attachment = Attachment.NONE;
            if (this.supportColor(size)) {
                attachment = attachment.with(Attachment.COLOR);
            }
            if (this.supportsDepth(size)) {
                attachment = attachment.with(Attachment.DEPTH);
            }
            if (attachment != Attachment.COLOR_DEPTH) continue;
            return size;
        }
        throw new RuntimeException("Unrecoverable GL_OUT_OF_MEMORY (allocated attachments = " + attachment.name() + ")");
    }

    private boolean supportColor(Size size) {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._getError();
        GlStateManager._bindTexture(this.colorAttachment);
        GlStateManager._texImage2D(3553, 0, 32856, size.width, size.height, 0, 6408, 5121, null);
        return GlStateManager._getError() != 1285;
    }

    private boolean supportsDepth(Size size) {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._getError();
        GlStateManager._bindTexture(this.depthAttachment);
        GlStateManager._texImage2D(3553, 0, 6402, size.width, size.height, 0, 6402, 5126, null);
        return GlStateManager._getError() != 1285;
    }

    @Environment(value=EnvType.CLIENT)
    static class Size {
        public final int width;
        public final int height;

        Size(int width, int height) {
            this.width = width;
            this.height = height;
        }

        static List<Size> findCompatible(int width, int height) {
            RenderSystem.assertOnRenderThreadOrInit();
            int i = RenderSystem.maxSupportedTextureSize();
            if (width <= 0 || width > i || height <= 0 || height > i) {
                return ImmutableList.of((Object)DEFAULT);
            }
            return ImmutableList.of((Object)new Size(width, height), (Object)DEFAULT);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Size size = (Size)o;
            return this.width == size.width && this.height == size.height;
        }

        public int hashCode() {
            return Objects.hash(this.width, this.height);
        }

        public String toString() {
            return this.width + "x" + this.height;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class Attachment
    extends Enum<Attachment> {
        public static final /* enum */ Attachment NONE = new Attachment();
        public static final /* enum */ Attachment COLOR = new Attachment();
        public static final /* enum */ Attachment DEPTH = new Attachment();
        public static final /* enum */ Attachment COLOR_DEPTH = new Attachment();
        private static final Attachment[] VALUES;
        private static final /* synthetic */ Attachment[] field_33732;

        public static Attachment[] values() {
            return (Attachment[])field_33732.clone();
        }

        public static Attachment valueOf(String string) {
            return Enum.valueOf(Attachment.class, string);
        }

        Attachment with(Attachment other) {
            return VALUES[this.ordinal() | other.ordinal()];
        }

        private static /* synthetic */ Attachment[] method_36806() {
            return new Attachment[]{NONE, COLOR, DEPTH, COLOR_DEPTH};
        }

        static {
            field_33732 = Attachment.method_36806();
            VALUES = Attachment.values();
        }
    }
}

