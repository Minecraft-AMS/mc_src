/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity.ai.pathing;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.TargetPathNode;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class Path {
    private final List<PathNode> nodes;
    private PathNode[] debugNodes = new PathNode[0];
    private PathNode[] debugSecondNodes = new PathNode[0];
    private Set<TargetPathNode> debugTargetNodes;
    private int currentNodeIndex;
    private final BlockPos target;
    private final float manhattanDistanceFromTarget;
    private final boolean reachesTarget;

    public Path(List<PathNode> nodes, BlockPos target, boolean reachesTarget) {
        this.nodes = nodes;
        this.target = target;
        this.manhattanDistanceFromTarget = nodes.isEmpty() ? Float.MAX_VALUE : this.nodes.get(this.nodes.size() - 1).getManhattanDistance(this.target);
        this.reachesTarget = reachesTarget;
    }

    public void next() {
        ++this.currentNodeIndex;
    }

    public boolean isStart() {
        return this.currentNodeIndex <= 0;
    }

    public boolean isFinished() {
        return this.currentNodeIndex >= this.nodes.size();
    }

    @Nullable
    public PathNode getEnd() {
        if (!this.nodes.isEmpty()) {
            return this.nodes.get(this.nodes.size() - 1);
        }
        return null;
    }

    public PathNode getNode(int index) {
        return this.nodes.get(index);
    }

    public void setLength(int length) {
        if (this.nodes.size() > length) {
            this.nodes.subList(length, this.nodes.size()).clear();
        }
    }

    public void setNode(int index, PathNode node) {
        this.nodes.set(index, node);
    }

    public int getLength() {
        return this.nodes.size();
    }

    public int getCurrentNodeIndex() {
        return this.currentNodeIndex;
    }

    public void setCurrentNodeIndex(int index) {
        this.currentNodeIndex = index;
    }

    public Vec3d getNodePosition(Entity entity, int index) {
        PathNode pathNode = this.nodes.get(index);
        double d = (double)pathNode.x + (double)((int)(entity.getWidth() + 1.0f)) * 0.5;
        double e = pathNode.y;
        double f = (double)pathNode.z + (double)((int)(entity.getWidth() + 1.0f)) * 0.5;
        return new Vec3d(d, e, f);
    }

    public BlockPos getNodePos(int index) {
        return this.nodes.get(index).getBlockPos();
    }

    public Vec3d getNodePosition(Entity entity) {
        return this.getNodePosition(entity, this.currentNodeIndex);
    }

    public BlockPos getCurrentNodePos() {
        return this.nodes.get(this.currentNodeIndex).getBlockPos();
    }

    public PathNode getCurrentNode() {
        return this.nodes.get(this.currentNodeIndex);
    }

    @Nullable
    public PathNode getLastNode() {
        return this.currentNodeIndex > 0 ? this.nodes.get(this.currentNodeIndex - 1) : null;
    }

    public boolean equalsPath(@Nullable Path o) {
        if (o == null) {
            return false;
        }
        if (o.nodes.size() != this.nodes.size()) {
            return false;
        }
        for (int i = 0; i < this.nodes.size(); ++i) {
            PathNode pathNode = this.nodes.get(i);
            PathNode pathNode2 = o.nodes.get(i);
            if (pathNode.x == pathNode2.x && pathNode.y == pathNode2.y && pathNode.z == pathNode2.z) continue;
            return false;
        }
        return true;
    }

    public boolean reachesTarget() {
        return this.reachesTarget;
    }

    @Debug
    void setDebugInfo(PathNode[] debugNodes, PathNode[] debugSecondNodes, Set<TargetPathNode> debugTargetNodes) {
        this.debugNodes = debugNodes;
        this.debugSecondNodes = debugSecondNodes;
        this.debugTargetNodes = debugTargetNodes;
    }

    @Debug
    public PathNode[] getDebugNodes() {
        return this.debugNodes;
    }

    @Debug
    public PathNode[] getDebugSecondNodes() {
        return this.debugSecondNodes;
    }

    public void toBuffer(PacketByteBuf buffer) {
        if (this.debugTargetNodes == null || this.debugTargetNodes.isEmpty()) {
            return;
        }
        buffer.writeBoolean(this.reachesTarget);
        buffer.writeInt(this.currentNodeIndex);
        buffer.writeInt(this.debugTargetNodes.size());
        this.debugTargetNodes.forEach(targetPathNode -> targetPathNode.toBuffer(buffer));
        buffer.writeInt(this.target.getX());
        buffer.writeInt(this.target.getY());
        buffer.writeInt(this.target.getZ());
        buffer.writeInt(this.nodes.size());
        for (PathNode pathNode : this.nodes) {
            pathNode.toBuffer(buffer);
        }
        buffer.writeInt(this.debugNodes.length);
        for (PathNode pathNode2 : this.debugNodes) {
            pathNode2.toBuffer(buffer);
        }
        buffer.writeInt(this.debugSecondNodes.length);
        for (PathNode pathNode2 : this.debugSecondNodes) {
            pathNode2.toBuffer(buffer);
        }
    }

    public static Path fromBuffer(PacketByteBuf buffer) {
        boolean bl = buffer.readBoolean();
        int i = buffer.readInt();
        int j = buffer.readInt();
        HashSet set = Sets.newHashSet();
        for (int k = 0; k < j; ++k) {
            set.add(TargetPathNode.fromBuffer(buffer));
        }
        BlockPos blockPos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
        ArrayList list = Lists.newArrayList();
        int l = buffer.readInt();
        for (int m = 0; m < l; ++m) {
            list.add(PathNode.readBuf(buffer));
        }
        PathNode[] pathNodes = new PathNode[buffer.readInt()];
        for (int n = 0; n < pathNodes.length; ++n) {
            pathNodes[n] = PathNode.readBuf(buffer);
        }
        PathNode[] pathNodes2 = new PathNode[buffer.readInt()];
        for (int o = 0; o < pathNodes2.length; ++o) {
            pathNodes2[o] = PathNode.readBuf(buffer);
        }
        Path path = new Path(list, blockPos, bl);
        path.debugNodes = pathNodes;
        path.debugSecondNodes = pathNodes2;
        path.debugTargetNodes = set;
        path.currentNodeIndex = i;
        return path;
    }

    public String toString() {
        return "Path(length=" + this.nodes.size() + ")";
    }

    public BlockPos getTarget() {
        return this.target;
    }

    public float getManhattanDistanceFromTarget() {
        return this.manhattanDistanceFromTarget;
    }
}

