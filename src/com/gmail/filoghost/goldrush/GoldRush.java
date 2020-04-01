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
package com.gmail.filoghost.goldrush;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import wild.api.WildCommons;
import wild.api.bridges.BoostersBridge;
import wild.api.config.PluginConfig;
import wild.api.item.BookTutorial;

import com.gmail.filoghost.goldrush.commands.ArenaCommand;
import com.gmail.filoghost.goldrush.commands.ClassificaCommand;
import com.gmail.filoghost.goldrush.commands.CoinsCommand;
import com.gmail.filoghost.goldrush.commands.GlobalCommand;
import com.gmail.filoghost.goldrush.commands.GoldrushCommand;
import com.gmail.filoghost.goldrush.commands.RankCommand;
import com.gmail.filoghost.goldrush.commands.SpawnCommand;
import com.gmail.filoghost.goldrush.commands.StartCommand;
import com.gmail.filoghost.goldrush.commands.StatsCommand;
import com.gmail.filoghost.goldrush.constants.Lang;
import com.gmail.filoghost.goldrush.enums.GameState;
import com.gmail.filoghost.goldrush.listeners.PlayerListener;
import com.gmail.filoghost.goldrush.listeners.RestrictionsListener;
import com.gmail.filoghost.goldrush.listeners.StrengthFixListener;
import com.gmail.filoghost.goldrush.mysql.SQLColumns;
import com.gmail.filoghost.goldrush.mysql.SQLManager;
import com.gmail.filoghost.goldrush.mysql.SQLStat;
import com.gmail.filoghost.goldrush.objects.Arena;
import com.gmail.filoghost.goldrush.objects.ArenasIconMenu;
import com.gmail.filoghost.goldrush.objects.GrPlayer;
import com.gmail.filoghost.goldrush.timers.ExpMergeTimer;
import com.gmail.filoghost.goldrush.timers.MenuUpdateTimer;
import com.gmail.filoghost.goldrush.timers.NoDamageRegenTimer;
import com.gmail.filoghost.goldrush.timers.SpawnerTimer;
import com.gmail.filoghost.goldrush.utils.Debug;
import com.gmail.filoghost.goldrush.utils.Ranking;
import com.gmail.filoghost.goldrush.utils.SettingsChecker;
import com.gmail.filoghost.goldrush.utils.Utils;
import com.google.common.collect.Maps;

public class GoldRush extends JavaPlugin {
	
	public static final String PLUGIN_ID = "gold_rush";
	
	public static Plugin plugin;
	public static World mainWorld;
	public static Logger logger;
	public static BookTutorial book;
	
	public static Map<String, Arena> arenaMap = Maps.newConcurrentMap();
	public static Map<Player, Arena> playerMap = Maps.newConcurrentMap();
	public static ArenasIconMenu arenasGui;
	
	@Override
	public void onEnable() {
		if (!Bukkit.getPluginManager().isPluginEnabled("WildCommons")) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + this.getName() + "] Richiesto WildCommons!");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) { }
			setEnabled(false);
			Bukkit.shutdown();
			return;
		}
		
		if (!Bukkit.getPluginManager().isPluginEnabled("HolographicMobs")) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + this.getName() + "] Richiesto HolographicMobs!");
			WildCommons.pauseThread(10000);
			Bukkit.shutdown();
			return;
		}
		
		logger = this.getLogger();
		plugin = this;
		mainWorld = Bukkit.getWorld("world");
		
		BoostersBridge.registerPluginID(PLUGIN_ID);

		SettingsChecker.run();
		
		
		PluginConfig config = null;
		try {
			config = new PluginConfig(this, "config.yml");
		} catch (Exception e) {
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "Impossibile leggere config.yml");
			WildCommons.pauseThread(10000);
			Bukkit.shutdown();
		}
		Configuration.setup(config);
		
		try {
			
			SQLManager.connect(Configuration.mysqlHost, Configuration.mysqlPort, Configuration.mysqlDatabase, Configuration.mysqlUser, Configuration.mysqlPass);
			SQLManager.checkConnection();
			
			SQLManager.getMysql().update("CREATE TABLE IF NOT EXISTS " + SQLManager.TABLE + " ("
					+ SQLColumns.NAME + " varchar(20) NOT NULL UNIQUE, "
					+ SQLColumns.UPGRADES + " TEXT NOT NULL, "
					+ SQLColumns.COINS + " INT unsigned NOT NULL, "
					+ SQLColumns.WINS + " INT unsigned NOT NULL, "
					+ SQLColumns.KILLS + " INT unsigned NOT NULL, "
					+ SQLColumns.DEATHS + " INT unsigned NOT NULL"
					+ ") ENGINE = InnoDB DEFAULT CHARSET = UTF8;");
			
		} catch (Exception e) {
			e.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "Impossibile connettersi al database: " + e.getMessage());
			WildCommons.pauseThread(10000);
			Bukkit.shutdown();
		}
		
		
		
		book = new BookTutorial(this, "Gold Rush");
		
		File arenasFolder = new File(getDataFolder(), Lang.ARENAS_FOLDER);
		if (!arenasFolder.exists())	{
			arenasFolder.mkdirs();
		}

		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				try {
					Ranking.loadRankings();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				final List<SQLStat> topKillers = Ranking.getTopKills();
				
				if (topKillers.size() >= 3) {
					final String first = topKillers.get(0).getName();
					final String second = topKillers.get(1).getName();
					final String third = topKillers.get(2).getName();
					
					new BukkitRunnable() {
						@Override
						public void run() {
							Utils.setHeadName(Configuration.firstKillerHead, first);
							Utils.setSign(Configuration.firstKillerSign, "§1§l§nPRIMO", "", first, "" + topKillers.get(0).getValue());
							
							Utils.setHeadName(Configuration.secondKillerHead, second);
							Utils.setSign(Configuration.secondKillerSign, "§1§l§nSECONDO", "", second, "" + topKillers.get(1).getValue());
							
							Utils.setHeadName(Configuration.thirdKillerHead, third);
							Utils.setSign(Configuration.thirdKillerSign, "§1§l§nTERZO", "", third, "" + topKillers.get(2).getValue());
						}
					}.runTask(plugin);
					
				} else {
					Debug.color("§cNon posso fare la classifica uccisioni: lista troppo piccola (" + topKillers.size() + ").");
				}
				
				final List<SQLStat> topWinners = Ranking.getTopWins();
				
				if (topWinners.size() >= 3) {
					final String first = topWinners.get(0).getName();
					final String second = topWinners.get(1).getName();
					final String third = topWinners.get(2).getName();
					
					new BukkitRunnable() {
						@Override
						public void run() {
							Utils.setHeadName(Configuration.firstWinnerHead, first);
							Utils.setSign(Configuration.firstWinnerSign, "§1§l§nPRIMO", "", first, "" + topWinners.get(0).getValue());
							
							Utils.setHeadName(Configuration.secondWinnerHead, second);
							Utils.setSign(Configuration.secondWinnerSign, "§1§l§nSECONDO", "", second, "" + topWinners.get(1).getValue());
							
							Utils.setHeadName(Configuration.thirdWinnerHead, third);
							Utils.setSign(Configuration.thirdWinnerSign, "§1§l§nTERZO", "", third, "" + topWinners.get(2).getValue());
						}
					}.runTask(plugin);
				} else {
					Debug.color("§cImpossibile fare la classifica vittorie: lista troppo piccola (" + topWinners.size() + ").");
				}
			
			}
		}.runTaskTimerAsynchronously(this, 0L, 60 * 20L);
		
		
		
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getPluginManager().registerEvents(new RestrictionsListener(), this);
		Bukkit.getPluginManager().registerEvents(new StrengthFixListener(), this);
		
		
		new GoldrushCommand(this, "goldrush");
		new StartCommand(this, "start");
		new SpawnCommand(this, "spawn");
		new ArenaCommand(this, "arena");
		new RankCommand(this, "rank");
		new StatsCommand(this, "stats");
		new ClassificaCommand(this, "classifica");
		new CoinsCommand(this, "coins");
		new GlobalCommand(this, "global");

		File[] files = arenasFolder.listFiles();

		for (File arenaFile : files) {
			
			if (arenaFile.isFile() && arenaFile.exists() && arenaFile.getName().endsWith(".yml")) {
			
				try {
					loadArena(arenaFile, Bukkit.getConsoleSender());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		if (!Bukkit.getOnlinePlayers().isEmpty()) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				try {
					GrPlayer.load(player.getName());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		arenasGui = new ArenasIconMenu("Arene");
		
		new MenuUpdateTimer().startNewTask();
		new SpawnerTimer().startNewTask();
		new ExpMergeTimer().startNewTask();
		new NoDamageRegenTimer().startNewTask();
	}
	
	@Override
	public void onDisable() {
		
		BoostersBridge.unregisterPluginID(PLUGIN_ID);
		
		getLogger().info("Saving all the players, please wait...");
		
		for (Arena arena : arenaMap.values()) {
			
			if (arena.getState() == GameState.GAME) {
				getLogger().info("Forced arena end: " + arena.getName());
				arena.giveRewards(null, true, false);
			}
		}
		
		getLogger().info("Saved!");
	}
	
	
	public static void loadArena(File file, CommandSender sender) throws Exception {
		if (!file.exists()) throw new Exception("File must exist");
		if (!file.isFile()) throw new Exception("The file can't be a folder");
		
		FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
		Arena arena = new Arena(yaml, sender);
		
		if (arena.isCreationSuccessful()) {
			arenaMap.put(arena.getName(), arena);
		} else {
			plugin.getLogger().warning("Impossibile caricare l'arena " + file.getName());
		}
	}
	
	
	public static boolean isPlaying(Player player) {
		return playerMap.containsKey(player);
	}
	
	public static Arena getArenaByName(String name) {
		return arenaMap.get(name);
	}
	
	public static Arena getArenaByNameIgnoreCase(String name) {
		for (Entry<String, Arena> entry : arenaMap.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(name)) {
				return entry.getValue();
			}
		}
		
		return null;
	}
	
	/**
	 * @return l'arena del giocatore o null se non sta giocando.
	 */
	public static Arena getArenaByPlayer(Player player) {
		return playerMap.get(player);
	}
	
}
