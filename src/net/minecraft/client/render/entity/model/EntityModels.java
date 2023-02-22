/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BedBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BellBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ConduitBlockEntityRenderer;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.WitherSkullEntityRenderer;
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer;
import net.minecraft.client.render.entity.model.AllayEntityModel;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.client.render.entity.model.AxolotlEntityModel;
import net.minecraft.client.render.entity.model.BatEntityModel;
import net.minecraft.client.render.entity.model.BeeEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.BlazeEntityModel;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.client.render.entity.model.CodEntityModel;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.client.render.entity.model.DonkeyEntityModel;
import net.minecraft.client.render.entity.model.DragonHeadEntityModel;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.render.entity.model.EndermiteEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EvokerFangsEntityModel;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.client.render.entity.model.FrogEntityModel;
import net.minecraft.client.render.entity.model.GhastEntityModel;
import net.minecraft.client.render.entity.model.GoatEntityModel;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
import net.minecraft.client.render.entity.model.HoglinEntityModel;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.render.entity.model.LargePufferfishEntityModel;
import net.minecraft.client.render.entity.model.LargeTropicalFishEntityModel;
import net.minecraft.client.render.entity.model.LeashKnotEntityModel;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.client.render.entity.model.LlamaSpitEntityModel;
import net.minecraft.client.render.entity.model.MagmaCubeEntityModel;
import net.minecraft.client.render.entity.model.MediumPufferfishEntityModel;
import net.minecraft.client.render.entity.model.MinecartEntityModel;
import net.minecraft.client.render.entity.model.OcelotEntityModel;
import net.minecraft.client.render.entity.model.PandaEntityModel;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.render.entity.model.PhantomEntityModel;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.model.PolarBearEntityModel;
import net.minecraft.client.render.entity.model.RabbitEntityModel;
import net.minecraft.client.render.entity.model.RavagerEntityModel;
import net.minecraft.client.render.entity.model.SalmonEntityModel;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.render.entity.model.SheepWoolEntityModel;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.entity.model.ShulkerBulletEntityModel;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.render.entity.model.SilverfishEntityModel;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.client.render.entity.model.SmallPufferfishEntityModel;
import net.minecraft.client.render.entity.model.SmallTropicalFishEntityModel;
import net.minecraft.client.render.entity.model.SnowGolemEntityModel;
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.client.render.entity.model.StriderEntityModel;
import net.minecraft.client.render.entity.model.TadpoleEntityModel;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.entity.model.TurtleEntityModel;
import net.minecraft.client.render.entity.model.VexEntityModel;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.render.entity.model.WardenEntityModel;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.SignType;

@Environment(value=EnvType.CLIENT)
public class EntityModels {
    private static final Dilation FISH_PATTERN_DILATION = new Dilation(0.008f);
    private static final Dilation ARMOR_DILATION = new Dilation(1.0f);
    private static final Dilation HAT_DILATION = new Dilation(0.5f);

    public static Map<EntityModelLayer, TexturedModelData> getModels() {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        TexturedModelData texturedModelData = TexturedModelData.of(BipedEntityModel.getModelData(Dilation.NONE, 0.0f), 64, 64);
        TexturedModelData texturedModelData2 = TexturedModelData.of(BipedEntityModel.getModelData(ARMOR_DILATION, 0.0f), 64, 32);
        TexturedModelData texturedModelData3 = TexturedModelData.of(BipedEntityModel.getModelData(new Dilation(1.02f), 0.0f), 64, 32);
        TexturedModelData texturedModelData4 = TexturedModelData.of(BipedEntityModel.getModelData(HAT_DILATION, 0.0f), 64, 32);
        TexturedModelData texturedModelData5 = MinecartEntityModel.getTexturedModelData();
        TexturedModelData texturedModelData6 = SkullEntityModel.getSkullTexturedModelData();
        TexturedModelData texturedModelData7 = TexturedModelData.of(HorseEntityModel.getModelData(Dilation.NONE), 64, 64);
        TexturedModelData texturedModelData8 = IllagerEntityModel.getTexturedModelData();
        TexturedModelData texturedModelData9 = CowEntityModel.getTexturedModelData();
        TexturedModelData texturedModelData10 = TexturedModelData.of(OcelotEntityModel.getModelData(Dilation.NONE), 64, 32);
        TexturedModelData texturedModelData11 = TexturedModelData.of(PiglinEntityModel.getModelData(Dilation.NONE), 64, 64);
        TexturedModelData texturedModelData12 = SkullEntityModel.getHeadTexturedModelData();
        TexturedModelData texturedModelData13 = LlamaEntityModel.getTexturedModelData(Dilation.NONE);
        TexturedModelData texturedModelData14 = StriderEntityModel.getTexturedModelData();
        TexturedModelData texturedModelData15 = HoglinEntityModel.getTexturedModelData();
        TexturedModelData texturedModelData16 = SkeletonEntityModel.getTexturedModelData();
        TexturedModelData texturedModelData17 = TexturedModelData.of(VillagerResemblingModel.getModelData(), 64, 64);
        TexturedModelData texturedModelData18 = SpiderEntityModel.getTexturedModelData();
        builder.put((Object)EntityModelLayers.ALLAY, (Object)AllayEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.ARMOR_STAND, (Object)ArmorStandEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.ARMOR_STAND_INNER_ARMOR, (Object)ArmorStandArmorEntityModel.getTexturedModelData(HAT_DILATION));
        builder.put((Object)EntityModelLayers.ARMOR_STAND_OUTER_ARMOR, (Object)ArmorStandArmorEntityModel.getTexturedModelData(ARMOR_DILATION));
        builder.put((Object)EntityModelLayers.AXOLOTL, (Object)AxolotlEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.BANNER, (Object)BannerBlockEntityRenderer.getTexturedModelData());
        builder.put((Object)EntityModelLayers.BAT, (Object)BatEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.BED_FOOT, (Object)BedBlockEntityRenderer.getFootTexturedModelData());
        builder.put((Object)EntityModelLayers.BED_HEAD, (Object)BedBlockEntityRenderer.getHeadTexturedModelData());
        builder.put((Object)EntityModelLayers.BEE, (Object)BeeEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.BELL, (Object)BellBlockEntityRenderer.getTexturedModelData());
        builder.put((Object)EntityModelLayers.BLAZE, (Object)BlazeEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.BOOK, (Object)BookModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.CAT, (Object)texturedModelData10);
        builder.put((Object)EntityModelLayers.CAT_COLLAR, (Object)TexturedModelData.of(OcelotEntityModel.getModelData(new Dilation(0.01f)), 64, 32));
        builder.put((Object)EntityModelLayers.CAVE_SPIDER, (Object)texturedModelData18);
        builder.put((Object)EntityModelLayers.CHEST, (Object)ChestBlockEntityRenderer.getSingleTexturedModelData());
        builder.put((Object)EntityModelLayers.DOUBLE_CHEST_LEFT, (Object)ChestBlockEntityRenderer.getLeftDoubleTexturedModelData());
        builder.put((Object)EntityModelLayers.DOUBLE_CHEST_RIGHT, (Object)ChestBlockEntityRenderer.getRightDoubleTexturedModelData());
        builder.put((Object)EntityModelLayers.CHEST_MINECART, (Object)texturedModelData5);
        builder.put((Object)EntityModelLayers.CHICKEN, (Object)ChickenEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.COD, (Object)CodEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.COMMAND_BLOCK_MINECART, (Object)texturedModelData5);
        builder.put((Object)EntityModelLayers.CONDUIT_EYE, (Object)ConduitBlockEntityRenderer.getEyeTexturedModelData());
        builder.put((Object)EntityModelLayers.CONDUIT_WIND, (Object)ConduitBlockEntityRenderer.getWindTexturedModelData());
        builder.put((Object)EntityModelLayers.CONDUIT_SHELL, (Object)ConduitBlockEntityRenderer.getShellTexturedModelData());
        builder.put((Object)EntityModelLayers.CONDUIT, (Object)ConduitBlockEntityRenderer.getPlainTexturedModelData());
        builder.put((Object)EntityModelLayers.COW, (Object)texturedModelData9);
        builder.put((Object)EntityModelLayers.CREEPER, (Object)CreeperEntityModel.getTexturedModelData(Dilation.NONE));
        builder.put((Object)EntityModelLayers.CREEPER_ARMOR, (Object)CreeperEntityModel.getTexturedModelData(new Dilation(2.0f)));
        builder.put((Object)EntityModelLayers.CREEPER_HEAD, (Object)texturedModelData6);
        builder.put((Object)EntityModelLayers.DOLPHIN, (Object)DolphinEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.DONKEY, (Object)DonkeyEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.DRAGON_SKULL, (Object)DragonHeadEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.DROWNED, (Object)DrownedEntityModel.getTexturedModelData(Dilation.NONE));
        builder.put((Object)EntityModelLayers.DROWNED_INNER_ARMOR, (Object)texturedModelData4);
        builder.put((Object)EntityModelLayers.DROWNED_OUTER_ARMOR, (Object)texturedModelData4);
        builder.put((Object)EntityModelLayers.DROWNED_OUTER, (Object)DrownedEntityModel.getTexturedModelData(new Dilation(0.25f)));
        builder.put((Object)EntityModelLayers.ELDER_GUARDIAN, (Object)GuardianEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.ELYTRA, (Object)ElytraEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.ENDERMAN, (Object)EndermanEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.ENDERMITE, (Object)EndermiteEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.ENDER_DRAGON, (Object)EnderDragonEntityRenderer.getTexturedModelData());
        builder.put((Object)EntityModelLayers.END_CRYSTAL, (Object)EndCrystalEntityRenderer.getTexturedModelData());
        builder.put((Object)EntityModelLayers.EVOKER, (Object)texturedModelData8);
        builder.put((Object)EntityModelLayers.EVOKER_FANGS, (Object)EvokerFangsEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.FOX, (Object)FoxEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.FROG, (Object)FrogEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.FURNACE_MINECART, (Object)texturedModelData5);
        builder.put((Object)EntityModelLayers.GHAST, (Object)GhastEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.GIANT, (Object)texturedModelData);
        builder.put((Object)EntityModelLayers.GIANT_INNER_ARMOR, (Object)texturedModelData4);
        builder.put((Object)EntityModelLayers.GIANT_OUTER_ARMOR, (Object)texturedModelData2);
        builder.put((Object)EntityModelLayers.GLOW_SQUID, (Object)SquidEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.GOAT, (Object)GoatEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.GUARDIAN, (Object)GuardianEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.HOGLIN, (Object)texturedModelData15);
        builder.put((Object)EntityModelLayers.HOPPER_MINECART, (Object)texturedModelData5);
        builder.put((Object)EntityModelLayers.HORSE, (Object)texturedModelData7);
        builder.put((Object)EntityModelLayers.HORSE_ARMOR, (Object)TexturedModelData.of(HorseEntityModel.getModelData(new Dilation(0.1f)), 64, 64));
        builder.put((Object)EntityModelLayers.HUSK, (Object)texturedModelData);
        builder.put((Object)EntityModelLayers.HUSK_INNER_ARMOR, (Object)texturedModelData4);
        builder.put((Object)EntityModelLayers.HUSK_OUTER_ARMOR, (Object)texturedModelData2);
        builder.put((Object)EntityModelLayers.ILLUSIONER, (Object)texturedModelData8);
        builder.put((Object)EntityModelLayers.IRON_GOLEM, (Object)IronGolemEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.LEASH_KNOT, (Object)LeashKnotEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.LLAMA, (Object)texturedModelData13);
        builder.put((Object)EntityModelLayers.LLAMA_DECOR, (Object)LlamaEntityModel.getTexturedModelData(new Dilation(0.5f)));
        builder.put((Object)EntityModelLayers.LLAMA_SPIT, (Object)LlamaSpitEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.MAGMA_CUBE, (Object)MagmaCubeEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.MINECART, (Object)texturedModelData5);
        builder.put((Object)EntityModelLayers.MOOSHROOM, (Object)texturedModelData9);
        builder.put((Object)EntityModelLayers.MULE, (Object)DonkeyEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.OCELOT, (Object)texturedModelData10);
        builder.put((Object)EntityModelLayers.PANDA, (Object)PandaEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.PARROT, (Object)ParrotEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.PHANTOM, (Object)PhantomEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.PIG, (Object)PigEntityModel.getTexturedModelData(Dilation.NONE));
        builder.put((Object)EntityModelLayers.PIG_SADDLE, (Object)PigEntityModel.getTexturedModelData(new Dilation(0.5f)));
        builder.put((Object)EntityModelLayers.PIGLIN, (Object)texturedModelData11);
        builder.put((Object)EntityModelLayers.PIGLIN_INNER_ARMOR, (Object)texturedModelData4);
        builder.put((Object)EntityModelLayers.PIGLIN_OUTER_ARMOR, (Object)texturedModelData3);
        builder.put((Object)EntityModelLayers.PIGLIN_BRUTE, (Object)texturedModelData11);
        builder.put((Object)EntityModelLayers.PIGLIN_BRUTE_INNER_ARMOR, (Object)texturedModelData4);
        builder.put((Object)EntityModelLayers.PIGLIN_BRUTE_OUTER_ARMOR, (Object)texturedModelData3);
        builder.put((Object)EntityModelLayers.PILLAGER, (Object)texturedModelData8);
        builder.put((Object)EntityModelLayers.PLAYER, (Object)TexturedModelData.of(PlayerEntityModel.getTexturedModelData(Dilation.NONE, false), 64, 64));
        builder.put((Object)EntityModelLayers.PLAYER_HEAD, (Object)texturedModelData12);
        builder.put((Object)EntityModelLayers.PLAYER_INNER_ARMOR, (Object)texturedModelData4);
        builder.put((Object)EntityModelLayers.PLAYER_OUTER_ARMOR, (Object)texturedModelData2);
        builder.put((Object)EntityModelLayers.PLAYER_SLIM, (Object)TexturedModelData.of(PlayerEntityModel.getTexturedModelData(Dilation.NONE, true), 64, 64));
        builder.put((Object)EntityModelLayers.PLAYER_SLIM_INNER_ARMOR, (Object)texturedModelData4);
        builder.put((Object)EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR, (Object)texturedModelData2);
        builder.put((Object)EntityModelLayers.SPIN_ATTACK, (Object)TridentRiptideFeatureRenderer.getTexturedModelData());
        builder.put((Object)EntityModelLayers.POLAR_BEAR, (Object)PolarBearEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.PUFFERFISH_BIG, (Object)LargePufferfishEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.PUFFERFISH_MEDIUM, (Object)MediumPufferfishEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.PUFFERFISH_SMALL, (Object)SmallPufferfishEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.RABBIT, (Object)RabbitEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.RAVAGER, (Object)RavagerEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.SALMON, (Object)SalmonEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.SHEEP, (Object)SheepEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.SHEEP_FUR, (Object)SheepWoolEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.SHIELD, (Object)ShieldEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.SHULKER, (Object)ShulkerEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.SHULKER_BULLET, (Object)ShulkerBulletEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.SILVERFISH, (Object)SilverfishEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.SKELETON, (Object)texturedModelData16);
        builder.put((Object)EntityModelLayers.SKELETON_INNER_ARMOR, (Object)texturedModelData4);
        builder.put((Object)EntityModelLayers.SKELETON_OUTER_ARMOR, (Object)texturedModelData2);
        builder.put((Object)EntityModelLayers.SKELETON_HORSE, (Object)texturedModelData7);
        builder.put((Object)EntityModelLayers.SKELETON_SKULL, (Object)texturedModelData6);
        builder.put((Object)EntityModelLayers.SLIME, (Object)SlimeEntityModel.getInnerTexturedModelData());
        builder.put((Object)EntityModelLayers.SLIME_OUTER, (Object)SlimeEntityModel.getOuterTexturedModelData());
        builder.put((Object)EntityModelLayers.SNOW_GOLEM, (Object)SnowGolemEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.SPAWNER_MINECART, (Object)texturedModelData5);
        builder.put((Object)EntityModelLayers.SPIDER, (Object)texturedModelData18);
        builder.put((Object)EntityModelLayers.SQUID, (Object)SquidEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.STRAY, (Object)texturedModelData16);
        builder.put((Object)EntityModelLayers.STRAY_INNER_ARMOR, (Object)texturedModelData4);
        builder.put((Object)EntityModelLayers.STRAY_OUTER_ARMOR, (Object)texturedModelData2);
        builder.put((Object)EntityModelLayers.STRAY_OUTER, (Object)TexturedModelData.of(BipedEntityModel.getModelData(new Dilation(0.25f), 0.0f), 64, 32));
        builder.put((Object)EntityModelLayers.STRIDER, (Object)texturedModelData14);
        builder.put((Object)EntityModelLayers.STRIDER_SADDLE, (Object)texturedModelData14);
        builder.put((Object)EntityModelLayers.TADPOLE, (Object)TadpoleEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.TNT_MINECART, (Object)texturedModelData5);
        builder.put((Object)EntityModelLayers.TRADER_LLAMA, (Object)texturedModelData13);
        builder.put((Object)EntityModelLayers.TRIDENT, (Object)TridentEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.TROPICAL_FISH_LARGE, (Object)LargeTropicalFishEntityModel.getTexturedModelData(Dilation.NONE));
        builder.put((Object)EntityModelLayers.TROPICAL_FISH_LARGE_PATTERN, (Object)LargeTropicalFishEntityModel.getTexturedModelData(FISH_PATTERN_DILATION));
        builder.put((Object)EntityModelLayers.TROPICAL_FISH_SMALL, (Object)SmallTropicalFishEntityModel.getTexturedModelData(Dilation.NONE));
        builder.put((Object)EntityModelLayers.TROPICAL_FISH_SMALL_PATTERN, (Object)SmallTropicalFishEntityModel.getTexturedModelData(FISH_PATTERN_DILATION));
        builder.put((Object)EntityModelLayers.TURTLE, (Object)TurtleEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.VEX, (Object)VexEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.VILLAGER, (Object)texturedModelData17);
        builder.put((Object)EntityModelLayers.VINDICATOR, (Object)texturedModelData8);
        builder.put((Object)EntityModelLayers.WARDEN, (Object)WardenEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.WANDERING_TRADER, (Object)texturedModelData17);
        builder.put((Object)EntityModelLayers.WITCH, (Object)WitchEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.WITHER, (Object)WitherEntityModel.getTexturedModelData(Dilation.NONE));
        builder.put((Object)EntityModelLayers.WITHER_ARMOR, (Object)WitherEntityModel.getTexturedModelData(HAT_DILATION));
        builder.put((Object)EntityModelLayers.WITHER_SKULL, (Object)WitherSkullEntityRenderer.getTexturedModelData());
        builder.put((Object)EntityModelLayers.WITHER_SKELETON, (Object)texturedModelData16);
        builder.put((Object)EntityModelLayers.WITHER_SKELETON_INNER_ARMOR, (Object)texturedModelData4);
        builder.put((Object)EntityModelLayers.WITHER_SKELETON_OUTER_ARMOR, (Object)texturedModelData2);
        builder.put((Object)EntityModelLayers.WITHER_SKELETON_SKULL, (Object)texturedModelData6);
        builder.put((Object)EntityModelLayers.WOLF, (Object)WolfEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.ZOGLIN, (Object)texturedModelData15);
        builder.put((Object)EntityModelLayers.ZOMBIE, (Object)texturedModelData);
        builder.put((Object)EntityModelLayers.ZOMBIE_INNER_ARMOR, (Object)texturedModelData4);
        builder.put((Object)EntityModelLayers.ZOMBIE_OUTER_ARMOR, (Object)texturedModelData2);
        builder.put((Object)EntityModelLayers.ZOMBIE_HEAD, (Object)texturedModelData12);
        builder.put((Object)EntityModelLayers.ZOMBIE_HORSE, (Object)texturedModelData7);
        builder.put((Object)EntityModelLayers.ZOMBIE_VILLAGER, (Object)ZombieVillagerEntityModel.getTexturedModelData());
        builder.put((Object)EntityModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR, (Object)ZombieVillagerEntityModel.getArmorTexturedModelData(HAT_DILATION));
        builder.put((Object)EntityModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR, (Object)ZombieVillagerEntityModel.getArmorTexturedModelData(ARMOR_DILATION));
        builder.put((Object)EntityModelLayers.ZOMBIFIED_PIGLIN, (Object)texturedModelData11);
        builder.put((Object)EntityModelLayers.ZOMBIFIED_PIGLIN_INNER_ARMOR, (Object)texturedModelData4);
        builder.put((Object)EntityModelLayers.ZOMBIFIED_PIGLIN_OUTER_ARMOR, (Object)texturedModelData3);
        TexturedModelData texturedModelData19 = BoatEntityModel.getTexturedModelData(false);
        TexturedModelData texturedModelData20 = BoatEntityModel.getTexturedModelData(true);
        for (BoatEntity.Type type : BoatEntity.Type.values()) {
            builder.put((Object)EntityModelLayers.createBoat(type), (Object)texturedModelData19);
            builder.put((Object)EntityModelLayers.createChestBoat(type), (Object)texturedModelData20);
        }
        TexturedModelData texturedModelData21 = SignBlockEntityRenderer.getTexturedModelData();
        SignType.stream().forEach(signType -> builder.put((Object)EntityModelLayers.createSign(signType), (Object)texturedModelData21));
        ImmutableMap immutableMap = builder.build();
        List list = EntityModelLayers.getLayers().filter(layer -> !immutableMap.containsKey(layer)).collect(Collectors.toList());
        if (!list.isEmpty()) {
            throw new IllegalStateException("Missing layer definitions: " + list);
        }
        return immutableMap;
    }
}

