/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 */
package net.minecraft.nbt.scanner;

import com.google.common.collect.ImmutableSet;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtType;
import net.minecraft.nbt.scanner.NbtCollector;
import net.minecraft.nbt.scanner.NbtScanQuery;
import net.minecraft.nbt.scanner.NbtScanner;
import net.minecraft.nbt.scanner.NbtTreeNode;

public class SelectiveNbtCollector
extends NbtCollector {
    private int queriesLeft;
    private final Set<NbtType<?>> allPossibleTypes;
    private final Deque<NbtTreeNode> selectionStack = new ArrayDeque<NbtTreeNode>();

    public SelectiveNbtCollector(NbtScanQuery ... queries) {
        this.queriesLeft = queries.length;
        ImmutableSet.Builder builder = ImmutableSet.builder();
        NbtTreeNode nbtTreeNode = NbtTreeNode.createRoot();
        for (NbtScanQuery nbtScanQuery : queries) {
            nbtTreeNode.add(nbtScanQuery);
            builder.add(nbtScanQuery.type());
        }
        this.selectionStack.push(nbtTreeNode);
        builder.add(NbtCompound.TYPE);
        this.allPossibleTypes = builder.build();
    }

    @Override
    public NbtScanner.Result start(NbtType<?> rootType) {
        if (rootType != NbtCompound.TYPE) {
            return NbtScanner.Result.HALT;
        }
        return super.start(rootType);
    }

    @Override
    public NbtScanner.NestedResult visitSubNbtType(NbtType<?> type) {
        NbtTreeNode nbtTreeNode = this.selectionStack.element();
        if (this.getDepth() > nbtTreeNode.depth()) {
            return super.visitSubNbtType(type);
        }
        if (this.queriesLeft <= 0) {
            return NbtScanner.NestedResult.HALT;
        }
        if (!this.allPossibleTypes.contains(type)) {
            return NbtScanner.NestedResult.SKIP;
        }
        return super.visitSubNbtType(type);
    }

    @Override
    public NbtScanner.NestedResult startSubNbt(NbtType<?> type, String key) {
        NbtTreeNode nbtTreeNode2;
        NbtTreeNode nbtTreeNode = this.selectionStack.element();
        if (this.getDepth() > nbtTreeNode.depth()) {
            return super.startSubNbt(type, key);
        }
        if (nbtTreeNode.selectedFields().remove(key, type)) {
            --this.queriesLeft;
            return super.startSubNbt(type, key);
        }
        if (type == NbtCompound.TYPE && (nbtTreeNode2 = nbtTreeNode.fieldsToRecurse().get(key)) != null) {
            this.selectionStack.push(nbtTreeNode2);
            return super.startSubNbt(type, key);
        }
        return NbtScanner.NestedResult.SKIP;
    }

    @Override
    public NbtScanner.Result endNested() {
        if (this.getDepth() == this.selectionStack.element().depth()) {
            this.selectionStack.pop();
        }
        return super.endNested();
    }

    public int getQueriesLeft() {
        return this.queriesLeft;
    }
}

