package maxhyper.dttconstruct.replacement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class SingleTreeFeatureConfig implements IFeatureConfig {
    public static final Codec<SingleTreeFeatureConfig> CODEC = RecordCodecBuilder.create(
            (instance) -> instance.group(Codec.STRING.fieldOf("species").orElse("dynamictrees:null").forGetter(
                    (config) -> config.species.toString()),
                    Codec.BOOL.fieldOf("generate_from_biome").orElse(false).forGetter(
                            (config) -> config.generateFromBiome),
                    Codec.STRING.fieldOf("soil_bLock").orElse("minecraft:air").forGetter(
                            (config) -> config.soilBlock.toString())
            ).apply(instance, SingleTreeFeatureConfig::new)
    );

    private SingleTreeFeatureConfig (String species, boolean fromBiome, String soilBLock) {
        this(new ResourceLocation(species), fromBiome, new ResourceLocation(soilBLock));
    }

    protected SingleTreeFeatureConfig (ResourceLocation species, ResourceLocation soilBlock) {
        this(species, false, soilBlock);
    }
    protected SingleTreeFeatureConfig (ResourceLocation defaultSpecies, boolean fromBiome, ResourceLocation soilBlock) {
        this.species = defaultSpecies;
        this.generateFromBiome = fromBiome;
        this.soilBlock = soilBlock;
    }

    public final ResourceLocation species;
    public final ResourceLocation soilBlock;
    public final boolean generateFromBiome;

}
