/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.packet.c2s.play.UpdateStructureBlockC2SPacket;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class StructureBlockScreen
extends Screen {
    private final StructureBlockBlockEntity structureBlock;
    private BlockMirror mirror = BlockMirror.NONE;
    private BlockRotation rotation = BlockRotation.NONE;
    private StructureBlockMode mode = StructureBlockMode.DATA;
    private boolean ignoreEntities;
    private boolean showAir;
    private boolean showBoundingBox;
    private TextFieldWidget inputName;
    private TextFieldWidget inputPosX;
    private TextFieldWidget inputPosY;
    private TextFieldWidget inputPosZ;
    private TextFieldWidget inputSizeX;
    private TextFieldWidget inputSizeY;
    private TextFieldWidget inputSizeZ;
    private TextFieldWidget inputIntegrity;
    private TextFieldWidget inputSeed;
    private TextFieldWidget inputMetadata;
    private ButtonWidget buttonDone;
    private ButtonWidget buttonCancel;
    private ButtonWidget buttonSave;
    private ButtonWidget buttonLoad;
    private ButtonWidget buttonRotate0;
    private ButtonWidget buttonRotate90;
    private ButtonWidget buttonRotate180;
    private ButtonWidget buttonRotate270;
    private ButtonWidget buttonMode;
    private ButtonWidget buttonDetect;
    private ButtonWidget buttonEntities;
    private ButtonWidget buttonMirror;
    private ButtonWidget buttonShowAir;
    private ButtonWidget buttonShowBoundingBox;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.0###");

    public StructureBlockScreen(StructureBlockBlockEntity structureBlock) {
        super(new TranslatableText(Blocks.STRUCTURE_BLOCK.getTranslationKey(), new Object[0]));
        this.structureBlock = structureBlock;
        this.decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
    }

    @Override
    public void tick() {
        this.inputName.tick();
        this.inputPosX.tick();
        this.inputPosY.tick();
        this.inputPosZ.tick();
        this.inputSizeX.tick();
        this.inputSizeY.tick();
        this.inputSizeZ.tick();
        this.inputIntegrity.tick();
        this.inputSeed.tick();
        this.inputMetadata.tick();
    }

    private void done() {
        if (this.method_2516(StructureBlockBlockEntity.Action.UPDATE_DATA)) {
            this.minecraft.openScreen(null);
        }
    }

    private void cancel() {
        this.structureBlock.setMirror(this.mirror);
        this.structureBlock.setRotation(this.rotation);
        this.structureBlock.setMode(this.mode);
        this.structureBlock.setIgnoreEntities(this.ignoreEntities);
        this.structureBlock.setShowAir(this.showAir);
        this.structureBlock.setShowBoundingBox(this.showBoundingBox);
        this.minecraft.openScreen(null);
    }

    @Override
    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        this.buttonDone = this.addButton(new ButtonWidget(this.width / 2 - 4 - 150, 210, 150, 20, I18n.translate("gui.done", new Object[0]), buttonWidget -> this.done()));
        this.buttonCancel = this.addButton(new ButtonWidget(this.width / 2 + 4, 210, 150, 20, I18n.translate("gui.cancel", new Object[0]), buttonWidget -> this.cancel()));
        this.buttonSave = this.addButton(new ButtonWidget(this.width / 2 + 4 + 100, 185, 50, 20, I18n.translate("structure_block.button.save", new Object[0]), buttonWidget -> {
            if (this.structureBlock.getMode() == StructureBlockMode.SAVE) {
                this.method_2516(StructureBlockBlockEntity.Action.SAVE_AREA);
                this.minecraft.openScreen(null);
            }
        }));
        this.buttonLoad = this.addButton(new ButtonWidget(this.width / 2 + 4 + 100, 185, 50, 20, I18n.translate("structure_block.button.load", new Object[0]), buttonWidget -> {
            if (this.structureBlock.getMode() == StructureBlockMode.LOAD) {
                this.method_2516(StructureBlockBlockEntity.Action.LOAD_AREA);
                this.minecraft.openScreen(null);
            }
        }));
        this.buttonMode = this.addButton(new ButtonWidget(this.width / 2 - 4 - 150, 185, 50, 20, "MODE", buttonWidget -> {
            this.structureBlock.cycleMode();
            this.updateMode();
        }));
        this.buttonDetect = this.addButton(new ButtonWidget(this.width / 2 + 4 + 100, 120, 50, 20, I18n.translate("structure_block.button.detect_size", new Object[0]), buttonWidget -> {
            if (this.structureBlock.getMode() == StructureBlockMode.SAVE) {
                this.method_2516(StructureBlockBlockEntity.Action.SCAN_AREA);
                this.minecraft.openScreen(null);
            }
        }));
        this.buttonEntities = this.addButton(new ButtonWidget(this.width / 2 + 4 + 100, 160, 50, 20, "ENTITIES", buttonWidget -> {
            this.structureBlock.setIgnoreEntities(!this.structureBlock.shouldIgnoreEntities());
            this.updateIgnoreEntitiesButton();
        }));
        this.buttonMirror = this.addButton(new ButtonWidget(this.width / 2 - 20, 185, 40, 20, "MIRROR", buttonWidget -> {
            switch (this.structureBlock.getMirror()) {
                case NONE: {
                    this.structureBlock.setMirror(BlockMirror.LEFT_RIGHT);
                    break;
                }
                case LEFT_RIGHT: {
                    this.structureBlock.setMirror(BlockMirror.FRONT_BACK);
                    break;
                }
                case FRONT_BACK: {
                    this.structureBlock.setMirror(BlockMirror.NONE);
                }
            }
            this.updateMirrorButton();
        }));
        this.buttonShowAir = this.addButton(new ButtonWidget(this.width / 2 + 4 + 100, 80, 50, 20, "SHOWAIR", buttonWidget -> {
            this.structureBlock.setShowAir(!this.structureBlock.shouldShowAir());
            this.updateShowAirButton();
        }));
        this.buttonShowBoundingBox = this.addButton(new ButtonWidget(this.width / 2 + 4 + 100, 80, 50, 20, "SHOWBB", buttonWidget -> {
            this.structureBlock.setShowBoundingBox(!this.structureBlock.shouldShowBoundingBox());
            this.updateShowBoundingBoxButton();
        }));
        this.buttonRotate0 = this.addButton(new ButtonWidget(this.width / 2 - 1 - 40 - 1 - 40 - 20, 185, 40, 20, "0", buttonWidget -> {
            this.structureBlock.setRotation(BlockRotation.NONE);
            this.updateRotationButton();
        }));
        this.buttonRotate90 = this.addButton(new ButtonWidget(this.width / 2 - 1 - 40 - 20, 185, 40, 20, "90", buttonWidget -> {
            this.structureBlock.setRotation(BlockRotation.CLOCKWISE_90);
            this.updateRotationButton();
        }));
        this.buttonRotate180 = this.addButton(new ButtonWidget(this.width / 2 + 1 + 20, 185, 40, 20, "180", buttonWidget -> {
            this.structureBlock.setRotation(BlockRotation.CLOCKWISE_180);
            this.updateRotationButton();
        }));
        this.buttonRotate270 = this.addButton(new ButtonWidget(this.width / 2 + 1 + 40 + 1 + 20, 185, 40, 20, "270", buttonWidget -> {
            this.structureBlock.setRotation(BlockRotation.COUNTERCLOCKWISE_90);
            this.updateRotationButton();
        }));
        this.inputName = new TextFieldWidget(this.font, this.width / 2 - 152, 40, 300, 20, I18n.translate("structure_block.structure_name", new Object[0])){

            @Override
            public boolean charTyped(char chr, int keyCode) {
                if (!StructureBlockScreen.this.isValidCharacterForName(this.getText(), chr, this.getCursor())) {
                    return false;
                }
                return super.charTyped(chr, keyCode);
            }
        };
        this.inputName.setMaxLength(64);
        this.inputName.setText(this.structureBlock.getStructureName());
        this.children.add(this.inputName);
        BlockPos blockPos = this.structureBlock.getOffset();
        this.inputPosX = new TextFieldWidget(this.font, this.width / 2 - 152, 80, 80, 20, I18n.translate("structure_block.position.x", new Object[0]));
        this.inputPosX.setMaxLength(15);
        this.inputPosX.setText(Integer.toString(blockPos.getX()));
        this.children.add(this.inputPosX);
        this.inputPosY = new TextFieldWidget(this.font, this.width / 2 - 72, 80, 80, 20, I18n.translate("structure_block.position.y", new Object[0]));
        this.inputPosY.setMaxLength(15);
        this.inputPosY.setText(Integer.toString(blockPos.getY()));
        this.children.add(this.inputPosY);
        this.inputPosZ = new TextFieldWidget(this.font, this.width / 2 + 8, 80, 80, 20, I18n.translate("structure_block.position.z", new Object[0]));
        this.inputPosZ.setMaxLength(15);
        this.inputPosZ.setText(Integer.toString(blockPos.getZ()));
        this.children.add(this.inputPosZ);
        BlockPos blockPos2 = this.structureBlock.getSize();
        this.inputSizeX = new TextFieldWidget(this.font, this.width / 2 - 152, 120, 80, 20, I18n.translate("structure_block.size.x", new Object[0]));
        this.inputSizeX.setMaxLength(15);
        this.inputSizeX.setText(Integer.toString(blockPos2.getX()));
        this.children.add(this.inputSizeX);
        this.inputSizeY = new TextFieldWidget(this.font, this.width / 2 - 72, 120, 80, 20, I18n.translate("structure_block.size.y", new Object[0]));
        this.inputSizeY.setMaxLength(15);
        this.inputSizeY.setText(Integer.toString(blockPos2.getY()));
        this.children.add(this.inputSizeY);
        this.inputSizeZ = new TextFieldWidget(this.font, this.width / 2 + 8, 120, 80, 20, I18n.translate("structure_block.size.z", new Object[0]));
        this.inputSizeZ.setMaxLength(15);
        this.inputSizeZ.setText(Integer.toString(blockPos2.getZ()));
        this.children.add(this.inputSizeZ);
        this.inputIntegrity = new TextFieldWidget(this.font, this.width / 2 - 152, 120, 80, 20, I18n.translate("structure_block.integrity.integrity", new Object[0]));
        this.inputIntegrity.setMaxLength(15);
        this.inputIntegrity.setText(this.decimalFormat.format(this.structureBlock.getIntegrity()));
        this.children.add(this.inputIntegrity);
        this.inputSeed = new TextFieldWidget(this.font, this.width / 2 - 72, 120, 80, 20, I18n.translate("structure_block.integrity.seed", new Object[0]));
        this.inputSeed.setMaxLength(31);
        this.inputSeed.setText(Long.toString(this.structureBlock.getSeed()));
        this.children.add(this.inputSeed);
        this.inputMetadata = new TextFieldWidget(this.font, this.width / 2 - 152, 120, 240, 20, I18n.translate("structure_block.custom_data", new Object[0]));
        this.inputMetadata.setMaxLength(128);
        this.inputMetadata.setText(this.structureBlock.getMetadata());
        this.children.add(this.inputMetadata);
        this.mirror = this.structureBlock.getMirror();
        this.updateMirrorButton();
        this.rotation = this.structureBlock.getRotation();
        this.updateRotationButton();
        this.mode = this.structureBlock.getMode();
        this.updateMode();
        this.ignoreEntities = this.structureBlock.shouldIgnoreEntities();
        this.updateIgnoreEntitiesButton();
        this.showAir = this.structureBlock.shouldShowAir();
        this.updateShowAirButton();
        this.showBoundingBox = this.structureBlock.shouldShowBoundingBox();
        this.updateShowBoundingBoxButton();
        this.setInitialFocus(this.inputName);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.inputName.getText();
        String string2 = this.inputPosX.getText();
        String string3 = this.inputPosY.getText();
        String string4 = this.inputPosZ.getText();
        String string5 = this.inputSizeX.getText();
        String string6 = this.inputSizeY.getText();
        String string7 = this.inputSizeZ.getText();
        String string8 = this.inputIntegrity.getText();
        String string9 = this.inputSeed.getText();
        String string10 = this.inputMetadata.getText();
        this.init(client, width, height);
        this.inputName.setText(string);
        this.inputPosX.setText(string2);
        this.inputPosY.setText(string3);
        this.inputPosZ.setText(string4);
        this.inputSizeX.setText(string5);
        this.inputSizeY.setText(string6);
        this.inputSizeZ.setText(string7);
        this.inputIntegrity.setText(string8);
        this.inputSeed.setText(string9);
        this.inputMetadata.setText(string10);
    }

    @Override
    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
    }

    private void updateIgnoreEntitiesButton() {
        boolean bl;
        boolean bl2 = bl = !this.structureBlock.shouldIgnoreEntities();
        if (bl) {
            this.buttonEntities.setMessage(I18n.translate("options.on", new Object[0]));
        } else {
            this.buttonEntities.setMessage(I18n.translate("options.off", new Object[0]));
        }
    }

    private void updateShowAirButton() {
        boolean bl = this.structureBlock.shouldShowAir();
        if (bl) {
            this.buttonShowAir.setMessage(I18n.translate("options.on", new Object[0]));
        } else {
            this.buttonShowAir.setMessage(I18n.translate("options.off", new Object[0]));
        }
    }

    private void updateShowBoundingBoxButton() {
        boolean bl = this.structureBlock.shouldShowBoundingBox();
        if (bl) {
            this.buttonShowBoundingBox.setMessage(I18n.translate("options.on", new Object[0]));
        } else {
            this.buttonShowBoundingBox.setMessage(I18n.translate("options.off", new Object[0]));
        }
    }

    private void updateMirrorButton() {
        BlockMirror blockMirror = this.structureBlock.getMirror();
        switch (blockMirror) {
            case NONE: {
                this.buttonMirror.setMessage("|");
                break;
            }
            case LEFT_RIGHT: {
                this.buttonMirror.setMessage("< >");
                break;
            }
            case FRONT_BACK: {
                this.buttonMirror.setMessage("^ v");
            }
        }
    }

    private void updateRotationButton() {
        this.buttonRotate0.active = true;
        this.buttonRotate90.active = true;
        this.buttonRotate180.active = true;
        this.buttonRotate270.active = true;
        switch (this.structureBlock.getRotation()) {
            case NONE: {
                this.buttonRotate0.active = false;
                break;
            }
            case CLOCKWISE_180: {
                this.buttonRotate180.active = false;
                break;
            }
            case COUNTERCLOCKWISE_90: {
                this.buttonRotate270.active = false;
                break;
            }
            case CLOCKWISE_90: {
                this.buttonRotate90.active = false;
            }
        }
    }

    private void updateMode() {
        this.inputName.setVisible(false);
        this.inputPosX.setVisible(false);
        this.inputPosY.setVisible(false);
        this.inputPosZ.setVisible(false);
        this.inputSizeX.setVisible(false);
        this.inputSizeY.setVisible(false);
        this.inputSizeZ.setVisible(false);
        this.inputIntegrity.setVisible(false);
        this.inputSeed.setVisible(false);
        this.inputMetadata.setVisible(false);
        this.buttonSave.visible = false;
        this.buttonLoad.visible = false;
        this.buttonDetect.visible = false;
        this.buttonEntities.visible = false;
        this.buttonMirror.visible = false;
        this.buttonRotate0.visible = false;
        this.buttonRotate90.visible = false;
        this.buttonRotate180.visible = false;
        this.buttonRotate270.visible = false;
        this.buttonShowAir.visible = false;
        this.buttonShowBoundingBox.visible = false;
        switch (this.structureBlock.getMode()) {
            case SAVE: {
                this.inputName.setVisible(true);
                this.inputPosX.setVisible(true);
                this.inputPosY.setVisible(true);
                this.inputPosZ.setVisible(true);
                this.inputSizeX.setVisible(true);
                this.inputSizeY.setVisible(true);
                this.inputSizeZ.setVisible(true);
                this.buttonSave.visible = true;
                this.buttonDetect.visible = true;
                this.buttonEntities.visible = true;
                this.buttonShowAir.visible = true;
                break;
            }
            case LOAD: {
                this.inputName.setVisible(true);
                this.inputPosX.setVisible(true);
                this.inputPosY.setVisible(true);
                this.inputPosZ.setVisible(true);
                this.inputIntegrity.setVisible(true);
                this.inputSeed.setVisible(true);
                this.buttonLoad.visible = true;
                this.buttonEntities.visible = true;
                this.buttonMirror.visible = true;
                this.buttonRotate0.visible = true;
                this.buttonRotate90.visible = true;
                this.buttonRotate180.visible = true;
                this.buttonRotate270.visible = true;
                this.buttonShowBoundingBox.visible = true;
                this.updateRotationButton();
                break;
            }
            case CORNER: {
                this.inputName.setVisible(true);
                break;
            }
            case DATA: {
                this.inputMetadata.setVisible(true);
            }
        }
        this.buttonMode.setMessage(I18n.translate("structure_block.mode." + this.structureBlock.getMode().asString(), new Object[0]));
    }

    private boolean method_2516(StructureBlockBlockEntity.Action action) {
        BlockPos blockPos = new BlockPos(this.parseInt(this.inputPosX.getText()), this.parseInt(this.inputPosY.getText()), this.parseInt(this.inputPosZ.getText()));
        BlockPos blockPos2 = new BlockPos(this.parseInt(this.inputSizeX.getText()), this.parseInt(this.inputSizeY.getText()), this.parseInt(this.inputSizeZ.getText()));
        float f = this.parseFloat(this.inputIntegrity.getText());
        long l = this.parseLong(this.inputSeed.getText());
        this.minecraft.getNetworkHandler().sendPacket(new UpdateStructureBlockC2SPacket(this.structureBlock.getPos(), action, this.structureBlock.getMode(), this.inputName.getText(), blockPos, blockPos2, this.structureBlock.getMirror(), this.structureBlock.getRotation(), this.inputMetadata.getText(), this.structureBlock.shouldIgnoreEntities(), this.structureBlock.shouldShowAir(), this.structureBlock.shouldShowBoundingBox(), f, l));
        return true;
    }

    private long parseLong(String string) {
        try {
            return Long.valueOf(string);
        }
        catch (NumberFormatException numberFormatException) {
            return 0L;
        }
    }

    private float parseFloat(String string) {
        try {
            return Float.valueOf(string).floatValue();
        }
        catch (NumberFormatException numberFormatException) {
            return 1.0f;
        }
    }

    private int parseInt(String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException numberFormatException) {
            return 0;
        }
    }

    @Override
    public void onClose() {
        this.cancel();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 257 || keyCode == 335) {
            this.done();
            return true;
        }
        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        int i;
        String string;
        this.renderBackground();
        StructureBlockMode structureBlockMode = this.structureBlock.getMode();
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 10, 0xFFFFFF);
        if (structureBlockMode != StructureBlockMode.DATA) {
            this.drawString(this.font, I18n.translate("structure_block.structure_name", new Object[0]), this.width / 2 - 153, 30, 0xA0A0A0);
            this.inputName.render(mouseX, mouseY, delta);
        }
        if (structureBlockMode == StructureBlockMode.LOAD || structureBlockMode == StructureBlockMode.SAVE) {
            this.drawString(this.font, I18n.translate("structure_block.position", new Object[0]), this.width / 2 - 153, 70, 0xA0A0A0);
            this.inputPosX.render(mouseX, mouseY, delta);
            this.inputPosY.render(mouseX, mouseY, delta);
            this.inputPosZ.render(mouseX, mouseY, delta);
            string = I18n.translate("structure_block.include_entities", new Object[0]);
            i = this.font.getStringWidth(string);
            this.drawString(this.font, string, this.width / 2 + 154 - i, 150, 0xA0A0A0);
        }
        if (structureBlockMode == StructureBlockMode.SAVE) {
            this.drawString(this.font, I18n.translate("structure_block.size", new Object[0]), this.width / 2 - 153, 110, 0xA0A0A0);
            this.inputSizeX.render(mouseX, mouseY, delta);
            this.inputSizeY.render(mouseX, mouseY, delta);
            this.inputSizeZ.render(mouseX, mouseY, delta);
            string = I18n.translate("structure_block.detect_size", new Object[0]);
            i = this.font.getStringWidth(string);
            this.drawString(this.font, string, this.width / 2 + 154 - i, 110, 0xA0A0A0);
            String string2 = I18n.translate("structure_block.show_air", new Object[0]);
            int j = this.font.getStringWidth(string2);
            this.drawString(this.font, string2, this.width / 2 + 154 - j, 70, 0xA0A0A0);
        }
        if (structureBlockMode == StructureBlockMode.LOAD) {
            this.drawString(this.font, I18n.translate("structure_block.integrity", new Object[0]), this.width / 2 - 153, 110, 0xA0A0A0);
            this.inputIntegrity.render(mouseX, mouseY, delta);
            this.inputSeed.render(mouseX, mouseY, delta);
            string = I18n.translate("structure_block.show_boundingbox", new Object[0]);
            i = this.font.getStringWidth(string);
            this.drawString(this.font, string, this.width / 2 + 154 - i, 70, 0xA0A0A0);
        }
        if (structureBlockMode == StructureBlockMode.DATA) {
            this.drawString(this.font, I18n.translate("structure_block.custom_data", new Object[0]), this.width / 2 - 153, 110, 0xA0A0A0);
            this.inputMetadata.render(mouseX, mouseY, delta);
        }
        string = "structure_block.mode_info." + structureBlockMode.asString();
        this.drawString(this.font, I18n.translate(string, new Object[0]), this.width / 2 - 153, 174, 0xA0A0A0);
        super.render(mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
