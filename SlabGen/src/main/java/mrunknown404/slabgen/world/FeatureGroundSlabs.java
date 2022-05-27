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
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class FeatureGroundSlabs extends Feature<NoneFeatureConfiguration> {
	private static final Map<Block, SlabSpawnInfo> SPAWN_MAP = new HashMap<Block, SlabSpawnInfo>();
	private static final Set<Block> SIDE_BLOCKS = new HashSet<Block>();
	
	private static final BlockState SNOW_LAYER;
	
	static {
		SNOW_LAYER = Blocks.SNOW.defaultBlockState().setValue(SnowLayerBlock.LAYERS, 5);
		
		addSideBlock(Blocks.SANDSTONE);
		addSideBlock(Blocks.RED_SANDSTONE);
		addSideBlock(Blocks.PACKED_ICE);
		add(Blocks.GRASS_BLOCK, Blocks.DIRT, SGBlocks.GRASS_SLAB.get());
		add(Blocks.MYCELIUM, Blocks.DIRT, SGBlocks.MYCELIUM_SLAB.get());
		add(Blocks.COARSE_DIRT, null, SGBlocks.COARSE_DIRT_SLAB.get());
		add(Blocks.DIRT, null, SGBlocks.DIRT_SLAB.get());
		add(Blocks.STONE, null, Blocks.STONE_SLAB);
		add(Blocks.ANDESITE, null, Blocks.ANDESITE_SLAB);
		add(Blocks.DIORITE, null, Blocks.DIORITE_SLAB);
		add(Blocks.GRANITE, null, Blocks.GRANITE_SLAB);
		add(Blocks.SAND, null, SGBlocks.SAND_SLAB.get());
		add(Blocks.RED_SAND, null, SGBlocks.RED_SAND_SLAB.get());
		add(Blocks.GRAVEL, null, SGBlocks.GRAVEL_SLAB.get());
		add(Blocks.PODZOL, Blocks.DIRT, SGBlocks.PODZOL_SLAB.get());
		add(Blocks.DIRT_PATH, Blocks.DIRT, SGBlocks.PATH_SLAB.get());
		add(Blocks.SNOW_BLOCK, null, SNOW_LAYER);
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
				int yy = level.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX() + x, pos.getZ() + z);
				
				for (int y = gen.getSeaLevel() - 1; y < yy; y++) {
					BlockPos originPos = new BlockPos(pos.getX() + x, y, pos.getZ() + z), abovePos = originPos.above();
					Block block = level.getBlockState(originPos).getBlock();
					SlabSpawnInfo info = SPAWN_MAP.getOrDefault(block, null);
					BlockState aboveState = level.getBlockState(abovePos);
					
					if (info != null && aboveState.getBlock() != Blocks.CAVE_AIR && !aboveState.getMaterial().isLiquid() && aboveState.getMaterial().isReplaceable()) {
						float count = 0;
						boolean isSnowy = false;
						
						for (Direction dir : Direction.values()) {
							if (dir == Direction.UP || dir == Direction.DOWN) {
								continue;
							}
							
							BlockState relAboveState = level.getBlockState(abovePos.relative(dir));
							Block relAboveBlock = relAboveState.getBlock();
							if (relAboveBlock == Blocks.SNOW) {
								isSnowy = true;
							}
							
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
								setBlock(level, originPos, info.replaceBlock);
							}
							
							setBlock(level, abovePos, isSnowy ? SNOW_LAYER : info.slabBlock);
						}
					}
				}
			}
		}
		
		return true;
	}
	
	public static void add(Block originBlock, BlockState replaceBlock, BlockState slabBlock) {
		SPAWN_MAP.put(originBlock, new SlabSpawnInfo(replaceBlock, slabBlock));
		addSideBlock(originBlock);
	}
	
	public static void add(Block originBlock, Block replaceBlock, Block slabBlock) {
		add(originBlock, replaceBlock == null ? null : replaceBlock.defaultBlockState(), slabBlock.defaultBlockState());
	}
	
	public static void addSideBlock(Block block) {
		SIDE_BLOCKS.add(block);
	}
	
	private static boolean isSide(Block b) {
		return SIDE_BLOCKS.contains(b);
	}
	
	private static class SlabSpawnInfo {
		private final BlockState slabBlock, replaceBlock;
		
		private SlabSpawnInfo(@Nullable BlockState replaceBlock, BlockState slabBlock) {
			this.slabBlock = slabBlock;
			this.replaceBlock = replaceBlock;
		}
		
		private boolean shouldReplace() {
			return replaceBlock != null;
		}
	}
}
