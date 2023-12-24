package mapped;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public final class ExplosivesPlusGenuineChunkerClearChunk extends ExplosivesPlusGenuineChunker {
	World worldObj;

	public ExplosivesPlusGenuineChunkerClearChunk(World world, EntityPlayer player, double posX, double posY, double posZ, int kind, String ID) {
		super(world, player, posX, posY, posZ, kind, ID);
	}

	protected final Chunk processChunk(Chunk inputChunk) {
		this.world.editingBlocks = true;
		int i;
		int j;
		int k;
		int x;
		int y;
		if(this.type == 0) {
			for(i = 0; i < 16; ++i) {
				for(j = 0; j < 16; ++j) {
					k = inputChunk.getBlockID(i, 0, j);
					if(k != 0) {
						byte[] r = inputChunk.getBiomeArray();
						if(this.Id == this.world.getBiomeGenForCoords(inputChunk.xPosition * 16 + i, inputChunk.zPosition * 16 + j).getClass().getName()) {
							for(x = 0; x < 128; ++x) {
								y = inputChunk.getBlockID(i, x, j);
								if(y != 0 && (y != 7 || mod_ExplosivesPlus.EnhancedExplosions)) {
									inputChunk.setBlockID(i, x, j, 0);
								}
							}
						}
					}
				}
			}
		}

		if(this.type == 1) {
			i = MathHelper.floor_double(this.posX);
			j = MathHelper.floor_double(this.posY);
			k = MathHelper.floor_double(this.posZ);
			int var12 = mod_ExplosivesPlus.missileRadius2;

			for(x = -var12; x < var12; ++x) {
				for(y = -var12; y < var12; ++y) {
					for(int z = -var12; z < var12; ++z) {
						double dist = (double)MathHelper.sqrt_double((double)(x * x + y * y + z * z));
						if(dist <= (double)var12) {
							int i1 = inputChunk.worldObj.getBlockId(i + x, j + y, k + z);
							if(i1 != 0 && (i1 != Block.bedrock.blockID || mod_ExplosivesPlus.EnhancedExplosions)) {
								if(dist > (double)(var12 - 1) && dist < (double)var12) {
									if(inputChunk.worldObj.rand.nextInt(3) > 0) {
										inputChunk.worldObj.setBlockWithNotify(i + x, j + y, k + z, 0);
									}
								} else {
									inputChunk.worldObj.setBlockWithNotify(i + x, j + y, k + z, 0);
								}
							}
						}
					}
				}
			}
		}

		inputChunk.generateHeightMap();
		inputChunk.generateSkylightMap();
		this.world.editingBlocks = false;
		return inputChunk;
	}
}