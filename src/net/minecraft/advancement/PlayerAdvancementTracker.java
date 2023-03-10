/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.common.io.Files
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 *  com.google.gson.internal.Streams
 *  com.google.gson.reflect.TypeToken
 *  com.google.gson.stream.JsonReader
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.advancement;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.CriterionProgress;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.network.MessageType;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SelectAdvancementTabS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class PlayerAdvancementTracker {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_VISIBLE_CHILDREN = 2;
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(AdvancementProgress.class, (Object)new AdvancementProgress.Serializer()).registerTypeAdapter(Identifier.class, (Object)new Identifier.Serializer()).setPrettyPrinting().create();
    private static final TypeToken<Map<Identifier, AdvancementProgress>> JSON_TYPE = new TypeToken<Map<Identifier, AdvancementProgress>>(){};
    private final DataFixer dataFixer;
    private final PlayerManager playerManager;
    private final File advancementFile;
    private final Map<Advancement, AdvancementProgress> advancementToProgress = Maps.newLinkedHashMap();
    private final Set<Advancement> visibleAdvancements = Sets.newLinkedHashSet();
    private final Set<Advancement> visibilityUpdates = Sets.newLinkedHashSet();
    private final Set<Advancement> progressUpdates = Sets.newLinkedHashSet();
    private ServerPlayerEntity owner;
    @Nullable
    private Advancement currentDisplayTab;
    private boolean dirty = true;

    public PlayerAdvancementTracker(DataFixer dataFixer, PlayerManager playerManager, ServerAdvancementLoader advancementLoader, File advancementFile, ServerPlayerEntity owner) {
        this.dataFixer = dataFixer;
        this.playerManager = playerManager;
        this.advancementFile = advancementFile;
        this.owner = owner;
        this.load(advancementLoader);
    }

    public void setOwner(ServerPlayerEntity owner) {
        this.owner = owner;
    }

    public void clearCriteria() {
        for (Criterion<?> criterion : Criteria.getCriteria()) {
            criterion.endTracking(this);
        }
    }

    public void reload(ServerAdvancementLoader advancementLoader) {
        this.clearCriteria();
        this.advancementToProgress.clear();
        this.visibleAdvancements.clear();
        this.visibilityUpdates.clear();
        this.progressUpdates.clear();
        this.dirty = true;
        this.currentDisplayTab = null;
        this.load(advancementLoader);
    }

    private void beginTrackingAllAdvancements(ServerAdvancementLoader advancementLoader) {
        for (Advancement advancement : advancementLoader.getAdvancements()) {
            this.beginTracking(advancement);
        }
    }

    private void updateCompleted() {
        ArrayList list = Lists.newArrayList();
        for (Map.Entry<Advancement, AdvancementProgress> entry : this.advancementToProgress.entrySet()) {
            if (!entry.getValue().isDone()) continue;
            list.add(entry.getKey());
            this.progressUpdates.add(entry.getKey());
        }
        for (Advancement advancement : list) {
            this.updateDisplay(advancement);
        }
    }

    private void rewardEmptyAdvancements(ServerAdvancementLoader advancementLoader) {
        for (Advancement advancement : advancementLoader.getAdvancements()) {
            if (!advancement.getCriteria().isEmpty()) continue;
            this.grantCriterion(advancement, "");
            advancement.getRewards().apply(this.owner);
        }
    }

    private void load(ServerAdvancementLoader advancementLoader) {
        if (this.advancementFile.isFile()) {
            try (JsonReader jsonReader = new JsonReader((Reader)new StringReader(Files.toString((File)this.advancementFile, (Charset)StandardCharsets.UTF_8)));){
                jsonReader.setLenient(false);
                Dynamic dynamic = new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)Streams.parse((JsonReader)jsonReader));
                if (!dynamic.get("DataVersion").asNumber().result().isPresent()) {
                    dynamic = dynamic.set("DataVersion", dynamic.createInt(1343));
                }
                dynamic = this.dataFixer.update(DataFixTypes.ADVANCEMENTS.getTypeReference(), dynamic, dynamic.get("DataVersion").asInt(0), SharedConstants.getGameVersion().getWorldVersion());
                dynamic = dynamic.remove("DataVersion");
                Map map = (Map)GSON.getAdapter(JSON_TYPE).fromJsonTree((JsonElement)dynamic.getValue());
                if (map == null) {
                    throw new JsonParseException("Found null for advancements");
                }
                Stream<Map.Entry> stream = map.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue));
                for (Map.Entry entry : stream.collect(Collectors.toList())) {
                    Advancement advancement = advancementLoader.get((Identifier)entry.getKey());
                    if (advancement == null) {
                        LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", entry.getKey(), (Object)this.advancementFile);
                        continue;
                    }
                    this.initProgress(advancement, (AdvancementProgress)entry.getValue());
                }
            }
            catch (JsonParseException jsonParseException) {
                LOGGER.error("Couldn't parse player advancements in {}", (Object)this.advancementFile, (Object)jsonParseException);
            }
            catch (IOException iOException) {
                LOGGER.error("Couldn't access player advancements in {}", (Object)this.advancementFile, (Object)iOException);
            }
        }
        this.rewardEmptyAdvancements(advancementLoader);
        this.updateCompleted();
        this.beginTrackingAllAdvancements(advancementLoader);
    }

    public void save() {
        HashMap map = Maps.newHashMap();
        for (Map.Entry<Advancement, AdvancementProgress> entry : this.advancementToProgress.entrySet()) {
            AdvancementProgress advancementProgress = entry.getValue();
            if (!advancementProgress.isAnyObtained()) continue;
            map.put(entry.getKey().getId(), advancementProgress);
        }
        if (this.advancementFile.getParentFile() != null) {
            this.advancementFile.getParentFile().mkdirs();
        }
        JsonElement jsonElement = GSON.toJsonTree((Object)map);
        jsonElement.getAsJsonObject().addProperty("DataVersion", (Number)SharedConstants.getGameVersion().getWorldVersion());
        try (FileOutputStream outputStream = new FileOutputStream(this.advancementFile);
             OutputStreamWriter writer = new OutputStreamWriter((OutputStream)outputStream, Charsets.UTF_8.newEncoder());){
            GSON.toJson(jsonElement, (Appendable)writer);
        }
        catch (IOException iOException) {
            LOGGER.error("Couldn't save player advancements to {}", (Object)this.advancementFile, (Object)iOException);
        }
    }

    public boolean grantCriterion(Advancement advancement, String criterionName) {
        boolean bl = false;
        AdvancementProgress advancementProgress = this.getProgress(advancement);
        boolean bl2 = advancementProgress.isDone();
        if (advancementProgress.obtain(criterionName)) {
            this.endTrackingCompleted(advancement);
            this.progressUpdates.add(advancement);
            bl = true;
            if (!bl2 && advancementProgress.isDone()) {
                advancement.getRewards().apply(this.owner);
                if (advancement.getDisplay() != null && advancement.getDisplay().shouldAnnounceToChat() && this.owner.world.getGameRules().getBoolean(GameRules.ANNOUNCE_ADVANCEMENTS)) {
                    this.playerManager.broadcast(new TranslatableText("chat.type.advancement." + advancement.getDisplay().getFrame().getId(), this.owner.getDisplayName(), advancement.toHoverableText()), MessageType.SYSTEM, Util.NIL_UUID);
                }
            }
        }
        if (advancementProgress.isDone()) {
            this.updateDisplay(advancement);
        }
        return bl;
    }

    public boolean revokeCriterion(Advancement advancement, String criterionName) {
        boolean bl = false;
        AdvancementProgress advancementProgress = this.getProgress(advancement);
        if (advancementProgress.reset(criterionName)) {
            this.beginTracking(advancement);
            this.progressUpdates.add(advancement);
            bl = true;
        }
        if (!advancementProgress.isAnyObtained()) {
            this.updateDisplay(advancement);
        }
        return bl;
    }

    private void beginTracking(Advancement advancement) {
        AdvancementProgress advancementProgress = this.getProgress(advancement);
        if (advancementProgress.isDone()) {
            return;
        }
        for (Map.Entry<String, AdvancementCriterion> entry : advancement.getCriteria().entrySet()) {
            Criterion<CriterionConditions> criterion;
            CriterionConditions criterionConditions;
            CriterionProgress criterionProgress = advancementProgress.getCriterionProgress(entry.getKey());
            if (criterionProgress == null || criterionProgress.isObtained() || (criterionConditions = entry.getValue().getConditions()) == null || (criterion = Criteria.getById(criterionConditions.getId())) == null) continue;
            criterion.beginTrackingCondition(this, new Criterion.ConditionsContainer<CriterionConditions>(criterionConditions, advancement, entry.getKey()));
        }
    }

    private void endTrackingCompleted(Advancement advancement) {
        AdvancementProgress advancementProgress = this.getProgress(advancement);
        for (Map.Entry<String, AdvancementCriterion> entry : advancement.getCriteria().entrySet()) {
            Criterion<CriterionConditions> criterion;
            CriterionConditions criterionConditions;
            CriterionProgress criterionProgress = advancementProgress.getCriterionProgress(entry.getKey());
            if (criterionProgress == null || !criterionProgress.isObtained() && !advancementProgress.isDone() || (criterionConditions = entry.getValue().getConditions()) == null || (criterion = Criteria.getById(criterionConditions.getId())) == null) continue;
            criterion.endTrackingCondition(this, new Criterion.ConditionsContainer<CriterionConditions>(criterionConditions, advancement, entry.getKey()));
        }
    }

    public void sendUpdate(ServerPlayerEntity player) {
        if (this.dirty || !this.visibilityUpdates.isEmpty() || !this.progressUpdates.isEmpty()) {
            HashMap map = Maps.newHashMap();
            LinkedHashSet set = Sets.newLinkedHashSet();
            LinkedHashSet set2 = Sets.newLinkedHashSet();
            for (Advancement advancement : this.progressUpdates) {
                if (!this.visibleAdvancements.contains(advancement)) continue;
                map.put(advancement.getId(), this.advancementToProgress.get(advancement));
            }
            for (Advancement advancement : this.visibilityUpdates) {
                if (this.visibleAdvancements.contains(advancement)) {
                    set.add(advancement);
                    continue;
                }
                set2.add(advancement.getId());
            }
            if (this.dirty || !map.isEmpty() || !set.isEmpty() || !set2.isEmpty()) {
                player.networkHandler.sendPacket(new AdvancementUpdateS2CPacket(this.dirty, set, set2, map));
                this.visibilityUpdates.clear();
                this.progressUpdates.clear();
            }
        }
        this.dirty = false;
    }

    public void setDisplayTab(@Nullable Advancement advancement) {
        Advancement advancement2 = this.currentDisplayTab;
        this.currentDisplayTab = advancement != null && advancement.getParent() == null && advancement.getDisplay() != null ? advancement : null;
        if (advancement2 != this.currentDisplayTab) {
            this.owner.networkHandler.sendPacket(new SelectAdvancementTabS2CPacket(this.currentDisplayTab == null ? null : this.currentDisplayTab.getId()));
        }
    }

    public AdvancementProgress getProgress(Advancement advancement) {
        AdvancementProgress advancementProgress = this.advancementToProgress.get(advancement);
        if (advancementProgress == null) {
            advancementProgress = new AdvancementProgress();
            this.initProgress(advancement, advancementProgress);
        }
        return advancementProgress;
    }

    private void initProgress(Advancement advancement, AdvancementProgress progress) {
        progress.init(advancement.getCriteria(), advancement.getRequirements());
        this.advancementToProgress.put(advancement, progress);
    }

    private void updateDisplay(Advancement advancement) {
        boolean bl = this.canSee(advancement);
        boolean bl2 = this.visibleAdvancements.contains(advancement);
        if (bl && !bl2) {
            this.visibleAdvancements.add(advancement);
            this.visibilityUpdates.add(advancement);
            if (this.advancementToProgress.containsKey(advancement)) {
                this.progressUpdates.add(advancement);
            }
        } else if (!bl && bl2) {
            this.visibleAdvancements.remove(advancement);
            this.visibilityUpdates.add(advancement);
        }
        if (bl != bl2 && advancement.getParent() != null) {
            this.updateDisplay(advancement.getParent());
        }
        for (Advancement advancement2 : advancement.getChildren()) {
            this.updateDisplay(advancement2);
        }
    }

    private boolean canSee(Advancement advancement) {
        for (int i = 0; advancement != null && i <= 2; advancement = advancement.getParent(), ++i) {
            if (i == 0 && this.hasChildrenDone(advancement)) {
                return true;
            }
            if (advancement.getDisplay() == null) {
                return false;
            }
            AdvancementProgress advancementProgress = this.getProgress(advancement);
            if (advancementProgress.isDone()) {
                return true;
            }
            if (!advancement.getDisplay().isHidden()) continue;
            return false;
        }
        return false;
    }

    private boolean hasChildrenDone(Advancement advancement) {
        AdvancementProgress advancementProgress = this.getProgress(advancement);
        if (advancementProgress.isDone()) {
            return true;
        }
        for (Advancement advancement2 : advancement.getChildren()) {
            if (!this.hasChildrenDone(advancement2)) continue;
            return true;
        }
        return false;
    }
}

