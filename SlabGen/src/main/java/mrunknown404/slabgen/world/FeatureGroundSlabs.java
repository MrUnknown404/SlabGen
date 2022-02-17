package mrunknown404.slabgen.world;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import mrunknown404.slabgen.utils.SGRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class FeatureGroundSlabs extends Feature<NoFeatureConfig> {
	private static final Map<Block, SlabSpawnInfo> SPAWN_MAP = new HashMap<Block, SlabSpawnInfo>();
	private static final Set<Block> SIDE_BLOCKS = new HashSet<Block>(Arrays.asList(Blocks.STONE, Blocks.ANDESITE, Blocks.GRANITE, Blocks.DIORITE, Blocks.GRASS_BLOCK,
			Blocks.MYCELIUM, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.SAND, Blocks.SANDSTONE, Blocks.GRAVEL));
	
	static {
		add(Blocks.GRASS_BLOCK, Blocks.DIRT, SGRegistry.GRASS_SLAB.get());
		add(Blocks.MYCELIUM, Blocks.DIRT, SGRegistry.MYCELIUM_SLAB.get());
		add(Blocks.COARSE_DIRT, null, SGRegistry.COARSE_DIRT_SLAB.get());
		add(Blocks.DIRT, null, SGRegistry.DIRT_SLAB.get());
		add(Blocks.STONE, null, Blocks.STONE_SLAB);
		add(Blocks.ANDESITE, null, Blocks.ANDESITE_SLAB);
		add(Blocks.DIORITE, null, Blocks.DIORITE_SLAB);
		add(Blocks.GRANITE, null, Blocks.GRANITE_SLAB);
		add(Blocks.SAND, null, SGRegistry.SAND_SLAB.get());
		add(Blocks.GRAVEL, null, SGRegistry.GRAVEL_SLAB.get());
	}
	
	public FeatureGroundSlabs() {
		super(NoFeatureConfig.CODEC);
	}
	
	@Override
	protected void setBlock(IWorldWriter world, BlockPos pos, BlockState state) {
		world.setBlock(pos, state, 19);
	}
	
	@Override
	public boolean place(ISeedReader seed, ChunkGenerator gen, Random r, BlockPos pos, NoFeatureConfig config) {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int yy = seed.getHeightmapPos(Heightmap.Type.WORLD_SURFACE, new BlockPos(pos.getX() + x, 0, pos.getZ() + z)).getY();
				
				for (int y = gen.getSeaLevel() - 1; y < yy; y++) {
					BlockPos originPos = new BlockPos(pos.getX() + x, y, pos.getZ() + z), abovePos = originPos.above();
					SlabSpawnInfo info = SPAWN_MAP.getOrDefault(seed.getBlockState(originPos).getBlock(), null);
					BlockState aboveState = seed.getBlockState(abovePos);
					
					if (info != null && aboveState.getBlock() != Blocks.CAVE_AIR && !aboveState.getMaterial().isLiquid() && aboveState.getMaterial().isReplaceable()) {
						float count = 0;
						
						for (Direction dir : Direction.values()) {
							if (dir == Direction.UP || dir == Direction.DOWN) {
								continue;
							}
							
							Block relAboveBlock = seed.getBlockState(abovePos.relative(dir)).getBlock();
							if (isSide(relAboveBlock)) {
								count++;
							} else if (relAboveBlock instanceof SlabBlock) {
								count += 0.5f;
							}
							
							BlockState relOriginBlock = seed.getBlockState(originPos.relative(dir));
							if (relOriginBlock.getMaterial().isReplaceable() || relOriginBlock.getBlock() instanceof SlabBlock) {
								count -= 0.5f;
							}
						}
						
						if (count > 1) {
							if (info.shouldReplace()) {
								setBlock(seed, originPos, info.replaceBlock.defaultBlockState());
							}
							setBlock(seed, abovePos, info.slabBlock.defaultBlockState());
						}
					}
				}
			}
		}
		
		return true;
	}
	
	private static void add(Block originBlock, Block replaceBlock, Block slabBlock) {
		SPAWN_MAP.put(originBlock, new SlabSpawnInfo(slabBlock, replaceBlock));
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
