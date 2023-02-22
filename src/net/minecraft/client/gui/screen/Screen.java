/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class Screen
extends AbstractParentElement
implements Drawable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<String> ALLOWED_PROTOCOLS = Sets.newHashSet((Object[])new String[]{"http", "https"});
    protected final Text title;
    protected final List<Element> children = Lists.newArrayList();
    @Nullable
    protected MinecraftClient minecraft;
    protected ItemRenderer itemRenderer;
    public int width;
    public int height;
    protected final List<AbstractButtonWidget> buttons = Lists.newArrayList();
    public boolean passEvents;
    protected TextRenderer font;
    private URI clickedLink;

    protected Screen(Text title) {
        this.title = title;
    }

    public Text getTitle() {
        return this.title;
    }

    public String getNarrationMessage() {
        return this.getTitle().getString();
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        for (int i = 0; i < this.buttons.size(); ++i) {
            this.buttons.get(i).render(mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && this.shouldCloseOnEsc()) {
            this.onClose();
            return true;
        }
        if (keyCode == 258) {
            boolean bl;
            boolean bl2 = bl = !Screen.hasShiftDown();
            if (!this.changeFocus(bl)) {
                this.changeFocus(bl);
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean shouldCloseOnEsc() {
        return true;
    }

    public void onClose() {
        this.minecraft.openScreen(null);
    }

    protected <T extends AbstractButtonWidget> T addButton(T button) {
        this.buttons.add(button);
        this.children.add(button);
        return button;
    }

    protected void renderTooltip(ItemStack stack, int x, int y) {
        this.renderTooltip(this.getTooltipFromItem(stack), x, y);
    }

    public List<String> getTooltipFromItem(ItemStack stack) {
        List<Text> list = stack.getTooltip(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
        ArrayList list2 = Lists.newArrayList();
        for (Text text : list) {
            list2.add(text.asFormattedString());
        }
        return list2;
    }

    public void renderTooltip(String text, int x, int y) {
        this.renderTooltip(Arrays.asList(text), x, y);
    }

    public void renderTooltip(List<String> text, int x, int y) {
        int j;
        if (text.isEmpty()) {
            return;
        }
        GlStateManager.disableRescaleNormal();
        DiffuseLighting.disable();
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
        int i = 0;
        for (String string : text) {
            j = this.font.getStringWidth(string);
            if (j <= i) continue;
            i = j;
        }
        int k = x + 12;
        int l = y - 12;
        j = i;
        int m = 8;
        if (text.size() > 1) {
            m += 2 + (text.size() - 1) * 10;
        }
        if (k + i > this.width) {
            k -= 28 + i;
        }
        if (l + m + 6 > this.height) {
            l = this.height - m - 6;
        }
        this.blitOffset = 300;
        this.itemRenderer.zOffset = 300.0f;
        int n = -267386864;
        this.fillGradient(k - 3, l - 4, k + j + 3, l - 3, -267386864, -267386864);
        this.fillGradient(k - 3, l + m + 3, k + j + 3, l + m + 4, -267386864, -267386864);
        this.fillGradient(k - 3, l - 3, k + j + 3, l + m + 3, -267386864, -267386864);
        this.fillGradient(k - 4, l - 3, k - 3, l + m + 3, -267386864, -267386864);
        this.fillGradient(k + j + 3, l - 3, k + j + 4, l + m + 3, -267386864, -267386864);
        int o = 0x505000FF;
        int p = 1344798847;
        this.fillGradient(k - 3, l - 3 + 1, k - 3 + 1, l + m + 3 - 1, 0x505000FF, 1344798847);
        this.fillGradient(k + j + 2, l - 3 + 1, k + j + 3, l + m + 3 - 1, 0x505000FF, 1344798847);
        this.fillGradient(k - 3, l - 3, k + j + 3, l - 3 + 1, 0x505000FF, 0x505000FF);
        this.fillGradient(k - 3, l + m + 2, k + j + 3, l + m + 3, 1344798847, 1344798847);
        for (int q = 0; q < text.size(); ++q) {
            String string2 = text.get(q);
            this.font.drawWithShadow(string2, k, l, -1);
            if (q == 0) {
                l += 2;
            }
            l += 10;
        }
        this.blitOffset = 0;
        this.itemRenderer.zOffset = 0.0f;
        GlStateManager.enableLighting();
        GlStateManager.enableDepthTest();
        DiffuseLighting.enable();
        GlStateManager.enableRescaleNormal();
    }

    protected void renderComponentHoverEffect(Text component, int x, int y) {
        if (component == null || component.getStyle().getHoverEvent() == null) {
            return;
        }
        HoverEvent hoverEvent = component.getStyle().getHoverEvent();
        if (hoverEvent.getAction() == HoverEvent.Action.SHOW_ITEM) {
            ItemStack itemStack = ItemStack.EMPTY;
            try {
                CompoundTag tag = StringNbtReader.parse(hoverEvent.getValue().getString());
                if (tag instanceof CompoundTag) {
                    itemStack = ItemStack.fromTag(tag);
                }
            }
            catch (CommandSyntaxException tag) {
                // empty catch block
            }
            if (itemStack.isEmpty()) {
                this.renderTooltip((Object)((Object)Formatting.RED) + "Invalid Item!", x, y);
            } else {
                this.renderTooltip(itemStack, x, y);
            }
        } else if (hoverEvent.getAction() == HoverEvent.Action.SHOW_ENTITY) {
            if (this.minecraft.options.advancedItemTooltips) {
                try {
                    CompoundTag compoundTag = StringNbtReader.parse(hoverEvent.getValue().getString());
                    ArrayList list = Lists.newArrayList();
                    Text text = Text.Serializer.fromJson(compoundTag.getString("name"));
                    if (text != null) {
                        list.add(text.asFormattedString());
                    }
                    if (compoundTag.contains("type", 8)) {
                        String string = compoundTag.getString("type");
                        list.add("Type: " + string);
                    }
                    list.add(compoundTag.getString("id"));
                    this.renderTooltip(list, x, y);
                }
                catch (JsonSyntaxException | CommandSyntaxException exception) {
                    this.renderTooltip((Object)((Object)Formatting.RED) + "Invalid Entity!", x, y);
                }
            }
        } else if (hoverEvent.getAction() == HoverEvent.Action.SHOW_TEXT) {
            this.renderTooltip(this.minecraft.textRenderer.wrapStringToWidthAsList(hoverEvent.getValue().asFormattedString(), Math.max(this.width / 2, 200)), x, y);
        }
        GlStateManager.disableLighting();
    }

    protected void insertText(String text, boolean override) {
    }

    public boolean handleComponentClicked(Text text) {
        if (text == null) {
            return false;
        }
        ClickEvent clickEvent = text.getStyle().getClickEvent();
        if (Screen.hasShiftDown()) {
            if (text.getStyle().getInsertion() != null) {
                this.insertText(text.getStyle().getInsertion(), false);
            }
        } else if (clickEvent != null) {
            block19: {
                if (clickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
                    if (!this.minecraft.options.chatLinks) {
                        return false;
                    }
                    try {
                        URI uRI = new URI(clickEvent.getValue());
                        String string = uRI.getScheme();
                        if (string == null) {
                            throw new URISyntaxException(clickEvent.getValue(), "Missing protocol");
                        }
                        if (!ALLOWED_PROTOCOLS.contains(string.toLowerCase(Locale.ROOT))) {
                            throw new URISyntaxException(clickEvent.getValue(), "Unsupported protocol: " + string.toLowerCase(Locale.ROOT));
                        }
                        if (this.minecraft.options.chatLinksPrompt) {
                            this.clickedLink = uRI;
                            this.minecraft.openScreen(new ConfirmChatLinkScreen(this::confirmLink, clickEvent.getValue(), false));
                            break block19;
                        }
                        this.openLink(uRI);
                    }
                    catch (URISyntaxException uRISyntaxException) {
                        LOGGER.error("Can't open url for {}", (Object)clickEvent, (Object)uRISyntaxException);
                    }
                } else if (clickEvent.getAction() == ClickEvent.Action.OPEN_FILE) {
                    URI uRI = new File(clickEvent.getValue()).toURI();
                    this.openLink(uRI);
                } else if (clickEvent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                    this.insertText(clickEvent.getValue(), true);
                } else if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
                    this.sendMessage(clickEvent.getValue(), false);
                } else {
                    LOGGER.error("Don't know how to handle {}", (Object)clickEvent);
                }
            }
            return true;
        }
        return false;
    }

    public void sendMessage(String message) {
        this.sendMessage(message, true);
    }

    public void sendMessage(String message, boolean toHud) {
        if (toHud) {
            this.minecraft.inGameHud.getChatHud().addToMessageHistory(message);
        }
        this.minecraft.player.sendChatMessage(message);
    }

    public void init(MinecraftClient client, int width, int height) {
        this.minecraft = client;
        this.itemRenderer = client.getItemRenderer();
        this.font = client.textRenderer;
        this.width = width;
        this.height = height;
        this.buttons.clear();
        this.children.clear();
        this.setFocused(null);
        this.init();
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public List<? extends Element> children() {
        return this.children;
    }

    protected void init() {
    }

    public void tick() {
    }

    public void removed() {
    }

    public void renderBackground() {
        this.renderBackground(0);
    }

    public void renderBackground(int alpha) {
        if (this.minecraft.world != null) {
            this.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            this.renderDirtBackground(alpha);
        }
    }

    public void renderDirtBackground(int alpha) {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        this.minecraft.getTextureManager().bindTexture(BACKGROUND_LOCATION);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        float f = 32.0f;
        bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0, this.height, 0.0).texture(0.0, (float)this.height / 32.0f + (float)alpha).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(this.width, this.height, 0.0).texture((float)this.width / 32.0f, (float)this.height / 32.0f + (float)alpha).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(this.width, 0.0, 0.0).texture((float)this.width / 32.0f, (double)alpha).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(0.0, 0.0, 0.0).texture(0.0, (double)alpha).color(64, 64, 64, 255).next();
        tessellator.draw();
    }

    public boolean isPauseScreen() {
        return true;
    }

    private void confirmLink(boolean open) {
        if (open) {
            this.openLink(this.clickedLink);
        }
        this.clickedLink = null;
        this.minecraft.openScreen(this);
    }

    private void openLink(URI link) {
        Util.getOperatingSystem().open(link);
    }

    public static boolean hasControlDown() {
        if (MinecraftClient.IS_SYSTEM_MAC) {
            return InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 343) || InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 347);
        }
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 341) || InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 345);
    }

    public static boolean hasShiftDown() {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 340) || InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 344);
    }

    public static boolean hasAltDown() {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 342) || InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 346);
    }

    public static boolean isCut(int code) {
        return code == 88 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isPaste(int code) {
        return code == 86 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isCopy(int code) {
        return code == 67 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public static boolean isSelectAll(int code) {
        return code == 65 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown();
    }

    public void resize(MinecraftClient client, int width, int height) {
        this.init(client, width, height);
    }

    public static void wrapScreenError(Runnable task, String errorTitle, String screenName) {
        try {
            task.run();
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, errorTitle);
            CrashReportSection crashReportSection = crashReport.addElement("Affected screen");
            crashReportSection.add("Screen name", () -> screenName);
            throw new CrashException(crashReport);
        }
    }

    protected boolean isValidCharacterForName(String name, char character, int cursorPos) {
        int i = name.indexOf(58);
        int j = name.indexOf(47);
        if (character == ':') {
            return (j == -1 || cursorPos <= j) && i == -1;
        }
        if (character == '/') {
            return cursorPos > i;
        }
        return character == '_' || character == '-' || character >= 'a' && character <= 'z' || character >= '0' && character <= '9' || character == '.';
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return true;
    }
}
