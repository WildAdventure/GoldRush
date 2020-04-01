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

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.NumberConversions;

import wild.api.menu.IconMenu;
import wild.api.sound.EasySound;

import com.gmail.filoghost.goldrush.Configuration;
import com.gmail.filoghost.goldrush.GoldRush;
import com.gmail.filoghost.goldrush.constants.Items;
import com.gmail.filoghost.goldrush.constants.Numbers;
import com.gmail.filoghost.goldrush.constants.Permissions;
import com.gmail.filoghost.goldrush.enums.GameState;
import com.gmail.filoghost.goldrush.enums.TeamColor;
import com.gmail.filoghost.goldrush.objects.Arena;
import com.gmail.filoghost.goldrush.objects.GrPlayer;
import com.gmail.filoghost.goldrush.objects.shop.ShopGui;
import com.gmail.filoghost.goldrush.objects.shop.ShopItem;
import com.gmail.filoghost.goldrush.utils.Utils;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class PlayerListener implements Listener {
	
	private Map<Player, Long> teleportCooldown = Maps.newHashMap();
	private Map<Player, Long> respawnTime = Maps.newHashMap();
	public static Map<Player, Long> lastDamage = Maps.newHashMap();
	public static Set<Player> forceGlobalChat = Sets.newHashSet();
	public static Set<Player> globalChatTip = Sets.newHashSet();

	@EventHandler (priority = EventPriority.HIGHEST)
	public void playerLogin(AsyncPlayerPreLoginEvent event) {
		if (event.getLoginResult() != Result.ALLOWED) {
			return;
		}
		
		try {
			GrPlayer.load(event.getName());
		} catch (SQLException e) {
			GoldRush.plugin.getLogger().log(Level.SEVERE, "Impossibile caricare i dati di " + event.getName(), e);
			event.setLoginResult(Result.KICK_OTHER);
			event.setKickMessage("Impossibile caricare i tuoi dati dal database, contatta lo staff.");
		}
	}
	
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onChat(AsyncPlayerChatEvent event) {
		Arena arena = GoldRush.getArenaByPlayer(event.getPlayer());
		if (arena == null) {
			return; // Globale
		}
		
		TeamColor color = arena.getTeamManager().getTeamColor(event.getPlayer());
		if (color == null) {
			return; // Non ancora incominciata, o non ci sono i team; Globale
		}
		
		if (forceGlobalChat.contains(event.getPlayer())) {
			return; // Globale
		}
		
		if (!globalChatTip.contains(event.getPlayer())) {
			globalChatTip.add(event.getPlayer());
			event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Stai usando la chat del tuo team.");
			event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Per parlare in globale, usa " + ChatColor.GRAY + "/g <messaggio>");
		}
		
		boolean bothTeams = arena.getState() == GameState.END;
		String prefix = null;
		event.getRecipients().clear();
		
		if (color == TeamColor.RED) {
			prefix = ChatColor.RED + "[ROSSI] " + ChatColor.RESET;
			event.getRecipients().addAll(arena.getTeamManager().getRedList());
			if (bothTeams) {
				prefix = ChatColor.GOLD + "[ARENA] " + prefix;
				event.getRecipients().addAll(arena.getTeamManager().getBlueList());
			}
		} else if (color == TeamColor.BLUE) {
			prefix = ChatColor.BLUE + "[BLU] " + ChatColor.RESET;
			event.getRecipients().addAll(arena.getTeamManager().getBlueList());
			if (bothTeams) {
				prefix = ChatColor.GOLD + "[ARENA] " + prefix;
				event.getRecipients().addAll(arena.getTeamManager().getRedList());
			}
		}
		
		event.setFormat(prefix + event.getFormat());
	}
	
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void playerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		GrPlayer grPlayer = GrPlayer.get(player);
		
		if (grPlayer == null) {
			System.out.println("Impossibile caricare " + player.getName() + "!");
			player.kickPlayer("Non è stato possibile caricare i tuoi dati, riprova e contatta lo staff.");
			return;
		}
		
		
		Utils.toTheLobby(grPlayer, player, false, true);
		
		if (player.hasPermission(Permissions.MODIFY)) {
			player.setGameMode(GameMode.CREATIVE);
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void playerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		Arena arena = GoldRush.getArenaByPlayer(player);
		if (arena != null) {
			arena.removeGamer(player);
		}
		
		GrPlayer.unload(player);
		teleportCooldown.remove(player);
		respawnTime.remove(player);
		lastDamage.remove(player);
		globalChatTip.remove(player);
		forceGlobalChat.remove(player);
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onDeath(PlayerDeathEvent event) {
		Player dead = event.getEntity();
		Player killer = dead.getKiller();
		
		if (dead != null && killer != null) {
			Arena deadArena = GoldRush.getArenaByPlayer(dead);
			Arena killerArena = GoldRush.getArenaByPlayer(killer);
			
			if (deadArena != null && killerArena != null && deadArena == killerArena) {
				killerArena.killEvent(killer, dead);
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void mobDeath(EntityDeathEvent event) {
		if (event.getEntityType() == EntityType.PIG_ZOMBIE) {
			Iterator<ItemStack> iter = event.getDrops().iterator();
			while (iter.hasNext()) {
				ItemStack next = iter.next();
				
				if (next.getType() == Material.GOLD_SWORD && (next.getEnchantments() == null || next.getEnchantments().isEmpty())) {
					iter.remove();
				}
			}
			
			Player killer = event.getEntity().getKiller();
			
			if (killer != null) {
				Arena arena = GoldRush.getArenaByPlayer(killer);
				if (arena != null) {
					arena.getGameData(killer).addPigman();
				}
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void playerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		respawnTime.put(player, System.currentTimeMillis());
		
		// Rimuove effetti pozioni
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		
		Arena arena = GoldRush.getArenaByPlayer(player);
		if (arena == null) {
			event.setRespawnLocation(Configuration.lobby);
			
			GrPlayer grPlayer = GrPlayer.get(player);
			Utils.giveLobbyStuff(player);
			grPlayer.displayStatsSidebar(player);
		} else {
			arena.handleRespawn(event);
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerDamage(EntityDamageEvent event) {
		
		if (event.getEntity().getType() == EntityType.ITEM_FRAME) {
			
			boolean cancel = true;
			
			if (event instanceof EntityDamageByEntityEvent) {
				Entity breaker = ((EntityDamageByEntityEvent) event).getDamager();
				
				if (breaker instanceof Player && ((Player) breaker).hasPermission(Permissions.MODIFY)) {
					cancel = false;
				}
			}
			
			if (cancel) {
				event.setCancelled(true);
			}
		}
		
		if (event.getCause() == DamageCause.VOID) {
			event.setCancelled(false);
			event.setDamage(1000.0); // Anche allo spawn
			
		} else {
			if (event.getEntityType() == EntityType.PLAYER) {
				Player player = (Player) event.getEntity();
				Arena arena = GoldRush.getArenaByPlayer(player);
				
				if (arena == null || arena.getState() != GameState.GAME) {
					event.setCancelled(true);
					return;
				}
				
				if (event.getCause() == DamageCause.FALL) {
					Long lastRespawn = respawnTime.get(player);
					if (lastRespawn != null && System.currentTimeMillis() - lastRespawn.longValue() < 3000) {
						event.setCancelled(true);
						return;
					}
				}
				
				if (event instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent) event).getDamager().getType() == EntityType.PIG_ZOMBIE) {
					event.setDamage(DamageModifier.BASE, Numbers.PIGMAN_DAMAGE);
				}
				
				lastDamage.put(player, System.currentTimeMillis());
				
			} else if (event.getEntityType() == EntityType.PIG_ZOMBIE) {
				
				if (event.getCause() == DamageCause.FALL) {
					event.setDamage(0.0);
				}
			}
		}
	}
	
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onExp(PlayerExpChangeEvent event) {
		
		if (event.getPlayer().hasPermission(Permissions.EXP_X1_5)) {
			event.setAmount(NumberConversions.ceil(event.getAmount() * 1.5));
		} else if (event.getPlayer().hasPermission(Permissions.EXP_X1_25)) {
			event.setAmount(NumberConversions.ceil(event.getAmount() * 1.25));
		}
		
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
			
		if (event.hasBlock() && Items.ALLOWED_FROM_PADS.contains(event.getClickedBlock().getType())) {
				
			Arena arena = GoldRush.getArenaByPlayer(event.getPlayer());
			if (arena == null) {
				return;
			}
			
			event.setCancelled(true);

			TeamColor color = arena.getTeamManager().getTeamColor(event.getPlayer());
			Location destionation = null; // non null se è stato trovata una destinazione per il team
			ShopGui shopGui = null;

			if (color == TeamColor.RED) {
				if (arena.getRedPadButtons().contains(event.getClickedBlock())) {
					destionation = arena.getRedPadDestination();
				}
				shopGui = arena.getRedGui();
			} else if (color == TeamColor.BLUE) {
				if (arena.getBluePadButtons().contains(event.getClickedBlock())) {
					destionation = arena.getBluePadDestination();
				}
				shopGui = arena.getBlueGui();
			}

			if (destionation != null) {

				long now = System.currentTimeMillis();
				Long lastTeleport = teleportCooldown.get(event.getPlayer());

				if (lastTeleport != null && now - lastTeleport < 1000) {
					return;
				}

				teleportCooldown.put(event.getPlayer(), now);

				if (shopGui.isUnlockedPads()) {
					event.getPlayer().teleport(destionation, TeleportCause.PLUGIN);
					EasySound.quickPlay(event.getPlayer(), Sound.ENDERMAN_TELEPORT);
					event.getPlayer().sendMessage(ChatColor.GRAY + "Sei stato teletrasportato a destinazione!");
				} else {
					EasySound.quickPlay(event.getPlayer(), Sound.CLICK, 0.6f);
					event.getPlayer().sendMessage(ChatColor.GRAY + "Il teletrasporto non è ancora stato sbloccato.");
				}
			}
		}
		
		
		// On click block
		if (action == Action.RIGHT_CLICK_BLOCK && event.hasBlock()) {
			
			if (event.getClickedBlock().getType() == Material.DISPENSER) {
				
				event.setCancelled(true); // Non fa aprire i dispenser
				Player player = event.getPlayer();
				
				Arena arena = GoldRush.getArenaByPlayer(player);
				if (arena != null && arena.getState() == GameState.GAME) {
	
					TeamColor color = arena.getTeamManager().getTeamColor(player);
					if (color != null) {

						TeamColor potColor = null;
						if (event.getClickedBlock().equals(arena.getBluePot())) {
							potColor = TeamColor.BLUE;
						} else if (event.getClickedBlock().equals(arena.getRedPot())) {
							potColor = TeamColor.RED;
						}

						if (potColor == null) {
							return; // Ignora
						}

						if (potColor == color) {
							ItemStack itemInHand = player.getItemInHand();
							if (itemInHand != null && itemInHand.getType() == Material.GOLD_INGOT) {
								// Ok, consegna oro
								int currentGold = 0;
								if (color == TeamColor.BLUE) {
									currentGold = arena.getTeamManager().getBlueGold();
								} else if (color == TeamColor.RED) {
									currentGold = arena.getTeamManager().getRedGold();
								}
								
								int deliveredGold = Math.min(itemInHand.getAmount(), arena.getTeamManager().getMaxScore() - currentGold);
								
								arena.addGoldEvent(player, color, deliveredGold);
								
								if (deliveredGold >= itemInHand.getAmount()) {
									player.setItemInHand(null);
								} else {
									itemInHand.setAmount(itemInHand.getAmount() - deliveredGold);
								}
									
							} else {
								player.sendMessage(ChatColor.RED + "Devi consegnare almeno un lingotto d'oro.");
							}
						} else {
							player.sendMessage(ChatColor.RED + "Questo non è il raccoglitore del tuo team!");
						}
					}
				}
				
				
			} else if (event.getClickedBlock().getType() == Material.WALL_SIGN) {
				Sign sign = (Sign) event.getClickedBlock().getState();
				Arena arena = GoldRush.getArenaByName(ChatColor.stripColor(sign.getLine(0)));
				if (arena != null) {
					event.setCancelled(true);
					arena.addGamer(event.getPlayer());
					return;
				}
			}
		}
	
		
		if (event.hasItem()) {
			
			Player player = event.getPlayer();
			
			TeamColor preferredColor = null;
			if (Items.TEAM_SELECTOR_RED.isSimilar(event.getItem())) {
				preferredColor = TeamColor.RED;
			} else if (Items.TEAM_SELECTOR_BLUE.isSimilar(event.getItem())) {
				preferredColor = TeamColor.BLUE;
			}
			
			if (preferredColor != null) {
				
				event.setCancelled(true); // Previene l'uso della lana
				
				// Ha scelto il team
				Arena arena = GoldRush.getArenaByPlayer(player);
				if (arena == null) {
					return;
				}
				
				if (arena.getTeamPreferences().get(player) == preferredColor) {
					player.sendMessage(ChatColor.GRAY + "Hai già scelto quel team.");
					return;
				}
				
				if (!arena.canPreferTeam(player, preferredColor)) {
					player.sendMessage(ChatColor.GRAY + "Troppi giocatori hanno già scelto quel team!");
					return;
				}
				
				arena.getTeamPreferences().put(player, preferredColor);
				player.sendMessage(ChatColor.GRAY + "Hai espresso la tua preferenza per il team " + preferredColor.getChatColor() + ChatColor.BOLD + preferredColor.getMaleLowercase() + ChatColor.GRAY);
				return;
			}
		}
		
		
		if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && event.hasItem()) {
			
			Player player = event.getPlayer();
			Material type = event.getItem().getType();
			
			if (type == Material.EMERALD) {
				IconMenu shop = new IconMenu("Shop Mercante", 6);
				ShopItem.placeIcons(shop, GrPlayer.get(player), player, 0);
				shop.refresh();
				shop.open(player);
				
			} else if (type == Material.PAPER) {
				GoldRush.arenasGui.open(player);
			}
		}
	}
}
