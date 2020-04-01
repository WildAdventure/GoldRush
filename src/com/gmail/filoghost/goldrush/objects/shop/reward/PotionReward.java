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

import java.util.List;
import java.util.Set;

import lombok.Getter;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import wild.api.translation.Translation;

import com.gmail.filoghost.goldrush.constants.Sounds;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class PotionReward implements ShopReward {
	
	public static int INFINITE_POTION_THREESHOLD = 1000000000;
	
	private static Set<PotionEffectType> uselessAmplifier = Sets.newHashSet(
			PotionEffectType.BLINDNESS,
			PotionEffectType.CONFUSION,
			PotionEffectType.FIRE_RESISTANCE,
			PotionEffectType.INVISIBILITY,
			PotionEffectType.NIGHT_VISION,
			PotionEffectType.WITHER
	);
	
	@Getter private PotionEffect[] effects;
	
	public PotionReward(PotionEffectType type, int amplifier, int durationSeconds) {
		this(new PotionEffect(type, durationSeconds * 20, amplifier));
	}

	public PotionReward(PotionEffect... effects) {
		this.effects = effects;
	}

	@Override
	public List<String> getLoreText() {
		List<String> lore = Lists.newArrayList();
		if (effects.length > 1) {
			lore.add("Contenuto:");
			for (PotionEffect effect : effects) {
				lore.add(ChatColor.DARK_GRAY + "● " + ChatColor.GRAY + formatPotionEffect(effect));
			}
		}
		return lore;
	}

	@Override
	public void giveReward(Player player) {
		Sounds.BUY_ITEM.playTo(player);
		for (PotionEffect effect : effects) {
			player.addPotionEffect(effect, true);
		}
	}
	
	
	public static String formatPotionEffect(PotionEffect potion) {
		StringBuilder output = new StringBuilder();
		
		output.append(Translation.of(potion.getType()));
		
		if (!uselessAmplifier.contains(potion.getType())) {
			output.append(" ");
			output.append(getRoman(potion.getAmplifier() + 1));
		}
		
		if (potion.getDuration() < INFINITE_POTION_THREESHOLD) {
			
			int seconds = potion.getDuration() / 20;
			int minutes = 0;
			
			if (seconds >= 60) {
				minutes = seconds / 60;
				seconds = seconds % 60;
			}
			
			output.append(" (");
			output.append(minutes);
			output.append(":");
			if (seconds < 10) output.append("0");
			output.append(seconds);
			output.append(")");
		}
		
		return output.toString();
	}
	
	public static String getRoman(int i) {
		switch (i) {
			case 1:		return "I";
			case 2:		return "II";
			case 3:		return "III";
			case 4:		return "IV";
			case 5:		return "V";
			case 6:		return "VI";
			case 7:		return "VII";
			case 8:		return "VIII";
			case 9:		return "IX";
			case 10:	return "X";
			default: 	return Integer.toString(i);
		}
	}

}
