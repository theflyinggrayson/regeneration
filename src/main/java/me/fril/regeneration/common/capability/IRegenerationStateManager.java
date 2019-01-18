package me.fril.regeneration.common.capability;

import me.fril.regeneration.util.RegenState.Transition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.apache.commons.lang3.tuple.Pair;

public interface IRegenerationStateManager extends INBTSerializable<NBTTagCompound> {
	
	//Event proxy methods
	boolean onKilled();
	
	void onPunchEntity(EntityLivingBase entity);
	
	void onPunchBlock(PlayerInteractEvent.LeftClickBlock e);
	
	//Proxy methods for timing related stuff
	double getStateProgress();
	
	//Debug things
	@Deprecated
	Pair<Transition, Long> getScheduledEvent();
	
	@Deprecated
	void fastForward();
	
	
}