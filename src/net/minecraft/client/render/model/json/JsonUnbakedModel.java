/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Joiner
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.render.model.json;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.BuiltinBakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class JsonUnbakedModel
implements UnbakedModel {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final BakedQuadFactory QUAD_FACTORY = new BakedQuadFactory();
    @VisibleForTesting
    static final Gson GSON = new GsonBuilder().registerTypeAdapter(JsonUnbakedModel.class, (Object)new Deserializer()).registerTypeAdapter(ModelElement.class, (Object)new ModelElement.Deserializer()).registerTypeAdapter(ModelElementFace.class, (Object)new ModelElementFace.Deserializer()).registerTypeAdapter(ModelElementTexture.class, (Object)new ModelElementTexture.Deserializer()).registerTypeAdapter(Transformation.class, (Object)new Transformation.Deserializer()).registerTypeAdapter(ModelTransformation.class, (Object)new ModelTransformation.Deserializer()).registerTypeAdapter(ModelOverride.class, (Object)new ModelOverride.Deserializer()).create();
    private static final char TEXTURE_REFERENCE_INITIAL = '#';
    public static final String PARTICLE_KEY = "particle";
    private final List<ModelElement> elements;
    @Nullable
    private final GuiLight guiLight;
    private final boolean ambientOcclusion;
    private final ModelTransformation transformations;
    private final List<ModelOverride> overrides;
    public String id = "";
    @VisibleForTesting
    protected final Map<String, Either<SpriteIdentifier, String>> textureMap;
    @Nullable
    protected JsonUnbakedModel parent;
    @Nullable
    protected Identifier parentId;

    public static JsonUnbakedModel deserialize(Reader input) {
        return JsonHelper.deserialize(GSON, input, JsonUnbakedModel.class);
    }

    public static JsonUnbakedModel deserialize(String json) {
        return JsonUnbakedModel.deserialize(new StringReader(json));
    }

    public JsonUnbakedModel(@Nullable Identifier parentId, List<ModelElement> elements, Map<String, Either<SpriteIdentifier, String>> textureMap, boolean ambientOcclusion, @Nullable GuiLight guiLight, ModelTransformation transformations, List<ModelOverride> overrides) {
        this.elements = elements;
        this.ambientOcclusion = ambientOcclusion;
        this.guiLight = guiLight;
        this.textureMap = textureMap;
        this.parentId = parentId;
        this.transformations = transformations;
        this.overrides = overrides;
    }

    public List<ModelElement> getElements() {
        if (this.elements.isEmpty() && this.parent != null) {
            return this.parent.getElements();
        }
        return this.elements;
    }

    public boolean useAmbientOcclusion() {
        if (this.parent != null) {
            return this.parent.useAmbientOcclusion();
        }
        return this.ambientOcclusion;
    }

    public GuiLight getGuiLight() {
        if (this.guiLight != null) {
            return this.guiLight;
        }
        if (this.parent != null) {
            return this.parent.getGuiLight();
        }
        return GuiLight.BLOCK;
    }

    public boolean needsResolution() {
        return this.parentId == null || this.parent != null && this.parent.needsResolution();
    }

    public List<ModelOverride> getOverrides() {
        return this.overrides;
    }

    private ModelOverrideList compileOverrides(ModelLoader modelLoader, JsonUnbakedModel parent) {
        if (this.overrides.isEmpty()) {
            return ModelOverrideList.EMPTY;
        }
        return new ModelOverrideList(modelLoader, parent, modelLoader::getOrLoadModel, this.overrides);
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        HashSet set = Sets.newHashSet();
        for (ModelOverride modelOverride : this.overrides) {
            set.add(modelOverride.getModelId());
        }
        if (this.parentId != null) {
            set.add(this.parentId);
        }
        return set;
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
        LinkedHashSet set = Sets.newLinkedHashSet();
        JsonUnbakedModel jsonUnbakedModel = this;
        while (jsonUnbakedModel.parentId != null && jsonUnbakedModel.parent == null) {
            set.add(jsonUnbakedModel);
            UnbakedModel unbakedModel = unbakedModelGetter.apply(jsonUnbakedModel.parentId);
            if (unbakedModel == null) {
                LOGGER.warn("No parent '{}' while loading model '{}'", (Object)this.parentId, (Object)jsonUnbakedModel);
            }
            if (set.contains(unbakedModel)) {
                LOGGER.warn("Found 'parent' loop while loading model '{}' in chain: {} -> {}", new Object[]{jsonUnbakedModel, set.stream().map(Object::toString).collect(Collectors.joining(" -> ")), this.parentId});
                unbakedModel = null;
            }
            if (unbakedModel == null) {
                jsonUnbakedModel.parentId = ModelLoader.MISSING_ID;
                unbakedModel = unbakedModelGetter.apply(jsonUnbakedModel.parentId);
            }
            if (!(unbakedModel instanceof JsonUnbakedModel)) {
                throw new IllegalStateException("BlockModel parent has to be a block model.");
            }
            jsonUnbakedModel.parent = (JsonUnbakedModel)unbakedModel;
            jsonUnbakedModel = jsonUnbakedModel.parent;
        }
        HashSet set2 = Sets.newHashSet((Object[])new SpriteIdentifier[]{this.resolveSprite(PARTICLE_KEY)});
        for (ModelElement modelElement : this.getElements()) {
            for (ModelElementFace modelElementFace : modelElement.faces.values()) {
                SpriteIdentifier spriteIdentifier = this.resolveSprite(modelElementFace.textureId);
                if (Objects.equals(spriteIdentifier.getTextureId(), MissingSprite.getMissingSpriteId())) {
                    unresolvedTextureReferences.add((Pair<String, String>)Pair.of((Object)modelElementFace.textureId, (Object)this.id));
                }
                set2.add(spriteIdentifier);
            }
        }
        this.overrides.forEach(override -> {
            UnbakedModel unbakedModel = (UnbakedModel)unbakedModelGetter.apply(override.getModelId());
            if (Objects.equals(unbakedModel, this)) {
                return;
            }
            set2.addAll(unbakedModel.getTextureDependencies(unbakedModelGetter, unresolvedTextureReferences));
        });
        if (this.getRootModel() == ModelLoader.GENERATION_MARKER) {
            ItemModelGenerator.LAYERS.forEach(layer -> set2.add(this.resolveSprite((String)layer)));
        }
        return set2;
    }

    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        return this.bake(loader, this, textureGetter, rotationContainer, modelId, true);
    }

    public BakedModel bake(ModelLoader loader, JsonUnbakedModel parent, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Identifier id, boolean hasDepth) {
        Sprite sprite = textureGetter.apply(this.resolveSprite(PARTICLE_KEY));
        if (this.getRootModel() == ModelLoader.BLOCK_ENTITY_MARKER) {
            return new BuiltinBakedModel(this.getTransformations(), this.compileOverrides(loader, parent), sprite, this.getGuiLight().isSide());
        }
        BasicBakedModel.Builder builder = new BasicBakedModel.Builder(this, this.compileOverrides(loader, parent), hasDepth).setParticle(sprite);
        for (ModelElement modelElement : this.getElements()) {
            for (Direction direction : modelElement.faces.keySet()) {
                ModelElementFace modelElementFace = modelElement.faces.get(direction);
                Sprite sprite2 = textureGetter.apply(this.resolveSprite(modelElementFace.textureId));
                if (modelElementFace.cullFace == null) {
                    builder.addQuad(JsonUnbakedModel.createQuad(modelElement, modelElementFace, sprite2, direction, settings, id));
                    continue;
                }
                builder.addQuad(Direction.transform(settings.getRotation().getMatrix(), modelElementFace.cullFace), JsonUnbakedModel.createQuad(modelElement, modelElementFace, sprite2, direction, settings, id));
            }
        }
        return builder.build();
    }

    private static BakedQuad createQuad(ModelElement element, ModelElementFace elementFace, Sprite sprite, Direction side, ModelBakeSettings settings, Identifier id) {
        return QUAD_FACTORY.bake(element.from, element.to, elementFace, sprite, side, settings, element.rotation, element.shade, id);
    }

    public boolean textureExists(String name) {
        return !MissingSprite.getMissingSpriteId().equals(this.resolveSprite(name).getTextureId());
    }

    public SpriteIdentifier resolveSprite(String spriteName) {
        if (JsonUnbakedModel.isTextureReference(spriteName)) {
            spriteName = spriteName.substring(1);
        }
        ArrayList list = Lists.newArrayList();
        Either<SpriteIdentifier, String> either;
        Optional optional;
        while (!(optional = (either = this.resolveTexture(spriteName)).left()).isPresent()) {
            spriteName = (String)either.right().get();
            if (list.contains(spriteName)) {
                LOGGER.warn("Unable to resolve texture due to reference chain {}->{} in {}", new Object[]{Joiner.on((String)"->").join((Iterable)list), spriteName, this.id});
                return new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, MissingSprite.getMissingSpriteId());
            }
            list.add(spriteName);
        }
        return (SpriteIdentifier)optional.get();
    }

    private Either<SpriteIdentifier, String> resolveTexture(String name) {
        JsonUnbakedModel jsonUnbakedModel = this;
        while (jsonUnbakedModel != null) {
            Either<SpriteIdentifier, String> either = jsonUnbakedModel.textureMap.get(name);
            if (either != null) {
                return either;
            }
            jsonUnbakedModel = jsonUnbakedModel.parent;
        }
        return Either.left((Object)new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, MissingSprite.getMissingSpriteId()));
    }

    static boolean isTextureReference(String reference) {
        return reference.charAt(0) == '#';
    }

    public JsonUnbakedModel getRootModel() {
        return this.parent == null ? this : this.parent.getRootModel();
    }

    public ModelTransformation getTransformations() {
        Transformation transformation = this.getTransformation(ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND);
        Transformation transformation2 = this.getTransformation(ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND);
        Transformation transformation3 = this.getTransformation(ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND);
        Transformation transformation4 = this.getTransformation(ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND);
        Transformation transformation5 = this.getTransformation(ModelTransformation.Mode.HEAD);
        Transformation transformation6 = this.getTransformation(ModelTransformation.Mode.GUI);
        Transformation transformation7 = this.getTransformation(ModelTransformation.Mode.GROUND);
        Transformation transformation8 = this.getTransformation(ModelTransformation.Mode.FIXED);
        return new ModelTransformation(transformation, transformation2, transformation3, transformation4, transformation5, transformation6, transformation7, transformation8);
    }

    private Transformation getTransformation(ModelTransformation.Mode renderMode) {
        if (this.parent != null && !this.transformations.isTransformationDefined(renderMode)) {
            return this.parent.getTransformation(renderMode);
        }
        return this.transformations.getTransformation(renderMode);
    }

    public String toString() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public static final class GuiLight
    extends Enum<GuiLight> {
        public static final /* enum */ GuiLight ITEM = new GuiLight("front");
        public static final /* enum */ GuiLight BLOCK = new GuiLight("side");
        private final String name;
        private static final /* synthetic */ GuiLight[] field_21861;

        public static GuiLight[] values() {
            return (GuiLight[])field_21861.clone();
        }

        public static GuiLight valueOf(String string) {
            return Enum.valueOf(GuiLight.class, string);
        }

        private GuiLight(String name) {
            this.name = name;
        }

        public static GuiLight byName(String value) {
            for (GuiLight guiLight : GuiLight.values()) {
                if (!guiLight.name.equals(value)) continue;
                return guiLight;
            }
            throw new IllegalArgumentException("Invalid gui light: " + value);
        }

        public boolean isSide() {
            return this == BLOCK;
        }

        private static /* synthetic */ GuiLight[] method_36920() {
            return new GuiLight[]{ITEM, BLOCK};
        }

        static {
            field_21861 = GuiLight.method_36920();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<JsonUnbakedModel> {
        private static final boolean DEFAULT_AMBIENT_OCCLUSION = true;

        public JsonUnbakedModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            List<ModelElement> list = this.elementsFromJson(jsonDeserializationContext, jsonObject);
            String string = this.parentFromJson(jsonObject);
            Map<String, Either<SpriteIdentifier, String>> map = this.texturesFromJson(jsonObject);
            boolean bl = this.ambientOcclusionFromJson(jsonObject);
            ModelTransformation modelTransformation = ModelTransformation.NONE;
            if (jsonObject.has("display")) {
                JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "display");
                modelTransformation = (ModelTransformation)jsonDeserializationContext.deserialize((JsonElement)jsonObject2, ModelTransformation.class);
            }
            List<ModelOverride> list2 = this.overridesFromJson(jsonDeserializationContext, jsonObject);
            GuiLight guiLight = null;
            if (jsonObject.has("gui_light")) {
                guiLight = GuiLight.byName(JsonHelper.getString(jsonObject, "gui_light"));
            }
            Identifier identifier = string.isEmpty() ? null : new Identifier(string);
            return new JsonUnbakedModel(identifier, list, map, bl, guiLight, modelTransformation, list2);
        }

        protected List<ModelOverride> overridesFromJson(JsonDeserializationContext context, JsonObject object) {
            ArrayList list = Lists.newArrayList();
            if (object.has("overrides")) {
                JsonArray jsonArray = JsonHelper.getArray(object, "overrides");
                for (JsonElement jsonElement : jsonArray) {
                    list.add((ModelOverride)context.deserialize(jsonElement, ModelOverride.class));
                }
            }
            return list;
        }

        private Map<String, Either<SpriteIdentifier, String>> texturesFromJson(JsonObject object) {
            Identifier identifier = SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
            HashMap map = Maps.newHashMap();
            if (object.has("textures")) {
                JsonObject jsonObject = JsonHelper.getObject(object, "textures");
                for (Map.Entry entry : jsonObject.entrySet()) {
                    map.put((String)entry.getKey(), Deserializer.resolveReference(identifier, ((JsonElement)entry.getValue()).getAsString()));
                }
            }
            return map;
        }

        private static Either<SpriteIdentifier, String> resolveReference(Identifier id, String name) {
            if (JsonUnbakedModel.isTextureReference(name)) {
                return Either.right((Object)name.substring(1));
            }
            Identifier identifier = Identifier.tryParse(name);
            if (identifier == null) {
                throw new JsonParseException(name + " is not valid resource location");
            }
            return Either.left((Object)new SpriteIdentifier(id, identifier));
        }

        private String parentFromJson(JsonObject json) {
            return JsonHelper.getString(json, "parent", "");
        }

        protected boolean ambientOcclusionFromJson(JsonObject json) {
            return JsonHelper.getBoolean(json, "ambientocclusion", true);
        }

        protected List<ModelElement> elementsFromJson(JsonDeserializationContext context, JsonObject json) {
            ArrayList list = Lists.newArrayList();
            if (json.has("elements")) {
                for (JsonElement jsonElement : JsonHelper.getArray(json, "elements")) {
                    list.add((ModelElement)context.deserialize(jsonElement, ModelElement.class));
                }
            }
            return list;
        }

        public /* synthetic */ Object deserialize(JsonElement element, Type unused, JsonDeserializationContext ctx) throws JsonParseException {
            return this.deserialize(element, unused, ctx);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class UncheckedModelException
    extends RuntimeException {
        public UncheckedModelException(String message) {
            super(message);
        }
    }
}

