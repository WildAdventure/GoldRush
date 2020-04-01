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
package com.gmail.filoghost.goldrush;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Serializer {
	
	private static DecimalFormat decimalFormat;
	static {
		// More precision is not needed at all.
		decimalFormat = new DecimalFormat("0.000");
		DecimalFormatSymbols formatSymbols = decimalFormat.getDecimalFormatSymbols();
		formatSymbols.setDecimalSeparator('.');
		decimalFormat.setDecimalFormatSymbols(formatSymbols);
	}

	public static Location locationFromString(String input) throws IllegalArgumentException {
		if (input == null) {
			throw new IllegalArgumentException("l'input non era settato");
		}
		
		String[] parts = input.split(",");
		
		if (parts.length != 6) {
			throw new IllegalArgumentException("le parti della stringa non sono 6");
		}
		
		try {
			double x = Double.parseDouble(parts[1].replace(" ", ""));
			double y = Double.parseDouble(parts[2].replace(" ", ""));
			double z = Double.parseDouble(parts[3].replace(" ", ""));
			float yaw = (float) Double.parseDouble(parts[4].replace(" ", ""));
			float pitch = (float) Double.parseDouble(parts[5].replace(" ", ""));
		
			World world = Bukkit.getWorld(parts[0].trim());
			if (world == null) {
				throw new IllegalArgumentException("mondo non trovato");
			}
			
			return new Location(world, x, y, z, yaw, pitch);
			
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("numeri non validi");
		}
	}
	
	public static Block blockFromString(String input) throws IllegalArgumentException {
		if (input == null) {
			throw new IllegalArgumentException("l'input non era settato");
		}
		
		String[] parts = input.split(",");
		
		if (parts.length != 4) {
			throw new IllegalArgumentException("le parti della stringa non sono 4");
		}
		
		try {
			int x = Integer.parseInt(parts[1].replace(" ", ""));
			int y = Integer.parseInt(parts[2].replace(" ", ""));
			int z = Integer.parseInt(parts[3].replace(" ", ""));
			
			World world = Bukkit.getWorld(parts[0].trim());
			if (world == null) {
				throw new IllegalArgumentException("mondo non trovato");
			}
			
			return world.getBlockAt(x, y, z);
			
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("numeri non validi");
		}
	}
	
	public static String locationToString(Location loc) {
		return (loc.getWorld().getName() + ", " + decimalFormat.format(loc.getX()) + ", " + decimalFormat.format(loc.getY()) + ", " + decimalFormat.format(loc.getZ()) + ", " + decimalFormat.format(loc.getYaw()) + ", " + decimalFormat.format(loc.getPitch()));
	}
	
	public static String blockToString(Block block) {
		return (block.getWorld().getName() + ", " + block.getX() + ", " + block.getY() + ", " + block.getZ());
	}
}
