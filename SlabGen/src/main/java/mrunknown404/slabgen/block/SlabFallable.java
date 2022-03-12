package mrunknown404.slabgen.block;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SlabFallable extends FallingBlock implements SimpleWaterloggedBlock {
	private static final VoxelShape BOTTOM_AABB = Block.box(0, 0, 0, 16, 8, 16), TOP_AABB = Block.box(0, 8, 0, 16, 16, 16);
	
	private final int dustColor;
	
	public SlabFallable(SoundType soundType, float hardness, float blast, int dustColor) {
		super(Properties.of(Material.SAND).sound(soundType).strength(hardness, blast).randomTicks());
		this.dustColor = dustColor;
		registerDefaultState(defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM).setValue(SlabBlock.WATERLOGGED, Boolean.valueOf(false)));
	}
	
	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, Random r) {
		if (world.isEmptyBlock(pos.below()) || isFree(world.getBlockState(pos.below())) && pos.getY() >= world.getMinBuildHeight()) {
			world.setBlock(pos, Blocks.AIR.defaultBlockState(), UPDATE_ALL);
			FallingBlockEntity fallingblockentity = FallingBlockEntity.fall(world, pos.offset(0.5, 0, 0.5), state);
			falling(fallingblockentity);
		} else if (state.getValue(SlabBlock.TYPE) == SlabType.BOTTOM) {
			BlockState st = world.getBlockState(pos.below());
			
			if (st.hasProperty(SlabBlock.TYPE) && st.getValue(SlabBlock.TYPE) == SlabType.BOTTOM) {
				if (st.getBlock() == this) {
					world.setBlockAndUpdate(pos.below(), state.setValue(SlabBlock.TYPE, SlabType.DOUBLE));
					world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				} else {
					FallingBlockEntity fallingblockentity = FallingBlockEntity.fall(world, pos.offset(0.5, 0, 0.5), state);
					falling(fallingblockentity);
				}
			}
		}
	}
	
	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return state.getValue(SlabBlock.TYPE) != SlabType.DOUBLE;
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(SlabBlock.TYPE, SlabBlock.WATERLOGGED);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext ctx) {
		SlabType slabtype = state.getValue(SlabBlock.TYPE);
		switch (slabtype) {
			case DOUBLE:
				return Shapes.block();
			case TOP:
				return TOP_AABB;
			default:
				return BOTTOM_AABB;
		}
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockPos blockpos = ctx.getClickedPos();
		BlockState blockstate = ctx.getLevel().getBlockState(blockpos);
		if (blockstate.is(this)) {
			return blockstate.setValue(SlabBlock.TYPE, SlabType.DOUBLE).setValue(SlabBlock.WATERLOGGED, Boolean.valueOf(false));
		}
		
		return defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM).setValue(SlabBlock.WATERLOGGED,
				Boolean.valueOf(ctx.getLevel().getFluidState(blockpos).getType() == Fluids.WATER));
	}
	
	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext ctx) {
		SlabType slabtype = state.getValue(SlabBlock.TYPE);
		if (slabtype != SlabType.DOUBLE && ctx.getItemInHand().getItem() == this.asItem()) {
			if (ctx.replacingClickedOnBlock()) {
				boolean flag = ctx.getClickLocation().y - ctx.getClickedPos().getY() > 0.5;
				Direction direction = ctx.getClickedFace();
				
				if (slabtype == SlabType.BOTTOM) {
					return direction == Direction.UP || flag && direction.getAxis().isHorizontal();
				}
				
				return direction == Direction.DOWN || !flag && direction.getAxis().isHorizontal();
			}
			
			return true;
		}
		
		return false;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(SlabBlock.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	
	@Override
	public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluid) {
		return state.getValue(SlabBlock.TYPE) != SlabType.DOUBLE ? SimpleWaterloggedBlock.super.placeLiquid(world, pos, state, fluid) : false;
	}
	
	@Override
	public boolean canPlaceLiquid(BlockGetter reader, BlockPos pos, BlockState state, Fluid fluid) {
		return state.getValue(SlabBlock.TYPE) != SlabType.DOUBLE ? SimpleWaterloggedBlock.super.canPlaceLiquid(reader, pos, state, fluid) : false;
	}
	
	@Override
	public BlockState updateShape(BlockState state0, Direction dir, BlockState state1, LevelAccessor world, BlockPos pos0, BlockPos pos1) {
		if (state0.getValue(SlabBlock.WATERLOGGED)) {
			world.scheduleTick(pos0, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
		
		return super.updateShape(state0, dir, state1, world, pos0, pos1);
	}
	
	@Override
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
		return type == PathComputationType.WATER ? reader.getFluidState(pos).is(FluidTags.WATER) : false;
	}
	
	@Override
	public int getDustColor(BlockState state, BlockGetter reader, BlockPos pos) {
		return dustColor;
	}
}
