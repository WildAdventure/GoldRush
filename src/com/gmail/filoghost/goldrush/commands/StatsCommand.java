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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import wild.api.command.CommandFramework;

import com.gmail.filoghost.goldrush.objects.GrPlayer;

public class StatsCommand extends CommandFramework {
	
	private static final DecimalFormat format = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));

	public StatsCommand(JavaPlugin plugin, String label) {
		super(plugin, label);
	}
	
	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		GrPlayer grPlayer = GrPlayer.get(CommandValidate.getPlayerSender(sender));
		sender.sendMessage("§6§m-----§r §6§lLe tue statistiche §6§m-----");
		sender.sendMessage("§e§lCoins:§f " + grPlayer.getCoins());
		sender.sendMessage("§e§lVittorie:§f " + grPlayer.getWins());
		sender.sendMessage("§e§lUccisioni:§f " + grPlayer.getKills());
		sender.sendMessage("§e§lMorti:§f " + grPlayer.getDeaths());
		sender.sendMessage("§e§lRapporto Uccisioni/Morti:§f " + calculateKDR(grPlayer.getKills(), grPlayer.getDeaths()));
		sender.sendMessage("");
		sender.sendMessage("§7Classifica globale: §f/classifica");
		sender.sendMessage("");
	}
	
	public static String calculateKDR(int kills, int deaths) {
		if (kills <= 0) {
			return "0";
		}
		
		if (deaths <= 0) {
			return "\u221E";
		}
		
		return format.format((double) kills / (double) deaths);
	}
	
}
