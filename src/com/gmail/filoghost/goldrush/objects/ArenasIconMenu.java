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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import wild.api.item.ItemBuilder;
import wild.api.menu.ClickHandler;
import wild.api.menu.IconMenu;

import com.gmail.filoghost.goldrush.GoldRush;
import com.gmail.filoghost.goldrush.enums.StainedClayColor;
import com.gmail.filoghost.goldrush.utils.StaticIcon;

public class ArenasIconMenu {
    
	IconMenu iconMenu;
	StaticIcon infoIcon;
	List<Arena> orderedArenas;
    
	private String menuName;
	
    public ArenasIconMenu(String menuName) {
    	this.menuName = menuName;
    	infoIcon = new StaticIcon(ItemBuilder.of(Material.BOOK_AND_QUILL).name("§d§lArene disponibili")
    													.lore("§7Passa sopra i blocchi per visualizzare il nome",
    														  "§7delle arene e i dettagli. Le arene evidenziate",
    														  "§7sono quelle che devono ancora iniziare, e",
    														  "§7all'interno delle quali c'è almeno un giocatore.").build());
    	orderedArenas = getOrderedArenas();
    	iconMenu = new IconMenu(menuName, 6);
    	update();
    }
    
    private static ItemStack getItemStack(Arena arena) {
    	ItemStack item = new ItemStack(Material.STAINED_CLAY);
    	
    	StainedClayColor color = StainedClayColor.GREEN; // Default
    	
    	int playersInside = arena.getGamers().size();
    	String status = "-";
    	switch (arena.getState()) {
			case END:
				color = StainedClayColor.RED;
				status = "§cPartita in corso.";
				break;
			case GAME:
				color = StainedClayColor.RED;
				status = "§cPartita in corso.";
				break;
			case PREGAME:
				
				if (arena.getGameTimer().isStarted()) {
					status = "§aInizia fra " + arena.getGameTimer().getFormattedTime() + ".";
				} else {
					status = "§f" + arena.getMinGamers() + " giocatori per iniziare.";
				}
				
				if (playersInside > 0) {
					if (playersInside < arena.getMaxGamers()) {
						color = StainedClayColor.YELLOW;
					} else {
						color = StainedClayColor.ORANGE;
					}
				} else {
					color = StainedClayColor.GREEN;
				}

				break;
    	}
    	
    	
    	
    	if (playersInside > 1) {
    		// Si può vedere solo quando è più di 1
    		item.setAmount(playersInside);
    	}
    	
    	item.setDurability(color.getData());
    	
    	ItemMeta meta = item.getItemMeta();
    	meta.setDisplayName("§6§l" + arena.getName());
    	meta.setLore(Arrays.asList("§7" + playersInside + "/" + arena.getMaxGamers(), "", status));
    	item.setItemMeta(meta);
    	return item;
    	
    }
    
    public void open(Player player) {
    	iconMenu.open(player);
    }
    
    private List<Arena> getOrderedArenas() {
    	Collection<Arena> arenas = GoldRush.arenaMap.values();
    	List<Arena> orderedArenas = new ArrayList<Arena>();
    	orderedArenas.addAll(arenas);
    	
    	Collections.sort(orderedArenas, new Comparator<Arena>() {

			@Override
			public int compare(Arena o1, Arena o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
    		
    	});
    	return orderedArenas;
    }
    
    public void refreshOrderedArenas() {
    	orderedArenas = getOrderedArenas();
    	iconMenu = new IconMenu(menuName, 6);
    	update();
    }
    
    public void update() {
    	iconMenu.clearIcons();
    	
    	int index = 0;
    	for (final Arena arena : orderedArenas) {
    		StaticIcon icon = new StaticIcon(getItemStack(arena));
    		icon.setClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(Player player) {
					arena.addGamer(player);
				}
			});
    		iconMenu.setIconRaw(index, icon);
    		index++;
    	}
    	
    	iconMenu.setIconRaw(53, infoIcon);
    	iconMenu.refresh();
    }
	
}
