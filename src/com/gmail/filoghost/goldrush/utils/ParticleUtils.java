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
package com.gmail.filoghost.goldrush.utils;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import wild.api.world.Particle;

import com.gmail.filoghost.goldrush.GoldRush;
import com.gmail.filoghost.goldrush.runnables.DetonateFireworkTask;

public class ParticleUtils {

	public static final Color[] colors = new Color[]{
		Color.fromRGB(255, 0, 0),
		Color.fromRGB(255, 128, 0),
		Color.fromRGB(255, 255, 0),
		Color.fromRGB(128, 255, 0),
		Color.fromRGB(0, 255, 0),
		Color.fromRGB(0, 255, 128),
		Color.fromRGB(0, 255, 255),
		Color.fromRGB(0, 128, 255),
		Color.fromRGB(0, 0, 255),
		Color.fromRGB(128, 0, 255),
		Color.fromRGB(255, 0, 255),
		Color.fromRGB(255, 0, 128),
	};
	
	
	private static double[] sinValues = new double[]{0.0, 0.5, 0.866, 1.0, 0.866, 0.5, 0.0, -0.5, -0.866, -1.0, -0.866, -0.5};
	private static double[] cosValues = new double[]{1.0, 0.866, 0.5, 0.0, -0.5, -0.866, -1.0, -0.866, -0.5, 0.0, 0.5, 0.866};
	private static int sinCosSteps = 12;
		
	public static void winningFirework(Location loc, int index) {
		Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        
        Color currentColor = colors[index];
        
        int red = currentColor.getRed();
        int green = currentColor.getGreen();
        int blue = currentColor.getBlue();
        
        FireworkEffect effect = FireworkEffect.builder().withColor(Color.fromRGB(red, green, blue))
				.withColor(Color.fromRGB((red/4)*3, (green/4)*3, (blue/4)*3))
				.withColor(Color.fromRGB(red/2, green/2, blue/2))
				.with(Type.BURST)
				.withTrail()
				.build();
        
        meta.addEffect(effect);
        meta.setPower(0);
        firework.setFireworkMeta(meta);
	}
	
	public static void fireworkExplosion(Location loc, FireworkEffect[] effects) {
		World world = loc.getWorld();
		Firework firework = (Firework) world.spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffects(effects);
        firework.setFireworkMeta(meta);
        Bukkit.getScheduler().scheduleSyncDelayedTask(GoldRush.plugin, new DetonateFireworkTask(firework), 2L);
	}
	
	public static void circle(Particle particle, Location start, double radius) {
		
		double x = start.getX();
		double y = start.getY();
		double z = start.getZ();
		
		World world = start.getWorld();
		
		for (int i = 0; i < sinCosSteps; i++) {
			singleParticle(new Location(world, x + sinValues[i] * radius, y, z + cosValues[i] * radius), particle);
		}
	}
	
	public static void trail(Particle particle, Location start, Location end) {
		
		double distance = start.distance(end);
		
		double x = start.getX();
		double y = start.getY();
		double z = start.getZ();
		
		double iterx = (end.getX()-x)/(distance*2);
		double itery = (end.getY()-y)/(distance*2);
		double iterz = (end.getZ()-z)/(distance*2);
		
		World world = start.getWorld();
		
		for (double i = 0; i < distance; i += 0.5) {
			x += iterx;
			y += itery;
			z += iterz;
			particle(new Location(world, x, y, z), particle);
		}
	}
	
	@Deprecated
	public static void trail(Particle particle, Location start, Location end, float speed) {
		
		double distance = start.distance(end);
		
		double x = start.getX();
		double y = start.getY();
		double z = start.getZ();
		
		double iterx = (end.getX()-x)/(distance*2);
		double itery = (end.getY()-y)/(distance*2);
		double iterz = (end.getZ()-z)/(distance*2);
		
		World world = start.getWorld();
		
		for (double i = 0; i < distance; i += 0.5) {
			x += iterx;
			y += itery;
			z += iterz;
			detailedParticle(new Location(world, x, y, z), particle, 0.3f, 0.3f, speed, 10);
		}
	}
	
	public static void smallTrail(Particle particle, Location start, Location end) {
		
		double distance = start.distance(end);
		
		double x = start.getX();
		double y = start.getY();
		double z = start.getZ();
		
		double iterx = (end.getX()-x)/(distance*2);
		double itery = (end.getY()-y)/(distance*2);
		double iterz = (end.getZ()-z)/(distance*2);
		
		World world = start.getWorld();
		
		for (double i = 0; i < distance; i += 0.5) {
			x += iterx;
			y += itery;
			z += iterz;
			smallParticle(new Location(world, x, y, z), particle);
		}
	}
	
	public static void detailedParticle(Location loc, Particle particle, float width, float height, float speed, int amount) {
		particle.display(loc, width, height, width, speed, amount);
	}
	
	public static void blood(Player target) {
		Location loc = target.getEyeLocation().subtract(0.0, 0.7, 0.0);
		detailedParticle(loc, Particle.RED_DUST, 0.4F, 0.8F, 0.0F, 80);
	}
	
	public static void bloodExplode(Player target) {
		Location loc = target.getEyeLocation().subtract(0.0, 0.7, 0.0);
		detailedParticle(loc, Particle.RED_DUST, 0.8F, 0.8F, 0.0F, 200);
	}
	
	public static void bigSmoke(Location loc) {
		detailedParticle(loc, Particle.SMOKE, 0.5F, 0.5F, 0.0F, 100);
	}
	
	public static void particle(Location loc, Particle particle) {
		detailedParticle(loc, particle, 0.1F, 0.1F, 0.0F, 4);
	}
	
	public static void singleParticle(Location loc, Particle particle) {
		detailedParticle(loc, particle, 0.0F, 0.0F, 0.0F, 1);
	}
	
	public static void bigFlames(Location loc) {
		detailedParticle(loc, Particle.FLAME, 0.4F, 0.4F, 0.0F, 10);
	}
	
	public static void regenEffect(Location loc) {
		detailedParticle(loc, Particle.SPELL, 0.3F, 1.0F, 0.0F, 20);
	}
	
	public static void smallParticle(Location loc, Particle particle) {
		detailedParticle(loc, particle, 0.1F, 0.1F, 0.0F, 2);
	}
}
