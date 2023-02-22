/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.tuple.Triple
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.MultipartUnbakedModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ModelLoader {
    public static final Identifier FIRE_0 = new Identifier("block/fire_0");
    public static final Identifier FIRE_1 = new Identifier("block/fire_1");
    public static final Identifier LAVA_FLOW = new Identifier("block/lava_flow");
    public static final Identifier WATER_FLOW = new Identifier("block/water_flow");
    public static final Identifier WATER_OVERLAY = new Identifier("block/water_overlay");
    public static final Identifier DESTROY_STAGE_0 = new Identifier("block/destroy_stage_0");
    public static final Identifier DESTROY_STAGE_1 = new Identifier("block/destroy_stage_1");
    public static final Identifier DESTROY_STAGE_2 = new Identifier("block/destroy_stage_2");
    public static final Identifier DESTROY_STAGE_3 = new Identifier("block/destroy_stage_3");
    public static final Identifier DESTROY_STAGE_4 = new Identifier("block/destroy_stage_4");
    public static final Identifier DESTROY_STAGE_5 = new Identifier("block/destroy_stage_5");
    public static final Identifier DESTROY_STAGE_6 = new Identifier("block/destroy_stage_6");
    public static final Identifier DESTROY_STAGE_7 = new Identifier("block/destroy_stage_7");
    public static final Identifier DESTROY_STAGE_8 = new Identifier("block/destroy_stage_8");
    public static final Identifier DESTROY_STAGE_9 = new Identifier("block/destroy_stage_9");
    private static final Set<Identifier> DEFAULT_TEXTURES = Sets.newHashSet((Object[])new Identifier[]{WATER_FLOW, LAVA_FLOW, WATER_OVERLAY, FIRE_0, FIRE_1, DESTROY_STAGE_0, DESTROY_STAGE_1, DESTROY_STAGE_2, DESTROY_STAGE_3, DESTROY_STAGE_4, DESTROY_STAGE_5, DESTROY_STAGE_6, DESTROY_STAGE_7, DESTROY_STAGE_8, DESTROY_STAGE_9, new Identifier("item/empty_armor_slot_helmet"), new Identifier("item/empty_armor_slot_chestplate"), new Identifier("item/empty_armor_slot_leggings"), new Identifier("item/empty_armor_slot_boots"), new Identifier("item/empty_armor_slot_shield")});
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ModelIdentifier MISSING = new ModelIdentifier("builtin/missing", "missing");
    @VisibleForTesting
    public static final String MISSING_DEFINITION = ("{    'textures': {       'particle': '" + MissingSprite.getMissingSpriteId().getPath() + "',       'missingno': '" + MissingSprite.getMissingSpriteId().getPath() + "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}").replace('\'', '\"');
    private static final Map<String, String> BUILTIN_MODEL_DEFINITIONS = Maps.newHashMap((Map)ImmutableMap.of((Object)"missing", (Object)MISSING_DEFINITION));
    private static final Splitter COMMA_SPLITTER = Splitter.on((char)',');
    private static final Splitter KEY_VALUE_SPLITTER = Splitter.on((char)'=').limit(2);
    public static final JsonUnbakedModel GENERATION_MARKER = Util.make(JsonUnbakedModel.deserialize("{}"), jsonUnbakedModel -> {
        jsonUnbakedModel.id = "generation marker";
    });
    public static final JsonUnbakedModel BLOCK_ENTITY_MARKER = Util.make(JsonUnbakedModel.deserialize("{}"), jsonUnbakedModel -> {
        jsonUnbakedModel.id = "block entity marker";
    });
    private static final StateManager<Block, BlockState> ITEM_FRAME_STATE_FACTORY = new StateManager.Builder(Blocks.AIR).add(BooleanProperty.of("map")).build(BlockState::new);
    private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
    private static final Map<Identifier, StateManager<Block, BlockState>> STATIC_DEFINITIONS = ImmutableMap.of((Object)new Identifier("item_frame"), ITEM_FRAME_STATE_FACTORY);
    private final ResourceManager resourceManager;
    private final SpriteAtlasTexture spriteAtlas;
    private final BlockColors field_20272;
    private final Set<Identifier> modelsToLoad = Sets.newHashSet();
    private final ModelVariantMap.DeserializationContext variantMapDeserializationContext = new ModelVariantMap.DeserializationContext();
    private final Map<Identifier, UnbakedModel> unbakedModels = Maps.newHashMap();
    private final Map<Triple<Identifier, ModelRotation, Boolean>, BakedModel> bakedModelCache = Maps.newHashMap();
    private final Map<Identifier, UnbakedModel> modelsToBake = Maps.newHashMap();
    private final Map<Identifier, BakedModel> bakedModels = Maps.newHashMap();
    private final SpriteAtlasTexture.Data spriteAtlasData;
    private int field_20273 = 1;
    private final Object2IntMap<BlockState> field_20274 = (Object2IntMap)Util.make(new Object2IntOpenHashMap(), object2IntOpenHashMap -> object2IntOpenHashMap.defaultReturnValue(-1));

    public ModelLoader(ResourceManager resourceManager, SpriteAtlasTexture spriteAtlas, BlockColors blockColors, Profiler profiler) {
        this.resourceManager = resourceManager;
        this.spriteAtlas = spriteAtlas;
        this.field_20272 = blockColors;
        profiler.push("missing_model");
        try {
            this.unbakedModels.put(MISSING, this.loadModelFromJson(MISSING));
            this.addModel(MISSING);
        }
        catch (IOException iOException) {
            LOGGER.error("Error loading missing model, should never happen :(", (Throwable)iOException);
            throw new RuntimeException(iOException);
        }
        profiler.swap("static_definitions");
        STATIC_DEFINITIONS.forEach((identifier, stateManager) -> stateManager.getStates().forEach(blockState -> this.addModel(BlockModels.getModelId(identifier, blockState))));
        profiler.swap("blocks");
        for (Block block : Registry.BLOCK) {
            block.getStateManager().getStates().forEach(blockState -> this.addModel(BlockModels.getModelId(blockState)));
        }
        profiler.swap("items");
        for (Identifier identifier2 : Registry.ITEM.getIds()) {
            this.addModel(new ModelIdentifier(identifier2, "inventory"));
        }
        profiler.swap("special");
        this.addModel(new ModelIdentifier("minecraft:trident_in_hand#inventory"));
        profiler.swap("textures");
        LinkedHashSet set = Sets.newLinkedHashSet();
        Set<Identifier> set2 = this.modelsToBake.values().stream().flatMap(unbakedModel -> unbakedModel.getTextureDependencies(this::getOrLoadModel, set).stream()).collect(Collectors.toSet());
        set2.addAll(DEFAULT_TEXTURES);
        set.forEach(string -> LOGGER.warn("Unable to resolve texture reference: {}", string));
        profiler.swap("stitching");
        this.spriteAtlasData = this.spriteAtlas.stitch(this.resourceManager, set2, profiler);
        profiler.pop();
    }

    public void upload(Profiler profiler) {
        profiler.push("atlas");
        this.spriteAtlas.upload(this.spriteAtlasData);
        profiler.swap("baking");
        this.modelsToBake.keySet().forEach(identifier -> {
            BakedModel bakedModel = null;
            try {
                bakedModel = this.bake((Identifier)identifier, ModelRotation.X0_Y0);
            }
            catch (Exception exception) {
                LOGGER.warn("Unable to bake model: '{}': {}", identifier, (Object)exception);
            }
            if (bakedModel != null) {
                this.bakedModels.put((Identifier)identifier, bakedModel);
            }
        });
        profiler.pop();
    }

    private static Predicate<BlockState> stateKeyToPredicate(StateManager<Block, BlockState> stateFactory, String key) {
        HashMap map = Maps.newHashMap();
        for (String string : COMMA_SPLITTER.split((CharSequence)key)) {
            Iterator iterator = KEY_VALUE_SPLITTER.split((CharSequence)string).iterator();
            if (!iterator.hasNext()) continue;
            String string2 = (String)iterator.next();
            Property<?> property = stateFactory.getProperty(string2);
            if (property != null && iterator.hasNext()) {
                String string3 = (String)iterator.next();
                Object comparable = ModelLoader.getPropertyValue(property, string3);
                if (comparable != null) {
                    map.put(property, comparable);
                    continue;
                }
                throw new RuntimeException("Unknown value: '" + string3 + "' for blockstate property: '" + string2 + "' " + property.getValues());
            }
            if (string2.isEmpty()) continue;
            throw new RuntimeException("Unknown blockstate property: '" + string2 + "'");
        }
        Block block = stateFactory.getOwner();
        return blockState -> {
            if (blockState == null || block != blockState.getBlock()) {
                return false;
            }
            for (Map.Entry entry : map.entrySet()) {
                if (Objects.equals(blockState.get((Property)entry.getKey()), entry.getValue())) continue;
                return false;
            }
            return true;
        };
    }

    @Nullable
    static <T extends Comparable<T>> T getPropertyValue(Property<T> property, String string) {
        return (T)((Comparable)property.parse(string).orElse(null));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public UnbakedModel getOrLoadModel(Identifier id) {
        if (this.unbakedModels.containsKey(id)) {
            return this.unbakedModels.get(id);
        }
        if (this.modelsToLoad.contains(id)) {
            throw new IllegalStateException("Circular reference while loading " + id);
        }
        this.modelsToLoad.add(id);
        UnbakedModel unbakedModel = this.unbakedModels.get(MISSING);
        while (!this.modelsToLoad.isEmpty()) {
            Identifier identifier = this.modelsToLoad.iterator().next();
            try {
                if (this.unbakedModels.containsKey(identifier)) continue;
                this.loadModel(identifier);
            }
            catch (ModelLoaderException modelLoaderException) {
                LOGGER.warn(modelLoaderException.getMessage());
                this.unbakedModels.put(identifier, unbakedModel);
            }
            catch (Exception exception) {
                LOGGER.warn("Unable to load model: '{}' referenced from: {}: {}", (Object)identifier, (Object)id, (Object)exception);
                this.unbakedModels.put(identifier, unbakedModel);
            }
            finally {
                this.modelsToLoad.remove(identifier);
            }
        }
        return this.unbakedModels.getOrDefault(id, unbakedModel);
    }

    private void loadModel(Identifier id) throws Exception {
        if (!(id instanceof ModelIdentifier)) {
            this.putModel(id, this.loadModelFromJson(id));
            return;
        }
        ModelIdentifier modelIdentifier2 = (ModelIdentifier)id;
        if (Objects.equals(modelIdentifier2.getVariant(), "inventory")) {
            Identifier identifier = new Identifier(id.getNamespace(), "item/" + id.getPath());
            JsonUnbakedModel jsonUnbakedModel = this.loadModelFromJson(identifier);
            this.putModel(modelIdentifier2, jsonUnbakedModel);
            this.unbakedModels.put(identifier, jsonUnbakedModel);
        } else {
            Identifier identifier = new Identifier(id.getNamespace(), id.getPath());
            StateManager stateManager = Optional.ofNullable(STATIC_DEFINITIONS.get(identifier)).orElseGet(() -> Registry.BLOCK.get(identifier).getStateManager());
            this.variantMapDeserializationContext.setStateFactory(stateManager);
            ImmutableList list = ImmutableList.copyOf(this.field_20272.method_21592((Block)stateManager.getOwner()));
            ImmutableList immutableList = stateManager.getStates();
            HashMap map = Maps.newHashMap();
            immutableList.forEach(blockState -> map.put(BlockModels.getModelId(identifier, blockState), blockState));
            HashMap map2 = Maps.newHashMap();
            Identifier identifier2 = new Identifier(id.getNamespace(), "blockstates/" + id.getPath() + ".json");
            UnbakedModel unbakedModel = this.unbakedModels.get(MISSING);
            class_4455 lv = new class_4455((List<UnbakedModel>)ImmutableList.of((Object)unbakedModel), (List<Object>)ImmutableList.of());
            Pair pair = Pair.of((Object)unbakedModel, () -> lv);
            try {
                List list2;
                try {
                    list2 = this.resourceManager.getAllResources(identifier2).stream().map(resource -> {
                        try (InputStream inputStream = resource.getInputStream();){
                            Pair pair = Pair.of((Object)resource.getResourcePackName(), (Object)ModelVariantMap.deserialize(this.variantMapDeserializationContext, new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
                            return pair;
                        }
                        catch (Exception exception) {
                            throw new ModelLoaderException(String.format("Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", resource.getId(), resource.getResourcePackName(), exception.getMessage()));
                        }
                    }).collect(Collectors.toList());
                }
                catch (IOException iOException) {
                    LOGGER.warn("Exception loading blockstate definition: {}: {}", (Object)identifier2, (Object)iOException);
                    HashMap map3 = Maps.newHashMap();
                    map.forEach((modelIdentifier, blockState) -> {
                        Pair pair2 = (Pair)map2.get(blockState);
                        if (pair2 == null) {
                            LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", (Object)identifier2, modelIdentifier);
                            pair2 = pair;
                        }
                        this.putModel((Identifier)modelIdentifier, (UnbakedModel)pair2.getFirst());
                        try {
                            class_4455 lv = (class_4455)((Supplier)pair2.getSecond()).get();
                            map3.computeIfAbsent(lv, arg -> Sets.newIdentityHashSet()).add(blockState);
                        }
                        catch (Exception exception) {
                            LOGGER.warn("Exception evaluating model definition: '{}'", modelIdentifier, (Object)exception);
                        }
                    });
                    map3.forEach((arg, set) -> {
                        Iterator iterator = set.iterator();
                        while (iterator.hasNext()) {
                            BlockState blockState = (BlockState)iterator.next();
                            if (blockState.getRenderType() == BlockRenderType.MODEL) continue;
                            iterator.remove();
                            this.field_20274.put((Object)blockState, 0);
                        }
                        if (set.size() > 1) {
                            this.method_21603((Iterable<BlockState>)set);
                        }
                    });
                    return;
                }
                for (Pair pair2 : list2) {
                    MultipartUnbakedModel multipartUnbakedModel;
                    ModelVariantMap modelVariantMap = (ModelVariantMap)pair2.getSecond();
                    IdentityHashMap map4 = Maps.newIdentityHashMap();
                    if (modelVariantMap.hasMultipartModel()) {
                        multipartUnbakedModel = modelVariantMap.getMultipartModel();
                        immutableList.forEach(arg_0 -> ModelLoader.method_4738(map4, multipartUnbakedModel, (List)list, arg_0));
                    } else {
                        multipartUnbakedModel = null;
                    }
                    modelVariantMap.getVariantMap().forEach((arg_0, arg_1) -> ModelLoader.method_4731(immutableList, stateManager, map4, (List)list, multipartUnbakedModel, pair, modelVariantMap, identifier2, pair2, arg_0, arg_1));
                    map2.putAll(map4);
                }
            }
            catch (ModelLoaderException modelLoaderException) {
                throw modelLoaderException;
            }
            catch (Exception exception) {
                throw new ModelLoaderException(String.format("Exception loading blockstate definition: '%s': %s", identifier2, exception));
            }
            finally {
                HashMap map6 = Maps.newHashMap();
                map.forEach((modelIdentifier, blockState) -> {
                    Pair pair2 = (Pair)map2.get(blockState);
                    if (pair2 == null) {
                        LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", (Object)identifier2, modelIdentifier);
                        pair2 = pair;
                    }
                    this.putModel((Identifier)modelIdentifier, (UnbakedModel)pair2.getFirst());
                    try {
                        class_4455 lv = (class_4455)((Supplier)pair2.getSecond()).get();
                        map3.computeIfAbsent(lv, arg -> Sets.newIdentityHashSet()).add(blockState);
                    }
                    catch (Exception exception) {
                        LOGGER.warn("Exception evaluating model definition: '{}'", modelIdentifier, (Object)exception);
                    }
                });
                map6.forEach((arg, set) -> {
                    Iterator iterator = set.iterator();
                    while (iterator.hasNext()) {
                        BlockState blockState = (BlockState)iterator.next();
                        if (blockState.getRenderType() == BlockRenderType.MODEL) continue;
                        iterator.remove();
                        this.field_20274.put((Object)blockState, 0);
                    }
                    if (set.size() > 1) {
                        this.method_21603((Iterable<BlockState>)set);
                    }
                });
            }
        }
    }

    private void putModel(Identifier id, UnbakedModel unbakedModel) {
        this.unbakedModels.put(id, unbakedModel);
        this.modelsToLoad.addAll(unbakedModel.getModelDependencies());
    }

    private void addModel(ModelIdentifier modelId) {
        UnbakedModel unbakedModel = this.getOrLoadModel(modelId);
        this.unbakedModels.put(modelId, unbakedModel);
        this.modelsToBake.put(modelId, unbakedModel);
    }

    private void method_21603(Iterable<BlockState> iterable) {
        int i = this.field_20273++;
        iterable.forEach(blockState -> this.field_20274.put(blockState, i));
    }

    @Nullable
    public BakedModel bake(Identifier identifier, ModelBakeSettings settings) {
        JsonUnbakedModel jsonUnbakedModel;
        Triple triple = Triple.of((Object)identifier, (Object)settings.getRotation(), (Object)settings.isShaded());
        if (this.bakedModelCache.containsKey(triple)) {
            return this.bakedModelCache.get(triple);
        }
        UnbakedModel unbakedModel = this.getOrLoadModel(identifier);
        if (unbakedModel instanceof JsonUnbakedModel && (jsonUnbakedModel = (JsonUnbakedModel)unbakedModel).getRootModel() == GENERATION_MARKER) {
            return ITEM_MODEL_GENERATOR.create(this.spriteAtlas::getSprite, jsonUnbakedModel).bake(this, jsonUnbakedModel, this.spriteAtlas::getSprite, settings);
        }
        BakedModel bakedModel = unbakedModel.bake(this, this.spriteAtlas::getSprite, settings);
        this.bakedModelCache.put((Triple<Identifier, ModelRotation, Boolean>)triple, bakedModel);
        return bakedModel;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private JsonUnbakedModel loadModelFromJson(Identifier id) throws IOException {
        String string;
        Resource resource;
        Reader reader;
        block8: {
            block7: {
                JsonUnbakedModel jsonUnbakedModel;
                reader = null;
                resource = null;
                try {
                    string = id.getPath();
                    if (!"builtin/generated".equals(string)) break block7;
                    jsonUnbakedModel = GENERATION_MARKER;
                }
                catch (Throwable throwable) {
                    IOUtils.closeQuietly(reader);
                    IOUtils.closeQuietly(resource);
                    throw throwable;
                }
                IOUtils.closeQuietly(reader);
                IOUtils.closeQuietly(resource);
                return jsonUnbakedModel;
            }
            if (!"builtin/entity".equals(string)) break block8;
            JsonUnbakedModel jsonUnbakedModel = BLOCK_ENTITY_MARKER;
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(resource);
            return jsonUnbakedModel;
        }
        if (string.startsWith("builtin/")) {
            String string2 = string.substring("builtin/".length());
            String string3 = BUILTIN_MODEL_DEFINITIONS.get(string2);
            if (string3 == null) {
                throw new FileNotFoundException(id.toString());
            }
            reader = new StringReader(string3);
        } else {
            resource = this.resourceManager.getResource(new Identifier(id.getNamespace(), "models/" + id.getPath() + ".json"));
            reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
        }
        JsonUnbakedModel jsonUnbakedModel = JsonUnbakedModel.deserialize(reader);
        jsonUnbakedModel.id = id.toString();
        JsonUnbakedModel jsonUnbakedModel2 = jsonUnbakedModel;
        IOUtils.closeQuietly((Reader)reader);
        IOUtils.closeQuietly((Closeable)resource);
        return jsonUnbakedModel2;
    }

    public Map<Identifier, BakedModel> getBakedModelMap() {
        return this.bakedModels;
    }

    public Object2IntMap<BlockState> method_21605() {
        return this.field_20274;
    }

    private static /* synthetic */ void method_4731(ImmutableList immutableList, StateManager stateManager, Map map, List list, MultipartUnbakedModel multipartUnbakedModel, Pair pair, ModelVariantMap modelVariantMap, Identifier identifier, Pair pair2, String string, WeightedUnbakedModel weightedUnbakedModel) {
        try {
            immutableList.stream().filter(ModelLoader.stateKeyToPredicate(stateManager, string)).forEach(blockState -> {
                Pair pair2 = map.put(blockState, Pair.of((Object)weightedUnbakedModel, () -> class_4455.method_21608(blockState, weightedUnbakedModel, list)));
                if (pair2 != null && pair2.getFirst() != multipartUnbakedModel) {
                    map.put(blockState, pair);
                    throw new RuntimeException("Overlapping definition with: " + (String)modelVariantMap.getVariantMap().entrySet().stream().filter(entry -> entry.getValue() == pair2.getFirst()).findFirst().get().getKey());
                }
            });
        }
        catch (Exception exception) {
            LOGGER.warn("Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}", (Object)identifier, pair2.getFirst(), (Object)string, (Object)exception.getMessage());
        }
    }

    private static /* synthetic */ void method_4738(Map map, MultipartUnbakedModel multipartUnbakedModel, List list, BlockState blockState) {
        map.put(blockState, Pair.of((Object)multipartUnbakedModel, () -> class_4455.method_21607(blockState, multipartUnbakedModel, list)));
    }

    @Environment(value=EnvType.CLIENT)
    static class class_4455 {
        private final List<UnbakedModel> field_20275;
        private final List<Object> field_20276;

        public class_4455(List<UnbakedModel> list, List<Object> list2) {
            this.field_20275 = list;
            this.field_20276 = list2;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object instanceof class_4455) {
                class_4455 lv = (class_4455)object;
                return Objects.equals(this.field_20275, lv.field_20275) && Objects.equals(this.field_20276, lv.field_20276);
            }
            return false;
        }

        public int hashCode() {
            return 31 * this.field_20275.hashCode() + this.field_20276.hashCode();
        }

        public static class_4455 method_21607(BlockState blockState, MultipartUnbakedModel multipartUnbakedModel, Collection<Property<?>> collection) {
            StateManager<Block, BlockState> stateManager = blockState.getBlock().getStateManager();
            List list = (List)multipartUnbakedModel.getComponents().stream().filter(multipartModelComponent -> multipartModelComponent.getPredicate(stateManager).test(blockState)).map(MultipartModelComponent::getModel).collect(ImmutableList.toImmutableList());
            List<Object> list2 = class_4455.method_21609(blockState, collection);
            return new class_4455(list, list2);
        }

        public static class_4455 method_21608(BlockState blockState, UnbakedModel unbakedModel, Collection<Property<?>> collection) {
            List<Object> list = class_4455.method_21609(blockState, collection);
            return new class_4455((List<UnbakedModel>)ImmutableList.of((Object)unbakedModel), list);
        }

        private static List<Object> method_21609(BlockState blockState, Collection<Property<?>> collection) {
            return (List)collection.stream().map(blockState::get).collect(ImmutableList.toImmutableList());
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ModelLoaderException
    extends RuntimeException {
        public ModelLoaderException(String string) {
            super(string);
        }
    }
}

