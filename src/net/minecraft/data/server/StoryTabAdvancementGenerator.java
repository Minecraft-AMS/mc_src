/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.server;

import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.advancement.criterion.ChangedDimensionCriterion;
import net.minecraft.advancement.criterion.CuredZombieVillagerCriterion;
import net.minecraft.advancement.criterion.EnchantedItemCriterion;
import net.minecraft.advancement.criterion.EntityHurtPlayerCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.LocationArrivalCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.Feature;

public class StoryTabAdvancementGenerator
implements Consumer<Consumer<Advancement>> {
    @Override
    public void accept(Consumer<Advancement> consumer) {
        Advancement advancement = Advancement.Task.create().display(Blocks.GRASS_BLOCK, (Text)new TranslatableText("advancements.story.root.title", new Object[0]), (Text)new TranslatableText("advancements.story.root.description", new Object[0]), new Identifier("textures/gui/advancements/backgrounds/stone.png"), AdvancementFrame.TASK, false, false, false).criterion("crafting_table", InventoryChangedCriterion.Conditions.items(Blocks.CRAFTING_TABLE)).build(consumer, "story/root");
        Advancement advancement2 = Advancement.Task.create().parent(advancement).display(Items.WOODEN_PICKAXE, (Text)new TranslatableText("advancements.story.mine_stone.title", new Object[0]), (Text)new TranslatableText("advancements.story.mine_stone.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("get_stone", InventoryChangedCriterion.Conditions.items(Blocks.COBBLESTONE)).build(consumer, "story/mine_stone");
        Advancement advancement3 = Advancement.Task.create().parent(advancement2).display(Items.STONE_PICKAXE, (Text)new TranslatableText("advancements.story.upgrade_tools.title", new Object[0]), (Text)new TranslatableText("advancements.story.upgrade_tools.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("stone_pickaxe", InventoryChangedCriterion.Conditions.items(Items.STONE_PICKAXE)).build(consumer, "story/upgrade_tools");
        Advancement advancement4 = Advancement.Task.create().parent(advancement3).display(Items.IRON_INGOT, (Text)new TranslatableText("advancements.story.smelt_iron.title", new Object[0]), (Text)new TranslatableText("advancements.story.smelt_iron.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("iron", InventoryChangedCriterion.Conditions.items(Items.IRON_INGOT)).build(consumer, "story/smelt_iron");
        Advancement advancement5 = Advancement.Task.create().parent(advancement4).display(Items.IRON_PICKAXE, (Text)new TranslatableText("advancements.story.iron_tools.title", new Object[0]), (Text)new TranslatableText("advancements.story.iron_tools.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("iron_pickaxe", InventoryChangedCriterion.Conditions.items(Items.IRON_PICKAXE)).build(consumer, "story/iron_tools");
        Advancement advancement6 = Advancement.Task.create().parent(advancement5).display(Items.DIAMOND, (Text)new TranslatableText("advancements.story.mine_diamond.title", new Object[0]), (Text)new TranslatableText("advancements.story.mine_diamond.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("diamond", InventoryChangedCriterion.Conditions.items(Items.DIAMOND)).build(consumer, "story/mine_diamond");
        Advancement advancement7 = Advancement.Task.create().parent(advancement4).display(Items.LAVA_BUCKET, (Text)new TranslatableText("advancements.story.lava_bucket.title", new Object[0]), (Text)new TranslatableText("advancements.story.lava_bucket.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("lava_bucket", InventoryChangedCriterion.Conditions.items(Items.LAVA_BUCKET)).build(consumer, "story/lava_bucket");
        Advancement advancement8 = Advancement.Task.create().parent(advancement4).display(Items.IRON_CHESTPLATE, (Text)new TranslatableText("advancements.story.obtain_armor.title", new Object[0]), (Text)new TranslatableText("advancements.story.obtain_armor.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criteriaMerger(CriteriaMerger.OR).criterion("iron_helmet", InventoryChangedCriterion.Conditions.items(Items.IRON_HELMET)).criterion("iron_chestplate", InventoryChangedCriterion.Conditions.items(Items.IRON_CHESTPLATE)).criterion("iron_leggings", InventoryChangedCriterion.Conditions.items(Items.IRON_LEGGINGS)).criterion("iron_boots", InventoryChangedCriterion.Conditions.items(Items.IRON_BOOTS)).build(consumer, "story/obtain_armor");
        Advancement advancement9 = Advancement.Task.create().parent(advancement6).display(Items.ENCHANTED_BOOK, (Text)new TranslatableText("advancements.story.enchant_item.title", new Object[0]), (Text)new TranslatableText("advancements.story.enchant_item.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("enchanted_item", EnchantedItemCriterion.Conditions.any()).build(consumer, "story/enchant_item");
        Advancement advancement10 = Advancement.Task.create().parent(advancement7).display(Blocks.OBSIDIAN, (Text)new TranslatableText("advancements.story.form_obsidian.title", new Object[0]), (Text)new TranslatableText("advancements.story.form_obsidian.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("obsidian", InventoryChangedCriterion.Conditions.items(Blocks.OBSIDIAN)).build(consumer, "story/form_obsidian");
        Advancement advancement11 = Advancement.Task.create().parent(advancement8).display(Items.SHIELD, (Text)new TranslatableText("advancements.story.deflect_arrow.title", new Object[0]), (Text)new TranslatableText("advancements.story.deflect_arrow.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("deflected_projectile", EntityHurtPlayerCriterion.Conditions.create(DamagePredicate.Builder.create().type(DamageSourcePredicate.Builder.create().projectile(true)).blocked(true))).build(consumer, "story/deflect_arrow");
        Advancement advancement12 = Advancement.Task.create().parent(advancement6).display(Items.DIAMOND_CHESTPLATE, (Text)new TranslatableText("advancements.story.shiny_gear.title", new Object[0]), (Text)new TranslatableText("advancements.story.shiny_gear.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criteriaMerger(CriteriaMerger.OR).criterion("diamond_helmet", InventoryChangedCriterion.Conditions.items(Items.DIAMOND_HELMET)).criterion("diamond_chestplate", InventoryChangedCriterion.Conditions.items(Items.DIAMOND_CHESTPLATE)).criterion("diamond_leggings", InventoryChangedCriterion.Conditions.items(Items.DIAMOND_LEGGINGS)).criterion("diamond_boots", InventoryChangedCriterion.Conditions.items(Items.DIAMOND_BOOTS)).build(consumer, "story/shiny_gear");
        Advancement advancement13 = Advancement.Task.create().parent(advancement10).display(Items.FLINT_AND_STEEL, (Text)new TranslatableText("advancements.story.enter_the_nether.title", new Object[0]), (Text)new TranslatableText("advancements.story.enter_the_nether.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("entered_nether", ChangedDimensionCriterion.Conditions.to(DimensionType.THE_NETHER)).build(consumer, "story/enter_the_nether");
        Advancement advancement14 = Advancement.Task.create().parent(advancement13).display(Items.GOLDEN_APPLE, (Text)new TranslatableText("advancements.story.cure_zombie_villager.title", new Object[0]), (Text)new TranslatableText("advancements.story.cure_zombie_villager.description", new Object[0]), null, AdvancementFrame.GOAL, true, true, false).criterion("cured_zombie", CuredZombieVillagerCriterion.Conditions.any()).build(consumer, "story/cure_zombie_villager");
        Advancement advancement15 = Advancement.Task.create().parent(advancement13).display(Items.ENDER_EYE, (Text)new TranslatableText("advancements.story.follow_ender_eye.title", new Object[0]), (Text)new TranslatableText("advancements.story.follow_ender_eye.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("in_stronghold", LocationArrivalCriterion.Conditions.create(LocationPredicate.feature(Feature.STRONGHOLD))).build(consumer, "story/follow_ender_eye");
        Advancement advancement16 = Advancement.Task.create().parent(advancement15).display(Blocks.END_STONE, (Text)new TranslatableText("advancements.story.enter_the_end.title", new Object[0]), (Text)new TranslatableText("advancements.story.enter_the_end.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("entered_end", ChangedDimensionCriterion.Conditions.to(DimensionType.THE_END)).build(consumer, "story/enter_the_end");
    }

    @Override
    public /* synthetic */ void accept(Object object) {
        this.accept((Consumer)object);
    }
}
