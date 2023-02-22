/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.model;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;

@Environment(value=EnvType.CLIENT)
public class Model {
    public final List<ModelPart> cuboidList = Lists.newArrayList();
    public int textureWidth = 64;
    public int textureHeight = 32;

    public ModelPart getRandomCuboid(Random rand) {
        return this.cuboidList.get(rand.nextInt(this.cuboidList.size()));
    }
}

