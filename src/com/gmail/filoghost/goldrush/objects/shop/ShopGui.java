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
package com.gmail.filoghost.goldrush.objects.shop;

import java.util.Arrays;

import lombok.Getter;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import wild.api.menu.ClickHandler;
import wild.api.menu.Icon;
import wild.api.menu.IconBuilder;
import wild.api.menu.IconMenu;

import com.gmail.filoghost.goldrush.Configuration;
import com.gmail.filoghost.goldrush.constants.Lang;
import com.gmail.filoghost.goldrush.constants.Numbers;
import com.gmail.filoghost.goldrush.constants.Sounds;
import com.gmail.filoghost.goldrush.enums.TeamColor;
import com.gmail.filoghost.goldrush.objects.Arena;
import com.gmail.filoghost.goldrush.objects.GrPlayer;
import com.gmail.filoghost.goldrush.utils.Utils;

@Getter
public class ShopGui {
	
	private Arena arena;
	private TeamColor color;
	private String title;
	private boolean unlockedPads;
	
	private Icon lockedPadsIcon, unlockedPadsIcon;
	
	
	public ShopGui(final Arena arena, final TeamColor color, String title) {
		this.arena = arena;
		this.color = color;
		this.title = title;
		
		lockedPadsIcon = new IconBuilder(Material.GOLD_PLATE)
			.name(ChatColor.GREEN + "Sblocca teletrasporti")
			.lore(Arrays.asList("", ChatColor.GRAY + "Costo: " + ChatColor.WHITE + Numbers.PADS_UNLOCK_XP_LEVELS + " Livelli"))
			.clickHandler(new ClickHandler() {
			
				@Override
				public void onClick(Player player) {
					if (unlockedPads) {
						player.sendMessage(ChatColor.RED + "I teletrasporti sono già sbloccati.");
						return;
					} else {
						if (player.getLevel() >= Numbers.PADS_UNLOCK_XP_LEVELS) {
							unlockedPads = true;
							player.setLevel(player.getLevel() - Numbers.PADS_UNLOCK_XP_LEVELS);
							Sounds.BUY_PADS.playTo(player);
							arena.awardCoinsCalculatingBonus(player, arena.getGameData(player), Configuration.coinsUnlockTeleports);
							arena.tellAll(Lang.GOLDRUSH_PREFIX + color.getChatColor() + player.getName() + "§7 ha sbloccato i teletrasporti " + color.getChatColor() + "§l" + color.getPlural());
							IconMenu iconMenu = Utils.getOpenIconMenu(player);
							for (int i = 0; i < iconMenu.getSize(); i++) {
								Icon at = iconMenu.getIconAt(i);
								if (at != null && at == lockedPadsIcon) {
									iconMenu.setIconRaw(i, unlockedPadsIcon);
									iconMenu.refresh(i);
								}
							}
							
						} else {
							player.sendMessage(ChatColor.RED + "Servono almeno " + Numbers.PADS_UNLOCK_XP_LEVELS + " livelli di esperienza.");
						}
					}
				}
				
			}).build();
		
		unlockedPadsIcon = new IconBuilder(Material.GOLD_PLATE)
			.name(ChatColor.DARK_GRAY + "Sblocca teletrasporti")
			.lore(Arrays.asList("", ChatColor.GRAY + "Sbloccati!"))
			.clickHandler(new ClickHandler() {
			
				@Override
				public void onClick(Player player) {
					player.sendMessage(ChatColor.RED + "I teletrasporti sono già sbloccati.");
					return;
				}
				
			}).build();
	}
	
	public void open(Player player) {
		final IconMenu menu = new IconMenu(title, 6);
		GrPlayer grPlayer = GrPlayer.get(player);
		
		menu.setIcon(5, 1, unlockedPads ? unlockedPadsIcon : lockedPadsIcon);
		ShopItem.placeIcons(menu, grPlayer, player, 1);
		
		menu.refresh();
		menu.open(player);
	}
	
}
