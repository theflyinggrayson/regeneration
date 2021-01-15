package me.swirtzly.regen.common.item;

import me.swirtzly.regen.Regeneration;
import me.swirtzly.regen.common.objects.RItems;
import me.swirtzly.regen.common.objects.RSounds;
import me.swirtzly.regen.common.regen.IRegen;
import me.swirtzly.regen.common.regen.RegenCap;
import me.swirtzly.regen.common.regen.state.RegenStates;
import me.swirtzly.regen.config.RegenConfig;
import me.swirtzly.regen.util.ClientUtil;
import me.swirtzly.regen.util.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;


/**
 * Created by Sub on 16/09/2018.
 */
public class FobWatchItem extends Item {

    public FobWatchItem() {
        super(new Item.Properties().setNoRepair().group(RItems.MAIN).maxStackSize(1));
    }

    public static CompoundNBT getStackTag(ItemStack stack) {
        CompoundNBT stackTag = stack.getOrCreateTag();
        if (!stackTag.contains("is_open") || !stackTag.contains("is_gold")) {
            stackTag.putBoolean("is_open", false);
            stackTag.putBoolean("is_gold", random.nextBoolean());
        }
        return stackTag;
    }

    public static boolean getEngrave(ItemStack stack) {
        return getStackTag(stack).getBoolean("is_gold");
    }

    public static void setEngrave(ItemStack stack, boolean isGold) {
        getStackTag(stack).putBoolean("is_gold", isGold);
    }

    public static boolean getOpen(ItemStack stack) {
        return getStackTag(stack).getBoolean("is_open");
    }

    public static void setOpen(ItemStack stack, boolean isOpen) {
        getStackTag(stack).putBoolean("is_open", isOpen);
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
        super.onCreated(stack, worldIn, playerIn);
        setDamage(stack, 0);
        setOpen(stack, false);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (stack.getItem() instanceof FobWatchItem) {
            if (getOpen(stack)) {
                if (entityIn.ticksExisted % 600 == 0) {
                    setOpen(stack, false);
                }
            }
        }
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {

        ItemStack stack = player.getHeldItem(hand);
        IRegen cap = RegenCap.get(player).orElseGet(null);

        if (!player.isSneaking()) { // transferring watch->player
            if (getDamage(stack) == RegenConfig.COMMON.regenCapacity.get())
                return msgUsageFailed(player, "regen.messages.transfer.empty_watch", stack);
            else if (cap.getRegens() == RegenConfig.COMMON.regenCapacity.get())
                return msgUsageFailed(player, "regen.messages.transfer.max_regens", stack);

            int supply = RegenConfig.COMMON.regenCapacity.get() - getDamage(stack), needed = RegenConfig.COMMON.regenCapacity.get() - cap.getRegens(), used = Math.min(supply, needed);

            if (cap.canRegenerate()) {
                setOpen(stack, true);
                PlayerUtil.sendMessage(player, new TranslationTextComponent("regen.messages.gained_regens", used), true);
            } else {
                if (!world.isRemote) {
                    setOpen(stack, true);
                    PlayerUtil.sendMessage(player, new TranslationTextComponent("regen.messages.now_timelord"), true);
                }
            }

            if (used < 0)
                Regeneration.LOG.warn(player.getName().getString() + ": Fob watch used <0 regens (supply: " + supply + ", needed:" + needed + ", used:" + used + ", capacity:" + RegenConfig.COMMON.regenCapacity.get() + ", damage:" + getDamage(stack) + ", regens:" + cap.getRegens());

            setDamage(stack, stack.getDamage() + used);

            if (world.isRemote) {
                ClientUtil.playPositionedSoundRecord(RSounds.FOB_WATCH.get(), 1.0F, 2.0F);
            } else {
                setOpen(stack, true);
                cap.addRegens(used);
            }

        } else { // transferring player->watch
            if (!cap.canRegenerate()) return msgUsageFailed(player, "regen.messages.transfer.no_regens", stack);

            if (cap.getCurrentState() != RegenStates.ALIVE) {
                return msgUsageFailed(player, "regen.messages.not_alive", stack);
            }

            if (getDamage(stack) == 0)
                return msgUsageFailed(player, "regen.messages.transfer.full_watch", stack);

            setDamage(stack, getDamage(stack) - 1);
            PlayerUtil.sendMessage(player, "regen.messages.transfer.success", true);

            if (world.isRemote) {
                ClientUtil.playPositionedSoundRecord(SoundEvents.BLOCK_FIRE_EXTINGUISH, 5.0F, 2.0F);
            } else {
                setOpen(stack, true);
                cap.extractRegens(1);
            }

        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }


    private ActionResult<ItemStack> msgUsageFailed(PlayerEntity player, String message, ItemStack stack) {
        PlayerUtil.sendMessage(player, message, true);
        return ActionResult.resultFail(stack);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isDamageable() {
        return super.isDamageable();
    }

    @Override
    public boolean shouldSyncTag() {
        return true;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return RegenConfig.COMMON.regenCapacity.get();
    }
}