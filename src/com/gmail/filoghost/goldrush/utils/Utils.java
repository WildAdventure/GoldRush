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
package com.gmail.filoghost.goldrush.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.NumberConversions;

import wild.api.WildCommons;
import wild.api.bridges.CosmeticsBridge;
import wild.api.bridges.CosmeticsBridge.Status;
import wild.api.item.ItemBuilder;
import wild.api.menu.IconMenu;
import wild.api.menu.MenuInventoryHolder;

import com.gmail.filoghost.goldrush.Configuration;
import com.gmail.filoghost.goldrush.GoldRush;
import com.gmail.filoghost.goldrush.constants.Lang;
import com.gmail.filoghost.goldrush.objects.GrPlayer;
import com.gmail.filoghost.goldrush.runnables.DisplayStatsSidebarTask;
import com.google.common.collect.Sets;

public class Utils {
	
	public static Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
	public static Random random = new Random();
	
	public static final DecimalFormat DECIMAL_FORMAT_THOUSANDS = new DecimalFormat("###,###", new DecimalFormatSymbols(Locale.ITALY));
	
	public static final ItemStack
	
		shop = ItemBuilder.of(Material.EMERALD)
			.name("§aShop §7(Click destro)")
			.lore("§7Per aprire fai click con mouse destro", "§7mentre tieni l'oggetto in mano.")
			.build(),
	
	
		arenaNavigator = ItemBuilder.of(Material.PAPER)
				.name("§aArene §7(Click destro)")
				.lore("§7Per aprire fai click con mouse destro", "§7mentre tieni l'oggetto in mano.")
				.build();
	
	public static IconMenu getOpenIconMenu(Player player) {
		InventoryView invView = player.getOpenInventory();
		if (invView != null) {
			Inventory top = invView.getTopInventory();
			if (top != null) {
				InventoryHolder holder = top.getHolder();
				if (holder instanceof MenuInventoryHolder) {
					return ((MenuInventoryHolder) holder).getIconMenu();
				}
			}
		}
		
		return null;
	}
	
	public static String serializeInts(Set<Integer> ints) {
		if (ints != null && !ints.isEmpty()) {
			return StringUtils.join(ints, ",");
		} else {
			return "";
		}
	}
	
	public static Set<Integer> deserizeInts(String ints) {
		Set<Integer> result = Sets.newHashSet();
		
		if (ints != null && !ints.isEmpty()) {
			for (String part : ints.split(",")) {
				try {
					result.add(Integer.parseInt(part));
				} catch (NumberFormatException ex) {
					GoldRush.logger.info("Could not parse int (" + part + ") in " + ints);
				}
			}
		}
		
		return result;
	}
	
	public static double xzDistanceSquared(Location l1, Location l2) {
		return square(l1.getX() - l2.getX()) + square(l1.getZ() - l2.getZ());
	}
	
	public static double square(double d) {
		return d * d;
	}
	
	/*
	public static boolean isPlate(Material mat) {
		return mat == Material.WOOD_PLATE || mat == Material.STONE_PLATE || mat == Material.IRON_PLATE || mat == Material.GOLD_PLATE;
	}
	*/
	
	public static FileConfiguration loadFileOnly(String path) {
		
		if (!path.endsWith(".yml")) path += ".yml";
		
		File file = new File(GoldRush.plugin.getDataFolder(), path);
		
		if (!file.exists()) {
			Debug.ln("The file does not exist: " + path);
			return null;
		}
		
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		return config;
		
	}
	
	public static List<String> createList(String... lines) {
		List<String> list = new ArrayList<>();
		for (String line : lines) {
			list.add(line);
		}
		return list;
	}
	
	public static FileConfiguration loadArenaFile(String arenaName) {
		return loadFileOnly(Lang.ARENAS_FOLDER + File.separator + arenaName.toLowerCase());
	}
	
	
	public static String getMethodName() {
		return Thread.currentThread().getStackTrace()[3].getMethodName();
	}
	
	public static Block getBlockBelow(Location loc) {
		return loc.getWorld().getBlockAt(loc.getBlockX(), NumberConversions.floor(loc.getY() - 0.2), loc.getBlockZ());
	}
	
	public static void clearPlayer(Player player) {
		if (player.getGameMode() != GameMode.ADVENTURE) {
			player.setGameMode(GameMode.ADVENTURE);
		}
		WildCommons.clearInventoryFully(player);
		player.setHealth(20.0);
		player.setFoodLevel(20);
		player.setSaturation(20F);
		player.setLevel(0);
		player.setExp(0);
		player.setTotalExperience(0);
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
	}
	
	public static void setNight(Player player) {
		player.setPlayerTime(14000, false);
	}
	
	
	public static void toTheLobby(GrPlayer grPlayer, Player player, boolean message, boolean scheduledScoreboard) {
		
		clearPlayer(player);
		giveLobbyStuff(player);
		player.resetPlayerTime();
		player.teleport(Configuration.lobby);
		player.updateInventory();
		CosmeticsBridge.updateCosmetics(player, Status.LOBBY);
		
		if (scheduledScoreboard) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(GoldRush.plugin, new DisplayStatsSidebarTask(grPlayer, player), 1L);
		} else {
			grPlayer.displayStatsSidebar(player);
		}
		
		if (message) {
			player.sendMessage(Lang.TELEPORTED_TO_SPAWN);
		}
	}
	
	public static void giveLobbyStuff(Player player) {
		Inventory inv = player.getInventory();
		
		inv.setItem(0, GoldRush.book.getItemStack());
		inv.setItem(1, shop);
		inv.setItem(2, arenaNavigator);
		CosmeticsBridge.giveCosmeticsItems(inv);
	}

	public static void setValue(Object instance, String fieldName, Object value) {
		try {
			Field field = instance.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(instance, value);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    }
	
	
	public static void setHeadName(Block block, String name) {
		if (block  == null || name == null) return;
		BlockState state = block.getState();
		if (state instanceof Skull) {
			((Skull) state).setOwner(name);
			state.update();
		}
	}
	
	public static void setSign(Block block, String first, String second, String third, String fourth) {
		if (block  == null) return;
		BlockState state = block.getState();
		if (state instanceof Sign) {
			Sign sign = (Sign) state;
			sign.setLine(0, first);
			sign.setLine(1, second);
			sign.setLine(2, third);
			sign.setLine(3, fourth);
			state.update();
		}
	}
	
	public static boolean hasPieceOfArmor(Player player) {
		PlayerInventory inv = player.getInventory();
		return inv.getHelmet() != null || inv.getChestplate() != null || inv.getLeggings() != null || inv.getBoots() != null;
	}
	
	
	public static boolean hasItem(Inventory inv, Material mat, int amount) {
		int amountFound = 0;
		
		for (ItemStack item : inv.getContents()) {
			if (item != null && item.getType() == mat) {
				amountFound += item.getAmount();
			}
		}
		
		return amountFound >= amount;
	}
	
	public static boolean takeItem(Inventory inv, Material mat, int amount) {
		if (amount <= 0) {
			return true;
		}
		
		int itemsToTake = amount; // Inizia dalla quantità richiesta e sottrae
		
		ItemStack[] contents = inv.getContents();
		ItemStack current = null;
		
		
		for (int i = 0; i < contents.length; i++) {

			current = contents[i];
			
			if (current != null && current.getType() == mat) {
				if (current.getAmount() > itemsToTake) {
					current.setAmount(current.getAmount() - itemsToTake);
					return true;
				} else {
					itemsToTake -= current.getAmount();
					inv.setItem(i, new ItemStack(Material.AIR));
				}
			}
			
			if (itemsToTake <= 0) return true;
		}
		
		return false;
	}
	
	public static int calculateTotalExp(final int level, final int partialExp) {
		int linearExpLevels;
		int increasingBitLevels = 0; // Aumenta di 3 ogni livello
		int increasingMuchLevels = 0; // Aumenta di 7 ogni livello
		
		if (level > 16) {
			linearExpLevels = 16;
			int levelsTemp = level - linearExpLevels;
			
			if (levelsTemp > 15) {
				increasingBitLevels = 15;
				increasingMuchLevels = levelsTemp - 15;
				
			} else {
				increasingBitLevels = levelsTemp;
			}
			
		} else {
			linearExpLevels = level;
		}
		
		int result = linearExpLevels * 17;
		if (increasingBitLevels > 0) {
			
			result += increasingBitLevels * 20;
			result += (increasingBitLevels - 1) * (increasingBitLevels * 3) / 2; // Area sottesa
			
			if (increasingMuchLevels > 0) {
				result += increasingMuchLevels * 69;
				result += (increasingMuchLevels - 1) * (increasingMuchLevels * 7) / 2; // Area sottesa
			}
		}
		
		result += partialExp;
		return result;
	}
}
