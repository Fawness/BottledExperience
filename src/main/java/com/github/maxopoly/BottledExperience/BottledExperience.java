package com.github.maxopoly.BottledExperience;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class BottledExperience extends JavaPlugin{
	
	private int XpPerBottle;
	public void onEnable() {
		saveDefaultConfig();
		reloadConfig();
		FileConfiguration config = getConfig();
		XpPerBottle = config.getInt("xp_per_bottle");
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
	}
	
	public int getXpPerBottle() {
		return XpPerBottle;
	}

}
