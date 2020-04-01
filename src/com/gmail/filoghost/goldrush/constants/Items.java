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

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import wild.api.item.ItemBuilder;

public class Items {

	public static ItemStack RED_HELMET = getColoredArmor(Material.LEATHER_HELMET, Color.RED);
	public static ItemStack RED_BODY = getColoredArmor(Material.LEATHER_CHESTPLATE, Color.RED);
	public static ItemStack RED_LEGGINGS = getColoredArmor(Material.LEATHER_LEGGINGS, Color.RED);
	public static ItemStack RED_BOOTS = getColoredArmor(Material.LEATHER_BOOTS, Color.RED);
	
	public static ItemStack BLUE_HELMET = getColoredArmor(Material.LEATHER_HELMET, Color.BLUE);
	public static ItemStack BLUE_BODY = getColoredArmor(Material.LEATHER_CHESTPLATE, Color.BLUE);
	public static ItemStack BLUE_LEGGINGS = getColoredArmor(Material.LEATHER_LEGGINGS, Color.BLUE);
	public static ItemStack BLUE_BOOTS = getColoredArmor(Material.LEATHER_BOOTS, Color.BLUE);
	
	public static ItemStack RED_WOOL = ItemBuilder.of(Material.WOOL).durability(14).name(ChatColor.RED + "Squadra Rossa").build();
	public static ItemStack BLUE_WOOL =  ItemBuilder.of(Material.WOOL).durability(11).name(ChatColor.BLUE + "Squadra Blu").build();
	
	public static final ItemStack TEAM_SELECTOR_RED = ItemBuilder.of(Material.WOOL).durability(14).name(ChatColor.RED + "Scegli Squadra Rossa " + ChatColor.GRAY + "(Click destro)").build();
	public static final ItemStack TEAM_SELECTOR_BLUE = ItemBuilder.of(Material.WOOL).durability(11).name(ChatColor.BLUE + "Scegli Squadra Blu " + ChatColor.GRAY + "(Click destro)").build();
	
	public static final ItemStack[] DEFAULT_KIT = {
		ItemBuilder.of(Material.WOOD_SWORD).enchant(Enchantment.DURABILITY, 10).build(),
		ItemBuilder.of(Material.BOW).build(),
		ItemBuilder.of(Material.ARROW).amount(5).build(),
	};
	
	public static List<Material> ALLOWED_DROPS = Arrays.asList(
		Material.GOLD_NUGGET,
		Material.GOLD_INGOT,
		Material.GOLD_BLOCK,
		Material.ROTTEN_FLESH
	);
	
	
	public static List<Material> ALLOWED_FROM_PADS = Arrays.asList(
			Material.STONE_BUTTON,
			Material.STONE_PLATE
		);
	
	private static ItemStack getColoredArmor(Material mat, Color color) {
		ItemStack item = new ItemStack(mat);
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setColor(color);
		item.setItemMeta(meta);
		return item;
	}
	
}
