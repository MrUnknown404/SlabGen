package mrunknown404.slabgen.block;

import java.util.Random;

import mrunknown404.slabgen.registries.SGBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SlabDirt extends SlabBlock {
	public SlabDirt() {
		super(Properties.of(Material.DIRT).sound(SoundType.GRAVEL).strength(0.5f, 0.5f).randomTicks());
	}
	
	private static boolean canBeGrass(BlockState state, LevelReader world, BlockPos pos) {
		BlockPos blockpos = pos.above();
		BlockState blockstate = world.getBlockState(blockpos);
		
		if (blockstate.is(Blocks.SNOW) && blockstate.getValue(SnowLayerBlock.LAYERS) == 1) {
			return true;
		} else if (blockstate.getFluidState().getAmount() == 8) {
			return false;
		} else {
			return getLightBlockInto(world, state, pos, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(world, blockpos)) < world.getMaxLightLevel();
		}
	}
	
	private static int getLightBlockInto(BlockGetter world, BlockState state0, BlockPos pos0, BlockState state1, BlockPos pos1, Direction dir, int value) {
		boolean flag = state0.canOcclude() && state0.useShapeForLightOcclusion();
		boolean flag1 = state1.canOcclude() && state1.useShapeForLightOcclusion();
		
		if (!flag && !flag1) {
			return value;
		} else if (flag && !flag1) {
			return state0.getValue(SlabBlock.TYPE) == SlabType.BOTTOM ? 0 : value;
		}
		
		VoxelShape voxelshape = flag ? state0.getOcclusionShape(world, pos0) : Shapes.empty();
		VoxelShape voxelshape1 = flag1 ? state1.getOcclusionShape(world, pos1) : Shapes.empty();
		return Shapes.mergedFaceOccludes(voxelshape, voxelshape1, dir) ? 16 : value;
	}
	
	private static boolean canPropagate(BlockState state, LevelReader world, BlockPos pos) {
		return canBeGrass(state, world, pos) && !world.getFluidState(pos.above()).is(FluidTags.WATER);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random r) {
		if (!canBeGrass(state, world, pos)) {
			if (!world.isAreaLoaded(pos, 1)) {
				return;
			}
			
			world.setBlockAndUpdate(pos, SGBlocks.DIRT_SLAB.get().defaultBlockState().setValue(SlabBlock.TYPE, world.getBlockState(pos).getValue(SlabBlock.TYPE)));
		} else {
			if (!world.isAreaLoaded(pos, 3)) {
				return;
			}
			
			if (world.getMaxLocalRawBrightness(pos.above()) >= 9) {
				for (int i = 0; i < 4; i++) {
					BlockPos blockpos = pos.offset(r.nextInt(3) - 1, r.nextInt(5) - 3, r.nextInt(3) - 1);
					if (world.getBlockState(blockpos).is(Blocks.GRASS_BLOCK) && canPropagate(defaultBlockState(), world, pos)) {
						world.setBlockAndUpdate(pos,
								SGBlocks.GRASS_SLAB.get().defaultBlockState().setValue(SlabBlock.TYPE, world.getBlockState(pos).getValue(SlabBlock.TYPE)));
					} else if (world.getBlockState(blockpos).is(Blocks.MYCELIUM) && canPropagate(defaultBlockState(), world, pos)) {
						world.setBlockAndUpdate(pos,
								SGBlocks.MYCELIUM_SLAB.get().defaultBlockState().setValue(SlabBlock.TYPE, world.getBlockState(pos).getValue(SlabBlock.TYPE)));
					}
				}
			}
		}
	}
}
