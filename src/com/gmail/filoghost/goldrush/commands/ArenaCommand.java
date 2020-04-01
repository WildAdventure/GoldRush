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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.goldrush.GoldRush;
import com.gmail.filoghost.goldrush.Serializer;
import com.gmail.filoghost.goldrush.constants.ConfigNodes;
import com.gmail.filoghost.goldrush.constants.Items;
import com.gmail.filoghost.goldrush.constants.Lang;
import com.gmail.filoghost.goldrush.constants.Permissions;
import com.gmail.filoghost.goldrush.enums.TeamColor;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import wild.api.command.CommandFramework;
import wild.api.command.CommandFramework.Permission;

@Permission(Permissions.ARENA_COMMAND)
public class ArenaCommand extends CommandFramework {
	
	
	private static FileConfiguration creatingArena;
	private static File creatingArenaFile;
	
	
	public ArenaCommand(JavaPlugin plugin, String label) {
		super(plugin, label);
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		
		if (args.length < 1) {
			player.sendMessage("§6========== Comandi /arena ==========");
			player.sendMessage("§e/arena new <nome>§7 - Seleziona l'arena da modificare");
			player.sendMessage("§e/arena setPlayers <min> <max>§7 - Imposta il numero di giocatori");
			player.sendMessage("§e/arena setSign§7 - Imposta il cartello dell'arena");
			player.sendMessage("§e/arena setLobby§7 - Setta la lobby di attesa");
			player.sendMessage("§e/arena setSpawn <red|blue>§7 - Setta lo spawn rosso o blu");
			player.sendMessage("§e/arena setPot <red|blue>§7 - Raccoglitore per l'oro (dispenser)");
			player.sendMessage("§e/arena setVillager <red|blue>§7 - Villico per scambi");
			player.sendMessage("§e/arena setPadTo <red|blue>§7 - Destinazione teletrasporto");
			player.sendMessage("§e/arena addPadFrom <red|blue>§7 - Aggiunge pulsante o pedana teletrasporto");
			player.sendMessage("§e/arena removePadFrom <red|blue> <n>§7 - Rimuove pulsante o pedana teletrasporto");
			player.sendMessage("§e/arena addExp§7 - Aggiunge spawner di exp (blocco di smeraldi)");
			player.sendMessage("§e/arena removeExp <n>§7 - Rimuove uno spawner di exp");
			player.sendMessage("§e/arena addMob§7 - Aggiunge spawner di mob (blocco d'oro)");
			player.sendMessage("§e/arena removeMob <n>§7 - Rimuove uno spawner di mob");
			player.sendMessage("§e/arena info§7 - Status dell'arena selezionata");
			player.sendMessage("§e/arena save§7 - Salva su disco");
			player.sendMessage("§e/arena load <file>§7 - Carica l'arena da disco");
			player.sendMessage("§e/arena edit <file>§7 - Modifica l'arena salvata");
			player.sendMessage("§7(I blocchi d'oro e di smeraldi possono essere tolti dopo averli settati)");
			return;
		}
		
	
		
		
		if (args[0].equalsIgnoreCase("new")) {
			
			CommandValidate.minLength(args, 2, "Utilizzo corretto: /" + label + " new <nome>");
			CommandValidate.isTrue(args[1].length() >= 3, "Il nome deve essere almeno di 3 caratteri.");
			
			String name = WordUtils.capitalize(args[1]);
			
			try {
				creatingArenaFile = new File(GoldRush.plugin.getDataFolder(), Lang.ARENAS_FOLDER + File.separator + name.toLowerCase() + ".yml");
				creatingArena = makeDefault(name);
				creatingArena.set(ConfigNodes.NAME, name);
				player.sendMessage(ChatColor.GREEN + "Inizio fase di creazione per l'arena \"" + name + "\".");
				player.sendMessage(ChatColor.GREEN + "Procedi con /arena setPlayers <min> <max>.");
			} catch (Exception ex) {
				ex.printStackTrace();
				player.sendMessage(ChatColor.RED + "Errore nel creare l'arena. Guarda la console.");
			}
			
			return;
		}
		

		
		if (args[0].equalsIgnoreCase("setPlayers")) {
			CommandValidate.notNull(creatingArena, "Prima devi creare un arena con /arena new <nome>");
			CommandValidate.minLength(args, 3, "Utilizzo corretto: /" + label + " setPlayers <min> <max>");
			int min = CommandValidate.getPositiveInteger(args[1]);
			int max = CommandValidate.getPositiveInteger(args[2]);
			CommandValidate.isTrue(min >= 2 && min <= 30, "Il minimo deve essere tra 2 e 30");
			CommandValidate.isTrue(max >= 2 && max <= 30, "Il massimo deve essere tra 2 e 30");
			CommandValidate.isTrue(min <= max, "Il minimo dei giocatori è maggiore del massimo!");
			creatingArena.set(ConfigNodes.PLAYERS_MIN, min);
			creatingArena.set(ConfigNodes.PLAYERS_MAX, max);
			player.sendMessage(ChatColor.GREEN + "Impostato il numero di giocatori: da " + min + " a " + max + ".");
			player.sendMessage(ChatColor.GREEN + "Procedi con /arena setLobby.");
			return;
		}


		if (args[0].equalsIgnoreCase("setLobby")) {
			CommandValidate.notNull(creatingArena, "Prima devi creare un arena con /arena new <nome>");
			String serLoc = Serializer.locationToString(player.getLocation());
			
			creatingArena.set(ConfigNodes.LOBBY, serLoc);

			player.sendMessage(ChatColor.GREEN + "Hai settato il punto di spawn per la lobby di attesa.");
			player.sendMessage(ChatColor.GREEN + "Procedi con /arena setSign.");
			return;
		}
		
		
		
		
		if (args[0].equalsIgnoreCase("setSign")) {
			CommandValidate.notNull(creatingArena, "Prima devi creare un arena con /arena new <nome>");
			CommandValidate.notNull(creatingArena.getString(ConfigNodes.NAME), "Devi inserire un nome valido");
			
			Block block = player.getTargetBlock((Set<Material>) null, 64);
			CommandValidate.isTrue(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST, "Non stai guardando un cartello! Stai guardando " + block.getType());
			
			creatingArena.set(ConfigNodes.SIGN_X, block.getX());
			creatingArena.set(ConfigNodes.SIGN_Y, block.getY());
			creatingArena.set(ConfigNodes.SIGN_Z, block.getZ());
			
			player.sendMessage("§aAggiunto il cartello.");
			player.sendMessage(ChatColor.GREEN + "Procedi con:");
			player.sendMessage(ChatColor.GREEN + "- /arena setSpawn <red|blue>");
			player.sendMessage(ChatColor.GREEN + "- /arena setPot <red|blue>");
			player.sendMessage(ChatColor.GREEN + "- /arena setVillager <red|blue>");
			player.sendMessage(ChatColor.GREEN + "- /arena addExp");
			player.sendMessage(ChatColor.GREEN + "- /arena addMob");
			return;
		}
		
		
		
		
		
		if (args[0].equalsIgnoreCase("setSpawn")) {
			CommandValidate.notNull(creatingArena, "Prima devi creare un arena con /arena new <nome>");
			CommandValidate.minLength(args, 2, "Utilizzo corretto: /" + label + " setSpawn <red|blue>");
			TeamColor color;
			
			try {
				color = TeamColor.valueOf(args[1].toUpperCase());
			} catch (IllegalArgumentException ex) {
				throw new ExecuteException("Il team deve essere \"red\" o \"blue\".");
			}
			
			String serLoc = Serializer.locationToString(player.getLocation());
			
			if (color == TeamColor.BLUE) {
				creatingArena.set(ConfigNodes.BLUE_SPAWNPOINT, serLoc);
			} else if (color == TeamColor.RED) {
				creatingArena.set(ConfigNodes.RED_SPAWNPOINT, serLoc);
			}
			
			player.sendMessage(ChatColor.GREEN + "Settato lo spawnpoint per il team \"" + color.toString().toLowerCase() + "\".");
			return;
		}
		
		
		if (args[0].equalsIgnoreCase("setVillager")) {
			CommandValidate.notNull(creatingArena, "Prima devi creare un arena con /arena new <nome>");
			CommandValidate.minLength(args, 2, "Utilizzo corretto: /" + label + " setVillager <red|blue>");
			TeamColor color;
			
			try {
				color = TeamColor.valueOf(args[1].toUpperCase());
			} catch (IllegalArgumentException ex) {
				throw new ExecuteException("Il team deve essere \"red\" o \"blue\".");
			}
			
			String serLoc = Serializer.locationToString(player.getLocation());
			
			if (color == TeamColor.BLUE) {
				creatingArena.set(ConfigNodes.BLUE_VILLAGER, serLoc);
			} else if (color == TeamColor.RED) {
				creatingArena.set(ConfigNodes.RED_VILLAGER, serLoc);
			}
			
			player.sendMessage(ChatColor.GREEN + "Settato la posizione del villico per il team \"" + color.toString().toLowerCase() + "\".");
			return;
		}
		
		
		if (args[0].equalsIgnoreCase("setPot")) {
			CommandValidate.notNull(creatingArena, "Prima devi creare un arena con /arena new <nome>");
			CommandValidate.minLength(args, 2, "Utilizzo corretto: /" + label + " setPot <red|blue>");
			
			Block block = player.getTargetBlock((Set<Material>) null, 64);
			CommandValidate.isTrue(block.getType() == Material.DISPENSER, "Non stai guardando un dispenser! Stai guardando " + block.getType());
			
			TeamColor color;
			
			try {
				color = TeamColor.valueOf(args[1].toUpperCase());
			} catch (IllegalArgumentException ex) {
				throw new ExecuteException("Il team deve essere \"red\" o \"blue\".");
			}
			
			String serBlock = Serializer.blockToString(block);
			
			if (color == TeamColor.BLUE) {
				creatingArena.set(ConfigNodes.BLUE_POT, serBlock);
			} else if (color == TeamColor.RED) {
				creatingArena.set(ConfigNodes.RED_POT, serBlock);
			}
			
			player.sendMessage(ChatColor.GREEN + "Settato il raccoglitore d'oro per il team \"" + color.toString().toLowerCase() + "\".");
			return;
		}
		
		
		
		if (args[0].equalsIgnoreCase("setPadTo")) {
			CommandValidate.notNull(creatingArena, "Prima devi creare un arena con /arena new <nome>");
			CommandValidate.minLength(args, 2, "Utilizzo corretto: /" + label + " setPadTo <red|blue>");
			
			Location loc = player.getLocation();
			TeamColor color;
			
			try {
				color = TeamColor.valueOf(args[1].toUpperCase());
			} catch (IllegalArgumentException ex) {
				throw new ExecuteException("Il team deve essere \"red\" o \"blue\".");
			}
			
			String serLoc = Serializer.locationToString(loc);
			
			if (color == TeamColor.BLUE) {
				creatingArena.set(ConfigNodes.BLUE_PAD_TO, serLoc);
			} else if (color == TeamColor.RED) {
				creatingArena.set(ConfigNodes.RED_PAD_TO, serLoc);
			}
			
			player.sendMessage(ChatColor.GREEN + "Settato il teletrasporto di arrivo per il team \"" + color.toString().toLowerCase() + "\" alla tua posizione attuale.");
			return;
		}
		
		
		if (args[0].equalsIgnoreCase("addPadFrom")) {
			CommandValidate.notNull(creatingArena, "Prima devi creare un arena con /arena new <nome>");
			CommandValidate.minLength(args, 2, "Utilizzo corretto: /" + label + " addPadFrom <red|blue>");
			
			TeamColor color;
			
			try {
				color = TeamColor.valueOf(args[1].toUpperCase());
			} catch (IllegalArgumentException ex) {
				throw new ExecuteException("Il team deve essere \"red\" o \"blue\".");
			}
			
			Block block = player.getTargetBlock((Set<Material>) null, 64);
			CommandValidate.isTrue(Items.ALLOWED_FROM_PADS.contains(block.getType()), "Devi usare uno di questi materiali: " + Joiner.on(", ").join(Items.ALLOWED_FROM_PADS) + ". Stai guardando: " + block.getType());
			
			String serBlock = Serializer.blockToString(block);
			
			List<String> fromPads = null;
			if (color == TeamColor.BLUE) {
				fromPads = creatingArena.getStringList(ConfigNodes.BLUE_PADS_FROM);
			} else if (color == TeamColor.RED) {
				fromPads = creatingArena.getStringList(ConfigNodes.RED_PADS_FROM);
			}
			
			if (fromPads == null) {
				fromPads = Lists.newArrayList();
			}
			
			fromPads.add(serBlock);
			
			if (color == TeamColor.BLUE) {
				creatingArena.set(ConfigNodes.BLUE_PADS_FROM, fromPads);
			} else if (color == TeamColor.RED) {
				creatingArena.set(ConfigNodes.RED_PADS_FROM, fromPads);
			}
			
			player.sendMessage(ChatColor.GREEN + "Aggiunto il punto di teletrasporto n." + fromPads.size() + " per il team " + color.toString().toLowerCase() + ".");
			return;
		}
		
		
		
		if (args[0].equalsIgnoreCase("removePadFrom")) {
			CommandValidate.notNull(creatingArena, "Prima devi creare un arena con /arena new <nome>");
			CommandValidate.minLength(args, 3, "Utilizzo corretto: /" + label + " removePadFrom <red|blue> <numero>");
			
			TeamColor color;
			
			try {
				color = TeamColor.valueOf(args[1].toUpperCase());
			} catch (IllegalArgumentException ex) {
				throw new ExecuteException("Il team deve essere \"red\" o \"blue\".");
			}
			
			int index = CommandValidate.getPositiveInteger(args[2]) - 1;
			
			List<String> fromPads = null;
			if (color == TeamColor.BLUE) {
				fromPads = creatingArena.getStringList(ConfigNodes.BLUE_PADS_FROM);
			} else if (color == TeamColor.RED) {
				fromPads = creatingArena.getStringList(ConfigNodes.RED_PADS_FROM);
			}
			
			CommandValidate.isTrue(fromPads != null && !fromPads.isEmpty(), "Devi prima settare almeno un teletrasporto.");
			CommandValidate.isTrue(index < fromPads.size(), "Non esiste quel teletrasporto (lista: /" + label + " info).");
			
			fromPads.remove(index);
			if (color == TeamColor.BLUE) {
				creatingArena.set(ConfigNodes.BLUE_PADS_FROM, fromPads);
			} else if (color == TeamColor.RED) {
				creatingArena.set(ConfigNodes.RED_PADS_FROM, fromPads);
			}
			player.sendMessage("§aRimosso teletrasporto n." + (index + 1) + " per il team " + color.toString().toLowerCase() + ".");
			return;
		}
		
		
		
		
		if (args[0].equalsIgnoreCase("addExp")) {
			Block block = player.getTargetBlock((Set<Material>) null, 64);
			CommandValidate.isTrue(block.getType() == Material.EMERALD_BLOCK, "Non stai guardando un blocco di smeraldi! Stai guardando " + block.getType());
			
			List<String> expSpawners = creatingArena.getStringList(ConfigNodes.EXP_SPAWNERS);
			if (expSpawners == null) {
				expSpawners = Lists.newArrayList();
			}
			
			String serBlock = Serializer.blockToString(block);
			CommandValidate.isTrue(!expSpawners.contains(serBlock), "Questo blocco è già un exp spawner!");
			expSpawners.add(serBlock);
			creatingArena.set(ConfigNodes.EXP_SPAWNERS, expSpawners);
			
			player.sendMessage(ChatColor.GREEN + "Aggiunto l'exp spawner n." + expSpawners.size() + ".");
			return;
		}
		
		
		
		
		if (args[0].equalsIgnoreCase("addMob")) {
			Block block = player.getTargetBlock((Set<Material>) null, 64);
			CommandValidate.isTrue(block.getType() == Material.GOLD_BLOCK, "Non stai guardando un blocco d'oro! Stai guardando " + block.getType());
			
			List<String> mobSpawners = creatingArena.getStringList(ConfigNodes.MOB_SPAWNERS);
			if (mobSpawners == null) {
				mobSpawners = Lists.newArrayList();
			}
			
			String serBlock = Serializer.blockToString(block);
			CommandValidate.isTrue(!mobSpawners.contains(serBlock), "Questo blocco è già un mob spawner!");
			mobSpawners.add(serBlock);
			creatingArena.set(ConfigNodes.MOB_SPAWNERS, mobSpawners);
			
			player.sendMessage(ChatColor.GREEN + "Aggiunto il mob spawner n." + mobSpawners.size() + ".");
			return;
		}
			
		
		
		
		if (args[0].equalsIgnoreCase("removeExp")) {
			CommandValidate.notNull(creatingArena, "Prima devi creare un arena con /arena new <nome>");
			CommandValidate.minLength(args, 2, "Utilizzo corretto: /" + label + " removeExp <numero>");
			
			int index = CommandValidate.getPositiveInteger(args[1]) - 1;
			
			List<String> expSpawners = creatingArena.getStringList(ConfigNodes.EXP_SPAWNERS);
			CommandValidate.isTrue(expSpawners != null && !expSpawners.isEmpty(), "Devi prima settare almeno un exp spawner.");
			CommandValidate.isTrue(index < expSpawners.size(), "Non esiste quell'exp spawner (lista: /" + label + " info).");
			
			expSpawners.remove(index);
			creatingArena.set(ConfigNodes.EXP_SPAWNERS, expSpawners);
			player.sendMessage("§aRimosso exp spawner n." + (index + 1) + ".");
			return;
		}
			
		
		
		if (args[0].equalsIgnoreCase("removeMob")) {
			CommandValidate.notNull(creatingArena, "Prima devi creare un arena con /arena new <nome>");
			CommandValidate.minLength(args, 2, "Utilizzo corretto: /" + label + " removeMob <numero>");
			
			int index = CommandValidate.getPositiveInteger(args[1]) - 1;
			
			List<String> mobSpawners = creatingArena.getStringList(ConfigNodes.MOB_SPAWNERS);
			CommandValidate.isTrue(mobSpawners != null && !mobSpawners.isEmpty(), "Devi prima settare almeno un mob spawner.");
			CommandValidate.isTrue(index < mobSpawners.size(), "Non esiste quel mob spawner (lista: /" + label + " info).");
			
			mobSpawners.remove(index);
			creatingArena.set(ConfigNodes.MOB_SPAWNERS, mobSpawners);
			player.sendMessage("§aRimosso mob spawner n." + (index + 1) + ".");
			return;
		}
		
		
		
		if (args[0].equalsIgnoreCase("info")) {
			CommandValidate.notNull(creatingArena, "Prima devi creare un arena con /arena new <nome>");
			
			String name = creatingArena.getString(ConfigNodes.NAME);
			int min = creatingArena.getInt(ConfigNodes.PLAYERS_MIN);
			int max = creatingArena.getInt(ConfigNodes.PLAYERS_MAX);
			int signX = creatingArena.getInt(ConfigNodes.SIGN_X);
			int signY = creatingArena.getInt(ConfigNodes.SIGN_Y);
			int signZ = creatingArena.getInt(ConfigNodes.SIGN_Z);
			
			String lobby = creatingArena.getString(ConfigNodes.LOBBY);
			String redSpawn = creatingArena.getString(ConfigNodes.RED_SPAWNPOINT);
			String blueSpawn = creatingArena.getString(ConfigNodes.BLUE_SPAWNPOINT);
			String redVillager = creatingArena.getString(ConfigNodes.RED_VILLAGER);
			String blueVillager = creatingArena.getString(ConfigNodes.BLUE_VILLAGER);
			String redPot = creatingArena.getString(ConfigNodes.RED_POT);
			String bluePot = creatingArena.getString(ConfigNodes.BLUE_POT);
			
			String redPadTo = creatingArena.getString(ConfigNodes.RED_PAD_TO);
			String bluePadTo = creatingArena.getString(ConfigNodes.BLUE_PAD_TO);
			List<String> redPadsFrom = creatingArena.getStringList(ConfigNodes.RED_PADS_FROM);
			List<String> bluePadsFrom = creatingArena.getStringList(ConfigNodes.BLUE_PADS_FROM);
			
			List<String> expSpawners = creatingArena.getStringList(ConfigNodes.EXP_SPAWNERS);
			List<String> mobSpawners = creatingArena.getStringList(ConfigNodes.MOB_SPAWNERS);
			
			player.sendMessage("§8==========[ §dInformazioni Arena §8]==========");
			
			if (name != null && name.length() > 0) {
				player.sendMessage("§aNome: §7" + name);
			} else {
				player.sendMessage("§aNome: §cnon impostato");
			}
			
			if (min != 0 && max != 0) {
				player.sendMessage("§aGiocatori: §7da " + min + " a " + max);
			} else {
				player.sendMessage("§aGiocatori: §cnon impostato");
			}
			
			
			if (!(signX == 0 && signY == 0 && signZ == 0)) {
				player.sendMessage("§aCoordinate cartello: §7" + signX + ", " + signY + ", " + signZ);
			} else {
				player.sendMessage("§aCoordinate cartello: §cnon impostato");
			}
			
			player.sendMessage("§aLobby attesa: §7" + (lobby != null ? lobby : "§cnon impostato"));
			player.sendMessage("§aSpawn rosso: §7" + (redSpawn != null ? redSpawn : "§cnon impostato"));
			player.sendMessage("§aSpawn blu: §7" + (blueSpawn != null ? blueSpawn : "§cnon impostato"));
			
			player.sendMessage("§aVillager rosso: §7" + (redVillager != null ? redVillager : "§cnon impostato"));
			player.sendMessage("§aVillager blu: §7" + (blueVillager != null ? blueVillager : "§cnon impostato"));

			player.sendMessage("§aRaccoglitore rosso: §7" + (redPot != null ? redPot : "§cnon impostato"));
			player.sendMessage("§aRaccoglitore blu: §7" + (bluePot != null ? bluePot : "§cnon impostato"));
			
			player.sendMessage("§aPad rosso di arrivo: §7" + (redPadTo != null ? redPadTo : "§cnon impostato"));
			player.sendMessage("§aPad blu di arrivo: §7" + (bluePadTo != null ? bluePadTo : "§cnon impostato"));

			if (redPadsFrom != null && redPadsFrom.size() > 0) {
				player.sendMessage("§aPad rossi di partenza (mondo, x, y, z): ");
				int listSize = redPadsFrom.size();
				for (int i = 0; i < listSize; i++) {
					player.sendMessage("§f" + (i+1) + ")§7   " + redPadsFrom.get(i).replace(",", "§f,§7 "));
				}
			} else {
				player.sendMessage("§aPad rossi di partenza: §cnon impostati");
			}
			
			if (bluePadsFrom != null && bluePadsFrom.size() > 0) {
				player.sendMessage("§aPad blu di partenza (mondo, x, y, z): ");
				int listSize = bluePadsFrom.size();
				for (int i = 0; i < listSize; i++) {
					player.sendMessage("§f" + (i+1) + ")§7   " + bluePadsFrom.get(i).replace(",", "§f,§7 "));
				}
			} else {
				player.sendMessage("§aPad blu di partenza: §cnon impostati");
			}
			
			if (expSpawners != null && expSpawners.size() > 0) {
				player.sendMessage("§aExp spawners (mondo, x, y, z): ");
				int listSize = expSpawners.size();
				for (int i = 0; i < listSize; i++) {
					player.sendMessage("§f" + (i+1) + ")§7   " + expSpawners.get(i).replace(",", "§f,§7 "));
				}
			} else {
				player.sendMessage("§aExp spawners: §cnon impostati");
			}
			
			if (mobSpawners != null && mobSpawners.size() > 0) {
				player.sendMessage("§aMob spawners (mondo, x, y, z): ");
				int listSize = mobSpawners.size();
				for (int i = 0; i < listSize; i++) {
					player.sendMessage("§f" + (i+1) + ")§7   " + mobSpawners.get(i).replace(",", "§f,§7 "));
				}
			} else {
				player.sendMessage("§aMob spawners: §cnon impostati");
			}
			return;
		}
		
		
		
		
		
		
		if (args[0].equalsIgnoreCase("save")) {
			CommandValidate.notNull(creatingArena, "Prima devi creare un arena con /arena new <nome>");
			CommandValidate.notNull(creatingArena.getString(ConfigNodes.NAME), "Non hai dato un nome all'arena!");
			CommandValidate.isTrue(creatingArena.getInt(ConfigNodes.PLAYERS_MIN) > 0 && creatingArena.getInt(ConfigNodes.PLAYERS_MAX) > 0, "Non hai impostato il numero di giocatori!");
			CommandValidate.isTrue(creatingArena.getInt(ConfigNodes.SIGN_X) != 0 && creatingArena.getInt(ConfigNodes.SIGN_Y) != 0 && creatingArena.getInt(ConfigNodes.SIGN_Z) != 0 , "Non hai impostato il cartello!");
			CommandValidate.notNull(creatingArena.getString(ConfigNodes.LOBBY), "Non hai settato la lobby di attesa!");
			CommandValidate.notNull(creatingArena.getString(ConfigNodes.RED_SPAWNPOINT), "Non hai settato lo spawn rosso!");
			CommandValidate.notNull(creatingArena.getString(ConfigNodes.BLUE_SPAWNPOINT), "Non hai settato lo spawn blu!");
			CommandValidate.notNull(creatingArena.getString(ConfigNodes.RED_POT), "Non hai settato il raccoglitore d'oro rosso!");
			CommandValidate.notNull(creatingArena.getString(ConfigNodes.BLUE_POT), "Non hai settato il raccoglitore d'oro blu!");
			CommandValidate.notNull(creatingArena.getString(ConfigNodes.RED_VILLAGER), "Non hai settato il villico rosso!");
			CommandValidate.notNull(creatingArena.getString(ConfigNodes.BLUE_VILLAGER), "Non hai settato il villico blu!");
			CommandValidate.notNull(creatingArena.getString(ConfigNodes.RED_PAD_TO), "Non hai settato il teletrasporto rosso di arrivo!");
			CommandValidate.notNull(creatingArena.getString(ConfigNodes.BLUE_PAD_TO), "Non hai settato il teletrasporto blu di arrivo!");
			CommandValidate.isTrue(!creatingArena.getStringList(ConfigNodes.RED_PADS_FROM).isEmpty(), "Serve almeno 1 teletrasporto di partenza rosso.");
			CommandValidate.isTrue(!creatingArena.getStringList(ConfigNodes.BLUE_PADS_FROM).isEmpty(), "Serve almeno 1 teletrasporto di partenza blu.");
			CommandValidate.isTrue(!creatingArena.getStringList(ConfigNodes.EXP_SPAWNERS).isEmpty(), "Serve almeno 1 spawner di exp.");
			CommandValidate.isTrue(!creatingArena.getStringList(ConfigNodes.MOB_SPAWNERS).isEmpty(), "Serve almeno 1 spawner di mob.");
			
			try {
				creatingArena.save(creatingArenaFile);
				player.sendMessage(ChatColor.GREEN + "Arena salvata con successo!");
				player.sendMessage(ChatColor.GREEN + "Per caricarla scrivi /arena load " + creatingArena.getString(ConfigNodes.NAME).toLowerCase() + ".yml");
			} catch (IOException e) {
				e.printStackTrace();
				player.sendMessage(ChatColor.RED + "Arena non salvata (errore I/O). Guarda la console.");
			}
			
			return;
		}
		
		
		
		
		
		
		if (args[0].equalsIgnoreCase("edit")) {
			CommandValidate.minLength(args, 2, "Utilizzo corretto: /" + label + " edit <file>");
			String fileName = args[1].toLowerCase();
			if (!fileName.endsWith(".yml")) {
				fileName += ".yml";
			}
			
			File file = new File(GoldRush.plugin.getDataFolder(), Lang.ARENAS_FOLDER + File.separator + fileName);
			CommandValidate.isTrue(file.exists(), "Il file non è stato trovato o caricato correttamente.");
			creatingArena = YamlConfiguration.loadConfiguration(file);
			creatingArenaFile = file;
			player.sendMessage("§aCaricato il file \"" + args[1].toLowerCase() + ".yml\".");
			return;
		}
		
		
		
		
		
		if (args[0].equalsIgnoreCase("load")) {
			CommandValidate.minLength(args, 2, "Utilizzo corretto: /" + label + " load <file>");
			String fileName = args[1].toLowerCase();
			if (!fileName.endsWith(".yml")) {
				fileName += ".yml";
			}
			File file = new File(GoldRush.plugin.getDataFolder(), Lang.ARENAS_FOLDER + File.separator + fileName);
			
			CommandValidate.isTrue(file.exists(), "Il file specificato non esiste.");
			CommandValidate.isTrue(file.isFile(), "Il file è una cartella.");
			
			try {
				GoldRush.loadArena(file, sender);
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage(ChatColor.RED + "Impossibile caricare l'arena. Guarda la console.");
			}
			return;
		}
		
		

		player.sendMessage(ChatColor.RED + "Comando non trovato. Scrivi /" + label + " per la lista dei comandi.");
		
	}
	
	private static FileConfiguration makeDefault(String name) {
		YamlConfiguration config = new YamlConfiguration();
		config.set(ConfigNodes.NAME, name);
		config.set(ConfigNodes.SIGN_X, 0);
		config.set(ConfigNodes.SIGN_Y, 0);
		config.set(ConfigNodes.SIGN_Z, 0);
		config.set(ConfigNodes.EXP_SPAWNERS, new ArrayList<String>());
		config.set(ConfigNodes.MOB_SPAWNERS, new ArrayList<String>());
		config.set(ConfigNodes.RED_PADS_FROM, new ArrayList<String>());
		config.set(ConfigNodes.BLUE_PADS_FROM, new ArrayList<String>());
		return config;
	}
}
