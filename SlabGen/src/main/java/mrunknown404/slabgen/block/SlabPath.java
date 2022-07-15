package mrunknown404.slabgen.block;

import java.util.Random;

import mrunknown404.slabgen.registries.SGBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SlabPath extends SlabBlock {
	private static final VoxelShape TOP = Block.box(0, 8, 0, 16, 15, 16), BOTTOM = Block.box(0, 0, 0, 16, 7, 16), FULL = Block.box(0, 0, 0, 16, 15, 16);
	
	public SlabPath() {
		super(Properties.of(Material.DIRT).sound(SoundType.GRAVEL).strength(0.65f, 0.65f));
	}
	
	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return !defaultBlockState().canSurvive(ctx.getLevel(), ctx.getClickedPos()) ?
				Block.pushEntitiesUp(defaultBlockState(), Blocks.DIRT.defaultBlockState(), ctx.getLevel(), ctx.getClickedPos()) :
				super.getStateForPlacement(ctx);
	}
	
	@Override
	public BlockState updateShape(BlockState state0, Direction dir, BlockState state1, LevelAccessor world, BlockPos pos0, BlockPos pos1) {
		if (dir == Direction.UP && !state0.canSurvive(world, pos0)) {
			world.scheduleTick(pos0, this, 1); // Does this cause 2 ticks?
		}
		
		return super.updateShape(state0, dir, state1, world, pos0, pos1);
	}
	
	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, Random r) {
		world.setBlockAndUpdate(pos, SGBlocks.DIRT_SLAB.get().defaultBlockState().setValue(TYPE, state.getValue(TYPE)));
	}
	
	@Override
	public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos) {
		if (state.getValue(TYPE) == SlabType.BOTTOM) {
			return true;
		}
		
		BlockState blockstate = reader.getBlockState(pos.above());
		return !blockstate.getMaterial().isSolid() || blockstate.getBlock() instanceof FenceGateBlock;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext ctx) {
		return switch (state.getValue(TYPE)) {
			case TOP -> SlabPath.TOP;
			case BOTTOM -> SlabPath.BOTTOM;
			case DOUBLE -> SlabPath.FULL;
			default -> Shapes.block();
		};
	}
	
	@Override
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType path) {
		return false;
	}
}
