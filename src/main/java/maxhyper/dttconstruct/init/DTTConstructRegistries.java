package maxhyper.dttconstruct.init;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.cell.CellKit;
import com.ferreusveritas.dynamictrees.api.registry.RegistryEvent;
import com.ferreusveritas.dynamictrees.block.branch.BranchBlock;
import com.ferreusveritas.dynamictrees.systems.BranchConnectables;
import maxhyper.dttconstruct.DynamicTreesTinkersConstruct;
import maxhyper.dttconstruct.cellkits.DTCCellKits;
import maxhyper.dttconstruct.world.BiomeSpeciesFeatureConfiguration;
import maxhyper.dttconstruct.world.SingleDynamicTreeFeature;
import maxhyper.dttconstruct.world.SpeciesFeatureConfiguration;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid = DynamicTreesTinkersConstruct.MOD_ID)
public class DTTConstructRegistries {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, DynamicTreesTinkersConstruct.MOD_ID);
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, DynamicTreesTinkersConstruct.MOD_ID);
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, DynamicTreesTinkersConstruct.MOD_ID);

    public static final RegistryObject<SingleDynamicTreeFeature> DYNAMIC_TREE_FEATURE = FEATURES.register("tree", ()->new SingleDynamicTreeFeature(SpeciesFeatureConfiguration.CODEC));
    public static final RegistryObject<ConfiguredFeature<SpeciesFeatureConfiguration, SingleDynamicTreeFeature>> BLOODSHROOM_CONFIGURED_FEATURE = CONFIGURED_FEATURES.register("bloodshroom", () -> new ConfiguredFeature(DYNAMIC_TREE_FEATURE.get(),
            new SpeciesFeatureConfiguration("dttconstruct:bloodshroom")));
    public static final RegistryObject<ConfiguredFeature<SpeciesFeatureConfiguration, SingleDynamicTreeFeature>> ENDERBARK_CONFIGURED_FEATURE = CONFIGURED_FEATURES.register("enderbark", () -> new ConfiguredFeature(DYNAMIC_TREE_FEATURE.get(),
            new SpeciesFeatureConfiguration("dttconstruct:enderbark")));
    public static final RegistryObject<ConfiguredFeature<SpeciesFeatureConfiguration, SingleDynamicTreeFeature>> GREENHEART_CONFIGURED_FEATURE = CONFIGURED_FEATURES.register("greenheart", () -> new ConfiguredFeature(DYNAMIC_TREE_FEATURE.get(),
            new SpeciesFeatureConfiguration("dttconstruct:greenheart")));
    public static final RegistryObject<ConfiguredFeature<SpeciesFeatureConfiguration, SingleDynamicTreeFeature>> SKYROOT_CONFIGURED_FEATURE = CONFIGURED_FEATURES.register("skyroot", () -> new ConfiguredFeature(DYNAMIC_TREE_FEATURE.get(),
            new SpeciesFeatureConfiguration("dttconstruct:skyroot")));
    public static final RegistryObject<ConfiguredFeature<SpeciesFeatureConfiguration, SingleDynamicTreeFeature>> BIOME_CONFIGURED_FEATURE = CONFIGURED_FEATURES.register("any_tree", () -> new ConfiguredFeature(DYNAMIC_TREE_FEATURE.get(),
            new BiomeSpeciesFeatureConfiguration()));

    public static void setup() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        FEATURES.register(modBus);
        CONFIGURED_FEATURES.register(modBus);
        PLACED_FEATURES.register(modBus);
    }

    public static void setupConnectables (){
        Block ichor = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("tconstruct","ichor_congealed_slime"));
        if (ichor != null) {
            BranchConnectables.makeBlockConnectable(ichor, (state, world, pos, side) -> {
                if (side == Direction.DOWN) {
                    BlockState branchState = world.getBlockState(pos.relative(Direction.UP));
                    BranchBlock branch = TreeHelper.getBranch(branchState);
                    if (branch != null) {
                        return Math.min(Math.max(1, branch.getRadius(branchState) - 1), 8);
                    } else {
                        return 8;
                    }
                }
                return 0;
            });
        }
    }

    @SubscribeEvent
    public static void onCellKitRegistry(RegistryEvent<CellKit> event) {
        DTCCellKits.register(event.getRegistry());
    }


}
