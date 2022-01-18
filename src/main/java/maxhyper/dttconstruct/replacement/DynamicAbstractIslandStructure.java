package maxhyper.dttconstruct.replacement;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import slimeknights.tconstruct.world.worldgen.islands.AbstractIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.SlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.islands.variants.IIslandVariant;

public abstract class DynamicAbstractIslandStructure extends AbstractIslandStructure {

    DynamicAbstractIslandStructure (ResourceLocation registryName){
        setRegistryName(registryName);
    }

    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return DefaultStart2::new;
    }

    public class DefaultStart2 extends StructureStart<NoFeatureConfig> {
        public DefaultStart2(Structure<NoFeatureConfig> structureIn, int chunkPosX, int chunkPosZ, MutableBoundingBox bounds, int references, long seed) {
            super(structureIn, chunkPosX, chunkPosZ, bounds, references, seed);
        }

        public void generatePieces(DynamicRegistries registries, ChunkGenerator generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn, NoFeatureConfig config) {
            // determine orientation
            Rotation rotation = Rotation.values()[this.random.nextInt(Rotation.values().length)];
            // determine coords
            int x = chunkX * 16 + 4 + this.random.nextInt(8);
            int z = chunkZ * 16 + 4 + this.random.nextInt(8);
            int y = getHeight(generator, rotation, x, z, this.random);

            IIslandVariant variant = getVariant(random);
            // fetch the tree now so its consistent on the whole island
            SlimeIslandPiece slimeIslandPiece = new SlimeIslandPiece(templateManagerIn, variant, SIZES[this.random.nextInt(SIZES.length)], new BlockPos(x, y, z), variant.getTreeFeature(random), rotation);
            this.pieces.add(slimeIslandPiece);
            this.calculateBoundingBox();
            System.out.println(" ASAFASA : " + this);
        }
    }

}
