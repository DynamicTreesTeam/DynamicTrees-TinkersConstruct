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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import oshi.util.tuples.Pair;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.DirtType;
import slimeknights.tconstruct.world.block.FoliageType;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class SlimeMangroveRootsBlock extends BasicRootsBlock {

    public enum GrassType implements StringRepresentable {
        EARTH(FoliageType.EARTH),
        SKY(FoliageType.SKY),
        ICHOR(FoliageType.ICHOR),
        ENDER(FoliageType.ENDER),
        BLOOD(FoliageType.BLOOD),
        NONE(null);

        final FoliageType original;
        GrassType (FoliageType original){
            this.original = original;
        }

        public static GrassType getFromFoliage (FoliageType foliageType){
            return Enum.valueOf(GrassType.class, foliageType.getSerializedName());
        }

        public FoliageType asFoliage(){
            return original;
        }

        public String getSerializedName() {
            if (original == null) return toString().toLowerCase(Locale.ENGLISH);
            return original.getSerializedName();
        }
    }

    public static final EnumProperty<DirtType> DIRT_TYPE = EnumProperty.create("dirt_type", DirtType.class);
    public static final EnumProperty<GrassType> GRASS_TYPE = EnumProperty.create("grass_type", GrassType.class);

    public SlimeMangroveRootsBlock(ResourceLocation name, Properties properties) {
        super(name, properties.randomTicks());
        registerDefaultState(defaultBlockState()
                .setValue(GRASS_TYPE, GrassType.NONE)
                .setValue(DIRT_TYPE, DirtType.EARTH));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
       super.createBlockStateDefinition(builder);
       builder.add(DIRT_TYPE).add(GRASS_TYPE);
    }

    @Override
    public SlimeMangroveFamily getFamily() {
        return (SlimeMangroveFamily)super.getFamily();
    }

    public Block getPrimitiveSlimeDirt(BlockState state){
        if (!state.is(this)) return Blocks.AIR;
        DirtType slime = state.getValue(DIRT_TYPE);
        GrassType grass = state.getValue(GRASS_TYPE);
        if (grass == GrassType.NONE){
            return TinkerWorld.slimeDirt.get(slime);
        }
        return TinkerWorld.slimeGrass.get(slime).get(grass.asFoliage());
    }

    private Block getPrimitiveAny (BlockState state){
        return state.getValue(LAYER).getPrimitive(getFamily()).orElse(null);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {

        if (isFullBlock(state)){
            level.setBlock(pos, state.setValue(LAYER, Layer.FILLED).setValue(GRASS_TYPE, GrassType.NONE), level.isClientSide ? 11 : 3);
            this.spawnDestroyParticles(level, player, pos, state);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            Block primitive = getPrimitiveAny(state);
            if (!player.isCreative() && primitive != null) dropResources(getPrimitiveSlimeDirt(state).defaultBlockState(), level, pos, null, player, player.getMainHandItem());
            return false;
        }
        return this.removedByEntity(state, level, pos, player);
    }

    @Override
    public int setRadius(LevelAccessor level, BlockPos pos, int radius, @javax.annotation.Nullable Direction originDir, int flags) {
        destroyMode = DynamicTrees.DestroyMode.SET_RADIUS;
        BlockState currentState = level.getBlockState(pos);
        boolean replacingWater = currentState.getFluidState() == Fluids.WATER.getSource(false);
        boolean replacingGround = this.getFamily().isAcceptableSoilForRootSystem(currentState);
        boolean setWaterlogged = replacingWater && !replacingGround;
        Layer layer = currentState.is(this) ? currentState.getValue(LAYER) : (replacingGround ? BasicRootsBlock.Layer.COVERED : BasicRootsBlock.Layer.EXPOSED);
        DirtType slimeType = DirtType.EARTH;
        GrassType grassType = GrassType.NONE;
        if (replacingGround){
            Pair<DirtType, GrassType> pair = getSlimeFromState(currentState);
            slimeType = pair.getA();
            if (canBeGrassy(level, pos)){
                grassType = pair.getB();
            }
        }
        level.setBlock(pos, this.getStateForRadius(radius)
                .setValue(LAYER, layer)
                .setValue(WATERLOGGED, setWaterlogged)
                .setValue(DIRT_TYPE, slimeType)
                .setValue(GRASS_TYPE, grassType), flags);

        destroyMode = DynamicTrees.DestroyMode.SLOPPY;
        return radius;
    }

    protected boolean canBeGrassy(LevelAccessor level, BlockPos pos) {
        BlockPos upPos = pos.above();
        BlockState upState = level.getBlockState(upPos);
        return !upState.isCollisionShapeFullBlock(level, upPos) && upState.getFluidState().isEmpty();
    }

    private Pair<DirtType, GrassType> getSlimeFromState (BlockState state){
        for (Map.Entry<DirtType, EnumObject<FoliageType, Block>> entry : TinkerWorld.slimeGrass.entrySet()){
            for (FoliageType type : FoliageType.values()){
                if (state.is(entry.getValue().get(type))){
                    return new Pair<>(entry.getKey(), GrassType.getFromFoliage(type));
                }
            }
        }
        for (DirtType type : DirtType.TINKER){
            if (state.is(TinkerWorld.slimeDirt.get(type))){
                return new Pair<>(type, GrassType.NONE);
            }
        }

        return new Pair<>(DirtType.EARTH, GrassType.NONE);
    }


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

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        if (state.getValue(LAYER) == Layer.COVERED)
            return getPrimitiveSlimeDirt(state).getSoundType(state, level, pos, entity);
        return super.getSoundType(state, level, pos, entity);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        if (state.getValue(LAYER) == Layer.COVERED)
            return getPrimitiveSlimeDirt(state).getCloneItemStack(state, target, level, pos, player);
        return super.getCloneItemStack(state, target, level, pos, player);
    }

    @OnlyIn(Dist.CLIENT)
    public int foliageColorMultiplier(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        GrassType grass = state.getValue(GRASS_TYPE);
        if (grass == GrassType.NONE) return 1;
        return grass.asFoliage().getColor();
    }

}
