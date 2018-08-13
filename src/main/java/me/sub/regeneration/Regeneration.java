package me.sub.regeneration;

import me.sub.regeneration.common.capabilities.timelord.capability.CapabilityRegeneration;
import me.sub.regeneration.common.capabilities.timelord.capability.IRegenerationCapability;
import me.sub.regeneration.networking.RNetwork;
import me.sub.regeneration.proxy.CommonProxy;
import me.sub.regeneration.utils.DebugCommand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Regeneration.MODID, name = Regeneration.NAME, version = Regeneration.VERSION, dependencies = "required:forge@[14.23.1.2574,)", acceptedMinecraftVersions = "1.12.2", updateJSON = Regeneration.UPDATE_JSON)
@EventBusSubscriber
public class Regeneration {

	@SidedProxy(serverSide = "me.sub.regeneration.proxy.CommonProxy", clientSide = "me.sub.regeneration.proxy.ClientProxy")
	public static CommonProxy proxy;

	public static final String MODID = "lcm-regen";
	public static final String NAME = "Regeneration";
	public static final String VERSION = "a4";
	public static final ResourceLocation ICONS = new ResourceLocation(MODID, "textures/gui/ability_icons.png");
	public static final String UPDATE_JSON = "https://github.com/SandedShoes/Regeneration/blob/master-1.12.2/update.json";
	
	@Mod.Instance(MODID)
	public static Regeneration INSTANCE;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		RNetwork.init();
		MinecraftForge.EVENT_BUS.register(proxy);
		CapabilityManager.INSTANCE.register(IRegenerationCapability.class, new CapabilityRegeneration.Storage(), CapabilityRegeneration::new);
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

	@EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new DebugCommand());
	}

}
