/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.block.entity;

import com.google.common.collect.Lists;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Nameable;
import org.jetbrains.annotations.Nullable;

public class BannerBlockEntity
extends BlockEntity
implements Nameable {
    private Text customName;
    private DyeColor baseColor = DyeColor.WHITE;
    private ListTag patternListTag;
    private boolean patternListTagRead;
    private List<BannerPattern> patterns;
    private List<DyeColor> patternColors;
    private String patternCacheKey;

    public BannerBlockEntity() {
        super(BlockEntityType.BANNER);
    }

    public BannerBlockEntity(DyeColor baseColor) {
        this();
        this.baseColor = baseColor;
    }

    @Environment(value=EnvType.CLIENT)
    public void readFrom(ItemStack stack, DyeColor baseColor) {
        this.patternListTag = null;
        CompoundTag compoundTag = stack.getSubTag("BlockEntityTag");
        if (compoundTag != null && compoundTag.contains("Patterns", 9)) {
            this.patternListTag = compoundTag.getList("Patterns", 10).copy();
        }
        this.baseColor = baseColor;
        this.patterns = null;
        this.patternColors = null;
        this.patternCacheKey = "";
        this.patternListTagRead = true;
        this.customName = stack.hasCustomName() ? stack.getName() : null;
    }

    @Override
    public Text getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return new TranslatableText("block.minecraft.banner", new Object[0]);
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
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (this.patternListTag != null) {
            tag.put("Patterns", this.patternListTag);
        }
        if (this.customName != null) {
            tag.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        if (tag.contains("CustomName", 8)) {
            this.customName = Text.Serializer.fromJson(tag.getString("CustomName"));
        }
        this.baseColor = this.hasWorld() ? ((AbstractBannerBlock)this.getCachedState().getBlock()).getColor() : null;
        this.patternListTag = tag.getList("Patterns", 10);
        this.patterns = null;
        this.patternColors = null;
        this.patternCacheKey = null;
        this.patternListTagRead = true;
    }

    @Override
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 6, this.toInitialChunkDataTag());
    }

    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }

    public static int getPatternCount(ItemStack stack) {
        CompoundTag compoundTag = stack.getSubTag("BlockEntityTag");
        if (compoundTag != null && compoundTag.contains("Patterns")) {
            return compoundTag.getList("Patterns", 10).size();
        }
        return 0;
    }

    @Environment(value=EnvType.CLIENT)
    public List<BannerPattern> getPatterns() {
        this.readPattern();
        return this.patterns;
    }

    @Environment(value=EnvType.CLIENT)
    public List<DyeColor> getPatternColors() {
        this.readPattern();
        return this.patternColors;
    }

    @Environment(value=EnvType.CLIENT)
    public String getPatternCacheKey() {
        this.readPattern();
        return this.patternCacheKey;
    }

    @Environment(value=EnvType.CLIENT)
    private void readPattern() {
        if (this.patterns != null && this.patternColors != null && this.patternCacheKey != null) {
            return;
        }
        if (!this.patternListTagRead) {
            this.patternCacheKey = "";
            return;
        }
        this.patterns = Lists.newArrayList();
        this.patternColors = Lists.newArrayList();
        DyeColor dyeColor = this.getColorForState(this::getCachedState);
        if (dyeColor == null) {
            this.patternCacheKey = "banner_missing";
        } else {
            this.patterns.add(BannerPattern.BASE);
            this.patternColors.add(dyeColor);
            this.patternCacheKey = "b" + dyeColor.getId();
            if (this.patternListTag != null) {
                for (int i = 0; i < this.patternListTag.size(); ++i) {
                    CompoundTag compoundTag = this.patternListTag.getCompound(i);
                    BannerPattern bannerPattern = BannerPattern.byId(compoundTag.getString("Pattern"));
                    if (bannerPattern == null) continue;
                    this.patterns.add(bannerPattern);
                    int j = compoundTag.getInt("Color");
                    this.patternColors.add(DyeColor.byId(j));
                    this.patternCacheKey = this.patternCacheKey + bannerPattern.getId() + j;
                }
            }
        }
    }

    public static void loadFromItemStack(ItemStack stack) {
        CompoundTag compoundTag = stack.getSubTag("BlockEntityTag");
        if (compoundTag == null || !compoundTag.contains("Patterns", 9)) {
            return;
        }
        ListTag listTag = compoundTag.getList("Patterns", 10);
        if (listTag.isEmpty()) {
            return;
        }
        listTag.remove(listTag.size() - 1);
        if (listTag.isEmpty()) {
            stack.removeSubTag("BlockEntityTag");
        }
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockState blockState) {
        ItemStack itemStack = new ItemStack(BannerBlock.getForColor(this.getColorForState(() -> blockState)));
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
