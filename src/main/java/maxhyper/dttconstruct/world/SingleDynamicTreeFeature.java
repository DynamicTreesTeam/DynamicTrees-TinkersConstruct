package maxhyper.dttconstruct.world;

import com.ferreusveritas.dynamictrees.block.rooty.SoilHelper;
import com.ferreusveritas.dynamictrees.tree.species.Species;
import com.ferreusveritas.dynamictrees.util.LevelContext;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import com.ferreusveritas.dynamictrees.worldgen.DynamicTreeFeature;
import com.ferreusveritas.dynamictrees.worldgen.GenerationContext;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

public class SingleDynamicTreeFeature extends Feature<SpeciesFeatureConfiguration> {

    public SingleDynamicTreeFeature(Codec<SpeciesFeatureConfiguration> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(@NotNull FeaturePlaceContext<SpeciesFeatureConfiguration> context) {
        LevelContext levelContext = LevelContext.create(context.level());
        ChunkPos chunkPos = context.level().getChunk(context.origin()).getPos();
        AtomicReference<Double> closestRingDistance = new AtomicReference<>((double) 1024);
        AtomicReference<BlockPos> closestRing = new AtomicReference<>(context.origin());
        AtomicReference<Integer> closestRingRad = new AtomicReference<>(0);
        DynamicTreeFeature.DISC_PROVIDER.getPoissonDiscs(levelContext, chunkPos).forEach((disc) -> {
            BlockPos ringPos = new BlockPos(disc.x, context.origin().getY(), disc.z);
            double dist = context.origin().distSqr(ringPos);
            if (dist < closestRingDistance.get()){
                closestRingDistance.set(dist);
                closestRing.set(ringPos);
                closestRingRad.set(disc.radius);
            }
        });
        BlockPos newOrigin = closestRing.get().below();
        Species species = context.config().getSpecies(levelContext, newOrigin);
        if (!species.isAcceptableSoilForWorldgen(context.level().getBlockState(newOrigin))){
            newOrigin = newOrigin.below();
            if (!species.isAcceptableSoilForWorldgen(context.level().getBlockState(newOrigin))){
                return false;
            }
        }

        if (!species.isValid()) return false;

        return species.generate(new GenerationContext(levelContext, species, newOrigin, newOrigin.mutable(), context.level().getBiome(newOrigin), Direction.Plane.HORIZONTAL.getRandomDirection(context.random()), closestRingRad.get(), SafeChunkBounds.ANY_WG));
    }
}
