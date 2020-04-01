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

import java.util.Arrays;

import lombok.NonNull;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gmail.filoghost.goldrush.enums.GameState;
import com.gmail.filoghost.goldrush.enums.TeamColor;
import com.gmail.filoghost.goldrush.utils.MobStatue;
import com.gmail.filoghost.holographicmobs.api.ClickHandler;
import com.gmail.filoghost.holographicmobs.object.types.HologramVillager;

public class VillagerTrader {
	
	private MobStatue statue;

	public VillagerTrader(final Arena arena, @NonNull Location loc, @NonNull final TeamColor color, @NonNull String name) {
		statue = new MobStatue();
		statue.setType(HologramVillager.class);
		statue.setHologramLines(Arrays.asList(color.getChatColor() + name));
		statue.setClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(Player player) {
				if (arena.getState() == GameState.PREGAME) {
					player.sendMessage(ChatColor.RED + "La partita non è ancora iniziata.");
					return;
				}
				
				TeamColor clickerColor = arena.getTeamManager().getTeamColor(player);
				
				if (clickerColor == null) { // Non in un'arena
					return;
				}
				
				if (clickerColor != color) {
					player.sendMessage(ChatColor.RED + "Questo non è il villico del tuo team.");
					return;
				}
				
				if (color == TeamColor.BLUE) {
					arena.getBlueGui().open(player);
				} else if (color == TeamColor.RED) {
					arena.getRedGui().open(player);
				}
			}
		});
		
		statue.setLocation(loc);
		statue.update();
	}

}
