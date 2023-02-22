/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
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
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.model.json;

import com.google.common.annotations.VisibleForTesting;
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
import net.minecraft.client.render.model.json.ModelItemOverride;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class JsonUnbakedModel
implements UnbakedModel {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final BakedQuadFactory QUAD_FACTORY = new BakedQuadFactory();
    @VisibleForTesting
    static final Gson GSON = new GsonBuilder().registerTypeAdapter(JsonUnbakedModel.class, (Object)new Deserializer()).registerTypeAdapter(ModelElement.class, (Object)new ModelElement.Deserializer()).registerTypeAdapter(ModelElementFace.class, (Object)new ModelElementFace.Deserializer()).registerTypeAdapter(ModelElementTexture.class, (Object)new ModelElementTexture.Deserializer()).registerTypeAdapter(Transformation.class, (Object)new Transformation.Deserializer()).registerTypeAdapter(ModelTransformation.class, (Object)new ModelTransformation.Deserializer()).registerTypeAdapter(ModelItemOverride.class, (Object)new ModelItemOverride.Deserializer()).create();
    private final List<ModelElement> elements;
    private final boolean depthInGui;
    private final boolean ambientOcclusion;
    private final ModelTransformation transformations;
    private final List<ModelItemOverride> overrides;
    public String id = "";
    @VisibleForTesting
    protected final Map<String, String> textureMap;
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

    public JsonUnbakedModel(@Nullable Identifier parentId, List<ModelElement> elements, Map<String, String> textureMap, boolean ambientOcclusion, boolean depthInGui, ModelTransformation transformations, List<ModelItemOverride> overrides) {
        this.elements = elements;
        this.ambientOcclusion = ambientOcclusion;
        this.depthInGui = depthInGui;
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

    public boolean hasDepthInGui() {
        return this.depthInGui;
    }

    public List<ModelItemOverride> getOverrides() {
        return this.overrides;
    }

    private ModelItemPropertyOverrideList compileOverrides(ModelLoader modelLoader, JsonUnbakedModel parent) {
        if (this.overrides.isEmpty()) {
            return ModelItemPropertyOverrideList.EMPTY;
        }
        return new ModelItemPropertyOverrideList(modelLoader, parent, modelLoader::getOrLoadModel, this.overrides);
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        HashSet set = Sets.newHashSet();
        for (ModelItemOverride modelItemOverride : this.overrides) {
            set.add(modelItemOverride.getModelId());
        }
        if (this.parentId != null) {
            set.add(this.parentId);
        }
        return set;
    }

    @Override
    public Collection<Identifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<String> unresolvedTextureReferences) {
        LinkedHashSet set = Sets.newLinkedHashSet();
        JsonUnbakedModel jsonUnbakedModel = this;
        while (jsonUnbakedModel.parentId != null && jsonUnbakedModel.parent == null) {
            set.add(jsonUnbakedModel);
            UnbakedModel unbakedModel = unbakedModelGetter.apply(jsonUnbakedModel.parentId);
            if (unbakedModel == null) {
                LOGGER.warn("No parent '{}' while loading model '{}'", (Object)this.parentId, (Object)jsonUnbakedModel);
            }
            if (set.contains(unbakedModel)) {
                LOGGER.warn("Found 'parent' loop while loading model '{}' in chain: {} -> {}", (Object)jsonUnbakedModel, (Object)set.stream().map(Object::toString).collect(Collectors.joining(" -> ")), (Object)this.parentId);
                unbakedModel = null;
            }
            if (unbakedModel == null) {
                jsonUnbakedModel.parentId = ModelLoader.MISSING;
                unbakedModel = unbakedModelGetter.apply(jsonUnbakedModel.parentId);
            }
            if (!(unbakedModel instanceof JsonUnbakedModel)) {
                throw new IllegalStateException("BlockModel parent has to be a block model.");
            }
            jsonUnbakedModel.parent = (JsonUnbakedModel)unbakedModel;
            jsonUnbakedModel = jsonUnbakedModel.parent;
        }
        HashSet set2 = Sets.newHashSet((Object[])new Identifier[]{new Identifier(this.resolveTexture("particle"))});
        for (ModelElement modelElement : this.getElements()) {
            for (ModelElementFace modelElementFace : modelElement.faces.values()) {
                String string2 = this.resolveTexture(modelElementFace.textureId);
                if (Objects.equals(string2, MissingSprite.getMissingSpriteId().toString())) {
                    unresolvedTextureReferences.add(String.format("%s in %s", modelElementFace.textureId, this.id));
                }
                set2.add(new Identifier(string2));
            }
        }
        this.overrides.forEach(modelItemOverride -> {
            UnbakedModel unbakedModel = (UnbakedModel)unbakedModelGetter.apply(modelItemOverride.getModelId());
            if (Objects.equals(unbakedModel, this)) {
                return;
            }
            set2.addAll(unbakedModel.getTextureDependencies(unbakedModelGetter, unresolvedTextureReferences));
        });
        if (this.getRootModel() == ModelLoader.GENERATION_MARKER) {
            ItemModelGenerator.LAYERS.forEach(string -> set2.add(new Identifier(this.resolveTexture((String)string))));
        }
        return set2;
    }

    @Override
    public BakedModel bake(ModelLoader loader, Function<Identifier, Sprite> textureGetter, ModelBakeSettings rotationContainer) {
        return this.bake(loader, this, textureGetter, rotationContainer);
    }

    public BakedModel bake(ModelLoader loader, JsonUnbakedModel parent, Function<Identifier, Sprite> textureGetter, ModelBakeSettings settings) {
        Sprite sprite = textureGetter.apply(new Identifier(this.resolveTexture("particle")));
        if (this.getRootModel() == ModelLoader.BLOCK_ENTITY_MARKER) {
            return new BuiltinBakedModel(this.getTransformations(), this.compileOverrides(loader, parent), sprite);
        }
        BasicBakedModel.Builder builder = new BasicBakedModel.Builder(this, this.compileOverrides(loader, parent)).setParticle(sprite);
        for (ModelElement modelElement : this.getElements()) {
            for (Direction direction : modelElement.faces.keySet()) {
                ModelElementFace modelElementFace = modelElement.faces.get(direction);
                Sprite sprite2 = textureGetter.apply(new Identifier(this.resolveTexture(modelElementFace.textureId)));
                if (modelElementFace.cullFace == null) {
                    builder.addQuad(JsonUnbakedModel.createQuad(modelElement, modelElementFace, sprite2, direction, settings));
                    continue;
                }
                builder.addQuad(settings.getRotation().apply(modelElementFace.cullFace), JsonUnbakedModel.createQuad(modelElement, modelElementFace, sprite2, direction, settings));
            }
        }
        return builder.build();
    }

    private static BakedQuad createQuad(ModelElement element, ModelElementFace elementFace, Sprite sprite, Direction side, ModelBakeSettings settings) {
        return QUAD_FACTORY.bake(element.from, element.to, elementFace, sprite, side, settings, element.rotation, element.shade);
    }

    public boolean textureExists(String name) {
        return !MissingSprite.getMissingSpriteId().toString().equals(this.resolveTexture(name));
    }

    public String resolveTexture(String name) {
        if (!this.isTextureReference(name)) {
            name = '#' + name;
        }
        return this.resolveTexture(name, new TextureResolutionContext(this));
    }

    private String resolveTexture(String name, TextureResolutionContext context) {
        if (this.isTextureReference(name)) {
            if (this == context.current) {
                LOGGER.warn("Unable to resolve texture due to upward reference: {} in {}", (Object)name, (Object)this.id);
                return MissingSprite.getMissingSpriteId().toString();
            }
            String string = this.textureMap.get(name.substring(1));
            if (string == null && this.parent != null) {
                string = this.parent.resolveTexture(name, context);
            }
            context.current = this;
            if (string != null && this.isTextureReference(string)) {
                string = context.root.resolveTexture(string, context);
            }
            if (string == null || this.isTextureReference(string)) {
                return MissingSprite.getMissingSpriteId().toString();
            }
            return string;
        }
        return name;
    }

    private boolean isTextureReference(String name) {
        return name.charAt(0) == '#';
    }

    public JsonUnbakedModel getRootModel() {
        return this.parent == null ? this : this.parent.getRootModel();
    }

    public ModelTransformation getTransformations() {
        Transformation transformation = this.getTransformation(ModelTransformation.Type.THIRD_PERSON_LEFT_HAND);
        Transformation transformation2 = this.getTransformation(ModelTransformation.Type.THIRD_PERSON_RIGHT_HAND);
        Transformation transformation3 = this.getTransformation(ModelTransformation.Type.FIRST_PERSON_LEFT_HAND);
        Transformation transformation4 = this.getTransformation(ModelTransformation.Type.FIRST_PERSON_RIGHT_HAND);
        Transformation transformation5 = this.getTransformation(ModelTransformation.Type.HEAD);
        Transformation transformation6 = this.getTransformation(ModelTransformation.Type.GUI);
        Transformation transformation7 = this.getTransformation(ModelTransformation.Type.GROUND);
        Transformation transformation8 = this.getTransformation(ModelTransformation.Type.FIXED);
        return new ModelTransformation(transformation, transformation2, transformation3, transformation4, transformation5, transformation6, transformation7, transformation8);
    }

    private Transformation getTransformation(ModelTransformation.Type renderMode) {
        if (this.parent != null && !this.transformations.isTransformationDefined(renderMode)) {
            return this.parent.getTransformation(renderMode);
        }
        return this.transformations.getTransformation(renderMode);
    }

    public String toString() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Deserializer
    implements JsonDeserializer<JsonUnbakedModel> {
        public JsonUnbakedModel deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = element.getAsJsonObject();
            List<ModelElement> list = this.deserializeElements(context, jsonObject);
            String string = this.deserializeParent(jsonObject);
            Map<String, String> map = this.deserializeTextures(jsonObject);
            boolean bl = this.deserializeAmbientOcclusion(jsonObject);
            ModelTransformation modelTransformation = ModelTransformation.NONE;
            if (jsonObject.has("display")) {
                JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "display");
                modelTransformation = (ModelTransformation)context.deserialize((JsonElement)jsonObject2, ModelTransformation.class);
            }
            List<ModelItemOverride> list2 = this.deserializeOverrides(context, jsonObject);
            Identifier identifier = string.isEmpty() ? null : new Identifier(string);
            return new JsonUnbakedModel(identifier, list, map, bl, true, modelTransformation, list2);
        }

        protected List<ModelItemOverride> deserializeOverrides(JsonDeserializationContext context, JsonObject object) {
            ArrayList list = Lists.newArrayList();
            if (object.has("overrides")) {
                JsonArray jsonArray = JsonHelper.getArray(object, "overrides");
                for (JsonElement jsonElement : jsonArray) {
                    list.add(context.deserialize(jsonElement, ModelItemOverride.class));
                }
            }
            return list;
        }

        private Map<String, String> deserializeTextures(JsonObject object) {
            HashMap map = Maps.newHashMap();
            if (object.has("textures")) {
                JsonObject jsonObject = JsonHelper.getObject(object, "textures");
                for (Map.Entry entry : jsonObject.entrySet()) {
                    map.put(entry.getKey(), ((JsonElement)entry.getValue()).getAsString());
                }
            }
            return map;
        }

        private String deserializeParent(JsonObject object) {
            return JsonHelper.getString(object, "parent", "");
        }

        protected boolean deserializeAmbientOcclusion(JsonObject object) {
            return JsonHelper.getBoolean(object, "ambientocclusion", true);
        }

        protected List<ModelElement> deserializeElements(JsonDeserializationContext context, JsonObject object) {
            ArrayList list = Lists.newArrayList();
            if (object.has("elements")) {
                for (JsonElement jsonElement : JsonHelper.getArray(object, "elements")) {
                    list.add(context.deserialize(jsonElement, ModelElement.class));
                }
            }
            return list;
        }

        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.deserialize(jsonElement, type, jsonDeserializationContext);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class TextureResolutionContext {
        public final JsonUnbakedModel root;
        public JsonUnbakedModel current;

        private TextureResolutionContext(JsonUnbakedModel root) {
            this.root = root;
        }
    }
}

