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
package com.gmail.filoghost.goldrush.objects.shop.reward;

import java.util.HashMap;
import java.util.List;

import lombok.Getter;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import wild.api.translation.Translation;

import com.gmail.filoghost.goldrush.constants.Sounds;
import com.google.common.collect.Lists;

public class ItemReward implements ShopReward {
	
	@Getter private ItemStack[] contents;

	public ItemReward(Material... materials) {
		contents = new ItemStack[materials.length];
		for (int i = 0; i < materials.length; i++) {
			contents[i] = new ItemStack(materials[i]);
		}
	}
	
	public ItemReward(ItemStack... contents) {
		this.contents = contents;
	}

	@Override
	public List<String> getLoreText() {
		List<String> lore = Lists.newArrayList();
		if (contents.length > 1) {
			// Con 1 solo si capisce cosa c'è dentro
			lore.add(ChatColor.WHITE + "Contenuto:");
			for (ItemStack content : contents) {
				lore.add(ChatColor.DARK_GRAY + "● " + ChatColor.GRAY + Translation.of(content.getType()) + (content.getAmount() > 1 ? ChatColor.WHITE + "x" + content.getAmount() : ""));
			}
		}
		return lore;
	}

	@Override
	public void giveReward(Player player) {
		Sounds.BUY_ITEM.playTo(player);
		
		ItemStack[] clones = new ItemStack[contents.length];
		for (int i = 0; i < contents.length; i++) {
			clones[i] = contents[i].clone();
		}
		HashMap<Integer, ItemStack> exceedings = player.getInventory().addItem(clones);

		if (exceedings != null && !exceedings.isEmpty()) {
			player.sendMessage(ChatColor.RED + "Non avevi abbastanza spazio per tutti gli oggetti, alcuni sono andati perduti.");
		}
	}

}
