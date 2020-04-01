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

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import wild.api.WildConstants;

import com.gmail.filoghost.goldrush.constants.Lang;
import com.gmail.filoghost.goldrush.mysql.SQLManager;
import com.gmail.filoghost.goldrush.mysql.SQLPlayerData;
import com.gmail.filoghost.goldrush.mysql.SQLTask;
import com.google.common.collect.Maps;

@Getter
public class GrPlayer {
	
	private String name;
	private boolean needSave;
	
	private Set<Integer> upgrades;
	
	private int coins;
	private int wins;
	private int kills;
	private int deaths;
	private Scoreboard statsScoreboard;
	private Team coinsTeam;
	
	// Solo nomi lowercase
	public static Map<String, GrPlayer> playersMap = Maps.newConcurrentMap();
	
	public GrPlayer(String name, SQLPlayerData data) {
		this.name = name;
		this.coins = data.getCoins();
		this.wins = data.getWins();
		this.kills = data.getKills();
		this.deaths = data.getDeaths();
		this.upgrades = data.getUpgrades();
	}
	
	public static void load(String name) throws SQLException {
		SQLPlayerData data = SQLManager.getPlayerData(name);
		playersMap.put(name.toLowerCase(), new GrPlayer(name, data));
	}
	
	public static GrPlayer get(Player base) {
		return playersMap.get(base.getName().toLowerCase());
	}
	
	public static void unload(Player base) {
		final GrPlayer removed = playersMap.remove(base.getName().toLowerCase());
		removed.saveIfNecessaryAsync();
	}
	
	public void saveIfNecessary() {
		if (needSave) {
			try {
				SQLManager.setStats(name, upgrades, coins, wins, kills, deaths);
				needSave = false;
			} catch (SQLException e) {
				e.printStackTrace();
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Si è verificato un errore con il database (" + e.getClass().getName() + "): " + e.getMessage());
			}
		}
	}
	
	public void saveIfNecessaryAsync() {
		if (needSave) {
			new SQLTask() {
				
				@Override
				public void execute() throws SQLException {
					SQLManager.setStats(name, upgrades, coins, wins, kills, deaths);
					needSave = false;
				}
			}.submitAsync(null);
		}
	}
	
	@Deprecated
	public boolean hasUpgrade(int id) {
		if (id <= 0) {
			return true;
		}
		return upgrades.contains(id);
	}
	
	public void addUpgrade(int id) {
		if (upgrades.add(id)) {
			needSave = true;
		}
	}
	
	public void addCoins(int x) {
		if (x != 0) {
			needSave = true;
		}
		coins += x;
	}
	
	public void addWins(int x) {
		if (x != 0) {
			needSave = true;
		}
		wins += x;
	}
	
	public void addKills(int x) {
		if (x != 0) {
			needSave = true;
		}
		kills += x;
	}
	
	public void addDeaths(int x) {
		if (x != 0) {
			needSave = true;
		}
		deaths += x;
	}
	
	public void displayStatsSidebar(Player player) {
		statsScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective obj = statsScoreboard.registerNewObjective("stats", "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName(Lang.OBJECTIVE_STATISTICHE_NAME);
		
		coinsTeam = statsScoreboard.registerNewTeam("coins");
		String coinsEntry = emptyLine(11);
		coinsTeam.addEntry(coinsEntry);
		coinsTeam.setSuffix(ChatColor.WHITE.toString() + coins);
		
		setScore(obj, emptyLine(13), 13);
		setScore(obj, Lang.OBJECTIVE_TITLE_PREFIX + "Coins", 12);
		setScore(obj, coinsEntry, 11);
		setScore(obj, emptyLine(10), 10);
		setScore(obj, Lang.OBJECTIVE_TITLE_PREFIX + "Vittorie", 9);
		setScore(obj, wins + emptyLine(8), 8);
		setScore(obj, emptyLine(7), 7);
		setScore(obj, Lang.OBJECTIVE_TITLE_PREFIX + "Uccisioni", 6);
		setScore(obj, kills + emptyLine(5), 5);
		setScore(obj, emptyLine(4), 4);
		setScore(obj, Lang.OBJECTIVE_TITLE_PREFIX + "Morti", 3);
		setScore(obj, deaths + emptyLine(2), 2);
		setScore(obj, emptyLine(1), 1);
		WildConstants.Messages.displayIP(statsScoreboard, obj, 0);

		player.setScoreboard(statsScoreboard);
	}
	
	public void updateCoinsIfSidebar() {
		if (coinsTeam != null) {
			coinsTeam.setSuffix(ChatColor.WHITE.toString() + coins);
		}
	}
	
	
	private static String emptyLine(int sideNumber) {
		if (sideNumber > 15 || sideNumber < 0) return "";
		return ChatColor.values()[sideNumber].toString();
	}
	
	private static void setScore(Objective obj, String entry, int score) {
		obj.getScore(entry).setScore(score);
	}
	
}
