/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelCuboidData;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;

@Environment(value=EnvType.CLIENT)
public class ModelData {
    private ModelPartData data = new ModelPartData((List<ModelCuboidData>)ImmutableList.of(), ModelTransform.NONE);

    public ModelPartData getRoot() {
        return this.data;
    }
}

