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

import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

import wild.api.config.PluginConfig;

import com.gmail.filoghost.goldrush.constants.ConfigNodes;

public class Configuration {

	public static int countDown;
	public static String joinMessage;
	public static String gameMotd;
	public static Location lobby;
	
	public static Block firstKillerHead;
	public static Block firstKillerSign;
	public static Block secondKillerHead;
	public static Block secondKillerSign;
	public static Block thirdKillerHead;
	public static Block thirdKillerSign;
	
	public static Block firstWinnerHead;
	public static Block firstWinnerSign;
	public static Block secondWinnerHead;
	public static Block secondWinnerSign;
	public static Block thirdWinnerHead;
	public static Block thirdWinnerSign;
	
	public static String mysqlHost;
	public static int mysqlPort;
	public static String mysqlDatabase;
	public static String mysqlUser;
	public static String mysqlPass;
	
	public static int coinsKill;
	public static int coinsDeliver;
	public static int coinsDeliverTeammates;
	public static int coinsUnlockTeleports;
	public static int coinsWin;
	
	private Configuration() { }
	
	public static void setup(PluginConfig config) {
		boolean needSave = false;
		needSave |= setIfNotSet(config, "countdown-seconds", 30);
		needSave |= setIfNotSet(config, "mysql.host", "localhost");
		needSave |= setIfNotSet(config, "mysql.port", 3306);
		needSave |= setIfNotSet(config, "mysql.database", "bukkit");
		needSave |= setIfNotSet(config, "mysql.user", "root");
		needSave |= setIfNotSet(config, "mysql.pass", "");
		
		needSave |= setIfNotSet(config, "coins.kill", 10);
		needSave |= setIfNotSet(config, "coins.deliver", 50);
		needSave |= setIfNotSet(config, "coins.deliver-teammates", 20);
		needSave |= setIfNotSet(config, "coins.unlock-teleports", 50);
		needSave |= setIfNotSet(config, "coins.win", 250);
		
		
		if (needSave) {
			try {
				config.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Legge tutte le impostazioni
		countDown = config.getInt("countdown-seconds");
		mysqlHost = config.getString("mysql.host");
		mysqlPort = config.getInt("mysql.port");
		mysqlDatabase = config.getString("mysql.database");
		mysqlUser = config.getString("mysql.user");
		mysqlPass = config.getString("mysql.pass");
		
		coinsKill = config.getInt("coins.kill");
		coinsDeliver = config.getInt("coins.deliver");
		coinsDeliverTeammates = config.getInt("coins.deliver-teammates");
		coinsUnlockTeleports  = config.getInt("coins.unlock-teleports");
		coinsWin  = config.getInt("coins.win");
		
		
		lobby = new Location(GoldRush.mainWorld, config.getDouble("lobby.x"), config.getDouble("lobby.y"), config.getDouble("lobby.z"), (float) config.getDouble("lobby.yaw"), (float) config.getDouble("lobby.pitch"));
		
		// Killers
		if (config.isSet(ConfigNodes.FIRST_KILLER_HEAD)) {
			firstKillerHead = getBlockFromConfigSection(config, ConfigNodes.FIRST_KILLER_HEAD);
		}
		if (config.isSet(ConfigNodes.SECOND_KILLER_HEAD)) {
			secondKillerHead = getBlockFromConfigSection(config, ConfigNodes.SECOND_KILLER_HEAD);
		}
		if (config.isSet(ConfigNodes.THIRD_KILLER_HEAD)) {
			thirdKillerHead = getBlockFromConfigSection(config, ConfigNodes.THIRD_KILLER_HEAD);
		}
		
		if (config.isSet(ConfigNodes.FIRST_KILLER_SIGN)) {
			firstKillerSign = getBlockFromConfigSection(config, ConfigNodes.FIRST_KILLER_SIGN);
		}
		if (config.isSet(ConfigNodes.SECOND_KILLER_SIGN)) {
			secondKillerSign = getBlockFromConfigSection(config, ConfigNodes.SECOND_KILLER_SIGN);
		}
		if (config.isSet(ConfigNodes.THIRD_KILLER_SIGN)) {
			thirdKillerSign = getBlockFromConfigSection(config, ConfigNodes.THIRD_KILLER_SIGN);
		}
		
		
		// Winners
		if (config.isSet(ConfigNodes.FIRST_WINNER_HEAD)) {
			firstWinnerHead = getBlockFromConfigSection(config, ConfigNodes.FIRST_WINNER_HEAD);
		}
		if (config.isSet(ConfigNodes.SECOND_WINNER_HEAD)) {
			secondWinnerHead = getBlockFromConfigSection(config, ConfigNodes.SECOND_WINNER_HEAD);
		}
		if (config.isSet(ConfigNodes.THIRD_WINNER_HEAD)) {
			thirdWinnerHead = getBlockFromConfigSection(config, ConfigNodes.THIRD_WINNER_HEAD);
		}
		
		if (config.isSet(ConfigNodes.FIRST_WINNER_SIGN)) {
			firstWinnerSign = getBlockFromConfigSection(config, ConfigNodes.FIRST_WINNER_SIGN);
		}
		if (config.isSet(ConfigNodes.SECOND_WINNER_SIGN)) {
			secondWinnerSign = getBlockFromConfigSection(config, ConfigNodes.SECOND_WINNER_SIGN);
		}
		if (config.isSet(ConfigNodes.THIRD_WINNER_SIGN)) {
			thirdWinnerSign = getBlockFromConfigSection(config, ConfigNodes.THIRD_WINNER_SIGN);
		}
		
	}
	
	public static boolean setIfNotSet(PluginConfig config, String path, Object val) {
		if (!config.isSet(path)) {
			config.set(path, val);
			return true;
		}
		
		return false;
	}
	
	public static Block getBlockFromConfigSection(FileConfiguration config, String section) {
		return GoldRush.mainWorld.getBlockAt(config.getInt(section + ".x"), config.getInt(section + ".y"), config.getInt(section + ".z"));
	}
	
	public static void setBlockToConfigSection(FileConfiguration config, String section, Block block) {
		config.set(section + ".x", block.getX());
		config.set(section + ".y", block.getY());
		config.set(section + ".z", block.getZ());
	}
	
	public static void saveBlock(FileConfiguration config, String section, Block block) {
		setBlockToConfigSection(config, section, block);
		GoldRush.plugin.saveConfig();
	}
	
	public static void setLobby(Location loc) {
		FileConfiguration config = GoldRush.plugin.getConfig();
		Configuration.lobby = loc;
		config.set("lobby.x", lobby.getX());
		config.set("lobby.y", lobby.getY());
		config.set("lobby.z", lobby.getZ());
		config.set("lobby.yaw", lobby.getYaw());
		config.set("lobby.pitch", lobby.getPitch());
		GoldRush.plugin.saveConfig();
	}

	
	public static FileConfiguration getConfig() {
		return GoldRush.plugin.getConfig();
	}
}
