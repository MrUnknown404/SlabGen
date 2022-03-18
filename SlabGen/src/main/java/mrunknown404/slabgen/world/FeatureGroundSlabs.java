package mrunknown404.slabgen.world;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import mrunknown404.slabgen.registries.SGBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class FeatureGroundSlabs extends Feature<NoneFeatureConfiguration> {
	private static final Map<Block, SlabSpawnInfo> SPAWN_MAP = new HashMap<Block, SlabSpawnInfo>();
	private static final Set<Block> SIDE_BLOCKS = new HashSet<Block>();
	
	static {
		SIDE_BLOCKS.add(Blocks.SANDSTONE);
		add(Blocks.GRASS_BLOCK, Blocks.DIRT, SGBlocks.GRASS_SLAB.get());
		add(Blocks.MYCELIUM, Blocks.DIRT, SGBlocks.MYCELIUM_SLAB.get());
		add(Blocks.COARSE_DIRT, null, SGBlocks.COARSE_DIRT_SLAB.get());
		add(Blocks.DIRT, null, SGBlocks.DIRT_SLAB.get());
		add(Blocks.STONE, null, Blocks.STONE_SLAB);
		add(Blocks.ANDESITE, null, Blocks.ANDESITE_SLAB);
		add(Blocks.DIORITE, null, Blocks.DIORITE_SLAB);
		add(Blocks.GRANITE, null, Blocks.GRANITE_SLAB);
		add(Blocks.SAND, null, SGBlocks.SAND_SLAB.get());
		add(Blocks.GRAVEL, null, SGBlocks.GRAVEL_SLAB.get());
		add(Blocks.PODZOL, null, SGBlocks.PODZOL_SLAB.get());
	}
	
	public FeatureGroundSlabs() {
		super(NoneFeatureConfiguration.CODEC);
	}
	
	@Override
	protected void setBlock(LevelWriter world, BlockPos pos, BlockState state) {
		world.setBlock(pos, state, 19);
	}
	
	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
		ChunkGenerator gen = ctx.chunkGenerator();
		BlockPos pos = ctx.origin();
		WorldGenLevel level = ctx.level();
		
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int yy = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(pos.getX() + x, 0, pos.getZ() + z)).getY();
				
				for (int y = gen.getSeaLevel() - 1; y < yy; y++) {
					BlockPos originPos = new BlockPos(pos.getX() + x, y, pos.getZ() + z), abovePos = originPos.above();
					SlabSpawnInfo info = SPAWN_MAP.getOrDefault(level.getBlockState(originPos).getBlock(), null);
					BlockState aboveState = level.getBlockState(abovePos);
					
					if (info != null && aboveState.getBlock() != Blocks.CAVE_AIR && !aboveState.getMaterial().isLiquid() && aboveState.getMaterial().isReplaceable()) {
						float count = 0;
						
						for (Direction dir : Direction.values()) {
							if (dir == Direction.UP || dir == Direction.DOWN) {
								continue;
							}
							
							Block relAboveBlock = level.getBlockState(abovePos.relative(dir)).getBlock();
							if (isSide(relAboveBlock)) {
								count++;
							} else if (relAboveBlock instanceof SlabBlock) {
								count += 0.5f;
							}
							
							BlockState relOriginBlock = level.getBlockState(originPos.relative(dir));
							if (relOriginBlock.getMaterial().isReplaceable() || relOriginBlock.getBlock() instanceof SlabBlock) {
								count -= 0.5f;
							}
						}
						
						if (count > 1) {
							if (info.shouldReplace()) {
								setBlock(level, originPos, info.replaceBlock.defaultBlockState());
							}
							setBlock(level, abovePos, info.slabBlock.defaultBlockState());
						}
					}
				}
			}
		}
		
		return true;
	}
	
	private static void add(Block originBlock, Block replaceBlock, Block slabBlock) {
		SPAWN_MAP.put(originBlock, new SlabSpawnInfo(replaceBlock, slabBlock));
		SIDE_BLOCKS.add(originBlock);
	}
	
	private static boolean isSide(Block b) {
		return SIDE_BLOCKS.contains(b);
	}
	
	private static class SlabSpawnInfo {
		private final Block slabBlock, replaceBlock;
		
		private SlabSpawnInfo(@Nullable Block replaceBlock, Block slabBlock) {
			this.slabBlock = slabBlock;
			this.replaceBlock = replaceBlock;
		}
		
		private boolean shouldReplace() {
			return replaceBlock != null;
		}
	}
}
