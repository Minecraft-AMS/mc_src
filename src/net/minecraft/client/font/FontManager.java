/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.font;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.io.BufferedReader;
import java.io.Reader;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.BlankFont;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.FontType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.DependencyTracker;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class FontManager
implements ResourceReloader,
AutoCloseable {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final String FONTS_JSON = "fonts.json";
    public static final Identifier MISSING_STORAGE_ID = new Identifier("minecraft", "missing");
    private static final ResourceFinder FINDER = ResourceFinder.json("font");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final FontStorage missingStorage;
    private final List<Font> fonts = new ArrayList<Font>();
    private final Map<Identifier, FontStorage> fontStorages = new HashMap<Identifier, FontStorage>();
    private final TextureManager textureManager;
    private Map<Identifier, Identifier> idOverrides = ImmutableMap.of();

    public FontManager(TextureManager manager) {
        this.textureManager = manager;
        this.missingStorage = Util.make(new FontStorage(manager, MISSING_STORAGE_ID), fontStorage -> fontStorage.setFonts(Lists.newArrayList((Object[])new Font[]{new BlankFont()})));
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        prepareProfiler.startTick();
        prepareProfiler.endTick();
        return ((CompletableFuture)this.loadIndex(manager, prepareExecutor).thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(index -> this.reload((ProviderIndex)index, applyProfiler), applyExecutor);
    }

    private CompletableFuture<ProviderIndex> loadIndex(ResourceManager resourceManager, Executor executor) {
        ArrayList<CompletableFuture<FontEntry>> list = new ArrayList<CompletableFuture<FontEntry>>();
        for (Map.Entry<Identifier, List<Resource>> entry : FINDER.findAllResources(resourceManager).entrySet()) {
            Identifier identifier = FINDER.toResourceId(entry.getKey());
            list.add(CompletableFuture.supplyAsync(() -> {
                List<Pair<FontKey, FontLoader>> list = FontManager.loadFontProviders((List)entry.getValue(), identifier);
                FontEntry fontEntry = new FontEntry(identifier);
                for (Pair<FontKey, FontLoader> pair : list) {
                    FontKey fontKey = (FontKey)pair.getFirst();
                    ((FontLoader)pair.getSecond()).build().ifLeft(loadable -> {
                        CompletableFuture<Optional<Font>> completableFuture = this.load(fontKey, (FontLoader.Loadable)loadable, resourceManager, executor);
                        fontEntry.addBuilder(fontKey, completableFuture);
                    }).ifRight(reference -> fontEntry.addReferenceBuilder(fontKey, (FontLoader.Reference)reference));
                }
                return fontEntry;
            }, executor));
        }
        return Util.combineSafe(list).thenCompose(entries -> {
            List list = entries.stream().flatMap(FontEntry::getImmediateProviders).collect(Collectors.toCollection(ArrayList::new));
            BlankFont font = new BlankFont();
            list.add(CompletableFuture.completedFuture(Optional.of(font)));
            return Util.combineSafe(list).thenCompose(providers -> {
                Map<Identifier, List<Font>> map = this.getRequiredFontProviders((List<FontEntry>)entries);
                CompletableFuture[] completableFutures = (CompletableFuture[])map.values().stream().map(dest -> CompletableFuture.runAsync(() -> this.insertFont((List<Font>)dest, font), executor)).toArray(CompletableFuture[]::new);
                return CompletableFuture.allOf(completableFutures).thenApply(void_ -> {
                    List<Font> list2 = providers.stream().flatMap(Optional::stream).toList();
                    return new ProviderIndex(map, list2);
                });
            });
        });
    }

    private CompletableFuture<Optional<Font>> load(FontKey key, FontLoader.Loadable loadable, ResourceManager resourceManager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return Optional.of(loadable.load(resourceManager));
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to load builder {}, rejecting", (Object)key, (Object)exception);
                return Optional.empty();
            }
        }, executor);
    }

    private Map<Identifier, List<Font>> getRequiredFontProviders(List<FontEntry> entries) {
        HashMap<Identifier, List<Font>> map = new HashMap<Identifier, List<Font>>();
        DependencyTracker<Identifier, FontEntry> dependencyTracker = new DependencyTracker<Identifier, FontEntry>();
        entries.forEach(entry -> dependencyTracker.add(entry.fontId, (FontEntry)entry));
        dependencyTracker.traverse((dependent, fontEntry) -> fontEntry.getRequiredFontProviders(map::get).ifPresent(fonts -> map.put((Identifier)dependent, (List<Font>)fonts)));
        return map;
    }

    private void insertFont(List<Font> fonts, Font font) {
        fonts.add(0, font);
        IntOpenHashSet intSet = new IntOpenHashSet();
        for (Font font2 : fonts) {
            intSet.addAll((IntCollection)font2.getProvidedGlyphs());
        }
        intSet.forEach(codePoint -> {
            Font font;
            if (codePoint == 32) {
                return;
            }
            Iterator iterator = Lists.reverse((List)fonts).iterator();
            while (iterator.hasNext() && (font = (Font)iterator.next()).getGlyph(codePoint) == null) {
            }
        });
    }

    private void reload(ProviderIndex index, Profiler profiler) {
        profiler.startTick();
        profiler.push("closing");
        this.fontStorages.values().forEach(FontStorage::close);
        this.fontStorages.clear();
        this.fonts.forEach(Font::close);
        this.fonts.clear();
        profiler.swap("reloading");
        index.providers().forEach((fontId, providers) -> {
            FontStorage fontStorage = new FontStorage(this.textureManager, (Identifier)fontId);
            fontStorage.setFonts(Lists.reverse((List)providers));
            this.fontStorages.put((Identifier)fontId, fontStorage);
        });
        this.fonts.addAll(index.allProviders);
        profiler.pop();
        profiler.endTick();
        if (!this.fontStorages.containsKey(this.getEffectiveId(MinecraftClient.DEFAULT_FONT_ID))) {
            throw new IllegalStateException("Default font failed to load");
        }
    }

    private static List<Pair<FontKey, FontLoader>> loadFontProviders(List<Resource> fontResources, Identifier id) {
        ArrayList<Pair<FontKey, FontLoader>> list = new ArrayList<Pair<FontKey, FontLoader>>();
        for (Resource resource : fontResources) {
            try {
                BufferedReader reader = resource.getReader();
                try {
                    JsonArray jsonArray = JsonHelper.getArray(JsonHelper.deserialize(GSON, (Reader)reader, JsonObject.class), "providers");
                    for (int i = jsonArray.size() - 1; i >= 0; --i) {
                        JsonObject jsonObject = JsonHelper.asObject(jsonArray.get(i), "providers[" + i + "]");
                        String string = JsonHelper.getString(jsonObject, "type");
                        FontType fontType = FontType.byId(string);
                        FontKey fontKey = new FontKey(id, resource.getResourcePackName(), i);
                        list.add((Pair<FontKey, FontLoader>)Pair.of((Object)fontKey, (Object)fontType.createLoader(jsonObject)));
                    }
                }
                finally {
                    if (reader == null) continue;
                    ((Reader)reader).close();
                }
            }
            catch (Exception exception) {
                LOGGER.warn("Unable to load font '{}' in {} in resourcepack: '{}'", new Object[]{id, FONTS_JSON, resource.getResourcePackName(), exception});
            }
        }
        return list;
    }

    public void setIdOverrides(Map<Identifier, Identifier> idOverrides) {
        this.idOverrides = idOverrides;
    }

    private Identifier getEffectiveId(Identifier id) {
        return this.idOverrides.getOrDefault(id, id);
    }

    public TextRenderer createTextRenderer() {
        return new TextRenderer(id -> this.fontStorages.getOrDefault(this.getEffectiveId((Identifier)id), this.missingStorage), false);
    }

    public TextRenderer createAdvanceValidatingTextRenderer() {
        return new TextRenderer(id -> this.fontStorages.getOrDefault(this.getEffectiveId((Identifier)id), this.missingStorage), true);
    }

    @Override
    public void close() {
        this.fontStorages.values().forEach(FontStorage::close);
        this.fonts.forEach(Font::close);
        this.missingStorage.close();
    }

    @Environment(value=EnvType.CLIENT)
    record FontKey(Identifier fontId, String pack, int index) {
        @Override
        public String toString() {
            return "(" + this.fontId + ": builder #" + this.index + " from pack " + this.pack + ")";
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class ProviderIndex
    extends Record {
        private final Map<Identifier, List<Font>> providers;
        final List<Font> allProviders;

        ProviderIndex(Map<Identifier, List<Font>> map, List<Font> list) {
            this.providers = map;
            this.allProviders = list;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ProviderIndex.class, "providers;allProviders", "providers", "allProviders"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ProviderIndex.class, "providers;allProviders", "providers", "allProviders"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ProviderIndex.class, "providers;allProviders", "providers", "allProviders"}, this, object);
        }

        public Map<Identifier, List<Font>> providers() {
            return this.providers;
        }

        public List<Font> allProviders() {
            return this.allProviders;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class FontEntry
    extends Record
    implements DependencyTracker.Dependencies<Identifier> {
        final Identifier fontId;
        private final List<Builder> builders;
        private final Set<Identifier> dependencies;

        public FontEntry(Identifier fontId) {
            this(fontId, new ArrayList<Builder>(), new HashSet<Identifier>());
        }

        private FontEntry(Identifier identifier, List<Builder> list, Set<Identifier> set) {
            this.fontId = identifier;
            this.builders = list;
            this.dependencies = set;
        }

        public void addReferenceBuilder(FontKey key, FontLoader.Reference reference) {
            this.builders.add(new Builder(key, (Either<CompletableFuture<Optional<Font>>, Identifier>)Either.right((Object)reference.id())));
            this.dependencies.add(reference.id());
        }

        public void addBuilder(FontKey key, CompletableFuture<Optional<Font>> provider) {
            this.builders.add(new Builder(key, (Either<CompletableFuture<Optional<Font>>, Identifier>)Either.left(provider)));
        }

        private Stream<CompletableFuture<Optional<Font>>> getImmediateProviders() {
            return this.builders.stream().flatMap(builder -> builder.result.left().stream());
        }

        public Optional<List<Font>> getRequiredFontProviders(Function<Identifier, List<Font>> fontRetriever) {
            ArrayList list = new ArrayList();
            for (Builder builder : this.builders) {
                Optional<List<Font>> optional = builder.build(fontRetriever);
                if (optional.isPresent()) {
                    list.addAll(optional.get());
                    continue;
                }
                return Optional.empty();
            }
            return Optional.of(list);
        }

        @Override
        public void forDependencies(Consumer<Identifier> callback) {
            this.dependencies.forEach(callback);
        }

        @Override
        public void forOptionalDependencies(Consumer<Identifier> callback) {
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{FontEntry.class, "fontId;builders;dependencies", "fontId", "builders", "dependencies"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{FontEntry.class, "fontId;builders;dependencies", "fontId", "builders", "dependencies"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{FontEntry.class, "fontId;builders;dependencies", "fontId", "builders", "dependencies"}, this, object);
        }

        public Identifier fontId() {
            return this.fontId;
        }

        public List<Builder> builders() {
            return this.builders;
        }

        public Set<Identifier> dependencies() {
            return this.dependencies;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class Builder
    extends Record {
        private final FontKey id;
        final Either<CompletableFuture<Optional<Font>>, Identifier> result;

        Builder(FontKey fontKey, Either<CompletableFuture<Optional<Font>>, Identifier> either) {
            this.id = fontKey;
            this.result = either;
        }

        public Optional<List<Font>> build(Function<Identifier, List<Font>> fontRetriever) {
            return (Optional)this.result.map(completableFuture -> ((Optional)completableFuture.join()).map(List::of), referee -> {
                List list = (List)fontRetriever.apply((Identifier)referee);
                if (list == null) {
                    LOGGER.warn("Can't find font {} referenced by builder {}, either because it's missing, failed to load or is part of loading cycle", referee, (Object)this.id);
                    return Optional.empty();
                }
                return Optional.of(list);
            });
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Builder.class, "id;result", "id", "result"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Builder.class, "id;result", "id", "result"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Builder.class, "id;result", "id", "result"}, this, object);
        }

        public FontKey id() {
            return this.id;
        }

        public Either<CompletableFuture<Optional<Font>>, Identifier> result() {
            return this.result;
        }
    }
}

