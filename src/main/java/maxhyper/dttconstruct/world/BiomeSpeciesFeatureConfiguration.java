package maxhyper.dttconstruct.world;

import com.ferreusveritas.dynamictrees.DynamicTrees;
import com.ferreusveritas.dynamictrees.api.worldgen.BiomePropertySelectors;
import com.ferreusveritas.dynamictrees.tree.species.Species;
import com.ferreusveritas.dynamictrees.util.LevelContext;
import com.ferreusveritas.dynamictrees.worldgen.BiomeDatabase;
import com.ferreusveritas.dynamictrees.worldgen.BiomeDatabases;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class BiomeSpeciesFeatureConfiguration extends SpeciesFeatureConfiguration {

    public BiomeSpeciesFeatureConfiguration() {
        super(DynamicTrees.location("oak").toString());
    }

    public Species getSpecies(LevelContext level, BlockPos pos){
        BiomeDatabase biomeDatabase = BiomeDatabases.getDimensionalOrDefault(level.dimensionName());
        BiomeDatabase.EntryReader biomeEntry = biomeDatabase.getEntry(level.accessor().getBiome(pos));
        BiomePropertySelectors.SpeciesSelector speciesSelector = biomeEntry.getSpeciesSelector();
        BiomePropertySelectors.SpeciesSelection speciesSelection = speciesSelector.getSpecies(pos, level.accessor().getBlockState(pos), level.accessor().getRandom());
        if (!biomeEntry.isBlacklisted() && speciesSelection.isHandled()) {
            return speciesSelection.getSpecies();
        }
        return super.getSpecies(level, pos);
    }
}
