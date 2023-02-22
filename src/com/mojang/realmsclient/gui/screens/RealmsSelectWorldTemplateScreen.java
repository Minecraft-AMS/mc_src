/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Either
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Either;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.gui.screens.RealmsScreenWithCallback;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.TextRenderingUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsSelectWorldTemplateScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final RealmsScreenWithCallback<WorldTemplate> lastScreen;
    private WorldTemplateObjectSelectionList field_20071;
    private int selectedTemplate = -1;
    private String title;
    private RealmsButton selectButton;
    private RealmsButton trailerButton;
    private RealmsButton publisherButton;
    private String toolTip;
    private String currentLink;
    private final RealmsServer.WorldType worldType;
    private int clicks;
    private String warning;
    private String warningURL;
    private boolean displayWarning;
    private boolean hoverWarning;
    private List<TextRenderingUtils.Line> noTemplatesMessage;

    public RealmsSelectWorldTemplateScreen(RealmsScreenWithCallback<WorldTemplate> realmsScreenWithCallback, RealmsServer.WorldType worldType) {
        this(realmsScreenWithCallback, worldType, null);
    }

    public RealmsSelectWorldTemplateScreen(RealmsScreenWithCallback<WorldTemplate> realmsScreenWithCallback, RealmsServer.WorldType worldType, @Nullable WorldTemplatePaginatedList worldTemplatePaginatedList) {
        this.lastScreen = realmsScreenWithCallback;
        this.worldType = worldType;
        if (worldTemplatePaginatedList == null) {
            this.field_20071 = new WorldTemplateObjectSelectionList();
            this.method_21415(new WorldTemplatePaginatedList(10));
        } else {
            this.field_20071 = new WorldTemplateObjectSelectionList(Lists.newArrayList(worldTemplatePaginatedList.templates));
            this.method_21415(worldTemplatePaginatedList);
        }
        this.title = RealmsSelectWorldTemplateScreen.getLocalizedString("mco.template.title");
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setWarning(String string) {
        this.warning = string;
        this.displayWarning = true;
    }

    @Override
    public boolean mouseClicked(double x, double y, int buttonNum) {
        if (this.hoverWarning && this.warningURL != null) {
            RealmsUtil.browseTo("https://beta.minecraft.net/realms/adventure-maps-in-1-9");
            return true;
        }
        return super.mouseClicked(x, y, buttonNum);
    }

    @Override
    public void init() {
        this.setKeyboardHandlerSendRepeatsToGui(true);
        this.field_20071 = new WorldTemplateObjectSelectionList(this.field_20071.method_21450());
        this.trailerButton = new RealmsButton(2, this.width() / 2 - 206, this.height() - 32, 100, 20, RealmsSelectWorldTemplateScreen.getLocalizedString("mco.template.button.trailer")){

            @Override
            public void onPress() {
                RealmsSelectWorldTemplateScreen.this.onTrailer();
            }
        };
        this.buttonsAdd(this.trailerButton);
        this.selectButton = new RealmsButton(1, this.width() / 2 - 100, this.height() - 32, 100, 20, RealmsSelectWorldTemplateScreen.getLocalizedString("mco.template.button.select")){

            @Override
            public void onPress() {
                RealmsSelectWorldTemplateScreen.this.selectTemplate();
            }
        };
        this.buttonsAdd(this.selectButton);
        this.buttonsAdd(new RealmsButton(0, this.width() / 2 + 6, this.height() - 32, 100, 20, RealmsSelectWorldTemplateScreen.getLocalizedString(this.worldType == RealmsServer.WorldType.MINIGAME ? "gui.cancel" : "gui.back")){

            @Override
            public void onPress() {
                RealmsSelectWorldTemplateScreen.this.backButtonClicked();
            }
        });
        this.publisherButton = new RealmsButton(3, this.width() / 2 + 112, this.height() - 32, 100, 20, RealmsSelectWorldTemplateScreen.getLocalizedString("mco.template.button.publisher")){

            @Override
            public void onPress() {
                RealmsSelectWorldTemplateScreen.this.onPublish();
            }
        };
        this.buttonsAdd(this.publisherButton);
        this.selectButton.active(false);
        this.trailerButton.setVisible(false);
        this.publisherButton.setVisible(false);
        this.addWidget(this.field_20071);
        this.focusOn(this.field_20071);
        Realms.narrateNow(Stream.of(this.title, this.warning).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    private void updateButtonStates() {
        this.publisherButton.setVisible(this.shouldPublisherBeVisible());
        this.trailerButton.setVisible(this.shouldTrailerBeVisible());
        this.selectButton.active(this.shouldSelectButtonBeActive());
    }

    private boolean shouldSelectButtonBeActive() {
        return this.selectedTemplate != -1;
    }

    private boolean shouldPublisherBeVisible() {
        return this.selectedTemplate != -1 && !this.method_21434().link.isEmpty();
    }

    private WorldTemplate method_21434() {
        return this.field_20071.method_21447(this.selectedTemplate);
    }

    private boolean shouldTrailerBeVisible() {
        return this.selectedTemplate != -1 && !this.method_21434().trailer.isEmpty();
    }

    @Override
    public void tick() {
        super.tick();
        --this.clicks;
        if (this.clicks < 0) {
            this.clicks = 0;
        }
    }

    @Override
    public boolean keyPressed(int eventKey, int scancode, int mods) {
        switch (eventKey) {
            case 256: {
                this.backButtonClicked();
                return true;
            }
        }
        return super.keyPressed(eventKey, scancode, mods);
    }

    private void backButtonClicked() {
        this.lastScreen.callback(null);
        Realms.setScreen(this.lastScreen);
    }

    private void selectTemplate() {
        if (this.selectedTemplate >= 0 && this.selectedTemplate < this.field_20071.getItemCount()) {
            WorldTemplate worldTemplate = this.method_21434();
            this.lastScreen.callback(worldTemplate);
        }
    }

    private void onTrailer() {
        if (this.selectedTemplate >= 0 && this.selectedTemplate < this.field_20071.getItemCount()) {
            WorldTemplate worldTemplate = this.method_21434();
            if (!"".equals(worldTemplate.trailer)) {
                RealmsUtil.browseTo(worldTemplate.trailer);
            }
        }
    }

    private void onPublish() {
        if (this.selectedTemplate >= 0 && this.selectedTemplate < this.field_20071.getItemCount()) {
            WorldTemplate worldTemplate = this.method_21434();
            if (!"".equals(worldTemplate.link)) {
                RealmsUtil.browseTo(worldTemplate.link);
            }
        }
    }

    private void method_21415(final WorldTemplatePaginatedList worldTemplatePaginatedList) {
        new Thread("realms-template-fetcher"){

            @Override
            public void run() {
                WorldTemplatePaginatedList worldTemplatePaginatedList2 = worldTemplatePaginatedList;
                RealmsClient realmsClient = RealmsClient.createRealmsClient();
                while (worldTemplatePaginatedList2 != null) {
                    Either either = RealmsSelectWorldTemplateScreen.this.method_21416(worldTemplatePaginatedList2, realmsClient);
                    worldTemplatePaginatedList2 = Realms.execute(() -> {
                        if (either.right().isPresent()) {
                            LOGGER.error("Couldn't fetch templates: {}", either.right().get());
                            if (RealmsSelectWorldTemplateScreen.this.field_20071.method_21446()) {
                                RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose(RealmsScreen.getLocalizedString("mco.template.select.failure"), new TextRenderingUtils.LineSegment[0]);
                            }
                            return null;
                        }
                        assert (either.left().isPresent());
                        WorldTemplatePaginatedList worldTemplatePaginatedList2 = (WorldTemplatePaginatedList)either.left().get();
                        for (WorldTemplate worldTemplate : worldTemplatePaginatedList2.templates) {
                            RealmsSelectWorldTemplateScreen.this.field_20071.addEntry(worldTemplate);
                        }
                        if (worldTemplatePaginatedList2.templates.isEmpty()) {
                            if (RealmsSelectWorldTemplateScreen.this.field_20071.method_21446()) {
                                String string = RealmsScreen.getLocalizedString("mco.template.select.none", "%link");
                                TextRenderingUtils.LineSegment lineSegment = TextRenderingUtils.LineSegment.link(RealmsScreen.getLocalizedString("mco.template.select.none.linkTitle"), "https://minecraft.net/realms/content-creator/");
                                RealmsSelectWorldTemplateScreen.this.noTemplatesMessage = TextRenderingUtils.decompose(string, lineSegment);
                            }
                            return null;
                        }
                        return worldTemplatePaginatedList2;
                    }).join();
                }
            }
        }.start();
    }

    private Either<WorldTemplatePaginatedList, String> method_21416(WorldTemplatePaginatedList worldTemplatePaginatedList, RealmsClient realmsClient) {
        try {
            return Either.left((Object)realmsClient.fetchWorldTemplates(worldTemplatePaginatedList.page + 1, worldTemplatePaginatedList.size, this.worldType));
        }
        catch (RealmsServiceException realmsServiceException) {
            return Either.right((Object)realmsServiceException.getMessage());
        }
    }

    @Override
    public void render(int xm, int ym, float a) {
        this.toolTip = null;
        this.currentLink = null;
        this.hoverWarning = false;
        this.renderBackground();
        this.field_20071.render(xm, ym, a);
        if (this.noTemplatesMessage != null) {
            this.method_21414(xm, ym, this.noTemplatesMessage);
        }
        this.drawCenteredString(this.title, this.width() / 2, 13, 0xFFFFFF);
        if (this.displayWarning) {
            int k;
            int i;
            String[] strings = this.warning.split("\\\\n");
            for (i = 0; i < strings.length; ++i) {
                int j = this.fontWidth(strings[i]);
                k = this.width() / 2 - j / 2;
                int l = RealmsConstants.row(-1 + i);
                if (xm < k || xm > k + j || ym < l || ym > l + this.fontLineHeight()) continue;
                this.hoverWarning = true;
            }
            for (i = 0; i < strings.length; ++i) {
                String string = strings[i];
                k = 0xA0A0A0;
                if (this.warningURL != null) {
                    if (this.hoverWarning) {
                        k = 7107012;
                        string = "\u00a7n" + string;
                    } else {
                        k = 0x3366BB;
                    }
                }
                this.drawCenteredString(string, this.width() / 2, RealmsConstants.row(-1 + i), k);
            }
        }
        super.render(xm, ym, a);
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(this.toolTip, xm, ym);
        }
    }

    private void method_21414(int i, int j, List<TextRenderingUtils.Line> list) {
        for (int k = 0; k < list.size(); ++k) {
            TextRenderingUtils.Line line = list.get(k);
            int l = RealmsConstants.row(4 + k);
            int m = line.segments.stream().mapToInt(lineSegment -> this.fontWidth(lineSegment.renderedText())).sum();
            int n = this.width() / 2 - m / 2;
            for (TextRenderingUtils.LineSegment lineSegment2 : line.segments) {
                int o = lineSegment2.isLink() ? 0x3366BB : 0xFFFFFF;
                int p = this.draw(lineSegment2.renderedText(), n, l, o, true);
                if (lineSegment2.isLink() && i > n && i < p && j > l - 3 && j < l + 8) {
                    this.toolTip = lineSegment2.getLinkUrl();
                    this.currentLink = lineSegment2.getLinkUrl();
                }
                n = p;
            }
        }
    }

    protected void renderMousehoverTooltip(String msg, int x, int y) {
        if (msg == null) {
            return;
        }
        int i = x + 12;
        int j = y - 12;
        int k = this.fontWidth(msg);
        this.fillGradient(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
        this.fontDrawShadow(msg, i, j, 0xFFFFFF);
    }

    @Environment(value=EnvType.CLIENT)
    class WorldTemplateObjectSelectionListEntry
    extends RealmListEntry {
        final WorldTemplate mTemplate;

        public WorldTemplateObjectSelectionListEntry(WorldTemplate template) {
            this.mTemplate = template;
        }

        @Override
        public void render(int index, int rowTop, int rowLeft, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float a) {
            this.renderWorldTemplateItem(this.mTemplate, rowLeft, rowTop, mouseX, mouseY);
        }

        private void renderWorldTemplateItem(WorldTemplate worldTemplate, int x, int y, int mouseX, int mouseY) {
            int i = x + 45 + 20;
            RealmsSelectWorldTemplateScreen.this.drawString(worldTemplate.name, i, y + 2, 0xFFFFFF);
            RealmsSelectWorldTemplateScreen.this.drawString(worldTemplate.author, i, y + 15, 0x808080);
            RealmsSelectWorldTemplateScreen.this.drawString(worldTemplate.version, i + 227 - RealmsSelectWorldTemplateScreen.this.fontWidth(worldTemplate.version), y + 1, 0x808080);
            if (!("".equals(worldTemplate.link) && "".equals(worldTemplate.trailer) && "".equals(worldTemplate.recommendedPlayers))) {
                this.drawIcons(i - 1, y + 25, mouseX, mouseY, worldTemplate.link, worldTemplate.trailer, worldTemplate.recommendedPlayers);
            }
            this.drawImage(x, y + 1, mouseX, mouseY, worldTemplate);
        }

        private void drawImage(int x, int y, int xm, int ym, WorldTemplate worldTemplate) {
            RealmsTextureManager.bindWorldTemplate(worldTemplate.id, worldTemplate.image);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RealmsScreen.blit(x + 1, y + 1, 0.0f, 0.0f, 38, 38, 38, 38);
            RealmsScreen.bind("realms:textures/gui/realms/slot_frame.png");
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RealmsScreen.blit(x, y, 0.0f, 0.0f, 40, 40, 40, 40);
        }

        private void drawIcons(int x, int y, int xm, int ym, String link, String trailerLink, String recommendedPlayers) {
            if (!"".equals(recommendedPlayers)) {
                RealmsSelectWorldTemplateScreen.this.drawString(recommendedPlayers, x, y + 4, 0x808080);
            }
            int i = "".equals(recommendedPlayers) ? 0 : RealmsSelectWorldTemplateScreen.this.fontWidth(recommendedPlayers) + 2;
            boolean bl = false;
            boolean bl2 = false;
            if (xm >= x + i && xm <= x + i + 32 && ym >= y && ym <= y + 15 && ym < RealmsSelectWorldTemplateScreen.this.height() - 15 && ym > 32) {
                if (xm <= x + 15 + i && xm > i) {
                    if ("".equals(link)) {
                        bl2 = true;
                    } else {
                        bl = true;
                    }
                } else if (!"".equals(link)) {
                    bl2 = true;
                }
            }
            if (!"".equals(link)) {
                RealmsScreen.bind("realms:textures/gui/realms/link_icons.png");
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.pushMatrix();
                RenderSystem.scalef(1.0f, 1.0f, 1.0f);
                RealmsScreen.blit(x + i, y, bl ? 15.0f : 0.0f, 0.0f, 15, 15, 30, 15);
                RenderSystem.popMatrix();
            }
            if (!"".equals(trailerLink)) {
                RealmsScreen.bind("realms:textures/gui/realms/trailer_icons.png");
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.pushMatrix();
                RenderSystem.scalef(1.0f, 1.0f, 1.0f);
                RealmsScreen.blit(x + i + ("".equals(link) ? 0 : 17), y, bl2 ? 15.0f : 0.0f, 0.0f, 15, 15, 30, 15);
                RenderSystem.popMatrix();
            }
            if (bl && !"".equals(link)) {
                RealmsSelectWorldTemplateScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.template.info.tooltip");
                RealmsSelectWorldTemplateScreen.this.currentLink = link;
            } else if (bl2 && !"".equals(trailerLink)) {
                RealmsSelectWorldTemplateScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.template.trailer.tooltip");
                RealmsSelectWorldTemplateScreen.this.currentLink = trailerLink;
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    class WorldTemplateObjectSelectionList
    extends RealmsObjectSelectionList<WorldTemplateObjectSelectionListEntry> {
        public WorldTemplateObjectSelectionList() {
            this(Collections.emptyList());
        }

        public WorldTemplateObjectSelectionList(Iterable<WorldTemplate> iterable) {
            super(RealmsSelectWorldTemplateScreen.this.width(), RealmsSelectWorldTemplateScreen.this.height(), RealmsSelectWorldTemplateScreen.this.displayWarning ? RealmsConstants.row(1) : 32, RealmsSelectWorldTemplateScreen.this.height() - 40, 46);
            iterable.forEach(this::addEntry);
        }

        @Override
        public void addEntry(WorldTemplate template) {
            this.addEntry(new WorldTemplateObjectSelectionListEntry(template));
        }

        @Override
        public boolean mouseClicked(double xm, double ym, int buttonNum) {
            if (buttonNum == 0 && ym >= (double)this.y0() && ym <= (double)this.y1()) {
                int i = this.width() / 2 - 150;
                if (RealmsSelectWorldTemplateScreen.this.currentLink != null) {
                    RealmsUtil.browseTo(RealmsSelectWorldTemplateScreen.this.currentLink);
                }
                int j = (int)Math.floor(ym - (double)this.y0()) - this.headerHeight() + this.getScroll() - 4;
                int k = j / this.itemHeight();
                if (xm >= (double)i && xm < (double)this.getScrollbarPosition() && k >= 0 && j >= 0 && k < this.getItemCount()) {
                    this.selectItem(k);
                    this.itemClicked(j, k, xm, ym, this.width());
                    if (k >= RealmsSelectWorldTemplateScreen.this.field_20071.getItemCount()) {
                        return super.mouseClicked(xm, ym, buttonNum);
                    }
                    RealmsSelectWorldTemplateScreen.this.selectedTemplate = k;
                    RealmsSelectWorldTemplateScreen.this.updateButtonStates();
                    RealmsSelectWorldTemplateScreen.this.clicks = RealmsSelectWorldTemplateScreen.this.clicks + 7;
                    if (RealmsSelectWorldTemplateScreen.this.clicks >= 10) {
                        RealmsSelectWorldTemplateScreen.this.selectTemplate();
                    }
                    return true;
                }
            }
            return super.mouseClicked(xm, ym, buttonNum);
        }

        @Override
        public void selectItem(int item) {
            RealmsSelectWorldTemplateScreen.this.selectedTemplate = item;
            this.setSelected(item);
            if (item != -1) {
                WorldTemplate worldTemplate = RealmsSelectWorldTemplateScreen.this.field_20071.method_21447(item);
                String string = RealmsScreen.getLocalizedString("narrator.select.list.position", item + 1, RealmsSelectWorldTemplateScreen.this.field_20071.getItemCount());
                String string2 = RealmsScreen.getLocalizedString("mco.template.select.narrate.version", worldTemplate.version);
                String string3 = RealmsScreen.getLocalizedString("mco.template.select.narrate.authors", worldTemplate.author);
                String string4 = Realms.joinNarrations(Arrays.asList(worldTemplate.name, string3, worldTemplate.recommendedPlayers, string2, string));
                Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", string4));
            }
            RealmsSelectWorldTemplateScreen.this.updateButtonStates();
        }

        @Override
        public void itemClicked(int clickSlotPos, int slot, double xm, double ym, int width) {
            if (slot >= RealmsSelectWorldTemplateScreen.this.field_20071.getItemCount()) {
                return;
            }
        }

        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 46;
        }

        @Override
        public int getRowWidth() {
            return 300;
        }

        @Override
        public void renderBackground() {
            RealmsSelectWorldTemplateScreen.this.renderBackground();
        }

        @Override
        public boolean isFocused() {
            return RealmsSelectWorldTemplateScreen.this.isFocused(this);
        }

        public boolean method_21446() {
            return this.getItemCount() == 0;
        }

        public WorldTemplate method_21447(int i) {
            return ((WorldTemplateObjectSelectionListEntry)this.children().get((int)i)).mTemplate;
        }

        public List<WorldTemplate> method_21450() {
            return this.children().stream().map(worldTemplateObjectSelectionListEntry -> worldTemplateObjectSelectionListEntry.mTemplate).collect(Collectors.toList());
        }
    }
}

