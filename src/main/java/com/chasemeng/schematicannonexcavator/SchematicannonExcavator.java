package com.chasemeng.schematicannonexcavator;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SchematicannonExcavator.MODID)
public class SchematicannonExcavator {
    public static final String MODID = "schematicannonexcavator";
    public static final Logger LOGGER = LogManager.getLogger();

    public SchematicannonExcavator() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ExcavatorConfig.SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ExcavatorClientEvents::onClientSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }
}