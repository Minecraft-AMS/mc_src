/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 */
package net.minecraft.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SwordItem
extends ToolItem {
    private final float attackDamage;
    private final float attackSpeed;

    public SwordItem(ToolMaterial material, int attackDamage, float attackSpeed, Item.Settings settings) {
        super(material, settings);
        this.attackSpeed = attackSpeed;
        this.attackDamage = (float)attackDamage + material.getAttackDamage();
    }

    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        Block block = state.getBlock();
        if (block == Blocks.COBWEB) {
            return 15.0f;
        }
        Material material = state.getMaterial();
        if (material == Material.PLANT || material == Material.REPLACEABLE_PLANT || material == Material.UNUSED_PLANT || state.matches(BlockTags.LEAVES) || material == Material.PUMPKIN) {
            return 1.5f;
        }
        return 1.0f;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (state.getHardness(world, pos) != 0.0f) {
            stack.damage(2, miner, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public boolean isEffectiveOn(BlockState state) {
        return state.getBlock() == Blocks.COBWEB;
    }

    @Override
    public Multimap<String, EntityAttributeModifier> getModifiers(EquipmentSlot slot) {
        Multimap<String, EntityAttributeModifier> multimap = super.getModifiers(slot);
        if (slot == EquipmentSlot.MAINHAND) {
            multimap.put((Object)EntityAttributes.ATTACK_DAMAGE.getId(), (Object)new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_UUID, "Weapon modifier", (double)this.attackDamage, EntityAttributeModifier.Operation.ADDITION));
            multimap.put((Object)EntityAttributes.ATTACK_SPEED.getId(), (Object)new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_UUID, "Weapon modifier", (double)this.attackSpeed, EntityAttributeModifier.Operation.ADDITION));
        }
        return multimap;
    }
}

