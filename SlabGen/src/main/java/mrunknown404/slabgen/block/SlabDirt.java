package mrunknown404.slabgen.block;

import java.util.Random;

import mrunknown404.slabgen.utils.SGRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

public class SlabDirt extends SlabBlock {
	public SlabDirt() {
		super(Properties.of(Material.DIRT).sound(SoundType.GRAVEL).harvestTool(ToolType.SHOVEL).strength(0.5f, 0.5f).randomTicks());
	}
	
	private static boolean canBeGrass(BlockState state, IWorldReader world, BlockPos pos) {
		BlockPos blockpos = pos.above();
		BlockState blockstate = world.getBlockState(blockpos);
		
		if (blockstate.is(Blocks.SNOW) && blockstate.getValue(SnowBlock.LAYERS) == 1) {
			return true;
		} else if (blockstate.getFluidState().getAmount() == 8) {
			return false;
		} else {
			return getLightBlockInto(world, state, pos, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(world, blockpos)) < world.getMaxLightLevel();
		}
	}
	
	private static int getLightBlockInto(IBlockReader world, BlockState state0, BlockPos pos0, BlockState state1, BlockPos pos1, Direction dir, int value) {
		boolean flag = state0.canOcclude() && state0.useShapeForLightOcclusion();
		boolean flag1 = state1.canOcclude() && state1.useShapeForLightOcclusion();
		
		if (!flag && !flag1) {
			return value;
		} else if (flag && !flag1) {
			return state0.getValue(SlabBlock.TYPE) == SlabType.BOTTOM ? 0 : value;
		}
		
		VoxelShape voxelshape = flag ? state0.getOcclusionShape(world, pos0) : VoxelShapes.empty();
		VoxelShape voxelshape1 = flag1 ? state1.getOcclusionShape(world, pos1) : VoxelShapes.empty();
		return VoxelShapes.mergedFaceOccludes(voxelshape, voxelshape1, dir) ? 16 : value;
	}
	
	private static boolean canPropagate(BlockState state, IWorldReader world, BlockPos pos) {
		return canBeGrass(state, world, pos) && !world.getFluidState(pos.above()).is(FluidTags.WATER);
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random r) {
		if (!canBeGrass(state, world, pos)) {
			if (!world.isAreaLoaded(pos, 3)) {
				return;
			}
			
			world.setBlockAndUpdate(pos, SGRegistry.DIRT_SLAB.get().defaultBlockState().setValue(SlabBlock.TYPE, world.getBlockState(pos).getValue(SlabBlock.TYPE)));
		} else {
			if (world.getMaxLocalRawBrightness(pos.above()) >= 9) {
				for (int i = 0; i < 4; i++) {
					BlockPos blockpos = pos.offset(r.nextInt(3) - 1, r.nextInt(5) - 3, r.nextInt(3) - 1);
					if (world.getBlockState(blockpos).is(Blocks.GRASS_BLOCK) && canPropagate(defaultBlockState(), world, pos)) {
						world.setBlockAndUpdate(pos, SGRegistry.GRASS_SLAB.get().defaultBlockState().setValue(SlabBlock.TYPE, world.getBlockState(pos).getValue(SlabBlock.TYPE)));
					}
				}
			}
		}
	}
}
