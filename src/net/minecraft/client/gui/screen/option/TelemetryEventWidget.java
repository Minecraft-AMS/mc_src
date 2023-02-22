/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.client.gui.screen.option;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.DoubleConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.telemetry.TelemetryEventProperty;
import net.minecraft.client.util.telemetry.TelemetryEventType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TelemetryEventWidget
extends ScrollableWidget {
    private static final int MARGIN_X = 32;
    private static final String REQUIRED_TRANSLATION_KEY = "telemetry.event.required";
    private static final String OPTIONAL_TRANSLATION_KEY = "telemetry.event.optional";
    private static final Text PROPERTY_TITLE_TEXT = Text.translatable("telemetry_info.property_title").formatted(Formatting.UNDERLINE);
    private final TextRenderer textRenderer;
    private Contents contents;
    @Nullable
    private DoubleConsumer scrollConsumer;

    public TelemetryEventWidget(int x, int y, int width, int height, TextRenderer textRenderer) {
        super(x, y, width, height, Text.empty());
        this.textRenderer = textRenderer;
        this.contents = this.collectContents(MinecraftClient.getInstance().isOptionalTelemetryEnabled());
    }

    public void refresh(boolean optionalTelemetryEnabled) {
        this.contents = this.collectContents(optionalTelemetryEnabled);
        this.setScrollY(this.getScrollY());
    }

    private Contents collectContents(boolean optionalTelemetryEnabled) {
        ContentsBuilder contentsBuilder = new ContentsBuilder(this.getGridWidth());
        ArrayList<TelemetryEventType> list = new ArrayList<TelemetryEventType>(TelemetryEventType.getTypes());
        list.sort(Comparator.comparing(TelemetryEventType::isOptional));
        if (!optionalTelemetryEnabled) {
            list.removeIf(TelemetryEventType::isOptional);
        }
        for (int i = 0; i < list.size(); ++i) {
            TelemetryEventType telemetryEventType = (TelemetryEventType)list.get(i);
            this.appendEventInfo(contentsBuilder, telemetryEventType);
            if (i >= list.size() - 1) continue;
            contentsBuilder.appendSpace(this.textRenderer.fontHeight);
        }
        return contentsBuilder.build();
    }

    public void setScrollConsumer(@Nullable DoubleConsumer scrollConsumer) {
        this.scrollConsumer = scrollConsumer;
    }

    @Override
    protected void setScrollY(double scrollY) {
        super.setScrollY(scrollY);
        if (this.scrollConsumer != null) {
            this.scrollConsumer.accept(this.getScrollY());
        }
    }

    @Override
    protected int getContentsHeight() {
        return this.contents.grid().getHeight();
    }

    @Override
    protected boolean overflows() {
        return this.getContentsHeight() > this.height;
    }

    @Override
    protected double getDeltaYPerScroll() {
        return this.textRenderer.fontHeight;
    }

    @Override
    protected void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int i = this.getY() + this.getPadding();
        int j = this.getX() + this.getPadding();
        matrices.push();
        matrices.translate((double)j, (double)i, 0.0);
        this.contents.grid().render(matrices, mouseX, mouseY, delta);
        matrices.pop();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.contents.narration());
    }

    private void appendEventInfo(ContentsBuilder builder, TelemetryEventType eventType) {
        String string = eventType.isOptional() ? OPTIONAL_TRANSLATION_KEY : REQUIRED_TRANSLATION_KEY;
        builder.appendText(this.textRenderer, Text.translatable(string, eventType.getTitle()));
        builder.appendText(this.textRenderer, eventType.getDescription().formatted(Formatting.GRAY));
        builder.appendSpace(this.textRenderer.fontHeight / 2);
        builder.appendTitle(this.textRenderer, PROPERTY_TITLE_TEXT, 2);
        this.appendProperties(eventType, builder);
    }

    private void appendProperties(TelemetryEventType eventType, ContentsBuilder builder) {
        for (TelemetryEventProperty<?> telemetryEventProperty : eventType.getProperties()) {
            builder.appendTitle(this.textRenderer, telemetryEventProperty.getTitle());
        }
    }

    private int getGridWidth() {
        return this.width - this.getPaddingDoubled();
    }

    @Environment(value=EnvType.CLIENT)
    record Contents(GridWidget grid, Text narration) {
        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Contents.class, "container;narration", "grid", "narration"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Contents.class, "container;narration", "grid", "narration"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Contents.class, "container;narration", "grid", "narration"}, this, object);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ContentsBuilder {
        private final int gridWidth;
        private final GridWidget grid;
        private final GridWidget.Adder widgetAdder;
        private final Positioner positioner;
        private final MutableText narration = Text.empty();

        public ContentsBuilder(int gridWidth) {
            this.gridWidth = gridWidth;
            this.grid = new GridWidget();
            this.grid.getMainPositioner().alignLeft();
            this.widgetAdder = this.grid.createAdder(1);
            this.widgetAdder.add(EmptyWidget.ofWidth(gridWidth));
            this.positioner = this.widgetAdder.copyPositioner().alignHorizontalCenter().marginX(32);
        }

        public void appendTitle(TextRenderer textRenderer, Text title) {
            this.appendTitle(textRenderer, title, 0);
        }

        public void appendTitle(TextRenderer textRenderer, Text title, int marginBottom) {
            this.widgetAdder.add(MultilineTextWidget.createNonCentered(this.gridWidth, textRenderer, title), this.widgetAdder.copyPositioner().marginBottom(marginBottom));
            this.narration.append(title).append("\n");
        }

        public void appendText(TextRenderer textRenderer, Text text) {
            this.widgetAdder.add(MultilineTextWidget.createCentered(this.gridWidth - 64, textRenderer, text), this.positioner);
            this.narration.append(text).append("\n");
        }

        public void appendSpace(int height) {
            this.widgetAdder.add(EmptyWidget.ofHeight(height));
        }

        public Contents build() {
            this.grid.recalculateDimensions();
            return new Contents(this.grid, this.narration);
        }
    }
}

