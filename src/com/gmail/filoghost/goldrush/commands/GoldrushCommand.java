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
package com.gmail.filoghost.goldrush.commands;

import java.sql.SQLException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import wild.api.command.CommandFramework;
import wild.api.command.CommandFramework.Permission;

import com.gmail.filoghost.goldrush.Configuration;
import com.gmail.filoghost.goldrush.GoldRush;
import com.gmail.filoghost.goldrush.constants.Permissions;
import com.gmail.filoghost.goldrush.mysql.SQLColumns;
import com.gmail.filoghost.goldrush.mysql.SQLManager;
import com.gmail.filoghost.goldrush.mysql.SQLPlayerData;
import com.gmail.filoghost.goldrush.mysql.SQLTask;
import com.gmail.filoghost.goldrush.objects.Arena;
import com.gmail.filoghost.goldrush.objects.GrPlayer;
import com.gmail.filoghost.goldrush.objects.shop.ShopItem;
import com.gmail.filoghost.goldrush.utils.Debug;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

@Permission(Permissions.ADMIN_COMMAND)
public class GoldrushCommand extends CommandFramework {

	public GoldrushCommand(JavaPlugin plugin, String label) {
		super(plugin, label);
	}

	@Override
	public void execute(final CommandSender sender, String label, String[] args) {
		
		
		if (args.length == 0) {
			sender.sendMessage("§6========== Comandi /" + label + " ==========");
			sender.sendMessage("§e/" + label + " info");
			sender.sendMessage("§e/" + label + " stats <player>");
			sender.sendMessage("§e/" + label + " setspawn");
			sender.sendMessage("§e/" + label + " tp <arena>");
			sender.sendMessage("§e/" + label + " join <arena>");
			sender.sendMessage("§e/" + label + " debug");
			if (sender instanceof ConsoleCommandSender) {
				sender.sendMessage("§c(Hidden) §e/" + label + " reset");
				sender.sendMessage("§c(Hidden) §e/" + label + " addcoins <player> <amount>");
			}
			return;
		}

		if (args[0].equalsIgnoreCase("info")) {
			sender.sendMessage("§8==========[ §dInformazioni Generali §8]==========");
			sender.sendMessage("§aGiocatori online: §f" + Bukkit.getOnlinePlayers().size());
			sender.sendMessage("§aGiocatori caricati: §f" + GrPlayer.playersMap.size());
			sender.sendMessage("§aGiocatori nelle arene: §f" + GoldRush.playerMap.size());
			sender.sendMessage("§aArena caricate: §f" + GoldRush.arenaMap.size());
			return;
		}
		
		
		if (args[0].equalsIgnoreCase("tp")) {
			CommandValidate.minLength(args, 2, "Utilizzo corretto: /" + label + " tp <arena>");
			Arena arena = GoldRush.getArenaByNameIgnoreCase(args[1]);
			CommandValidate.notNull(arena, "Arena non trovata.");
			CommandValidate.getPlayerSender(sender).teleport(arena.getLobby());
			sender.sendMessage("§aSei stato teletrasportato nell'arena.");
			return;
		}
		
		
		if (args[0].equalsIgnoreCase("join")) {
			CommandValidate.minLength(args, 2, "Utilizzo corretto: /" + label + " join <arena>");
			Arena arena = GoldRush.getArenaByNameIgnoreCase(args[1]);
			CommandValidate.notNull(arena, "Arena non trovata.");
			((Player) sender).setScoreboard(arena.getScoreboard());
			sender.sendMessage("§aSei entrato nell'arena come admin.");
			return;
		}
		
		
		if (args[0].equalsIgnoreCase("stats")) {
			CommandValidate.minLength(args, 2, "Utilizzo corretto: /" + label + " stats <player>");
			Player target = Bukkit.getPlayerExact(args[1]);
			CommandValidate.notNull(target, "Quel giocatore non è online.");
			GrPlayer grTarget = GrPlayer.get(target);
			sender.sendMessage("§6Statistiche di " + target.getName() + ":");
			sender.sendMessage("§eCoins: §f" + grTarget.getCoins());
			sender.sendMessage("§eUccisioni: §f" + grTarget.getKills());
			sender.sendMessage("§eVittorie: §f" + grTarget.getWins());
			
			List<String> upgrades = Lists.newArrayList();
			for (ShopItem item : ShopItem.values()) {
				if (item.getCoinsUnlockCost() > 0 && item.has(grTarget)) {
					upgrades.add(item.getName());
				}
			}
			
			sender.sendMessage("§eUpgrades: §f" + Joiner.on("§7, §f").join(upgrades));
			return;
		}
		
		
		if (args[0].equalsIgnoreCase("setspawn")) {
			Location newLobby = CommandValidate.getPlayerSender(sender).getLocation();
			Configuration.setLobby(newLobby);
			sender.sendMessage("§aHai settato lo spawn.");
			return;
		}
		
		
		if (args[0].equalsIgnoreCase("debug")) {
			if (Debug.enable) {
				Debug.enable = false;
				sender.sendMessage("§eDebug disabilitato.");
			} else {
				Debug.enable = true;
				sender.sendMessage("§eDebug abilitato.");
			}
			return;
		}
		
		
		if (args[0].equalsIgnoreCase("reset") && sender instanceof ConsoleCommandSender) {
			sender.sendMessage(ChatColor.GRAY + "Attendi...");
			new SQLTask() {
				
				@Override
				public void execute() throws SQLException {
					SQLManager.getMysql().update("UPDATE " + SQLManager.TABLE + " SET " + SQLColumns.WINS + " = 0, " + SQLColumns.KILLS + " = 0, " + SQLColumns.DEATHS + " = 0;");
					sender.sendMessage(ChatColor.GREEN + "Uccisioni, vittorie e morti resettate!");
				}
			}.submitAsync(sender);
			return;
		}
		
		
		if (args[0].equalsIgnoreCase("addcoins") && sender instanceof ConsoleCommandSender) {
			CommandValidate.minLength(args, 3, "/" + label + " addcoins <giocatore> <quantità>");
			final String playerName = args[1];
			final int coins = CommandValidate.getPositiveIntegerNotZero(args[2]);
			
			Player onlinePlayer = Bukkit.getPlayerExact(playerName);
			
			if (onlinePlayer != null) {
				GrPlayer grPlayer = GrPlayer.get(onlinePlayer);
				grPlayer.addCoins(coins);
				
				grPlayer.updateCoinsIfSidebar();
				grPlayer.saveIfNecessaryAsync();
			} else {
				new SQLTask() {
					
					@Override
					public void execute() throws SQLException {
						SQLPlayerData data = SQLManager.getPlayerData(playerName);
						SQLManager.setStats(playerName, data.getUpgrades(), data.getCoins() + coins, data.getWins(), data.getKills(), data.getDeaths());
					}
				}.submitAsync(sender);
			}
			sender.sendMessage(ChatColor.GREEN + "Aggiunti " + coins + " Coins a " + playerName);
			return;
		}
		
		sender.sendMessage("§cComando non trovato. /" + label + " per la lista dei comandi");
		return;
		
	}

}
