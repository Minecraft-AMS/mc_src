/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ComparisonChain
 *  com.google.common.collect.Ordering
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.hud;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PlayerListHud
extends DrawableHelper {
    private static final Ordering<PlayerListEntry> ENTRY_ORDERING = Ordering.from((Comparator)new EntryOrderComparator());
    private final MinecraftClient client;
    private final InGameHud inGameHud;
    private Text footer;
    private Text header;
    private long showTime;
    private boolean visible;

    public PlayerListHud(MinecraftClient client, InGameHud inGameHud) {
        this.client = client;
        this.inGameHud = inGameHud;
    }

    public Text getPlayerName(PlayerListEntry entry) {
        if (entry.getDisplayName() != null) {
            return this.applyGameModeFormatting(entry, entry.getDisplayName().shallowCopy());
        }
        return this.applyGameModeFormatting(entry, Team.decorateName(entry.getScoreboardTeam(), new LiteralText(entry.getProfile().getName())));
    }

    private Text applyGameModeFormatting(PlayerListEntry entry, MutableText name) {
        return entry.getGameMode() == GameMode.SPECTATOR ? name.formatted(Formatting.ITALIC) : name;
    }

    public void tick(boolean visible) {
        if (visible && !this.visible) {
            this.showTime = Util.getMeasuringTimeMs();
        }
        this.visible = visible;
    }

    public void render(MatrixStack matrices, int i, Scoreboard scoreboard, @Nullable ScoreboardObjective objective) {
        int w;
        int t;
        boolean bl;
        int m;
        int l;
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.player.networkHandler;
        List list = ENTRY_ORDERING.sortedCopy(clientPlayNetworkHandler.getPlayerList());
        int j = 0;
        int k = 0;
        for (PlayerListEntry playerListEntry : list) {
            l = this.client.textRenderer.getWidth(this.getPlayerName(playerListEntry));
            j = Math.max(j, l);
            if (objective == null || objective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) continue;
            l = this.client.textRenderer.getWidth(" " + scoreboard.getPlayerScore(playerListEntry.getProfile().getName(), objective).getScore());
            k = Math.max(k, l);
        }
        list = list.subList(0, Math.min(list.size(), 80));
        int n = m = list.size();
        l = 1;
        while (n > 20) {
            n = (m + ++l - 1) / l;
        }
        boolean bl2 = bl = this.client.isInSingleplayer() || this.client.getNetworkHandler().getConnection().isEncrypted();
        int o = objective != null ? (objective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS ? 90 : k) : 0;
        int p = Math.min(l * ((bl ? 9 : 0) + j + o + 13), i - 50) / l;
        int q = i / 2 - (p * l + (l - 1) * 5) / 2;
        int r = 10;
        int s = p * l + (l - 1) * 5;
        List<OrderedText> list2 = null;
        if (this.header != null) {
            list2 = this.client.textRenderer.wrapLines(this.header, i - 50);
            for (OrderedText orderedText : list2) {
                s = Math.max(s, this.client.textRenderer.getWidth(orderedText));
            }
        }
        List<OrderedText> list3 = null;
        if (this.footer != null) {
            list3 = this.client.textRenderer.wrapLines(this.footer, i - 50);
            for (OrderedText orderedText2 : list3) {
                s = Math.max(s, this.client.textRenderer.getWidth(orderedText2));
            }
        }
        if (list2 != null) {
            PlayerListHud.fill(matrices, i / 2 - s / 2 - 1, r - 1, i / 2 + s / 2 + 1, r + list2.size() * this.client.textRenderer.fontHeight, Integer.MIN_VALUE);
            for (OrderedText orderedText2 : list2) {
                t = this.client.textRenderer.getWidth(orderedText2);
                this.client.textRenderer.drawWithShadow(matrices, orderedText2, (float)(i / 2 - t / 2), (float)r, -1);
                r += this.client.textRenderer.fontHeight;
            }
            ++r;
        }
        PlayerListHud.fill(matrices, i / 2 - s / 2 - 1, r - 1, i / 2 + s / 2 + 1, r + n * 9, Integer.MIN_VALUE);
        int n2 = this.client.options.getTextBackgroundColor(0x20FFFFFF);
        for (int v = 0; v < m; ++v) {
            int ad;
            int ae;
            t = v / n;
            w = v % n;
            int x = q + t * p + t * 5;
            int y = r + w * 9;
            PlayerListHud.fill(matrices, x, y, x + p, y + 8, n2);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.enableAlphaTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            if (v >= list.size()) continue;
            PlayerListEntry playerListEntry2 = (PlayerListEntry)list.get(v);
            GameProfile gameProfile = playerListEntry2.getProfile();
            if (bl) {
                PlayerEntity playerEntity = this.client.world.getPlayerByUuid(gameProfile.getId());
                boolean bl22 = playerEntity != null && playerEntity.isPartVisible(PlayerModelPart.CAPE) && ("Dinnerbone".equals(gameProfile.getName()) || "Grumm".equals(gameProfile.getName()));
                this.client.getTextureManager().bindTexture(playerListEntry2.getSkinTexture());
                int z = 8 + (bl22 ? 8 : 0);
                int aa = 8 * (bl22 ? -1 : 1);
                DrawableHelper.drawTexture(matrices, x, y, 8, 8, 8.0f, z, 8, aa, 64, 64);
                if (playerEntity != null && playerEntity.isPartVisible(PlayerModelPart.HAT)) {
                    int ab = 8 + (bl22 ? 8 : 0);
                    int ac = 8 * (bl22 ? -1 : 1);
                    DrawableHelper.drawTexture(matrices, x, y, 8, 8, 40.0f, ab, 8, ac, 64, 64);
                }
                x += 9;
            }
            this.client.textRenderer.drawWithShadow(matrices, this.getPlayerName(playerListEntry2), (float)x, (float)y, playerListEntry2.getGameMode() == GameMode.SPECTATOR ? -1862270977 : -1);
            if (objective != null && playerListEntry2.getGameMode() != GameMode.SPECTATOR && (ae = (ad = x + j + 1) + o) - ad > 5) {
                this.renderScoreboardObjective(objective, y, gameProfile.getName(), ad, ae, playerListEntry2, matrices);
            }
            this.renderLatencyIcon(matrices, p, x - (bl ? 9 : 0), y, playerListEntry2);
        }
        if (list3 != null) {
            PlayerListHud.fill(matrices, i / 2 - s / 2 - 1, (r += n * 9 + 1) - 1, i / 2 + s / 2 + 1, r + list3.size() * this.client.textRenderer.fontHeight, Integer.MIN_VALUE);
            for (OrderedText orderedText3 : list3) {
                w = this.client.textRenderer.getWidth(orderedText3);
                this.client.textRenderer.drawWithShadow(matrices, orderedText3, (float)(i / 2 - w / 2), (float)r, -1);
                r += this.client.textRenderer.fontHeight;
            }
        }
    }

    protected void renderLatencyIcon(MatrixStack matrices, int i, int j, int k, PlayerListEntry entry) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(GUI_ICONS_TEXTURE);
        boolean l = false;
        int m = entry.getLatency() < 0 ? 5 : (entry.getLatency() < 150 ? 0 : (entry.getLatency() < 300 ? 1 : (entry.getLatency() < 600 ? 2 : (entry.getLatency() < 1000 ? 3 : 4))));
        this.setZOffset(this.getZOffset() + 100);
        this.drawTexture(matrices, j + i - 11, k, 0, 176 + m * 8, 10, 8);
        this.setZOffset(this.getZOffset() - 100);
    }

    private void renderScoreboardObjective(ScoreboardObjective objective, int i, String string, int j, int k, PlayerListEntry entry, MatrixStack matrices) {
        int l = objective.getScoreboard().getPlayerScore(string, objective).getScore();
        if (objective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
            boolean bl;
            this.client.getTextureManager().bindTexture(GUI_ICONS_TEXTURE);
            long m = Util.getMeasuringTimeMs();
            if (this.showTime == entry.method_2976()) {
                if (l < entry.method_2973()) {
                    entry.method_2978(m);
                    entry.method_2975(this.inGameHud.getTicks() + 20);
                } else if (l > entry.method_2973()) {
                    entry.method_2978(m);
                    entry.method_2975(this.inGameHud.getTicks() + 10);
                }
            }
            if (m - entry.method_2974() > 1000L || this.showTime != entry.method_2976()) {
                entry.method_2972(l);
                entry.method_2965(l);
                entry.method_2978(m);
            }
            entry.method_2964(this.showTime);
            entry.method_2972(l);
            int n = MathHelper.ceil((float)Math.max(l, entry.method_2960()) / 2.0f);
            int o = Math.max(MathHelper.ceil(l / 2), Math.max(MathHelper.ceil(entry.method_2960() / 2), 10));
            boolean bl2 = bl = entry.method_2961() > (long)this.inGameHud.getTicks() && (entry.method_2961() - (long)this.inGameHud.getTicks()) / 3L % 2L == 1L;
            if (n > 0) {
                int p = MathHelper.floor(Math.min((float)(k - j - 4) / (float)o, 9.0f));
                if (p > 3) {
                    int q;
                    for (q = n; q < o; ++q) {
                        this.drawTexture(matrices, j + q * p, i, bl ? 25 : 16, 0, 9, 9);
                    }
                    for (q = 0; q < n; ++q) {
                        this.drawTexture(matrices, j + q * p, i, bl ? 25 : 16, 0, 9, 9);
                        if (bl) {
                            if (q * 2 + 1 < entry.method_2960()) {
                                this.drawTexture(matrices, j + q * p, i, 70, 0, 9, 9);
                            }
                            if (q * 2 + 1 == entry.method_2960()) {
                                this.drawTexture(matrices, j + q * p, i, 79, 0, 9, 9);
                            }
                        }
                        if (q * 2 + 1 < l) {
                            this.drawTexture(matrices, j + q * p, i, q >= 10 ? 160 : 52, 0, 9, 9);
                        }
                        if (q * 2 + 1 != l) continue;
                        this.drawTexture(matrices, j + q * p, i, q >= 10 ? 169 : 61, 0, 9, 9);
                    }
                } else {
                    float f = MathHelper.clamp((float)l / 20.0f, 0.0f, 1.0f);
                    int r = (int)((1.0f - f) * 255.0f) << 16 | (int)(f * 255.0f) << 8;
                    String string2 = "" + (float)l / 2.0f;
                    if (k - this.client.textRenderer.getWidth(string2 + "hp") >= j) {
                        string2 = string2 + "hp";
                    }
                    this.client.textRenderer.drawWithShadow(matrices, string2, (float)((k + j) / 2 - this.client.textRenderer.getWidth(string2) / 2), (float)i, r);
                }
            }
        } else {
            String string3 = (Object)((Object)Formatting.YELLOW) + "" + l;
            this.client.textRenderer.drawWithShadow(matrices, string3, (float)(k - this.client.textRenderer.getWidth(string3)), (float)i, 0xFFFFFF);
        }
    }

    public void setFooter(@Nullable Text footer) {
        this.footer = footer;
    }

    public void setHeader(@Nullable Text header) {
        this.header = header;
    }

    public void clear() {
        this.header = null;
        this.footer = null;
    }

    @Environment(value=EnvType.CLIENT)
    static class EntryOrderComparator
    implements Comparator<PlayerListEntry> {
        private EntryOrderComparator() {
        }

        @Override
        public int compare(PlayerListEntry playerListEntry, PlayerListEntry playerListEntry2) {
            Team team = playerListEntry.getScoreboardTeam();
            Team team2 = playerListEntry2.getScoreboardTeam();
            return ComparisonChain.start().compareTrueFirst(playerListEntry.getGameMode() != GameMode.SPECTATOR, playerListEntry2.getGameMode() != GameMode.SPECTATOR).compare((Comparable)((Object)(team != null ? team.getName() : "")), (Comparable)((Object)(team2 != null ? team2.getName() : ""))).compare((Object)playerListEntry.getProfile().getName(), (Object)playerListEntry2.getProfile().getName(), String::compareToIgnoreCase).result();
        }

        @Override
        public /* synthetic */ int compare(Object object, Object object2) {
            return this.compare((PlayerListEntry)object, (PlayerListEntry)object2);
        }
    }
}

