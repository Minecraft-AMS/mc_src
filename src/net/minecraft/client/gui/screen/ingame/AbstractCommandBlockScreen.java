/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.context.CommandContextBuilder
 *  com.mojang.brigadier.context.SuggestionContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.Suggestion
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Rect2i;
import net.minecraft.server.command.CommandSource;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.CommandBlockExecutor;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractCommandBlockScreen
extends Screen {
    protected TextFieldWidget consoleCommandTextField;
    protected TextFieldWidget previousOutputTextField;
    protected ButtonWidget doneButton;
    protected ButtonWidget cancelButton;
    protected ButtonWidget toggleTrackingOutputButton;
    protected boolean trackingOutput;
    protected final List<String> exceptions = Lists.newArrayList();
    protected int field_2757;
    protected int field_2756;
    protected ParseResults<CommandSource> parsedCommand;
    protected CompletableFuture<Suggestions> suggestionsFuture;
    protected SuggestionWindow suggestionWindow;
    private boolean completingSuggestion;

    public AbstractCommandBlockScreen() {
        super(NarratorManager.EMPTY);
    }

    @Override
    public void tick() {
        this.consoleCommandTextField.tick();
    }

    abstract CommandBlockExecutor getCommandExecutor();

    abstract int method_2364();

    @Override
    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        this.doneButton = this.addButton(new ButtonWidget(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.done", new Object[0]), buttonWidget -> this.commitAndClose()));
        this.cancelButton = this.addButton(new ButtonWidget(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, I18n.translate("gui.cancel", new Object[0]), buttonWidget -> this.onClose()));
        this.toggleTrackingOutputButton = this.addButton(new ButtonWidget(this.width / 2 + 150 - 20, this.method_2364(), 20, 20, "O", buttonWidget -> {
            CommandBlockExecutor commandBlockExecutor;
            commandBlockExecutor.shouldTrackOutput(!(commandBlockExecutor = this.getCommandExecutor()).isTrackingOutput());
            this.updateTrackedOutput();
        }));
        this.consoleCommandTextField = new TextFieldWidget(this.font, this.width / 2 - 150, 50, 300, 20, I18n.translate("advMode.command", new Object[0]));
        this.consoleCommandTextField.setMaxLength(32500);
        this.consoleCommandTextField.setRenderTextProvider(this::method_2348);
        this.consoleCommandTextField.setChangedListener(this::onCommandChanged);
        this.children.add(this.consoleCommandTextField);
        this.previousOutputTextField = new TextFieldWidget(this.font, this.width / 2 - 150, this.method_2364(), 276, 20, I18n.translate("advMode.previousOutput", new Object[0]));
        this.previousOutputTextField.setMaxLength(32500);
        this.previousOutputTextField.setEditable(false);
        this.previousOutputTextField.setText("-");
        this.children.add(this.previousOutputTextField);
        this.setInitialFocus(this.consoleCommandTextField);
        this.consoleCommandTextField.method_1876(true);
        this.updateCommand();
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.consoleCommandTextField.getText();
        this.init(client, width, height);
        this.setCommand(string);
        this.updateCommand();
    }

    protected void updateTrackedOutput() {
        if (this.getCommandExecutor().isTrackingOutput()) {
            this.toggleTrackingOutputButton.setMessage("O");
            this.previousOutputTextField.setText(this.getCommandExecutor().getLastOutput().getString());
        } else {
            this.toggleTrackingOutputButton.setMessage("X");
            this.previousOutputTextField.setText("-");
        }
    }

    protected void commitAndClose() {
        CommandBlockExecutor commandBlockExecutor = this.getCommandExecutor();
        this.syncSettingsToServer(commandBlockExecutor);
        if (!commandBlockExecutor.isTrackingOutput()) {
            commandBlockExecutor.setLastOutput(null);
        }
        this.minecraft.openScreen(null);
    }

    @Override
    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
    }

    protected abstract void syncSettingsToServer(CommandBlockExecutor var1);

    @Override
    public void onClose() {
        this.getCommandExecutor().shouldTrackOutput(this.trackingOutput);
        this.minecraft.openScreen(null);
    }

    private void onCommandChanged(String text) {
        this.updateCommand();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.suggestionWindow != null && this.suggestionWindow.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.getFocused() == this.consoleCommandTextField && keyCode == 258) {
            this.showSuggestions();
            return true;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 257 || keyCode == 335) {
            this.commitAndClose();
            return true;
        }
        if (keyCode == 258 && this.getFocused() == this.consoleCommandTextField) {
            this.showSuggestions();
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double d, double e, double amount) {
        if (this.suggestionWindow != null && this.suggestionWindow.mouseScrolled(MathHelper.clamp(amount, -1.0, 1.0))) {
            return true;
        }
        return super.mouseScrolled(d, e, amount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.suggestionWindow != null && this.suggestionWindow.mouseClicked((int)mouseX, (int)mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    protected void updateCommand() {
        int j;
        String string = this.consoleCommandTextField.getText();
        if (this.parsedCommand != null && !this.parsedCommand.getReader().getString().equals(string)) {
            this.parsedCommand = null;
        }
        if (!this.completingSuggestion) {
            this.consoleCommandTextField.setSuggestion(null);
            this.suggestionWindow = null;
        }
        this.exceptions.clear();
        CommandDispatcher<CommandSource> commandDispatcher = this.minecraft.player.networkHandler.getCommandDispatcher();
        StringReader stringReader = new StringReader(string);
        if (stringReader.canRead() && stringReader.peek() == '/') {
            stringReader.skip();
        }
        int i = stringReader.getCursor();
        if (this.parsedCommand == null) {
            this.parsedCommand = commandDispatcher.parse(stringReader, (Object)this.minecraft.player.networkHandler.getCommandSource());
        }
        if (!((j = this.consoleCommandTextField.getCursor()) < i || this.suggestionWindow != null && this.completingSuggestion)) {
            this.suggestionsFuture = commandDispatcher.getCompletionSuggestions(this.parsedCommand, j);
            this.suggestionsFuture.thenRun(() -> {
                if (!this.suggestionsFuture.isDone()) {
                    return;
                }
                this.updateCommandFeedback();
            });
        }
    }

    private void updateCommandFeedback() {
        if (this.suggestionsFuture.join().isEmpty() && !this.parsedCommand.getExceptions().isEmpty() && this.consoleCommandTextField.getCursor() == this.consoleCommandTextField.getText().length()) {
            int i = 0;
            for (Map.Entry entry : this.parsedCommand.getExceptions().entrySet()) {
                CommandSyntaxException commandSyntaxException = (CommandSyntaxException)((Object)entry.getValue());
                if (commandSyntaxException.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
                    ++i;
                    continue;
                }
                this.exceptions.add(commandSyntaxException.getMessage());
            }
            if (i > 0) {
                this.exceptions.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
            }
        }
        this.field_2757 = 0;
        this.field_2756 = this.width;
        if (this.exceptions.isEmpty()) {
            this.method_2356(Formatting.GRAY);
        }
        this.suggestionWindow = null;
        if (this.minecraft.options.autoSuggestions) {
            this.showSuggestions();
        }
    }

    private String method_2348(String string, int i) {
        if (this.parsedCommand != null) {
            return ChatScreen.getRenderText(this.parsedCommand, string, i);
        }
        return string;
    }

    private void method_2356(Formatting formatting) {
        CommandContextBuilder commandContextBuilder = this.parsedCommand.getContext();
        SuggestionContext suggestionContext = commandContextBuilder.findSuggestionContext(this.consoleCommandTextField.getCursor());
        Map map = this.minecraft.player.networkHandler.getCommandDispatcher().getSmartUsage(suggestionContext.parent, (Object)this.minecraft.player.networkHandler.getCommandSource());
        ArrayList list = Lists.newArrayList();
        int i = 0;
        for (Map.Entry entry : map.entrySet()) {
            if (entry.getKey() instanceof LiteralCommandNode) continue;
            list.add((Object)((Object)formatting) + (String)entry.getValue());
            i = Math.max(i, this.font.getStringWidth((String)entry.getValue()));
        }
        if (!list.isEmpty()) {
            this.exceptions.addAll(list);
            this.field_2757 = MathHelper.clamp(this.consoleCommandTextField.getCharacterX(suggestionContext.startPos), 0, this.consoleCommandTextField.getCharacterX(0) + this.consoleCommandTextField.method_1859() - i);
            this.field_2756 = i;
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, I18n.translate("advMode.setCommand", new Object[0]), this.width / 2, 20, 0xFFFFFF);
        this.drawString(this.font, I18n.translate("advMode.command", new Object[0]), this.width / 2 - 150, 40, 0xA0A0A0);
        this.consoleCommandTextField.render(mouseX, mouseY, delta);
        int i = 75;
        if (!this.previousOutputTextField.getText().isEmpty()) {
            this.drawString(this.font, I18n.translate("advMode.previousOutput", new Object[0]), this.width / 2 - 150, (i += 5 * this.font.fontHeight + 1 + this.method_2364() - 135) + 4, 0xA0A0A0);
            this.previousOutputTextField.render(mouseX, mouseY, delta);
        }
        super.render(mouseX, mouseY, delta);
        if (this.suggestionWindow != null) {
            this.suggestionWindow.draw(mouseX, mouseY);
        } else {
            i = 0;
            for (String string : this.exceptions) {
                AbstractCommandBlockScreen.fill(this.field_2757 - 1, 72 + 12 * i, this.field_2757 + this.field_2756 + 1, 84 + 12 * i, Integer.MIN_VALUE);
                this.font.drawWithShadow(string, this.field_2757, 74 + 12 * i, -1);
                ++i;
            }
        }
    }

    public void showSuggestions() {
        Suggestions suggestions;
        if (this.suggestionsFuture != null && this.suggestionsFuture.isDone() && !(suggestions = this.suggestionsFuture.join()).isEmpty()) {
            int i = 0;
            for (Suggestion suggestion : suggestions.getList()) {
                i = Math.max(i, this.font.getStringWidth(suggestion.getText()));
            }
            int j = MathHelper.clamp(this.consoleCommandTextField.getCharacterX(suggestions.getRange().getStart()), 0, this.consoleCommandTextField.getCharacterX(0) + this.consoleCommandTextField.method_1859() - i);
            this.suggestionWindow = new SuggestionWindow(j, 72, i, suggestions);
        }
    }

    protected void setCommand(String command) {
        this.consoleCommandTextField.setText(command);
    }

    @Nullable
    private static String suggestSuffix(String typed, String suggestion) {
        if (suggestion.startsWith(typed)) {
            return suggestion.substring(typed.length());
        }
        return null;
    }

    @Environment(value=EnvType.CLIENT)
    class SuggestionWindow {
        private final Rect2i area;
        private final Suggestions suggestions;
        private final String typedText;
        private int inWindowIndex;
        private int selection;
        private Vec2f mouse = Vec2f.ZERO;
        private boolean completed;

        private SuggestionWindow(int x, int y, int width, Suggestions suggestions) {
            this.area = new Rect2i(x - 1, y, width + 1, Math.min(suggestions.getList().size(), 7) * 12);
            this.suggestions = suggestions;
            this.typedText = AbstractCommandBlockScreen.this.consoleCommandTextField.getText();
            this.select(0);
        }

        public void draw(int mouseX, int mouseY) {
            Message message;
            boolean bl4;
            int i = Math.min(this.suggestions.getList().size(), 7);
            int j = Integer.MIN_VALUE;
            int k = -5592406;
            boolean bl = this.inWindowIndex > 0;
            boolean bl2 = this.suggestions.getList().size() > this.inWindowIndex + i;
            boolean bl3 = bl || bl2;
            boolean bl5 = bl4 = this.mouse.x != (float)mouseX || this.mouse.y != (float)mouseY;
            if (bl4) {
                this.mouse = new Vec2f(mouseX, mouseY);
            }
            if (bl3) {
                int l;
                DrawableHelper.fill(this.area.getX(), this.area.getY() - 1, this.area.getX() + this.area.getWidth(), this.area.getY(), Integer.MIN_VALUE);
                DrawableHelper.fill(this.area.getX(), this.area.getY() + this.area.getHeight(), this.area.getX() + this.area.getWidth(), this.area.getY() + this.area.getHeight() + 1, Integer.MIN_VALUE);
                if (bl) {
                    for (l = 0; l < this.area.getWidth(); ++l) {
                        if (l % 2 != 0) continue;
                        DrawableHelper.fill(this.area.getX() + l, this.area.getY() - 1, this.area.getX() + l + 1, this.area.getY(), -1);
                    }
                }
                if (bl2) {
                    for (l = 0; l < this.area.getWidth(); ++l) {
                        if (l % 2 != 0) continue;
                        DrawableHelper.fill(this.area.getX() + l, this.area.getY() + this.area.getHeight(), this.area.getX() + l + 1, this.area.getY() + this.area.getHeight() + 1, -1);
                    }
                }
            }
            boolean bl52 = false;
            for (int m = 0; m < i; ++m) {
                Suggestion suggestion = (Suggestion)this.suggestions.getList().get(m + this.inWindowIndex);
                DrawableHelper.fill(this.area.getX(), this.area.getY() + 12 * m, this.area.getX() + this.area.getWidth(), this.area.getY() + 12 * m + 12, Integer.MIN_VALUE);
                if (mouseX > this.area.getX() && mouseX < this.area.getX() + this.area.getWidth() && mouseY > this.area.getY() + 12 * m && mouseY < this.area.getY() + 12 * m + 12) {
                    if (bl4) {
                        this.select(m + this.inWindowIndex);
                    }
                    bl52 = true;
                }
                AbstractCommandBlockScreen.this.font.drawWithShadow(suggestion.getText(), this.area.getX() + 1, this.area.getY() + 2 + 12 * m, m + this.inWindowIndex == this.selection ? -256 : -5592406);
            }
            if (bl52 && (message = ((Suggestion)this.suggestions.getList().get(this.selection)).getTooltip()) != null) {
                AbstractCommandBlockScreen.this.renderTooltip(Texts.toText(message).asFormattedString(), mouseX, mouseY);
            }
        }

        public boolean mouseClicked(int x, int y, int button) {
            if (!this.area.contains(x, y)) {
                return false;
            }
            int i = (y - this.area.getY()) / 12 + this.inWindowIndex;
            if (i >= 0 && i < this.suggestions.getList().size()) {
                this.select(i);
                this.complete();
            }
            return true;
        }

        public boolean mouseScrolled(double amount) {
            int j;
            int i = (int)(((AbstractCommandBlockScreen)AbstractCommandBlockScreen.this).minecraft.mouse.getX() * (double)((AbstractCommandBlockScreen)AbstractCommandBlockScreen.this).minecraft.window.getScaledWidth() / (double)((AbstractCommandBlockScreen)AbstractCommandBlockScreen.this).minecraft.window.getWidth());
            if (this.area.contains(i, j = (int)(((AbstractCommandBlockScreen)AbstractCommandBlockScreen.this).minecraft.mouse.getY() * (double)((AbstractCommandBlockScreen)AbstractCommandBlockScreen.this).minecraft.window.getScaledHeight() / (double)((AbstractCommandBlockScreen)AbstractCommandBlockScreen.this).minecraft.window.getHeight()))) {
                this.inWindowIndex = MathHelper.clamp((int)((double)this.inWindowIndex - amount), 0, Math.max(this.suggestions.getList().size() - 7, 0));
                return true;
            }
            return false;
        }

        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (keyCode == 265) {
                this.scroll(-1);
                this.completed = false;
                return true;
            }
            if (keyCode == 264) {
                this.scroll(1);
                this.completed = false;
                return true;
            }
            if (keyCode == 258) {
                if (this.completed) {
                    this.scroll(Screen.hasShiftDown() ? -1 : 1);
                }
                this.complete();
                return true;
            }
            if (keyCode == 256) {
                this.discard();
                return true;
            }
            return false;
        }

        public void scroll(int offset) {
            this.select(this.selection + offset);
            int i = this.inWindowIndex;
            int j = this.inWindowIndex + 7 - 1;
            if (this.selection < i) {
                this.inWindowIndex = MathHelper.clamp(this.selection, 0, Math.max(this.suggestions.getList().size() - 7, 0));
            } else if (this.selection > j) {
                this.inWindowIndex = MathHelper.clamp(this.selection - 7, 0, Math.max(this.suggestions.getList().size() - 7, 0));
            }
        }

        public void select(int index) {
            this.selection = index;
            if (this.selection < 0) {
                this.selection += this.suggestions.getList().size();
            }
            if (this.selection >= this.suggestions.getList().size()) {
                this.selection -= this.suggestions.getList().size();
            }
            Suggestion suggestion = (Suggestion)this.suggestions.getList().get(this.selection);
            AbstractCommandBlockScreen.this.consoleCommandTextField.setSuggestion(AbstractCommandBlockScreen.suggestSuffix(AbstractCommandBlockScreen.this.consoleCommandTextField.getText(), suggestion.apply(this.typedText)));
        }

        public void complete() {
            Suggestion suggestion = (Suggestion)this.suggestions.getList().get(this.selection);
            AbstractCommandBlockScreen.this.completingSuggestion = true;
            AbstractCommandBlockScreen.this.setCommand(suggestion.apply(this.typedText));
            int i = suggestion.getRange().getStart() + suggestion.getText().length();
            AbstractCommandBlockScreen.this.consoleCommandTextField.setSelectionStart(i);
            AbstractCommandBlockScreen.this.consoleCommandTextField.method_1884(i);
            this.select(this.selection);
            AbstractCommandBlockScreen.this.completingSuggestion = false;
            this.completed = true;
        }

        public void discard() {
            AbstractCommandBlockScreen.this.suggestionWindow = null;
        }
    }
}

