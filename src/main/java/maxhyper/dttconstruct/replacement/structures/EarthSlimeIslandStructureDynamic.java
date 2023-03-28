package maxhyper.dttconstruct.replacement.structures;

import maxhyper.dttconstruct.replacement.SlimeIslandReplacement;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;
import slimeknights.tconstruct.world.worldgen.islands.AbstractIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.EarthSlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.SlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.islands.variants.IIslandVariant;

import java.util.Random;

import static com.ferreusveritas.dynamictrees.init.DTRegistries.DYNAMIC_TREE_CONFIGURED_FEATURE;

public class EarthSlimeIslandStructureDynamic extends EarthSlimeIslandStructure {

    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return (x$0, x$1, x$2, x$3, x$4, x$5) -> new DefaultStart(x$0, x$1, x$2, x$3, x$4, x$5){
            @Override
            public void generatePieces(DynamicRegistries registries, ChunkGenerator generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn, NoFeatureConfig config) {
                Rotation rotation = Rotation.values()[this.random.nextInt(Rotation.values().length)];
                int x = chunkX * 16 + 4 + this.random.nextInt(8);
                int z = chunkZ * 16 + 4 + this.random.nextInt(8);
                int y = getHeight(generator, rotation, x, z, this.random);
                IIslandVariant variant = getVariant(this.random);
                SlimeIslandPiece slimeIslandPiece = new SlimeIslandPiece(templateManagerIn, variant, AbstractIslandStructure.SIZES[this.random.nextInt(AbstractIslandStructure.SIZES.length)], new BlockPos(x, y, z), getTreeFeature(), rotation);
                this.pieces.add(slimeIslandPiece);
                this.calculateBoundingBox();
            }
        };
    }

    protected ConfiguredFeature<?,?> getTreeFeature (){
        return SlimeIslandReplacement.SINGLE_TREE_GREENHEART_CONFIGURED_FEATURE;
    }

}