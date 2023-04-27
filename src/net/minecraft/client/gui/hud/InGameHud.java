/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Ordering
 *  com.mojang.datafixers.util.Pair
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.hud;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.gui.hud.SubtitlesHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameMode;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class InGameHud {
    private static final Identifier VIGNETTE_TEXTURE = new Identifier("textures/misc/vignette.png");
    private static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");
    private static final Identifier PUMPKIN_BLUR = new Identifier("textures/misc/pumpkinblur.png");
    private static final Identifier SPYGLASS_SCOPE = new Identifier("textures/misc/spyglass_scope.png");
    private static final Identifier POWDER_SNOW_OUTLINE = new Identifier("textures/misc/powder_snow_outline.png");
    private static final Identifier ICONS = new Identifier("textures/gui/icons.png");
    private static final Text DEMO_EXPIRED_MESSAGE = Text.translatable("demo.demoExpired");
    private static final Text SAVING_LEVEL_TEXT = Text.translatable("menu.savingLevel");
    private static final int WHITE = 0xFFFFFF;
    private static final float field_32168 = 5.0f;
    private static final int field_32169 = 10;
    private static final int field_32170 = 10;
    private static final String SCOREBOARD_JOINER = ": ";
    private static final float field_32172 = 0.2f;
    private static final int field_33942 = 9;
    private static final int field_33943 = 8;
    private static final float field_35431 = 0.2f;
    private final Random random = Random.create();
    private final MinecraftClient client;
    private final ItemRenderer itemRenderer;
    private final ChatHud chatHud;
    private int ticks;
    @Nullable
    private Text overlayMessage;
    private int overlayRemaining;
    private boolean overlayTinted;
    private boolean canShowChatDisabledScreen;
    public float vignetteDarkness = 1.0f;
    private int heldItemTooltipFade;
    private ItemStack currentStack = ItemStack.EMPTY;
    private final DebugHud debugHud;
    private final SubtitlesHud subtitlesHud;
    private final SpectatorHud spectatorHud;
    private final PlayerListHud playerListHud;
    private final BossBarHud bossBarHud;
    private int titleRemainTicks;
    @Nullable
    private Text title;
    @Nullable
    private Text subtitle;
    private int titleFadeInTicks;
    private int titleStayTicks;
    private int titleFadeOutTicks;
    private int lastHealthValue;
    private int renderHealthValue;
    private long lastHealthCheckTime;
    private long heartJumpEndTick;
    private int scaledWidth;
    private int scaledHeight;
    private float autosaveIndicatorAlpha;
    private float lastAutosaveIndicatorAlpha;
    private float spyglassScale;

    public InGameHud(MinecraftClient client, ItemRenderer itemRenderer) {
        this.client = client;
        this.itemRenderer = itemRenderer;
        this.debugHud = new DebugHud(client);
        this.spectatorHud = new SpectatorHud(client);
        this.chatHud = new ChatHud(client);
        this.playerListHud = new PlayerListHud(client, this);
        this.bossBarHud = new BossBarHud(client);
        this.subtitlesHud = new SubtitlesHud(client);
        this.setDefaultTitleFade();
    }

    public void setDefaultTitleFade() {
        this.titleFadeInTicks = 10;
        this.titleStayTicks = 70;
        this.titleFadeOutTicks = 20;
    }

    public void render(DrawContext context, float tickDelta) {
        int k;
        float g;
        Window window = this.client.getWindow();
        this.scaledWidth = window.getScaledWidth();
        this.scaledHeight = window.getScaledHeight();
        TextRenderer textRenderer = this.getTextRenderer();
        RenderSystem.enableBlend();
        if (MinecraftClient.isFancyGraphicsOrBetter()) {
            this.renderVignetteOverlay(context, this.client.getCameraEntity());
        } else {
            RenderSystem.enableDepthTest();
        }
        float f = this.client.getLastFrameDuration();
        this.spyglassScale = MathHelper.lerp(0.5f * f, this.spyglassScale, 1.125f);
        if (this.client.options.getPerspective().isFirstPerson()) {
            if (this.client.player.isUsingSpyglass()) {
                this.renderSpyglassOverlay(context, this.spyglassScale);
            } else {
                this.spyglassScale = 0.5f;
                ItemStack itemStack = this.client.player.getInventory().getArmorStack(3);
                if (itemStack.isOf(Blocks.CARVED_PUMPKIN.asItem())) {
                    this.renderOverlay(context, PUMPKIN_BLUR, 1.0f);
                }
            }
        }
        if (this.client.player.getFrozenTicks() > 0) {
            this.renderOverlay(context, POWDER_SNOW_OUTLINE, this.client.player.getFreezingScale());
        }
        if ((g = MathHelper.lerp(tickDelta, this.client.player.lastNauseaStrength, this.client.player.nextNauseaStrength)) > 0.0f && !this.client.player.hasStatusEffect(StatusEffects.NAUSEA)) {
            this.renderPortalOverlay(context, g);
        }
        if (this.client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
            this.spectatorHud.renderSpectatorMenu(context);
        } else if (!this.client.options.hudHidden) {
            this.renderHotbar(tickDelta, context);
        }
        if (!this.client.options.hudHidden) {
            RenderSystem.enableBlend();
            this.renderCrosshair(context);
            this.client.getProfiler().push("bossHealth");
            this.bossBarHud.render(context);
            this.client.getProfiler().pop();
            if (this.client.interactionManager.hasStatusBars()) {
                this.renderStatusBars(context);
            }
            this.renderMountHealth(context);
            RenderSystem.disableBlend();
            int i = this.scaledWidth / 2 - 91;
            JumpingMount jumpingMount = this.client.player.getJumpingMount();
            if (jumpingMount != null) {
                this.renderMountJumpBar(jumpingMount, context, i);
            } else if (this.client.interactionManager.hasExperienceBar()) {
                this.renderExperienceBar(context, i);
            }
            if (this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR) {
                this.renderHeldItemTooltip(context);
            } else if (this.client.player.isSpectator()) {
                this.spectatorHud.render(context);
            }
        }
        if (this.client.player.getSleepTimer() > 0) {
            this.client.getProfiler().push("sleep");
            RenderSystem.disableDepthTest();
            float h = this.client.player.getSleepTimer();
            float j = h / 100.0f;
            if (j > 1.0f) {
                j = 1.0f - (h - 100.0f) / 10.0f;
            }
            k = (int)(220.0f * j) << 24 | 0x101020;
            context.fill(0, 0, this.scaledWidth, this.scaledHeight, k);
            RenderSystem.enableDepthTest();
            this.client.getProfiler().pop();
        }
        if (this.client.isDemo()) {
            this.renderDemoTimer(context);
        }
        this.renderStatusEffectOverlay(context);
        if (this.client.options.debugEnabled) {
            this.debugHud.render(context);
        }
        if (!this.client.options.hudHidden) {
            ScoreboardObjective scoreboardObjective2;
            int n;
            int m;
            if (this.overlayMessage != null && this.overlayRemaining > 0) {
                this.client.getProfiler().push("overlayMessage");
                float h = (float)this.overlayRemaining - tickDelta;
                int l = (int)(h * 255.0f / 20.0f);
                if (l > 255) {
                    l = 255;
                }
                if (l > 8) {
                    context.getMatrices().push();
                    context.getMatrices().translate(this.scaledWidth / 2, this.scaledHeight - 68, 0.0f);
                    k = 0xFFFFFF;
                    if (this.overlayTinted) {
                        k = MathHelper.hsvToRgb(h / 50.0f, 0.7f, 0.6f) & 0xFFFFFF;
                    }
                    m = l << 24 & 0xFF000000;
                    n = textRenderer.getWidth(this.overlayMessage);
                    this.drawTextBackground(context, textRenderer, -4, n, 0xFFFFFF | m);
                    context.drawTextWithShadow(textRenderer, this.overlayMessage, -n / 2, -4, k | m);
                    context.getMatrices().pop();
                }
                this.client.getProfiler().pop();
            }
            if (this.title != null && this.titleRemainTicks > 0) {
                this.client.getProfiler().push("titleAndSubtitle");
                float h = (float)this.titleRemainTicks - tickDelta;
                int l = 255;
                if (this.titleRemainTicks > this.titleFadeOutTicks + this.titleStayTicks) {
                    float o = (float)(this.titleFadeInTicks + this.titleStayTicks + this.titleFadeOutTicks) - h;
                    l = (int)(o * 255.0f / (float)this.titleFadeInTicks);
                }
                if (this.titleRemainTicks <= this.titleFadeOutTicks) {
                    l = (int)(h * 255.0f / (float)this.titleFadeOutTicks);
                }
                if ((l = MathHelper.clamp(l, 0, 255)) > 8) {
                    context.getMatrices().push();
                    context.getMatrices().translate(this.scaledWidth / 2, this.scaledHeight / 2, 0.0f);
                    RenderSystem.enableBlend();
                    context.getMatrices().push();
                    context.getMatrices().scale(4.0f, 4.0f, 4.0f);
                    int k2 = l << 24 & 0xFF000000;
                    m = textRenderer.getWidth(this.title);
                    this.drawTextBackground(context, textRenderer, -10, m, 0xFFFFFF | k2);
                    context.drawTextWithShadow(textRenderer, this.title, -m / 2, -10, 0xFFFFFF | k2);
                    context.getMatrices().pop();
                    if (this.subtitle != null) {
                        context.getMatrices().push();
                        context.getMatrices().scale(2.0f, 2.0f, 2.0f);
                        n = textRenderer.getWidth(this.subtitle);
                        this.drawTextBackground(context, textRenderer, 5, n, 0xFFFFFF | k2);
                        context.drawTextWithShadow(textRenderer, this.subtitle, -n / 2, 5, 0xFFFFFF | k2);
                        context.getMatrices().pop();
                    }
                    RenderSystem.disableBlend();
                    context.getMatrices().pop();
                }
                this.client.getProfiler().pop();
            }
            this.subtitlesHud.render(context);
            Scoreboard scoreboard = this.client.world.getScoreboard();
            ScoreboardObjective scoreboardObjective = null;
            Team team = scoreboard.getPlayerTeam(this.client.player.getEntityName());
            if (team != null && (m = team.getColor().getColorIndex()) >= 0) {
                scoreboardObjective = scoreboard.getObjectiveForSlot(3 + m);
            }
            ScoreboardObjective scoreboardObjective3 = scoreboardObjective2 = scoreboardObjective != null ? scoreboardObjective : scoreboard.getObjectiveForSlot(1);
            if (scoreboardObjective2 != null) {
                this.renderScoreboardSidebar(context, scoreboardObjective2);
            }
            RenderSystem.enableBlend();
            n = MathHelper.floor(this.client.mouse.getX() * (double)window.getScaledWidth() / (double)window.getWidth());
            int p = MathHelper.floor(this.client.mouse.getY() * (double)window.getScaledHeight() / (double)window.getHeight());
            this.client.getProfiler().push("chat");
            this.chatHud.render(context, this.ticks, n, p);
            this.client.getProfiler().pop();
            scoreboardObjective2 = scoreboard.getObjectiveForSlot(0);
            if (this.client.options.playerListKey.isPressed() && (!this.client.isInSingleplayer() || this.client.player.networkHandler.getListedPlayerListEntries().size() > 1 || scoreboardObjective2 != null)) {
                this.playerListHud.setVisible(true);
                this.playerListHud.render(context, this.scaledWidth, scoreboard, scoreboardObjective2);
            } else {
                this.playerListHud.setVisible(false);
            }
            this.renderAutosaveIndicator(context);
        }
    }

    private void drawTextBackground(DrawContext context, TextRenderer textRenderer, int yOffset, int width, int color) {
        int i = this.client.options.getTextBackgroundColor(0.0f);
        if (i != 0) {
            int j = -width / 2;
            context.fill(j - 2, yOffset - 2, j + width + 2, yOffset + textRenderer.fontHeight + 2, ColorHelper.Argb.mixColor(i, color));
        }
    }

    private void renderCrosshair(DrawContext context) {
        GameOptions gameOptions = this.client.options;
        if (!gameOptions.getPerspective().isFirstPerson()) {
            return;
        }
        if (this.client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR && !this.shouldRenderSpectatorCrosshair(this.client.crosshairTarget)) {
            return;
        }
        if (gameOptions.debugEnabled && !gameOptions.hudHidden && !this.client.player.hasReducedDebugInfo() && !gameOptions.getReducedDebugInfo().getValue().booleanValue()) {
            Camera camera = this.client.gameRenderer.getCamera();
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            matrixStack.push();
            matrixStack.multiplyPositionMatrix(context.getMatrices().peek().getPositionMatrix());
            matrixStack.translate(this.scaledWidth / 2, this.scaledHeight / 2, 0.0f);
            matrixStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(camera.getPitch()));
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw()));
            matrixStack.scale(-1.0f, -1.0f, -1.0f);
            RenderSystem.applyModelViewMatrix();
            RenderSystem.renderCrosshair(10);
            matrixStack.pop();
            RenderSystem.applyModelViewMatrix();
        } else {
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
            int i = 15;
            context.drawTexture(ICONS, (this.scaledWidth - 15) / 2, (this.scaledHeight - 15) / 2, 0, 0, 15, 15);
            if (this.client.options.getAttackIndicator().getValue() == AttackIndicator.CROSSHAIR) {
                float f = this.client.player.getAttackCooldownProgress(0.0f);
                boolean bl = false;
                if (this.client.targetedEntity != null && this.client.targetedEntity instanceof LivingEntity && f >= 1.0f) {
                    bl = this.client.player.getAttackCooldownProgressPerTick() > 5.0f;
                    bl &= this.client.targetedEntity.isAlive();
                }
                int j = this.scaledHeight / 2 - 7 + 16;
                int k = this.scaledWidth / 2 - 8;
                if (bl) {
                    context.drawTexture(ICONS, k, j, 68, 94, 16, 16);
                } else if (f < 1.0f) {
                    int l = (int)(f * 17.0f);
                    context.drawTexture(ICONS, k, j, 36, 94, 16, 4);
                    context.drawTexture(ICONS, k, j, 52, 94, l, 4);
                }
            }
            RenderSystem.defaultBlendFunc();
        }
    }

    private boolean shouldRenderSpectatorCrosshair(HitResult hitResult) {
        if (hitResult == null) {
            return false;
        }
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            return ((EntityHitResult)hitResult).getEntity() instanceof NamedScreenHandlerFactory;
        }
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            ClientWorld world = this.client.world;
            BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
            return world.getBlockState(blockPos).createScreenHandlerFactory(world, blockPos) != null;
        }
        return false;
    }

    protected void renderStatusEffectOverlay(DrawContext context) {
        AbstractInventoryScreen abstractInventoryScreen;
        Screen screen;
        Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
        if (collection.isEmpty() || (screen = this.client.currentScreen) instanceof AbstractInventoryScreen && (abstractInventoryScreen = (AbstractInventoryScreen)screen).hideStatusEffectHud()) {
            return;
        }
        RenderSystem.enableBlend();
        int i = 0;
        int j = 0;
        StatusEffectSpriteManager statusEffectSpriteManager = this.client.getStatusEffectSpriteManager();
        ArrayList list = Lists.newArrayListWithExpectedSize((int)collection.size());
        for (StatusEffectInstance statusEffectInstance : Ordering.natural().reverse().sortedCopy(collection)) {
            int n;
            StatusEffect statusEffect = statusEffectInstance.getEffectType();
            if (!statusEffectInstance.shouldShowIcon()) continue;
            int k = this.scaledWidth;
            int l = 1;
            if (this.client.isDemo()) {
                l += 15;
            }
            if (statusEffect.isBeneficial()) {
                k -= 25 * ++i;
            } else {
                k -= 25 * ++j;
                l += 26;
            }
            float f = 1.0f;
            if (statusEffectInstance.isAmbient()) {
                context.drawTexture(HandledScreen.BACKGROUND_TEXTURE, k, l, 165, 166, 24, 24);
            } else {
                context.drawTexture(HandledScreen.BACKGROUND_TEXTURE, k, l, 141, 166, 24, 24);
                if (statusEffectInstance.isDurationBelow(200)) {
                    int m = statusEffectInstance.getDuration();
                    n = 10 - m / 20;
                    f = MathHelper.clamp((float)m / 10.0f / 5.0f * 0.5f, 0.0f, 0.5f) + MathHelper.cos((float)m * (float)Math.PI / 5.0f) * MathHelper.clamp((float)n / 10.0f * 0.25f, 0.0f, 0.25f);
                }
            }
            Sprite sprite = statusEffectSpriteManager.getSprite(statusEffect);
            n = k;
            int o = l;
            float g = f;
            list.add(() -> {
                context.setShaderColor(1.0f, 1.0f, 1.0f, g);
                context.drawSprite(n + 3, o + 3, 0, 18, 18, sprite);
                context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            });
        }
        list.forEach(Runnable::run);
    }

    private void renderHotbar(float tickDelta, DrawContext context) {
        float f;
        int o;
        int n;
        int m;
        PlayerEntity playerEntity = this.getCameraPlayer();
        if (playerEntity == null) {
            return;
        }
        ItemStack itemStack = playerEntity.getOffHandStack();
        Arm arm = playerEntity.getMainArm().getOpposite();
        int i = this.scaledWidth / 2;
        int j = 182;
        int k = 91;
        context.getMatrices().push();
        context.getMatrices().translate(0.0f, 0.0f, -90.0f);
        context.drawTexture(WIDGETS_TEXTURE, i - 91, this.scaledHeight - 22, 0, 0, 182, 22);
        context.drawTexture(WIDGETS_TEXTURE, i - 91 - 1 + playerEntity.getInventory().selectedSlot * 20, this.scaledHeight - 22 - 1, 0, 22, 24, 22);
        if (!itemStack.isEmpty()) {
            if (arm == Arm.LEFT) {
                context.drawTexture(WIDGETS_TEXTURE, i - 91 - 29, this.scaledHeight - 23, 24, 22, 29, 24);
            } else {
                context.drawTexture(WIDGETS_TEXTURE, i + 91, this.scaledHeight - 23, 53, 22, 29, 24);
            }
        }
        context.getMatrices().pop();
        int l = 1;
        for (m = 0; m < 9; ++m) {
            n = i - 90 + m * 20 + 2;
            o = this.scaledHeight - 16 - 3;
            this.renderHotbarItem(context, n, o, tickDelta, playerEntity, playerEntity.getInventory().main.get(m), l++);
        }
        if (!itemStack.isEmpty()) {
            m = this.scaledHeight - 16 - 3;
            if (arm == Arm.LEFT) {
                this.renderHotbarItem(context, i - 91 - 26, m, tickDelta, playerEntity, itemStack, l++);
            } else {
                this.renderHotbarItem(context, i + 91 + 10, m, tickDelta, playerEntity, itemStack, l++);
            }
        }
        RenderSystem.enableBlend();
        if (this.client.options.getAttackIndicator().getValue() == AttackIndicator.HOTBAR && (f = this.client.player.getAttackCooldownProgress(0.0f)) < 1.0f) {
            n = this.scaledHeight - 20;
            o = i + 91 + 6;
            if (arm == Arm.RIGHT) {
                o = i - 91 - 22;
            }
            int p = (int)(f * 19.0f);
            context.drawTexture(ICONS, o, n, 0, 94, 18, 18);
            context.drawTexture(ICONS, o, n + 18 - p, 18, 112 - p, 18, p);
        }
        RenderSystem.disableBlend();
    }

    public void renderMountJumpBar(JumpingMount mount, DrawContext context, int x) {
        this.client.getProfiler().push("jumpBar");
        float f = this.client.player.getMountJumpStrength();
        int i = 182;
        int j = (int)(f * 183.0f);
        int k = this.scaledHeight - 32 + 3;
        context.drawTexture(ICONS, x, k, 0, 84, 182, 5);
        if (mount.getJumpCooldown() > 0) {
            context.drawTexture(ICONS, x, k, 0, 74, 182, 5);
        } else if (j > 0) {
            context.drawTexture(ICONS, x, k, 0, 89, j, 5);
        }
        this.client.getProfiler().pop();
    }

    public void renderExperienceBar(DrawContext context, int x) {
        int l;
        int k;
        this.client.getProfiler().push("expBar");
        int i = this.client.player.getNextLevelExperience();
        if (i > 0) {
            int j = 182;
            k = (int)(this.client.player.experienceProgress * 183.0f);
            l = this.scaledHeight - 32 + 3;
            context.drawTexture(ICONS, x, l, 0, 64, 182, 5);
            if (k > 0) {
                context.drawTexture(ICONS, x, l, 0, 69, k, 5);
            }
        }
        this.client.getProfiler().pop();
        if (this.client.player.experienceLevel > 0) {
            this.client.getProfiler().push("expLevel");
            String string = "" + this.client.player.experienceLevel;
            k = (this.scaledWidth - this.getTextRenderer().getWidth(string)) / 2;
            l = this.scaledHeight - 31 - 4;
            context.drawText(this.getTextRenderer(), string, k + 1, l, 0, false);
            context.drawText(this.getTextRenderer(), string, k - 1, l, 0, false);
            context.drawText(this.getTextRenderer(), string, k, l + 1, 0, false);
            context.drawText(this.getTextRenderer(), string, k, l - 1, 0, false);
            context.drawText(this.getTextRenderer(), string, k, l, 8453920, false);
            this.client.getProfiler().pop();
        }
    }

    public void renderHeldItemTooltip(DrawContext context) {
        this.client.getProfiler().push("selectedItemName");
        if (this.heldItemTooltipFade > 0 && !this.currentStack.isEmpty()) {
            int l;
            MutableText mutableText = Text.empty().append(this.currentStack.getName()).formatted(this.currentStack.getRarity().formatting);
            if (this.currentStack.hasCustomName()) {
                mutableText.formatted(Formatting.ITALIC);
            }
            int i = this.getTextRenderer().getWidth(mutableText);
            int j = (this.scaledWidth - i) / 2;
            int k = this.scaledHeight - 59;
            if (!this.client.interactionManager.hasStatusBars()) {
                k += 14;
            }
            if ((l = (int)((float)this.heldItemTooltipFade * 256.0f / 10.0f)) > 255) {
                l = 255;
            }
            if (l > 0) {
                context.fill(j - 2, k - 2, j + i + 2, k + this.getTextRenderer().fontHeight + 2, this.client.options.getTextBackgroundColor(0));
                context.drawTextWithShadow(this.getTextRenderer(), mutableText, j, k, 0xFFFFFF + (l << 24));
            }
        }
        this.client.getProfiler().pop();
    }

    public void renderDemoTimer(DrawContext context) {
        this.client.getProfiler().push("demo");
        Text text = this.client.world.getTime() >= 120500L ? DEMO_EXPIRED_MESSAGE : Text.translatable("demo.remainingTime", StringHelper.formatTicks((int)(120500L - this.client.world.getTime())));
        int i = this.getTextRenderer().getWidth(text);
        context.drawTextWithShadow(this.getTextRenderer(), text, this.scaledWidth - i - 10, 5, 0xFFFFFF);
        this.client.getProfiler().pop();
    }

    private void renderScoreboardSidebar(DrawContext context, ScoreboardObjective objective) {
        int i;
        Scoreboard scoreboard = objective.getScoreboard();
        List<Object> collection = scoreboard.getAllPlayerScores(objective);
        List list = collection.stream().filter(score -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#")).collect(Collectors.toList());
        collection = list.size() > 15 ? Lists.newArrayList((Iterable)Iterables.skip(list, (int)(collection.size() - 15))) : list;
        ArrayList list2 = Lists.newArrayListWithCapacity((int)collection.size());
        Text text = objective.getDisplayName();
        int j = i = this.getTextRenderer().getWidth(text);
        int k = this.getTextRenderer().getWidth(SCOREBOARD_JOINER);
        for (ScoreboardPlayerScore scoreboardPlayerScore : collection) {
            Team team = scoreboard.getPlayerTeam(scoreboardPlayerScore.getPlayerName());
            MutableText text2 = Team.decorateName(team, Text.literal(scoreboardPlayerScore.getPlayerName()));
            list2.add(Pair.of((Object)scoreboardPlayerScore, (Object)text2));
            j = Math.max(j, this.getTextRenderer().getWidth(text2) + k + this.getTextRenderer().getWidth(Integer.toString(scoreboardPlayerScore.getScore())));
        }
        int l = collection.size() * this.getTextRenderer().fontHeight;
        int m = this.scaledHeight / 2 + l / 3;
        int n = 3;
        int o = this.scaledWidth - j - 3;
        int p = 0;
        int q = this.client.options.getTextBackgroundColor(0.3f);
        int r = this.client.options.getTextBackgroundColor(0.4f);
        for (Pair pair : list2) {
            ScoreboardPlayerScore scoreboardPlayerScore2 = (ScoreboardPlayerScore)pair.getFirst();
            Text text3 = (Text)pair.getSecond();
            String string = "" + Formatting.RED + scoreboardPlayerScore2.getScore();
            int s = o;
            int t = m - ++p * this.getTextRenderer().fontHeight;
            int u = this.scaledWidth - 3 + 2;
            context.fill(s - 2, t, u, t + this.getTextRenderer().fontHeight, q);
            context.drawText(this.getTextRenderer(), text3, s, t, -1, false);
            context.drawText(this.getTextRenderer(), string, u - this.getTextRenderer().getWidth(string), t, -1, false);
            if (p != collection.size()) continue;
            context.fill(s - 2, t - this.getTextRenderer().fontHeight - 1, u, t - 1, r);
            context.fill(s - 2, t - 1, u, t, q);
            context.drawText(this.getTextRenderer(), text, s + j / 2 - i / 2, t - this.getTextRenderer().fontHeight, -1, false);
        }
    }

    private PlayerEntity getCameraPlayer() {
        if (!(this.client.getCameraEntity() instanceof PlayerEntity)) {
            return null;
        }
        return (PlayerEntity)this.client.getCameraEntity();
    }

    private LivingEntity getRiddenEntity() {
        PlayerEntity playerEntity = this.getCameraPlayer();
        if (playerEntity != null) {
            Entity entity = playerEntity.getVehicle();
            if (entity == null) {
                return null;
            }
            if (entity instanceof LivingEntity) {
                return (LivingEntity)entity;
            }
        }
        return null;
    }

    private int getHeartCount(LivingEntity entity) {
        if (entity == null || !entity.isLiving()) {
            return 0;
        }
        float f = entity.getMaxHealth();
        int i = (int)(f + 0.5f) / 2;
        if (i > 30) {
            i = 30;
        }
        return i;
    }

    private int getHeartRows(int heartCount) {
        return (int)Math.ceil((double)heartCount / 10.0);
    }

    private void renderStatusBars(DrawContext context) {
        int ac;
        int ab;
        int aa;
        int z;
        int y;
        int x;
        PlayerEntity playerEntity = this.getCameraPlayer();
        if (playerEntity == null) {
            return;
        }
        int i = MathHelper.ceil(playerEntity.getHealth());
        boolean bl = this.heartJumpEndTick > (long)this.ticks && (this.heartJumpEndTick - (long)this.ticks) / 3L % 2L == 1L;
        long l = Util.getMeasuringTimeMs();
        if (i < this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
            this.lastHealthCheckTime = l;
            this.heartJumpEndTick = this.ticks + 20;
        } else if (i > this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
            this.lastHealthCheckTime = l;
            this.heartJumpEndTick = this.ticks + 10;
        }
        if (l - this.lastHealthCheckTime > 1000L) {
            this.lastHealthValue = i;
            this.renderHealthValue = i;
            this.lastHealthCheckTime = l;
        }
        this.lastHealthValue = i;
        int j = this.renderHealthValue;
        this.random.setSeed(this.ticks * 312871);
        HungerManager hungerManager = playerEntity.getHungerManager();
        int k = hungerManager.getFoodLevel();
        int m = this.scaledWidth / 2 - 91;
        int n = this.scaledWidth / 2 + 91;
        int o = this.scaledHeight - 39;
        float f = Math.max((float)playerEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH), (float)Math.max(j, i));
        int p = MathHelper.ceil(playerEntity.getAbsorptionAmount());
        int q = MathHelper.ceil((f + (float)p) / 2.0f / 10.0f);
        int r = Math.max(10 - (q - 2), 3);
        int s = o - (q - 1) * r - 10;
        int t = o - 10;
        int u = playerEntity.getArmor();
        int v = -1;
        if (playerEntity.hasStatusEffect(StatusEffects.REGENERATION)) {
            v = this.ticks % MathHelper.ceil(f + 5.0f);
        }
        this.client.getProfiler().push("armor");
        for (int w = 0; w < 10; ++w) {
            if (u <= 0) continue;
            x = m + w * 8;
            if (w * 2 + 1 < u) {
                context.drawTexture(ICONS, x, s, 34, 9, 9, 9);
            }
            if (w * 2 + 1 == u) {
                context.drawTexture(ICONS, x, s, 25, 9, 9, 9);
            }
            if (w * 2 + 1 <= u) continue;
            context.drawTexture(ICONS, x, s, 16, 9, 9, 9);
        }
        this.client.getProfiler().swap("health");
        this.renderHealthBar(context, playerEntity, m, o, r, v, f, i, j, p, bl);
        LivingEntity livingEntity = this.getRiddenEntity();
        x = this.getHeartCount(livingEntity);
        if (x == 0) {
            this.client.getProfiler().swap("food");
            for (y = 0; y < 10; ++y) {
                z = o;
                aa = 16;
                ab = 0;
                if (playerEntity.hasStatusEffect(StatusEffects.HUNGER)) {
                    aa += 36;
                    ab = 13;
                }
                if (playerEntity.getHungerManager().getSaturationLevel() <= 0.0f && this.ticks % (k * 3 + 1) == 0) {
                    z += this.random.nextInt(3) - 1;
                }
                ac = n - y * 8 - 9;
                context.drawTexture(ICONS, ac, z, 16 + ab * 9, 27, 9, 9);
                if (y * 2 + 1 < k) {
                    context.drawTexture(ICONS, ac, z, aa + 36, 27, 9, 9);
                }
                if (y * 2 + 1 != k) continue;
                context.drawTexture(ICONS, ac, z, aa + 45, 27, 9, 9);
            }
            t -= 10;
        }
        this.client.getProfiler().swap("air");
        y = playerEntity.getMaxAir();
        z = Math.min(playerEntity.getAir(), y);
        if (playerEntity.isSubmergedIn(FluidTags.WATER) || z < y) {
            aa = this.getHeartRows(x) - 1;
            t -= aa * 10;
            ab = MathHelper.ceil((double)(z - 2) * 10.0 / (double)y);
            ac = MathHelper.ceil((double)z * 10.0 / (double)y) - ab;
            for (int ad = 0; ad < ab + ac; ++ad) {
                if (ad < ab) {
                    context.drawTexture(ICONS, n - ad * 8 - 9, t, 16, 18, 9, 9);
                    continue;
                }
                context.drawTexture(ICONS, n - ad * 8 - 9, t, 25, 18, 9, 9);
            }
        }
        this.client.getProfiler().pop();
    }

    private void renderHealthBar(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking) {
        HeartType heartType = HeartType.fromPlayerState(player);
        int i = 9 * (player.getWorld().getLevelProperties().isHardcore() ? 5 : 0);
        int j = MathHelper.ceil((double)maxHealth / 2.0);
        int k = MathHelper.ceil((double)absorption / 2.0);
        int l = j * 2;
        for (int m = j + k - 1; m >= 0; --m) {
            boolean bl3;
            int s;
            boolean bl;
            int n = m / 10;
            int o = m % 10;
            int p = x + o * 8;
            int q = y - n * lines;
            if (lastHealth + absorption <= 4) {
                q += this.random.nextInt(2);
            }
            if (m < j && m == regeneratingHeartIndex) {
                q -= 2;
            }
            this.drawHeart(context, HeartType.CONTAINER, p, q, i, blinking, false);
            int r = m * 2;
            boolean bl2 = bl = m >= j;
            if (bl && (s = r - l) < absorption) {
                boolean bl22 = s + 1 == absorption;
                this.drawHeart(context, heartType == HeartType.WITHERED ? heartType : HeartType.ABSORBING, p, q, i, false, bl22);
            }
            if (blinking && r < health) {
                bl3 = r + 1 == health;
                this.drawHeart(context, heartType, p, q, i, true, bl3);
            }
            if (r >= lastHealth) continue;
            bl3 = r + 1 == lastHealth;
            this.drawHeart(context, heartType, p, q, i, false, bl3);
        }
    }

    private void drawHeart(DrawContext context, HeartType type, int x, int y, int v, boolean blinking, boolean halfHeart) {
        context.drawTexture(ICONS, x, y, type.getU(halfHeart, blinking), v, 9, 9);
    }

    private void renderMountHealth(DrawContext context) {
        LivingEntity livingEntity = this.getRiddenEntity();
        if (livingEntity == null) {
            return;
        }
        int i = this.getHeartCount(livingEntity);
        if (i == 0) {
            return;
        }
        int j = (int)Math.ceil(livingEntity.getHealth());
        this.client.getProfiler().swap("mountHealth");
        int k = this.scaledHeight - 39;
        int l = this.scaledWidth / 2 + 91;
        int m = k;
        int n = 0;
        boolean bl = false;
        while (i > 0) {
            int o = Math.min(i, 10);
            i -= o;
            for (int p = 0; p < o; ++p) {
                int q = 52;
                int r = 0;
                int s = l - p * 8 - 9;
                context.drawTexture(ICONS, s, m, 52 + r * 9, 9, 9, 9);
                if (p * 2 + 1 + n < j) {
                    context.drawTexture(ICONS, s, m, 88, 9, 9, 9);
                }
                if (p * 2 + 1 + n != j) continue;
                context.drawTexture(ICONS, s, m, 97, 9, 9, 9);
            }
            m -= 10;
            n += 20;
        }
    }

    private void renderOverlay(DrawContext context, Identifier texture, float opacity) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        context.setShaderColor(1.0f, 1.0f, 1.0f, opacity);
        context.drawTexture(texture, 0, 0, -90, 0.0f, 0.0f, this.scaledWidth, this.scaledHeight, this.scaledWidth, this.scaledHeight);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderSpyglassOverlay(DrawContext context, float scale) {
        float f;
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        float g = f = (float)Math.min(this.scaledWidth, this.scaledHeight);
        float h = Math.min((float)this.scaledWidth / f, (float)this.scaledHeight / g) * scale;
        int i = MathHelper.floor(f * h);
        int j = MathHelper.floor(g * h);
        int k = (this.scaledWidth - i) / 2;
        int l = (this.scaledHeight - j) / 2;
        int m = k + i;
        int n = l + j;
        context.drawTexture(SPYGLASS_SCOPE, k, l, -90, 0.0f, 0.0f, i, j, i, j);
        context.fill(0, n, this.scaledWidth, this.scaledHeight, -90, -16777216);
        context.fill(0, 0, this.scaledWidth, l, -90, -16777216);
        context.fill(0, l, k, n, -90, -16777216);
        context.fill(m, l, this.scaledWidth, n, -90, -16777216);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    private void updateVignetteDarkness(Entity entity) {
        if (entity == null) {
            return;
        }
        BlockPos blockPos = BlockPos.ofFloored(entity.getX(), entity.getEyeY(), entity.getZ());
        float f = LightmapTextureManager.getBrightness(entity.getWorld().getDimension(), entity.getWorld().getLightLevel(blockPos));
        float g = MathHelper.clamp(1.0f - f, 0.0f, 1.0f);
        this.vignetteDarkness += (g - this.vignetteDarkness) * 0.01f;
    }

    private void renderVignetteOverlay(DrawContext context, Entity entity) {
        WorldBorder worldBorder = this.client.world.getWorldBorder();
        float f = (float)worldBorder.getDistanceInsideBorder(entity);
        double d = Math.min(worldBorder.getShrinkingSpeed() * (double)worldBorder.getWarningTime() * 1000.0, Math.abs(worldBorder.getSizeLerpTarget() - worldBorder.getSize()));
        double e = Math.max((double)worldBorder.getWarningBlocks(), d);
        f = (double)f < e ? 1.0f - (float)((double)f / e) : 0.0f;
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        if (f > 0.0f) {
            f = MathHelper.clamp(f, 0.0f, 1.0f);
            context.setShaderColor(0.0f, f, f, 1.0f);
        } else {
            float g = this.vignetteDarkness;
            g = MathHelper.clamp(g, 0.0f, 1.0f);
            context.setShaderColor(g, g, g, 1.0f);
        }
        context.drawTexture(VIGNETTE_TEXTURE, 0, 0, -90, 0.0f, 0.0f, this.scaledWidth, this.scaledHeight, this.scaledWidth, this.scaledHeight);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.defaultBlendFunc();
    }

    private void renderPortalOverlay(DrawContext context, float nauseaStrength) {
        if (nauseaStrength < 1.0f) {
            nauseaStrength *= nauseaStrength;
            nauseaStrength *= nauseaStrength;
            nauseaStrength = nauseaStrength * 0.8f + 0.2f;
        }
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        context.setShaderColor(1.0f, 1.0f, 1.0f, nauseaStrength);
        Sprite sprite = this.client.getBlockRenderManager().getModels().getModelParticleSprite(Blocks.NETHER_PORTAL.getDefaultState());
        context.drawSprite(0, 0, -90, this.scaledWidth, this.scaledHeight, sprite);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderHotbarItem(DrawContext context, int x, int y, float f, PlayerEntity player, ItemStack stack, int seed) {
        if (stack.isEmpty()) {
            return;
        }
        float g = (float)stack.getBobbingAnimationTime() - f;
        if (g > 0.0f) {
            float h = 1.0f + g / 5.0f;
            context.getMatrices().push();
            context.getMatrices().translate(x + 8, y + 12, 0.0f);
            context.getMatrices().scale(1.0f / h, (h + 1.0f) / 2.0f, 1.0f);
            context.getMatrices().translate(-(x + 8), -(y + 12), 0.0f);
        }
        context.drawItem(player, stack, x, y, seed);
        if (g > 0.0f) {
            context.getMatrices().pop();
        }
        context.drawItemInSlot(this.client.textRenderer, stack, x, y);
    }

    public void tick(boolean paused) {
        this.tickAutosaveIndicator();
        if (!paused) {
            this.tick();
        }
    }

    private void tick() {
        if (this.overlayRemaining > 0) {
            --this.overlayRemaining;
        }
        if (this.titleRemainTicks > 0) {
            --this.titleRemainTicks;
            if (this.titleRemainTicks <= 0) {
                this.title = null;
                this.subtitle = null;
            }
        }
        ++this.ticks;
        Entity entity = this.client.getCameraEntity();
        if (entity != null) {
            this.updateVignetteDarkness(entity);
        }
        if (this.client.player != null) {
            ItemStack itemStack = this.client.player.getInventory().getMainHandStack();
            if (itemStack.isEmpty()) {
                this.heldItemTooltipFade = 0;
            } else if (this.currentStack.isEmpty() || !itemStack.isOf(this.currentStack.getItem()) || !itemStack.getName().equals(this.currentStack.getName())) {
                this.heldItemTooltipFade = (int)(40.0 * this.client.options.getNotificationDisplayTime().getValue());
            } else if (this.heldItemTooltipFade > 0) {
                --this.heldItemTooltipFade;
            }
            this.currentStack = itemStack;
        }
        this.chatHud.tickRemovalQueueIfExists();
    }

    private void tickAutosaveIndicator() {
        IntegratedServer minecraftServer = this.client.getServer();
        boolean bl = minecraftServer != null && minecraftServer.isSaving();
        this.lastAutosaveIndicatorAlpha = this.autosaveIndicatorAlpha;
        this.autosaveIndicatorAlpha = MathHelper.lerp(0.2f, this.autosaveIndicatorAlpha, bl ? 1.0f : 0.0f);
    }

    public void setRecordPlayingOverlay(Text description) {
        MutableText text = Text.translatable("record.nowPlaying", description);
        this.setOverlayMessage(text, true);
        this.client.getNarratorManager().narrate(text);
    }

    public void setOverlayMessage(Text message, boolean tinted) {
        this.setCanShowChatDisabledScreen(false);
        this.overlayMessage = message;
        this.overlayRemaining = 60;
        this.overlayTinted = tinted;
    }

    public void setCanShowChatDisabledScreen(boolean canShowChatDisabledScreen) {
        this.canShowChatDisabledScreen = canShowChatDisabledScreen;
    }

    public boolean shouldShowChatDisabledScreen() {
        return this.canShowChatDisabledScreen && this.overlayRemaining > 0;
    }

    public void setTitleTicks(int fadeInTicks, int stayTicks, int fadeOutTicks) {
        if (fadeInTicks >= 0) {
            this.titleFadeInTicks = fadeInTicks;
        }
        if (stayTicks >= 0) {
            this.titleStayTicks = stayTicks;
        }
        if (fadeOutTicks >= 0) {
            this.titleFadeOutTicks = fadeOutTicks;
        }
        if (this.titleRemainTicks > 0) {
            this.titleRemainTicks = this.titleFadeInTicks + this.titleStayTicks + this.titleFadeOutTicks;
        }
    }

    public void setSubtitle(Text subtitle) {
        this.subtitle = subtitle;
    }

    public void setTitle(Text title) {
        this.title = title;
        this.titleRemainTicks = this.titleFadeInTicks + this.titleStayTicks + this.titleFadeOutTicks;
    }

    public void clearTitle() {
        this.title = null;
        this.subtitle = null;
        this.titleRemainTicks = 0;
    }

    public ChatHud getChatHud() {
        return this.chatHud;
    }

    public int getTicks() {
        return this.ticks;
    }

    public TextRenderer getTextRenderer() {
        return this.client.textRenderer;
    }

    public SpectatorHud getSpectatorHud() {
        return this.spectatorHud;
    }

    public PlayerListHud getPlayerListHud() {
        return this.playerListHud;
    }

    public void clear() {
        this.playerListHud.clear();
        this.bossBarHud.clear();
        this.client.getToastManager().clear();
        this.client.options.debugEnabled = false;
        this.chatHud.clear(true);
    }

    public BossBarHud getBossBarHud() {
        return this.bossBarHud;
    }

    public void resetDebugHudChunk() {
        this.debugHud.resetChunk();
    }

    private void renderAutosaveIndicator(DrawContext context) {
        int i;
        if (this.client.options.getShowAutosaveIndicator().getValue().booleanValue() && (this.autosaveIndicatorAlpha > 0.0f || this.lastAutosaveIndicatorAlpha > 0.0f) && (i = MathHelper.floor(255.0f * MathHelper.clamp(MathHelper.lerp(this.client.getTickDelta(), this.lastAutosaveIndicatorAlpha, this.autosaveIndicatorAlpha), 0.0f, 1.0f))) > 8) {
            TextRenderer textRenderer = this.getTextRenderer();
            int j = textRenderer.getWidth(SAVING_LEVEL_TEXT);
            int k = 0xFFFFFF | i << 24 & 0xFF000000;
            context.drawTextWithShadow(textRenderer, SAVING_LEVEL_TEXT, this.scaledWidth - j - 10, this.scaledHeight - 15, k);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class HeartType
    extends Enum<HeartType> {
        public static final /* enum */ HeartType CONTAINER = new HeartType(0, false);
        public static final /* enum */ HeartType NORMAL = new HeartType(2, true);
        public static final /* enum */ HeartType POISONED = new HeartType(4, true);
        public static final /* enum */ HeartType WITHERED = new HeartType(6, true);
        public static final /* enum */ HeartType ABSORBING = new HeartType(8, false);
        public static final /* enum */ HeartType FROZEN = new HeartType(9, false);
        private final int textureIndex;
        private final boolean hasBlinkingTexture;
        private static final /* synthetic */ HeartType[] field_33952;

        public static HeartType[] values() {
            return (HeartType[])field_33952.clone();
        }

        public static HeartType valueOf(String string) {
            return Enum.valueOf(HeartType.class, string);
        }

        private HeartType(int textureIndex, boolean hasBlinkingTexture) {
            this.textureIndex = textureIndex;
            this.hasBlinkingTexture = hasBlinkingTexture;
        }

        public int getU(boolean halfHeart, boolean blinking) {
            int i;
            if (this == CONTAINER) {
                i = blinking ? 1 : 0;
            } else {
                int j = halfHeart ? 1 : 0;
                int k = this.hasBlinkingTexture && blinking ? 2 : 0;
                i = j + k;
            }
            return 16 + (this.textureIndex * 2 + i) * 9;
        }

        static HeartType fromPlayerState(PlayerEntity player) {
            HeartType heartType = player.hasStatusEffect(StatusEffects.POISON) ? POISONED : (player.hasStatusEffect(StatusEffects.WITHER) ? WITHERED : (player.isFrozen() ? FROZEN : NORMAL));
            return heartType;
        }

        private static /* synthetic */ HeartType[] method_37300() {
            return new HeartType[]{CONTAINER, NORMAL, POISONED, WITHERED, ABSORBING, FROZEN};
        }

        static {
            field_33952 = HeartType.method_37300();
        }
    }
}

