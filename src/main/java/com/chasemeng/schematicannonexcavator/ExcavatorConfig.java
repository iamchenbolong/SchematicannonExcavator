package com.chasemeng.schematicannonexcavator;

import net.minecraftforge.common.ForgeConfigSpec;
import java.util.Arrays;
import java.util.List;

public class ExcavatorConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue ENABLE_EXCAVATOR;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLIST;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("excavator");
        ENABLE_EXCAVATOR = builder.define("enableExcavator", false);
        BLACKLIST = builder.defineList("blacklist",
                Arrays.asList("minecraft:bedrock", "minecraft:command_block"),
                o -> o instanceof String);
        builder.pop();
        SPEC = builder.build();
    }
}