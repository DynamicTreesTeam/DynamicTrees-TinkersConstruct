package maxhyper.dttconstruct.init;

import maxhyper.dttconstruct.DynamicTreesTinkersConstruct;
import maxhyper.dttconstruct.blocks.SlimeMangroveRootsBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class DTConstructClient {

    @SuppressWarnings({"deprecation","removal"})
    public static void setup() {
        BlockColors blockColors = Minecraft.getInstance().getBlockColors();
        Block block = ForgeRegistries.BLOCKS.getValue(DynamicTreesTinkersConstruct.location("enderbark_roots"));
        if (block instanceof SlimeMangroveRootsBlock enderbarkRoots){
            blockColors.register((state, level, pos, tintIndex) ->
                    tintIndex == 0 ? enderbarkRoots.foliageColorMultiplier(state, level, pos) : 1, enderbarkRoots);
            ItemBlockRenderTypes.setRenderLayer(enderbarkRoots, RenderType.cutoutMipped());
        }
    }

}
