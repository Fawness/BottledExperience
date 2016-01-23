package com.github.maxopoly.BottledExperience;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import vg.civcraft.mc.civmodcore.itemHandling.ItemMap;

public class PlayerListener implements Listener {
	BottledExperience plugin;

	public PlayerListener(BottledExperience plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void playerInteract(PlayerInteractEvent e) {
		Block b = e.getClickedBlock();
		if (e.getAction() == Action.LEFT_CLICK_BLOCK && b != null && b.getType() == Material.ENCHANTMENT_TABLE) {
			Player p = e.getPlayer();
			if (p.getItemInHand() != null && p.getItemInHand().getType() == Material.GLASS_BOTTLE &&
					p.getTotalExperience() >= plugin.getXpPerBottle() ) {
				ItemMap playerInv = new ItemMap(p.getInventory());
				int bottles = playerInv.getAmount(new ItemStack( Material.GLASS_BOTTLE));
				int xpavailable = p.getTotalExperience() / plugin.getXpPerBottle();
				int remove = Math.min(bottles, xpavailable);
				log(p.getName() + "interacted with enchanting table, inventory has " + bottles + " bottles, " 
						+ p.getTotalExperience() + " total experience, enough to fill " + remove + " bottles");
				if (remove > 0) {
					int endXP = p.getTotalExperience() - (remove * plugin.getXpPerBottle());
					p.setLevel(0);
					p.setTotalExperience(0);
					log("Set xp for " + p.getName() + " to " + endXP);
					p.giveExp(endXP);
					ItemMap removeMap = new ItemMap();
					removeMap.addItemAmount( new ItemStack(Material.GLASS_BOTTLE), remove);
					for (ItemStack is : removeMap.getItemStackRepresentation()) {
						p.getInventory().removeItem(is);
						is.setType(Material.EXP_BOTTLE);
						p.getInventory().addItem(is);
						log("Turned " + is.getAmount() + " bottles into xp bottles for " + p.getName());
					}
				}
			}
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void xpBottleEvent(ExpBottleEvent e) {	
		e.setExperience(plugin.getXpPerBottle());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void xpBottleEventMonitor(ExpBottleEvent e) {
		if (e.getExperience() != plugin.getXpPerBottle()) {
			log("Xp control lost: " + e.getExperience());
		}
	}

	public void log(String msg) {
		BottledExperience.getPlugin().info(msg);
	}

}
