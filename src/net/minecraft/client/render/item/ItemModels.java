/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.item;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ItemModels {
    public final Int2ObjectMap<ModelIdentifier> modelIds = new Int2ObjectOpenHashMap(256);
    private final Int2ObjectMap<BakedModel> models = new Int2ObjectOpenHashMap(256);
    private final BakedModelManager modelManager;

    public ItemModels(BakedModelManager modelManager) {
        this.modelManager = modelManager;
    }

    public BakedModel getModel(ItemStack stack) {
        BakedModel bakedModel = this.getModel(stack.getItem());
        return bakedModel == null ? this.modelManager.getMissingModel() : bakedModel;
    }

    @Nullable
    public BakedModel getModel(Item item) {
        return (BakedModel)this.models.get(ItemModels.getModelId(item));
    }

    private static int getModelId(Item item) {
        return Item.getRawId(item);
    }

    public void putModel(Item item, ModelIdentifier modelId) {
        this.modelIds.put(ItemModels.getModelId(item), (Object)modelId);
    }

    public BakedModelManager getModelManager() {
        return this.modelManager;
    }

    public void reloadModels() {
        this.models.clear();
        for (Map.Entry entry : this.modelIds.entrySet()) {
            this.models.put((Integer)entry.getKey(), (Object)this.modelManager.getModel((ModelIdentifier)entry.getValue()));
        }
    }
}

