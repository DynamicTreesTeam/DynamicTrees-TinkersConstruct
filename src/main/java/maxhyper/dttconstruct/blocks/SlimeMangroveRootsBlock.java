package maxhyper.dttconstruct.blocks;

import com.ferreusveritas.dynamictrees.DynamicTrees;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.block.branch.BasicRootsBlock;
import com.ferreusveritas.dynamictrees.block.rooty.SoilHelper;
import com.ferreusveritas.dynamictrees.tree.family.Family;
import maxhyper.dttconstruct.trees.SlimeMangroveFamily;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import oshi.util.tuples.Pair;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.DirtType;
import slimeknights.tconstruct.world.block.FoliageType;
import slimeknights.tconstruct.world.client.SlimeColorizer;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;

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
            return Enum.valueOf(GrassType.class, foliageType.getSerializedName().toUpperCase(Locale.ENGLISH));
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
                .setValue(DIRT_TYPE, DirtType.VANILLA));
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
            return TinkerWorld.allDirt.get(slime);
        }
        return TinkerWorld.slimeGrass.get(slime).get(grass.asFoliage());
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {

        if (isFullBlock(state)){
            level.setBlock(pos, state.setValue(LAYER, Layer.FILLED).setValue(GRASS_TYPE, GrassType.NONE), level.isClientSide ? 11 : 3);
            this.spawnDestroyParticles(level, player, pos, state);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            Block primitive = getPrimitiveSlimeDirt(state);
            if (!player.isCreative() && primitive != null) dropResources(primitive.defaultBlockState(), level, pos, null, player, player.getMainHandItem());
            return false;
        }
        return this.removedByEntity(state, level, pos, player);
    }

    @Override
    public int setRadius(LevelAccessor level, BlockPos pos, int radius, @javax.annotation.Nullable Direction originDir, int flags) {
        destroyMode = DynamicTrees.DestroyMode.SET_RADIUS;
        BlockState currentState = level.getBlockState(pos);
        boolean replacingWater = currentState.getFluidState() == getFluid(currentState).getSource(false);
        boolean replacingGround = this.getFamily().isAcceptableSoilForRootSystem(currentState);
        boolean setWaterlogged = replacingWater && !replacingGround;
        boolean isFullBlock = radius >= 8;
        Layer layer;
        if (currentState.is(this)){
            layer = currentState.getValue(LAYER);
            if (layer == Layer.COVERED && isFullBlock){
                layer = Layer.FILLED;
            }
        } else layer = replacingGround ? Layer.COVERED : Layer.EXPOSED;

        DirtType dirtType = currentState.is(this) ? currentState.getValue(DIRT_TYPE) : DirtType.VANILLA;
        GrassType grassType = currentState.is(this) ? currentState.getValue(GRASS_TYPE) : GrassType.NONE;
        if (replacingGround){
            Pair<DirtType, GrassType> pair = getSlimeFromState(currentState);
            if (pair != null){
                dirtType = pair.getA();
                if (canBeGrassy(level, pos) && !isFullBlock){
                    grassType = pair.getB();
                }
            }
        }
        level.setBlock(pos, this.getStateForRadius(radius)
                .setValue(LAYER, layer)
                .setValue(WATERLOGGED, setWaterlogged)
                .setValue(DIRT_TYPE, dirtType)
                .setValue(GRASS_TYPE, grassType), flags);

        destroyMode = DynamicTrees.DestroyMode.SLOPPY;
        return radius;
    }

    protected boolean canBeGrassy(LevelAccessor level, BlockPos pos) {
        BlockPos upPos = pos.above();
        BlockState upState = level.getBlockState(upPos);
        return !upState.isCollisionShapeFullBlock(level, upPos) && upState.getFluidState().isEmpty();
    }

    private Pair<DirtType, GrassType> getSlimeFromState (BlockState state1){
        BlockState state = state1;
        if (SoilHelper.isSoilRegistered(state.getBlock()) && SoilHelper.getProperties(state.getBlock()).hasSubstitute() && SoilHelper.getProperties(state.getBlock()).getBlock().isPresent()){
            state = SoilHelper.getProperties(state.getBlock()).getBlock().get().getPrimitiveSoilBlock().defaultBlockState();
        }
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

        return null;
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!isFullBlock(state)) {
            Layer layer = Layer.COVERED;
            if (state.getValue(RADIUS) >= 8){
                if (state.getValue(LAYER) == Layer.EXPOSED)
                    layer = Layer.FILLED;
                else layer = null;
            }
            if (layer != null) {
                ItemStack handStack = player.getItemInHand(hand);
                if (handStack.getItem() instanceof BlockItem blockItem){
                    Pair<DirtType, GrassType> coverProperties = getSlimeFromState(blockItem.getBlock().defaultBlockState());
                    if (coverProperties != null) {
                        BlockState newState = state
                                .setValue(LAYER, layer)
                                .setValue(WATERLOGGED, false)
                                .setValue(DIRT_TYPE, coverProperties.getA())
                                .setValue(GRASS_TYPE, coverProperties.getB());
                        if (this.canPlace(player, level, pos, newState)) {
                            level.setBlock(pos, newState, 3);
                            if (!player.isCreative()) {
                                handStack.shrink(1);
                            }

                            level.playSound(null, pos, blockItem.getBlock().getSoundType(state, level, pos, player).getPlaceSound(), SoundSource.BLOCKS, 1.0F, 0.8F);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        ItemStack heldItem = player.getItemInHand(hand);
        return TreeHelper.getTreePart(state).getFamily(state, level, pos).onTreeActivated(new Family.TreeActivationContext(level, TreeHelper.findRootNode(level, pos), pos, state, player, hand, heldItem, hitResult)) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
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
        return SlimeColorizer.getColorForPos(pos, grass.asFoliage());
    }

    @Override
    public BlockState getStateForDecay(BlockState state, LevelAccessor level, BlockPos pos) {
        boolean waterlogged = state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED);
        Layer layer = state.hasProperty(LAYER) ? state.getValue(LAYER) : BasicRootsBlock.Layer.EXPOSED;
        if (layer == Layer.COVERED){
            return getPrimitiveSlimeDirt(state).defaultBlockState();
        } else
            return waterlogged ? getFluid(state).defaultFluidState().createLegacyBlock() : Blocks.AIR.defaultBlockState();
    }

    @Deprecated
    public float getDestroyProgress(BlockState pState, Player pPlayer, BlockGetter pLevel, BlockPos pPos) {
        if (pState.hasProperty(LAYER) && pState.getValue(LAYER) == Layer.COVERED){
            Block slimeDirt = getPrimitiveSlimeDirt(pState);
            return slimeDirt.getDestroyProgress(slimeDirt.defaultBlockState(), pPlayer, pLevel, pPos);
        }
        return super.getDestroyProgress(pState, pPlayer, pLevel, pPos);
    }

    private FlowingFluid getFluid(BlockState state){
        return TinkerFluids.enderSlime.get();
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? getFluid(state).getSource(false) : super.getFluidState(state);
    }

    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, getFluid(stateIn), getFluid(stateIn).getTickDelay(level));
        }

        return super.updateShape(stateIn, facing, facingState, level, currentPos, facingPos);
    }

    public boolean canPlaceLiquid(BlockGetter pLevel, BlockPos pPos, BlockState pState, Fluid pFluid) {
        return !this.isFullBlock(pState) && !(Boolean)pState.getValue(BlockStateProperties.WATERLOGGED) && pFluid == getFluid(pState);
    }

}
