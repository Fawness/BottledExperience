package com.github.maxopoly.BottledExperience;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.ChatColor;
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
			
			int totalExperience = computeCurrentXP(p);
			
			if (p.getInventory().getItemInMainHand() != null
					&& p.getInventory().getItemInMainHand().getType() == Material.GLASS_BOTTLE
					&& totalExperience >= plugin.getXpPerBottle()
					) {
				createXPBottles(p, totalExperience);
			}
		}
	}
	
	private void createXPBottles(Player p, int totalExperience) {
		ItemMap playerInv = new ItemMap(p.getInventory());
		int bottles = playerInv.getAmount(new ItemStack( Material.GLASS_BOTTLE));
		int xpavailable = totalExperience / plugin.getXpPerBottle();
		int remove = Math.min(bottles, xpavailable);
		
		log(p.getName() + " interacted with enchanting table, inventory has " + bottles + " bottles, " 
				+ totalExperience + " total experience, enough to fill " + remove + " bottles");
		
		if (remove == 0) {
			return;
		}
		
		boolean noSpace = false;
		int xpBottleCount = 0;
		ItemMap removeMap = new ItemMap();
		
		removeMap.addItemAmount( new ItemStack(Material.GLASS_BOTTLE), remove);
		
		for (ItemStack is : removeMap.getItemStackRepresentation()) {
			int initialAmount = is.getAmount();
			
			p.getInventory().removeItem(is);
			is.setType(Material.EXP_BOTTLE);
			
			HashMap<Integer, ItemStack> result = p.getInventory().addItem(is);
			
			if(result != null && result.size() > 0) {
				is.setType(Material.GLASS_BOTTLE);
				p.getInventory().addItem(is);
				
				noSpace = true;
				
				log("Cannot store " + is.getAmount() + " xp bottles in inventory for " + p.getName());
				
				break;
			} else {
				xpBottleCount += initialAmount; 
				
				log("Turned " + initialAmount + " bottles into xp bottles for " + p.getName());
			}
		}

		if(xpBottleCount > 0) {
			int endXP = totalExperience - xpBottleCount * plugin.getXpPerBottle();

			log("Set xp for " + p.getName() + " to " + endXP);

			p.setLevel(0);
			p.setExp(0f);
			p.giveExp(endXP);
			
			p.sendMessage(ChatColor.GREEN + "Created " + xpBottleCount + " XP bottles.");
		}
		
		if(noSpace) {
			p.sendMessage(ChatColor.RED + "Not enough space in inventory for all XP bottles.");
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

	/**
	 * This is 1.8 mechanics only.
	 */
	private int computeCurrentXP(Player p) {
		float cLevel = (float) p.getLevel();
		float progress = p.getExp();
		float a = 1f, b = 6f, c = 0f, x = 2f, y = 7f;
		if (cLevel > 16 && cLevel <= 31) {
			a = 2.5f; b = -40.5f; c = 360f; x = 5f; y = -38f;
		} else if (cLevel >= 32) {
			a = 4.5f; b = -162.5f; c = 2220f; x = 9f; y = -158f;
		}
		return (int) Math.floor(a * cLevel * cLevel + b * cLevel + c + progress * (x * cLevel + y));
	}

	public void log(String msg) {
		BottledExperience.getPlugin().info(msg);
	}

}
