/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.tuple.Pair
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MultipartBakedModel
implements BakedModel {
    private final List<Pair<Predicate<BlockState>, BakedModel>> components;
    protected final boolean ambientOcclusion;
    protected final boolean depthGui;
    protected final Sprite sprite;
    protected final ModelTransformation transformations;
    protected final ModelItemPropertyOverrideList itemPropertyOverrides;
    private final Map<BlockState, BitSet> field_5431 = new Object2ObjectOpenCustomHashMap(Util.identityHashStrategy());

    public MultipartBakedModel(List<Pair<Predicate<BlockState>, BakedModel>> components) {
        this.components = components;
        BakedModel bakedModel = (BakedModel)components.iterator().next().getRight();
        this.ambientOcclusion = bakedModel.useAmbientOcclusion();
        this.depthGui = bakedModel.hasDepth();
        this.sprite = bakedModel.getSprite();
        this.transformations = bakedModel.getTransformation();
        this.itemPropertyOverrides = bakedModel.getItemPropertyOverrides();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        if (state == null) {
            return Collections.emptyList();
        }
        BitSet bitSet = this.field_5431.get(state);
        if (bitSet == null) {
            bitSet = new BitSet();
            for (int i = 0; i < this.components.size(); ++i) {
                Pair<Predicate<BlockState>, BakedModel> pair = this.components.get(i);
                if (!((Predicate)pair.getLeft()).test(state)) continue;
                bitSet.set(i);
            }
            this.field_5431.put(state, bitSet);
        }
        ArrayList list = Lists.newArrayList();
        long l = random.nextLong();
        for (int j = 0; j < bitSet.length(); ++j) {
            if (!bitSet.get(j)) continue;
            list.addAll(((BakedModel)this.components.get(j).getRight()).getQuads(state, face, new Random(l)));
        }
        return list;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.ambientOcclusion;
    }

    @Override
    public boolean hasDepth() {
        return this.depthGui;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getSprite() {
        return this.sprite;
    }

    @Override
    public ModelTransformation getTransformation() {
        return this.transformations;
    }

    @Override
    public ModelItemPropertyOverrideList getItemPropertyOverrides() {
        return this.itemPropertyOverrides;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Builder {
        private final List<Pair<Predicate<BlockState>, BakedModel>> components = Lists.newArrayList();

        public void addComponent(Predicate<BlockState> predicate, BakedModel model) {
            this.components.add((Pair<Predicate<BlockState>, BakedModel>)Pair.of(predicate, (Object)model));
        }

        public BakedModel build() {
            return new MultipartBakedModel(this.components);
        }
    }
}
