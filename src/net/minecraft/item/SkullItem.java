/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.commons.lang3.StringUtils;

public class SkullItem
extends WallStandingBlockItem {
    public SkullItem(Block standingBlock, Block wallBlock, Item.Settings settings) {
        super(standingBlock, wallBlock, settings);
    }

    @Override
    public Text getName(ItemStack stack) {
        if (stack.getItem() == Items.PLAYER_HEAD && stack.hasTag()) {
            CompoundTag compoundTag2;
            String string = null;
            CompoundTag compoundTag = stack.getTag();
            if (compoundTag.contains("SkullOwner", 8)) {
                string = compoundTag.getString("SkullOwner");
            } else if (compoundTag.contains("SkullOwner", 10) && (compoundTag2 = compoundTag.getCompound("SkullOwner")).contains("Name", 8)) {
                string = compoundTag2.getString("Name");
            }
            if (string != null) {
                return new TranslatableText(this.getTranslationKey() + ".named", string);
            }
        }
        return super.getName(stack);
    }

    @Override
    public boolean postProcessTag(CompoundTag tag) {
        super.postProcessTag(tag);
        if (tag.contains("SkullOwner", 8) && !StringUtils.isBlank((CharSequence)tag.getString("SkullOwner"))) {
            GameProfile gameProfile = new GameProfile(null, tag.getString("SkullOwner"));
            gameProfile = SkullBlockEntity.loadProperties(gameProfile);
            tag.put("SkullOwner", NbtHelper.fromGameProfile(new CompoundTag(), gameProfile));
            return true;
        }
        return false;
    }
}

