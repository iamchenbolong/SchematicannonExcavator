package com.chasemeng.schematicannonexcavator.mixin;

import com.chasemeng.schematicannonexcavator.ExcavatorConfig;
import com.chasemeng.schematicannonexcavator.HookManager;
import com.chasemeng.schematicannonexcavator.util.ExcavatorHelper;
import com.simibubi.create.content.schematics.SchematicPrinter;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(SchematicannonBlockEntity.class)
public abstract class SchematicannonBlockEntityMixin {

    @Shadow(remap = false) private SchematicPrinter printer;
    @Shadow(remap = false) public SchematicannonBlockEntity.State state;
    @Shadow(remap = false) public String statusMsg;

    @Unique private final Map<BlockPos, Block> processedBlocks = new HashMap<>();
    @Unique private boolean excavatorPaused = false;

    @Inject(method = "tick", at = @At("HEAD"), remap = false, cancellable = true)
    private void onTick(CallbackInfo ci) {
        SchematicannonBlockEntity self = (SchematicannonBlockEntity) (Object) this;
        if (!ExcavatorConfig.ENABLE_EXCAVATOR.get()) return;

        Level level = self.getLevel();
        if (level == null) return;

        BlockPos target = null;
        try {
            target = this.printer.getCurrentTarget();
        } catch (Exception ignored) {
        }

        if (this.excavatorPaused) {
            if (target != null) {
                BlockState st = level.getBlockState(target);
                if (!st.isAir()) {
                    ItemStack stack = new ItemStack(st.getBlock());
                    if (ExcavatorHelper.canInsertIntoAnyContainer(level, self.getBlockPos(), stack)) {
                        this.excavatorPaused = false;
                        HookManager.setFullPaused(self, false);
                        this.state = SchematicannonBlockEntity.State.RUNNING;
                        this.statusMsg = "clearing";
                    } else {
                        this.state = SchematicannonBlockEntity.State.PAUSED;
                        this.statusMsg = "paused";
                        HookManager.setFullPaused(self, true);
                        ci.cancel();
                        return;
                    }
                } else {
                    this.excavatorPaused = false;
                    HookManager.setFullPaused(self, false);
                    this.state = SchematicannonBlockEntity.State.RUNNING;
                }
            } else {
                this.excavatorPaused = false;
                HookManager.setFullPaused(self, false);
                this.state = SchematicannonBlockEntity.State.RUNNING;
            }
        }

        if (this.state != SchematicannonBlockEntity.State.RUNNING) return;
        if (!"clearing".equals(this.statusMsg)) return;
        if (target == null) return;

        BlockState st = level.getBlockState(target);
        if (st.isAir()) {
            this.processedBlocks.remove(target);
            return;
        }

        Block prev = this.processedBlocks.get(target);
        if (prev == st.getBlock()) {
            return;
        }

        if (!HookManager.tryExcavate(self, level, target)) {
            this.state = SchematicannonBlockEntity.State.PAUSED;
            this.statusMsg = "paused";
            this.excavatorPaused = true;
            HookManager.setFullPaused(self, true);
            ci.cancel();
            return;
        }

        this.processedBlocks.put(target.immutable(), st.getBlock());
    }
}