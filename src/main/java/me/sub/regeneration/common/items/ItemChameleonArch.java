package me.sub.regeneration.common.items;

import me.sub.regeneration.common.capability.CapabilityRegeneration;
import me.sub.regeneration.common.capability.IRegenerationCapability;
import me.sub.regeneration.events.RObjects;
import me.sub.regeneration.utils.RegenConfig;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class ItemChameleonArch extends Item {
	
	public ItemChameleonArch() { // TODO how should combining/repairing work out?
		setCreativeTab(CreativeTabs.MISC);
		setMaxStackSize(1);
		setMaxDamage(RegenConfig.REGENERATION.regenCapacity);
	}
	
	@Override
	public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
		super.onCreated(stack, worldIn, playerIn);
		stack.setItemDamage(RegenConfig.REGENERATION.regenCapacity);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack arch = player.getHeldItem(hand);
		IRegenerationCapability handler = player.getCapability(CapabilityRegeneration.TIMELORD_CAP, null);
		
		if(handler == null) return new ActionResult<>(EnumActionResult.PASS, arch);
		
		if (arch.getItemDamage() == RegenConfig.REGENERATION.regenCapacity) {
			player.sendStatusMessage(new TextComponentString(I18n.translateToLocalFormatted("lcm-regen.messages.transfer.emptyArch")), true);
			return new ActionResult<>(EnumActionResult.FAIL, arch);
		}
		
		if (!handler.isTimelord()) {
			player.world.playSound(null, player.posX, player.posY, player.posZ, RObjects.SoundEvents.fobwatch, SoundCategory.PLAYERS, 1.0F, 1.0F);
			handler.setTimelord(true);
			doUsageDamage(arch, handler);
			player.sendStatusMessage(new TextComponentString(I18n.translateToLocalFormatted("lcm-regen.messages.becomeTimelord")), true);
		} else {
			if (!player.isSneaking()) {
				int used = doUsageDamage(arch, handler);
				if (used == 0) {
					if (handler.getRegensLeft() == RegenConfig.REGENERATION.regenCapacity) {
						player.sendStatusMessage(new TextComponentString(I18n.translateToLocalFormatted("lcm-regen.messages.transfer.fullCycle", used)), true);
					} else if (arch.getItemDamage() == RegenConfig.REGENERATION.regenCapacity)
						player.sendStatusMessage(new TextComponentString(I18n.translateToLocalFormatted("lcm-regen.messages.transfer.emptyArch", used)), true);
					return new ActionResult<>(EnumActionResult.FAIL, arch);
				}
				player.sendStatusMessage(new TextComponentString(I18n.translateToLocalFormatted("lcm-regen.messages.gainedRegenerations", used)), true); // too lazy to fix a single/plural issue here
			} else {
				if (arch.getItemDamage() == 0 && !player.isCreative()) {
					player.sendStatusMessage(new TextComponentString(I18n.translateToLocalFormatted("lcm-regen.messages.transfer.fullArch")), true);
					return new ActionResult<>(EnumActionResult.FAIL, arch);
				} else if (handler.getRegensLeft() < 1) {
					player.sendStatusMessage(new TextComponentString(I18n.translateToLocalFormatted("lcm-regen.messages.transfer.emptyCycle")), true);
					return new ActionResult<>(EnumActionResult.FAIL, arch);
				}
				arch.setItemDamage(arch.getItemDamage() - 1);
				handler.setRegensLeft(handler.getRegensLeft() - 1);
				player.sendStatusMessage(new TextComponentString(I18n.translateToLocalFormatted("lcm-regen.messages.transfer")), true);
				return new ActionResult<>(EnumActionResult.PASS, arch);
			}
		}
		return new ActionResult<>(EnumActionResult.PASS, arch);
	}
	
	private int doUsageDamage(ItemStack stack, IRegenerationCapability handler) {
		int supply = RegenConfig.REGENERATION.regenCapacity - stack.getItemDamage(), needed = RegenConfig.REGENERATION.regenCapacity - handler.getRegensLeft(), used = Math.min(supply, needed);
		if (used == 0)
			return 0;
		
		handler.setRegensLeft(handler.getRegensLeft() + used);
		handler.syncToAll();
		
		if (!handler.getPlayer().isCreative())
			stack.setItemDamage(stack.getItemDamage() + used);
		return used;
	}
	
	
}
