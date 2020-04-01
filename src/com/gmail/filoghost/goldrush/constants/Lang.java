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
package com.gmail.filoghost.goldrush.constants;

import org.bukkit.ChatColor;

public final class Lang {

	public static final String GOLDRUSH_PREFIX = 			"§7[§6§lGoldRush§7] ";
	public static final String NO_PERMISSION = 				"§cNon hai il permesso per questo comando.";
	
	public static final String ARENA_ALREADY_STARTED = 		"§cLa partita è già iniziata.";
	public static final String ARENA_FULL = 				"§cL'arena è piena.";
	public static final String PLAYER_ONLY_COMMAND = 		"§cSolo i giocatori possono usare questo comando.";
	
	public static final String TELEPORTED_TO_SPAWN = 		"§a§oSei stato teletrasportato allo spawn.";
	public static final String UPGRADE_BOUGHT = 			"§aHai acquistato l'oggetto!";
	public static final String UPGRADE_ALREADY_BOUGHT =		"§cHai già acquistato l'oggetto.";
	public static final String UPGRADE_ALREADY_EQUIP =		"§cHai già equipaggiato l'oggetto.";
	public static final String UPGRADE_EQUIP =				"§aHai equipaggiato l'oggetto.";
	public static final String NO_MONEY =					"§cNon hai abbastanza Coins.";
	public static final String UPGRADE_NOT_OWNED =			"§cNon possiedi questo oggetto.";
	public static final String UPGRADE_UNEQUIP =			"§eHai de-equipaggiato l'oggetto.";
	
	public static final String ARENA_PLAYER_PROTECTED = 	"§c§oIl giocatore è ancora protetto.";
	
	public static final String ARENAS_FOLDER = 				"arenas";
	public static final String PLAYERS_FOLDER = 			"players";
	
	public static final String SIDEBAR_RED_TEAM = 			"" + ChatColor.RED + ChatColor.BOLD + "Rossi";
	public static final String SIDEBAR_BLUE_TEAM = 			"" + ChatColor.BLUE + ChatColor.BOLD + "Blu";
	public static final String OBJECTIVE_CLASSIFICA_NAME = 	"    §6§lLingotti Raccolti§r    ";
	public static final String OBJECTIVE_STATISTICHE_NAME = "     " + ChatColor.GOLD + ChatColor.BOLD + ChatColor.UNDERLINE + "Gold Rush" + ChatColor.RESET + "     ";
	public static final String OBJECTIVE_TITLE_PREFIX = 		"" + ChatColor.YELLOW + ChatColor.BOLD;
	
	public static final String GRAY_LINE_SEPARATOR = 		"§8§m-----------------------";
	
}
