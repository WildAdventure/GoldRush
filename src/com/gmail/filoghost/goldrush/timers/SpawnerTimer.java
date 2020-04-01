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
package com.gmail.filoghost.goldrush.timers;

import org.bukkit.Location;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.PigZombie;
import org.bukkit.util.Vector;

import com.gmail.filoghost.goldrush.GoldRush;
import com.gmail.filoghost.goldrush.constants.Numbers;
import com.gmail.filoghost.goldrush.enums.GameState;
import com.gmail.filoghost.goldrush.objects.Arena;
import com.gmail.filoghost.goldrush.utils.Utils;

public class SpawnerTimer extends TimerMaster {
	
	private int ticks;
	private static final Vector EXP_VELOCITY = new Vector(0, -0.5, 0);
	

	public long getDelayBeforeFirstRun() {
		return 20;
	}

	public long getDelayBetweenEachRun() {
		return 10;
	}
	
	public void run() {
		ticks++;
		
		for (Arena arena : GoldRush.arenaMap.values()) {
			if (arena.getState() == GameState.GAME) {
				
				if (ticks % 2 == 0) { // Uno al secondo
					for (Location mobSpawner : arena.getMobSpawners()) {
						int inThisSpawner = 0;
						for (PigZombie pigZombie : mobSpawner.getWorld().getEntitiesByClass(PigZombie.class)) {
							if (Utils.xzDistanceSquared(pigZombie.getLocation(), mobSpawner) < 2 * 2) { // Raggio 2
								inThisSpawner++;
							}
						}
						
						if (inThisSpawner < Numbers.PIGMAN_MAX) {
							PigZombie pigman = mobSpawner.getWorld().spawn(mobSpawner, PigZombie.class);
							pigman.setAngry(true);
							pigman.setBaby(false);
							pigman.setHealth(Numbers.PIGMAN_HEALTH);
						}
					}
				}
				
				for (Location expSpawner : arena.getExpSpawners()) {
					ExperienceOrb orb = expSpawner.getWorld().spawn(expSpawner, ExperienceOrb.class);
					orb.setExperience(4);
					orb.setVelocity(EXP_VELOCITY);
				}
			}
		}
	}

}
