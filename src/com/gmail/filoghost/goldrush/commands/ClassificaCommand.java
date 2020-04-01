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

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import wild.api.command.CommandFramework;

import com.gmail.filoghost.goldrush.mysql.SQLStat;
import com.gmail.filoghost.goldrush.utils.Ranking;

public class ClassificaCommand extends CommandFramework {

	public ClassificaCommand(JavaPlugin plugin, String label) {
		super(plugin, label);
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		if (args.length == 0) {
			sender.sendMessage("§6§m-----§r §6§lComandi classifica §6§m-----");
			sender.sendMessage("§e/classifica uccisioni");
			sender.sendMessage("§e/classifica vittorie");
			sender.sendMessage("§e/stats §7- Le tue statistiche");
			sender.sendMessage("");
			return;
		}
		
		if (args[0].equalsIgnoreCase("uccisioni")) {
			sender.sendMessage("§6§m-----§r §6§lClassifica uccisioni §6§m-----");
			List<SQLStat> list = Ranking.getTopKills();
			int size = list.size() - 1;
			for (int i = 0; i < size; i++) {
				SQLStat entry = list.get(i);
				sender.sendMessage("§8" + (i+1) + ". §7" + entry.getValue() + " §8- §f" + entry.getName());
			}
			sender.sendMessage("");
			return;
		}
		
		if (args[0].equalsIgnoreCase("vittorie")) {
			sender.sendMessage("§6§m-----§r §6§lClassifica vittorie §6§m-----");
			List<SQLStat> list = Ranking.getTopWins();
			int size = list.size() - 1;
			for (int i = 0; i < size; i++) {
				SQLStat entry = list.get(i);
				sender.sendMessage("§8" + (i+1) + ". §7" + entry.getValue() + " §8- §f" + entry.getName());
			}
			sender.sendMessage("");
			return;
		}
		
		sender.sendMessage(ChatColor.RED + "Tipo di classifica non valido. Scrivi /" + label + " per i comandi.");
	}
	
}
