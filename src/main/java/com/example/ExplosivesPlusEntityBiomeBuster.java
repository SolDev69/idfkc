package com.example;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ExplosivesPlusEntityBiomeBuster extends Entity {
	public int fuse;
	public Set destroyedBlockPositions;
	public String biomeId;
	public int render;

	public ExplosivesPlusEntityBiomeBuster(World world) {
		super(world);
		this.fuse = 0;
		this.preventEntitySpawning = true;
		this.setSize(0.98F, 0.98F);
		this.yOffset = this.height / 2.0F;
		this.destroyedBlockPositions = new HashSet();
		this.render = Minecraft.getMinecraft().gameSettings.renderDistanceChunks;
	}

	public ExplosivesPlusEntityBiomeBuster(World world, double d, double d1, double d2) {
		this(world);
		this.setPosition(d + 0.5D, d1 + 0.5D, d2 + 0.5D);
		float f = (float)(Math.random() * (double)((float)Math.PI) * 2.0D);
		this.motionX = (double)(-MathHelper.sin(f * 3.141593F / 180.0F) * 0.02F);
		this.motionY = (double)0.2F;
		this.motionZ = (double)(-MathHelper.cos(f * 3.141593F / 180.0F) * 0.02F);
		this.fuse = mod_ExplosivesPlus.bbFuse;
		this.prevPosX = d;
		this.prevPosY = d1;
		this.prevPosZ = d2;
		this.biomeId = this.worldObj.getBiomeGenForCoords(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posZ)).getClass().getName();
	}

	protected void entityInit() {
	}

	protected boolean canTriggerWalking() {
		return false;
	}

	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	public void onUpdate() {
		Minecraft mc = Minecraft.getMinecraft();
		if(this.fuse-- <= 0) {
			this.setDead();
			mc.gameSettings.renderDistanceChunks = this.render;
			this.explode();
		} else {
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			this.motionY -= (double)0.04F;
			super.handleWaterMovement();
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= (double)0.98F;
			this.motionY *= (double)0.98F;
			this.motionZ *= (double)0.98F;
			if(this.onGround) {
				this.motionX *= (double)0.7F;
				this.motionZ *= (double)0.7F;
				this.motionY *= -0.5D;
			}

			this.worldObj.spawnParticle("smoke", this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
			this.worldObj.spawnParticle("explode", this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
			this.worldObj.spawnParticle("mobSpell", this.posX, this.posY + 0.5D, this.posZ, 1.0D, 0.0D, 0.0D);
		}

	}

	private void explode() {
		if(mod_ExplosivesPlus.soundsExist) {
			this.worldObj.playSoundAtEntity(this, "nuke.implosion", 10.0F, 1.0F);
		}

		boolean canProcess = true;
		int r = mod_ExplosivesPlus.bbChunkRadius;

		for(int i = -r; i < r; ++i) {
			for(int j = -r; j < r; ++j) {
				double var10005 = (double)((int)this.posX + (i - 2) * 16);
				double var10006 = (double)((int)this.posY);
				int var10007 = (int)this.posZ;
				canProcess = ExplosivesPlusGenuineThreadChunk.threadProcessor.offerActionToStack(new ExplosivesPlusGenuineChunkerClearChunk(this.worldObj, Minecraft.getMinecraft().thePlayer, var10005, var10006, (double)(var10007 + (j - 2) * 16), 0, this.biomeId));
			}
		}

		if(!canProcess) {
			Minecraft.getMinecraft().thePlayer.addChatMessage("Chunk processing queue is full at the moment.");
		}

	}

	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setByte("Fuse", (byte)this.fuse);
	}

	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		this.fuse = nbttagcompound.getByte("Fuse");
	}

	public float getShadowSize() {
		return 0.0F;
	}
}