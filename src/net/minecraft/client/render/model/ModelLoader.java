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
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.entity.BellBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ConduitBlockEntityRenderer;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.MultipartUnbakedModel;
import net.minecraft.client.render.model.SpriteAtlasManager;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelVariantMap;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.client.render.model.json.WeightedUnbakedModel;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ModelLoader {
    public static final SpriteIdentifier FIRE_0 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("block/fire_0"));
    public static final SpriteIdentifier FIRE_1 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("block/fire_1"));
    public static final SpriteIdentifier LAVA_FLOW = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("block/lava_flow"));
    public static final SpriteIdentifier WATER_FLOW = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("block/water_flow"));
    public static final SpriteIdentifier WATER_OVERLAY = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("block/water_overlay"));
    public static final SpriteIdentifier BANNER_BASE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("entity/banner_base"));
    public static final SpriteIdentifier SHIELD_BASE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("entity/shield_base"));
    public static final SpriteIdentifier SHIELD_BASE_NO_PATTERN = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("entity/shield_base_nopattern"));
    public static final int field_32983 = 10;
    public static final List<Identifier> BLOCK_DESTRUCTION_STAGES = IntStream.range(0, 10).mapToObj(i -> new Identifier("block/destroy_stage_" + i)).collect(Collectors.toList());
    public static final List<Identifier> BLOCK_DESTRUCTION_STAGE_TEXTURES = BLOCK_DESTRUCTION_STAGES.stream().map(identifier -> new Identifier("textures/" + identifier.getPath() + ".png")).collect(Collectors.toList());
    public static final List<RenderLayer> BLOCK_DESTRUCTION_RENDER_LAYERS = BLOCK_DESTRUCTION_STAGE_TEXTURES.stream().map(RenderLayer::getBlockBreaking).collect(Collectors.toList());
    private static final Set<SpriteIdentifier> DEFAULT_TEXTURES = Util.make(Sets.newHashSet(), hashSet -> {
        hashSet.add(WATER_FLOW);
        hashSet.add(LAVA_FLOW);
        hashSet.add(WATER_OVERLAY);
        hashSet.add(FIRE_0);
        hashSet.add(FIRE_1);
        hashSet.add(BellBlockEntityRenderer.BELL_BODY_TEXTURE);
        hashSet.add(ConduitBlockEntityRenderer.BASE_TEXTURE);
        hashSet.add(ConduitBlockEntityRenderer.CAGE_TEXTURE);
        hashSet.add(ConduitBlockEntityRenderer.WIND_TEXTURE);
        hashSet.add(ConduitBlockEntityRenderer.WIND_VERTICAL_TEXTURE);
        hashSet.add(ConduitBlockEntityRenderer.OPEN_EYE_TEXTURE);
        hashSet.add(ConduitBlockEntityRenderer.CLOSED_EYE_TEXTURE);
        hashSet.add(EnchantingTableBlockEntityRenderer.BOOK_TEXTURE);
        hashSet.add(BANNER_BASE);
        hashSet.add(SHIELD_BASE);
        hashSet.add(SHIELD_BASE_NO_PATTERN);
        for (Identifier identifier : BLOCK_DESTRUCTION_STAGES) {
            hashSet.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, identifier));
        }
        hashSet.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE));
        hashSet.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE));
        hashSet.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE));
        hashSet.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE));
        hashSet.add(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT));
        TexturedRenderLayers.addDefaultTextures(hashSet::add);
    });
    static final int field_32984 = -1;
    private static final int field_32985 = 0;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String BUILTIN = "builtin/";
    private static final String BUILTIN_GENERATED = "builtin/generated";
    private static final String BUILTIN_ENTITY = "builtin/entity";
    private static final String MISSING = "missing";
    public static final ModelIdentifier MISSING_ID = new ModelIdentifier("builtin/missing", "missing");
    private static final String field_21773 = MISSING_ID.toString();
    @VisibleForTesting
    public static final String MISSING_DEFINITION = ("{    'textures': {       'particle': '" + MissingSprite.getMissingSpriteId().getPath() + "',       'missingno': '" + MissingSprite.getMissingSpriteId().getPath() + "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}").replace('\'', '\"');
    private static final Map<String, String> BUILTIN_MODEL_DEFINITIONS = Maps.newHashMap((Map)ImmutableMap.of((Object)"missing", (Object)MISSING_DEFINITION));
    private static final Splitter COMMA_SPLITTER = Splitter.on((char)',');
    private static final Splitter KEY_VALUE_SPLITTER = Splitter.on((char)'=').limit(2);
    public static final JsonUnbakedModel GENERATION_MARKER = Util.make(JsonUnbakedModel.deserialize("{\"gui_light\": \"front\"}"), jsonUnbakedModel -> {
        jsonUnbakedModel.id = "generation marker";
    });
    public static final JsonUnbakedModel BLOCK_ENTITY_MARKER = Util.make(JsonUnbakedModel.deserialize("{\"gui_light\": \"side\"}"), jsonUnbakedModel -> {
        jsonUnbakedModel.id = "block entity marker";
    });
    private static final StateManager<Block, BlockState> ITEM_FRAME_STATE_FACTORY = new StateManager.Builder(Blocks.AIR).add(BooleanProperty.of("map")).build(Block::getDefaultState, BlockState::new);
    private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
    private static final Map<Identifier, StateManager<Block, BlockState>> STATIC_DEFINITIONS = ImmutableMap.of((Object)new Identifier("item_frame"), ITEM_FRAME_STATE_FACTORY, (Object)new Identifier("glow_item_frame"), ITEM_FRAME_STATE_FACTORY);
    private final ResourceManager resourceManager;
    @Nullable
    private SpriteAtlasManager spriteAtlasManager;
    private final BlockColors blockColors;
    private final Set<Identifier> modelsToLoad = Sets.newHashSet();
    private final ModelVariantMap.DeserializationContext variantMapDeserializationContext = new ModelVariantMap.DeserializationContext();
    private final Map<Identifier, UnbakedModel> unbakedModels = Maps.newHashMap();
    private final Map<Triple<Identifier, AffineTransformation, Boolean>, BakedModel> bakedModelCache = Maps.newHashMap();
    private final Map<Identifier, UnbakedModel> modelsToBake = Maps.newHashMap();
    private final Map<Identifier, BakedModel> bakedModels = Maps.newHashMap();
    private final Map<Identifier, Pair<SpriteAtlasTexture, SpriteAtlasTexture.Data>> spriteAtlasData;
    private int nextStateId = 1;
    private final Object2IntMap<BlockState> stateLookup = (Object2IntMap)Util.make(new Object2IntOpenHashMap(), object2IntOpenHashMap -> object2IntOpenHashMap.defaultReturnValue(-1));

    public ModelLoader(ResourceManager resourceManager, BlockColors blockColors, Profiler profiler, int i) {
        this.resourceManager = resourceManager;
        this.blockColors = blockColors;
        profiler.push("missing_model");
        try {
            this.unbakedModels.put(MISSING_ID, this.loadModelFromJson(MISSING_ID));
            this.addModel(MISSING_ID);
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
        this.addModel(new ModelIdentifier("minecraft:spyglass_in_hand#inventory"));
        profiler.swap("textures");
        LinkedHashSet set = Sets.newLinkedHashSet();
        Set set2 = this.modelsToBake.values().stream().flatMap(unbakedModel -> unbakedModel.getTextureDependencies(this::getOrLoadModel, set).stream()).collect(Collectors.toSet());
        set2.addAll(DEFAULT_TEXTURES);
        set.stream().filter(pair -> !((String)pair.getSecond()).equals(field_21773)).forEach(pair -> LOGGER.warn("Unable to resolve texture reference: {} in {}", pair.getFirst(), pair.getSecond()));
        Map<Identifier, List<SpriteIdentifier>> map = set2.stream().collect(Collectors.groupingBy(SpriteIdentifier::getAtlasId));
        profiler.swap("stitching");
        this.spriteAtlasData = Maps.newHashMap();
        for (Map.Entry<Identifier, List<SpriteIdentifier>> entry : map.entrySet()) {
            SpriteAtlasTexture spriteAtlasTexture = new SpriteAtlasTexture(entry.getKey());
            SpriteAtlasTexture.Data data = spriteAtlasTexture.stitch(this.resourceManager, entry.getValue().stream().map(SpriteIdentifier::getTextureId), profiler, i);
            this.spriteAtlasData.put(entry.getKey(), (Pair<SpriteAtlasTexture, SpriteAtlasTexture.Data>)Pair.of((Object)spriteAtlasTexture, (Object)data));
        }
        profiler.pop();
    }

    public SpriteAtlasManager upload(TextureManager textureManager, Profiler profiler) {
        profiler.push("atlas");
        for (Pair<SpriteAtlasTexture, SpriteAtlasTexture.Data> pair : this.spriteAtlasData.values()) {
            SpriteAtlasTexture spriteAtlasTexture = (SpriteAtlasTexture)pair.getFirst();
            SpriteAtlasTexture.Data data = (SpriteAtlasTexture.Data)pair.getSecond();
            spriteAtlasTexture.upload(data);
            textureManager.registerTexture(spriteAtlasTexture.getId(), spriteAtlasTexture);
            textureManager.bindTexture(spriteAtlasTexture.getId());
            spriteAtlasTexture.applyTextureFilter(data);
        }
        this.spriteAtlasManager = new SpriteAtlasManager(this.spriteAtlasData.values().stream().map(Pair::getFirst).collect(Collectors.toList()));
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
        return this.spriteAtlasManager;
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
            if (blockState == null || !blockState.isOf(block)) {
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
        UnbakedModel unbakedModel = this.unbakedModels.get(MISSING_ID);
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
            ImmutableList list = ImmutableList.copyOf(this.blockColors.getProperties((Block)stateManager.getOwner()));
            ImmutableList immutableList = stateManager.getStates();
            HashMap map = Maps.newHashMap();
            immutableList.forEach(blockState -> map.put(BlockModels.getModelId(identifier, blockState), blockState));
            HashMap map2 = Maps.newHashMap();
            Identifier identifier2 = new Identifier(id.getNamespace(), "blockstates/" + id.getPath() + ".json");
            UnbakedModel unbakedModel = this.unbakedModels.get(MISSING_ID);
            ModelDefinition modelDefinition2 = new ModelDefinition((List<UnbakedModel>)ImmutableList.of((Object)unbakedModel), (List<Object>)ImmutableList.of());
            Pair pair = Pair.of((Object)unbakedModel, () -> modelDefinition2);
            try {
                List list2;
                try {
                    list2 = this.resourceManager.getAllResources(identifier2).stream().map(resource -> {
                        Pair pair;
                        block8: {
                            InputStream inputStream = resource.getInputStream();
                            try {
                                pair = Pair.of((Object)resource.getResourcePackName(), (Object)ModelVariantMap.fromJson(this.variantMapDeserializationContext, new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
                                if (inputStream == null) break block8;
                            }
                            catch (Throwable throwable) {
                                try {
                                    if (inputStream != null) {
                                        try {
                                            inputStream.close();
                                        }
                                        catch (Throwable throwable2) {
                                            throwable.addSuppressed(throwable2);
                                        }
                                    }
                                    throw throwable;
                                }
                                catch (Exception exception) {
                                    throw new ModelLoaderException(String.format("Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", resource.getId(), resource.getResourcePackName(), exception.getMessage()));
                                }
                            }
                            inputStream.close();
                        }
                        return pair;
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
                            ModelDefinition modelDefinition2 = (ModelDefinition)((Supplier)pair2.getSecond()).get();
                            map3.computeIfAbsent(modelDefinition2, modelDefinition -> Sets.newIdentityHashSet()).add(blockState);
                        }
                        catch (Exception exception) {
                            LOGGER.warn("Exception evaluating model definition: '{}'", modelIdentifier, (Object)exception);
                        }
                    });
                    map3.forEach((modelDefinition, set) -> {
                        Iterator iterator = set.iterator();
                        while (iterator.hasNext()) {
                            BlockState blockState = (BlockState)iterator.next();
                            if (blockState.getRenderType() == BlockRenderType.MODEL) continue;
                            iterator.remove();
                            this.stateLookup.put((Object)blockState, 0);
                        }
                        if (set.size() > 1) {
                            this.addStates((Iterable<BlockState>)set);
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
                        ModelDefinition modelDefinition2 = (ModelDefinition)((Supplier)pair2.getSecond()).get();
                        map3.computeIfAbsent(modelDefinition2, modelDefinition -> Sets.newIdentityHashSet()).add(blockState);
                    }
                    catch (Exception exception) {
                        LOGGER.warn("Exception evaluating model definition: '{}'", modelIdentifier, (Object)exception);
                    }
                });
                map6.forEach((modelDefinition, set) -> {
                    Iterator iterator = set.iterator();
                    while (iterator.hasNext()) {
                        BlockState blockState = (BlockState)iterator.next();
                        if (blockState.getRenderType() == BlockRenderType.MODEL) continue;
                        iterator.remove();
                        this.stateLookup.put((Object)blockState, 0);
                    }
                    if (set.size() > 1) {
                        this.addStates((Iterable<BlockState>)set);
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

    private void addStates(Iterable<BlockState> states) {
        int i = this.nextStateId++;
        states.forEach(blockState -> this.stateLookup.put(blockState, i));
    }

    @Nullable
    public BakedModel bake(Identifier id, ModelBakeSettings settings) {
        JsonUnbakedModel jsonUnbakedModel;
        Triple triple = Triple.of((Object)id, (Object)settings.getRotation(), (Object)settings.isUvLocked());
        if (this.bakedModelCache.containsKey(triple)) {
            return this.bakedModelCache.get(triple);
        }
        if (this.spriteAtlasManager == null) {
            throw new IllegalStateException("bake called too early");
        }
        UnbakedModel unbakedModel = this.getOrLoadModel(id);
        if (unbakedModel instanceof JsonUnbakedModel && (jsonUnbakedModel = (JsonUnbakedModel)unbakedModel).getRootModel() == GENERATION_MARKER) {
            return ITEM_MODEL_GENERATOR.create(this.spriteAtlasManager::getSprite, jsonUnbakedModel).bake(this, jsonUnbakedModel, this.spriteAtlasManager::getSprite, settings, id, false);
        }
        BakedModel bakedModel = unbakedModel.bake(this, this.spriteAtlasManager::getSprite, settings, id);
        this.bakedModelCache.put((Triple<Identifier, AffineTransformation, Boolean>)triple, bakedModel);
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
                    if (!BUILTIN_GENERATED.equals(string)) break block7;
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
            if (!BUILTIN_ENTITY.equals(string)) break block8;
            JsonUnbakedModel jsonUnbakedModel = BLOCK_ENTITY_MARKER;
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(resource);
            return jsonUnbakedModel;
        }
        if (string.startsWith(BUILTIN)) {
            String string2 = string.substring(BUILTIN.length());
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

    public Object2IntMap<BlockState> getStateLookup() {
        return this.stateLookup;
    }

    private static /* synthetic */ void method_4731(ImmutableList immutableList, StateManager stateManager, Map map, List list, MultipartUnbakedModel multipartUnbakedModel, Pair pair, ModelVariantMap modelVariantMap, Identifier identifier, Pair pair2, String string, WeightedUnbakedModel weightedUnbakedModel) {
        try {
            immutableList.stream().filter(ModelLoader.stateKeyToPredicate(stateManager, string)).forEach(blockState -> {
                Pair pair2 = map.put(blockState, Pair.of((Object)weightedUnbakedModel, () -> ModelDefinition.create(blockState, weightedUnbakedModel, list)));
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
        map.put(blockState, Pair.of((Object)multipartUnbakedModel, () -> ModelDefinition.create(blockState, multipartUnbakedModel, list)));
    }

    @Environment(value=EnvType.CLIENT)
    static class ModelLoaderException
    extends RuntimeException {
        public ModelLoaderException(String message) {
            super(message);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ModelDefinition {
        private final List<UnbakedModel> components;
        private final List<Object> values;

        public ModelDefinition(List<UnbakedModel> components, List<Object> values) {
            this.components = components;
            this.values = values;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof ModelDefinition) {
                ModelDefinition modelDefinition = (ModelDefinition)o;
                return Objects.equals(this.components, modelDefinition.components) && Objects.equals(this.values, modelDefinition.values);
            }
            return false;
        }

        public int hashCode() {
            return 31 * this.components.hashCode() + this.values.hashCode();
        }

        public static ModelDefinition create(BlockState state, MultipartUnbakedModel rawModel, Collection<Property<?>> properties) {
            StateManager<Block, BlockState> stateManager = state.getBlock().getStateManager();
            List list = (List)rawModel.getComponents().stream().filter(multipartModelComponent -> multipartModelComponent.getPredicate(stateManager).test(state)).map(MultipartModelComponent::getModel).collect(ImmutableList.toImmutableList());
            List<Object> list2 = ModelDefinition.getStateValues(state, properties);
            return new ModelDefinition(list, list2);
        }

        public static ModelDefinition create(BlockState state, UnbakedModel rawModel, Collection<Property<?>> properties) {
            List<Object> list = ModelDefinition.getStateValues(state, properties);
            return new ModelDefinition((List<UnbakedModel>)ImmutableList.of((Object)rawModel), list);
        }

        private static List<Object> getStateValues(BlockState state, Collection<Property<?>> properties) {
            return (List)properties.stream().map(state::get).collect(ImmutableList.toImmutableList());
        }
    }
}

