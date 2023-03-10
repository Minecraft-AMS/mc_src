/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class JigsawBlockEntity
extends BlockEntity {
    public static final String TARGET_KEY = "target";
    public static final String POOL_KEY = "pool";
    public static final String JOINT_KEY = "joint";
    public static final String NAME_KEY = "name";
    public static final String FINAL_STATE_KEY = "final_state";
    private Identifier name = new Identifier("empty");
    private Identifier target = new Identifier("empty");
    private Identifier pool = new Identifier("empty");
    private Joint joint = Joint.ROLLABLE;
    private String finalState = "minecraft:air";

    public JigsawBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.JIGSAW, pos, state);
    }

    public Identifier getName() {
        return this.name;
    }

    public Identifier getTarget() {
        return this.target;
    }

    public Identifier getPool() {
        return this.pool;
    }

    public String getFinalState() {
        return this.finalState;
    }

    public Joint getJoint() {
        return this.joint;
    }

    public void setName(Identifier name) {
        this.name = name;
    }

    public void setTarget(Identifier target) {
        this.target = target;
    }

    public void setPool(Identifier pool) {
        this.pool = pool;
    }

    public void setFinalState(String finalState) {
        this.finalState = finalState;
    }

    public void setJoint(Joint joint) {
        this.joint = joint;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString(NAME_KEY, this.name.toString());
        nbt.putString(TARGET_KEY, this.target.toString());
        nbt.putString(POOL_KEY, this.pool.toString());
        nbt.putString(FINAL_STATE_KEY, this.finalState);
        nbt.putString(JOINT_KEY, this.joint.asString());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.name = new Identifier(nbt.getString(NAME_KEY));
        this.target = new Identifier(nbt.getString(TARGET_KEY));
        this.pool = new Identifier(nbt.getString(POOL_KEY));
        this.finalState = nbt.getString(FINAL_STATE_KEY);
        this.joint = Joint.byName(nbt.getString(JOINT_KEY)).orElseGet(() -> JigsawBlock.getFacing(this.getCachedState()).getAxis().isHorizontal() ? Joint.ALIGNED : Joint.ROLLABLE);
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    public void generate(ServerWorld world, int maxDepth, boolean keepJigsaws) {
        ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
        StructureManager structureManager = world.getStructureManager();
        StructureAccessor structureAccessor = world.getStructureAccessor();
        Random random = world.getRandom();
        BlockPos blockPos = this.getPos();
        ArrayList list = Lists.newArrayList();
        Structure structure = new Structure();
        structure.saveFromWorld(world, blockPos, new Vec3i(1, 1, 1), false, null);
        SinglePoolElement structurePoolElement = new SinglePoolElement(structure);
        PoolStructurePiece poolStructurePiece = new PoolStructurePiece(structureManager, structurePoolElement, blockPos, 1, BlockRotation.NONE, new BlockBox(blockPos));
        StructurePoolBasedGenerator.generate(world.getRegistryManager(), poolStructurePiece, maxDepth, PoolStructurePiece::new, chunkGenerator, structureManager, list, random, world);
        for (PoolStructurePiece poolStructurePiece2 : list) {
            poolStructurePiece2.generate((StructureWorldAccess)world, structureAccessor, chunkGenerator, random, BlockBox.infinite(), blockPos, keepJigsaws);
        }
    }

    public /* synthetic */ Packet toUpdatePacket() {
        return this.toUpdatePacket();
    }

    public static final class Joint
    extends Enum<Joint>
    implements StringIdentifiable {
        public static final /* enum */ Joint ROLLABLE = new Joint("rollable");
        public static final /* enum */ Joint ALIGNED = new Joint("aligned");
        private final String name;
        private static final /* synthetic */ Joint[] field_23332;

        public static Joint[] values() {
            return (Joint[])field_23332.clone();
        }

        public static Joint valueOf(String string) {
            return Enum.valueOf(Joint.class, string);
        }

        private Joint(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public static Optional<Joint> byName(String name) {
            return Arrays.stream(Joint.values()).filter(joint -> joint.asString().equals(name)).findFirst();
        }

        public Text asText() {
            return new TranslatableText("jigsaw_block.joint." + this.name);
        }

        private static /* synthetic */ Joint[] method_36716() {
            return new Joint[]{ROLLABLE, ALIGNED};
        }

        static {
            field_23332 = Joint.method_36716();
        }
    }
}

