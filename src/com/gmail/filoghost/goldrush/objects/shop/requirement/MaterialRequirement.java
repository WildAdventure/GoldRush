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
package com.gmail.filoghost.goldrush.objects.shop.requirement;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import wild.api.translation.Translation;

import com.gmail.filoghost.goldrush.objects.shop.ShopItem;
import com.gmail.filoghost.goldrush.utils.Utils;

public class MaterialRequirement implements ShopRequirement {
	
	private Material material;
	private int amount;
	

	public MaterialRequirement(Material material, int amount) {
		this.material = material;
		this.amount = amount;
	}

	@Override
	public boolean tryUse(Player player, ShopItem shopItem) {
		Inventory inv = player.getInventory();
		
		if (Utils.hasItem(inv, material, amount)) {
			Utils.takeItem(inv, material, amount);
			player.sendMessage(ChatColor.GREEN + "Hai comprato " + shopItem.getName() + " per " + amount + "x " + Translation.of(material) + "!");
			shopItem.getReward().giveReward(player);
			return true;
		} else {
			player.sendMessage(ChatColor.RED + "Ti serve " + amount + "x " + Translation.of(material) + " per comprare questo oggetto!");
			return false;
		}
	}

	@Override
	public String getLoreExtra() {
		return "Costo: " + ChatColor.WHITE + amount + "x " + Translation.of(material);
	}

}
