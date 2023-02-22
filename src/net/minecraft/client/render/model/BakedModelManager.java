/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.fluid.FluidState;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(value=EnvType.CLIENT)
public class BakedModelManager
extends SinglePreparationResourceReloadListener<ModelLoader> {
    private Map<Identifier, BakedModel> models;
    private final SpriteAtlasTexture spriteAtlas;
    private final BlockModels blockModelCache;
    private final BlockColors colorMap;
    private BakedModel missingModel;
    private Object2IntMap<BlockState> field_20278;

    public BakedModelManager(SpriteAtlasTexture spriteAtlas, BlockColors colorMap) {
        this.spriteAtlas = spriteAtlas;
        this.colorMap = colorMap;
        this.blockModelCache = new BlockModels(this);
    }

    public BakedModel getModel(ModelIdentifier id) {
        return this.models.getOrDefault(id, this.missingModel);
    }

    public BakedModel getMissingModel() {
        return this.missingModel;
    }

    public BlockModels getBlockModels() {
        return this.blockModelCache;
    }

    @Override
    protected ModelLoader prepare(ResourceManager resourceManager, Profiler profiler) {
        profiler.startTick();
        ModelLoader modelLoader = new ModelLoader(resourceManager, this.spriteAtlas, this.colorMap, profiler);
        profiler.endTick();
        return modelLoader;
    }

    @Override
    protected void apply(ModelLoader modelLoader, ResourceManager resourceManager, Profiler profiler) {
        profiler.startTick();
        profiler.push("upload");
        modelLoader.upload(profiler);
        this.models = modelLoader.getBakedModelMap();
        this.field_20278 = modelLoader.method_21605();
        this.missingModel = this.models.get(ModelLoader.MISSING);
        profiler.swap("cache");
        this.blockModelCache.reload();
        profiler.pop();
        profiler.endTick();
    }

    public boolean method_21611(BlockState blockState, BlockState blockState2) {
        int j;
        if (blockState == blockState2) {
            return false;
        }
        int i = this.field_20278.getInt((Object)blockState);
        if (i != -1 && i == (j = this.field_20278.getInt((Object)blockState2))) {
            FluidState fluidState2;
            FluidState fluidState = blockState.getFluidState();
            return fluidState != (fluidState2 = blockState2.getFluidState());
        }
        return true;
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.prepare(manager, profiler);
    }
}

