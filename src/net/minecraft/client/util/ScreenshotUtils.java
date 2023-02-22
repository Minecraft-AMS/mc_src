/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.util;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ScreenshotUtils {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    public static void method_1659(File file, int i, int j, Framebuffer framebuffer, Consumer<Text> consumer) {
        ScreenshotUtils.method_1662(file, null, i, j, framebuffer, consumer);
    }

    public static void method_1662(File file, @Nullable String string, int i, int j, Framebuffer framebuffer, Consumer<Text> consumer) {
        NativeImage nativeImage = ScreenshotUtils.method_1663(i, j, framebuffer);
        File file2 = new File(file, "screenshots");
        file2.mkdir();
        File file3 = string == null ? ScreenshotUtils.getScreenshotFilename(file2) : new File(file2, string);
        ResourceImpl.RESOURCE_IO_EXECUTOR.execute(() -> {
            try {
                nativeImage.writeFile(file3);
                Text text = new LiteralText(file3.getName()).formatted(Formatting.UNDERLINE).styled(style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file3.getAbsolutePath())));
                consumer.accept(new TranslatableText("screenshot.success", text));
            }
            catch (Exception exception) {
                LOGGER.warn("Couldn't save screenshot", (Throwable)exception);
                consumer.accept(new TranslatableText("screenshot.failure", exception.getMessage()));
            }
            finally {
                nativeImage.close();
            }
        });
    }

    public static NativeImage method_1663(int i, int j, Framebuffer framebuffer) {
        if (GLX.isUsingFBOs()) {
            i = framebuffer.textureWidth;
            j = framebuffer.textureHeight;
        }
        NativeImage nativeImage = new NativeImage(i, j, false);
        if (GLX.isUsingFBOs()) {
            GlStateManager.bindTexture(framebuffer.colorAttachment);
            nativeImage.loadFromTextureImage(0, true);
        } else {
            nativeImage.loadFromMemory(true);
        }
        nativeImage.method_4319();
        return nativeImage;
    }

    private static File getScreenshotFilename(File directory) {
        String string = DATE_FORMAT.format(new Date());
        int i = 1;
        File file;
        while ((file = new File(directory, string + (i == 1 ? "" : "_" + i) + ".png")).exists()) {
            ++i;
        }
        return file;
    }
}

