package mrunknown404.slabgen.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

public class SlabFallable extends FallingBlock implements IWaterLoggable {
	private static final VoxelShape BOTTOM_AABB = Block.box(0, 0, 0, 16, 8, 16), TOP_AABB = Block.box(0, 8, 0, 16, 16, 16);
	
	public SlabFallable(SoundType soundType, float hardness, float blast) {
		super(Properties.of(Material.SAND).sound(soundType).harvestTool(ToolType.SHOVEL).strength(hardness, blast).randomTicks());
		registerDefaultState(defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM).setValue(SlabBlock.WATERLOGGED, Boolean.valueOf(false)));
	}
	
	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random r) {
		boolean isTop = state.getValue(SlabBlock.TYPE) == SlabType.TOP;
		
		if (isTop || (world.isEmptyBlock(pos.below()) || isFree(world.getBlockState(pos.below())) && pos.getY() >= 0)) {
			FallingBlockEntity fallingblockentity = new FallingBlockEntity(world, pos.getX() + 0.5, pos.getY() + (isTop ? 0.5 : 0), pos.getZ() + 0.5,
					isTop ? state.setValue(SlabBlock.TYPE, SlabType.BOTTOM) : state);
			falling(fallingblockentity);
			world.addFreshEntity(fallingblockentity);
		} else if (state.getValue(SlabBlock.TYPE) == SlabType.BOTTOM) {
			BlockState st = world.getBlockState(pos.below());
			
			if (st.hasProperty(SlabBlock.TYPE) && st.getValue(SlabBlock.TYPE) == SlabType.BOTTOM) {
				if (st.getBlock() == this) {
					world.setBlockAndUpdate(pos.below(), state.setValue(SlabBlock.TYPE, SlabType.DOUBLE));
					world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				} else {
					FallingBlockEntity fallingblockentity = new FallingBlockEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, state);
					falling(fallingblockentity);
					world.addFreshEntity(fallingblockentity);
				}
			}
		}
	}
	
	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return state.getValue(SlabBlock.TYPE) != SlabType.DOUBLE;
	}
	
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(SlabBlock.TYPE, SlabBlock.WATERLOGGED);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext ctx) {
		SlabType slabtype = state.getValue(SlabBlock.TYPE);
		switch (slabtype) {
			case DOUBLE:
				return VoxelShapes.block();
			case TOP:
				return TOP_AABB;
			default:
				return BOTTOM_AABB;
		}
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		BlockPos blockpos = ctx.getClickedPos();
		BlockState blockstate = ctx.getLevel().getBlockState(blockpos);
		if (blockstate.is(this)) {
			return blockstate.setValue(SlabBlock.TYPE, SlabType.DOUBLE).setValue(SlabBlock.WATERLOGGED, Boolean.valueOf(false));
		}
		
		BlockState blockstate1 = defaultBlockState().setValue(SlabBlock.TYPE, SlabType.BOTTOM).setValue(SlabBlock.WATERLOGGED,
				Boolean.valueOf(ctx.getLevel().getFluidState(blockpos).getType() == Fluids.WATER));
		Direction direction = ctx.getClickedFace();
		return direction != Direction.DOWN && (direction == Direction.UP || !(ctx.getClickLocation().y - blockpos.getY() > 0.5)) ? blockstate1 :
				blockstate1.setValue(SlabBlock.TYPE, SlabType.TOP);
	}
	
	@Override
	public boolean canBeReplaced(BlockState state, BlockItemUseContext ctx) {
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
	public boolean placeLiquid(IWorld world, BlockPos pos, BlockState state, FluidState fluid) {
		return state.getValue(SlabBlock.TYPE) != SlabType.DOUBLE ? IWaterLoggable.super.placeLiquid(world, pos, state, fluid) : false;
	}
	
	@Override
	public boolean canPlaceLiquid(IBlockReader reader, BlockPos pos, BlockState state, Fluid fluid) {
		return state.getValue(SlabBlock.TYPE) != SlabType.DOUBLE ? IWaterLoggable.super.canPlaceLiquid(reader, pos, state, fluid) : false;
	}
	
	@Override
	public BlockState updateShape(BlockState state0, Direction dir, BlockState state1, IWorld world, BlockPos pos0, BlockPos pos1) {
		if (state0.getValue(SlabBlock.WATERLOGGED)) {
			world.getLiquidTicks().scheduleTick(pos0, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
		
		return super.updateShape(state0, dir, state1, world, pos0, pos1);
	}
	
	@Override
	public boolean isPathfindable(BlockState state, IBlockReader reader, BlockPos pos, PathType type) {
		return type == PathType.WATER ? reader.getFluidState(pos).is(FluidTags.WATER) : false;
	}
	
	@Override
	public int getDustColor(BlockState state, IBlockReader reader, BlockPos pos) {
		return -8356741;
	}
}
