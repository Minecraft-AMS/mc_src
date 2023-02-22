/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.server;

import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.advancement.criterion.ChanneledLightningCriterion;
import net.minecraft.advancement.criterion.KilledByCrossbowCriterion;
import net.minecraft.advancement.criterion.LocationArrivalCriterion;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.advancement.criterion.PlayerHurtEntityCriterion;
import net.minecraft.advancement.criterion.ShotCrossbowCriterion;
import net.minecraft.advancement.criterion.SummonedEntityCriterion;
import net.minecraft.advancement.criterion.UsedTotemCriterion;
import net.minecraft.advancement.criterion.VillagerTradeCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.raid.Raid;
import net.minecraft.item.Items;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class AdventureTabAdvancementGenerator
implements Consumer<Consumer<Advancement>> {
    private static final Biome[] BIOMES = new Biome[]{Biomes.BIRCH_FOREST_HILLS, Biomes.RIVER, Biomes.SWAMP, Biomes.DESERT, Biomes.WOODED_HILLS, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.SNOWY_TAIGA, Biomes.BADLANDS, Biomes.FOREST, Biomes.STONE_SHORE, Biomes.SNOWY_TUNDRA, Biomes.TAIGA_HILLS, Biomes.SNOWY_MOUNTAINS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.SAVANNA, Biomes.PLAINS, Biomes.FROZEN_RIVER, Biomes.GIANT_TREE_TAIGA, Biomes.SNOWY_BEACH, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.MUSHROOM_FIELD_SHORE, Biomes.MOUNTAINS, Biomes.DESERT_HILLS, Biomes.JUNGLE, Biomes.BEACH, Biomes.SAVANNA_PLATEAU, Biomes.SNOWY_TAIGA_HILLS, Biomes.BADLANDS_PLATEAU, Biomes.DARK_FOREST, Biomes.TAIGA, Biomes.BIRCH_FOREST, Biomes.MUSHROOM_FIELDS, Biomes.WOODED_MOUNTAINS, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.BAMBOO_JUNGLE, Biomes.BAMBOO_JUNGLE_HILLS};
    private static final EntityType<?>[] MONSTERS = new EntityType[]{EntityType.CAVE_SPIDER, EntityType.SPIDER, EntityType.ZOMBIE_PIGMAN, EntityType.ENDERMAN, EntityType.BLAZE, EntityType.CREEPER, EntityType.EVOKER, EntityType.GHAST, EntityType.GUARDIAN, EntityType.HUSK, EntityType.MAGMA_CUBE, EntityType.SHULKER, EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SLIME, EntityType.STRAY, EntityType.VINDICATOR, EntityType.WITCH, EntityType.WITHER_SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.PHANTOM, EntityType.DROWNED, EntityType.PILLAGER, EntityType.RAVAGER};

    @Override
    public void accept(Consumer<Advancement> consumer) {
        Advancement advancement = Advancement.Task.create().display(Items.MAP, (Text)new TranslatableText("advancements.adventure.root.title", new Object[0]), (Text)new TranslatableText("advancements.adventure.root.description", new Object[0]), new Identifier("textures/gui/advancements/backgrounds/adventure.png"), AdvancementFrame.TASK, false, false, false).criteriaMerger(CriteriaMerger.OR).criterion("killed_something", OnKilledCriterion.Conditions.createPlayerKilledEntity()).criterion("killed_by_something", OnKilledCriterion.Conditions.createEntityKilledPlayer()).build(consumer, "adventure/root");
        Advancement advancement2 = Advancement.Task.create().parent(advancement).display(Blocks.RED_BED, (Text)new TranslatableText("advancements.adventure.sleep_in_bed.title", new Object[0]), (Text)new TranslatableText("advancements.adventure.sleep_in_bed.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("slept_in_bed", LocationArrivalCriterion.Conditions.createSleptInBed()).build(consumer, "adventure/sleep_in_bed");
        Advancement advancement3 = this.method_10337(Advancement.Task.create()).parent(advancement2).display(Items.DIAMOND_BOOTS, (Text)new TranslatableText("advancements.adventure.adventuring_time.title", new Object[0]), (Text)new TranslatableText("advancements.adventure.adventuring_time.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(500)).build(consumer, "adventure/adventuring_time");
        Advancement advancement4 = Advancement.Task.create().parent(advancement).display(Items.EMERALD, (Text)new TranslatableText("advancements.adventure.trade.title", new Object[0]), (Text)new TranslatableText("advancements.adventure.trade.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("traded", VillagerTradeCriterion.Conditions.any()).build(consumer, "adventure/trade");
        Advancement advancement5 = this.method_10336(Advancement.Task.create()).parent(advancement).display(Items.IRON_SWORD, (Text)new TranslatableText("advancements.adventure.kill_a_mob.title", new Object[0]), (Text)new TranslatableText("advancements.adventure.kill_a_mob.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criteriaMerger(CriteriaMerger.OR).build(consumer, "adventure/kill_a_mob");
        Advancement advancement6 = this.method_10336(Advancement.Task.create()).parent(advancement5).display(Items.DIAMOND_SWORD, (Text)new TranslatableText("advancements.adventure.kill_all_mobs.title", new Object[0]), (Text)new TranslatableText("advancements.adventure.kill_all_mobs.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).build(consumer, "adventure/kill_all_mobs");
        Advancement advancement7 = Advancement.Task.create().parent(advancement5).display(Items.BOW, (Text)new TranslatableText("advancements.adventure.shoot_arrow.title", new Object[0]), (Text)new TranslatableText("advancements.adventure.shoot_arrow.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("shot_arrow", PlayerHurtEntityCriterion.Conditions.create(DamagePredicate.Builder.create().type(DamageSourcePredicate.Builder.create().projectile(true).directEntity(EntityPredicate.Builder.create().type(EntityType.ARROW))))).build(consumer, "adventure/shoot_arrow");
        Advancement advancement8 = Advancement.Task.create().parent(advancement5).display(Items.TRIDENT, (Text)new TranslatableText("advancements.adventure.throw_trident.title", new Object[0]), (Text)new TranslatableText("advancements.adventure.throw_trident.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("shot_trident", PlayerHurtEntityCriterion.Conditions.create(DamagePredicate.Builder.create().type(DamageSourcePredicate.Builder.create().projectile(true).directEntity(EntityPredicate.Builder.create().type(EntityType.TRIDENT))))).build(consumer, "adventure/throw_trident");
        Advancement advancement9 = Advancement.Task.create().parent(advancement8).display(Items.TRIDENT, (Text)new TranslatableText("advancements.adventure.very_very_frightening.title", new Object[0]), (Text)new TranslatableText("advancements.adventure.very_very_frightening.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("struck_villager", ChanneledLightningCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.VILLAGER).build())).build(consumer, "adventure/very_very_frightening");
        Advancement advancement10 = Advancement.Task.create().parent(advancement4).display(Blocks.CARVED_PUMPKIN, (Text)new TranslatableText("advancements.adventure.summon_iron_golem.title", new Object[0]), (Text)new TranslatableText("advancements.adventure.summon_iron_golem.description", new Object[0]), null, AdvancementFrame.GOAL, true, true, false).criterion("summoned_golem", SummonedEntityCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.IRON_GOLEM))).build(consumer, "adventure/summon_iron_golem");
        Advancement advancement11 = Advancement.Task.create().parent(advancement7).display(Items.ARROW, (Text)new TranslatableText("advancements.adventure.sniper_duel.title", new Object[0]), (Text)new TranslatableText("advancements.adventure.sniper_duel.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).criterion("killed_skeleton", OnKilledCriterion.Conditions.createPlayerKilledEntity(EntityPredicate.Builder.create().type(EntityType.SKELETON).distance(DistancePredicate.horizontal(NumberRange.FloatRange.atLeast(50.0f))), DamageSourcePredicate.Builder.create().projectile(true))).build(consumer, "adventure/sniper_duel");
        Advancement advancement12 = Advancement.Task.create().parent(advancement5).display(Items.TOTEM_OF_UNDYING, (Text)new TranslatableText("advancements.adventure.totem_of_undying.title", new Object[0]), (Text)new TranslatableText("advancements.adventure.totem_of_undying.description", new Object[0]), null, AdvancementFrame.GOAL, true, true, false).criterion("used_totem", UsedTotemCriterion.Conditions.create(Items.TOTEM_OF_UNDYING)).build(consumer, "adventure/totem_of_undying");
        Advancement advancement13 = Advancement.Task.create().parent(advancement).display(Items.CROSSBOW, (Text)new TranslatableText("advancements.adventure.ol_betsy.title", new Object[0]), (Text)new TranslatableText("advancements.adventure.ol_betsy.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("shot_crossbow", ShotCrossbowCriterion.Conditions.create(Items.CROSSBOW)).build(consumer, "adventure/ol_betsy");
        Advancement advancement14 = Advancement.Task.create().parent(advancement13).display(Items.CROSSBOW, (Text)new TranslatableText("advancements.adventure.whos_the_pillager_now.title", new Object[0]), (Text)new TranslatableText("advancements.adventure.whos_the_pillager_now.description", new Object[0]), null, AdvancementFrame.TASK, true, true, false).criterion("kill_pillager", KilledByCrossbowCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.PILLAGER))).build(consumer, "adventure/whos_the_pillager_now");
        Advancement advancement15 = Advancement.Task.create().parent(advancement13).display(Items.CROSSBOW, (Text)new TranslatableText("advancements.adventure.two_birds_one_arrow.title", new Object[0]), (Text)new TranslatableText("advancements.adventure.two_birds_one_arrow.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(65)).criterion("two_birds", KilledByCrossbowCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.PHANTOM), EntityPredicate.Builder.create().type(EntityType.PHANTOM))).build(consumer, "adventure/two_birds_one_arrow");
        Advancement advancement16 = Advancement.Task.create().parent(advancement13).display(Items.CROSSBOW, (Text)new TranslatableText("advancements.adventure.arbalistic.title", new Object[0]), (Text)new TranslatableText("advancements.adventure.arbalistic.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, true).rewards(AdvancementRewards.Builder.experience(85)).criterion("arbalistic", KilledByCrossbowCriterion.Conditions.create(NumberRange.IntRange.exactly(5))).build(consumer, "adventure/arbalistic");
        Advancement advancement17 = Advancement.Task.create().parent(advancement).display(Raid.getOminousBanner(), (Text)new TranslatableText("advancements.adventure.voluntary_exile.title", new Object[0]), (Text)new TranslatableText("advancements.adventure.voluntary_exile.description", new Object[0]), null, AdvancementFrame.TASK, true, true, true).criterion("voluntary_exile", OnKilledCriterion.Conditions.createPlayerKilledEntity(EntityPredicate.Builder.create().type(EntityTypeTags.RAIDERS).equipment(EntityEquipmentPredicate.field_19240))).build(consumer, "adventure/voluntary_exile");
        Advancement advancement18 = Advancement.Task.create().parent(advancement17).display(Raid.getOminousBanner(), (Text)new TranslatableText("advancements.adventure.hero_of_the_village.title", new Object[0]), (Text)new TranslatableText("advancements.adventure.hero_of_the_village.description", new Object[0]), null, AdvancementFrame.CHALLENGE, true, true, true).rewards(AdvancementRewards.Builder.experience(100)).criterion("hero_of_the_village", LocationArrivalCriterion.Conditions.createHeroOfTheVillage()).build(consumer, "adventure/hero_of_the_village");
    }

    private Advancement.Task method_10336(Advancement.Task task) {
        for (EntityType<?> entityType : MONSTERS) {
            task.criterion(Registry.ENTITY_TYPE.getId(entityType).toString(), OnKilledCriterion.Conditions.createPlayerKilledEntity(EntityPredicate.Builder.create().type(entityType)));
        }
        return task;
    }

    private Advancement.Task method_10337(Advancement.Task task) {
        for (Biome biome : BIOMES) {
            task.criterion(Registry.BIOME.getId(biome).toString(), LocationArrivalCriterion.Conditions.create(LocationPredicate.biome(biome)));
        }
        return task;
    }

    @Override
    public /* synthetic */ void accept(Object object) {
        this.accept((Consumer)object);
    }
}
