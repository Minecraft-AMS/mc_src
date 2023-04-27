/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class GameModeSelectionScreen
extends Screen {
    static final Identifier TEXTURE = new Identifier("textures/gui/container/gamemode_switcher.png");
    private static final int TEXTURE_WIDTH = 128;
    private static final int TEXTURE_HEIGHT = 128;
    private static final int BUTTON_SIZE = 26;
    private static final int ICON_OFFSET = 5;
    private static final int field_32314 = 31;
    private static final int field_32315 = 5;
    private static final int UI_WIDTH = GameModeSelection.values().length * 31 - 5;
    private static final Text SELECT_NEXT_TEXT = Text.translatable("debug.gamemodes.select_next", Text.translatable("debug.gamemodes.press_f4").formatted(Formatting.AQUA));
    private final GameModeSelection currentGameMode;
    private GameModeSelection gameMode;
    private int lastMouseX;
    private int lastMouseY;
    private boolean mouseUsedForSelection;
    private final List<ButtonWidget> gameModeButtons = Lists.newArrayList();

    public GameModeSelectionScreen() {
        super(NarratorManager.EMPTY);
        this.gameMode = this.currentGameMode = GameModeSelection.of(this.getPreviousGameMode());
    }

    private GameMode getPreviousGameMode() {
        ClientPlayerInteractionManager clientPlayerInteractionManager = MinecraftClient.getInstance().interactionManager;
        GameMode gameMode = clientPlayerInteractionManager.getPreviousGameMode();
        if (gameMode != null) {
            return gameMode;
        }
        return clientPlayerInteractionManager.getCurrentGameMode() == GameMode.CREATIVE ? GameMode.SURVIVAL : GameMode.CREATIVE;
    }

    @Override
    protected void init() {
        super.init();
        this.gameMode = this.currentGameMode;
        for (int i = 0; i < GameModeSelection.VALUES.length; ++i) {
            GameModeSelection gameModeSelection = GameModeSelection.VALUES[i];
            this.gameModeButtons.add(new ButtonWidget(gameModeSelection, this.width / 2 - UI_WIDTH / 2 + i * 31, this.height / 2 - 31));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.checkForClose()) {
            return;
        }
        context.getMatrices().push();
        RenderSystem.enableBlend();
        int i = this.width / 2 - 62;
        int j = this.height / 2 - 31 - 27;
        context.drawTexture(TEXTURE, i, j, 0.0f, 0.0f, 125, 75, 128, 128);
        context.getMatrices().pop();
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.gameMode.getText(), this.width / 2, this.height / 2 - 31 - 20, -1);
        context.drawCenteredTextWithShadow(this.textRenderer, SELECT_NEXT_TEXT, this.width / 2, this.height / 2 + 5, 0xFFFFFF);
        if (!this.mouseUsedForSelection) {
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
            this.mouseUsedForSelection = true;
        }
        boolean bl = this.lastMouseX == mouseX && this.lastMouseY == mouseY;
        for (ButtonWidget buttonWidget : this.gameModeButtons) {
            buttonWidget.render(context, mouseX, mouseY, delta);
            buttonWidget.setSelected(this.gameMode == buttonWidget.gameMode);
            if (bl || !buttonWidget.isSelected()) continue;
            this.gameMode = buttonWidget.gameMode;
        }
    }

    private void apply() {
        GameModeSelectionScreen.apply(this.client, this.gameMode);
    }

    private static void apply(MinecraftClient client, GameModeSelection gameModeSelection) {
        if (client.interactionManager == null || client.player == null) {
            return;
        }
        GameModeSelection gameModeSelection2 = GameModeSelection.of(client.interactionManager.getCurrentGameMode());
        GameModeSelection gameModeSelection3 = gameModeSelection;
        if (client.player.hasPermissionLevel(2) && gameModeSelection3 != gameModeSelection2) {
            client.player.networkHandler.sendCommand(gameModeSelection3.getCommand());
        }
    }

    private boolean checkForClose() {
        if (!InputUtil.isKeyPressed(this.client.getWindow().getHandle(), 292)) {
            this.apply();
            this.client.setScreen(null);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 293) {
            this.mouseUsedForSelection = false;
            this.gameMode = this.gameMode.next();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    static final class GameModeSelection
    extends Enum<GameModeSelection> {
        public static final /* enum */ GameModeSelection CREATIVE = new GameModeSelection(Text.translatable("gameMode.creative"), "gamemode creative", new ItemStack(Blocks.GRASS_BLOCK));
        public static final /* enum */ GameModeSelection SURVIVAL = new GameModeSelection(Text.translatable("gameMode.survival"), "gamemode survival", new ItemStack(Items.IRON_SWORD));
        public static final /* enum */ GameModeSelection ADVENTURE = new GameModeSelection(Text.translatable("gameMode.adventure"), "gamemode adventure", new ItemStack(Items.MAP));
        public static final /* enum */ GameModeSelection SPECTATOR = new GameModeSelection(Text.translatable("gameMode.spectator"), "gamemode spectator", new ItemStack(Items.ENDER_EYE));
        protected static final GameModeSelection[] VALUES;
        private static final int field_32317 = 16;
        protected static final int field_32316 = 5;
        final Text text;
        final String command;
        final ItemStack icon;
        private static final /* synthetic */ GameModeSelection[] field_24584;

        public static GameModeSelection[] values() {
            return (GameModeSelection[])field_24584.clone();
        }

        public static GameModeSelection valueOf(String string) {
            return Enum.valueOf(GameModeSelection.class, string);
        }

        private GameModeSelection(Text text, String command, ItemStack icon) {
            this.text = text;
            this.command = command;
            this.icon = icon;
        }

        void renderIcon(DrawContext context, int x, int y) {
            context.drawItem(this.icon, x, y);
        }

        Text getText() {
            return this.text;
        }

        String getCommand() {
            return this.command;
        }

        GameModeSelection next() {
            return switch (this) {
                default -> throw new IncompatibleClassChangeError();
                case CREATIVE -> SURVIVAL;
                case SURVIVAL -> ADVENTURE;
                case ADVENTURE -> SPECTATOR;
                case SPECTATOR -> CREATIVE;
            };
        }

        static GameModeSelection of(GameMode gameMode) {
            return switch (gameMode) {
                default -> throw new IncompatibleClassChangeError();
                case GameMode.SPECTATOR -> SPECTATOR;
                case GameMode.SURVIVAL -> SURVIVAL;
                case GameMode.CREATIVE -> CREATIVE;
                case GameMode.ADVENTURE -> ADVENTURE;
            };
        }

        private static /* synthetic */ GameModeSelection[] method_36886() {
            return new GameModeSelection[]{CREATIVE, SURVIVAL, ADVENTURE, SPECTATOR};
        }

        static {
            field_24584 = GameModeSelection.method_36886();
            VALUES = GameModeSelection.values();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public class ButtonWidget
    extends ClickableWidget {
        final GameModeSelection gameMode;
        private boolean selected;

        public ButtonWidget(GameModeSelection gameMode, int x, int y) {
            super(x, y, 26, 26, gameMode.getText());
            this.gameMode = gameMode;
        }

        @Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            this.drawBackground(context);
            this.gameMode.renderIcon(context, this.getX() + 5, this.getY() + 5);
            if (this.selected) {
                this.drawSelectionBox(context);
            }
        }

        @Override
        public void appendClickableNarrations(NarrationMessageBuilder builder) {
            this.appendDefaultNarrations(builder);
        }

        @Override
        public boolean isSelected() {
            return super.isSelected() || this.selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        private void drawBackground(DrawContext context) {
            context.drawTexture(TEXTURE, this.getX(), this.getY(), 0.0f, 75.0f, 26, 26, 128, 128);
        }

        private void drawSelectionBox(DrawContext context) {
            context.drawTexture(TEXTURE, this.getX(), this.getY(), 26.0f, 75.0f, 26, 26, 128, 128);
        }
    }
}

