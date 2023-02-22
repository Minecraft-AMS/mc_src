/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Texts;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BookScreen
extends Screen {
    public static final Contents EMPTY_PROVIDER = new Contents(){

        @Override
        public int getLineCount() {
            return 0;
        }

        @Override
        public Text getLine(int line) {
            return new LiteralText("");
        }
    };
    public static final Identifier BOOK_TEXTURE = new Identifier("textures/gui/book.png");
    private Contents contents;
    private int pageIndex;
    private List<Text> cachedPage = Collections.emptyList();
    private int cachedPageIndex = -1;
    private PageTurnWidget nextPageButton;
    private PageTurnWidget previousPageButton;
    private final boolean pageTurnSound;

    public BookScreen(Contents pageProvider) {
        this(pageProvider, true);
    }

    public BookScreen() {
        this(EMPTY_PROVIDER, false);
    }

    private BookScreen(Contents contents, boolean playPageTurnSound) {
        super(NarratorManager.EMPTY);
        this.contents = contents;
        this.pageTurnSound = playPageTurnSound;
    }

    public void setPageProvider(Contents pageProvider) {
        this.contents = pageProvider;
        this.pageIndex = MathHelper.clamp(this.pageIndex, 0, pageProvider.getLineCount());
        this.updatePageButtons();
        this.cachedPageIndex = -1;
    }

    public boolean setPage(int index) {
        int i = MathHelper.clamp(index, 0, this.contents.getLineCount() - 1);
        if (i != this.pageIndex) {
            this.pageIndex = i;
            this.updatePageButtons();
            this.cachedPageIndex = -1;
            return true;
        }
        return false;
    }

    protected boolean jumpToPage(int page) {
        return this.setPage(page);
    }

    @Override
    protected void init() {
        this.addCloseButton();
        this.addPageButtons();
    }

    protected void addCloseButton() {
        this.addButton(new ButtonWidget(this.width / 2 - 100, 196, 200, 20, I18n.translate("gui.done", new Object[0]), buttonWidget -> this.minecraft.openScreen(null)));
    }

    protected void addPageButtons() {
        int i = (this.width - 192) / 2;
        int j = 2;
        this.nextPageButton = this.addButton(new PageTurnWidget(i + 116, 159, true, buttonWidget -> this.goToNextPage(), this.pageTurnSound));
        this.previousPageButton = this.addButton(new PageTurnWidget(i + 43, 159, false, buttonWidget -> this.goToPreviousPage(), this.pageTurnSound));
        this.updatePageButtons();
    }

    private int getPageCount() {
        return this.contents.getLineCount();
    }

    protected void goToPreviousPage() {
        if (this.pageIndex > 0) {
            --this.pageIndex;
        }
        this.updatePageButtons();
    }

    protected void goToNextPage() {
        if (this.pageIndex < this.getPageCount() - 1) {
            ++this.pageIndex;
        }
        this.updatePageButtons();
    }

    private void updatePageButtons() {
        this.nextPageButton.visible = this.pageIndex < this.getPageCount() - 1;
        this.previousPageButton.visible = this.pageIndex > 0;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        switch (keyCode) {
            case 266: {
                this.previousPageButton.onPress();
                return true;
            }
            case 267: {
                this.nextPageButton.onPress();
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(BOOK_TEXTURE);
        int i = (this.width - 192) / 2;
        int j = 2;
        this.blit(i, 2, 0, 0, 192, 192);
        String string = I18n.translate("book.pageIndicator", this.pageIndex + 1, Math.max(this.getPageCount(), 1));
        if (this.cachedPageIndex != this.pageIndex) {
            Text text = this.contents.getLineOrDefault(this.pageIndex);
            this.cachedPage = Texts.wrapLines(text, 114, this.font, true, true);
        }
        this.cachedPageIndex = this.pageIndex;
        int k = this.getStringWidth(string);
        this.font.draw(string, i - k + 192 - 44, 18.0f, 0);
        int l = Math.min(128 / this.font.fontHeight, this.cachedPage.size());
        for (int m = 0; m < l; ++m) {
            Text text2 = this.cachedPage.get(m);
            this.font.draw(text2.asFormattedString(), i + 36, 32 + m * this.font.fontHeight, 0);
        }
        Text text3 = this.getTextAt(mouseX, mouseY);
        if (text3 != null) {
            this.renderComponentHoverEffect(text3, mouseX, mouseY);
        }
        super.render(mouseX, mouseY, delta);
    }

    private int getStringWidth(String string) {
        return this.font.getStringWidth(this.font.isRightToLeft() ? this.font.mirror(string) : string);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Text text;
        if (button == 0 && (text = this.getTextAt(mouseX, mouseY)) != null && this.handleComponentClicked(text)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean handleComponentClicked(Text text) {
        ClickEvent clickEvent = text.getStyle().getClickEvent();
        if (clickEvent == null) {
            return false;
        }
        if (clickEvent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
            String string = clickEvent.getValue();
            try {
                int i = Integer.parseInt(string) - 1;
                return this.jumpToPage(i);
            }
            catch (Exception exception) {
                return false;
            }
        }
        boolean bl = super.handleComponentClicked(text);
        if (bl && clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
            this.minecraft.openScreen(null);
        }
        return bl;
    }

    @Nullable
    public Text getTextAt(double x, double y) {
        if (this.cachedPage == null) {
            return null;
        }
        int i = MathHelper.floor(x - (double)((this.width - 192) / 2) - 36.0);
        int j = MathHelper.floor(y - 2.0 - 30.0);
        if (i < 0 || j < 0) {
            return null;
        }
        int k = Math.min(128 / this.font.fontHeight, this.cachedPage.size());
        if (i <= 114 && j < this.minecraft.textRenderer.fontHeight * k + k) {
            int l = j / this.minecraft.textRenderer.fontHeight;
            if (l >= 0 && l < this.cachedPage.size()) {
                Text text = this.cachedPage.get(l);
                int m = 0;
                for (Text text2 : text) {
                    if (!(text2 instanceof LiteralText) || (m += this.minecraft.textRenderer.getStringWidth(text2.asFormattedString())) <= i) continue;
                    return text2;
                }
            }
            return null;
        }
        return null;
    }

    public static List<String> readPages(CompoundTag tag) {
        ListTag listTag = tag.getList("pages", 8).copy();
        ImmutableList.Builder builder = ImmutableList.builder();
        for (int i = 0; i < listTag.size(); ++i) {
            builder.add((Object)listTag.getString(i));
        }
        return builder.build();
    }

    @Environment(value=EnvType.CLIENT)
    public static class WritableBookContents
    implements Contents {
        private final List<String> lines;

        public WritableBookContents(ItemStack itemStack) {
            this.lines = WritableBookContents.getLines(itemStack);
        }

        private static List<String> getLines(ItemStack itemStack) {
            CompoundTag compoundTag = itemStack.getTag();
            return compoundTag != null ? BookScreen.readPages(compoundTag) : ImmutableList.of();
        }

        @Override
        public int getLineCount() {
            return this.lines.size();
        }

        @Override
        public Text getLine(int line) {
            return new LiteralText(this.lines.get(line));
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class WrittenBookContents
    implements Contents {
        private final List<String> lines;

        public WrittenBookContents(ItemStack itemStack) {
            this.lines = WrittenBookContents.getLines(itemStack);
        }

        private static List<String> getLines(ItemStack itemStack) {
            CompoundTag compoundTag = itemStack.getTag();
            if (compoundTag != null && WrittenBookItem.isValid(compoundTag)) {
                return BookScreen.readPages(compoundTag);
            }
            return ImmutableList.of((Object)new TranslatableText("book.invalid.tag", new Object[0]).formatted(Formatting.DARK_RED).asFormattedString());
        }

        @Override
        public int getLineCount() {
            return this.lines.size();
        }

        @Override
        public Text getLine(int line) {
            String string = this.lines.get(line);
            try {
                Text text = Text.Serializer.fromJson(string);
                if (text != null) {
                    return text;
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            return new LiteralText(string);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Contents {
        public int getLineCount();

        public Text getLine(int var1);

        default public Text getLineOrDefault(int line) {
            if (line >= 0 && line < this.getLineCount()) {
                return this.getLine(line);
            }
            return new LiteralText("");
        }

        public static Contents create(ItemStack stack) {
            Item item = stack.getItem();
            if (item == Items.WRITTEN_BOOK) {
                return new WrittenBookContents(stack);
            }
            if (item == Items.WRITABLE_BOOK) {
                return new WritableBookContents(stack);
            }
            return EMPTY_PROVIDER;
        }
    }
}

