package maxhyper.dttconstruct.init;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.cell.CellKit;
import com.ferreusveritas.dynamictrees.api.registry.RegistryEvent;
import com.ferreusveritas.dynamictrees.block.branch.BranchBlock;
import com.ferreusveritas.dynamictrees.systems.BranchConnectables;
import maxhyper.dttconstruct.DynamicTreesTinkersConstruct;
import maxhyper.dttconstruct.cellkits.DTCCellKits;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid = DynamicTreesTinkersConstruct.MOD_ID)
public class DTTConstructRegistries {

//    public static final DeferredRegister<Structure<?>> STRUCTURE_FEATURES
//            = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, DynamicTreesTConstruct.MOD_ID);

    public static void setup() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        //STRUCTURE_FEATURES.register(bus);
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
