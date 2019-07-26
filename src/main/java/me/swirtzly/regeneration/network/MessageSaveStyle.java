package me.swirtzly.regeneration.network;

import me.swirtzly.regeneration.common.capability.CapabilityRegeneration;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Created by Sub
 * on 20/09/2018.
 */
public class MessageSaveStyle {
	
	private CompoundNBT style;
	
	public MessageSaveStyle(CompoundNBT nbtTagCompound) {
		style = nbtTagCompound;
	}
	
	public static void encode(MessageSaveStyle saveStyle, PacketBuffer buf) {
		buf.writeCompoundTag(saveStyle.style);
	}
	
	public static MessageSaveStyle decode(PacketBuffer buffer) {
		return new MessageSaveStyle(buffer.readCompoundTag());
	}
	
	public static class Handler {
		public static void handle(MessageSaveStyle message, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().getSender().getServer().runAsync(() -> {
				CapabilityRegeneration.getForPlayer(ctx.get().getSender()).ifPresent((cap) -> {
					cap.setStyle(message.style);
					cap.synchronise();
					System.out.println("WE got it!");
				});
				ctx.get().setPacketHandled(true);
			});
		}
	}
}
