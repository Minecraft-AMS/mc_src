/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.sensor;

import java.util.function.Supplier;
import net.minecraft.entity.ai.brain.sensor.DummySensor;
import net.minecraft.entity.ai.brain.sensor.GolemLastSeenSensor;
import net.minecraft.entity.ai.brain.sensor.HurtBySensor;
import net.minecraft.entity.ai.brain.sensor.InteractableDoorsSensor;
import net.minecraft.entity.ai.brain.sensor.NearestBedSensor;
import net.minecraft.entity.ai.brain.sensor.NearestLivingEntitiesSensor;
import net.minecraft.entity.ai.brain.sensor.NearestPlayersSensor;
import net.minecraft.entity.ai.brain.sensor.SecondaryPointsOfInterestSensor;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.VillagerBabiesSensor;
import net.minecraft.entity.ai.brain.sensor.VillagerHostilesSensor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SensorType<U extends Sensor<?>> {
    public static final SensorType<DummySensor> DUMMY = SensorType.register("dummy", DummySensor::new);
    public static final SensorType<NearestLivingEntitiesSensor> NEAREST_LIVING_ENTITIES = SensorType.register("nearest_living_entities", NearestLivingEntitiesSensor::new);
    public static final SensorType<NearestPlayersSensor> NEAREST_PLAYERS = SensorType.register("nearest_players", NearestPlayersSensor::new);
    public static final SensorType<InteractableDoorsSensor> INTERACTABLE_DOORS = SensorType.register("interactable_doors", InteractableDoorsSensor::new);
    public static final SensorType<NearestBedSensor> NEAREST_BED = SensorType.register("nearest_bed", NearestBedSensor::new);
    public static final SensorType<HurtBySensor> HURT_BY = SensorType.register("hurt_by", HurtBySensor::new);
    public static final SensorType<VillagerHostilesSensor> VILLAGER_HOSTILES = SensorType.register("villager_hostiles", VillagerHostilesSensor::new);
    public static final SensorType<VillagerBabiesSensor> VILLAGER_BABIES = SensorType.register("villager_babies", VillagerBabiesSensor::new);
    public static final SensorType<SecondaryPointsOfInterestSensor> SECONDARY_POIS = SensorType.register("secondary_pois", SecondaryPointsOfInterestSensor::new);
    public static final SensorType<GolemLastSeenSensor> GOLEM_LAST_SEEN = SensorType.register("golem_last_seen", GolemLastSeenSensor::new);
    private final Supplier<U> factory;

    private SensorType(Supplier<U> supplier) {
        this.factory = supplier;
    }

    public U create() {
        return (U)((Sensor)this.factory.get());
    }

    private static <U extends Sensor<?>> SensorType<U> register(String id, Supplier<U> supplier) {
        return Registry.register(Registry.SENSOR_TYPE, new Identifier(id), new SensorType<U>(supplier));
    }
}
