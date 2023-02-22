/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.VillagerResourceMetadata;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

@Environment(value=EnvType.CLIENT)
public class VillagerClothingFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>>
extends FeatureRenderer<T, M>
implements SynchronousResourceReloadListener {
    private static final Int2ObjectMap<Identifier> LEVEL_TO_ID = (Int2ObjectMap)Util.make(new Int2ObjectOpenHashMap(), int2ObjectOpenHashMap -> {
        int2ObjectOpenHashMap.put(1, (Object)new Identifier("stone"));
        int2ObjectOpenHashMap.put(2, (Object)new Identifier("iron"));
        int2ObjectOpenHashMap.put(3, (Object)new Identifier("gold"));
        int2ObjectOpenHashMap.put(4, (Object)new Identifier("emerald"));
        int2ObjectOpenHashMap.put(5, (Object)new Identifier("diamond"));
    });
    private final Object2ObjectMap<VillagerType, VillagerResourceMetadata.HatType> villagerTypeToHat = new Object2ObjectOpenHashMap();
    private final Object2ObjectMap<VillagerProfession, VillagerResourceMetadata.HatType> professionToHat = new Object2ObjectOpenHashMap();
    private final ReloadableResourceManager resourceManager;
    private final String entityType;

    public VillagerClothingFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext, ReloadableResourceManager reloadableResourceManager, String string) {
        super(featureRendererContext);
        this.resourceManager = reloadableResourceManager;
        this.entityType = string;
        reloadableResourceManager.registerListener(this);
    }

    @Override
    public void render(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
        if (((Entity)livingEntity).isInvisible()) {
            return;
        }
        VillagerData villagerData = ((VillagerDataContainer)livingEntity).getVillagerData();
        VillagerType villagerType = villagerData.getType();
        VillagerProfession villagerProfession = villagerData.getProfession();
        VillagerResourceMetadata.HatType hatType = this.getHatType(this.villagerTypeToHat, "type", Registry.VILLAGER_TYPE, villagerType);
        VillagerResourceMetadata.HatType hatType2 = this.getHatType(this.professionToHat, "profession", Registry.VILLAGER_PROFESSION, villagerProfession);
        Object entityModel = this.getContextModel();
        this.bindTexture(this.findTexture("type", Registry.VILLAGER_TYPE.getId(villagerType)));
        ((ModelWithHat)entityModel).setHatVisible(hatType2 == VillagerResourceMetadata.HatType.NONE || hatType2 == VillagerResourceMetadata.HatType.PARTIAL && hatType != VillagerResourceMetadata.HatType.FULL);
        ((EntityModel)entityModel).render(livingEntity, f, g, i, j, k, l);
        ((ModelWithHat)entityModel).setHatVisible(true);
        if (villagerProfession != VillagerProfession.NONE && !((LivingEntity)livingEntity).isBaby()) {
            this.bindTexture(this.findTexture("profession", Registry.VILLAGER_PROFESSION.getId(villagerProfession)));
            ((EntityModel)entityModel).render(livingEntity, f, g, i, j, k, l);
            this.bindTexture(this.findTexture("profession_level", (Identifier)LEVEL_TO_ID.get(MathHelper.clamp(villagerData.getLevel(), 1, LEVEL_TO_ID.size()))));
            ((EntityModel)entityModel).render(livingEntity, f, g, i, j, k, l);
        }
    }

    @Override
    public boolean hasHurtOverlay() {
        return true;
    }

    private Identifier findTexture(String keyType, Identifier keyId) {
        return new Identifier(keyId.getNamespace(), "textures/entity/" + this.entityType + "/" + keyType + "/" + keyId.getPath() + ".png");
    }

    public <K> VillagerResourceMetadata.HatType getHatType(Object2ObjectMap<K, VillagerResourceMetadata.HatType> hatLookUp, String keyType, DefaultedRegistry<K> registry, K key) {
        return (VillagerResourceMetadata.HatType)((Object)hatLookUp.computeIfAbsent(key, object2 -> {
            try (Resource resource = this.resourceManager.getResource(this.findTexture(keyType, registry.getId(key)));){
                VillagerResourceMetadata villagerResourceMetadata = resource.getMetadata(VillagerResourceMetadata.READER);
                if (villagerResourceMetadata == null) return VillagerResourceMetadata.HatType.NONE;
                VillagerResourceMetadata.HatType hatType = villagerResourceMetadata.getHatType();
                return hatType;
            }
            catch (IOException iOException) {
                // empty catch block
            }
            return VillagerResourceMetadata.HatType.NONE;
        }));
    }

    @Override
    public void apply(ResourceManager manager) {
        this.professionToHat.clear();
        this.villagerTypeToHat.clear();
    }
}

