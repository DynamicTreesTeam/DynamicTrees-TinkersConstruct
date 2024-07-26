package maxhyper.dttconstruct;

import com.ferreusveritas.dynamictrees.api.GatherDataHelper;
import com.ferreusveritas.dynamictrees.api.registry.RegistryHandler;
import com.ferreusveritas.dynamictrees.block.leaves.LeavesProperties;
import com.ferreusveritas.dynamictrees.block.rooty.SoilProperties;
import com.ferreusveritas.dynamictrees.tree.family.Family;
import com.ferreusveritas.dynamictrees.tree.species.Species;
import maxhyper.dttconstruct.init.DTTConstructRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DynamicTreesTinkersConstruct.MOD_ID)
public class DynamicTreesTinkersConstruct
{
    public static final String MOD_ID = "dttconstruct";

    public DynamicTreesTinkersConstruct() {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener(this::commonSetup);
        modBus.addListener(this::clientSetup);
        modBus.addListener(this::gatherData);

        MinecraftForge.EVENT_BUS.register(this);

        RegistryHandler.setup(MOD_ID);

        DTTConstructRegistries.setup();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        //if (DTConfigs.WORLD_GEN.get()){
            //SlimeIslandReplacement.commonSetup(event);
        //}
        //        if (DTConfigs.REPLACE_NYLIUM_FUNGI.get()) {
//            replaceNyliumFungiFeatures();
//        }

        event.enqueueWork(DTTConstructRegistries::setupConnectables);
    }

    private void clientSetup(final FMLClientSetupEvent event) {

    }

//    public static void replaceNyliumFungiFeatures() {
//        TreeRegistry.findSpecies(new ResourceLocation(MOD_ID, "bloodshroom")).getSapling().ifPresent(bloodSapling ->
//                replaceFeatureConfigs(((WeightedBlockStateProvider) Features.Configs.WARPED_FOREST_CONFIG.stateProvider), bloodSapling));
//    }
//
//    private static void replaceFeatureConfigs(WeightedBlockStateProvider featureConfig, Block sapling) {
//        for (final WeightedList.Entry<BlockState> entry : featureConfig.weightedList.entries) {
//            if (entry.data.getBlock() == ForgeRegistries.BLOCKS.getValue(new ResourceLocation("tconstruct", "blood_slime_sapling"))) {
//                entry.data = sapling.defaultBlockState();
//            }
//        }
//    }

    private void gatherData(final GatherDataEvent event) {
        GatherDataHelper.gatherAllData(
                MOD_ID,
                event,
                //SoilProperties.REGISTRY,
                Family.REGISTRY,
                Species.REGISTRY,
                LeavesProperties.REGISTRY
        );
    }

    public static ResourceLocation location(final String path) {
        return new ResourceLocation(MOD_ID, path);
    }

}
