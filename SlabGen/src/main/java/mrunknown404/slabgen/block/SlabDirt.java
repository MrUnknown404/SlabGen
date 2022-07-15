package mrunknown404.slabgen.block;

import java.util.Random;

import mrunknown404.slabgen.registries.SGBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.ForgeEventFactory;

public class SlabDirt extends SlabBlock {
	public SlabDirt() {
		super(Properties.of(Material.DIRT).sound(SoundType.GRAVEL).strength(0.5f, 0.5f).randomTicks());
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray) {
		ItemStack itemstack = player.getItemInHand(hand);
		
		if (ray.getDirection() == Direction.DOWN || !itemstack.isCorrectToolForDrops(state)) { // Weird fix. Forge removed 'ToolType' and I couldn't find a better way
			return InteractionResult.PASS;
		}
		
		BlockState eventState = ForgeEventFactory.onToolUse(state, new UseOnContext(player, hand, ray), ToolActions.SHOVEL_FLATTEN, true);
		BlockState finalState = eventState != state ? eventState : SGBlocks.PATH_SLAB.get().defaultBlockState().setValue(TYPE, state.getValue(TYPE));
		
		if (finalState != null) {
			if (state.getValue(TYPE) == SlabType.BOTTOM || (state.getValue(TYPE) == SlabType.DOUBLE || state.getValue(TYPE) == SlabType.TOP) && world.isEmptyBlock(pos.above())) {
				world.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1f, 1f);
				
				if (!world.isClientSide) {
					world.setBlock(pos, finalState, 11);
					if (player != null) {
						player.getItemInHand(hand).hurtAndBreak(1, player, pl -> pl.broadcastBreakEvent(hand));
					}
				}
			}
		}
		
		return InteractionResult.sidedSuccess(world.isClientSide);
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
						world.setBlockAndUpdate(pos, SGBlocks.GRASS_SLAB.get().defaultBlockState().setValue(SlabBlock.TYPE, world.getBlockState(pos).getValue(SlabBlock.TYPE)));
					} else if (world.getBlockState(blockpos).is(Blocks.MYCELIUM) && canPropagate(defaultBlockState(), world, pos)) {
						world.setBlockAndUpdate(pos, SGBlocks.MYCELIUM_SLAB.get().defaultBlockState().setValue(SlabBlock.TYPE, world.getBlockState(pos).getValue(SlabBlock.TYPE)));
					}
				}
			}
		}
	}
}
