/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.base.Strings;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListEntry;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsScreen;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.report.log.ChatLog;
import net.minecraft.client.report.log.ChatLogEntry;
import net.minecraft.client.report.log.ReceivedMessage;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SocialInteractionsPlayerListWidget
extends ElementListWidget<SocialInteractionsPlayerListEntry> {
    private final SocialInteractionsScreen parent;
    private final List<SocialInteractionsPlayerListEntry> players = Lists.newArrayList();
    @Nullable
    private String currentSearch;

    public SocialInteractionsPlayerListWidget(SocialInteractionsScreen parent, MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
        this.parent = parent;
        this.setRenderBackground(false);
        this.setRenderHorizontalShadows(false);
    }

    @Override
    protected void enableScissor(DrawContext context) {
        context.enableScissor(this.left, this.top + 4, this.right, this.bottom);
    }

    public void update(Collection<UUID> uuids, double scrollAmount, boolean includeOffline) {
        HashMap<UUID, SocialInteractionsPlayerListEntry> map = new HashMap<UUID, SocialInteractionsPlayerListEntry>();
        this.setPlayers(uuids, map);
        this.markOfflineMembers(map, includeOffline);
        this.refresh(map.values(), scrollAmount);
    }

    private void setPlayers(Collection<UUID> playerUuids, Map<UUID, SocialInteractionsPlayerListEntry> entriesByUuids) {
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
        for (UUID uUID : playerUuids) {
            PlayerListEntry playerListEntry = clientPlayNetworkHandler.getPlayerListEntry(uUID);
            if (playerListEntry == null) continue;
            boolean bl = playerListEntry.hasPublicKey();
            entriesByUuids.put(uUID, new SocialInteractionsPlayerListEntry(this.client, this.parent, uUID, playerListEntry.getProfile().getName(), playerListEntry::getSkinTexture, bl));
        }
    }

    private void markOfflineMembers(Map<UUID, SocialInteractionsPlayerListEntry> entries, boolean includeOffline) {
        Collection<GameProfile> collection = SocialInteractionsPlayerListWidget.collectReportableProfiles(this.client.getAbuseReportContext().getChatLog());
        for (GameProfile gameProfile : collection) {
            SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry;
            if (includeOffline) {
                socialInteractionsPlayerListEntry = entries.computeIfAbsent(gameProfile.getId(), uuid -> {
                    SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry = new SocialInteractionsPlayerListEntry(this.client, this.parent, gameProfile.getId(), gameProfile.getName(), (Supplier<Identifier>)Suppliers.memoize(() -> this.client.getSkinProvider().loadSkin(gameProfile)), true);
                    socialInteractionsPlayerListEntry.setOffline(true);
                    return socialInteractionsPlayerListEntry;
                });
            } else {
                socialInteractionsPlayerListEntry = entries.get(gameProfile.getId());
                if (socialInteractionsPlayerListEntry == null) continue;
            }
            socialInteractionsPlayerListEntry.setSentMessage(true);
        }
    }

    private static Collection<GameProfile> collectReportableProfiles(ChatLog log) {
        ObjectLinkedOpenHashSet set = new ObjectLinkedOpenHashSet();
        for (int i = log.getMaxIndex(); i >= log.getMinIndex(); --i) {
            ReceivedMessage.ChatMessage chatMessage;
            ChatLogEntry chatLogEntry = log.get(i);
            if (!(chatLogEntry instanceof ReceivedMessage.ChatMessage) || !(chatMessage = (ReceivedMessage.ChatMessage)chatLogEntry).message().hasSignature()) continue;
            set.add(chatMessage.profile());
        }
        return set;
    }

    private void sortPlayers() {
        this.players.sort(Comparator.comparing(player -> {
            if (player.getUuid().equals(this.client.getSession().getUuidOrNull())) {
                return 0;
            }
            if (player.getUuid().version() == 2) {
                return 4;
            }
            if (this.client.getAbuseReportContext().draftPlayerUuidEquals(player.getUuid())) {
                return 1;
            }
            if (player.hasSentMessage()) {
                return 2;
            }
            return 3;
        }).thenComparing(player -> {
            int i;
            if (!player.getName().isBlank() && ((i = player.getName().codePointAt(0)) == 95 || i >= 97 && i <= 122 || i >= 65 && i <= 90 || i >= 48 && i <= 57)) {
                return 0;
            }
            return 1;
        }).thenComparing(SocialInteractionsPlayerListEntry::getName, String::compareToIgnoreCase));
    }

    private void refresh(Collection<SocialInteractionsPlayerListEntry> players, double scrollAmount) {
        this.players.clear();
        this.players.addAll(players);
        this.sortPlayers();
        this.filterPlayers();
        this.replaceEntries(this.players);
        this.setScrollAmount(scrollAmount);
    }

    private void filterPlayers() {
        if (this.currentSearch != null) {
            this.players.removeIf(player -> !player.getName().toLowerCase(Locale.ROOT).contains(this.currentSearch));
            this.replaceEntries(this.players);
        }
    }

    public void setCurrentSearch(String currentSearch) {
        this.currentSearch = currentSearch;
    }

    public boolean isEmpty() {
        return this.players.isEmpty();
    }

    public void setPlayerOnline(PlayerListEntry player, SocialInteractionsScreen.Tab tab) {
        UUID uUID = player.getProfile().getId();
        for (SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry : this.players) {
            if (!socialInteractionsPlayerListEntry.getUuid().equals(uUID)) continue;
            socialInteractionsPlayerListEntry.setOffline(false);
            return;
        }
        if ((tab == SocialInteractionsScreen.Tab.ALL || this.client.getSocialInteractionsManager().isPlayerMuted(uUID)) && (Strings.isNullOrEmpty((String)this.currentSearch) || player.getProfile().getName().toLowerCase(Locale.ROOT).contains(this.currentSearch))) {
            SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry;
            boolean bl = player.hasPublicKey();
            socialInteractionsPlayerListEntry = new SocialInteractionsPlayerListEntry(this.client, this.parent, player.getProfile().getId(), player.getProfile().getName(), player::getSkinTexture, bl);
            this.addEntry(socialInteractionsPlayerListEntry);
            this.players.add(socialInteractionsPlayerListEntry);
        }
    }

    public void setPlayerOffline(UUID uuid) {
        for (SocialInteractionsPlayerListEntry socialInteractionsPlayerListEntry : this.players) {
            if (!socialInteractionsPlayerListEntry.getUuid().equals(uuid)) continue;
            socialInteractionsPlayerListEntry.setOffline(true);
            return;
        }
    }
}

