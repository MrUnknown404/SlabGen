package mrunknown404.slabgen.block;

import java.util.Random;

import mrunknown404.slabgen.utils.SGRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.ForgeEventFactory;

public class SlabDirt extends SlabBlock {
	public SlabDirt() {
		super(Properties.of(Material.DIRT).sound(SoundType.GRAVEL).harvestTool(ToolType.SHOVEL).strength(0.5f, 0.5f).randomTicks());
	}
	
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) {
		ItemStack itemstack = player.getItemInHand(hand);
		
		if (ray.getDirection() == Direction.DOWN || !itemstack.getToolTypes().contains(ToolType.SHOVEL)) {
			return ActionResultType.PASS;
		}
		
		BlockState eventState = ForgeEventFactory.onToolUse(state, world, pos, player, itemstack, ToolType.SHOVEL);
		BlockState finalState = eventState != state ? eventState : SGRegistry.PATH_SLAB.get().defaultBlockState().setValue(TYPE, state.getValue(TYPE));
		
		if (finalState != null) {
			if (state.getValue(TYPE) == SlabType.BOTTOM || (state.getValue(TYPE) == SlabType.DOUBLE || state.getValue(TYPE) == SlabType.TOP) && world.isEmptyBlock(pos.above())) {
				world.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
				
				if (!world.isClientSide) {
					world.setBlock(pos, finalState, 11);
					if (player != null) {
						player.getItemInHand(hand).hurtAndBreak(1, player, (pl) -> {
							pl.broadcastBreakEvent(hand);
						});
					}
				}
			}
		}
		
		return ActionResultType.sidedSuccess(world.isClientSide);
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
					} else if (world.getBlockState(blockpos).is(Blocks.MYCELIUM) && canPropagate(defaultBlockState(), world, pos)) {
						world.setBlockAndUpdate(pos,
								SGRegistry.MYCELIUM_SLAB.get().defaultBlockState().setValue(SlabBlock.TYPE, world.getBlockState(pos).getValue(SlabBlock.TYPE)));
					}
				}
			}
		}
	}
}
