package mrunknown404.slabgen.block;

import java.util.Random;

import mrunknown404.slabgen.utils.SGRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

public class SlabPath extends SlabBlock {
	private static final VoxelShape TOP = Block.box(0, 8, 0, 16, 15, 16), BOTTOM = Block.box(0, 0, 0, 16, 7, 16), FULL = Block.box(0, 0, 0, 16, 15, 16);
	
	public SlabPath() {
		super(Properties.of(Material.DIRT).sound(SoundType.GRAVEL).harvestTool(ToolType.SHOVEL).strength(0.65f, 0.65f));
	}
	
	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return !defaultBlockState().canSurvive(ctx.getLevel(), ctx.getClickedPos()) ?
				Block.pushEntitiesUp(defaultBlockState(), Blocks.DIRT.defaultBlockState(), ctx.getLevel(), ctx.getClickedPos()) :
				super.getStateForPlacement(ctx);
	}
	
	@Override
	public BlockState updateShape(BlockState state0, Direction dir, BlockState state1, IWorld world, BlockPos pos0, BlockPos pos1) {
		if (dir == Direction.UP && !state0.canSurvive(world, pos0)) {
			world.getBlockTicks().scheduleTick(pos0, this, 1); // Does this cause 2 ticks?
		}
		
		return super.updateShape(state0, dir, state1, world, pos0, pos1);
	}
	
	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random r) {
		world.setBlockAndUpdate(pos, SGRegistry.DIRT_SLAB.get().defaultBlockState().setValue(TYPE, state.getValue(TYPE)));
	}
	
	@Override
	public boolean canSurvive(BlockState state, IWorldReader reader, BlockPos pos) {
		if (state.getValue(TYPE) == SlabType.BOTTOM) {
			return true;
		}
		
		BlockState blockstate = reader.getBlockState(pos.above());
		return !blockstate.getMaterial().isSolid() || blockstate.getBlock() instanceof FenceGateBlock;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext ctx) {
		switch (state.getValue(TYPE)) {
			case TOP:
				return TOP;
			case BOTTOM:
				return BOTTOM;
			case DOUBLE:
				return FULL;
			default:
				return VoxelShapes.block();
		}
	}
	
	@Override
	public boolean isPathfindable(BlockState state, IBlockReader reader, BlockPos pos, PathType path) {
		return false;
	}
}
