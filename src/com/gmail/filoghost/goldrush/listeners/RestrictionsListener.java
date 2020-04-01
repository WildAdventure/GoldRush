/*
 * Copyright (c) 2020, Wild Adventure
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 4. Redistribution of this software in source or binary forms shall be free
 *    of all charges or fees to the recipient of this software.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gmail.filoghost.goldrush.listeners;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.filoghost.goldrush.GoldRush;
import com.gmail.filoghost.goldrush.constants.Items;
import com.gmail.filoghost.goldrush.constants.Permissions;
import com.gmail.filoghost.goldrush.utils.Debug;

public class RestrictionsListener implements Listener {

	/*
	 *   #################################
	 *   #            PLAYERS            #
	 *   #################################
	 */
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void playerDeath(PlayerDeathEvent event) {
		if (GoldRush.isPlaying(event.getEntity())) {
			// Giocatore, droppa alcuni oggetti
			for (Iterator<ItemStack> iter = event.getDrops().iterator(); iter.hasNext();) {
				if (!Items.ALLOWED_DROPS.contains(iter.next().getType())) {
					iter.remove();
				}
			}
		} else {
			// Non giocante
			event.getDrops().clear();
		}
		
		event.setDroppedExp(event.getEntity().getTotalExperience() / 2);
		event.setKeepLevel(false);
		event.setDeathMessage(null);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void teleport(PlayerTeleportEvent event) {
		if (event.getFrom().getWorld() == event.getTo().getWorld()) return;
		
		// E' in un altro mondo
		if (!event.getPlayer().hasPermission(Permissions.MODIFY)) {
			Debug.ln("A player tried to access another world: " + event.getTo().getWorld().getName());
			event.setCancelled(true);
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerInteract(PlayerInteractEvent event) {
		
		if (!event.getPlayer().hasPermission(Permissions.MODIFY)) {
			
			if (event.hasBlock()) {
				if (GoldRush.isPlaying(event.getPlayer()) && event.getClickedBlock().getType() == Material.WORKBENCH) {
					// Ok, può interagire
					event.setUseItemInHand(Result.ALLOW);
				} else {
					event.setUseInteractedBlock(Result.DENY);
				}
			}
			
			if (event.hasItem()) {
				if (event.getItem().getType() == Material.EXP_BOTTLE || event.getItem().getType() == Material.BOW) {
					// Si può usare
					event.setUseItemInHand(Result.ALLOW);
				} else {
					event.setUseItemInHand(Result.DENY);
				}
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerInteractEntity(PlayerInteractEntityEvent event) {
		if (event.getRightClicked().getType() == EntityType.ITEM_FRAME && !event.getPlayer().hasPermission(Permissions.MODIFY)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void food(FoodLevelChangeEvent event) {
		HumanEntity human = event.getEntity();
		if (human instanceof Player) {
			event.setCancelled(true);
			((Player) human).setSaturation(20F);
		}
	}
	
	/*
	 *   #################################
	 *   #             ITEMS             #
	 *   #################################
	 */
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void itemDrop(PlayerDropItemEvent event) {
		if (!GoldRush.isPlaying(event.getPlayer())) {
			event.setCancelled(true);
			return;
		}
		
		if (!Items.ALLOWED_DROPS.contains(event.getItemDrop().getItemStack().getType())) {
			event.setCancelled(true);
			event.getPlayer().updateInventory();
			return;
		}
	}
	
	/*
	 *   #################################
	 *   #             BLOCKS            #
	 *   #################################
	 */
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPhysics(BlockPhysicsEvent event) {
		
		Material type = event.getBlock().getType();
		
		if (type == Material.PORTAL || type == Material.WALL_SIGN || type == Material.SNOW || event.getChangedType() == Material.VINE) {
			event.setCancelled(true);
		}

	}
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void blockBreak(BlockBreakEvent event) {
		if (event.getPlayer().hasPermission(Permissions.MODIFY)) {
			Block block = event.getBlock();
			if (block.getType() == Material.WALL_SIGN) {
				Sign sign = (Sign) block.getState();
				String firstLine = sign.getLine(0);
				if (firstLine != null && firstLine.length() > 0) {
					if (GoldRush.getArenaByName(ChatColor.stripColor(firstLine)) != null) {
						event.setCancelled(true);
					}
				}
			}
		} else {
			event.setCancelled(true);
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void blockPlace(BlockPlaceEvent event) {
		if (!event.getPlayer().hasPermission(Permissions.MODIFY)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void hangingRemove(HangingBreakEvent event) {
		if (event instanceof HangingBreakByEntityEvent) {
			Entity breaker = ((HangingBreakByEntityEvent) event).getRemover();
			if (breaker instanceof Player && ((Player) breaker).hasPermission(Permissions.MODIFY)) {
				return; // Non annullare l'evento
			}
		}
		
		event.setCancelled(true);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void hangingPlace(HangingPlaceEvent event) {
		if (!event.getPlayer().hasPermission(Permissions.MODIFY)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void blockBurn(BlockBurnEvent event) { event.setCancelled(true); }
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void blockDamage(BlockDamageEvent event) { event.setCancelled(true); }
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void blockDispense(BlockDispenseEvent event) { event.setCancelled(true); }
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void blockFade(BlockFadeEvent event) { event.setCancelled(true); }
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void blockForm(BlockFormEvent event) { event.setCancelled(true); }
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void blockGrow(BlockGrowEvent event) { event.setCancelled(true); }
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void blockIgnite(BlockIgniteEvent event) { event.setCancelled(true); }
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void blockPistonExtend(BlockPistonExtendEvent event) { event.setCancelled(true); }
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void blockPistonRetract(BlockPistonRetractEvent event) { event.setCancelled(true); }
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void blockSpread(BlockSpreadEvent event) { event.setCancelled(true); }
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void blockFormByEntity(EntityBlockFormEvent event) { event.setCancelled(true); }
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void blockDecay(LeavesDecayEvent event) { event.setCancelled(true); }
	/*
	 *   #################################
	 *   #              MISC             #
	 *   #################################
	 */
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void mobSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() != SpawnReason.CUSTOM) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void weather(WeatherChangeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void explode(EntityExplodeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void chunkLoad(ChunkLoadEvent event) {
		for (Entity entity : event.getChunk().getEntities()) {
			if (entity.getType() == EntityType.EXPERIENCE_ORB || (entity instanceof LivingEntity && entity.getType() != EntityType.PLAYER)) {
				entity.remove();
			}
		}
	}
}
