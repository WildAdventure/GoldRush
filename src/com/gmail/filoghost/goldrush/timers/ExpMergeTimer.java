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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.goldrush.GoldRush;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ExpMergeTimer extends TimerMaster {

	@Override
	public long getDelayBeforeFirstRun() {
		return 0;
	}

	@Override
	public long getDelayBetweenEachRun() {
		return 43;
	}
	
	
	@Override
	public void run() {
		final Map<ExperienceOrb, Location> orbs = Maps.newHashMap();
		for (ExperienceOrb orb : GoldRush.mainWorld.getEntitiesByClass(ExperienceOrb.class)) {
			orbs.put(orb, orb.getLocation());
		}
		
		new BukkitRunnable() {
			@Override
			public void run() {
				
				final Set<EntityGroup> groups = getGroups(orbs, 0.5);
				
				new BukkitRunnable() {
					@Override
					public void run() {
						
						for (EntityGroup group : groups) {
							if (group.size() > 1) {
								Set<ExperienceOrb> groupOrbs = group.getEntities();
								Iterator<ExperienceOrb> iter = groupOrbs.iterator();
								
								ExperienceOrb first = null;
								int totalExp = 0;
								while (iter.hasNext()) {
									ExperienceOrb next = iter.next();
									totalExp += next.getExperience(); // Conta l'exp totale
									
									if (first == null) {
										first = next;
									} else {
										next.remove(); // Lasciamo solo la prima
									}
								}
								
								first.setExperience(totalExp);
							}
						}
					}
				}.runTask(GoldRush.plugin);
			}
		}.runTaskAsynchronously(GoldRush.plugin);
	}
	
	public static Set<EntityGroup> getGroups(Map<ExperienceOrb, Location> entities, double groupRadius) {
		Set<EntityGroup> groups = Sets.newHashSet();
		
		// Normal execution below
		for (Entry<ExperienceOrb, Location> entry : entities.entrySet()) {
			
			ExperienceOrb entity = entry.getKey();
			Location location = entry.getValue();

			EntityGroup primaryNearGroup = null;
			Set<EntityGroup> otherNearGroups = Sets.newHashSet();
			
			for (EntityGroup group : groups) {
				if (group.isInRadius(location)) {
					if (primaryNearGroup == null) {
						// Sets the primary group
						primaryNearGroup = group;
					} else {
						// Adds other groups to the set of groups to join
						otherNearGroups.add(group);
					}
				}
			}
			
			if (primaryNearGroup == null) {
				// No near group found, creates a new one
				EntityGroup newGroup = new EntityGroup(groupRadius);
				newGroup.add(entity, location);
				groups.add(newGroup);
			} else {
				// Add the entity to the primary group
				primaryNearGroup.add(entity, location);
				
				// Checks if there are groups to join. The entity can be in all there groups, so we'll need to join them
				if (otherNearGroups.size() > 0) {
					for (EntityGroup secondaryGroup : otherNearGroups) {
						// They're being merged
						secondaryGroup.transferTo(primaryNearGroup);
						//groups.remove(secondaryGroup);
					}
				}
			}
		}

		return groups;
	}

	private static class EntityGroup {

		private Map<ExperienceOrb, Location> entities;
		private final double radiusSquared;

		public EntityGroup(double radius) {
			entities = Maps.newHashMap();
			this.radiusSquared = radius * radius;
		}
		
		public void add(ExperienceOrb entity, Location loc) {
			entities.put(entity, loc);
		}
		
		public boolean isInRadius(Location toCheck) {
			for (Location entityLoc : entities.values()) {
				if (entityLoc.distanceSquared(toCheck) <= radiusSquared) {
					return true;
				}
			}
			
			return false;
		}
		
		public void transferTo(EntityGroup group) {
			group.entities.putAll(this.entities);
			this.entities.clear();
		}
		
		public Set<ExperienceOrb> getEntities() {
			return Sets.newHashSet(entities.keySet());
		}
		
		public int size() {
			return entities.size();
		}
	}

}
