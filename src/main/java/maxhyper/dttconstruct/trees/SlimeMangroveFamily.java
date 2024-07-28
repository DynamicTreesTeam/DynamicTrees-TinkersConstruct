package maxhyper.dttconstruct.trees;

import com.ferreusveritas.dynamictrees.api.registry.TypedRegistry;
import com.ferreusveritas.dynamictrees.block.branch.BasicRootsBlock;
import com.ferreusveritas.dynamictrees.block.branch.BranchBlock;
import com.ferreusveritas.dynamictrees.tree.family.Family;
import com.ferreusveritas.dynamictrees.tree.family.MangroveFamily;
import com.ferreusveritas.dynamictrees.util.Optionals;
import maxhyper.dttconstruct.blocks.SlimeMangroveRootsBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class SlimeMangroveFamily extends MangroveFamily {

    public static final TypedRegistry.EntryType<Family> TYPE = TypedRegistry.newType(SlimeMangroveFamily::new);

    public SlimeMangroveFamily(ResourceLocation name) {
        super(name);
    }

    @Override
    protected BranchBlock createRootsBlock(ResourceLocation name) {
        final BasicRootsBlock branch = new SlimeMangroveRootsBlock(name, this.getProperties());
        if (this.isFireProof()) branch.setFireSpreadSpeed(0).setFlammability(0);
        return branch;
    }

//    private Block primitiveRootsGrassy;
//
//    public void setPrimitiveRootsGrassy(Block primitiveRootsCovered, int index) {
//        this.primitiveRootsGrassy = primitiveRootsCovered;
//    }
//
//    public Optional<Block> getPrimitiveGrassyRoots(SlimeMangroveRootsBlock.Slime slimeType) {
//        return Optionals.ofBlock(primitiveRootsGrassy);
//    }

//    private int grassSpreadRequiredLight = 9;
//
//    public void setGrassSpreadRequiredLight(int grassSpreadRequiredLight) {
//        this.grassSpreadRequiredLight = grassSpreadRequiredLight;
//    }
//
//    public int getGrassSpreadRequiredLight() {
//        return grassSpreadRequiredLight;
//    }
}
