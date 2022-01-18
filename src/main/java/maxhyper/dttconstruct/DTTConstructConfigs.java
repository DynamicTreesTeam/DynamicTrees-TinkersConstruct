package maxhyper.dttconstruct;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;

@Mod.EventBusSubscriber(modid = DynamicTreesTConstruct.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DTTConstructConfigs {

    public static final File CONFIG_DIRECTORY;

    public static final ForgeConfigSpec COMMON_CONFIG;

    public static final ForgeConfigSpec.BooleanValue GENERATE_EARTH_SLIME_ISLANDS;
    public static final ForgeConfigSpec.BooleanValue GENERATE_SKY_SLIME_ISLANDS;
    public static final ForgeConfigSpec.BooleanValue GENERATE_CLAY_ISLANDS;
    public static final ForgeConfigSpec.BooleanValue GENERATE_BLOOD_ISLANDS;
    public static final ForgeConfigSpec.BooleanValue GENERATE_END_SLIME_ISLANDS;

    static {

        CONFIG_DIRECTORY = new File(FMLPaths.CONFIGDIR.get().toUri());

        final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

        COMMON_BUILDER.comment("Slime Island Generation").push("islands");
        GENERATE_EARTH_SLIME_ISLANDS = COMMON_BUILDER.comment("If true, this island generates").worldRestart().define("generate", true);
        GENERATE_SKY_SLIME_ISLANDS = COMMON_BUILDER.comment("If true, this island generates").worldRestart().define("generate", true);
        GENERATE_CLAY_ISLANDS = COMMON_BUILDER.comment("If true, this island generates").worldRestart().define("generate", true);
        GENERATE_BLOOD_ISLANDS = COMMON_BUILDER.comment("If true, this island generates").worldRestart().define("generate", true);
        GENERATE_END_SLIME_ISLANDS = COMMON_BUILDER.comment("If true, this island generates").worldRestart().define("generate", true);
        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();

    }

}
