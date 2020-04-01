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
package com.gmail.filoghost.goldrush.objects;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.NonNull;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import wild.api.WildConstants;
import wild.api.bridges.BoostersBridge;
import wild.api.bridges.BoostersBridge.Booster;
import wild.api.bridges.CosmeticsBridge;
import wild.api.bridges.CosmeticsBridge.Status;
import wild.api.scheduler.Countdowns;
import wild.api.sound.EasySound;

import com.gmail.filoghost.goldrush.Configuration;
import com.gmail.filoghost.goldrush.GoldRush;
import com.gmail.filoghost.goldrush.Serializer;
import com.gmail.filoghost.goldrush.constants.ConfigNodes;
import com.gmail.filoghost.goldrush.constants.Items;
import com.gmail.filoghost.goldrush.constants.Lang;
import com.gmail.filoghost.goldrush.constants.Permissions;
import com.gmail.filoghost.goldrush.enums.GameState;
import com.gmail.filoghost.goldrush.enums.TeamColor;
import com.gmail.filoghost.goldrush.objects.shop.ShopGui;
import com.gmail.filoghost.goldrush.runnables.FireworksTask;
import com.gmail.filoghost.goldrush.runnables.GivePotionEffectTask;
import com.gmail.filoghost.goldrush.timers.GameTimer;
import com.gmail.filoghost.goldrush.utils.Debug;
import com.gmail.filoghost.goldrush.utils.Utils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Getter
public class Arena {
	
	private boolean creationSuccessful = false;
	
	private String name;
	private GameState state;
	private ShopGui redGui, blueGui;
	
	private TeamManager teamManager;
	private Location lobby, redSpawn, blueSpawn;
	private Block redPot, bluePot;
	private Location redVillager, blueVillager;
	private Location redPadDestination, bluePadDestination;
	private List<Block> redPadButtons, bluePadButtons;
	private List<Location> expSpawners, mobSpawners;
	private int maxGamers, minGamers;
	private Block signBlock;
	private Scoreboard scoreboard;
	private Objective sidebar;
	private boolean isNight;
	
	private Map<Player, GameData> gamers = Maps.newHashMap();
	private Map<Player, TeamColor> teamPreferences = Maps.newHashMap();
	
	private GameTimer gameTimer;

	public Arena(FileConfiguration config, CommandSender sender) {
		
		if (config == null) {
			sender.sendMessage("§cFile non valido!");
			return;
		}
		
		state = GameState.PREGAME;
		
		/*
		 * Nome
		 */
		name = config.getString(ConfigNodes.NAME);
		if (name == null || name.length() == 0) {
			sender.sendMessage("§cIl nome non può essere vuoto!");
			return;
		}
		
		if (GoldRush.arenaMap.get(name) != null) {
			sender.sendMessage("§c" + name + ": è già caricata!");
			return;
		}
		
		/*
		 * Cartello
		 */
		Location signLoc = new Location(GoldRush.mainWorld, config.getInt(ConfigNodes.SIGN_X), config.getInt(ConfigNodes.SIGN_Y), config.getInt(ConfigNodes.SIGN_Z));
		
		Block signBlock = signLoc.getBlock();
		if (signBlock.getType() == Material.WALL_SIGN) {
			this.signBlock = signBlock;
		} else {
			sender.sendMessage("§c" + name + ": il blocco non è un cartello appeso al muro!");
			return;
		}
		
		/*
		 * Scoreboard
		 */
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		
		/*
		 * Lobby
		 */
		try {
			lobby = Serializer.locationFromString(config.getString(ConfigNodes.LOBBY));
		} catch (IllegalArgumentException e) {
			sender.sendMessage("§cImpossibile caricare la lobby: " + e.getMessage());
			return;
		}
		
		/*
		 * Spawnpoint red - blue
		 */
		try {
			redSpawn = Serializer.locationFromString(config.getString(ConfigNodes.RED_SPAWNPOINT));
			blueSpawn = Serializer.locationFromString(config.getString(ConfigNodes.BLUE_SPAWNPOINT));
		} catch (IllegalArgumentException e) {
			sender.sendMessage("§cImpossibile caricare uno spawnpoint: " + e.getMessage());
			return;
		}
		
		/*
		 * Villagers red - blue
		 */
		try {
			redVillager = Serializer.locationFromString(config.getString(ConfigNodes.RED_VILLAGER));
			blueVillager = Serializer.locationFromString(config.getString(ConfigNodes.BLUE_VILLAGER));
		} catch (IllegalArgumentException e) {
			sender.sendMessage("§cImpossibile caricare un villico: " + e.getMessage());
			return;
		}
		
		/*
		 * Raccoglitori red - blue
		 */
		try {
			redPot = Serializer.blockFromString(config.getString(ConfigNodes.RED_POT));
			bluePot = Serializer.blockFromString(config.getString(ConfigNodes.BLUE_POT));
		} catch (IllegalArgumentException e) {
			sender.sendMessage("§cImpossibile caricare un raccoglitore: " + e.getMessage());
			return;
		}
		
		/*
		 * Pad di arrivo red - blue
		 */
		try {
			redPadDestination = Serializer.locationFromString(config.getString(ConfigNodes.RED_PAD_TO));
			bluePadDestination = Serializer.locationFromString(config.getString(ConfigNodes.BLUE_PAD_TO));
		} catch (IllegalArgumentException e) {
			sender.sendMessage("§cImpossibile caricare un teletrasporto di destinazione: " + e.getMessage());
			return;
		}
		
		/*
		 * Pad di partenza red
		 */
		redPadButtons = Lists.newArrayList();
		List<String> redPadsFrom = config.getStringList(ConfigNodes.RED_PADS_FROM);
		for (String line : redPadsFrom) {
			try {
				Block block = Serializer.blockFromString(line);
				redPadButtons.add(block);
				
			} catch (IllegalArgumentException e) {
				sender.sendMessage("§c" + name + ": impossibile caricare un teletrasporto rosso: " + e.getMessage());
			}
		}
		if (redPadButtons.isEmpty()) {
			sender.sendMessage("§c" + name + ": i teletrasporti rossi devono essere almeno 1!");
			return;
		}
		
		/*
		 * Pad di partenza blue
		 */
		bluePadButtons = Lists.newArrayList();
		List<String> bluePadsFrom = config.getStringList(ConfigNodes.BLUE_PADS_FROM);
		for (String line : bluePadsFrom) {
			try {
				Block block = Serializer.blockFromString(line);
				bluePadButtons.add(block);
				
			} catch (IllegalArgumentException e) {
				sender.sendMessage("§c" + name + ": impossibile caricare un teletrasporto blu: " + e.getMessage());
			}
		}
		if (bluePadButtons.isEmpty()) {
			sender.sendMessage("§c" + name + ": i teletrasporti blu devono essere almeno 1!");
			return;
		}
		
		
		
		/*
		 * Exp spawners
		 */
		expSpawners = Lists.newArrayList();
		List<String> expSpawnersSer = config.getStringList(ConfigNodes.EXP_SPAWNERS);
		for (String line : expSpawnersSer) {
			try {
				Block block = Serializer.blockFromString(line);
				expSpawners.add(block.getLocation().add(0.5, -0.5, 0.5));
				
			} catch (IllegalArgumentException e) {
				sender.sendMessage("§c" + name + ": impossibile caricare un exp spawner: " + e.getMessage());
			}
		}
		if (expSpawners.isEmpty()) {
			sender.sendMessage("§c" + name + ": gli exp spawner devono essere almeno 1!");
			return;
		}
		
		/*
		 * Mob spawners
		 */
		mobSpawners = Lists.newArrayList();
		List<String> mobSpawnersSer = config.getStringList(ConfigNodes.MOB_SPAWNERS);
		for (String line : mobSpawnersSer) {
			try {
				Block block = Serializer.blockFromString(line);
				mobSpawners.add(block.getLocation().add(0.5, -2.0, 0.5));
				
			} catch (IllegalArgumentException e) {
				sender.sendMessage("§c" + name + ": impossibile caricare un mob spawner: " + e.getMessage());
			}
		}
		if (mobSpawners.isEmpty()) {
			sender.sendMessage("§c" + name + ": i mob spawner devono essere almeno 1!");
			return;
		}
		
		
		int min = config.getInt(ConfigNodes.PLAYERS_MIN);
		int max = config.getInt(ConfigNodes.PLAYERS_MAX);
		
		if (min == 0) sender.sendMessage("§e" + config + ": minimo player non impostato.");
		if (max == 0) sender.sendMessage("§e" + config + ": massimo dei player non impostato.");
		
		if (min < 2) min = 2;
		if (max < 2) max = 2;
		
		if (max < min) {
			sender.sendMessage("§c" + config + ": il minimo dei giocatore è maggiore del massimo!");
			return;
		}
		
		/*
		 * Caricamento notte - giorno
		 */
		isNight = config.getBoolean("night", false);

		
		this.minGamers = min;
		this.maxGamers = max;
		
		redGui = new ShopGui(this, TeamColor.RED, "Squadra rossa");
		blueGui = new ShopGui(this, TeamColor.BLUE, "Squadra blu");
		gameTimer = new GameTimer(this);
		teamManager = new TeamManager(20, scoreboard);
		
		new VillagerTrader(this, redVillager, TeamColor.RED, "Mercante");
		new VillagerTrader(this, blueVillager, TeamColor.BLUE, "Mercante");
		
		defaultSign();
		creationSuccessful = true;
		sender.sendMessage("§aCaricata con successo \"" + name + "\".");
	}
	
	
	public void reset() {
		redGui = new ShopGui(this, TeamColor.RED, "Squadra rossa");
		blueGui = new ShopGui(this, TeamColor.BLUE, "Squadra blu");
		teamManager.resetAndCreate();
		
		for (Player gamer : gamers.keySet()) {
			GoldRush.playerMap.remove(gamer);
			if (!gamer.isDead()) {
				Utils.toTheLobby(GrPlayer.get(gamer), gamer, true, false);
			}
		}
		gamers.clear();
		teamPreferences.clear();
		state = GameState.PREGAME;
		removeSidebar();
		defaultSign();
		
		clearMobsAndExp();
	}
	
	public void clearMobsAndExp() {
		for (Location expSpawner : expSpawners) {
			for (Entity exp : expSpawner.getWorld().getEntitiesByClass(ExperienceOrb.class)) {
				if (Utils.xzDistanceSquared(exp.getLocation(), expSpawner) < 20 * 20) {
					exp.remove();
				}
			}
		}
		
		for (Location mobSpawner : mobSpawners) {
			for (Entity mob : mobSpawner.getWorld().getEntitiesByClass(PigZombie.class)) {
				if (Utils.xzDistanceSquared(mob.getLocation(), mobSpawner) < 20 * 20) {
					mob.remove();
				}
			}
		}
	}
	
	
	public boolean removeGamer(Player player) {
		teamManager.remove(player);
		teamPreferences.remove(player);
		GameData gameData = gamers.remove(player);
		
		if (gameData != null) { // Era presente
			
			GrPlayer grPlayer = GrPlayer.get(player);
			grPlayer.addKills(gameData.getKills()); // TODO possibile NullPointerException
			grPlayer.addDeaths(gameData.getDeaths());
			grPlayer.saveIfNecessaryAsync();
			
			GoldRush.playerMap.remove(player);
			tellAll(Lang.GOLDRUSH_PREFIX + "§7" + player.getName() + " è uscito " + "(§f" + gamers.size() + "§7/§f" + maxGamers + "§7)");
			
			if (state == GameState.PREGAME && gameTimer.isStarted()) { // Countdown iniziato
				if (gamers.size() < minGamers) {
					gameTimer.stopTask();
					defaultSign();
					tellAll(Lang.GOLDRUSH_PREFIX + "§cConto alla rovescia interrotto: pochi giocatori.");
				}
			}
			
			if (state == GameState.GAME && isThereFewPlayers()) {
				// Partita interrotta, pochi giocatori
				tellAll(Lang.GOLDRUSH_PREFIX + getEndedEarlyMessage());
				giveRewards(null, false, false);
				reset();
			}
			
			defaultSign();
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isThereFewPlayers() {
		// Se una delle due squadre è vuota
		return teamManager.getBlueSize() == 0 || teamManager.getRedSize() == 0;
	}
	
	protected boolean canVipJoinFull() {
		return true;
	}
	
	/**
	 * @return true se il giocatore entra con successo nell'arena.
	 */
	public boolean addGamer(Player player) {
		if (state != GameState.PREGAME) {
			player.sendMessage(Lang.ARENA_ALREADY_STARTED);
			return false;
		}
		
		if (gamers.size() >= maxGamers) {
			
			if (canVipJoinFull() && player.hasPermission(Permissions.JOIN_FULL)) {
				// Autorizzato
			} else {
				player.sendMessage(Lang.ARENA_FULL);
				return false;
			}
		}
		
		if (GoldRush.isPlaying(player)) {
			player.sendMessage("§cSei già in un'arena.");
			player.sendMessage("§cUsa il comando /spawn per uscire (non ricevi punti).");
			return false;
		}
		
		GoldRush.playerMap.put(player, this);
		gamers.put(player, new GameData());
		Utils.clearPlayer(player);
		player.getInventory().addItem(Items.TEAM_SELECTOR_RED);
		player.getInventory().addItem(Items.TEAM_SELECTOR_BLUE);
		player.updateInventory();
		
		if (isNight) {
			Utils.setNight(player);
		}
		player.teleport(lobby);
		player.setScoreboard(scoreboard); // Vuota al momento
		defaultSign();
		
		CosmeticsBridge.updateCosmetics(player, Status.GAME);
		
		tellAll(Lang.GOLDRUSH_PREFIX + "§7" + player.getName() + " è entrato " + "(§f" + gamers.size() + "§7/§f" + maxGamers + "§7)");
		
		if (state == GameState.PREGAME) {
			if (gamers.size() >= minGamers && !gameTimer.isStarted()) {
				// Avvia il timer se non già avviato
				gameTimer.startNewTask();
			}
		}
		
		if (gamers.size() == maxGamers && gameTimer.isStarted()) {
			if (gameTimer.getRemainingSeconds() > 15) {
				gameTimer.setRemainingSeconds(15);
				tellAll(Lang.GOLDRUSH_PREFIX + "§eL'arena è piena, conto alla rovescia ridotto!");
			}
		}
		return true;
	}
	
	
	public void handleRespawn(final PlayerRespawnEvent event) {
		if (state == GameState.GAME) {
			
			TeamColor color = teamManager.getTeamColor(event.getPlayer());
			if (color == TeamColor.BLUE) {
				event.setRespawnLocation(blueSpawn);
			} else if (color == TeamColor.RED) {
				event.setRespawnLocation(redSpawn);
			}
			
			giveEquip(event.getPlayer());
			
			PotionEffect resistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 7 * 20, 2);
			PotionEffect strength = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 7 * 20, 2);
			Bukkit.getScheduler().scheduleSyncDelayedTask(GoldRush.plugin, new GivePotionEffectTask(event.getPlayer(), resistance, strength));
			
		} else {
			event.setRespawnLocation(lobby);
		}
	}


	public void killEvent(Player killer, Player victim) {
		
		if (state != GameState.GAME) {
			Debug.color("§dGiocatore ucciso dopo la partita: " + killer.getName() + " uccide " + victim.getName());
			return;
		}
		
		if (killer == victim) {
			return;
		}
		getGameData(victim).addDeath();
		GameData killerGameData = getGameData(killer).addKill();
		awardCoinsCalculatingBonus(killer, killerGameData, Configuration.coinsKill);
		
		killer.sendMessage("§a» §8Hai ucciso §7" + victim.getName());
		victim.sendMessage("§c» §8Sei stato ucciso da §7" + killer.getName());
	}
	
	
	public void addGoldEvent(Player golder, @NonNull TeamColor color, int amount) {
		List<Player> teamPlayers = null;
		if (color == TeamColor.BLUE) {
			teamPlayers = teamManager.getBlueList();
		} else if (color == TeamColor.RED) {
			teamPlayers = teamManager.getRedList();
		}
		
		GameData golderData = getGameData(golder);
		awardCoinsCalculatingBonus(golder, golderData, amount * Configuration.coinsDeliver);
		golderData.addIngots(amount);
		
		for (Player teamPlayer : teamPlayers) {
			if (teamPlayer != golder) {
				awardCoinsCalculatingBonus(teamPlayer, getGameData(teamPlayer), amount * Configuration.coinsDeliverTeammates);
			}
		}
		
		tellAll(Lang.GOLDRUSH_PREFIX + "§7" + color.getChatColor() + golder.getName() + "§7 ha raccolto " + (amount == 1 ? "1 lingotto" : amount + " lingotti") + " per i " + color.getChatColor() + ChatColor.BOLD + (color == TeamColor.BLUE ? "Blu" : "Rossi"));
		teamManager.addGold(color, amount);
		
		EasySound winSound = new EasySound(Sound.NOTE_PLING, 1.7f);
		EasySound loseSound = new EasySound(Sound.NOTE_BASS, 0.9f);
		
		if (color == TeamColor.BLUE) {
			for (Player player : teamManager.getBlueList()) {
				winSound.playTo(player);
			}
			for (Player player : teamManager.getRedList()) {
				loseSound.playTo(player);
			}
			if (teamManager.getBlueGold() >= teamManager.getMaxScore()) {
				end(TeamColor.BLUE);
			}
		} else if (color == TeamColor.RED) {
			for (Player player : teamManager.getRedList()) {
				winSound.playTo(player);
			}
			for (Player player : teamManager.getBlueList()) {
				loseSound.playTo(player);
			}
			if (teamManager.getRedGold() >= teamManager.getMaxScore()) {
				end(TeamColor.RED);
			}
		}
	}

	
	public boolean canPreferTeam(Player player, @NonNull TeamColor color) {
		int maxPlayersPerTeamWithPreference = (gamers.size() / 2) + (gamers.size() % 2);
		int sameColorCount = 0;
		for (TeamColor preferredColor : teamPreferences.values()) {
			if (color == preferredColor) {
				sameColorCount++;
			}
		}
		
		return sameColorCount < maxPlayersPerTeamWithPreference;
	}
	
	
	public void giveEquip(Player gamer) {
		PlayerInventory inv = gamer.getInventory();
		TeamColor color = teamManager.getTeamColor(gamer);
		
		if (color == TeamColor.BLUE) {
			inv.setArmorContents(new ItemStack[] {Items.BLUE_BOOTS, Items.BLUE_LEGGINGS, Items.BLUE_BODY, Items.BLUE_HELMET});
			inv.setItem(8, Items.BLUE_WOOL);
			
		} else if (color == TeamColor.RED) {
			inv.setArmorContents(new ItemStack[] {Items.RED_BOOTS, Items.RED_LEGGINGS, Items.RED_BODY, Items.RED_HELMET});
			inv.setItem(8, Items.RED_WOOL);
		}
		
		inv.addItem(Items.DEFAULT_KIT);
	}
	
	
	public void awardCoinsCalculatingBonus(Player gamer, GameData gameData, int coins) {
		Booster booster = BoostersBridge.getActiveBooster(GoldRush.PLUGIN_ID);
		coins = BoostersBridge.applyMultiplier(coins, booster);
		
		gameData.addCoins(coins);
		gamer.sendMessage(ChatColor.GOLD + "+ " + coins + " Coins" + BoostersBridge.messageSuffix(booster));
	}
	
	
	public void setSignLines(String one, String two, String three, String four) {
		try {
			if (signBlock.getType() != Material.WALL_SIGN) {
				signBlock.setType(Material.WALL_SIGN);
			}
			Sign sign = (Sign) signBlock.getState();
			if (one != null) sign.setLine(0, one);
			if (two != null) sign.setLine(1, two);
			if (three != null) sign.setLine(2, three);
			if (four != null) sign.setLine(3, four);
			sign.update(true, false);
		} catch (Exception ex) {
			System.out.println("Impossibile leggere il cartello: " + signBlock.getLocation().toString() + " Arena: " + name);
		}
	}
	
	public void defaultSign() {
		defaultSign(gameTimer.isStarted() ? (ChatColor.GREEN + "[" + gameTimer.getFormattedTime() + "]") : state.getName());
	}
	
	public void defaultSign(String formattedTime) {
		setSignLines(
			"§l" + name,
			"----------------",
			formattedTime,
			"§8[" + gamers.size() + "/" + maxGamers + "]"
		);
	}
	
	
	public void removeSidebar() {
		Objective obj = scoreboard.getObjective(DisplaySlot.SIDEBAR);
		
		if (obj != null) {
			obj.setDisplaySlot(null);
		}
	}
	
	public void start() {
		
		teamManager.resetAndCreate();
		
		state = GameState.GAME;
		defaultSign();
		
		int maxPlayersPerTeamWithPreference = (gamers.size() / 2) + (gamers.size() % 2);
		LinkedHashMap<Player, TeamColor> gamersPreferences = new LinkedHashMap<>();
		
		for (Player player : gamers.keySet()) {
			TeamColor preferredColor = teamPreferences.get(player);
			if (preferredColor != null) {
				gamersPreferences.put(player, preferredColor);
			}
		}
		
		for (Player player : gamers.keySet()) {
			TeamColor preferredColor = teamPreferences.get(player);
			if (preferredColor == null) {
				gamersPreferences.put(player, null);
			}
		}
		
		for (Entry<Player, TeamColor> entry : gamersPreferences.entrySet()) {
			
			Player player = entry.getKey();
			TeamColor preferredColor = entry.getValue();
			TeamColor actualColor = null;
			
			if (preferredColor == null) {
				actualColor = teamManager.autoAssign(player);
			} else {
				
				if (preferredColor == TeamColor.RED) {
					if (teamManager.getRedList().size() < maxPlayersPerTeamWithPreference) {
						teamManager.addToRed(player);
						actualColor = TeamColor.RED;
					}
				} else if (preferredColor == TeamColor.BLUE) {
					if (teamManager.getRedList().size() < maxPlayersPerTeamWithPreference) {
						teamManager.addToBlue(player);
						actualColor = TeamColor.BLUE;
					}
				}
				
				if (actualColor == null) {
					actualColor = teamManager.autoAssign(player);
					player.sendMessage(Lang.GOLDRUSH_PREFIX + ChatColor.AQUA + "Non è stato possibile assegnarti al team, troppi giocatori avevano scelto il tuo stesso team.");
				}
			}
			
			if (player.isOnline()) {

				Utils.clearPlayer(player);
				giveEquip(player);

				Location spawn = null;
				if (actualColor == TeamColor.RED) {
					spawn = redSpawn;
				} else if (actualColor == TeamColor.BLUE) {
					spawn = blueSpawn;
				}
				player.teleport(spawn);
				Countdowns.announceEndedCountdown(Lang.GOLDRUSH_PREFIX, player);
				player.sendMessage(Lang.GOLDRUSH_PREFIX + "§cArriva a 20 lingotti d'oro per vincere!");
				
				if (player.hasPermission(Permissions.EXP_X1_5)) {
					player.sendMessage(Lang.GOLDRUSH_PREFIX + "§eDurante la partita ricevi §a+50% di esperienza §egrazie al tuo rango.");
				} else if (player.hasPermission(Permissions.EXP_X1_25)) {
					player.sendMessage(Lang.GOLDRUSH_PREFIX + "§eDurante la partita ricevi §a+25% di esperienza §egrazie al tuo rango.");
				}
			}
			
			player.setScoreboard(scoreboard);
			WildConstants.Sounds.COUNTDOWN_FINISH.playTo(player);
		}
	}
	
	public void end(TeamColor winnerTeam) {
		
		state = GameState.END;
		giveRewards(winnerTeam, false, true);
		
		for (Player gamer : gamers.keySet()) {
			if (gamer.isDead()) {
				gamer.spigot().respawn();
			}
			Utils.clearPlayer(gamer);
		}

		clearMobsAndExp();
		final BukkitScheduler scheduler = Bukkit.getScheduler();
		
		if (winnerTeam != null) {

			soundAll(Sound.LEVEL_UP);
			Bukkit.broadcastMessage(Lang.GOLDRUSH_PREFIX + "§fI " + winnerTeam.getChatColor() + "§l" + winnerTeam.getPlural() + "§f hanno vinto nell'arena " + name);
				
			for (Player player : gamers.keySet()) {
				player.sendMessage(new String[] {
						"",
						Lang.GRAY_LINE_SEPARATOR,
						winnerTeam.getChatColor() + "§lVincitori:§f " + winnerTeam.getPlural(),
						"",
						"§6§lCoins Guadagnati:§7 " + getGameData(player).getCoins(),
						"§a§lUccisioni:§7 " + getGameData(player).getKills(),
						"§a§lMorti:§7 " + getGameData(player).getDeaths(),
						"§a§lMappa:§7 " + this.name,
						Lang.GRAY_LINE_SEPARATOR,
						""
				});
			}
	
			Location fireworkLoc = null;
			if (winnerTeam == TeamColor.BLUE) {
				fireworkLoc = blueSpawn.clone();
			} else if (winnerTeam == TeamColor.RED) {
				fireworkLoc = redSpawn.clone();
			}
			fireworkLoc.add(0, 1, 0);
			final int fireworkTaskId = scheduler.scheduleSyncRepeatingTask(GoldRush.plugin, new FireworksTask(fireworkLoc), 0L, 10L);
		
			scheduler.scheduleSyncDelayedTask(GoldRush.plugin, new Runnable() {
				@Override
				public void run() {
					scheduler.cancelTask(fireworkTaskId);
				}
			}, 260L);
			
			scheduler.scheduleSyncDelayedTask(GoldRush.plugin, new Runnable() {
				@Override
				public void run() {
					reset();
				}
			}, 300L);
		
		} else {
			reset();
		}
	}
	
	
	public void giveRewards(TeamColor winningTeam, boolean syncro, boolean checkMinKills) {
		for (Player player : gamers.keySet()) {
			GrPlayer grPlayer = GrPlayer.get(player);
			GameData gameData = getGameData(player);
			
			boolean skipCoins = checkMinKills && (gameData.getKills() < 3 && gameData.getPigmenKilled() < 5 && gameData.getGoldIngots() < 1);
			
			if (skipCoins) {
				player.sendMessage(ChatColor.RED + "Non hai ricevuto i Coins e la vittoria per questa partita, perché la tua partecipazione è stata troppo scarsa (devi uccidere almeno 3 giocatori o 5 pigman zombie, oppure consegnare almeno un lingotto d'oro).");
				gameData.setCoins(0);
			}

			if (winningTeam != null && winningTeam == teamManager.getTeamColor(player)) {
				
				if (!skipCoins) {
					grPlayer.addWins(1);
					awardCoinsCalculatingBonus(player, gameData, Configuration.coinsWin);
				}
			}
			
			grPlayer.addCoins(gameData.getCoins()); // TODO possibile NullPointerException
			player.sendMessage(ChatColor.GOLD + "Hai guadagnato " + gameData.getCoins() + " Coins");
			
			grPlayer.addKills(gameData.getKills());
			grPlayer.addDeaths(gameData.getDeaths());

			if (syncro)	{
				grPlayer.saveIfNecessary();
			} else {
				grPlayer.saveIfNecessaryAsync();
			}
		}
	}
	
	
	public GameData getGameData(Player player) {
		return gamers.get(player);
	}

	public int getCountdownSeconds() {
		return Configuration.countDown;
	}
	
	public String getEndedEarlyMessage() {
		return "§cSono usciti troppi giocatori, la partita finisce senza vincitore.";
	}
	
	
	public void tellAll(String broadcast) {
		for (Player player : gamers.keySet()) {
			player.sendMessage(broadcast);
		}
	}
	
	public void tellAll(String[] broadcast) {
		for (Player player : gamers.keySet()) {
			player.sendMessage(broadcast);
		}
	}
	
	public void soundAll(Sound sound) {
		for (Player player : gamers.keySet()) {
			EasySound.quickPlay(player, sound);
		}
	}
}
