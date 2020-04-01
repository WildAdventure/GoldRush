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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import wild.api.WildCommons;
import wild.api.command.CommandFramework;

import com.gmail.filoghost.goldrush.GoldRush;
import com.gmail.filoghost.goldrush.enums.GameState;
import com.gmail.filoghost.goldrush.objects.Arena;
import com.gmail.filoghost.goldrush.objects.GrPlayer;
import com.gmail.filoghost.goldrush.utils.Utils;

public class SpawnCommand extends CommandFramework {

	public SpawnCommand(JavaPlugin plugin, String label) {
		super(plugin, label);
	}

	@Override
	public void execute(CommandSender sender, String label, String[] args) {
		
		Player player = CommandValidate.getPlayerSender(sender);
		Arena arena = GoldRush.getArenaByPlayer(player);

		if (arena != null && arena.getState() == GameState.GAME) {
			if (args.length > 0 && args[0].equalsIgnoreCase("confirm")) {
				arena.removeGamer(player);
				Utils.toTheLobby(GrPlayer.get(player), player, false, false);
				player.sendMessage(ChatColor.RED + "Hai interrotto la partita in corso, hai perso i Coins e le uccisioni guadagnate!");
			} else {
				WildCommons.fancyMessage("Se esci dalla partita perderai i Coins e le uccisioni guadagnate. ").color(ChatColor.RED).then("Clicca Qui").color(ChatColor.GRAY).style(ChatColor.BOLD, ChatColor.UNDERLINE).command("/spawn confirm").tooltip("Clicca qui per confermare.").then(" se sei sicuro.").color(ChatColor.RED).style().send(player);
			}
			
		} else {
			if (arena != null) {
				arena.removeGamer(player);
			}
			Utils.toTheLobby(GrPlayer.get(player), player, true, false);
		}
	}
}
