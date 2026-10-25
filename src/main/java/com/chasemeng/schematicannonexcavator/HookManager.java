package com.chasemeng.schematicannonexcavator;

import com.chasemeng.schematicannonexcavator.util.ExcavatorHelper;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HookManager {
    public static boolean tryExcavate(SchematicannonBlockEntity cannon, Level level, BlockPos pos) {
        if (!ExcavatorConfig.ENABLE_EXCAVATOR.get()) return true;

        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return true;
        if (ExcavatorHelper.isBlacklisted(state.getBlock())) return true;
        if (ExcavatorHelper.isUnobtainable(state.getBlock())) return true;

        ItemStack stack = new ItemStack(state.getBlock());
        if (!ExcavatorHelper.canInsertIntoAnyContainer(level, cannon.getBlockPos(), stack)) {
            return false;
        }
        ExcavatorHelper.insertIntoNearbyContainer(level, cannon.getBlockPos(), stack);
        return true;
    }
}