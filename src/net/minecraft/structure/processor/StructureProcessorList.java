/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure.processor;

import java.util.List;
import net.minecraft.structure.processor.StructureProcessor;

public class StructureProcessorList {
    private final List<StructureProcessor> list;

    public StructureProcessorList(List<StructureProcessor> list) {
        this.list = list;
    }

    public List<StructureProcessor> getList() {
        return this.list;
    }

    public String toString() {
        return "ProcessorList[" + this.list + "]";
    }
}

