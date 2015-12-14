package com.github.maxopoly.BottledExperience;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {
	BottledExperience plugin;

	public PlayerListener(BottledExperience plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void playerInteract(PlayerInteractEvent e) {
		Block b = e.getClickedBlock();
		if (b != null && b.getType() == Material.ENCHANTMENT_TABLE) {
			Player p = e.getPlayer();
			if (p.getItemInHand() != null
					&& p.getItemInHand().getType() == Material.GLASS_BOTTLE) {
				ItemMap playerInv = new ItemMap(p.getInventory());
				int bottles = playerInv.getAmount(new ItemStack(
						Material.GLASS_BOTTLE));
				int xpavailable = p.getTotalExperience()
						/ plugin.getXpPerBottle();
				int remove = Math.min(bottles, xpavailable);
				p.setTotalExperience(p.getTotalExperience()
						- (remove * plugin.getXpPerBottle()));
				ItemMap removeMap = new ItemMap();
				removeMap.addItemAmount(new ItemStack(Material.GLASS_BOTTLE),
						remove);
				for (ItemStack is : removeMap.getItemStackRepresentation()) {
					p.getInventory().removeItem(is);
					is.setType(Material.EXP_BOTTLE);
					p.getInventory().addItem(is);
				}
			}
		}

	}

	@EventHandler
	public void xpBottleEvent(ExpBottleEvent e) {
		e.setExperience(plugin.getXpPerBottle());
	}

}
