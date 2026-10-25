package com.chasemeng.schematicannonexcavator.mixin;

import com.chasemeng.schematicannonexcavator.ExcavatorConfig;
import com.chasemeng.schematicannonexcavator.HookManager;
import com.simibubi.create.content.schematics.SchematicPrinter;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SchematicannonBlockEntity.class)
public abstract class SchematicannonBlockEntityMixin {

    @Shadow(remap = false) private SchematicPrinter printer;
    @Shadow(remap = false) public SchematicannonBlockEntity.State state;
    @Shadow(remap = false) public String statusMsg;

    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void onTick(CallbackInfo ci) {
        SchematicannonBlockEntity self = (SchematicannonBlockEntity) (Object) this;
        if (!ExcavatorConfig.ENABLE_EXCAVATOR.get()) return;
        if (this.state != SchematicannonBlockEntity.State.RUNNING) return;
        if (!"clearing".equals(this.statusMsg)) return;

        BlockPos target = this.printer.getCurrentTarget();
        if (target == null) return;

        Level level = self.getLevel();
        if (level == null) return;

        boolean canProceed = HookManager.tryExcavate(self, level, target);
        if (!canProceed) {
            this.state = SchematicannonBlockEntity.State.PAUSED;
            this.statusMsg = "containerFull";
        }
    }
}