/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.sound;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.SoundEngine;
import net.minecraft.client.sound.Source;

@Environment(value=EnvType.CLIENT)
public class Channel {
    private final Set<SourceManager> sources = Sets.newIdentityHashSet();
    private final SoundEngine soundEngine;
    private final Executor executor;

    public Channel(SoundEngine soundEngine, Executor executor) {
        this.soundEngine = soundEngine;
        this.executor = executor;
    }

    public SourceManager createSource(SoundEngine.RunMode mode) {
        SourceManager sourceManager = new SourceManager();
        this.executor.execute(() -> {
            Source source = this.soundEngine.createSource(mode);
            if (source != null) {
                sourceManager.source = source;
                this.sources.add(sourceManager);
            }
        });
        return sourceManager;
    }

    public void execute(Consumer<Stream<Source>> consumer) {
        this.executor.execute(() -> consumer.accept(this.sources.stream().map(sourceManager -> ((SourceManager)sourceManager).source).filter(Objects::nonNull)));
    }

    public void tick() {
        this.executor.execute(() -> {
            Iterator<SourceManager> iterator = this.sources.iterator();
            while (iterator.hasNext()) {
                SourceManager sourceManager = iterator.next();
                sourceManager.source.tick();
                if (!sourceManager.source.isStopped()) continue;
                sourceManager.close();
                iterator.remove();
            }
        });
    }

    public void close() {
        this.sources.forEach(SourceManager::close);
        this.sources.clear();
    }

    @Environment(value=EnvType.CLIENT)
    public class SourceManager {
        private Source source;
        private boolean stopped;

        public boolean isStopped() {
            return this.stopped;
        }

        public void run(Consumer<Source> action) {
            Channel.this.executor.execute(() -> {
                if (this.source != null) {
                    action.accept(this.source);
                }
            });
        }

        public void close() {
            this.stopped = true;
            Channel.this.soundEngine.release(this.source);
            this.source = null;
        }
    }
}

