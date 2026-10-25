package com.chasemeng.schematicannonexcavator.mixin;

import com.chasemeng.schematicannonexcavator.ExcavatorConfig;
import com.chasemeng.schematicannonexcavator.SchematicannonExcavator;
import com.simibubi.create.content.schematics.cannon.SchematicannonScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SchematicannonScreen.class)
public abstract class SchematicannonScreenMixin extends Screen {

    private static final ResourceLocation ICON =
            ResourceLocation.fromNamespaceAndPath("schematicannonexcavator", "textures/gui/excavator_button.png");

    protected SchematicannonScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addExcavatorButton(CallbackInfo ci) {
        int x = this.width / 2 + 90;
        int y = this.height / 2 - 80;

        Button customButton = new Button(
                x, y, 20, 20,
                Component.empty(),
                b -> {
                    boolean v = !ExcavatorConfig.ENABLE_EXCAVATOR.get();
                    ExcavatorConfig.ENABLE_EXCAVATOR.set(v);
                    SchematicannonExcavator.LOGGER.info("[Excavator] toggled: {}", v);
                },
                b -> Component.empty()
        ) {
            @Override
            protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
                boolean enabled = ExcavatorConfig.ENABLE_EXCAVATOR.get();

                int bgColor = enabled ? 0xFF2E7D32 : 0xFF424242;
                g.fill(this.getX(), this.getY(), this.getX() + 20, this.getY() + 20, bgColor);

                g.blit(ICON, this.getX() + 2, this.getY() + 2, 0, 0, 16, 16, 16, 16);

                int border = enabled ? 0xFF66BB6A : 0xFF888888;
                g.renderOutline(this.getX(), this.getY(), 20, 20, border);
            }
        };

        this.addRenderableWidget(customButton);
        SchematicannonExcavator.LOGGER.info("[Excavator] Button added at ({}, {})", x, y);
    }
}