package maxhyper.dttconstruct.world;

import com.ferreusveritas.dynamictrees.tree.species.Species;
import com.ferreusveritas.dynamictrees.util.LevelContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class SpeciesFeatureConfiguration implements FeatureConfiguration {
    public static final Codec<SpeciesFeatureConfiguration> CODEC = RecordCodecBuilder.create((builder) ->
            builder.group(Codec.STRING.fieldOf("species").forGetter((c)->c.speciesName)).apply(builder, SpeciesFeatureConfiguration::new)
    );
    private final String speciesName;
    private Species species = Species.NULL_SPECIES;

    public SpeciesFeatureConfiguration(String species) {
        this.speciesName = species;
    }

    public Species getSpecies(LevelContext level, BlockPos pos){
        if (!species.isValid())
            species = Species.REGISTRY.get(speciesName);
        return species;
    }
}
