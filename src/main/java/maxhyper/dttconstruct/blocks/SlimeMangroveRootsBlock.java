package maxhyper.dttconstruct.blocks;

import com.ferreusveritas.dynamictrees.DynamicTrees;
import com.ferreusveritas.dynamictrees.block.branch.BasicRootsBlock;
import maxhyper.dttconstruct.trees.SlimeMangroveFamily;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.IPlantable;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.DirtType;
import slimeknights.tconstruct.world.block.FoliageType;

import java.util.Locale;
import java.util.Map;

public class SlimeMangroveRootsBlock extends BasicRootsBlock {

    //public static final BooleanProperty GRASSY = BooleanProperty.create("grassy");
    public static final EnumProperty<SlimeType> SLIME_TYPE = EnumProperty.create("slime_type", SlimeType.class);

    public SlimeMangroveRootsBlock(ResourceLocation name, Properties properties) {
        super(name, properties.randomTicks());
        registerDefaultState(defaultBlockState()
                //.setValue(GRASSY, false)
                .setValue(SLIME_TYPE, SlimeType.EARTH));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
       super.createBlockStateDefinition(builder);
       builder.add(SLIME_TYPE)
               //.add(GRASSY)
               ;
    }

    @Override
    public SlimeMangroveFamily getFamily() {
        return (SlimeMangroveFamily)super.getFamily();
    }

//    private Optional<Block> getPrimitiveGrassIfGrassy (BlockState state){
//        if (isFullBlock(state) && state.hasProperty(GRASSY) && state.getValue(GRASSY)){
//            return getFamily().getPrimitiveGrassyRoots(state.getValue(SLIME_TYPE));
//        }
//        return Optional.empty();
//    }

    private Block getPrimitiveAny (BlockState state){
        return state.getValue(LAYER).getPrimitive(getFamily()).orElse(null);
    }

//    @Override
//    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
//
//        if (isFullBlock(state)){
//            level.setBlock(pos, state.setValue(LAYER, Layer.FILLED).setValue(GRASSY, false), level.isClientSide ? 11 : 3);
//            this.spawnDestroyParticles(level, player, pos, state);
//            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
//            Block primitive = getPrimitiveAny(state);
//            if (!player.isCreative() && primitive != null) dropResources(primitive.defaultBlockState(), level, pos, null, player, player.getMainHandItem());
//            return false;
//        }
//        return this.removedByEntity(state, level, pos, player);
//    }

    @Override
    public int setRadius(LevelAccessor level, BlockPos pos, int radius, @javax.annotation.Nullable Direction originDir, int flags) {
        destroyMode = DynamicTrees.DestroyMode.SET_RADIUS;
        BlockState currentState = level.getBlockState(pos);
        boolean replacingWater = currentState.getFluidState() == Fluids.WATER.getSource(false);
        boolean replacingGround = this.getFamily().isAcceptableSoilForRootSystem(currentState);
        boolean setWaterlogged = replacingWater && !replacingGround;
        Layer layer = currentState.is(this) ? currentState.getValue(LAYER) : (replacingGround ? BasicRootsBlock.Layer.COVERED : BasicRootsBlock.Layer.EXPOSED);
        SlimeType slimeType = SlimeType.EARTH;
        if (replacingGround){
            slimeType = getSlimeFromState(currentState).getA();
        }
        level.setBlock(pos, this.getStateForRadius(radius)
                .setValue(LAYER, layer)
                .setValue(WATERLOGGED, setWaterlogged)
                .setValue(SLIME_TYPE, slimeType), flags);

        destroyMode = DynamicTrees.DestroyMode.SLOPPY;
        return radius;
    }

    private Pair<SlimeType, FoliageType> getSlimeFromState (BlockState state){
        for (Map.Entry<DirtType, EnumObject<FoliageType, Block>> entry : TinkerWorld.slimeGrass.entrySet()){
            for (FoliageType type : FoliageType.values()){
                if (state.is(entry.getValue().get(type))){
                    return new Pair<>(entry.getKey().asSlime(), type);
                }
            }
        }
        return new Pair<>(SlimeType.EARTH, FoliageType.EARTH);
    }

//    protected boolean canBeGrassy(LevelAccessor level, BlockPos pos) {
//        BlockPos upPos = pos.above();
//        BlockState upState = level.getBlockState(upPos);
//        return !upState.isCollisionShapeFullBlock(level, upPos) && upState.getFluidState().isEmpty();
//    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
//        //if it's not covered don't tick.
//        if (!state.is(this) || state.getValue(LAYER) != Layer.COVERED) return;
//        int requiredLight = getFamily().getGrassSpreadRequiredLight();
//        //this is a similar behaviour to vanilla grass spreading but inverted to be handled by the dirt block
//        if (!level.isClientSide) {
//            if (!level.isAreaLoaded(pos, 3)) {
//                return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
//            }
//            if (!canBeGrassy(level, pos)){
//                level.setBlock(pos, state.setValue(GRASSY, false), 3);
//            } else if (level.getMaxLocalRawBrightness(pos.above()) >= requiredLight) {
//                for (int i = 0; i < 4; ++i) {
//                    BlockPos thatPos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
//
//                    if (!level.hasChunkAt(thatPos)) { return; }
//                    BlockState thatState = level.getBlockState(thatPos);
//
//                    Block block = getFamily().getPrimitiveGrassyRoots().orElse(null);
//                    if (block != null && thatState.getBlock() == block) {
//                        level.setBlock(pos, state.setValue(GRASSY, true), 3);
//                        return;
//                    }
//                }
//            }
//        }

    }

    //to-do: port to base DT
    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
        return super.canSustainPlant(state, world, pos, facing, plantable)
                || (state.getValue(LAYER) == Layer.COVERED);
    }

//    @Override
//    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
//        Optional<Block> primitiveGrass = getPrimitiveGrassIfGrassy(state);
//        if (primitiveGrass.isPresent())
//            return primitiveGrass.get().getSoundType(state, level, pos, entity);
//        return super.getSoundType(state, level, pos, entity);
//    }
//
//    @Override
//    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
//        Optional<Block> primitiveGrass = getPrimitiveGrassIfGrassy(state);
//        if (primitiveGrass.isPresent())
//            return primitiveGrass.get().getCloneItemStack(state, target, level, pos, player);
//        return super.getCloneItemStack(state, target, level, pos, player);
//    }

}
