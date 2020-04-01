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

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.goldrush.Configuration;
import com.gmail.filoghost.goldrush.GoldRush;
import com.gmail.filoghost.goldrush.constants.ConfigNodes;
import com.gmail.filoghost.goldrush.constants.Permissions;

import wild.api.command.CommandFramework;
import wild.api.command.CommandFramework.Permission;

@Permission(Permissions.RANK_COMMAND)
public class RankCommand extends CommandFramework {

	public RankCommand(JavaPlugin plugin, String label) {
		super(plugin, label);
	}
	
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		
		if (args.length < 1) {
			player.sendMessage("§6========== Comandi /rank ==========");
			player.sendMessage("§e/rank killer-head <1 | 2 | 3>§7 - Imposta la testa dei classificati.");
			player.sendMessage("§e/rank killer-sign <1 | 2 | 3>§7 - Imposta il cartello dei classificati.");
			player.sendMessage("§e/rank winner-head <1 | 2 | 3>§7 - Imposta la testa dei classificati.");
			player.sendMessage("§e/rank winner-sign <1 | 2 | 3>§7 - Imposta il cartello dei classificati.");
			return;
		}
		
		
		if (args[0].equalsIgnoreCase("killer-head")) {
			CommandValidate.minLength(args, 2, "Utilizzo corretto: /" + label + " killer-head <1 | 2 | 3>");
			Block block = player.getTargetBlock((Set<Material>) null, 64);
			CommandValidate.isTrue(block.getType() == Material.SKULL, "Non stai guardando una testa.");
			
			if (args[1].equalsIgnoreCase("1")) {
				Configuration.firstKillerHead = block;
				Configuration.saveBlock(GoldRush.plugin.getConfig(), ConfigNodes.FIRST_KILLER_HEAD, block);
				player.sendMessage("§aImpostata la prima testa.");
				
			} else if (args[1].equalsIgnoreCase("2")) {
				Configuration.secondKillerHead = block;
				Configuration.saveBlock(GoldRush.plugin.getConfig(), ConfigNodes.SECOND_KILLER_HEAD, block);
				player.sendMessage("§aImpostata la seconda testa.");
				
			} else if (args[1].equalsIgnoreCase("3")) {
				Configuration.thirdKillerHead = block;
				Configuration.saveBlock(GoldRush.plugin.getConfig(), ConfigNodes.THIRD_KILLER_HEAD, block);
				player.sendMessage("§aImpostata la terza testa.");
			} else {
				player.sendMessage("§cUtilizzo corretto: /" + label + " killer-head <1 | 2 | 3>");
			}
			return;
		}
		
		if (args[0].equalsIgnoreCase("killer-sign")) {
			CommandValidate.minLength(args, 2, "Utilizzo corretto: /" + label + " killer-sign <1 | 2 | 3>");
			Block block = player.getTargetBlock((Set<Material>) null, 64);
			CommandValidate.isTrue(block.getType() == Material.WALL_SIGN, "Non stai guardando un cartello a muro.");
			
			if (args[1].equalsIgnoreCase("1")) {
				Configuration.firstKillerSign = block;
				Configuration.saveBlock(GoldRush.plugin.getConfig(), ConfigNodes.FIRST_KILLER_SIGN, block);
				player.sendMessage("§aImpostato il primo cartello.");
				
			} else if (args[1].equalsIgnoreCase("2")) {
				Configuration.secondKillerSign = block;
				Configuration.saveBlock(GoldRush.plugin.getConfig(), ConfigNodes.SECOND_KILLER_SIGN, block);
				player.sendMessage("§aImpostato il secondo cartello.");
				
			} else if (args[1].equalsIgnoreCase("3")) {
				Configuration.thirdKillerSign = block;
				Configuration.saveBlock(GoldRush.plugin.getConfig(), ConfigNodes.THIRD_KILLER_SIGN, block);
				player.sendMessage("§aImpostato il terzo cartello.");
			} else {
				player.sendMessage("§cUtilizzo corretto: /" + label + " killer-sign <1 | 2 | 3>");
			}
			return;
		}
		
		
		if (args[0].equalsIgnoreCase("winner-head")) {
			CommandValidate.minLength(args, 2, "Utilizzo corretto: /" + label + " winner-head <1 | 2 | 3>");
			Block block = player.getTargetBlock((Set<Material>) null, 64);
			CommandValidate.isTrue(block.getType() == Material.SKULL, "Non stai guardando una testa.");
			
			if (args[1].equalsIgnoreCase("1")) {
				Configuration.firstWinnerHead = block;
				Configuration.saveBlock(GoldRush.plugin.getConfig(), ConfigNodes.FIRST_WINNER_HEAD, block);
				player.sendMessage("§aImpostata la prima testa.");
				
			} else if (args[1].equalsIgnoreCase("2")) {
				Configuration.secondWinnerHead = block;
				Configuration.saveBlock(GoldRush.plugin.getConfig(), ConfigNodes.SECOND_WINNER_HEAD, block);
				player.sendMessage("§aImpostata la seconda testa.");
				
			} else if (args[1].equalsIgnoreCase("3")) {
				Configuration.thirdWinnerHead = block;
				Configuration.saveBlock(GoldRush.plugin.getConfig(), ConfigNodes.THIRD_WINNER_HEAD, block);
				player.sendMessage("§aImpostata la terza testa.");
			} else {
				player.sendMessage("§cUtilizzo corretto: /" + label + " winner-head <1 | 2 | 3>");
			}
			return;
		}
		
		if (args[0].equalsIgnoreCase("winner-sign")) {
			CommandValidate.minLength(args, 2, "Utilizzo corretto: /" + label + " winner-sign <1 | 2 | 3>");
			Block block = player.getTargetBlock((Set<Material>) null, 64);
			CommandValidate.isTrue(block.getType() == Material.WALL_SIGN, "Non stai guardando un cartello a muro.");
			
			if (args[1].equalsIgnoreCase("1")) {
				Configuration.firstWinnerSign = block;
				Configuration.saveBlock(GoldRush.plugin.getConfig(), ConfigNodes.FIRST_WINNER_SIGN, block);
				player.sendMessage("§aImpostato il primo cartello.");
				
			} else if (args[1].equalsIgnoreCase("2")) {
				Configuration.secondWinnerSign = block;
				Configuration.saveBlock(GoldRush.plugin.getConfig(), ConfigNodes.SECOND_WINNER_SIGN, block);
				player.sendMessage("§aImpostato il secondo cartello.");
				
			} else if (args[1].equalsIgnoreCase("3")) {
				Configuration.thirdWinnerSign = block;
				Configuration.saveBlock(GoldRush.plugin.getConfig(), ConfigNodes.THIRD_WINNER_SIGN, block);
				player.sendMessage("§aImpostato il terzo cartello.");
			} else {
				player.sendMessage("§cUtilizzo corretto: /" + label + " winner-sign <1 | 2 | 3>");
			}
			return;
		}

		player.sendMessage("§cComando non trovato. /" + label + " per la lista dei comandi.");
	}
}
