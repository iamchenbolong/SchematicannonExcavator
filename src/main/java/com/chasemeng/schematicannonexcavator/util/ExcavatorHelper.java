package com.chasemeng.schematicannonexcavator.util;

import com.chasemeng.schematicannonexcavator.ExcavatorConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class ExcavatorHelper {

    public static boolean isBlacklisted(Block block) {
        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
        return id != null && ExcavatorConfig.BLACKLIST.get().contains(id.toString());
    }

    public static boolean isUnobtainable(Block block) {
        return block == Blocks.BEDROCK
                || block == Blocks.COMMAND_BLOCK
                || block == Blocks.CHAIN_COMMAND_BLOCK
                || block == Blocks.REPEATING_COMMAND_BLOCK
                || block == Blocks.STRUCTURE_BLOCK
                || block == Blocks.JIGSAW
                || block == Blocks.BARRIER
                || block == Blocks.LIGHT;
    }

    public static boolean canInsertIntoAnyContainer(Level level, BlockPos cannonPos, ItemStack stack) {
        for (Direction dir : Direction.values()) {
            BlockEntity be = level.getBlockEntity(cannonPos.relative(dir));
            if (be == null) continue;
            LazyOptional<IItemHandler> cap = be.getCapability(ForgeCapabilities.ITEM_HANDLER, dir.getOpposite());
            if (!cap.isPresent()) continue;
            IItemHandler handler = cap.orElse(null);
            if (handler == null) continue;

            ItemStack remaining = stack.copy();
            for (int i = 0; i < handler.getSlots(); i++) {
                remaining = handler.insertItem(i, remaining, true);
                if (remaining.isEmpty()) return true;
            }
        }
        return false;
    }

    public static boolean insertIntoNearbyContainer(Level level, BlockPos cannonPos, ItemStack stack) {
        ItemStack remaining = stack.copy();
        for (Direction dir : Direction.values()) {
            BlockEntity be = level.getBlockEntity(cannonPos.relative(dir));
            if (be == null) continue;
            LazyOptional<IItemHandler> cap = be.getCapability(ForgeCapabilities.ITEM_HANDLER, dir.getOpposite());
            if (!cap.isPresent()) continue;
            IItemHandler handler = cap.orElse(null);
            if (handler == null) continue;

            for (int i = 0; i < handler.getSlots(); i++) {
                remaining = handler.insertItem(i, remaining, false);
                if (remaining.isEmpty()) return true;
            }
        }
        return false;
    }
}