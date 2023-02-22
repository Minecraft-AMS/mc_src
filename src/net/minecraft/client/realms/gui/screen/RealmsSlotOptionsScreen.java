/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class RealmsSlotOptionsScreen
extends RealmsScreen {
    private static final int field_32125 = 2;
    public static final List<Difficulty> DIFFICULTIES = ImmutableList.of((Object)Difficulty.PEACEFUL, (Object)Difficulty.EASY, (Object)Difficulty.NORMAL, (Object)Difficulty.HARD);
    private static final int field_32126 = 0;
    public static final List<GameMode> GAME_MODES = ImmutableList.of((Object)GameMode.SURVIVAL, (Object)GameMode.CREATIVE, (Object)GameMode.ADVENTURE);
    private static final Text EDIT_SLOT_NAME = Text.translatable("mco.configure.world.edit.slot.name");
    static final Text SPAWN_PROTECTION = Text.translatable("mco.configure.world.spawnProtection");
    private static final Text SPAWN_TOGGLE_TITLE = Text.translatable("mco.configure.world.spawn_toggle.title").formatted(Formatting.RED, Formatting.BOLD);
    private TextFieldWidget nameEdit;
    protected final RealmsConfigureWorldScreen parent;
    private int column1_x;
    private int column2_x;
    private final RealmsWorldOptions options;
    private final RealmsServer.WorldType worldType;
    private Difficulty difficulty;
    private GameMode gameMode;
    private final String defaultSlotName;
    private String slotName;
    private boolean pvp;
    private boolean spawnNpcs;
    private boolean spawnAnimals;
    private boolean spawnMonsters;
    int spawnProtection;
    private boolean commandBlocks;
    private boolean forceGameMode;
    SettingsSlider spawnProtectionButton;

    public RealmsSlotOptionsScreen(RealmsConfigureWorldScreen parent, RealmsWorldOptions options, RealmsServer.WorldType worldType, int activeSlot) {
        super(Text.translatable("mco.configure.world.buttons.options"));
        this.parent = parent;
        this.options = options;
        this.worldType = worldType;
        this.difficulty = RealmsSlotOptionsScreen.get(DIFFICULTIES, options.difficulty, 2);
        this.gameMode = RealmsSlotOptionsScreen.get(GAME_MODES, options.gameMode, 0);
        this.defaultSlotName = options.getDefaultSlotName(activeSlot);
        this.setSlotName(options.getSlotName(activeSlot));
        if (worldType == RealmsServer.WorldType.NORMAL) {
            this.pvp = options.pvp;
            this.spawnProtection = options.spawnProtection;
            this.forceGameMode = options.forceGameMode;
            this.spawnAnimals = options.spawnAnimals;
            this.spawnMonsters = options.spawnMonsters;
            this.spawnNpcs = options.spawnNpcs;
            this.commandBlocks = options.commandBlocks;
        } else {
            this.pvp = true;
            this.spawnProtection = 0;
            this.forceGameMode = false;
            this.spawnAnimals = true;
            this.spawnMonsters = true;
            this.spawnNpcs = true;
            this.commandBlocks = true;
        }
    }

    @Override
    public void tick() {
        this.nameEdit.tick();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.client.setScreen(this.parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private static <T> T get(List<T> list, int index, int fallbackIndex) {
        try {
            return list.get(index);
        }
        catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            return list.get(fallbackIndex);
        }
    }

    private static <T> int indexOf(List<T> list, T value, int fallbackIndex) {
        int i = list.indexOf(value);
        return i == -1 ? fallbackIndex : i;
    }

    @Override
    public void init() {
        this.column2_x = 170;
        this.column1_x = this.width / 2 - this.column2_x;
        int i = this.width / 2 + 10;
        if (this.worldType != RealmsServer.WorldType.NORMAL) {
            MutableText text = this.worldType == RealmsServer.WorldType.ADVENTUREMAP ? Text.translatable("mco.configure.world.edit.subscreen.adventuremap") : (this.worldType == RealmsServer.WorldType.INSPIRATION ? Text.translatable("mco.configure.world.edit.subscreen.inspiration") : Text.translatable("mco.configure.world.edit.subscreen.experience"));
            this.addLabel(new RealmsLabel(text, this.width / 2, 26, 0xFF0000));
        }
        this.nameEdit = new TextFieldWidget(this.client.textRenderer, this.column1_x + 2, RealmsSlotOptionsScreen.row(1), this.column2_x - 4, 20, null, Text.translatable("mco.configure.world.edit.slot.name"));
        this.nameEdit.setMaxLength(10);
        this.nameEdit.setText(this.slotName);
        this.nameEdit.setChangedListener(this::setSlotName);
        this.focusOn(this.nameEdit);
        CyclingButtonWidget<Boolean> cyclingButtonWidget = this.addDrawableChild(CyclingButtonWidget.onOffBuilder(this.pvp).build(i, RealmsSlotOptionsScreen.row(1), this.column2_x, 20, Text.translatable("mco.configure.world.pvp"), (button, pvp) -> {
            this.pvp = pvp;
        }));
        this.addDrawableChild(CyclingButtonWidget.builder(GameMode::getSimpleTranslatableName).values((Collection<GameMode>)GAME_MODES).initially(this.gameMode).build(this.column1_x, RealmsSlotOptionsScreen.row(3), this.column2_x, 20, Text.translatable("selectWorld.gameMode"), (button, gameModeIndex) -> {
            this.gameMode = gameModeIndex;
        }));
        MutableText text2 = Text.translatable("mco.configure.world.spawn_toggle.message");
        CyclingButtonWidget<Boolean> cyclingButtonWidget2 = this.addDrawableChild(CyclingButtonWidget.onOffBuilder(this.spawnAnimals).build(i, RealmsSlotOptionsScreen.row(3), this.column2_x, 20, Text.translatable("mco.configure.world.spawnAnimals"), this.getSpawnToggleButtonCallback(text2, spawnAnimals -> {
            this.spawnAnimals = spawnAnimals;
        })));
        CyclingButtonWidget<Boolean> cyclingButtonWidget3 = CyclingButtonWidget.onOffBuilder(this.difficulty != Difficulty.PEACEFUL && this.spawnMonsters).build(i, RealmsSlotOptionsScreen.row(5), this.column2_x, 20, Text.translatable("mco.configure.world.spawnMonsters"), this.getSpawnToggleButtonCallback(text2, spawnMonsters -> {
            this.spawnMonsters = spawnMonsters;
        }));
        this.addDrawableChild(CyclingButtonWidget.builder(Difficulty::getTranslatableName).values((Collection<Difficulty>)DIFFICULTIES).initially(this.difficulty).build(this.column1_x, RealmsSlotOptionsScreen.row(5), this.column2_x, 20, Text.translatable("options.difficulty"), (button, difficulty) -> {
            this.difficulty = difficulty;
            if (this.worldType == RealmsServer.WorldType.NORMAL) {
                boolean bl;
                cyclingButtonWidget.active = bl = this.difficulty != Difficulty.PEACEFUL;
                cyclingButtonWidget3.setValue(bl && this.spawnMonsters);
            }
        }));
        this.addDrawableChild(cyclingButtonWidget3);
        this.spawnProtectionButton = this.addDrawableChild(new SettingsSlider(this.column1_x, RealmsSlotOptionsScreen.row(7), this.column2_x, this.spawnProtection, 0.0f, 16.0f));
        CyclingButtonWidget<Boolean> cyclingButtonWidget4 = this.addDrawableChild(CyclingButtonWidget.onOffBuilder(this.spawnNpcs).build(i, RealmsSlotOptionsScreen.row(7), this.column2_x, 20, Text.translatable("mco.configure.world.spawnNPCs"), this.getSpawnToggleButtonCallback(Text.translatable("mco.configure.world.spawn_toggle.message.npc"), spawnNpcs -> {
            this.spawnNpcs = spawnNpcs;
        })));
        CyclingButtonWidget<Boolean> cyclingButtonWidget5 = this.addDrawableChild(CyclingButtonWidget.onOffBuilder(this.forceGameMode).build(this.column1_x, RealmsSlotOptionsScreen.row(9), this.column2_x, 20, Text.translatable("mco.configure.world.forceGameMode"), (button, forceGameMode) -> {
            this.forceGameMode = forceGameMode;
        }));
        CyclingButtonWidget<Boolean> cyclingButtonWidget6 = this.addDrawableChild(CyclingButtonWidget.onOffBuilder(this.commandBlocks).build(i, RealmsSlotOptionsScreen.row(9), this.column2_x, 20, Text.translatable("mco.configure.world.commandBlocks"), (button, commandBlocks) -> {
            this.commandBlocks = commandBlocks;
        }));
        if (this.worldType != RealmsServer.WorldType.NORMAL) {
            cyclingButtonWidget.active = false;
            cyclingButtonWidget2.active = false;
            cyclingButtonWidget4.active = false;
            cyclingButtonWidget3.active = false;
            this.spawnProtectionButton.active = false;
            cyclingButtonWidget6.active = false;
            cyclingButtonWidget5.active = false;
        }
        if (this.difficulty == Difficulty.PEACEFUL) {
            cyclingButtonWidget3.active = false;
        }
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("mco.configure.world.buttons.done"), button -> this.saveSettings()).dimensions(this.column1_x, RealmsSlotOptionsScreen.row(13), this.column2_x, 20).build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.client.setScreen(this.parent)).dimensions(i, RealmsSlotOptionsScreen.row(13), this.column2_x, 20).build());
        this.addSelectableChild(this.nameEdit);
    }

    private CyclingButtonWidget.UpdateCallback<Boolean> getSpawnToggleButtonCallback(Text text, Consumer<Boolean> valueSetter) {
        return (button, value) -> {
            if (value.booleanValue()) {
                valueSetter.accept(true);
            } else {
                this.client.setScreen(new ConfirmScreen(confirmed -> {
                    if (confirmed) {
                        valueSetter.accept(false);
                    }
                    this.client.setScreen(this);
                }, SPAWN_TOGGLE_TITLE, text, ScreenTexts.PROCEED, ScreenTexts.CANCEL));
            }
        };
    }

    @Override
    public Text getNarratedTitle() {
        return ScreenTexts.joinSentences(this.getTitle(), this.narrateLabels());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        RealmsSlotOptionsScreen.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 17, 0xFFFFFF);
        this.textRenderer.draw(matrices, EDIT_SLOT_NAME, (float)(this.column1_x + this.column2_x / 2 - this.textRenderer.getWidth(EDIT_SLOT_NAME) / 2), (float)(RealmsSlotOptionsScreen.row(0) - 5), 0xFFFFFF);
        this.nameEdit.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    private void setSlotName(String slotName) {
        this.slotName = slotName.equals(this.defaultSlotName) ? "" : slotName;
    }

    private void saveSettings() {
        int i = RealmsSlotOptionsScreen.indexOf(DIFFICULTIES, this.difficulty, 2);
        int j = RealmsSlotOptionsScreen.indexOf(GAME_MODES, this.gameMode, 0);
        if (this.worldType == RealmsServer.WorldType.ADVENTUREMAP || this.worldType == RealmsServer.WorldType.EXPERIENCE || this.worldType == RealmsServer.WorldType.INSPIRATION) {
            this.parent.saveSlotSettings(new RealmsWorldOptions(this.options.pvp, this.options.spawnAnimals, this.options.spawnMonsters, this.options.spawnNpcs, this.options.spawnProtection, this.options.commandBlocks, i, j, this.options.forceGameMode, this.slotName));
        } else {
            boolean bl = this.worldType == RealmsServer.WorldType.NORMAL && this.difficulty != Difficulty.PEACEFUL && this.spawnMonsters;
            this.parent.saveSlotSettings(new RealmsWorldOptions(this.pvp, this.spawnAnimals, bl, this.spawnNpcs, this.spawnProtection, this.commandBlocks, i, j, this.forceGameMode, this.slotName));
        }
    }

    @Environment(value=EnvType.CLIENT)
    class SettingsSlider
    extends SliderWidget {
        private final double min;
        private final double max;

        public SettingsSlider(int x, int y, int width, int value, float min, float max) {
            super(x, y, width, 20, ScreenTexts.EMPTY, 0.0);
            this.min = min;
            this.max = max;
            this.value = (MathHelper.clamp((float)value, min, max) - min) / (max - min);
            this.updateMessage();
        }

        @Override
        public void applyValue() {
            if (!RealmsSlotOptionsScreen.this.spawnProtectionButton.active) {
                return;
            }
            RealmsSlotOptionsScreen.this.spawnProtection = (int)MathHelper.lerp(MathHelper.clamp(this.value, 0.0, 1.0), this.min, this.max);
        }

        @Override
        protected void updateMessage() {
            this.setMessage(ScreenTexts.composeGenericOptionText(SPAWN_PROTECTION, RealmsSlotOptionsScreen.this.spawnProtection == 0 ? ScreenTexts.OFF : Text.literal(String.valueOf(RealmsSlotOptionsScreen.this.spawnProtection))));
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
        }

        @Override
        public void onRelease(double mouseX, double mouseY) {
        }
    }
}

