/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.server;

import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.advancement.criterion.BredAnimalsCriterion;
import net.minecraft.advancement.criterion.ConsumeItemCriterion;
import net.minecraft.advancement.criterion.FilledBucketCriterion;
import net.minecraft.advancement.criterion.FishingRodHookedCriterion;
import net.minecraft.advancement.criterion.ItemDurabilityChangedCriterion;
import net.minecraft.advancement.criterion.PlacedBlockCriterion;
import net.minecraft.advancement.criterion.TameAnimalCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class HusbandryTabAdvancementGenerator
implements Consumer<Consumer<Advancement>> {
    private static final EntityType<?>[] field_11296 = new EntityType[]{EntityType.HORSE, EntityType.SHEEP, EntityType.COW, EntityType.MOOSHROOM, EntityType.PIG, EntityType.CHICKEN, EntityType.WOLF, EntityType.OCELOT, EntityType.RABBIT, EntityType.LLAMA, EntityType.TURTLE, EntityType.CAT, EntityType.PANDA, EntityType.FOX};
    private static final Item[] field_11295 = new Item[]{Items.COD, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.SALMON};
    private static final Item[] field_11297 = new Item[]{Items.COD_BUCKET, Items.TROPICAL_FISH_BUCKET, Items.PUFFERFISH_BUCKET, Items.SALMON_BUCKET};
    private static final Item[] field_11298 = new Item[]{Items.APPLE, Items.MUSHROOM_STEW, Items.BREAD, Items.PORKCHOP, Items.COOKED_PORKCHOP, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.COOKED_COD, Items.COOKED_SALMON, Items.COOKIE, Items.MELON_SLICE, Items.BEEF, Items.COOKED_BEEF, Items.CHICKEN, Items.COOKED_CHICKEN, Items.ROTTEN_FLESH, Items.SPIDER_EYE, Items.CARROT, Items.POTATO, Items.BAKED_POTATO, Items.POISONOUS_POTATO, Items.GOLDEN_CARROT, Items.PUMPKIN_PIE, Items.RABBIT, Items.COOKED_RABBIT, Items.RABBIT_STEW, Items.MUTTON, Items.COOKED_MUTTON, Items.CHORUS_FRUIT, Items.BEETROOT, Items.BEETROOT_SOUP, Items.DRIED_KELP, Items.SUSPICIOUS_STEW, Items.SWEET_BERRIES};

    @Override
    public void accept(Consumer<Advancement> consumer) {
        Advancement advancement = Advancement.Task.create().display(Blocks.HAY_BLOCK, (Text)new TranslatableText("advancements.husbandry.root.title", new Object[0]), (Text)new TranslatableText("advancements.husbandry.root.description", new Object[0]), new Identifier("textures/gui/advancements/backgrounds/husbandry.png"), AdvancementFrame.TASK, false, false, false).criterion("consumed_item", ConsumeItemCriterion.Conditions.any()).build(consumer, "husbandry/root");
        Advancement advancement2 = Advancement.Task.create().parent(advancement).display(Items.WHEAT, (Text)new TranslatableText("advancements.husbandry.plant_seed.title", new Object[0]), (Text)new TranslatableText("advancements.husbandry.plant_seed.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criteriaMerger(CriteriaMerger.OR).criterion("wheat", PlacedBlockCriterion.Conditions.block(Blocks.WHEAT)).criterion("pumpkin_stem", PlacedBlockCriterion.Conditions.block(Blocks.PUMPKIN_STEM)).criterion("melon_stem", PlacedBlockCriterion.Conditions.block(Blocks.MELON_STEM)).criterion("beetroots", PlacedBlockCriterion.Conditions.block(Blocks.BEETROOTS)).criterion("nether_wart", PlacedBlockCriterion.Conditions.block(Blocks.NETHER_WART)).build(consumer, "husbandry/plant_seed");
        Advancement advancement3 = Advancement.Task.create().parent(advancement).display(Items.WHEAT, (Text)new TranslatableText("advancements.husbandry.breed_an_animal.title", new Object[0]), (Text)new TranslatableText("advancements.husbandry.breed_an_animal.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criteriaMerger(CriteriaMerger.OR).criterion("bred", BredAnimalsCriterion.Conditions.any()).build(consumer, "husbandry/breed_an_animal");
        Advancement advancement4 = this.method_10341(Advancement.Task.create()).parent(advancement2).display(Items.APPLE, (Text)new TranslatableText("advancements.husbandry.balanced_diet.title", new Object[0]), (Text)new TranslatableText("advancements.husbandry.balanced_diet.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).build(consumer, "husbandry/balanced_diet");
        Advancement advancement5 = Advancement.Task.create().parent(advancement2).display(Items.DIAMOND_HOE, (Text)new TranslatableText("advancements.husbandry.break_diamond_hoe.title", new Object[0]), (Text)new TranslatableText("advancements.husbandry.break_diamond_hoe.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).criterion("broke_hoe", ItemDurabilityChangedCriterion.Conditions.create(ItemPredicate.Builder.create().item(Items.DIAMOND_HOE).build(), NumberRange.IntRange.exactly(0))).build(consumer, "husbandry/break_diamond_hoe");
        Advancement advancement6 = Advancement.Task.create().parent(advancement).display(Items.LEAD, (Text)new TranslatableText("advancements.husbandry.tame_an_animal.title", new Object[0]), (Text)new TranslatableText("advancements.husbandry.tame_an_animal.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("tamed_animal", TameAnimalCriterion.Conditions.any()).build(consumer, "husbandry/tame_an_animal");
        Advancement advancement7 = this.method_10342(Advancement.Task.create()).parent(advancement3).display(Items.GOLDEN_CARROT, (Text)new TranslatableText("advancements.husbandry.breed_all_animals.title", new Object[0]), (Text)new TranslatableText("advancements.husbandry.breed_all_animals.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).build(consumer, "husbandry/bred_all_animals");
        Advancement advancement8 = this.method_10339(Advancement.Task.create()).parent(advancement).criteriaMerger(CriteriaMerger.OR).display(Items.FISHING_ROD, (Text)new TranslatableText("advancements.husbandry.fishy_business.title", new Object[0]), (Text)new TranslatableText("advancements.husbandry.fishy_business.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).build(consumer, "husbandry/fishy_business");
        Advancement advancement9 = this.method_10340(Advancement.Task.create()).parent(advancement8).criteriaMerger(CriteriaMerger.OR).display(Items.PUFFERFISH_BUCKET, (Text)new TranslatableText("advancements.husbandry.tactical_fishing.title", new Object[0]), (Text)new TranslatableText("advancements.husbandry.tactical_fishing.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).build(consumer, "husbandry/tactical_fishing");
        Advancement advancement10 = this.method_16118(Advancement.Task.create()).parent(advancement6).display(Items.COD, (Text)new TranslatableText("advancements.husbandry.complete_catalogue.title", new Object[0]), (Text)new TranslatableText("advancements.husbandry.complete_catalogue.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).build(consumer, "husbandry/complete_catalogue");
    }

    private Advancement.Task method_10341(Advancement.Task task) {
        for (Item item : field_11298) {
            task.criterion(Registry.ITEM.getId(item).getPath(), ConsumeItemCriterion.Conditions.item(item));
        }
        return task;
    }

    private Advancement.Task method_10342(Advancement.Task task) {
        for (EntityType<?> entityType : field_11296) {
            task.criterion(EntityType.getId(entityType).toString(), BredAnimalsCriterion.Conditions.create(EntityPredicate.Builder.create().type(entityType)));
        }
        return task;
    }

    private Advancement.Task method_10340(Advancement.Task task) {
        for (Item item : field_11297) {
            task.criterion(Registry.ITEM.getId(item).getPath(), FilledBucketCriterion.Conditions.create(ItemPredicate.Builder.create().item(item).build()));
        }
        return task;
    }

    private Advancement.Task method_10339(Advancement.Task task) {
        for (Item item : field_11295) {
            task.criterion(Registry.ITEM.getId(item).getPath(), FishingRodHookedCriterion.Conditions.create(ItemPredicate.ANY, EntityPredicate.ANY, ItemPredicate.Builder.create().item(item).build()));
        }
        return task;
    }

    private Advancement.Task method_16118(Advancement.Task task) {
        CatEntity.TEXTURES.forEach((integer, identifier) -> task.criterion(identifier.getPath(), TameAnimalCriterion.Conditions.create(EntityPredicate.Builder.create().type((Identifier)identifier).build())));
        return task;
    }

    @Override
    public /* synthetic */ void accept(Object object) {
        this.accept((Consumer)object);
    }
}

