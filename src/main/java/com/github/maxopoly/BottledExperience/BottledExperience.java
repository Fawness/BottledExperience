package com.github.maxopoly.BottledExperience;

import org.bukkit.configuration.file.FileConfiguration;

import vg.civcraft.mc.civmodcore.ACivMod;

public class BottledExperience extends ACivMod {
	private static BottledExperience plugin;
	private int XpPerBottle;
	public void onEnable() {
		super.onEnable();
		plugin = this;
		saveDefaultConfig();
		reloadConfig();
		FileConfiguration config = getConfig();
		XpPerBottle = config.getInt("xp_per_bottle");
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
	}
	
	public int getXpPerBottle() {
		return XpPerBottle;
	}
	
	public static BottledExperience getPlugin() {
		return plugin;
	}
	
	protected String getPluginName() {
	    return "BottledExperience";
	}

}
