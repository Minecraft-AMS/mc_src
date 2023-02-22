/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.EvictingQueue
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Queues
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.particle;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.AshParticle;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.BlockFallingDustParticle;
import net.minecraft.client.particle.BlockLeakParticle;
import net.minecraft.client.particle.BlockMarkerParticle;
import net.minecraft.client.particle.BubbleColumnUpParticle;
import net.minecraft.client.particle.BubblePopParticle;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.CloudParticle;
import net.minecraft.client.particle.CrackParticle;
import net.minecraft.client.particle.CurrentDownParticle;
import net.minecraft.client.particle.DamageParticle;
import net.minecraft.client.particle.DragonBreathParticle;
import net.minecraft.client.particle.DustColorTransitionParticle;
import net.minecraft.client.particle.ElderGuardianAppearanceParticle;
import net.minecraft.client.particle.EmitterParticle;
import net.minecraft.client.particle.EmotionParticle;
import net.minecraft.client.particle.EnchantGlyphParticle;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.particle.ExplosionEmitterParticle;
import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.client.particle.ExplosionSmokeParticle;
import net.minecraft.client.particle.FireSmokeParticle;
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.particle.FishingParticle;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.client.particle.LargeFireSmokeParticle;
import net.minecraft.client.particle.LavaEmberParticle;
import net.minecraft.client.particle.NoteParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.particle.ParticleTextureData;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.RainSplashParticle;
import net.minecraft.client.particle.RedDustParticle;
import net.minecraft.client.particle.ReversePortalParticle;
import net.minecraft.client.particle.SculkChargeParticle;
import net.minecraft.client.particle.SculkChargePopParticle;
import net.minecraft.client.particle.ShriekParticle;
import net.minecraft.client.particle.SnowflakeParticle;
import net.minecraft.client.particle.SonicBoomParticle;
import net.minecraft.client.particle.SoulParticle;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.particle.SpitParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.particle.SquidInkParticle;
import net.minecraft.client.particle.SuspendParticle;
import net.minecraft.client.particle.SweepAttackParticle;
import net.minecraft.client.particle.TotemParticle;
import net.minecraft.client.particle.VibrationParticle;
import net.minecraft.client.particle.WaterBubbleParticle;
import net.minecraft.client.particle.WaterSplashParticle;
import net.minecraft.client.particle.WaterSuspendParticle;
import net.minecraft.client.particle.WhiteAshParticle;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ParticleManager
implements ResourceReloader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceFinder FINDER = ResourceFinder.json("particles");
    private static final Identifier PARTICLES_PATH = new Identifier("particles");
    private static final int MAX_PARTICLE_COUNT = 16384;
    private static final List<ParticleTextureSheet> PARTICLE_TEXTURE_SHEETS = ImmutableList.of((Object)ParticleTextureSheet.TERRAIN_SHEET, (Object)ParticleTextureSheet.PARTICLE_SHEET_OPAQUE, (Object)ParticleTextureSheet.PARTICLE_SHEET_LIT, (Object)ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT, (Object)ParticleTextureSheet.CUSTOM);
    protected ClientWorld world;
    private final Map<ParticleTextureSheet, Queue<Particle>> particles = Maps.newIdentityHashMap();
    private final Queue<EmitterParticle> newEmitterParticles = Queues.newArrayDeque();
    private final TextureManager textureManager;
    private final Random random = Random.create();
    private final Int2ObjectMap<ParticleFactory<?>> factories = new Int2ObjectOpenHashMap();
    private final Queue<Particle> newParticles = Queues.newArrayDeque();
    private final Map<Identifier, SimpleSpriteProvider> spriteAwareFactories = Maps.newHashMap();
    private final SpriteAtlasTexture particleAtlasTexture;
    private final Object2IntOpenHashMap<ParticleGroup> groupCounts = new Object2IntOpenHashMap();

    public ParticleManager(ClientWorld world, TextureManager textureManager) {
        this.particleAtlasTexture = new SpriteAtlasTexture(SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
        textureManager.registerTexture(this.particleAtlasTexture.getId(), this.particleAtlasTexture);
        this.world = world;
        this.textureManager = textureManager;
        this.registerDefaultFactories();
    }

    private void registerDefaultFactories() {
        this.registerFactory(ParticleTypes.AMBIENT_ENTITY_EFFECT, SpellParticle.EntityAmbientFactory::new);
        this.registerFactory(ParticleTypes.ANGRY_VILLAGER, EmotionParticle.AngryVillagerFactory::new);
        this.registerFactory(ParticleTypes.BLOCK_MARKER, new BlockMarkerParticle.Factory());
        this.registerFactory(ParticleTypes.BLOCK, new BlockDustParticle.Factory());
        this.registerFactory(ParticleTypes.BUBBLE, WaterBubbleParticle.Factory::new);
        this.registerFactory(ParticleTypes.BUBBLE_COLUMN_UP, BubbleColumnUpParticle.Factory::new);
        this.registerFactory(ParticleTypes.BUBBLE_POP, BubblePopParticle.Factory::new);
        this.registerFactory(ParticleTypes.CAMPFIRE_COSY_SMOKE, CampfireSmokeParticle.CosySmokeFactory::new);
        this.registerFactory(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, CampfireSmokeParticle.SignalSmokeFactory::new);
        this.registerFactory(ParticleTypes.CLOUD, CloudParticle.CloudFactory::new);
        this.registerFactory(ParticleTypes.COMPOSTER, SuspendParticle.Factory::new);
        this.registerFactory(ParticleTypes.CRIT, DamageParticle.Factory::new);
        this.registerFactory(ParticleTypes.CURRENT_DOWN, CurrentDownParticle.Factory::new);
        this.registerFactory(ParticleTypes.DAMAGE_INDICATOR, DamageParticle.DefaultFactory::new);
        this.registerFactory(ParticleTypes.DRAGON_BREATH, DragonBreathParticle.Factory::new);
        this.registerFactory(ParticleTypes.DOLPHIN, SuspendParticle.DolphinFactory::new);
        this.registerFactory(ParticleTypes.DRIPPING_LAVA, BlockLeakParticle.DrippingLavaFactory::new);
        this.registerFactory(ParticleTypes.FALLING_LAVA, BlockLeakParticle.FallingLavaFactory::new);
        this.registerFactory(ParticleTypes.LANDING_LAVA, BlockLeakParticle.LandingLavaFactory::new);
        this.registerFactory(ParticleTypes.DRIPPING_WATER, BlockLeakParticle.DrippingWaterFactory::new);
        this.registerFactory(ParticleTypes.FALLING_WATER, BlockLeakParticle.FallingWaterFactory::new);
        this.registerFactory(ParticleTypes.DUST, RedDustParticle.Factory::new);
        this.registerFactory(ParticleTypes.DUST_COLOR_TRANSITION, DustColorTransitionParticle.Factory::new);
        this.registerFactory(ParticleTypes.EFFECT, SpellParticle.DefaultFactory::new);
        this.registerFactory(ParticleTypes.ELDER_GUARDIAN, new ElderGuardianAppearanceParticle.Factory());
        this.registerFactory(ParticleTypes.ENCHANTED_HIT, DamageParticle.EnchantedHitFactory::new);
        this.registerFactory(ParticleTypes.ENCHANT, EnchantGlyphParticle.EnchantFactory::new);
        this.registerFactory(ParticleTypes.END_ROD, EndRodParticle.Factory::new);
        this.registerFactory(ParticleTypes.ENTITY_EFFECT, SpellParticle.EntityFactory::new);
        this.registerFactory(ParticleTypes.EXPLOSION_EMITTER, new ExplosionEmitterParticle.Factory());
        this.registerFactory(ParticleTypes.EXPLOSION, ExplosionLargeParticle.Factory::new);
        this.registerFactory(ParticleTypes.SONIC_BOOM, SonicBoomParticle.Factory::new);
        this.registerFactory(ParticleTypes.FALLING_DUST, BlockFallingDustParticle.Factory::new);
        this.registerFactory(ParticleTypes.FIREWORK, FireworksSparkParticle.ExplosionFactory::new);
        this.registerFactory(ParticleTypes.FISHING, FishingParticle.Factory::new);
        this.registerFactory(ParticleTypes.FLAME, FlameParticle.Factory::new);
        this.registerFactory(ParticleTypes.SCULK_SOUL, SoulParticle.SculkSoulFactory::new);
        this.registerFactory(ParticleTypes.SCULK_CHARGE, SculkChargeParticle.Factory::new);
        this.registerFactory(ParticleTypes.SCULK_CHARGE_POP, SculkChargePopParticle.Factory::new);
        this.registerFactory(ParticleTypes.SOUL, SoulParticle.Factory::new);
        this.registerFactory(ParticleTypes.SOUL_FIRE_FLAME, FlameParticle.Factory::new);
        this.registerFactory(ParticleTypes.FLASH, FireworksSparkParticle.FlashFactory::new);
        this.registerFactory(ParticleTypes.HAPPY_VILLAGER, SuspendParticle.HappyVillagerFactory::new);
        this.registerFactory(ParticleTypes.HEART, EmotionParticle.HeartFactory::new);
        this.registerFactory(ParticleTypes.INSTANT_EFFECT, SpellParticle.InstantFactory::new);
        this.registerFactory(ParticleTypes.ITEM, new CrackParticle.ItemFactory());
        this.registerFactory(ParticleTypes.ITEM_SLIME, new CrackParticle.SlimeballFactory());
        this.registerFactory(ParticleTypes.ITEM_SNOWBALL, new CrackParticle.SnowballFactory());
        this.registerFactory(ParticleTypes.LARGE_SMOKE, LargeFireSmokeParticle.Factory::new);
        this.registerFactory(ParticleTypes.LAVA, LavaEmberParticle.Factory::new);
        this.registerFactory(ParticleTypes.MYCELIUM, SuspendParticle.MyceliumFactory::new);
        this.registerFactory(ParticleTypes.NAUTILUS, EnchantGlyphParticle.NautilusFactory::new);
        this.registerFactory(ParticleTypes.NOTE, NoteParticle.Factory::new);
        this.registerFactory(ParticleTypes.POOF, ExplosionSmokeParticle.Factory::new);
        this.registerFactory(ParticleTypes.PORTAL, PortalParticle.Factory::new);
        this.registerFactory(ParticleTypes.RAIN, RainSplashParticle.Factory::new);
        this.registerFactory(ParticleTypes.SMOKE, FireSmokeParticle.Factory::new);
        this.registerFactory(ParticleTypes.SNEEZE, CloudParticle.SneezeFactory::new);
        this.registerFactory(ParticleTypes.SNOWFLAKE, SnowflakeParticle.Factory::new);
        this.registerFactory(ParticleTypes.SPIT, SpitParticle.Factory::new);
        this.registerFactory(ParticleTypes.SWEEP_ATTACK, SweepAttackParticle.Factory::new);
        this.registerFactory(ParticleTypes.TOTEM_OF_UNDYING, TotemParticle.Factory::new);
        this.registerFactory(ParticleTypes.SQUID_INK, SquidInkParticle.Factory::new);
        this.registerFactory(ParticleTypes.UNDERWATER, WaterSuspendParticle.UnderwaterFactory::new);
        this.registerFactory(ParticleTypes.SPLASH, WaterSplashParticle.SplashFactory::new);
        this.registerFactory(ParticleTypes.WITCH, SpellParticle.WitchFactory::new);
        this.registerFactory(ParticleTypes.DRIPPING_HONEY, BlockLeakParticle.DrippingHoneyFactory::new);
        this.registerFactory(ParticleTypes.FALLING_HONEY, BlockLeakParticle.FallingHoneyFactory::new);
        this.registerFactory(ParticleTypes.LANDING_HONEY, BlockLeakParticle.LandingHoneyFactory::new);
        this.registerFactory(ParticleTypes.FALLING_NECTAR, BlockLeakParticle.FallingNectarFactory::new);
        this.registerFactory(ParticleTypes.FALLING_SPORE_BLOSSOM, BlockLeakParticle.FallingSporeBlossomFactory::new);
        this.registerFactory(ParticleTypes.SPORE_BLOSSOM_AIR, WaterSuspendParticle.SporeBlossomAirFactory::new);
        this.registerFactory(ParticleTypes.ASH, AshParticle.Factory::new);
        this.registerFactory(ParticleTypes.CRIMSON_SPORE, WaterSuspendParticle.CrimsonSporeFactory::new);
        this.registerFactory(ParticleTypes.WARPED_SPORE, WaterSuspendParticle.WarpedSporeFactory::new);
        this.registerFactory(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, BlockLeakParticle.DrippingObsidianTearFactory::new);
        this.registerFactory(ParticleTypes.FALLING_OBSIDIAN_TEAR, BlockLeakParticle.FallingObsidianTearFactory::new);
        this.registerFactory(ParticleTypes.LANDING_OBSIDIAN_TEAR, BlockLeakParticle.LandingObsidianTearFactory::new);
        this.registerFactory(ParticleTypes.REVERSE_PORTAL, ReversePortalParticle.Factory::new);
        this.registerFactory(ParticleTypes.WHITE_ASH, WhiteAshParticle.Factory::new);
        this.registerFactory(ParticleTypes.SMALL_FLAME, FlameParticle.SmallFactory::new);
        this.registerFactory(ParticleTypes.DRIPPING_DRIPSTONE_WATER, BlockLeakParticle.FallingDripstoneWaterFactory::new);
        this.registerFactory(ParticleTypes.FALLING_DRIPSTONE_WATER, BlockLeakParticle.DripstoneLavaSplashFactory::new);
        this.registerFactory(ParticleTypes.DRIPPING_DRIPSTONE_LAVA, BlockLeakParticle.FallingDripstoneLavaFactory::new);
        this.registerFactory(ParticleTypes.FALLING_DRIPSTONE_LAVA, BlockLeakParticle.LandingDripstoneLavaFactory::new);
        this.registerFactory(ParticleTypes.VIBRATION, VibrationParticle.Factory::new);
        this.registerFactory(ParticleTypes.GLOW_SQUID_INK, SquidInkParticle.GlowSquidInkFactory::new);
        this.registerFactory(ParticleTypes.GLOW, GlowParticle.GlowFactory::new);
        this.registerFactory(ParticleTypes.WAX_ON, GlowParticle.WaxOnFactory::new);
        this.registerFactory(ParticleTypes.WAX_OFF, GlowParticle.WaxOffFactory::new);
        this.registerFactory(ParticleTypes.ELECTRIC_SPARK, GlowParticle.ElectricSparkFactory::new);
        this.registerFactory(ParticleTypes.SCRAPE, GlowParticle.ScrapeFactory::new);
        this.registerFactory(ParticleTypes.SHRIEK, ShriekParticle.Factory::new);
    }

    private <T extends ParticleEffect> void registerFactory(ParticleType<T> type, ParticleFactory<T> factory) {
        this.factories.put(Registries.PARTICLE_TYPE.getRawId(type), factory);
    }

    private <T extends ParticleEffect> void registerFactory(ParticleType<T> type, SpriteAwareFactory<T> factory) {
        SimpleSpriteProvider simpleSpriteProvider = new SimpleSpriteProvider();
        this.spriteAwareFactories.put(Registries.PARTICLE_TYPE.getId(type), simpleSpriteProvider);
        this.factories.put(Registries.PARTICLE_TYPE.getRawId(type), factory.create(simpleSpriteProvider));
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        CompletionStage completableFuture = CompletableFuture.supplyAsync(() -> FINDER.findResources(manager), prepareExecutor).thenCompose(particles -> {
            ArrayList list = new ArrayList(particles.size());
            particles.forEach((id, resource) -> {
                Identifier identifier = FINDER.toResourceId((Identifier)id);
                list.add(CompletableFuture.supplyAsync(() -> {
                    @Environment(value=EnvType.CLIENT)
                    record ReloadResult(Identifier id, Optional<List<Identifier>> sprites) {
                    }
                    return new ReloadResult(identifier, this.loadTextureList(identifier, (Resource)resource));
                }, prepareExecutor));
            });
            return Util.combineSafe(list);
        });
        CompletionStage completableFuture2 = SpriteLoader.fromAtlas(this.particleAtlasTexture).method_47661(manager, PARTICLES_PATH, 0, prepareExecutor).thenCompose(SpriteLoader.StitchResult::whenComplete);
        return ((CompletableFuture)CompletableFuture.allOf(new CompletableFuture[]{completableFuture2, completableFuture}).thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(arg_0 -> this.method_45766(applyProfiler, (CompletableFuture)completableFuture2, (CompletableFuture)completableFuture, arg_0), applyExecutor);
    }

    public void clearAtlas() {
        this.particleAtlasTexture.clear();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private Optional<List<Identifier>> loadTextureList(Identifier id, Resource resource) {
        try (BufferedReader reader = resource.getReader();){
            ParticleTextureData particleTextureData = ParticleTextureData.load(JsonHelper.deserialize(reader));
            List<Identifier> list = particleTextureData.getTextureList();
            boolean bl = this.spriteAwareFactories.containsKey(id);
            if (list == null) {
                if (bl) {
                    throw new IllegalStateException("Missing texture list for particle " + id);
                }
                Optional<List<Identifier>> optional2 = Optional.empty();
                return optional2;
            }
            if (!bl) {
                throw new IllegalStateException("Redundant texture list for particle " + id);
            }
            Optional<List<Identifier>> optional = Optional.of(list);
            return optional;
        }
        catch (IOException iOException) {
            throw new IllegalStateException("Failed to load description for particle " + id, iOException);
        }
    }

    public void addEmitter(Entity entity, ParticleEffect parameters) {
        this.newEmitterParticles.add(new EmitterParticle(this.world, entity, parameters));
    }

    public void addEmitter(Entity entity, ParticleEffect parameters, int maxAge) {
        this.newEmitterParticles.add(new EmitterParticle(this.world, entity, parameters, maxAge));
    }

    @Nullable
    public Particle addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        Particle particle = this.createParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
        if (particle != null) {
            this.addParticle(particle);
            return particle;
        }
        return null;
    }

    @Nullable
    private <T extends ParticleEffect> Particle createParticle(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        ParticleFactory particleFactory = (ParticleFactory)this.factories.get(Registries.PARTICLE_TYPE.getRawId(parameters.getType()));
        if (particleFactory == null) {
            return null;
        }
        return particleFactory.createParticle(parameters, this.world, x, y, z, velocityX, velocityY, velocityZ);
    }

    public void addParticle(Particle particle) {
        Optional<ParticleGroup> optional = particle.getGroup();
        if (optional.isPresent()) {
            if (this.canAdd(optional.get())) {
                this.newParticles.add(particle);
                this.addTo(optional.get(), 1);
            }
        } else {
            this.newParticles.add(particle);
        }
    }

    public void tick() {
        this.particles.forEach((sheet, queue) -> {
            this.world.getProfiler().push(sheet.toString());
            this.tickParticles((Collection<Particle>)queue);
            this.world.getProfiler().pop();
        });
        if (!this.newEmitterParticles.isEmpty()) {
            ArrayList list = Lists.newArrayList();
            for (EmitterParticle emitterParticle : this.newEmitterParticles) {
                emitterParticle.tick();
                if (emitterParticle.isAlive()) continue;
                list.add(emitterParticle);
            }
            this.newEmitterParticles.removeAll(list);
        }
        if (!this.newParticles.isEmpty()) {
            Particle particle;
            while ((particle = this.newParticles.poll()) != null) {
                this.particles.computeIfAbsent(particle.getType(), sheet -> EvictingQueue.create((int)16384)).add(particle);
            }
        }
    }

    private void tickParticles(Collection<Particle> particles) {
        if (!particles.isEmpty()) {
            Iterator<Particle> iterator = particles.iterator();
            while (iterator.hasNext()) {
                Particle particle = iterator.next();
                this.tickParticle(particle);
                if (particle.isAlive()) continue;
                particle.getGroup().ifPresent(group -> this.addTo((ParticleGroup)group, -1));
                iterator.remove();
            }
        }
    }

    private void addTo(ParticleGroup group, int count) {
        this.groupCounts.addTo((Object)group, count);
    }

    private void tickParticle(Particle particle) {
        try {
            particle.tick();
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Ticking Particle");
            CrashReportSection crashReportSection = crashReport.addElement("Particle being ticked");
            crashReportSection.add("Particle", particle::toString);
            crashReportSection.add("Particle Type", particle.getType()::toString);
            throw new CrashException(crashReport);
        }
    }

    public void renderParticles(MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, LightmapTextureManager lightmapTextureManager, Camera camera, float tickDelta) {
        lightmapTextureManager.enable();
        RenderSystem.enableDepthTest();
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.multiplyPositionMatrix(matrices.peek().getPositionMatrix());
        RenderSystem.applyModelViewMatrix();
        for (ParticleTextureSheet particleTextureSheet : PARTICLE_TEXTURE_SHEETS) {
            Iterable iterable = this.particles.get(particleTextureSheet);
            if (iterable == null) continue;
            RenderSystem.setShader(GameRenderer::getParticleProgram);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            particleTextureSheet.begin(bufferBuilder, this.textureManager);
            for (Particle particle : iterable) {
                try {
                    particle.buildGeometry(bufferBuilder, camera, tickDelta);
                }
                catch (Throwable throwable) {
                    CrashReport crashReport = CrashReport.create(throwable, "Rendering Particle");
                    CrashReportSection crashReportSection = crashReport.addElement("Particle being rendered");
                    crashReportSection.add("Particle", particle::toString);
                    crashReportSection.add("Particle Type", particleTextureSheet::toString);
                    throw new CrashException(crashReport);
                }
            }
            particleTextureSheet.draw(tessellator);
        }
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        lightmapTextureManager.disable();
    }

    public void setWorld(@Nullable ClientWorld world) {
        this.world = world;
        this.clearParticles();
        this.newEmitterParticles.clear();
    }

    public void addBlockBreakParticles(BlockPos pos, BlockState state) {
        if (state.isAir() || !state.hasBlockBreakParticles()) {
            return;
        }
        VoxelShape voxelShape = state.getOutlineShape(this.world, pos);
        double d = 0.25;
        voxelShape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double d = Math.min(1.0, maxX - minX);
            double e = Math.min(1.0, maxY - minY);
            double f = Math.min(1.0, maxZ - minZ);
            int i = Math.max(2, MathHelper.ceil(d / 0.25));
            int j = Math.max(2, MathHelper.ceil(e / 0.25));
            int k = Math.max(2, MathHelper.ceil(f / 0.25));
            for (int l = 0; l < i; ++l) {
                for (int m = 0; m < j; ++m) {
                    for (int n = 0; n < k; ++n) {
                        double g = ((double)l + 0.5) / (double)i;
                        double h = ((double)m + 0.5) / (double)j;
                        double o = ((double)n + 0.5) / (double)k;
                        double p = g * d + minX;
                        double q = h * e + minY;
                        double r = o * f + minZ;
                        this.addParticle(new BlockDustParticle(this.world, (double)pos.getX() + p, (double)pos.getY() + q, (double)pos.getZ() + r, g - 0.5, h - 0.5, o - 0.5, state, pos));
                    }
                }
            }
        });
    }

    public void addBlockBreakingParticles(BlockPos pos, Direction direction) {
        BlockState blockState = this.world.getBlockState(pos);
        if (blockState.getRenderType() == BlockRenderType.INVISIBLE) {
            return;
        }
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        float f = 0.1f;
        Box box = blockState.getOutlineShape(this.world, pos).getBoundingBox();
        double d = (double)i + this.random.nextDouble() * (box.maxX - box.minX - (double)0.2f) + (double)0.1f + box.minX;
        double e = (double)j + this.random.nextDouble() * (box.maxY - box.minY - (double)0.2f) + (double)0.1f + box.minY;
        double g = (double)k + this.random.nextDouble() * (box.maxZ - box.minZ - (double)0.2f) + (double)0.1f + box.minZ;
        if (direction == Direction.DOWN) {
            e = (double)j + box.minY - (double)0.1f;
        }
        if (direction == Direction.UP) {
            e = (double)j + box.maxY + (double)0.1f;
        }
        if (direction == Direction.NORTH) {
            g = (double)k + box.minZ - (double)0.1f;
        }
        if (direction == Direction.SOUTH) {
            g = (double)k + box.maxZ + (double)0.1f;
        }
        if (direction == Direction.WEST) {
            d = (double)i + box.minX - (double)0.1f;
        }
        if (direction == Direction.EAST) {
            d = (double)i + box.maxX + (double)0.1f;
        }
        this.addParticle(new BlockDustParticle(this.world, d, e, g, 0.0, 0.0, 0.0, blockState, pos).move(0.2f).scale(0.6f));
    }

    public String getDebugString() {
        return String.valueOf(this.particles.values().stream().mapToInt(Collection::size).sum());
    }

    private boolean canAdd(ParticleGroup group) {
        return this.groupCounts.getInt((Object)group) < group.getMaxCount();
    }

    private void clearParticles() {
        this.particles.clear();
        this.groupCounts.clear();
    }

    private /* synthetic */ void method_45766(Profiler profiler, CompletableFuture completableFuture, CompletableFuture completableFuture2, Void void_) {
        this.clearParticles();
        profiler.startTick();
        profiler.push("upload");
        SpriteLoader.StitchResult stitchResult = (SpriteLoader.StitchResult)completableFuture.join();
        this.particleAtlasTexture.upload(stitchResult);
        profiler.swap("bindSpriteSets");
        HashSet set = new HashSet();
        Sprite sprite = stitchResult.missing();
        ((List)completableFuture2.join()).forEach(result -> {
            Optional<List<Identifier>> optional = result.sprites();
            if (optional.isEmpty()) {
                return;
            }
            ArrayList<Sprite> list = new ArrayList<Sprite>();
            for (Identifier identifier : optional.get()) {
                Sprite sprite2 = stitchResult.regions().get(identifier);
                if (sprite2 == null) {
                    set.add(identifier);
                    list.add(sprite);
                    continue;
                }
                list.add(sprite2);
            }
            if (list.isEmpty()) {
                list.add(sprite);
            }
            this.spriteAwareFactories.get(result.id()).setSprites(list);
        });
        if (!set.isEmpty()) {
            LOGGER.warn("Missing particle sprites: {}", (Object)set.stream().sorted().map(Identifier::toString).collect(Collectors.joining(",")));
        }
        profiler.pop();
        profiler.endTick();
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    static interface SpriteAwareFactory<T extends ParticleEffect> {
        public ParticleFactory<T> create(SpriteProvider var1);
    }

    @Environment(value=EnvType.CLIENT)
    static class SimpleSpriteProvider
    implements SpriteProvider {
        private List<Sprite> sprites;

        SimpleSpriteProvider() {
        }

        @Override
        public Sprite getSprite(int age, int maxAge) {
            return this.sprites.get(age * (this.sprites.size() - 1) / maxAge);
        }

        @Override
        public Sprite getSprite(Random random) {
            return this.sprites.get(random.nextInt(this.sprites.size()));
        }

        public void setSprites(List<Sprite> sprites) {
            this.sprites = ImmutableList.copyOf(sprites);
        }
    }
}

