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

import java.util.List;

import lombok.Getter;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import wild.api.WildConstants;

import com.gmail.filoghost.goldrush.constants.Lang;
import com.gmail.filoghost.goldrush.enums.TeamColor;
import com.google.common.collect.Lists;

public class TeamManager {
	
	@Getter private int maxScore;
	private Scoreboard scoreboard;
	
	@Getter private List<Player> redList;
	@Getter private List<Player> blueList;
	
	private Team redTeam;
	private Team blueTeam;
	
	private Team redScore;
	private Team blueScore;
	
	@Getter private int redGold;
	@Getter private int blueGold;
	
	public TeamManager(int maxScore, Scoreboard attachedScoreboard) {
		this.maxScore = maxScore;
		this.scoreboard = attachedScoreboard;
		blueList = Lists.newArrayList();
		redList = Lists.newArrayList();
	}
	
	public void addToRed(Player player) {
		if (blueList.contains(player)) {
			blueList.remove(player);
		}
		redList.add(player);
		redTeam.addPlayer(player);
		player.sendMessage(Lang.GOLDRUSH_PREFIX + ChatColor.YELLOW + "Sei stato aggiunto alla squadra " + ChatColor.RED + "rossa" + ChatColor.YELLOW + "!");
	}
	
	public void addToBlue(Player player) {
		if (redList.contains(player)) {
			redList.remove(player);
		}
		blueList.add(player);
		blueTeam.addPlayer(player);
		player.sendMessage(Lang.GOLDRUSH_PREFIX + ChatColor.YELLOW + "Sei stato aggiunto alla squadra " + ChatColor.BLUE + "blu" + ChatColor.YELLOW + "!");
	}
	
	public int getRedSize() {
		return redList.size();
	}
	
	public int getBlueSize() {
		return blueList.size();
	}
	
	public TeamColor autoAssign(Player player) {
		if (blueList.size() > redList.size()) {
			addToRed(player);
			return TeamColor.RED;
		} else {
			addToBlue(player);
			return TeamColor.BLUE;
		}
	}
	
	public TeamColor getTeamColor(Player player) {
		if (blueList.contains(player)) {
			return TeamColor.BLUE;
		} else if (redList.contains(player)) {
			return TeamColor.RED;
		} else {
			return null;
		}
	}
	
	public boolean areEnemy(Player one, Player two) {
		if ((blueList.contains(one) && redList.contains(two)) || (redList.contains(one) && blueList.contains(two))) {
			return true;
		} else {
			return false;
		}
	}
	
	public void remove(Player player) {
		blueList.remove(player);
		redList.remove(player);
		Team team = scoreboard.getPlayerTeam(player);
		if (team != null) {
			team.removePlayer(player);
		}
	}
	
	public void resetAndCreate() {
		blueList = Lists.newArrayList();
		redList = Lists.newArrayList();
		
		String redScoreEntry = "§c§r";
		String blueScoreEntry = "§9§r";
		
		if (scoreboard.getObjective("classifica") != null) {
			scoreboard.getObjective("classifica").unregister();
		}
		
		blueTeam = createSafeTeam(scoreboard, "blue");
		blueTeam.setPrefix(ChatColor.BLUE.toString());
		blueTeam.setAllowFriendlyFire(false);
		redTeam = createSafeTeam(scoreboard, "red");
		redTeam.setPrefix(ChatColor.RED.toString());
		redTeam.setAllowFriendlyFire(false);
		
		blueScore = createSafeTeam(scoreboard, "blueScore");
		blueScore.addEntry(blueScoreEntry);
		redScore = createSafeTeam(scoreboard, "redScore");
		redScore.addEntry(redScoreEntry);
		
		Objective obj = scoreboard.registerNewObjective("classifica", "dummy");
		obj.setDisplayName(Lang.OBJECTIVE_CLASSIFICA_NAME);
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		
		obj.getScore("§6§r").setScore(7); // Separatore
		
		obj.getScore(Lang.SIDEBAR_RED_TEAM).setScore(6);
		obj.getScore(redScoreEntry).setScore(5);
		
		obj.getScore("§3§r").setScore(4); // Separatore
		
		obj.getScore(Lang.SIDEBAR_BLUE_TEAM).setScore(3);
		obj.getScore(blueScoreEntry).setScore(2);
		
		obj.getScore("§1§r").setScore(1); // Separatore
		WildConstants.Messages.displayIP(scoreboard, obj, 0);
		
		redGold = 0;
		blueGold = 0;
		blueScore.setPrefix("0§7/§f" + maxScore);
		redScore.setPrefix("0§7/§f" + maxScore);
		
	}
	
	private Team createSafeTeam(Scoreboard sb, String name) {
		if (scoreboard.getTeam(name) != null) {
			scoreboard.getTeam(name).unregister();
		}
		
		return scoreboard.registerNewTeam(name);
	}
	
	public void addGold(TeamColor color, int amount) {
		if (color == TeamColor.BLUE) {
			blueGold += amount;
			blueScore.setPrefix(blueGold + "§7/§f" + maxScore);

		} else if (color == TeamColor.RED) {
			redGold += amount;
			redScore.setPrefix(redGold + "§7/§f" + maxScore);
		}
	}
}
