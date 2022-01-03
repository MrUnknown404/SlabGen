package mrunknown404.slabgen.world;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import mrunknown404.slabgen.utils.SGRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class FeatureGroundSlabs extends Feature<NoFeatureConfig> {
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
				
				yLoop:
				for (int y = gen.getSeaLevel() - 1; y < 255; y++) {
					BlockPos newPos = new BlockPos(pos.getX() + x, y, pos.getZ() + z), abovePos = newPos.above();
					
					if (y > yy) {
						break yLoop;
					}
					
					if (isDirt(seed.getBlockState(newPos).getBlock()) && seed.getBlockState(abovePos).getMaterial().isReplaceable()) {
						float count = 0;
						
						Block an = seed.getBlockState(abovePos.north()).getBlock();
						Block ae = seed.getBlockState(abovePos.east()).getBlock();
						Block as = seed.getBlockState(abovePos.south()).getBlock();
						Block aw = seed.getBlockState(abovePos.west()).getBlock();
						BlockState dn = seed.getBlockState(newPos.north());
						BlockState de = seed.getBlockState(newPos.east());
						BlockState ds = seed.getBlockState(newPos.south());
						BlockState dw = seed.getBlockState(newPos.west());
						
						if (isDirt(an) || an == Blocks.STONE) {
							count++;
						} else if (an == SGRegistry.GRASS_SLAB.get()) {
							count += 0.5f;
						}
						if (isDirt(ae) || ae == Blocks.STONE) {
							count++;
						} else if (ae == SGRegistry.GRASS_SLAB.get()) {
							count += 0.5f;
						}
						if (isDirt(as) || as == Blocks.STONE) {
							count++;
						} else if (as == SGRegistry.GRASS_SLAB.get()) {
							count += 0.5f;
						}
						if (isDirt(aw) || aw == Blocks.STONE) {
							count++;
						} else if (aw == SGRegistry.GRASS_SLAB.get()) {
							count += 0.5f;
						}
						
						if (dn.getMaterial().isReplaceable() || dn.getBlock() == SGRegistry.GRASS_SLAB.get()) {
							count -= 0.5f;
						}
						if (de.getMaterial().isReplaceable() || de.getBlock() == SGRegistry.GRASS_SLAB.get()) {
							count -= 0.5f;
						}
						if (ds.getMaterial().isReplaceable() || ds.getBlock() == SGRegistry.GRASS_SLAB.get()) {
							count -= 0.5f;
						}
						if (dw.getMaterial().isReplaceable() || dw.getBlock() == SGRegistry.GRASS_SLAB.get()) {
							count -= 0.5f;
						}
						
						if (count > 1) {
							setBlock(seed, newPos, Blocks.DIRT.defaultBlockState());
							setBlock(seed, abovePos, SGRegistry.GRASS_SLAB.get().defaultBlockState());
						}
					}
				}
			}
		}
		
		return true;
	}
	
	private static final Set<Block> DIRTS = new HashSet<Block>(Arrays.asList(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.COARSE_DIRT));
	
	public static boolean isDirt(Block block) {
		return DIRTS.contains(block);
	}
}
