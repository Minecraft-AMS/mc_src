/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.sensor;

import java.util.function.Supplier;
import net.minecraft.entity.ai.brain.sensor.AxolotlAttackablesSensor;
import net.minecraft.entity.ai.brain.sensor.DummySensor;
import net.minecraft.entity.ai.brain.sensor.GolemLastSeenSensor;
import net.minecraft.entity.ai.brain.sensor.HoglinSpecificSensor;
import net.minecraft.entity.ai.brain.sensor.HurtBySensor;
import net.minecraft.entity.ai.brain.sensor.NearestBedSensor;
import net.minecraft.entity.ai.brain.sensor.NearestItemsSensor;
import net.minecraft.entity.ai.brain.sensor.NearestLivingEntitiesSensor;
import net.minecraft.entity.ai.brain.sensor.NearestPlayersSensor;
import net.minecraft.entity.ai.brain.sensor.NearestVisibleAdultSensor;
import net.minecraft.entity.ai.brain.sensor.PiglinBruteSpecificSensor;
import net.minecraft.entity.ai.brain.sensor.PiglinSpecificSensor;
import net.minecraft.entity.ai.brain.sensor.SecondaryPointsOfInterestSensor;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.TemptationsSensor;
import net.minecraft.entity.ai.brain.sensor.VillagerBabiesSensor;
import net.minecraft.entity.ai.brain.sensor.VillagerHostilesSensor;
import net.minecraft.entity.passive.AxolotlBrain;
import net.minecraft.entity.passive.GoatBrain;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SensorType<U extends Sensor<?>> {
    public static final SensorType<DummySensor> DUMMY = SensorType.register("dummy", DummySensor::new);
    public static final SensorType<NearestItemsSensor> NEAREST_ITEMS = SensorType.register("nearest_items", NearestItemsSensor::new);
    public static final SensorType<NearestLivingEntitiesSensor> NEAREST_LIVING_ENTITIES = SensorType.register("nearest_living_entities", NearestLivingEntitiesSensor::new);
    public static final SensorType<NearestPlayersSensor> NEAREST_PLAYERS = SensorType.register("nearest_players", NearestPlayersSensor::new);
    public static final SensorType<NearestBedSensor> NEAREST_BED = SensorType.register("nearest_bed", NearestBedSensor::new);
    public static final SensorType<HurtBySensor> HURT_BY = SensorType.register("hurt_by", HurtBySensor::new);
    public static final SensorType<VillagerHostilesSensor> VILLAGER_HOSTILES = SensorType.register("villager_hostiles", VillagerHostilesSensor::new);
    public static final SensorType<VillagerBabiesSensor> VILLAGER_BABIES = SensorType.register("villager_babies", VillagerBabiesSensor::new);
    public static final SensorType<SecondaryPointsOfInterestSensor> SECONDARY_POIS = SensorType.register("secondary_pois", SecondaryPointsOfInterestSensor::new);
    public static final SensorType<GolemLastSeenSensor> GOLEM_DETECTED = SensorType.register("golem_detected", GolemLastSeenSensor::new);
    public static final SensorType<PiglinSpecificSensor> PIGLIN_SPECIFIC_SENSOR = SensorType.register("piglin_specific_sensor", PiglinSpecificSensor::new);
    public static final SensorType<PiglinBruteSpecificSensor> PIGLIN_BRUTE_SPECIFIC_SENSOR = SensorType.register("piglin_brute_specific_sensor", PiglinBruteSpecificSensor::new);
    public static final SensorType<HoglinSpecificSensor> HOGLIN_SPECIFIC_SENSOR = SensorType.register("hoglin_specific_sensor", HoglinSpecificSensor::new);
    public static final SensorType<NearestVisibleAdultSensor> NEAREST_ADULT = SensorType.register("nearest_adult", NearestVisibleAdultSensor::new);
    public static final SensorType<AxolotlAttackablesSensor> AXOLOTL_ATTACKABLES = SensorType.register("axolotl_attackables", AxolotlAttackablesSensor::new);
    public static final SensorType<TemptationsSensor> AXOLOTL_TEMPTATIONS = SensorType.register("axolotl_temptations", () -> new TemptationsSensor(AxolotlBrain.getTemptItems()));
    public static final SensorType<TemptationsSensor> GOAT_TEMPTATIONS = SensorType.register("goat_temptations", () -> new TemptationsSensor(GoatBrain.getTemptItems()));
    private final Supplier<U> factory;

    private SensorType(Supplier<U> factory) {
        this.factory = factory;
    }

    public U create() {
        return (U)((Sensor)this.factory.get());
    }

    private static <U extends Sensor<?>> SensorType<U> register(String id, Supplier<U> factory) {
        return Registry.register(Registry.SENSOR_TYPE, new Identifier(id), new SensorType<U>(factory));
    }
}

