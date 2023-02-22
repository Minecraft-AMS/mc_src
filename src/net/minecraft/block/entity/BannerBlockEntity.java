/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Pair
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Nameable;
import org.jetbrains.annotations.Nullable;

public class BannerBlockEntity
extends BlockEntity
implements Nameable {
    @Nullable
    private Text customName;
    @Nullable
    private DyeColor baseColor = DyeColor.WHITE;
    @Nullable
    private NbtList patternListTag;
    private boolean patternListTagRead;
    @Nullable
    private List<Pair<BannerPattern, DyeColor>> patterns;

    public BannerBlockEntity() {
        super(BlockEntityType.BANNER);
    }

    public BannerBlockEntity(DyeColor baseColor) {
        this();
        this.baseColor = baseColor;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static NbtList getPatternListTag(ItemStack stack) {
        NbtList nbtList = null;
        NbtCompound nbtCompound = stack.getSubTag("BlockEntityTag");
        if (nbtCompound != null && nbtCompound.contains("Patterns", 9)) {
            nbtList = nbtCompound.getList("Patterns", 10).copy();
        }
        return nbtList;
    }

    @Environment(value=EnvType.CLIENT)
    public void readFrom(ItemStack stack, DyeColor baseColor) {
        this.patternListTag = BannerBlockEntity.getPatternListTag(stack);
        this.baseColor = baseColor;
        this.patterns = null;
        this.patternListTagRead = true;
        this.customName = stack.hasCustomName() ? stack.getName() : null;
    }

    @Override
    public Text getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return new TranslatableText("block.minecraft.banner");
    }

    @Override
    @Nullable
    public Text getCustomName() {
        return this.customName;
    }

    public void setCustomName(Text customName) {
        this.customName = customName;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.patternListTag != null) {
            nbt.put("Patterns", this.patternListTag);
        }
        if (this.customName != null) {
            nbt.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
        return nbt;
    }

    @Override
    public void fromTag(BlockState state, NbtCompound tag) {
        super.fromTag(state, tag);
        if (tag.contains("CustomName", 8)) {
            this.customName = Text.Serializer.fromJson(tag.getString("CustomName"));
        }
        this.baseColor = this.hasWorld() ? ((AbstractBannerBlock)this.getCachedState().getBlock()).getColor() : null;
        this.patternListTag = tag.getList("Patterns", 10);
        this.patterns = null;
        this.patternListTagRead = true;
    }

    @Override
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 6, this.toInitialChunkDataNbt());
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.writeNbt(new NbtCompound());
    }

    public static int getPatternCount(ItemStack stack) {
        NbtCompound nbtCompound = stack.getSubTag("BlockEntityTag");
        if (nbtCompound != null && nbtCompound.contains("Patterns")) {
            return nbtCompound.getList("Patterns", 10).size();
        }
        return 0;
    }

    @Environment(value=EnvType.CLIENT)
    public List<Pair<BannerPattern, DyeColor>> getPatterns() {
        if (this.patterns == null && this.patternListTagRead) {
            this.patterns = BannerBlockEntity.method_24280(this.getColorForState(this::getCachedState), this.patternListTag);
        }
        return this.patterns;
    }

    @Environment(value=EnvType.CLIENT)
    public static List<Pair<BannerPattern, DyeColor>> method_24280(DyeColor dyeColor, @Nullable NbtList nbtList) {
        ArrayList list = Lists.newArrayList();
        list.add(Pair.of((Object)((Object)BannerPattern.BASE), (Object)dyeColor));
        if (nbtList != null) {
            for (int i = 0; i < nbtList.size(); ++i) {
                NbtCompound nbtCompound = nbtList.getCompound(i);
                BannerPattern bannerPattern = BannerPattern.byId(nbtCompound.getString("Pattern"));
                if (bannerPattern == null) continue;
                int j = nbtCompound.getInt("Color");
                list.add(Pair.of((Object)((Object)bannerPattern), (Object)DyeColor.byId(j)));
            }
        }
        return list;
    }

    public static void loadFromItemStack(ItemStack stack) {
        NbtCompound nbtCompound = stack.getSubTag("BlockEntityTag");
        if (nbtCompound == null || !nbtCompound.contains("Patterns", 9)) {
            return;
        }
        NbtList nbtList = nbtCompound.getList("Patterns", 10);
        if (nbtList.isEmpty()) {
            return;
        }
        nbtList.remove(nbtList.size() - 1);
        if (nbtList.isEmpty()) {
            stack.removeSubTag("BlockEntityTag");
        }
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockState state) {
        ItemStack itemStack = new ItemStack(BannerBlock.getForColor(this.getColorForState(() -> state)));
        if (this.patternListTag != null && !this.patternListTag.isEmpty()) {
            itemStack.getOrCreateSubTag("BlockEntityTag").put("Patterns", this.patternListTag.copy());
        }
        if (this.customName != null) {
            itemStack.setCustomName(this.customName);
        }
        return itemStack;
    }

    public DyeColor getColorForState(Supplier<BlockState> supplier) {
        if (this.baseColor == null) {
            this.baseColor = ((AbstractBannerBlock)supplier.get().getBlock()).getColor();
        }
        return this.baseColor;
    }
}

