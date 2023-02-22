/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.data.dev;

import com.mojang.logging.LogUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class NbtProvider
implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final DataGenerator root;

    public NbtProvider(DataGenerator root) {
        this.root = root;
    }

    @Override
    public void run(DataCache cache) throws IOException {
        Path path2 = this.root.getOutput();
        for (Path path22 : this.root.getInputs()) {
            Files.walk(path22, new FileVisitOption[0]).filter(path -> path.toString().endsWith(".nbt")).forEach(path -> NbtProvider.convertNbtToSnbt(path, this.getLocation(path22, (Path)path), path2));
        }
    }

    @Override
    public String getName() {
        return "NBT to SNBT";
    }

    private String getLocation(Path targetPath, Path rootPath) {
        String string = targetPath.relativize(rootPath).toString().replaceAll("\\\\", "/");
        return string.substring(0, string.length() - ".nbt".length());
    }

    @Nullable
    public static Path convertNbtToSnbt(Path inputPath, String location, Path outputPath) {
        try {
            NbtProvider.writeTo(outputPath.resolve(location + ".snbt"), NbtHelper.toNbtProviderString(NbtIo.readCompressed(Files.newInputStream(inputPath, new OpenOption[0]))));
            LOGGER.info("Converted {} from NBT to SNBT", (Object)location);
            return outputPath.resolve(location + ".snbt");
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't convert {} from NBT to SNBT at {}", new Object[]{location, inputPath, iOException});
            return null;
        }
    }

    public static void writeTo(Path file, String content) throws IOException {
        Files.createDirectories(file.getParent(), new FileAttribute[0]);
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(file, new OpenOption[0]);){
            bufferedWriter.write(content);
            bufferedWriter.write(10);
        }
    }
}

