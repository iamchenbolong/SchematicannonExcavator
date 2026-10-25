package com.chasemeng.schematicannonexcavator;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ExcavatorConfigScreen extends Screen {
    private final Screen parent;
    private Button toggleButton;
    private EditBox blacklistBox;

    public ExcavatorConfigScreen(Screen parent) {
        super(Component.translatable("schematicannonexcavator.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int cy = this.height / 2;
        this.toggleButton = Button.builder(getToggleText(), b -> {
            boolean v = !ExcavatorConfig.ENABLE_EXCAVATOR.get();
            ExcavatorConfig.ENABLE_EXCAVATOR.set(v);
            b.setMessage(getToggleText());
        }).bounds(cx - 100, cy - 40, 200, 20).build();
        this.addRenderableWidget(toggleButton);

        this.blacklistBox = new EditBox(this.font, cx - 100, cy, 200, 20,
                Component.translatable("schematicannonexcavator.config.blacklist"));
        this.blacklistBox.setValue(String.join(",", ExcavatorConfig.BLACKLIST.get()));
        this.addRenderableWidget(blacklistBox);

        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> {
            ExcavatorConfig.BLACKLIST.set(java.util.Arrays.asList(blacklistBox.getValue().split(",")));
            ExcavatorConfig.SPEC.save();
            this.minecraft.setScreen(parent);
        }).bounds(cx - 100, cy + 40, 200, 20).build());
    }

    private Component getToggleText() {
        return Component.translatable("schematicannonexcavator.config.enable")
                .append(": ")
                .append(ExcavatorConfig.ENABLE_EXCAVATOR.get()
                        ? Component.translatable("gui.yes")
                        : Component.translatable("gui.no"));
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        this.renderBackground(g);
        g.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        g.drawString(this.font, Component.translatable("schematicannonexcavator.config.blacklist"),
                this.width / 2 - 100, this.height / 2 - 12, 0xA0A0A0);
        super.render(g, mx, my, pt);
    }
}