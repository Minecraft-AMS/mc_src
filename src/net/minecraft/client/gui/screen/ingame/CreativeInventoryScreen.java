/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryListener;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.HotbarStorage;
import net.minecraft.client.options.HotbarStorageEntry;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.SearchableContainer;
import net.minecraft.container.Container;
import net.minecraft.container.PlayerContainer;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CreativeInventoryScreen
extends AbstractInventoryScreen<CreativeContainer> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/creative_inventory/tabs.png");
    private static final BasicInventory inventory = new BasicInventory(45);
    private static int selectedTab = ItemGroup.BUILDING_BLOCKS.getIndex();
    private float scrollPosition;
    private boolean field_2892;
    private TextFieldWidget searchBox;
    private List<Slot> slots;
    private Slot deleteItemSlot;
    private CreativeInventoryListener field_2891;
    private boolean field_2888;
    private boolean field_2887;
    private final Map<Identifier, Tag<Item>> field_16201 = Maps.newTreeMap();

    public CreativeInventoryScreen(PlayerEntity playerEntity) {
        super(new CreativeContainer(playerEntity), playerEntity.inventory, new LiteralText(""));
        playerEntity.container = this.container;
        this.passEvents = true;
        this.containerHeight = 136;
        this.containerWidth = 195;
    }

    @Override
    public void tick() {
        if (!this.minecraft.interactionManager.hasCreativeInventory()) {
            this.minecraft.openScreen(new InventoryScreen(this.minecraft.player));
        } else if (this.searchBox != null) {
            this.searchBox.tick();
        }
    }

    @Override
    protected void onMouseClick(@Nullable Slot slot, int invSlot, int button, SlotActionType slotActionType) {
        if (this.method_2470(slot)) {
            this.searchBox.method_1872();
            this.searchBox.method_1884(0);
        }
        boolean bl = slotActionType == SlotActionType.QUICK_MOVE;
        SlotActionType slotActionType2 = slotActionType = invSlot == -999 && slotActionType == SlotActionType.PICKUP ? SlotActionType.THROW : slotActionType;
        if (slot != null || selectedTab == ItemGroup.INVENTORY.getIndex() || slotActionType == SlotActionType.QUICK_CRAFT) {
            if (slot != null && !slot.canTakeItems(this.minecraft.player)) {
                return;
            }
            if (slot == this.deleteItemSlot && bl) {
                for (int i = 0; i < this.minecraft.player.playerContainer.getStacks().size(); ++i) {
                    this.minecraft.interactionManager.clickCreativeStack(ItemStack.EMPTY, i);
                }
            } else if (selectedTab == ItemGroup.INVENTORY.getIndex()) {
                if (slot == this.deleteItemSlot) {
                    this.minecraft.player.inventory.setCursorStack(ItemStack.EMPTY);
                } else if (slotActionType == SlotActionType.THROW && slot != null && slot.hasStack()) {
                    ItemStack itemStack = slot.takeStack(button == 0 ? 1 : slot.getStack().getMaxCount());
                    ItemStack itemStack2 = slot.getStack();
                    this.minecraft.player.dropItem(itemStack, true);
                    this.minecraft.interactionManager.dropCreativeStack(itemStack);
                    this.minecraft.interactionManager.clickCreativeStack(itemStack2, ((CreativeSlot)((CreativeSlot)slot)).slot.id);
                } else if (slotActionType == SlotActionType.THROW && !this.minecraft.player.inventory.getCursorStack().isEmpty()) {
                    this.minecraft.player.dropItem(this.minecraft.player.inventory.getCursorStack(), true);
                    this.minecraft.interactionManager.dropCreativeStack(this.minecraft.player.inventory.getCursorStack());
                    this.minecraft.player.inventory.setCursorStack(ItemStack.EMPTY);
                } else {
                    this.minecraft.player.playerContainer.onSlotClick(slot == null ? invSlot : ((CreativeSlot)((CreativeSlot)slot)).slot.id, button, slotActionType, this.minecraft.player);
                    this.minecraft.player.playerContainer.sendContentUpdates();
                }
            } else if (slotActionType != SlotActionType.QUICK_CRAFT && slot.inventory == inventory) {
                PlayerInventory playerInventory = this.minecraft.player.inventory;
                ItemStack itemStack2 = playerInventory.getCursorStack();
                ItemStack itemStack3 = slot.getStack();
                if (slotActionType == SlotActionType.SWAP) {
                    if (!itemStack3.isEmpty() && button >= 0 && button < 9) {
                        ItemStack itemStack4 = itemStack3.copy();
                        itemStack4.setCount(itemStack4.getMaxCount());
                        this.minecraft.player.inventory.setInvStack(button, itemStack4);
                        this.minecraft.player.playerContainer.sendContentUpdates();
                    }
                    return;
                }
                if (slotActionType == SlotActionType.CLONE) {
                    if (playerInventory.getCursorStack().isEmpty() && slot.hasStack()) {
                        ItemStack itemStack4 = slot.getStack().copy();
                        itemStack4.setCount(itemStack4.getMaxCount());
                        playerInventory.setCursorStack(itemStack4);
                    }
                    return;
                }
                if (slotActionType == SlotActionType.THROW) {
                    if (!itemStack3.isEmpty()) {
                        ItemStack itemStack4 = itemStack3.copy();
                        itemStack4.setCount(button == 0 ? 1 : itemStack4.getMaxCount());
                        this.minecraft.player.dropItem(itemStack4, true);
                        this.minecraft.interactionManager.dropCreativeStack(itemStack4);
                    }
                    return;
                }
                if (!itemStack2.isEmpty() && !itemStack3.isEmpty() && itemStack2.isItemEqualIgnoreDamage(itemStack3) && ItemStack.areTagsEqual(itemStack2, itemStack3)) {
                    if (button == 0) {
                        if (bl) {
                            itemStack2.setCount(itemStack2.getMaxCount());
                        } else if (itemStack2.getCount() < itemStack2.getMaxCount()) {
                            itemStack2.increment(1);
                        }
                    } else {
                        itemStack2.decrement(1);
                    }
                } else if (itemStack3.isEmpty() || !itemStack2.isEmpty()) {
                    if (button == 0) {
                        playerInventory.setCursorStack(ItemStack.EMPTY);
                    } else {
                        playerInventory.getCursorStack().decrement(1);
                    }
                } else {
                    playerInventory.setCursorStack(itemStack3.copy());
                    itemStack2 = playerInventory.getCursorStack();
                    if (bl) {
                        itemStack2.setCount(itemStack2.getMaxCount());
                    }
                }
            } else if (this.container != null) {
                ItemStack itemStack = slot == null ? ItemStack.EMPTY : ((CreativeContainer)this.container).getSlot(slot.id).getStack();
                ((CreativeContainer)this.container).onSlotClick(slot == null ? invSlot : slot.id, button, slotActionType, this.minecraft.player);
                if (Container.unpackButtonId(button) == 2) {
                    for (int j = 0; j < 9; ++j) {
                        this.minecraft.interactionManager.clickCreativeStack(((CreativeContainer)this.container).getSlot(45 + j).getStack(), 36 + j);
                    }
                } else if (slot != null) {
                    ItemStack itemStack2 = ((CreativeContainer)this.container).getSlot(slot.id).getStack();
                    this.minecraft.interactionManager.clickCreativeStack(itemStack2, slot.id - ((CreativeContainer)this.container).slots.size() + 9 + 36);
                    int k = 45 + button;
                    if (slotActionType == SlotActionType.SWAP) {
                        this.minecraft.interactionManager.clickCreativeStack(itemStack, k - ((CreativeContainer)this.container).slots.size() + 9 + 36);
                    } else if (slotActionType == SlotActionType.THROW && !itemStack.isEmpty()) {
                        ItemStack itemStack4 = itemStack.copy();
                        itemStack4.setCount(button == 0 ? 1 : itemStack4.getMaxCount());
                        this.minecraft.player.dropItem(itemStack4, true);
                        this.minecraft.interactionManager.dropCreativeStack(itemStack4);
                    }
                    this.minecraft.player.playerContainer.sendContentUpdates();
                }
            }
        } else {
            PlayerInventory playerInventory = this.minecraft.player.inventory;
            if (!playerInventory.getCursorStack().isEmpty() && this.field_2887) {
                if (button == 0) {
                    this.minecraft.player.dropItem(playerInventory.getCursorStack(), true);
                    this.minecraft.interactionManager.dropCreativeStack(playerInventory.getCursorStack());
                    playerInventory.setCursorStack(ItemStack.EMPTY);
                }
                if (button == 1) {
                    ItemStack itemStack2 = playerInventory.getCursorStack().split(1);
                    this.minecraft.player.dropItem(itemStack2, true);
                    this.minecraft.interactionManager.dropCreativeStack(itemStack2);
                }
            }
        }
    }

    private boolean method_2470(@Nullable Slot slot) {
        return slot != null && slot.inventory == inventory;
    }

    @Override
    protected void method_2476() {
        int i = this.x;
        super.method_2476();
        if (this.searchBox != null && this.x != i) {
            this.searchBox.setX(this.x + 82);
        }
    }

    @Override
    protected void init() {
        if (this.minecraft.interactionManager.hasCreativeInventory()) {
            super.init();
            this.minecraft.keyboard.enableRepeatEvents(true);
            this.searchBox = new TextFieldWidget(this.font, this.x + 82, this.y + 6, 80, this.font.fontHeight, I18n.translate("itemGroup.search", new Object[0]));
            this.searchBox.setMaxLength(50);
            this.searchBox.setHasBorder(false);
            this.searchBox.setVisible(false);
            this.searchBox.setEditableColor(0xFFFFFF);
            this.children.add(this.searchBox);
            int i = selectedTab;
            selectedTab = -1;
            this.setSelectedTab(ItemGroup.GROUPS[i]);
            this.minecraft.player.playerContainer.removeListener(this.field_2891);
            this.field_2891 = new CreativeInventoryListener(this.minecraft);
            this.minecraft.player.playerContainer.addListener(this.field_2891);
        } else {
            this.minecraft.openScreen(new InventoryScreen(this.minecraft.player));
        }
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.searchBox.getText();
        this.init(client, width, height);
        this.searchBox.setText(string);
        if (!this.searchBox.getText().isEmpty()) {
            this.method_2464();
        }
    }

    @Override
    public void removed() {
        super.removed();
        if (this.minecraft.player != null && this.minecraft.player.inventory != null) {
            this.minecraft.player.playerContainer.removeListener(this.field_2891);
        }
        this.minecraft.keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        if (this.field_2888) {
            return false;
        }
        if (selectedTab != ItemGroup.SEARCH.getIndex()) {
            return false;
        }
        String string = this.searchBox.getText();
        if (this.searchBox.charTyped(chr, keyCode)) {
            if (!Objects.equals(string, this.searchBox.getText())) {
                this.method_2464();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean bl;
        this.field_2888 = false;
        if (selectedTab != ItemGroup.SEARCH.getIndex()) {
            if (this.minecraft.options.keyChat.matchesKey(keyCode, scanCode)) {
                this.field_2888 = true;
                this.setSelectedTab(ItemGroup.SEARCH);
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        boolean bl2 = bl = !this.method_2470(this.focusedSlot) || this.focusedSlot != null && this.focusedSlot.hasStack();
        if (bl && this.handleHotbarKeyPressed(keyCode, scanCode)) {
            this.field_2888 = true;
            return true;
        }
        String string = this.searchBox.getText();
        if (this.searchBox.keyPressed(keyCode, scanCode, modifiers)) {
            if (!Objects.equals(string, this.searchBox.getText())) {
                this.method_2464();
            }
            return true;
        }
        if (this.searchBox.isFocused() && this.searchBox.isVisible() && keyCode != 256) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        this.field_2888 = false;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    private void method_2464() {
        ((CreativeContainer)this.container).itemList.clear();
        this.field_16201.clear();
        String string = this.searchBox.getText();
        if (string.isEmpty()) {
            for (Item item : Registry.ITEM) {
                item.appendStacks(ItemGroup.SEARCH, ((CreativeContainer)this.container).itemList);
            }
        } else {
            SearchableContainer<ItemStack> searchable;
            if (string.startsWith("#")) {
                string = string.substring(1);
                searchable = this.minecraft.getSearchableContainer(SearchManager.ITEM_TAG);
                this.method_15871(string);
            } else {
                searchable = this.minecraft.getSearchableContainer(SearchManager.ITEM_TOOLTIP);
            }
            ((CreativeContainer)this.container).itemList.addAll(searchable.findAll(string.toLowerCase(Locale.ROOT)));
        }
        this.scrollPosition = 0.0f;
        ((CreativeContainer)this.container).method_2473(0.0f);
    }

    private void method_15871(String string) {
        Predicate<Identifier> predicate;
        int i = string.indexOf(58);
        if (i == -1) {
            predicate = identifier -> identifier.getPath().contains(string);
        } else {
            String string2 = string.substring(0, i).trim();
            String string3 = string.substring(i + 1).trim();
            predicate = identifier -> identifier.getNamespace().contains(string2) && identifier.getPath().contains(string3);
        }
        TagContainer<Item> tagContainer = ItemTags.getContainer();
        tagContainer.getKeys().stream().filter(predicate).forEach(identifier -> this.field_16201.put((Identifier)identifier, tagContainer.get((Identifier)identifier)));
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY) {
        ItemGroup itemGroup = ItemGroup.GROUPS[selectedTab];
        if (itemGroup.hasTooltip()) {
            GlStateManager.disableBlend();
            this.font.draw(I18n.translate(itemGroup.getTranslationKey(), new Object[0]), 8.0f, 6.0f, 0x404040);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            double d = mouseX - (double)this.x;
            double e = mouseY - (double)this.y;
            for (ItemGroup itemGroup : ItemGroup.GROUPS) {
                if (!this.isClickInTab(itemGroup, d, e)) continue;
                return true;
            }
            if (selectedTab != ItemGroup.INVENTORY.getIndex() && this.method_2467(mouseX, mouseY)) {
                this.field_2892 = this.hasScrollbar();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            double d = mouseX - (double)this.x;
            double e = mouseY - (double)this.y;
            this.field_2892 = false;
            for (ItemGroup itemGroup : ItemGroup.GROUPS) {
                if (!this.isClickInTab(itemGroup, d, e)) continue;
                this.setSelectedTab(itemGroup);
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean hasScrollbar() {
        return selectedTab != ItemGroup.INVENTORY.getIndex() && ItemGroup.GROUPS[selectedTab].hasScrollbar() && ((CreativeContainer)this.container).method_2474();
    }

    private void setSelectedTab(ItemGroup group) {
        int k;
        int j;
        int i = selectedTab;
        selectedTab = group.getIndex();
        this.cursorDragSlots.clear();
        ((CreativeContainer)this.container).itemList.clear();
        if (group == ItemGroup.HOTBAR) {
            HotbarStorage hotbarStorage = this.minecraft.getCreativeHotbarStorage();
            for (j = 0; j < 9; ++j) {
                HotbarStorageEntry hotbarStorageEntry = hotbarStorage.getSavedHotbar(j);
                if (hotbarStorageEntry.isEmpty()) {
                    for (k = 0; k < 9; ++k) {
                        if (k == j) {
                            ItemStack itemStack = new ItemStack(Items.PAPER);
                            itemStack.getOrCreateSubTag("CustomCreativeLock");
                            String string = this.minecraft.options.keysHotbar[j].getLocalizedName();
                            String string2 = this.minecraft.options.keySaveToolbarActivator.getLocalizedName();
                            itemStack.setCustomName(new TranslatableText("inventory.hotbarInfo", string2, string));
                            ((CreativeContainer)this.container).itemList.add(itemStack);
                            continue;
                        }
                        ((CreativeContainer)this.container).itemList.add(ItemStack.EMPTY);
                    }
                    continue;
                }
                ((CreativeContainer)this.container).itemList.addAll((Collection<ItemStack>)((Object)hotbarStorageEntry));
            }
        } else if (group != ItemGroup.SEARCH) {
            group.appendStacks(((CreativeContainer)this.container).itemList);
        }
        if (group == ItemGroup.INVENTORY) {
            PlayerContainer container = this.minecraft.player.playerContainer;
            if (this.slots == null) {
                this.slots = ImmutableList.copyOf((Collection)((CreativeContainer)this.container).slots);
            }
            ((CreativeContainer)this.container).slots.clear();
            for (j = 0; j < container.slots.size(); ++j) {
                int m;
                int l;
                CreativeSlot slot = new CreativeSlot(container.slots.get(j), j);
                ((CreativeContainer)this.container).slots.add(slot);
                if (j >= 5 && j < 9) {
                    k = j - 5;
                    l = k / 2;
                    m = k % 2;
                    slot.xPosition = 54 + l * 54;
                    slot.yPosition = 6 + m * 27;
                    continue;
                }
                if (j >= 0 && j < 5) {
                    slot.xPosition = -2000;
                    slot.yPosition = -2000;
                    continue;
                }
                if (j == 45) {
                    slot.xPosition = 35;
                    slot.yPosition = 20;
                    continue;
                }
                if (j >= container.slots.size()) continue;
                k = j - 9;
                l = k % 9;
                m = k / 9;
                slot.xPosition = 9 + l * 18;
                slot.yPosition = j >= 36 ? 112 : 54 + m * 18;
            }
            this.deleteItemSlot = new Slot(inventory, 0, 173, 112);
            ((CreativeContainer)this.container).slots.add(this.deleteItemSlot);
        } else if (i == ItemGroup.INVENTORY.getIndex()) {
            ((CreativeContainer)this.container).slots.clear();
            ((CreativeContainer)this.container).slots.addAll(this.slots);
            this.slots = null;
        }
        if (this.searchBox != null) {
            if (group == ItemGroup.SEARCH) {
                this.searchBox.setVisible(true);
                this.searchBox.method_1856(false);
                this.searchBox.method_1876(true);
                if (i != group.getIndex()) {
                    this.searchBox.setText("");
                }
                this.method_2464();
            } else {
                this.searchBox.setVisible(false);
                this.searchBox.method_1856(true);
                this.searchBox.method_1876(false);
                this.searchBox.setText("");
            }
        }
        this.scrollPosition = 0.0f;
        ((CreativeContainer)this.container).method_2473(0.0f);
    }

    @Override
    public boolean mouseScrolled(double d, double e, double amount) {
        if (!this.hasScrollbar()) {
            return false;
        }
        int i = (((CreativeContainer)this.container).itemList.size() + 9 - 1) / 9 - 5;
        this.scrollPosition = (float)((double)this.scrollPosition - amount / (double)i);
        this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
        ((CreativeContainer)this.container).method_2473(this.scrollPosition);
        return true;
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        boolean bl = mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.containerWidth) || mouseY >= (double)(top + this.containerHeight);
        this.field_2887 = bl && !this.isClickInTab(ItemGroup.GROUPS[selectedTab], mouseX, mouseY);
        return this.field_2887;
    }

    protected boolean method_2467(double d, double e) {
        int i = this.x;
        int j = this.y;
        int k = i + 175;
        int l = j + 18;
        int m = k + 14;
        int n = l + 112;
        return d >= (double)k && e >= (double)l && d < (double)m && e < (double)n;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.field_2892) {
            int i = this.y + 18;
            int j = i + 112;
            this.scrollPosition = ((float)mouseY - (float)i - 7.5f) / ((float)(j - i) - 15.0f);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
            ((CreativeContainer)this.container).method_2473(this.scrollPosition);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
        for (ItemGroup itemGroup : ItemGroup.GROUPS) {
            if (this.method_2471(itemGroup, mouseX, mouseY)) break;
        }
        if (this.deleteItemSlot != null && selectedTab == ItemGroup.INVENTORY.getIndex() && this.isPointWithinBounds(this.deleteItemSlot.xPosition, this.deleteItemSlot.yPosition, 16, 16, mouseX, mouseY)) {
            this.renderTooltip(I18n.translate("inventory.binSlot", new Object[0]), mouseX, mouseY);
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableLighting();
        this.drawMouseoverTooltip(mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(ItemStack stack, int x, int y) {
        if (selectedTab == ItemGroup.SEARCH.getIndex()) {
            Map<Enchantment, Integer> map;
            List<Text> list = stack.getTooltip(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
            ArrayList list2 = Lists.newArrayListWithCapacity((int)list.size());
            for (Text text : list) {
                list2.add(text.asFormattedString());
            }
            Item item = stack.getItem();
            ItemGroup itemGroup = item.getGroup();
            if (itemGroup == null && item == Items.ENCHANTED_BOOK && (map = EnchantmentHelper.getEnchantments(stack)).size() == 1) {
                Enchantment enchantment = map.keySet().iterator().next();
                for (ItemGroup itemGroup2 : ItemGroup.GROUPS) {
                    if (!itemGroup2.containsEnchantments(enchantment.type)) continue;
                    itemGroup = itemGroup2;
                    break;
                }
            }
            this.field_16201.forEach((identifier, tag) -> {
                if (tag.contains(item)) {
                    list2.add(1, "" + (Object)((Object)Formatting.BOLD) + (Object)((Object)Formatting.DARK_PURPLE) + "#" + identifier);
                }
            });
            if (itemGroup != null) {
                list2.add(1, "" + (Object)((Object)Formatting.BOLD) + (Object)((Object)Formatting.BLUE) + I18n.translate(itemGroup.getTranslationKey(), new Object[0]));
            }
            for (int i = 0; i < list2.size(); ++i) {
                if (i == 0) {
                    list2.set(i, (Object)((Object)stack.getRarity().formatting) + (String)list2.get(i));
                    continue;
                }
                list2.set(i, (Object)((Object)Formatting.GRAY) + (String)list2.get(i));
            }
            this.renderTooltip(list2, x, y);
        } else {
            super.renderTooltip(stack, x, y);
        }
    }

    @Override
    protected void drawBackground(float delta, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        DiffuseLighting.enableForItems();
        ItemGroup itemGroup = ItemGroup.GROUPS[selectedTab];
        for (ItemGroup itemGroup2 : ItemGroup.GROUPS) {
            this.minecraft.getTextureManager().bindTexture(TEXTURE);
            if (itemGroup2.getIndex() == selectedTab) continue;
            this.method_2468(itemGroup2);
        }
        this.minecraft.getTextureManager().bindTexture(new Identifier("textures/gui/container/creative_inventory/tab_" + itemGroup.getTexture()));
        this.blit(this.x, this.y, 0, 0, this.containerWidth, this.containerHeight);
        this.searchBox.render(mouseX, mouseY, delta);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        int i = this.x + 175;
        int j = this.y + 18;
        int k = j + 112;
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        if (itemGroup.hasScrollbar()) {
            this.blit(i, j + (int)((float)(k - j - 17) * this.scrollPosition), 232 + (this.hasScrollbar() ? 0 : 12), 0, 12, 15);
        }
        this.method_2468(itemGroup);
        if (itemGroup == ItemGroup.INVENTORY) {
            InventoryScreen.drawEntity(this.x + 88, this.y + 45, 20, this.x + 88 - mouseX, this.y + 45 - 30 - mouseY, this.minecraft.player);
        }
    }

    protected boolean isClickInTab(ItemGroup group, double mouseX, double mouseY) {
        int i = group.getColumn();
        int j = 28 * i;
        int k = 0;
        if (group.isSpecial()) {
            j = this.containerWidth - 28 * (6 - i) + 2;
        } else if (i > 0) {
            j += i;
        }
        k = group.isTopRow() ? (k -= 32) : (k += this.containerHeight);
        return mouseX >= (double)j && mouseX <= (double)(j + 28) && mouseY >= (double)k && mouseY <= (double)(k + 32);
    }

    protected boolean method_2471(ItemGroup itemGroup, int i, int j) {
        int k = itemGroup.getColumn();
        int l = 28 * k;
        int m = 0;
        if (itemGroup.isSpecial()) {
            l = this.containerWidth - 28 * (6 - k) + 2;
        } else if (k > 0) {
            l += k;
        }
        m = itemGroup.isTopRow() ? (m -= 32) : (m += this.containerHeight);
        if (this.isPointWithinBounds(l + 3, m + 3, 23, 27, i, j)) {
            this.renderTooltip(I18n.translate(itemGroup.getTranslationKey(), new Object[0]), i, j);
            return true;
        }
        return false;
    }

    protected void method_2468(ItemGroup itemGroup) {
        boolean bl = itemGroup.getIndex() == selectedTab;
        boolean bl2 = itemGroup.isTopRow();
        int i = itemGroup.getColumn();
        int j = i * 28;
        int k = 0;
        int l = this.x + 28 * i;
        int m = this.y;
        int n = 32;
        if (bl) {
            k += 32;
        }
        if (itemGroup.isSpecial()) {
            l = this.x + this.containerWidth - 28 * (6 - i);
        } else if (i > 0) {
            l += i;
        }
        if (bl2) {
            m -= 28;
        } else {
            k += 64;
            m += this.containerHeight - 4;
        }
        GlStateManager.disableLighting();
        this.blit(l, m, j, k, 28, 32);
        this.blitOffset = 100;
        this.itemRenderer.zOffset = 100.0f;
        int n2 = bl2 ? 1 : -1;
        GlStateManager.enableLighting();
        GlStateManager.enableRescaleNormal();
        ItemStack itemStack = itemGroup.getIcon();
        this.itemRenderer.renderGuiItem(itemStack, l += 6, m += 8 + n2);
        this.itemRenderer.renderGuiItemOverlay(this.font, itemStack, l, m);
        GlStateManager.disableLighting();
        this.itemRenderer.zOffset = 0.0f;
        this.blitOffset = 0;
    }

    public int method_2469() {
        return selectedTab;
    }

    public static void onHotbarKeyPress(MinecraftClient client, int index, boolean restore, boolean save) {
        ClientPlayerEntity clientPlayerEntity = client.player;
        HotbarStorage hotbarStorage = client.getCreativeHotbarStorage();
        HotbarStorageEntry hotbarStorageEntry = hotbarStorage.getSavedHotbar(index);
        if (restore) {
            for (int i = 0; i < PlayerInventory.getHotbarSize(); ++i) {
                ItemStack itemStack = ((ItemStack)hotbarStorageEntry.get(i)).copy();
                clientPlayerEntity.inventory.setInvStack(i, itemStack);
                client.interactionManager.clickCreativeStack(itemStack, 36 + i);
            }
            clientPlayerEntity.playerContainer.sendContentUpdates();
        } else if (save) {
            for (int i = 0; i < PlayerInventory.getHotbarSize(); ++i) {
                hotbarStorageEntry.set(i, clientPlayerEntity.inventory.getInvStack(i).copy());
            }
            String string = client.options.keysHotbar[index].getLocalizedName();
            String string2 = client.options.keyLoadToolbarActivator.getLocalizedName();
            client.inGameHud.setOverlayMessage(new TranslatableText("inventory.hotbarSaved", string2, string), false);
            hotbarStorage.save();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class class_482
    extends Slot {
        public class_482(Inventory invSlot, int xPosition, int i, int j) {
            super(invSlot, xPosition, i, j);
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            if (super.canTakeItems(playerEntity) && this.hasStack()) {
                return this.getStack().getSubTag("CustomCreativeLock") == null;
            }
            return !this.hasStack();
        }
    }

    @Environment(value=EnvType.CLIENT)
    class CreativeSlot
    extends Slot {
        private final Slot slot;

        public CreativeSlot(Slot slot, int i) {
            super(slot.inventory, i, 0, 0);
            this.slot = slot;
        }

        @Override
        public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
            this.slot.onTakeItem(player, stack);
            return stack;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return this.slot.canInsert(stack);
        }

        @Override
        public ItemStack getStack() {
            return this.slot.getStack();
        }

        @Override
        public boolean hasStack() {
            return this.slot.hasStack();
        }

        @Override
        public void setStack(ItemStack itemStack) {
            this.slot.setStack(itemStack);
        }

        @Override
        public void markDirty() {
            this.slot.markDirty();
        }

        @Override
        public int getMaxStackAmount() {
            return this.slot.getMaxStackAmount();
        }

        @Override
        public int getMaxStackAmount(ItemStack itemStack) {
            return this.slot.getMaxStackAmount(itemStack);
        }

        @Override
        @Nullable
        public String getBackgroundSprite() {
            return this.slot.getBackgroundSprite();
        }

        @Override
        public ItemStack takeStack(int amount) {
            return this.slot.takeStack(amount);
        }

        @Override
        public boolean doDrawHoveringEffect() {
            return this.slot.doDrawHoveringEffect();
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return this.slot.canTakeItems(playerEntity);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class CreativeContainer
    extends Container {
        public final DefaultedList<ItemStack> itemList = DefaultedList.of();

        public CreativeContainer(PlayerEntity playerEntity) {
            super(null, 0);
            int i;
            PlayerInventory playerInventory = playerEntity.inventory;
            for (i = 0; i < 5; ++i) {
                for (int j = 0; j < 9; ++j) {
                    this.addSlot(new class_482(inventory, i * 9 + j, 9 + j * 18, 18 + i * 18));
                }
            }
            for (i = 0; i < 9; ++i) {
                this.addSlot(new Slot(playerInventory, i, 9 + i * 18, 112));
            }
            this.method_2473(0.0f);
        }

        @Override
        public boolean canUse(PlayerEntity player) {
            return true;
        }

        public void method_2473(float f) {
            int i = (this.itemList.size() + 9 - 1) / 9 - 5;
            int j = (int)((double)(f * (float)i) + 0.5);
            if (j < 0) {
                j = 0;
            }
            for (int k = 0; k < 5; ++k) {
                for (int l = 0; l < 9; ++l) {
                    int m = l + (k + j) * 9;
                    if (m >= 0 && m < this.itemList.size()) {
                        inventory.setInvStack(l + k * 9, this.itemList.get(m));
                        continue;
                    }
                    inventory.setInvStack(l + k * 9, ItemStack.EMPTY);
                }
            }
        }

        public boolean method_2474() {
            return this.itemList.size() > 45;
        }

        @Override
        public ItemStack transferSlot(PlayerEntity player, int invSlot) {
            Slot slot;
            if (invSlot >= this.slots.size() - 9 && invSlot < this.slots.size() && (slot = (Slot)this.slots.get(invSlot)) != null && slot.hasStack()) {
                slot.setStack(ItemStack.EMPTY);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
            return slot.inventory != inventory;
        }

        @Override
        public boolean canInsertIntoSlot(Slot slot) {
            return slot.inventory != inventory;
        }
    }
}
