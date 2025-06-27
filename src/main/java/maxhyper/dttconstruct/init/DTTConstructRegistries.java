package maxhyper.dttconstruct.init;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.cell.CellKit;
import com.ferreusveritas.dynamictrees.api.registry.RegistryEvent;
import com.ferreusveritas.dynamictrees.api.registry.TypeRegistryEvent;
import com.ferreusveritas.dynamictrees.block.branch.BranchBlock;
import com.ferreusveritas.dynamictrees.block.rooty.SoilProperties;
import com.ferreusveritas.dynamictrees.systems.BranchConnectables;
import com.ferreusveritas.dynamictrees.tree.family.Family;
import com.mojang.serialization.Codec;
import maxhyper.dttconstruct.DynamicTreesTinkersConstruct;
import maxhyper.dttconstruct.cellkits.DTCCellKits;
import maxhyper.dttconstruct.trees.SlimeMangroveFamily;
import maxhyper.dttconstruct.world.BiomeSpeciesFeatureConfiguration;
import maxhyper.dttconstruct.world.SingleDynamicTreeFeature;
import maxhyper.dttconstruct.world.SpeciesFeatureConfiguration;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid = DynamicTreesTinkersConstruct.MOD_ID)
public class DTTConstructRegistries {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, DynamicTreesTinkersConstruct.MOD_ID);

    public static final Supplier<SingleDynamicTreeFeature> DYNAMIC_TREE_FEATURE = FEATURES.register("tree", ()->new SingleDynamicTreeFeature(SpeciesFeatureConfiguration.CODEC));
    public static final Supplier<SingleDynamicTreeFeature> DYNAMIC_TREE_FEATURE_BIOME = FEATURES.register("tree_biome", ()->new SingleDynamicTreeFeature(Codec.unit(BiomeSpeciesFeatureConfiguration::new)));

    public static void setup(IEventBus modBus) {
        FEATURES.register(modBus);
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


    @SubscribeEvent
    public static void registerFamilyTypes (final TypeRegistryEvent<Family> event) {
        event.registerType(DynamicTreesTinkersConstruct.location("slime_mangrove"), SlimeMangroveFamily.TYPE);
    }

    @SubscribeEvent
    public static void registerSoilPropertiesTypes (final TypeRegistryEvent<SoilProperties> event) {

    }

}
