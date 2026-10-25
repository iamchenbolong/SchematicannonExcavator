package com.chasemeng.schematicannonexcavator;

import com.chasemeng.schematicannonexcavator.util.ExcavatorHelper;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class HookManager {

    private static final Set<GlobalPos> FULL_PAUSED = ConcurrentHashMap.newKeySet();

    public static void setFullPaused(SchematicannonBlockEntity be, boolean full) {
        Level level = be.getLevel();
        if (level == null) return;
        GlobalPos key = GlobalPos.of(level.dimension(), be.getBlockPos());
        if (full) FULL_PAUSED.add(key);
        else FULL_PAUSED.remove(key);
    }

    public static boolean isFullPaused(Level level, BlockPos pos) {
        if (level == null) return false;
        return FULL_PAUSED.contains(GlobalPos.of(level.dimension(), pos));
    }

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