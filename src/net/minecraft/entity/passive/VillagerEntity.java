/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.entity.passive;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityInteraction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.InteractionObserver;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LargeEntitySpawnHelper;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.GolemLastSeenSensor;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.VillagerTaskListProvider;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillageGossipType;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerGossips;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class VillagerEntity
extends MerchantEntity
implements InteractionObserver,
VillagerDataContainer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final TrackedData<VillagerData> VILLAGER_DATA = DataTracker.registerData(VillagerEntity.class, TrackedDataHandlerRegistry.VILLAGER_DATA);
    public static final int field_30602 = 12;
    public static final Map<Item, Integer> ITEM_FOOD_VALUES = ImmutableMap.of((Object)Items.BREAD, (Object)4, (Object)Items.POTATO, (Object)1, (Object)Items.CARROT, (Object)1, (Object)Items.BEETROOT, (Object)1);
    private static final int field_30604 = 2;
    private static final Set<Item> GATHERABLE_ITEMS = ImmutableSet.of((Object)Items.BREAD, (Object)Items.POTATO, (Object)Items.CARROT, (Object)Items.WHEAT, (Object)Items.WHEAT_SEEDS, (Object)Items.BEETROOT, (Object[])new Item[]{Items.BEETROOT_SEEDS});
    private static final int field_30605 = 10;
    private static final int field_30606 = 1200;
    private static final int field_30607 = 24000;
    private static final int field_30608 = 25;
    private static final int field_30609 = 10;
    private static final int field_30610 = 5;
    private static final long field_30611 = 24000L;
    @VisibleForTesting
    public static final float field_30603 = 0.5f;
    private int levelUpTimer;
    private boolean levelingUp;
    @Nullable
    private PlayerEntity lastCustomer;
    private boolean field_30612;
    private int foodLevel;
    private final VillagerGossips gossip = new VillagerGossips();
    private long gossipStartTime;
    private long lastGossipDecayTime;
    private int experience;
    private long lastRestockTime;
    private int restocksToday;
    private long lastRestockCheckTime;
    private boolean natural;
    private static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(MemoryModuleType.HOME, MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleType.MEETING_POINT, MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, (Object[])new MemoryModuleType[]{MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, MemoryModuleType.PATH, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT, MemoryModuleType.LAST_WOKEN, MemoryModuleType.LAST_WORKED_AT_POI, MemoryModuleType.GOLEM_DETECTED_RECENTLY});
    private static final ImmutableList<SensorType<? extends Sensor<? super VillagerEntity>>> SENSORS = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.NEAREST_BED, SensorType.HURT_BY, SensorType.VILLAGER_HOSTILES, SensorType.VILLAGER_BABIES, SensorType.SECONDARY_POIS, SensorType.GOLEM_DETECTED);
    public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<VillagerEntity, RegistryEntry<PointOfInterestType>>> POINTS_OF_INTEREST = ImmutableMap.of(MemoryModuleType.HOME, (villager, registryEntry) -> registryEntry.matchesKey(PointOfInterestTypes.HOME), MemoryModuleType.JOB_SITE, (villager, registryEntry) -> villager.getVillagerData().getProfession().heldWorkstation().test((RegistryEntry<PointOfInterestType>)registryEntry), MemoryModuleType.POTENTIAL_JOB_SITE, (villager, registryEntry) -> VillagerProfession.IS_ACQUIRABLE_JOB_SITE.test((RegistryEntry<PointOfInterestType>)registryEntry), MemoryModuleType.MEETING_POINT, (villager, registryEntry) -> registryEntry.matchesKey(PointOfInterestTypes.MEETING));

    public VillagerEntity(EntityType<? extends VillagerEntity> entityType, World world) {
        this(entityType, world, VillagerType.PLAINS);
    }

    public VillagerEntity(EntityType<? extends VillagerEntity> entityType, World world, VillagerType type) {
        super((EntityType<? extends MerchantEntity>)entityType, world);
        ((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
        this.getNavigation().setCanSwim(true);
        this.setCanPickUpLoot(true);
        this.setVillagerData(this.getVillagerData().withType(type).withProfession(VillagerProfession.NONE));
    }

    public Brain<VillagerEntity> getBrain() {
        return super.getBrain();
    }

    protected Brain.Profile<VillagerEntity> createBrainProfile() {
        return Brain.createProfile(MEMORY_MODULES, SENSORS);
    }

    @Override
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        Brain<VillagerEntity> brain = this.createBrainProfile().deserialize(dynamic);
        this.initBrain(brain);
        return brain;
    }

    public void reinitializeBrain(ServerWorld world) {
        Brain<VillagerEntity> brain = this.getBrain();
        brain.stopAllTasks(world, this);
        this.brain = brain.copy();
        this.initBrain(this.getBrain());
    }

    private void initBrain(Brain<VillagerEntity> brain) {
        VillagerProfession villagerProfession = this.getVillagerData().getProfession();
        if (this.isBaby()) {
            brain.setSchedule(Schedule.VILLAGER_BABY);
            brain.setTaskList(Activity.PLAY, VillagerTaskListProvider.createPlayTasks(0.5f));
        } else {
            brain.setSchedule(Schedule.VILLAGER_DEFAULT);
            brain.setTaskList(Activity.WORK, (ImmutableList<Pair<Integer, Task<VillagerEntity>>>)VillagerTaskListProvider.createWorkTasks(villagerProfession, 0.5f), (Set<Pair<MemoryModuleType<?>, MemoryModuleState>>)ImmutableSet.of((Object)Pair.of(MemoryModuleType.JOB_SITE, (Object)((Object)MemoryModuleState.VALUE_PRESENT))));
        }
        brain.setTaskList(Activity.CORE, VillagerTaskListProvider.createCoreTasks(villagerProfession, 0.5f));
        brain.setTaskList(Activity.MEET, (ImmutableList<Pair<Integer, Task<VillagerEntity>>>)VillagerTaskListProvider.createMeetTasks(villagerProfession, 0.5f), (Set<Pair<MemoryModuleType<?>, MemoryModuleState>>)ImmutableSet.of((Object)Pair.of(MemoryModuleType.MEETING_POINT, (Object)((Object)MemoryModuleState.VALUE_PRESENT))));
        brain.setTaskList(Activity.REST, VillagerTaskListProvider.createRestTasks(villagerProfession, 0.5f));
        brain.setTaskList(Activity.IDLE, VillagerTaskListProvider.createIdleTasks(villagerProfession, 0.5f));
        brain.setTaskList(Activity.PANIC, VillagerTaskListProvider.createPanicTasks(villagerProfession, 0.5f));
        brain.setTaskList(Activity.PRE_RAID, VillagerTaskListProvider.createPreRaidTasks(villagerProfession, 0.5f));
        brain.setTaskList(Activity.RAID, VillagerTaskListProvider.createRaidTasks(villagerProfession, 0.5f));
        brain.setTaskList(Activity.HIDE, VillagerTaskListProvider.createHideTasks(villagerProfession, 0.5f));
        brain.setCoreActivities((Set<Activity>)ImmutableSet.of((Object)Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.doExclusively(Activity.IDLE);
        brain.refreshActivities(this.world.getTimeOfDay(), this.world.getTime());
    }

    @Override
    protected void onGrowUp() {
        super.onGrowUp();
        if (this.world instanceof ServerWorld) {
            this.reinitializeBrain((ServerWorld)this.world);
        }
    }

    public static DefaultAttributeContainer.Builder createVillagerAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0);
    }

    public boolean isNatural() {
        return this.natural;
    }

    @Override
    protected void mobTick() {
        Raid raid;
        this.world.getProfiler().push("villagerBrain");
        this.getBrain().tick((ServerWorld)this.world, this);
        this.world.getProfiler().pop();
        if (this.natural) {
            this.natural = false;
        }
        if (!this.hasCustomer() && this.levelUpTimer > 0) {
            --this.levelUpTimer;
            if (this.levelUpTimer <= 0) {
                if (this.levelingUp) {
                    this.levelUp();
                    this.levelingUp = false;
                }
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0));
            }
        }
        if (this.lastCustomer != null && this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).handleInteraction(EntityInteraction.TRADE, this.lastCustomer, this);
            this.world.sendEntityStatus(this, (byte)14);
            this.lastCustomer = null;
        }
        if (!this.isAiDisabled() && this.random.nextInt(100) == 0 && (raid = ((ServerWorld)this.world).getRaidAt(this.getBlockPos())) != null && raid.isActive() && !raid.isFinished()) {
            this.world.sendEntityStatus(this, (byte)42);
        }
        if (this.getVillagerData().getProfession() == VillagerProfession.NONE && this.hasCustomer()) {
            this.resetCustomer();
        }
        super.mobTick();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getHeadRollingTimeLeft() > 0) {
            this.setHeadRollingTimeLeft(this.getHeadRollingTimeLeft() - 1);
        }
        this.decayGossip();
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!itemStack.isOf(Items.VILLAGER_SPAWN_EGG) && this.isAlive() && !this.hasCustomer() && !this.isSleeping()) {
            if (this.isBaby()) {
                this.sayNo();
                return ActionResult.success(this.world.isClient);
            }
            boolean bl = this.getOffers().isEmpty();
            if (hand == Hand.MAIN_HAND) {
                if (bl && !this.world.isClient) {
                    this.sayNo();
                }
                player.incrementStat(Stats.TALKED_TO_VILLAGER);
            }
            if (bl) {
                return ActionResult.success(this.world.isClient);
            }
            if (!this.world.isClient && !this.offers.isEmpty()) {
                this.beginTradeWith(player);
            }
            return ActionResult.success(this.world.isClient);
        }
        return super.interactMob(player, hand);
    }

    private void sayNo() {
        this.setHeadRollingTimeLeft(40);
        if (!this.world.isClient()) {
            this.playSound(SoundEvents.ENTITY_VILLAGER_NO, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    private void beginTradeWith(PlayerEntity customer) {
        this.prepareOffersFor(customer);
        this.setCustomer(customer);
        this.sendOffers(customer, this.getDisplayName(), this.getVillagerData().getLevel());
    }

    @Override
    public void setCustomer(@Nullable PlayerEntity customer) {
        boolean bl = this.getCustomer() != null && customer == null;
        super.setCustomer(customer);
        if (bl) {
            this.resetCustomer();
        }
    }

    @Override
    protected void resetCustomer() {
        super.resetCustomer();
        this.clearSpecialPrices();
    }

    private void clearSpecialPrices() {
        for (TradeOffer tradeOffer : this.getOffers()) {
            tradeOffer.clearSpecialPrice();
        }
    }

    @Override
    public boolean canRefreshTrades() {
        return true;
    }

    @Override
    public boolean isClient() {
        return this.getWorld().isClient;
    }

    public void restock() {
        this.updateDemandBonus();
        for (TradeOffer tradeOffer : this.getOffers()) {
            tradeOffer.resetUses();
        }
        this.lastRestockTime = this.world.getTime();
        ++this.restocksToday;
    }

    private boolean needsRestock() {
        for (TradeOffer tradeOffer : this.getOffers()) {
            if (!tradeOffer.hasBeenUsed()) continue;
            return true;
        }
        return false;
    }

    private boolean canRestock() {
        return this.restocksToday == 0 || this.restocksToday < 2 && this.world.getTime() > this.lastRestockTime + 2400L;
    }

    public boolean shouldRestock() {
        long l = this.lastRestockTime + 12000L;
        long m = this.world.getTime();
        boolean bl = m > l;
        long n = this.world.getTimeOfDay();
        if (this.lastRestockCheckTime > 0L) {
            long p = n / 24000L;
            long o = this.lastRestockCheckTime / 24000L;
            bl |= p > o;
        }
        this.lastRestockCheckTime = n;
        if (bl) {
            this.lastRestockTime = m;
            this.clearDailyRestockCount();
        }
        return this.canRestock() && this.needsRestock();
    }

    private void restockAndUpdateDemandBonus() {
        int i = 2 - this.restocksToday;
        if (i > 0) {
            for (TradeOffer tradeOffer : this.getOffers()) {
                tradeOffer.resetUses();
            }
        }
        for (int j = 0; j < i; ++j) {
            this.updateDemandBonus();
        }
    }

    private void updateDemandBonus() {
        for (TradeOffer tradeOffer : this.getOffers()) {
            tradeOffer.updateDemandBonus();
        }
    }

    private void prepareOffersFor(PlayerEntity player) {
        int i = this.getReputation(player);
        if (i != 0) {
            for (TradeOffer tradeOffer : this.getOffers()) {
                tradeOffer.increaseSpecialPrice(-MathHelper.floor((float)i * tradeOffer.getPriceMultiplier()));
            }
        }
        if (player.hasStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE)) {
            StatusEffectInstance statusEffectInstance = player.getStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE);
            int j = statusEffectInstance.getAmplifier();
            for (TradeOffer tradeOffer2 : this.getOffers()) {
                double d = 0.3 + 0.0625 * (double)j;
                int k = (int)Math.floor(d * (double)tradeOffer2.getOriginalFirstBuyItem().getCount());
                tradeOffer2.increaseSpecialPrice(-Math.max(k, 1));
            }
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        VillagerData.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.getVillagerData()).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent(nbtElement -> nbt.put("VillagerData", (NbtElement)nbtElement));
        nbt.putByte("FoodLevel", (byte)this.foodLevel);
        nbt.put("Gossips", this.gossip.serialize(NbtOps.INSTANCE));
        nbt.putInt("Xp", this.experience);
        nbt.putLong("LastRestock", this.lastRestockTime);
        nbt.putLong("LastGossipDecay", this.lastGossipDecayTime);
        nbt.putInt("RestocksToday", this.restocksToday);
        if (this.natural) {
            nbt.putBoolean("AssignProfessionWhenSpawned", true);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("VillagerData", 10)) {
            DataResult dataResult = VillagerData.CODEC.parse(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)nbt.get("VillagerData")));
            dataResult.resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent(this::setVillagerData);
        }
        if (nbt.contains("Offers", 10)) {
            this.offers = new TradeOfferList(nbt.getCompound("Offers"));
        }
        if (nbt.contains("FoodLevel", 1)) {
            this.foodLevel = nbt.getByte("FoodLevel");
        }
        NbtList nbtList = nbt.getList("Gossips", 10);
        this.gossip.deserialize(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)nbtList));
        if (nbt.contains("Xp", 3)) {
            this.experience = nbt.getInt("Xp");
        }
        this.lastRestockTime = nbt.getLong("LastRestock");
        this.lastGossipDecayTime = nbt.getLong("LastGossipDecay");
        this.setCanPickUpLoot(true);
        if (this.world instanceof ServerWorld) {
            this.reinitializeBrain((ServerWorld)this.world);
        }
        this.restocksToday = nbt.getInt("RestocksToday");
        if (nbt.contains("AssignProfessionWhenSpawned")) {
            this.natural = nbt.getBoolean("AssignProfessionWhenSpawned");
        }
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return null;
        }
        if (this.hasCustomer()) {
            return SoundEvents.ENTITY_VILLAGER_TRADE;
        }
        return SoundEvents.ENTITY_VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VILLAGER_DEATH;
    }

    public void playWorkSound() {
        SoundEvent soundEvent = this.getVillagerData().getProfession().workSound();
        if (soundEvent != null) {
            this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    @Override
    public void setVillagerData(VillagerData villagerData) {
        VillagerData villagerData2 = this.getVillagerData();
        if (villagerData2.getProfession() != villagerData.getProfession()) {
            this.offers = null;
        }
        this.dataTracker.set(VILLAGER_DATA, villagerData);
    }

    @Override
    public VillagerData getVillagerData() {
        return this.dataTracker.get(VILLAGER_DATA);
    }

    @Override
    protected void afterUsing(TradeOffer offer) {
        int i = 3 + this.random.nextInt(4);
        this.experience += offer.getMerchantExperience();
        this.lastCustomer = this.getCustomer();
        if (this.canLevelUp()) {
            this.levelUpTimer = 40;
            this.levelingUp = true;
            i += 5;
        }
        if (offer.shouldRewardPlayerExperience()) {
            this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.getX(), this.getY() + 0.5, this.getZ(), i));
        }
    }

    public void method_35201(boolean bl) {
        this.field_30612 = bl;
    }

    public boolean method_35200() {
        return this.field_30612;
    }

    @Override
    public void setAttacker(@Nullable LivingEntity attacker) {
        if (attacker != null && this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).handleInteraction(EntityInteraction.VILLAGER_HURT, attacker, this);
            if (this.isAlive() && attacker instanceof PlayerEntity) {
                this.world.sendEntityStatus(this, (byte)13);
            }
        }
        super.setAttacker(attacker);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        LOGGER.info("Villager {} died, message: '{}'", (Object)this, (Object)damageSource.getDeathMessage(this).getString());
        Entity entity = damageSource.getAttacker();
        if (entity != null) {
            this.notifyDeath(entity);
        }
        this.releaseAllTickets();
        super.onDeath(damageSource);
    }

    private void releaseAllTickets() {
        this.releaseTicketFor(MemoryModuleType.HOME);
        this.releaseTicketFor(MemoryModuleType.JOB_SITE);
        this.releaseTicketFor(MemoryModuleType.POTENTIAL_JOB_SITE);
        this.releaseTicketFor(MemoryModuleType.MEETING_POINT);
    }

    private void notifyDeath(Entity killer) {
        World world = this.world;
        if (!(world instanceof ServerWorld)) {
            return;
        }
        ServerWorld serverWorld = (ServerWorld)world;
        Optional<LivingTargetCache> optional = this.brain.getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS);
        if (optional.isEmpty()) {
            return;
        }
        optional.get().iterate(InteractionObserver.class::isInstance).forEach(observer -> serverWorld.handleInteraction(EntityInteraction.VILLAGER_KILLED, killer, (InteractionObserver)((Object)observer)));
    }

    public void releaseTicketFor(MemoryModuleType<GlobalPos> pos2) {
        if (!(this.world instanceof ServerWorld)) {
            return;
        }
        MinecraftServer minecraftServer = ((ServerWorld)this.world).getServer();
        this.brain.getOptionalRegisteredMemory(pos2).ifPresent(pos -> {
            ServerWorld serverWorld = minecraftServer.getWorld(pos.getDimension());
            if (serverWorld == null) {
                return;
            }
            PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
            Optional<RegistryEntry<PointOfInterestType>> optional = pointOfInterestStorage.getType(pos.getPos());
            BiPredicate<VillagerEntity, RegistryEntry<PointOfInterestType>> biPredicate = POINTS_OF_INTEREST.get(pos2);
            if (optional.isPresent() && biPredicate.test(this, optional.get())) {
                pointOfInterestStorage.releaseTicket(pos.getPos());
                DebugInfoSender.sendPointOfInterest(serverWorld, pos.getPos());
            }
        });
    }

    @Override
    public boolean isReadyToBreed() {
        return this.foodLevel + this.getAvailableFood() >= 12 && !this.isSleeping() && this.getBreedingAge() == 0;
    }

    private boolean lacksFood() {
        return this.foodLevel < 12;
    }

    private void consumeAvailableFood() {
        if (!this.lacksFood() || this.getAvailableFood() == 0) {
            return;
        }
        for (int i = 0; i < this.getInventory().size(); ++i) {
            int j;
            Integer integer;
            ItemStack itemStack = this.getInventory().getStack(i);
            if (itemStack.isEmpty() || (integer = ITEM_FOOD_VALUES.get(itemStack.getItem())) == null) continue;
            for (int k = j = itemStack.getCount(); k > 0; --k) {
                this.foodLevel += integer.intValue();
                this.getInventory().removeStack(i, 1);
                if (this.lacksFood()) continue;
                return;
            }
        }
    }

    public int getReputation(PlayerEntity player) {
        return this.gossip.getReputationFor(player.getUuid(), gossipType -> true);
    }

    private void depleteFood(int amount) {
        this.foodLevel -= amount;
    }

    public void eatForBreeding() {
        this.consumeAvailableFood();
        this.depleteFood(12);
    }

    public void setOffers(TradeOfferList offers) {
        this.offers = offers;
    }

    private boolean canLevelUp() {
        int i = this.getVillagerData().getLevel();
        return VillagerData.canLevelUp(i) && this.experience >= VillagerData.getUpperLevelExperience(i);
    }

    private void levelUp() {
        this.setVillagerData(this.getVillagerData().withLevel(this.getVillagerData().getLevel() + 1));
        this.fillRecipes();
    }

    @Override
    protected Text getDefaultName() {
        return Text.translatable(this.getType().getTranslationKey() + "." + Registries.VILLAGER_PROFESSION.getId(this.getVillagerData().getProfession()).getPath());
    }

    @Override
    public void handleStatus(byte status) {
        if (status == 12) {
            this.produceParticles(ParticleTypes.HEART);
        } else if (status == 13) {
            this.produceParticles(ParticleTypes.ANGRY_VILLAGER);
        } else if (status == 14) {
            this.produceParticles(ParticleTypes.HAPPY_VILLAGER);
        } else if (status == 42) {
            this.produceParticles(ParticleTypes.SPLASH);
        } else {
            super.handleStatus(status);
        }
    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        if (spawnReason == SpawnReason.BREEDING) {
            this.setVillagerData(this.getVillagerData().withProfession(VillagerProfession.NONE));
        }
        if (spawnReason == SpawnReason.COMMAND || spawnReason == SpawnReason.SPAWN_EGG || spawnReason == SpawnReason.SPAWNER || spawnReason == SpawnReason.DISPENSER) {
            this.setVillagerData(this.getVillagerData().withType(VillagerType.forBiome(world.getBiome(this.getBlockPos()))));
        }
        if (spawnReason == SpawnReason.STRUCTURE) {
            this.natural = true;
        }
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    @Nullable
    public VillagerEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
        double d = this.random.nextDouble();
        VillagerType villagerType = d < 0.5 ? VillagerType.forBiome(serverWorld.getBiome(this.getBlockPos())) : (d < 0.75 ? this.getVillagerData().getType() : ((VillagerEntity)passiveEntity).getVillagerData().getType());
        VillagerEntity villagerEntity = new VillagerEntity(EntityType.VILLAGER, serverWorld, villagerType);
        villagerEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(villagerEntity.getBlockPos()), SpawnReason.BREEDING, null, null);
        return villagerEntity;
    }

    @Override
    public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
        if (world.getDifficulty() != Difficulty.PEACEFUL) {
            LOGGER.info("Villager {} was struck by lightning {}.", (Object)this, (Object)lightning);
            WitchEntity witchEntity = EntityType.WITCH.create(world);
            if (witchEntity != null) {
                witchEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
                witchEntity.initialize(world, world.getLocalDifficulty(witchEntity.getBlockPos()), SpawnReason.CONVERSION, null, null);
                witchEntity.setAiDisabled(this.isAiDisabled());
                if (this.hasCustomName()) {
                    witchEntity.setCustomName(this.getCustomName());
                    witchEntity.setCustomNameVisible(this.isCustomNameVisible());
                }
                witchEntity.setPersistent();
                world.spawnEntityAndPassengers(witchEntity);
                this.releaseAllTickets();
                this.discard();
            } else {
                super.onStruckByLightning(world, lightning);
            }
        } else {
            super.onStruckByLightning(world, lightning);
        }
    }

    @Override
    protected void loot(ItemEntity item) {
        InventoryOwner.pickUpItem(this, this, item);
    }

    @Override
    public boolean canGather(ItemStack stack) {
        Item item = stack.getItem();
        return (GATHERABLE_ITEMS.contains(item) || this.getVillagerData().getProfession().gatherableItems().contains((Object)item)) && this.getInventory().canInsert(stack);
    }

    public boolean wantsToStartBreeding() {
        return this.getAvailableFood() >= 24;
    }

    public boolean canBreed() {
        return this.getAvailableFood() < 12;
    }

    private int getAvailableFood() {
        SimpleInventory simpleInventory = this.getInventory();
        return ITEM_FOOD_VALUES.entrySet().stream().mapToInt(item -> simpleInventory.count((Item)item.getKey()) * (Integer)item.getValue()).sum();
    }

    public boolean hasSeedToPlant() {
        return this.getInventory().containsAny((Set<Item>)ImmutableSet.of((Object)Items.WHEAT_SEEDS, (Object)Items.POTATO, (Object)Items.CARROT, (Object)Items.BEETROOT_SEEDS));
    }

    @Override
    protected void fillRecipes() {
        VillagerData villagerData = this.getVillagerData();
        Int2ObjectMap<TradeOffers.Factory[]> int2ObjectMap = TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(villagerData.getProfession());
        if (int2ObjectMap == null || int2ObjectMap.isEmpty()) {
            return;
        }
        TradeOffers.Factory[] factorys = (TradeOffers.Factory[])int2ObjectMap.get(villagerData.getLevel());
        if (factorys == null) {
            return;
        }
        TradeOfferList tradeOfferList = this.getOffers();
        this.fillRecipesFromPool(tradeOfferList, factorys, 2);
    }

    public void talkWithVillager(ServerWorld world, VillagerEntity villager, long time) {
        if (time >= this.gossipStartTime && time < this.gossipStartTime + 1200L || time >= villager.gossipStartTime && time < villager.gossipStartTime + 1200L) {
            return;
        }
        this.gossip.shareGossipFrom(villager.gossip, this.random, 10);
        this.gossipStartTime = time;
        villager.gossipStartTime = time;
        this.summonGolem(world, time, 5);
    }

    private void decayGossip() {
        long l = this.world.getTime();
        if (this.lastGossipDecayTime == 0L) {
            this.lastGossipDecayTime = l;
            return;
        }
        if (l < this.lastGossipDecayTime + 24000L) {
            return;
        }
        this.gossip.decay();
        this.lastGossipDecayTime = l;
    }

    public void summonGolem(ServerWorld world, long time, int requiredCount) {
        if (!this.canSummonGolem(time)) {
            return;
        }
        Box box = this.getBoundingBox().expand(10.0, 10.0, 10.0);
        List<VillagerEntity> list = world.getNonSpectatingEntities(VillagerEntity.class, box);
        List list2 = list.stream().filter(villager -> villager.canSummonGolem(time)).limit(5L).collect(Collectors.toList());
        if (list2.size() < requiredCount) {
            return;
        }
        if (!LargeEntitySpawnHelper.trySpawnAt(EntityType.IRON_GOLEM, SpawnReason.MOB_SUMMONED, world, this.getBlockPos(), 10, 8, 6, LargeEntitySpawnHelper.Requirements.IRON_GOLEM).isPresent()) {
            return;
        }
        list.forEach(GolemLastSeenSensor::rememberIronGolem);
    }

    public boolean canSummonGolem(long time) {
        if (!this.hasRecentlySlept(this.world.getTime())) {
            return false;
        }
        return !this.brain.hasMemoryModule(MemoryModuleType.GOLEM_DETECTED_RECENTLY);
    }

    @Override
    public void onInteractionWith(EntityInteraction interaction, Entity entity) {
        if (interaction == EntityInteraction.ZOMBIE_VILLAGER_CURED) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.MAJOR_POSITIVE, 20);
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.MINOR_POSITIVE, 25);
        } else if (interaction == EntityInteraction.TRADE) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.TRADING, 2);
        } else if (interaction == EntityInteraction.VILLAGER_HURT) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.MINOR_NEGATIVE, 25);
        } else if (interaction == EntityInteraction.VILLAGER_KILLED) {
            this.gossip.startGossip(entity.getUuid(), VillageGossipType.MAJOR_NEGATIVE, 25);
        }
    }

    @Override
    public int getExperience() {
        return this.experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    private void clearDailyRestockCount() {
        this.restockAndUpdateDemandBonus();
        this.restocksToday = 0;
    }

    public VillagerGossips getGossip() {
        return this.gossip;
    }

    public void readGossipDataNbt(NbtElement nbt) {
        this.gossip.deserialize(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)nbt));
    }

    @Override
    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    @Override
    public void sleep(BlockPos pos) {
        super.sleep(pos);
        this.brain.remember(MemoryModuleType.LAST_SLEPT, this.world.getTime());
        this.brain.forget(MemoryModuleType.WALK_TARGET);
        this.brain.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
    }

    @Override
    public void wakeUp() {
        super.wakeUp();
        this.brain.remember(MemoryModuleType.LAST_WOKEN, this.world.getTime());
    }

    private boolean hasRecentlySlept(long worldTime) {
        Optional<Long> optional = this.brain.getOptionalRegisteredMemory(MemoryModuleType.LAST_SLEPT);
        if (optional.isPresent()) {
            return worldTime - optional.get() < 24000L;
        }
        return false;
    }

    @Override
    @Nullable
    public /* synthetic */ PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return this.createChild(world, entity);
    }
}

