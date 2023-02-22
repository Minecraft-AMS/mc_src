/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.AbstractRealmsButton;
import net.minecraft.realms.RealmsSliderButtonProxy;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class RealmsSliderButton
extends AbstractRealmsButton<RealmsSliderButtonProxy> {
    protected static final Identifier WIDGETS_LOCATION = new Identifier("textures/gui/widgets.png");
    private final int id;
    private final RealmsSliderButtonProxy proxy;
    private final double minValue;
    private final double maxValue;

    public RealmsSliderButton(int i, int j, int k, int l, int m, double d, double e) {
        this.id = i;
        this.minValue = d;
        this.maxValue = e;
        this.proxy = new RealmsSliderButtonProxy(this, j, k, l, 20, this.toPct(m));
        this.getProxy().setMessage(this.getMessage());
    }

    public String getMessage() {
        return "";
    }

    public double toPct(double d) {
        return MathHelper.clamp((this.clamp(d) - this.minValue) / (this.maxValue - this.minValue), 0.0, 1.0);
    }

    public double toValue(double d) {
        return this.clamp(MathHelper.lerp(MathHelper.clamp(d, 0.0, 1.0), this.minValue, this.maxValue));
    }

    public double clamp(double d) {
        return MathHelper.clamp(d, this.minValue, this.maxValue);
    }

    public int getYImage(boolean bl) {
        return 0;
    }

    public void onClick(double d, double e) {
    }

    public void onRelease(double d, double e) {
    }

    @Override
    public RealmsSliderButtonProxy getProxy() {
        return this.proxy;
    }

    public double getValue() {
        return this.proxy.getValue();
    }

    public void setValue(double d) {
        this.proxy.setValue(d);
    }

    public int id() {
        return this.id;
    }

    public void setMessage(String string) {
        this.proxy.setMessage(string);
    }

    public int getWidth() {
        return this.proxy.getWidth();
    }

    public int getHeight() {
        return this.proxy.getHeight();
    }

    public int y() {
        return this.proxy.y();
    }

    public abstract void applyValue();

    public void updateMessage() {
        this.proxy.setMessage(this.getMessage());
    }
}

