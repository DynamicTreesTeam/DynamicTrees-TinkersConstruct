package maxhyper.dttconstruct;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.blocks.branches.BranchBlock;
import com.ferreusveritas.dynamictrees.systems.BranchConnectables;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.world.TinkerWorld;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class DTTConstructRegistries {

    public static final DeferredRegister<Structure<?>> STRUCTURE_FEATURES
            = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, DynamicTreesTConstruct.MOD_ID);

    public static void setup() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        STRUCTURE_FEATURES.register(bus);
    }

    public static void setupConnectables (){
        Block ichor = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("tconstruct","ichor_congealed_slime"));
        if (ichor != null) {
            BranchConnectables.makeBlockConnectable(ichor, (state, world, pos, side) -> {
                if (side == Direction.DOWN) {
                    BlockState branchState = world.getBlockState(pos.relative(Direction.UP));
                    BranchBlock branch = TreeHelper.getBranch(branchState);
                    if (branch != null) {
                        return MathHelper.clamp(branch.getRadius(branchState) - 1, 1, 8);
                    } else {
                        return 8;
                    }
                }
                return 0;
            });
        }
    }

}
