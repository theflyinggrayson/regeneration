package me.sub.common.states;

import me.sub.common.init.RObjects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;

/**
 * Created by Sub
 * on 17/09/2018.
 */
public class TypeLayFade implements IRegenType {
    @Override
    public String getName() {
        return "LAYFADE";
    }

    //TODO Yeah I know, some magic numbers in this class. They were copy pasted from a System out.
    //I'll look further into them soon
    @Override
    public void onInitial(EntityPlayer player) {
        player.rotationPitch = -83.550026F;
        player.rotationYaw = -0.54983205F;
    }

    @Override
    public void onMidRegen(EntityPlayer player) {
        player.rotationPitch = -83.550026F;
        player.rotationYaw = -0.54983205F;
    }

    @Override
    public void onFinish(EntityPlayer player) {
        player.rotationPitch = -83.550026F;
        player.rotationYaw = -0.54983205F;
    }

    @Override
    public SoundEvent getSound() {
        return RObjects.Sounds.REGEN_1;
    }

    @Override
    public boolean blockMovement() {
        return true;
    }

    @Override
    public boolean isLaying() {
        return true;
    }
}