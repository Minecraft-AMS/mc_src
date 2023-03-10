/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.server;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.ChanneledLightningCriterion;
import net.minecraft.advancement.criterion.ItemUsedOnBlockCriterion;
import net.minecraft.advancement.criterion.KilledByCrossbowCriterion;
import net.minecraft.advancement.criterion.LightningStrikeCriterion;
import net.minecraft.advancement.criterion.LocationArrivalCriterion;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.advancement.criterion.PlayerHurtEntityCriterion;
import net.minecraft.advancement.criterion.ShotCrossbowCriterion;
import net.minecraft.advancement.criterion.SlideDownBlockCriterion;
import net.minecraft.advancement.criterion.SummonedEntityCriterion;
import net.minecraft.advancement.criterion.TargetHitCriterion;
import net.minecraft.advancement.criterion.TravelCriterion;
import net.minecraft.advancement.criterion.UsedTotemCriterion;
import net.minecraft.advancement.criterion.UsingItemCriterion;
import net.minecraft.advancement.criterion.VillagerTradeCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.PlayerPredicate;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LightningBoltPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;

public class AdventureTabAdvancementGenerator
implements Consumer<Consumer<Advancement>> {
    private static final int OVERWORLD_HEIGHT = 384;
    private static final int OVERWORLD_MAX_Y = 320;
    private static final int OVERWORLD_MIN_Y = -64;
    private static final int OVERWORLD_BEDROCK_LAYER_HEIGHT = 5;
    private static final EntityType<?>[] MONSTERS = new EntityType[]{EntityType.BLAZE, EntityType.CAVE_SPIDER, EntityType.CREEPER, EntityType.DROWNED, EntityType.ELDER_GUARDIAN, EntityType.ENDER_DRAGON, EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.EVOKER, EntityType.GHAST, EntityType.GUARDIAN, EntityType.HOGLIN, EntityType.HUSK, EntityType.MAGMA_CUBE, EntityType.PHANTOM, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.SHULKER, EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SLIME, EntityType.SPIDER, EntityType.STRAY, EntityType.VEX, EntityType.VINDICATOR, EntityType.WITCH, EntityType.WITHER_SKELETON, EntityType.WITHER, EntityType.ZOGLIN, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIE, EntityType.ZOMBIFIED_PIGLIN};

    private static LightningStrikeCriterion.Conditions createLightningStrike(NumberRange.IntRange range, EntityPredicate entity) {
        return LightningStrikeCriterion.Conditions.create(EntityPredicate.Builder.create().distance(DistancePredicate.absolute(NumberRange.FloatRange.atMost(30.0))).lightningBolt(LightningBoltPredicate.of(range)).build(), entity);
    }

    private static UsingItemCriterion.Conditions createLookingAtEntityUsing(EntityType<?> entity, Item item) {
        return UsingItemCriterion.Conditions.create(EntityPredicate.Builder.create().player(PlayerPredicate.Builder.create().lookingAt(EntityPredicate.Builder.create().type(entity).build()).build()), ItemPredicate.Builder.create().items(item));
    }

    @Override
    public void accept(Consumer<Advancement> consumer) {
        Advancement advancement = Advancement.Builder.create().display(Items.MAP, (Text)new TranslatableText("advancements.adventure.root.title"), (Text)new TranslatableText("advancements.adventure.root.description"), new Identifier("textures/gui/advancements/backgrounds/adventure.png"), AdvancementFrame.TASK, false, false, false).criteriaMerger(CriterionMerger.OR).criterion("killed_something", OnKilledCriterion.Conditions.createPlayerKilledEntity()).criterion("killed_by_something", OnKilledCriterion.Conditions.createEntityKilledPlayer()).build(consumer, "adventure/root");
        Advancement advancement2 = Advancement.Builder.create().parent(advancement).display(Blocks.RED_BED, (Text)new TranslatableText("advancements.adventure.sleep_in_bed.title"), (Text)new TranslatableText("advancements.adventure.sleep_in_bed.description"), null, AdvancementFrame.TASK, true, true, false).criterion("slept_in_bed", LocationArrivalCriterion.Conditions.createSleptInBed()).build(consumer, "adventure/sleep_in_bed");
        AdventureTabAdvancementGenerator.requireListedBiomesVisited(Advancement.Builder.create(), this.getOverworldBiomes()).parent(advancement2).display(Items.DIAMOND_BOOTS, (Text)new TranslatableText("advancements.adventure.adventuring_time.title"), (Text)new TranslatableText("advancements.adventure.adventuring_time.description"), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(500)).build(consumer, "adventure/adventuring_time");
        Advancement advancement3 = Advancement.Builder.create().parent(advancement).display(Items.EMERALD, (Text)new TranslatableText("advancements.adventure.trade.title"), (Text)new TranslatableText("advancements.adventure.trade.description"), null, AdvancementFrame.TASK, true, true, false).criterion("traded", VillagerTradeCriterion.Conditions.any()).build(consumer, "adventure/trade");
        Advancement.Builder.create().parent(advancement3).display(Items.EMERALD, (Text)new TranslatableText("advancements.adventure.trade_at_world_height.title"), (Text)new TranslatableText("advancements.adventure.trade_at_world_height.description"), null, AdvancementFrame.TASK, true, true, false).criterion("trade_at_world_height", VillagerTradeCriterion.Conditions.create(EntityPredicate.Builder.create().location(LocationPredicate.y(NumberRange.FloatRange.atLeast(319.0))))).build(consumer, "adventure/trade_at_world_height");
        Advancement advancement4 = this.requireListedMobsKilled(Advancement.Builder.create()).parent(advancement).display(Items.IRON_SWORD, (Text)new TranslatableText("advancements.adventure.kill_a_mob.title"), (Text)new TranslatableText("advancements.adventure.kill_a_mob.description"), null, AdvancementFrame.TASK, true, true, false).criteriaMerger(CriterionMerger.OR).build(consumer, "adventure/kill_a_mob");
        this.requireListedMobsKilled(Advancement.Builder.create()).parent(advancement4).display(Items.DIAMOND_SWORD, (Text)new TranslatableText("advancements.adventure.kill_all_mobs.title"), (Text)new TranslatableText("advancements.adventure.kill_all_mobs.description"), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).build(consumer, "adventure/kill_all_mobs");
        Advancement advancement5 = Advancement.Builder.create().parent(advancement4).display(Items.BOW, (Text)new TranslatableText("advancements.adventure.shoot_arrow.title"), (Text)new TranslatableText("advancements.adventure.shoot_arrow.description"), null, AdvancementFrame.TASK, true, true, false).criterion("shot_arrow", PlayerHurtEntityCriterion.Conditions.create(DamagePredicate.Builder.create().type(DamageSourcePredicate.Builder.create().projectile(true).directEntity(EntityPredicate.Builder.create().type(EntityTypeTags.ARROWS))))).build(consumer, "adventure/shoot_arrow");
        Advancement advancement6 = Advancement.Builder.create().parent(advancement4).display(Items.TRIDENT, (Text)new TranslatableText("advancements.adventure.throw_trident.title"), (Text)new TranslatableText("advancements.adventure.throw_trident.description"), null, AdvancementFrame.TASK, true, true, false).criterion("shot_trident", PlayerHurtEntityCriterion.Conditions.create(DamagePredicate.Builder.create().type(DamageSourcePredicate.Builder.create().projectile(true).directEntity(EntityPredicate.Builder.create().type(EntityType.TRIDENT))))).build(consumer, "adventure/throw_trident");
        Advancement.Builder.create().parent(advancement6).display(Items.TRIDENT, (Text)new TranslatableText("advancements.adventure.very_very_frightening.title"), (Text)new TranslatableText("advancements.adventure.very_very_frightening.description"), null, AdvancementFrame.TASK, true, true, false).criterion("struck_villager", ChanneledLightningCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.VILLAGER).build())).build(consumer, "adventure/very_very_frightening");
        Advancement.Builder.create().parent(advancement3).display(Blocks.CARVED_PUMPKIN, (Text)new TranslatableText("advancements.adventure.summon_iron_golem.title"), (Text)new TranslatableText("advancements.adventure.summon_iron_golem.description"), null, AdvancementFrame.GOAL, true, true, false).criterion("summoned_golem", SummonedEntityCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.IRON_GOLEM))).build(consumer, "adventure/summon_iron_golem");
        Advancement.Builder.create().parent(advancement5).display(Items.ARROW, (Text)new TranslatableText("advancements.adventure.sniper_duel.title"), (Text)new TranslatableText("advancements.adventure.sniper_duel.description"), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).criterion("killed_skeleton", OnKilledCriterion.Conditions.createPlayerKilledEntity(EntityPredicate.Builder.create().type(EntityType.SKELETON).distance(DistancePredicate.horizontal(NumberRange.FloatRange.atLeast(50.0))), DamageSourcePredicate.Builder.create().projectile(true))).build(consumer, "adventure/sniper_duel");
        Advancement.Builder.create().parent(advancement4).display(Items.TOTEM_OF_UNDYING, (Text)new TranslatableText("advancements.adventure.totem_of_undying.title"), (Text)new TranslatableText("advancements.adventure.totem_of_undying.description"), null, AdvancementFrame.GOAL, true, true, false).criterion("used_totem", UsedTotemCriterion.Conditions.create(Items.TOTEM_OF_UNDYING)).build(consumer, "adventure/totem_of_undying");
        Advancement advancement7 = Advancement.Builder.create().parent(advancement).display(Items.CROSSBOW, (Text)new TranslatableText("advancements.adventure.ol_betsy.title"), (Text)new TranslatableText("advancements.adventure.ol_betsy.description"), null, AdvancementFrame.TASK, true, true, false).criterion("shot_crossbow", ShotCrossbowCriterion.Conditions.create(Items.CROSSBOW)).build(consumer, "adventure/ol_betsy");
        Advancement.Builder.create().parent(advancement7).display(Items.CROSSBOW, (Text)new TranslatableText("advancements.adventure.whos_the_pillager_now.title"), (Text)new TranslatableText("advancements.adventure.whos_the_pillager_now.description"), null, AdvancementFrame.TASK, true, true, false).criterion("kill_pillager", KilledByCrossbowCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.PILLAGER))).build(consumer, "adventure/whos_the_pillager_now");
        Advancement.Builder.create().parent(advancement7).display(Items.CROSSBOW, (Text)new TranslatableText("advancements.adventure.two_birds_one_arrow.title"), (Text)new TranslatableText("advancements.adventure.two_birds_one_arrow.description"), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(65)).criterion("two_birds", KilledByCrossbowCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.PHANTOM), EntityPredicate.Builder.create().type(EntityType.PHANTOM))).build(consumer, "adventure/two_birds_one_arrow");
        Advancement.Builder.create().parent(advancement7).display(Items.CROSSBOW, (Text)new TranslatableText("advancements.adventure.arbalistic.title"), (Text)new TranslatableText("advancements.adventure.arbalistic.description"), null, AdvancementFrame.CHALLENGE, true, true, true).rewards(AdvancementRewards.Builder.experience(85)).criterion("arbalistic", KilledByCrossbowCriterion.Conditions.create(NumberRange.IntRange.exactly(5))).build(consumer, "adventure/arbalistic");
        Advancement advancement8 = Advancement.Builder.create().parent(advancement).display(Raid.getOminousBanner(), (Text)new TranslatableText("advancements.adventure.voluntary_exile.title"), (Text)new TranslatableText("advancements.adventure.voluntary_exile.description"), null, AdvancementFrame.TASK, true, true, true).criterion("voluntary_exile", OnKilledCriterion.Conditions.createPlayerKilledEntity(EntityPredicate.Builder.create().type(EntityTypeTags.RAIDERS).equipment(EntityEquipmentPredicate.OMINOUS_BANNER_ON_HEAD))).build(consumer, "adventure/voluntary_exile");
        Advancement.Builder.create().parent(advancement8).display(Raid.getOminousBanner(), (Text)new TranslatableText("advancements.adventure.hero_of_the_village.title"), (Text)new TranslatableText("advancements.adventure.hero_of_the_village.description"), null, AdvancementFrame.CHALLENGE, true, true, true).rewards(AdvancementRewards.Builder.experience(100)).criterion("hero_of_the_village", LocationArrivalCriterion.Conditions.createHeroOfTheVillage()).build(consumer, "adventure/hero_of_the_village");
        Advancement.Builder.create().parent(advancement).display(Blocks.HONEY_BLOCK.asItem(), (Text)new TranslatableText("advancements.adventure.honey_block_slide.title"), (Text)new TranslatableText("advancements.adventure.honey_block_slide.description"), null, AdvancementFrame.TASK, true, true, false).criterion("honey_block_slide", SlideDownBlockCriterion.Conditions.create(Blocks.HONEY_BLOCK)).build(consumer, "adventure/honey_block_slide");
        Advancement.Builder.create().parent(advancement5).display(Blocks.TARGET.asItem(), (Text)new TranslatableText("advancements.adventure.bullseye.title"), (Text)new TranslatableText("advancements.adventure.bullseye.description"), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).criterion("bullseye", TargetHitCriterion.Conditions.create(NumberRange.IntRange.exactly(15), EntityPredicate.Extended.ofLegacy(EntityPredicate.Builder.create().distance(DistancePredicate.horizontal(NumberRange.FloatRange.atLeast(30.0))).build()))).build(consumer, "adventure/bullseye");
        Advancement.Builder.create().parent(advancement2).display(Items.LEATHER_BOOTS, (Text)new TranslatableText("advancements.adventure.walk_on_powder_snow_with_leather_boots.title"), (Text)new TranslatableText("advancements.adventure.walk_on_powder_snow_with_leather_boots.description"), null, AdvancementFrame.TASK, true, true, false).criterion("walk_on_powder_snow_with_leather_boots", LocationArrivalCriterion.Conditions.createSteppingOnWithBoots(Blocks.POWDER_SNOW, Items.LEATHER_BOOTS)).build(consumer, "adventure/walk_on_powder_snow_with_leather_boots");
        Advancement.Builder.create().parent(advancement).display(Items.LIGHTNING_ROD, (Text)new TranslatableText("advancements.adventure.lightning_rod_with_villager_no_fire.title"), (Text)new TranslatableText("advancements.adventure.lightning_rod_with_villager_no_fire.description"), null, AdvancementFrame.TASK, true, true, false).criterion("lightning_rod_with_villager_no_fire", AdventureTabAdvancementGenerator.createLightningStrike(NumberRange.IntRange.exactly(0), EntityPredicate.Builder.create().type(EntityType.VILLAGER).build())).build(consumer, "adventure/lightning_rod_with_villager_no_fire");
        Advancement advancement9 = Advancement.Builder.create().parent(advancement).display(Items.SPYGLASS, (Text)new TranslatableText("advancements.adventure.spyglass_at_parrot.title"), (Text)new TranslatableText("advancements.adventure.spyglass_at_parrot.description"), null, AdvancementFrame.TASK, true, true, false).criterion("spyglass_at_parrot", AdventureTabAdvancementGenerator.createLookingAtEntityUsing(EntityType.PARROT, Items.SPYGLASS)).build(consumer, "adventure/spyglass_at_parrot");
        Advancement advancement10 = Advancement.Builder.create().parent(advancement9).display(Items.SPYGLASS, (Text)new TranslatableText("advancements.adventure.spyglass_at_ghast.title"), (Text)new TranslatableText("advancements.adventure.spyglass_at_ghast.description"), null, AdvancementFrame.TASK, true, true, false).criterion("spyglass_at_ghast", AdventureTabAdvancementGenerator.createLookingAtEntityUsing(EntityType.GHAST, Items.SPYGLASS)).build(consumer, "adventure/spyglass_at_ghast");
        Advancement.Builder.create().parent(advancement2).display(Items.JUKEBOX, (Text)new TranslatableText("advancements.adventure.play_jukebox_in_meadows.title"), (Text)new TranslatableText("advancements.adventure.play_jukebox_in_meadows.description"), null, AdvancementFrame.TASK, true, true, false).criterion("play_jukebox_in_meadows", ItemUsedOnBlockCriterion.Conditions.create(LocationPredicate.Builder.create().biome(BiomeKeys.MEADOW).block(BlockPredicate.Builder.create().blocks(Blocks.JUKEBOX).build()), ItemPredicate.Builder.create().tag(ItemTags.MUSIC_DISCS))).build(consumer, "adventure/play_jukebox_in_meadows");
        Advancement.Builder.create().parent(advancement10).display(Items.SPYGLASS, (Text)new TranslatableText("advancements.adventure.spyglass_at_dragon.title"), (Text)new TranslatableText("advancements.adventure.spyglass_at_dragon.description"), null, AdvancementFrame.TASK, true, true, false).criterion("spyglass_at_dragon", AdventureTabAdvancementGenerator.createLookingAtEntityUsing(EntityType.ENDER_DRAGON, Items.SPYGLASS)).build(consumer, "adventure/spyglass_at_dragon");
        Advancement.Builder.create().parent(advancement).display(Items.WATER_BUCKET, (Text)new TranslatableText("advancements.adventure.fall_from_world_height.title"), (Text)new TranslatableText("advancements.adventure.fall_from_world_height.description"), null, AdvancementFrame.TASK, true, true, false).criterion("fall_from_world_height", TravelCriterion.Conditions.fallFromHeight(EntityPredicate.Builder.create().location(LocationPredicate.y(NumberRange.FloatRange.atMost(-59.0))), DistancePredicate.y(NumberRange.FloatRange.atLeast(379.0)), LocationPredicate.y(NumberRange.FloatRange.atLeast(319.0)))).build(consumer, "adventure/fall_from_world_height");
    }

    private List<RegistryKey<Biome>> getOverworldBiomes() {
        return MultiNoiseBiomeSource.Preset.OVERWORLD.getBiomeSource(BuiltinRegistries.BIOME).getBiomes().stream().flatMap(biomeEntry -> biomeEntry.getKey().stream()).collect(Collectors.toList());
    }

    private Advancement.Builder requireListedMobsKilled(Advancement.Builder task) {
        for (EntityType<?> entityType : MONSTERS) {
            task.criterion(Registry.ENTITY_TYPE.getId(entityType).toString(), OnKilledCriterion.Conditions.createPlayerKilledEntity(EntityPredicate.Builder.create().type(entityType)));
        }
        return task;
    }

    protected static Advancement.Builder requireListedBiomesVisited(Advancement.Builder task, List<RegistryKey<Biome>> biomes) {
        for (RegistryKey<Biome> registryKey : biomes) {
            task.criterion(registryKey.getValue().toString(), LocationArrivalCriterion.Conditions.create(LocationPredicate.biome(registryKey)));
        }
        return task;
    }

    @Override
    public /* synthetic */ void accept(Object exporter) {
        this.accept((Consumer)exporter);
    }
}

