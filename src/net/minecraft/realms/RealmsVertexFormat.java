/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.realms;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.realms.RealmsVertexFormatElement;

@Environment(value=EnvType.CLIENT)
public class RealmsVertexFormat {
    private VertexFormat v;

    public RealmsVertexFormat(VertexFormat vertexFormat) {
        this.v = vertexFormat;
    }

    public RealmsVertexFormat from(VertexFormat vertexFormat) {
        this.v = vertexFormat;
        return this;
    }

    public VertexFormat getVertexFormat() {
        return this.v;
    }

    public void clear() {
        this.v.clear();
    }

    public int getUvOffset(int i) {
        return this.v.getUvOffset(i);
    }

    public int getElementCount() {
        return this.v.getElementCount();
    }

    public boolean hasColor() {
        return this.v.hasColorElement();
    }

    public boolean hasUv(int i) {
        return this.v.hasUvElement(i);
    }

    public RealmsVertexFormatElement getElement(int i) {
        return new RealmsVertexFormatElement(this.v.getElement(i));
    }

    public RealmsVertexFormat addElement(RealmsVertexFormatElement realmsVertexFormatElement) {
        return this.from(this.v.add(realmsVertexFormatElement.getVertexFormatElement()));
    }

    public int getColorOffset() {
        return this.v.getColorOffset();
    }

    public List<RealmsVertexFormatElement> getElements() {
        ArrayList list = Lists.newArrayList();
        for (VertexFormatElement vertexFormatElement : this.v.getElements()) {
            list.add(new RealmsVertexFormatElement(vertexFormatElement));
        }
        return list;
    }

    public boolean hasNormal() {
        return this.v.hasNormalElement();
    }

    public int getVertexSize() {
        return this.v.getVertexSize();
    }

    public int getOffset(int i) {
        return this.v.getElementOffset(i);
    }

    public int getNormalOffset() {
        return this.v.getNormalOffset();
    }

    public int getIntegerSize() {
        return this.v.getVertexSizeInteger();
    }

    public boolean equals(Object o) {
        return this.v.equals(o);
    }

    public int hashCode() {
        return this.v.hashCode();
    }

    public String toString() {
        return this.v.toString();
    }
}
