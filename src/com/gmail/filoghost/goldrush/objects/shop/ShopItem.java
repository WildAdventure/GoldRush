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

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.NonNull;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import wild.api.item.ItemBuilder;
import wild.api.menu.ClickHandler;
import wild.api.menu.Icon;
import wild.api.menu.IconMenu;

import com.gmail.filoghost.goldrush.GoldRush;
import com.gmail.filoghost.goldrush.constants.Sounds;
import com.gmail.filoghost.goldrush.objects.GrPlayer;
import com.gmail.filoghost.goldrush.objects.shop.requirement.ExpRequirement;
import com.gmail.filoghost.goldrush.objects.shop.requirement.MaterialRequirement;
import com.gmail.filoghost.goldrush.objects.shop.requirement.ShopRequirement;
import com.gmail.filoghost.goldrush.objects.shop.reward.ItemReward;
import com.gmail.filoghost.goldrush.objects.shop.reward.PotionReward;
import com.gmail.filoghost.goldrush.objects.shop.reward.ShopReward;
import com.gmail.filoghost.goldrush.utils.Utils;
import com.google.common.collect.Lists;

@Getter
public enum ShopItem {
	
	/*					ID		Icon pos.		Nome        					Coins		Requirement/Exp		Icon						Reward (opz.)		Upgrade richiesti (opz.)		*/
	STONE_SWORD 	(	1,		1, 1,			"Spada di Pietra",				0,			8,					Material.STONE_SWORD),
	IRON_SWORD 		(	2,		2, 1,			"Spada di Ferro",				120000,		15,					Material.IRON_SWORD, 		null,				array(ShopItem.STONE_SWORD) ),
	DIAMOND_SWORD 	(	3,		3, 1,			"Spada di Diamante",			600000,		30,					Material.DIAMOND_SWORD, 	null,				array(ShopItem.IRON_SWORD) ),
	
	BOW_POWER_1 	(	10,		7, 1,			"Arco Potenza I",				50000,		6,					ItemBuilder.of(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, 1).build() ),
	BOW_POWER_2 	(	11,		8, 1,			"Arco Potenza II",				200000,		12,					ItemBuilder.of(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, 2).build(), null, array(ShopItem.BOW_POWER_1), null ),
	ARROWS_15 		(	19,		9, 1,			"15 Frecce",					0,			8,					ItemBuilder.of(Material.ARROW).amount(15).build() ),
	
	GOLD_HELMET 	(	20,		1, 2,			"Elmo d'Oro",					0,			4,					Material.GOLD_HELMET ),
	GOLD_CHEST 		(	21,		1, 3,			"Corazza d'Oro",				0,			10,					Material.GOLD_CHESTPLATE ),
	GOLD_LEGS 		(	22,		1, 4,			"Gambali d'Oro",				0,			6,					Material.GOLD_LEGGINGS ),
	GOLD_BOOTS		(	23,		1, 5,			"Stivali d'Oro",				0,			2,					Material.GOLD_BOOTS ),
	
	IRON_HELMET 	(	30,		2, 2,			"Elmo di Ferro",				40000,		6,					Material.IRON_HELMET,		null,				array(ShopItem.GOLD_HELMET) ),
	IRON_CHEST 		(	31,		2, 3,			"Corazza di Ferro",				120000,		18,					Material.IRON_CHESTPLATE, 	null,				array(ShopItem.GOLD_CHEST) ),
	IRON_LEGS 		(	32,		2, 4,			"Gambali di Ferro",				100000,		15,					Material.IRON_LEGGINGS, 	null,				array(ShopItem.GOLD_LEGS) ),
	IRON_BOOTS 		(	33,		2, 5,			"Stivali di Ferro",				40000,		6,					Material.IRON_BOOTS, 		null,				array(ShopItem.GOLD_BOOTS) ),
	
	DIAMOND_HELMET 	(	40,		3, 2,			"Elmo di Diamante",				225000,		12,					Material.DIAMOND_HELMET, 	null,				array(ShopItem.IRON_HELMET) ),
	DIAMOND_CHEST 	(	41,		3, 3,			"Corazza di Diamante",			600000,		32,					Material.DIAMOND_CHESTPLATE,null,				array(ShopItem.IRON_CHEST) ),
	DIAMOND_LEGS 	(	42,		3, 4,			"Gambali di Diamante",			450000,		24,					Material.DIAMOND_LEGGINGS, 	null,				array(ShopItem.IRON_LEGS) ),
	DIAMOND_BOOTS 	(	43,		3, 5,			"Stivali di Diamante",			225000,		12,					Material.DIAMOND_BOOTS, 	null,				array(ShopItem.IRON_BOOTS) ),
	
	
	/*					ID,			Icon pos.		Nome        					Coins		Exp					Icon						Reward (opz.)		*/
	EFFECT_SPEED 	(	100,		7, 3,			"Velocità I (1 minuto)",		100000,		8,					Material.FEATHER,			new PotionReward(PotionEffectType.SPEED, 			0, 60) ),
	EFFECT_STRENGTH (	101,		8, 3,			"Forza I (1 minuto)",			250000,		20,					Material.FLINT,				new PotionReward(PotionEffectType.INCREASE_DAMAGE, 	0, 60) ),
	EFFECT_REGEN 	(	102,		9, 3,			"Rigenerazione I (30 secondi)",	200000,		15,					Material.SPECKLED_MELON,	new PotionReward(PotionEffectType.REGENERATION, 	0, 30) ),
	
	/*  Misto  */
	ROTTEN_TO_EXP 	(	900,		9, 6,			"10 Ampolle di Esperienza",		50000,		new MaterialRequirement(Material.ROTTEN_FLESH, 10), ItemBuilder.of(Material.ROTTEN_FLESH).amount(10).build(), new ItemReward(new ItemStack(Material.EXP_BOTTLE, 10)) ),
	;
	
	private int id;
	private int menuX, menuY;
	private String name;
	private int coinsUnlockCost;
	private ShopRequirement useCost;
	private ShopReward reward;
	private Icon iconLocked, iconUnlocked;
	
	
	private ShopItem(int id, int menuX, int menuY, String name, int coinsUnlockCost, int expCost, Material material) {
		this(id, menuX, menuY, name, coinsUnlockCost, new ExpRequirement(expCost), material, null, null, null);
	}
	private ShopItem(int id, int menuX, int menuY, String name, int coinsUnlockCost, ShopRequirement useCost, Material material) {
		this(id, menuX, menuY, name, coinsUnlockCost, useCost, material, null, null, null);
	}
	
	
	private ShopItem(int id, int menuX, int menuY, String name, int coinsUnlockCost, int expCost, ItemStack iconStack) {
		this(id, menuX, menuY, name, coinsUnlockCost, new ExpRequirement(expCost), iconStack, null, null, null);
	}
	private ShopItem(int id, int menuX, int menuY, String name, int coinsUnlockCost, ShopRequirement useCost, ItemStack iconStack) {
		this(id, menuX, menuY, name, coinsUnlockCost, useCost, iconStack, null, null, null);
	}
	
	
	private ShopItem(int id, int menuX, int menuY, String name, int coinsUnlockCost, int expCost, Material material, ShopReward reward) {
		this(id, menuX, menuY, name, coinsUnlockCost, new ExpRequirement(expCost), material, reward, null, null);
	}
	private ShopItem(int id, int menuX, int menuY, String name, int coinsUnlockCost, ShopRequirement useCost, Material material, ShopReward reward) {
		this(id, menuX, menuY, name, coinsUnlockCost, useCost, material, reward, null, null);
	}
	
	
	private ShopItem(int id, int menuX, int menuY, String name, int coinsUnlockCost, int expCost, ItemStack iconStack, ShopReward reward) {
		this(id, menuX, menuY, name, coinsUnlockCost, new ExpRequirement(expCost), iconStack, reward, null, null);
	}
	private ShopItem(int id, int menuX, int menuY, String name, int coinsUnlockCost, ShopRequirement useCost, ItemStack iconStack, ShopReward reward) {
		this(id, menuX, menuY, name, coinsUnlockCost, useCost, iconStack, reward, null, null);
	}
	
	
	private ShopItem(int id, int menuX, int menuY, String name, int coinsUnlockCost, int expCost, Material material, ShopReward reward, ShopItem[] requiredUpgrades) {
		this(id, menuX, menuY, name, coinsUnlockCost, new ExpRequirement(expCost), new ItemStack(material), reward, requiredUpgrades, null);
	}
	private ShopItem(int id, int menuX, int menuY, String name, int coinsUnlockCost, ShopRequirement useCost, Material material, ShopReward reward, ShopItem[] requiredUpgrades) {
		this(id, menuX, menuY, name, coinsUnlockCost, useCost, new ItemStack(material), reward, requiredUpgrades, null);
	}
	
	
	private ShopItem(int id, int menuX, int menuY, String name, int coinsUnlockCost, int expCost, Material material, ShopReward reward, ShopItem[] requiredUpgrades, String[] loreExtra) {
		this(id, menuX, menuY, name, coinsUnlockCost, new ExpRequirement(expCost), new ItemStack(material), reward, requiredUpgrades, loreExtra);
	}
	private ShopItem(int id, int menuX, int menuY, String name, int coinsUnlockCost, ShopRequirement useCost, Material material, ShopReward reward, ShopItem[] requiredUpgrades, String[] loreExtra) {
		this(id, menuX, menuY, name, coinsUnlockCost, useCost, new ItemStack(material), reward, requiredUpgrades, loreExtra);
	}
	

	private ShopItem(int id, int menuX, int menuY, String name, int coinsUnlockCost, int expCost, ItemStack iconStack, ShopReward reward, ShopItem[] requiredUpgrades, String[] loreExtra) {
		this(id, menuX, menuY, name, coinsUnlockCost, new ExpRequirement(expCost), iconStack, reward, requiredUpgrades, loreExtra);
	}
	private ShopItem(int id, int menuX, int menuY, String name, int coinsUnlockCost, final ShopRequirement useCost, ItemStack iconStack, ShopReward reward, final ShopItem[] requiredUpgrades, String[] loreExtra) {
		this.id = id;
		this.menuX = menuX;
		this.menuY = menuY;
		this.name = name;
		this.coinsUnlockCost = coinsUnlockCost;
		this.useCost = useCost;
		
		if (reward == null) {
			// Usa l'icona
			reward = new ItemReward(iconStack);
		}
		
		this.reward = reward;

		List<String> lorePartial = Lists.newArrayList();
		if (loreExtra != null && loreExtra.length > 0) {
			for (String s : loreExtra) {
				lorePartial.add(ChatColor.GRAY + s);
			}
		}
		
		List<String> rewardExtra = reward.getLoreText();
		if (!rewardExtra.isEmpty()) {
			lorePartial.add("");
			lorePartial.addAll(rewardExtra);
		}
		
		iconLocked = new Icon();
		iconUnlocked = new Icon();
		copyStack(iconLocked, iconStack);
		copyStack(iconUnlocked, iconStack);

		iconLocked.setName(ChatColor.RED + name);
		iconUnlocked.setName(ChatColor.GREEN + name);
		
		List<String> loreLocked = new ArrayList<String>(lorePartial);
		List<String> loreUnlocked = lorePartial;
		
		if (requiredUpgrades != null && requiredUpgrades.length > 0) {
			loreLocked.add("");
			loreLocked.add(ChatColor.WHITE + "Upgrade richiest" + (requiredUpgrades.length == 1 ? "o" : "i") + ":");
			for (ShopItem requiredUpgrade : requiredUpgrades) {
				loreLocked.add(ChatColor.DARK_GRAY + "● " + ChatColor.GRAY + requiredUpgrade.getName());
			}
		}
		
		loreLocked.add("");
		loreLocked.add(ChatColor.GOLD + "Sblocca per " + Utils.DECIMAL_FORMAT_THOUSANDS.format(coinsUnlockCost) + " Coins");
		
		loreUnlocked.add("");
		loreUnlocked.add(ChatColor.GRAY + useCost.getLoreExtra());
		
		iconLocked.setLore(loreLocked);
		iconUnlocked.setLore(loreUnlocked);
		
		iconLocked.setClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(Player player) {
				
				if (ShopItem.this.id <= 0) {
					player.sendMessage(ChatColor.RED + "Questo oggetto non è sbloccabile.");
					return;
				}
				
				GrPlayer grPlayer = GrPlayer.get(player);
				if (ShopItem.this.has(grPlayer)) {
					player.sendMessage(ChatColor.RED + "Questo oggetto è già sbloccato!");
				} else {
					
					if (requiredUpgrades != null) {
						for (ShopItem requiredUpgrade : requiredUpgrades) {
							if (!requiredUpgrade.has(grPlayer)) {
								player.sendMessage(ChatColor.RED + "Devi prima sbloccare " + requiredUpgrade.getName() + ".");
								return;
							}
						}
					}
					
					if (GoldRush.isPlaying(player)) {
						player.sendMessage(ChatColor.RED + "Non puoi sbloccare oggetti durante la partita.");
						return;
					}
					
					if (grPlayer.getCoins() >= ShopItem.this.coinsUnlockCost) {
						grPlayer.addCoins(- ShopItem.this.coinsUnlockCost);
						grPlayer.updateCoinsIfSidebar();
						grPlayer.addUpgrade(ShopItem.this.id);
						Sounds.UNLOCK_UPGRADE.playTo(player);
						IconMenu menu = Utils.getOpenIconMenu(player);
						for (int i = 0; i < menu.getSize(); i++) {
							if (iconLocked == menu.getIconAt(i)) {
								menu.setIconRaw(i, iconUnlocked);
								menu.refresh(i);
								break;
							}
						}
						player.sendMessage(ChatColor.GOLD + "Hai sbloccato " + ShopItem.this.name + " per " + Utils.DECIMAL_FORMAT_THOUSANDS.format(ShopItem.this.coinsUnlockCost) + " Coins!");
					} else {
						player.sendMessage(ChatColor.RED + "Ti servono " + Utils.DECIMAL_FORMAT_THOUSANDS.format(ShopItem.this.coinsUnlockCost) + " Coins per sbloccare questo oggetto!");
					}
				}
			}
		});
		
		iconUnlocked.setClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(Player player) {
				if (!GoldRush.isPlaying(player)) {
					player.sendMessage(ChatColor.RED + "Puoi acquistare questo oggetto all'interno delle partite.");
					return;
				}
				
				if (!has(GrPlayer.get(player))) {
					player.sendMessage(ChatColor.RED + "Non hai ancora sbloccato questo oggetto!");
					return;
				}
				
				useCost.tryUse(player, ShopItem.this);
			}
		});
	}
	
	
	private void copyStack(Icon source, ItemStack stack) {
		source.setMaterial(stack.getType());
		source.setDataValue(stack.getDurability());
		source.setAmount(stack.getAmount());
		
		if (stack.getEnchantments() != null) {
			for (Entry<Enchantment, Integer> entry : stack.getEnchantments().entrySet()) {
				source.addEnchantment(entry.getKey(), entry.getValue());
			}
		}
	}
	
	
	@SuppressWarnings("deprecation")
	public boolean has(GrPlayer player) {
		if (id <= 0 || coinsUnlockCost <= 0 || player.hasUpgrade(id)) {
			return true;
		} else {
			return false;
		}
	}
	
	
	public Icon getIcon(GrPlayer player) {
		if (has(player)) {
			return iconUnlocked;
		} else {
			return iconLocked;
		}
	}
	
	
	public static ShopItem getById(int id) {
		if (id > 0) {
			for (ShopItem item : values()) {
				if (item.id == id) {
					return item;
				}
			}
		}
		
		return null;
	}
	
	private static ShopItem[] array(ShopItem... arr) {
		return arr;
	}
	
	public static void placeIcons(IconMenu menu, @NonNull GrPlayer grPlayer, Player player, int yOffset) {
		for (ShopItem item : ShopItem.values()) {
			menu.setIcon(item.getMenuX(), item.getMenuY(), item.getIcon(grPlayer));
		}
	}

}
