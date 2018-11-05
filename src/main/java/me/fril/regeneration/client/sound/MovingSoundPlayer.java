package me.fril.regeneration.client.sound;

import me.fril.regeneration.handlers.RegenObjects;
import me.fril.regeneration.common.capability.CapabilityRegeneration;
import me.fril.regeneration.common.capability.IRegeneration;
import me.fril.regeneration.util.RegenState;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;

/**
 * Created by Sub
 * on 20/09/2018.
 */
public class MovingSoundPlayer extends MovingSound {
	
	private final EntityPlayer player;
	private float distance = 0.0F;
	private SoundEvent soundCheck;
	
	public MovingSoundPlayer(EntityPlayer playerIn, SoundEvent soundIn, SoundCategory categoryIn) {
		super(soundIn, categoryIn);
		
		player = playerIn;
		repeat = true;
		repeatDelay = 0;
		soundCheck = soundIn;
	}
	
	@Override
	public void update() {
		if (player.isDead) {
			donePlaying = true;
			return;
		}
		
		xPosF = (float) player.posX;
		yPosF = (float) player.posY;
		zPosF = (float) player.posZ;
		
		distance = MathHelper.clamp(distance + 0.0025F, 0.0F, 1.0F);
		volume = 1.0F;
		
		
		IRegeneration cap = CapabilityRegeneration.getForPlayer(player);
		ResourceLocation sound = soundCheck.getSoundName();
		boolean stopCondition = false;
		
		//NOTE shouldn't heartbeat be a player-only sound?
		//I wish I could use a switch here...
		if (sound.equals(RegenObjects.Sounds.HEART_BEAT.getSoundName())) { //FIXME heartbeat is never played
			stopCondition = cap.getState().isGraceful();
		} else if (sound.equals(RegenObjects.Sounds.HAND_GLOW.getSoundName())) {
			stopCondition = cap.getState() != RegenState.GRACE_GLOWING;
			volume = 0.3F;
		} else if (sound.equals(RegenObjects.Sounds.CRITICAL_STAGE.getSoundName())) {
			stopCondition = cap.getState() != RegenState.GRACE_CRIT;
		} else if (sound.equals(RegenObjects.Sounds.REGENERATION.getSoundName())) {
			stopCondition = cap.getState() != RegenState.REGENERATING;
		}
		
		if (stopCondition) donePlaying = true;
	}
}
