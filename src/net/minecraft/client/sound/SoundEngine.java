/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.openal.AL
 *  org.lwjgl.openal.AL10
 *  org.lwjgl.openal.ALC
 *  org.lwjgl.openal.ALC10
 *  org.lwjgl.openal.ALC11
 *  org.lwjgl.openal.ALCCapabilities
 *  org.lwjgl.openal.ALCapabilities
 *  org.lwjgl.openal.ALUtil
 *  org.lwjgl.openal.SOFTHRTF
 *  org.lwjgl.system.MemoryStack
 *  org.slf4j.Logger
 */
package net.minecraft.client.sound;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.sound.AlUtil;
import net.minecraft.client.sound.SoundListener;
import net.minecraft.client.sound.Source;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.openal.ALUtil;
import org.lwjgl.openal.SOFTHRTF;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SoundEngine {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_34945 = 0;
    private static final int field_31897 = 30;
    private long devicePointer;
    private long contextPointer;
    private boolean disconnectExtensionPresent;
    @Nullable
    private String deviceSpecifier;
    private static final SourceSet EMPTY_SOURCE_SET = new SourceSet(){

        @Override
        @Nullable
        public Source createSource() {
            return null;
        }

        @Override
        public boolean release(Source source) {
            return false;
        }

        @Override
        public void close() {
        }

        @Override
        public int getMaxSourceCount() {
            return 0;
        }

        @Override
        public int getSourceCount() {
            return 0;
        }
    };
    private SourceSet streamingSources = EMPTY_SOURCE_SET;
    private SourceSet staticSources = EMPTY_SOURCE_SET;
    private final SoundListener listener = new SoundListener();

    public SoundEngine() {
        this.deviceSpecifier = SoundEngine.findAvailableDeviceSpecifier();
    }

    public void init(@Nullable String deviceSpecifier, boolean directionalAudio) {
        this.devicePointer = SoundEngine.openDeviceOrFallback(deviceSpecifier);
        this.disconnectExtensionPresent = ALC10.alcIsExtensionPresent((long)this.devicePointer, (CharSequence)"ALC_EXT_disconnect");
        ALCCapabilities aLCCapabilities = ALC.createCapabilities((long)this.devicePointer);
        if (AlUtil.checkAlcErrors(this.devicePointer, "Get capabilities")) {
            throw new IllegalStateException("Failed to get OpenAL capabilities");
        }
        if (!aLCCapabilities.OpenALC11) {
            throw new IllegalStateException("OpenAL 1.1 not supported");
        }
        this.setDirectionalAudio(aLCCapabilities.ALC_SOFT_HRTF && directionalAudio);
        this.contextPointer = ALC10.alcCreateContext((long)this.devicePointer, (IntBuffer)null);
        ALC10.alcMakeContextCurrent((long)this.contextPointer);
        int i = this.getMonoSourceCount();
        int j = MathHelper.clamp((int)MathHelper.sqrt(i), 2, 8);
        int k = MathHelper.clamp(i - j, 8, 255);
        this.streamingSources = new SourceSetImpl(k);
        this.staticSources = new SourceSetImpl(j);
        ALCapabilities aLCapabilities = AL.createCapabilities((ALCCapabilities)aLCCapabilities);
        AlUtil.checkErrors("Initialization");
        if (!aLCapabilities.AL_EXT_source_distance_model) {
            throw new IllegalStateException("AL_EXT_source_distance_model is not supported");
        }
        AL10.alEnable((int)512);
        if (!aLCapabilities.AL_EXT_LINEAR_DISTANCE) {
            throw new IllegalStateException("AL_EXT_LINEAR_DISTANCE is not supported");
        }
        AlUtil.checkErrors("Enable per-source distance models");
        LOGGER.info("OpenAL initialized on device {}", (Object)this.getCurrentDeviceName());
    }

    private void setDirectionalAudio(boolean enabled) {
        int i = ALC10.alcGetInteger((long)this.devicePointer, (int)6548);
        if (i > 0) {
            try (MemoryStack memoryStack = MemoryStack.stackPush();){
                IntBuffer intBuffer = memoryStack.callocInt(10).put(6546).put(enabled ? 1 : 0).put(6550).put(0).put(0).flip();
                if (!SOFTHRTF.alcResetDeviceSOFT((long)this.devicePointer, (IntBuffer)intBuffer)) {
                    LOGGER.warn("Failed to reset device: {}", (Object)ALC10.alcGetString((long)this.devicePointer, (int)ALC10.alcGetError((long)this.devicePointer)));
                }
            }
        }
    }

    private int getMonoSourceCount() {
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            int i = ALC10.alcGetInteger((long)this.devicePointer, (int)4098);
            if (AlUtil.checkAlcErrors(this.devicePointer, "Get attributes size")) {
                throw new IllegalStateException("Failed to get OpenAL attributes");
            }
            IntBuffer intBuffer = memoryStack.mallocInt(i);
            ALC10.alcGetIntegerv((long)this.devicePointer, (int)4099, (IntBuffer)intBuffer);
            if (AlUtil.checkAlcErrors(this.devicePointer, "Get attributes")) {
                throw new IllegalStateException("Failed to get OpenAL attributes");
            }
            int j = 0;
            while (j < i) {
                int k;
                if ((k = intBuffer.get(j++)) == 0) {
                    break;
                }
                int l = intBuffer.get(j++);
                if (k != 4112) continue;
                int n = l;
                return n;
            }
        }
        return 30;
    }

    @Nullable
    public static String findAvailableDeviceSpecifier() {
        if (!ALC10.alcIsExtensionPresent((long)0L, (CharSequence)"ALC_ENUMERATE_ALL_EXT")) {
            return null;
        }
        ALUtil.getStringList((long)0L, (int)4115);
        return ALC10.alcGetString((long)0L, (int)4114);
    }

    public String getCurrentDeviceName() {
        String string = ALC10.alcGetString((long)this.devicePointer, (int)4115);
        if (string == null) {
            string = ALC10.alcGetString((long)this.devicePointer, (int)4101);
        }
        if (string == null) {
            string = "Unknown";
        }
        return string;
    }

    public synchronized boolean updateDeviceSpecifier() {
        String string = SoundEngine.findAvailableDeviceSpecifier();
        if (Objects.equals(this.deviceSpecifier, string)) {
            return false;
        }
        this.deviceSpecifier = string;
        return true;
    }

    private static long openDeviceOrFallback(@Nullable String deviceSpecifier) {
        OptionalLong optionalLong = OptionalLong.empty();
        if (deviceSpecifier != null) {
            optionalLong = SoundEngine.openDevice(deviceSpecifier);
        }
        if (optionalLong.isEmpty()) {
            optionalLong = SoundEngine.openDevice(SoundEngine.findAvailableDeviceSpecifier());
        }
        if (optionalLong.isEmpty()) {
            optionalLong = SoundEngine.openDevice(null);
        }
        if (optionalLong.isEmpty()) {
            throw new IllegalStateException("Failed to open OpenAL device");
        }
        return optionalLong.getAsLong();
    }

    private static OptionalLong openDevice(@Nullable String deviceSpecifier) {
        long l = ALC10.alcOpenDevice((CharSequence)deviceSpecifier);
        if (l != 0L && !AlUtil.checkAlcErrors(l, "Open device")) {
            return OptionalLong.of(l);
        }
        return OptionalLong.empty();
    }

    public void close() {
        this.streamingSources.close();
        this.staticSources.close();
        ALC10.alcDestroyContext((long)this.contextPointer);
        if (this.devicePointer != 0L) {
            ALC10.alcCloseDevice((long)this.devicePointer);
        }
    }

    public SoundListener getListener() {
        return this.listener;
    }

    @Nullable
    public Source createSource(RunMode mode) {
        return (mode == RunMode.STREAMING ? this.staticSources : this.streamingSources).createSource();
    }

    public void release(Source source) {
        if (!this.streamingSources.release(source) && !this.staticSources.release(source)) {
            throw new IllegalStateException("Tried to release unknown channel");
        }
    }

    public String getDebugString() {
        return String.format(Locale.ROOT, "Sounds: %d/%d + %d/%d", this.streamingSources.getSourceCount(), this.streamingSources.getMaxSourceCount(), this.staticSources.getSourceCount(), this.staticSources.getMaxSourceCount());
    }

    public List<String> getSoundDevices() {
        List list = ALUtil.getStringList((long)0L, (int)4115);
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
    }

    public boolean isDeviceUnavailable() {
        return this.disconnectExtensionPresent && ALC11.alcGetInteger((long)this.devicePointer, (int)787) == 0;
    }

    @Environment(value=EnvType.CLIENT)
    static interface SourceSet {
        @Nullable
        public Source createSource();

        public boolean release(Source var1);

        public void close();

        public int getMaxSourceCount();

        public int getSourceCount();
    }

    @Environment(value=EnvType.CLIENT)
    static class SourceSetImpl
    implements SourceSet {
        private final int maxSourceCount;
        private final Set<Source> sources = Sets.newIdentityHashSet();

        public SourceSetImpl(int maxSourceCount) {
            this.maxSourceCount = maxSourceCount;
        }

        @Override
        @Nullable
        public Source createSource() {
            if (this.sources.size() >= this.maxSourceCount) {
                if (SharedConstants.isDevelopment) {
                    LOGGER.warn("Maximum sound pool size {} reached", (Object)this.maxSourceCount);
                }
                return null;
            }
            Source source = Source.create();
            if (source != null) {
                this.sources.add(source);
            }
            return source;
        }

        @Override
        public boolean release(Source source) {
            if (!this.sources.remove(source)) {
                return false;
            }
            source.close();
            return true;
        }

        @Override
        public void close() {
            this.sources.forEach(Source::close);
            this.sources.clear();
        }

        @Override
        public int getMaxSourceCount() {
            return this.maxSourceCount;
        }

        @Override
        public int getSourceCount() {
            return this.sources.size();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class RunMode
    extends Enum<RunMode> {
        public static final /* enum */ RunMode STATIC = new RunMode();
        public static final /* enum */ RunMode STREAMING = new RunMode();
        private static final /* synthetic */ RunMode[] field_18354;

        public static RunMode[] values() {
            return (RunMode[])field_18354.clone();
        }

        public static RunMode valueOf(String string) {
            return Enum.valueOf(RunMode.class, string);
        }

        private static /* synthetic */ RunMode[] method_36800() {
            return new RunMode[]{STATIC, STREAMING};
        }

        static {
            field_18354 = RunMode.method_36800();
        }
    }
}

