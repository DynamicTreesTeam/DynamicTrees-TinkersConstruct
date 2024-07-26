//package maxhyper.dttconstruct.replacement;
//
//import com.ferreusveritas.dynamictrees.DynamicTrees;
//import com.ferreusveritas.dynamictrees.api.TreeHelper;
//import com.ferreusveritas.dynamictrees.init.DTConfigs;
//import com.ferreusveritas.dynamictrees.systems.poissondisc.PoissonDisc;
//import com.ferreusveritas.dynamictrees.trees.Species;
//import com.ferreusveritas.dynamictrees.util.BlockStates;
//import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
//import com.ferreusveritas.dynamictrees.util.WorldContext;
//import com.ferreusveritas.dynamictrees.worldgen.BiomeDatabase;
//import com.ferreusveritas.dynamictrees.worldgen.BiomeDatabases;
//import com.ferreusveritas.dynamictrees.worldgen.JoCode;
//import com.ferreusveritas.dynamictrees.worldgen.TreeGenerator;
//import com.mojang.serialization.Codec;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.Blocks;
//import net.minecraft.core.BlockPos;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.tags.BlockTags;
//import net.minecraft.tags.TagKey;
//import net.minecraft.util.Direction;
//import net.minecraft.util.RandomSource;
//import net.minecraft.util.math.ChunkPos;
//import net.minecraft.world.biome.Biome;
//import net.minecraft.world.level.WorldGenLevel;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.chunk.ChunkGenerator;
//import net.minecraft.world.level.levelgen.feature.Feature;
//import net.minecraftforge.registries.ForgeRegistries;
//
//import java.util.Collections;
//import java.util.List;
//
//public class SingleTreeFeature extends Feature<SingleTreeFeatureConfig> {
//
//    protected static TagKey<Block> congealed_slime_tag;
//
//    public SingleTreeFeature(Codec<SingleTreeFeatureConfig> codec) {
//        super(codec);
//        this.setRegistryName(new ResourceLocation(DynamicTrees.MOD_ID, "tree"));
//    }
//
//    @Override
//    public boolean place(SingleTreeFeatureConfig config, WorldGenLevel world, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
//        if (congealed_slime_tag == null) congealed_slime_tag = BlockTags.create(new ResourceLocation("tconstruct","congealed_slime"));
//
//        final ChunkPos chunkPos = world.getChunk(pos).getPos();
//        WorldContext worldContext = new WorldContext(world.getLevel().dimension(), world.getSeed(), world, world.getLevel());
//
//        if (DTConfigs.WORLD_GEN_DEBUG.get()) world.setBlock(pos.below(), Blocks.LAPIS_BLOCK.defaultBlockState(), 0);
//
//        //we find the closest valid poisson disc to place the tree there
//        List<PoissonDisc> discs = TreeGenerator.getTreeGenerator().getCircleProvider().getPoissonDiscs(worldContext, chunkPos);
//
//        BlockPos rootPos;
//        int radius;
//        if (discs.size() > 0) {
//            PoissonDisc closest = Collections.min(discs, (a,b)->{
//                int posY = pos.getY();
//                Double distA = pos.distSqr(a.x,posY,a.z, false);
//                Double distB = pos.distSqr(b.x,posY,b.z, false);
//                return distA.compareTo(distB);
//            });
//            //if the ground is air two blocks down it means the circle is outside the island
//            rootPos = new BlockPos(closest.x, pos.below().getY(), closest.z);
//            radius = closest.radius;
//        } else {
//            //if there arent any valid discs, fuck it just generate where it stands with a random radius
//            rootPos = pos;
//            radius = rand.nextInt(7) + 2;
//        }
//
//        if (!world.getBlockState(rootPos.above()).getMaterial().isReplaceable()) rootPos = rootPos.above();
//        else {
//            if (world.getBlockState(rootPos).getMaterial().isReplaceable()) rootPos = rootPos.below();
//            if (world.getBlockState(rootPos).getMaterial().isReplaceable()) {
//                if (DTConfigs.WORLD_GEN_DEBUG.get()) world.setBlock(rootPos.above(), Blocks.GOLD_BLOCK.defaultBlockState(), 0);
//                return false;
//            }
//        }
//
//        BlockState rootState = world.getBlockState(rootPos);
//        //if the state is a tree part that means there's already a tree!
//        if (TreeHelper.getTreePart(rootState) != TreeHelper.NULL_TREE_PART) return false;
//
//        //This is the default soil block of the island, used to replace and test generation
//        Block defaultSoilBlock = ForgeRegistries.BLOCKS.getValue(config.soilBlock);
//        if (defaultSoilBlock == null) return false;
//        BlockState deafaultSoilState = defaultSoilBlock.defaultBlockState();
//
//        Biome biome = world.getBiome(rootPos);
//
//        //get the species either from the biome database or from the config
//        Species species;
//        if (config.generateFromBiome){
//            BiomeDatabase biomeDatabase = BiomeDatabases.getDimensionalOrDefault(world.getLevel().dimension().location());
//            species = biomeDatabase.getEntry(biome).getSpeciesSelector().getSpecies(rootPos, BlockStates.GRASS, rand).getSpecies();
//            if (!species.isAcceptableSoil(deafaultSoilState)) species = Species.REGISTRY.get(config.species);
//        } else
//            species = Species.REGISTRY.get(config.species);
//
//
//        if (!species.isValid()) return false;
//
//        //replace congealed slime and sand with a useful soil
//        Block rootBlock = rootState.getBlock();
//        if (congealed_slime_tag.contains(rootBlock) || rootBlock == Blocks.SAND) {
//            world.setBlock(rootPos, deafaultSoilState, 0);
//        }
//
//        //this is a debug only issue where one point will place a gold block and another will generate on it
//        if (DTConfigs.WORLD_GEN_DEBUG.get() && (rootBlock == Blocks.GOLD_BLOCK || rootBlock == Blocks.LAPIS_BLOCK)){
//            return false;
//        }
//
//        JoCode joCode = species.getRandomJoCode(radius, rand).orElse(null);
//        if (joCode == null) return false;
//
//        //Finally, if the soil is valid generate the tree
//        if (species.isAcceptableSoilForWorldgen(world, rootPos, world.getBlockState(rootPos)))
//            joCode.generate(worldContext, species, rootPos, biome, Direction.Plane.HORIZONTAL.getRandomDirection(rand), radius, SafeChunkBounds.ANY_WG, false);
//        else {
//            world.setBlock(rootPos, Blocks.DIAMOND_BLOCK.defaultBlockState(), 0);
//            return false;
//        }
//
//        //if the root block is not a tree part that means the tree generation failed
//        if (TreeHelper.getTreePart(world.getBlockState(rootPos)) == TreeHelper.NULL_TREE_PART) {
//            if (DTConfigs.WORLD_GEN_DEBUG.get()) world.setBlock(rootPos, Blocks.NETHERITE_BLOCK.defaultBlockState(), 0);
//            return false;
//        }
//
//        return true;
//    }
//
//}
