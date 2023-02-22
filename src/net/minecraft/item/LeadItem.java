/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class LeadItem
extends Item {
    public LeadItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos;
        World world = context.getWorld();
        Block block = world.getBlockState(blockPos = context.getBlockPos()).getBlock();
        if (block.matches(BlockTags.FENCES)) {
            PlayerEntity playerEntity = context.getPlayer();
            if (!world.isClient && playerEntity != null) {
                LeadItem.attachHeldMobsToBlock(playerEntity, world, blockPos);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public static boolean attachHeldMobsToBlock(PlayerEntity player, World world, BlockPos pos) {
        LeadKnotEntity leadKnotEntity = null;
        boolean bl = false;
        double d = 7.0;
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        List<MobEntity> list = world.getNonSpectatingEntities(MobEntity.class, new Box((double)i - 7.0, (double)j - 7.0, (double)k - 7.0, (double)i + 7.0, (double)j + 7.0, (double)k + 7.0));
        for (MobEntity mobEntity : list) {
            if (mobEntity.getHoldingEntity() != player) continue;
            if (leadKnotEntity == null) {
                leadKnotEntity = LeadKnotEntity.getOrCreate(world, pos);
            }
            mobEntity.attachLeash(leadKnotEntity, true);
            bl = true;
        }
        return bl;
    }
}

