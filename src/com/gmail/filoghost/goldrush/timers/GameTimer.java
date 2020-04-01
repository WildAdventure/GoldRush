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

import org.bukkit.ChatColor;

import wild.api.scheduler.Countdowns;
import wild.api.util.UnitFormatter;

import com.gmail.filoghost.goldrush.constants.Lang;
import com.gmail.filoghost.goldrush.objects.Arena;
import com.gmail.filoghost.goldrush.utils.Debug;

public class GameTimer extends TimerMaster {
	
	private Integer countDown; // Null checks
	private Arena arena;

	
	
	public GameTimer(Arena arena) {
		this.arena = arena;
	}

	
	@Override
	public void startNewTask() {
		Debug.ln("E' incominciato il countdown nell'arena " + arena.getName());
		countDown = arena.getCountdownSeconds();
		super.startNewTask();
	}
	
	@Override
	public void stopTask() {
		Debug.ln("E' finito countdown nell'arena " + arena.getName());
		super.stopTask();
	}

	public long getDelayBeforeFirstRun() {
		return 0;
	}

	public long getDelayBetweenEachRun() {
		return 20;
	}
	
	
	public void resetTimer(int countDownInSeconds) {
		countDown = countDownInSeconds;
		stopTask();
		startNewTask();
	}
	
	
	public int getRemainingSeconds() {
		return countDown;
	}
	
	
	public void setRemainingSeconds(int seconds) {
		if (super.isStarted()) {
			countDown = seconds;
		}
	}
	
	
	public String getFormattedTime_() {
		if (countDown >= 60) {
			
			if (countDown % 15 == 0) {
				
			int minutes = countDown / 60;
			String minutesLang = "minuti";
			if (minutes == 1) minutesLang = "minuto";
				
			return "§a[" + minutes + " " + minutesLang + "]";
			
			}
		
		// Meno di 1 minuto
		} else {
			
			// Più di 10 secondi
			if (countDown > 10) {
				if (countDown % 10 == 0) {
					int seconds = countDown;
					String secondsLang = "secondi";
					if (seconds == 1) secondsLang = "secondo";
					return "§a[" + seconds + " " + secondsLang + "]";
				}
				
			// Meno di 10 secondi
			} else {
				int seconds = countDown;
				String secondsLang = "secondi";
				if (seconds == 1) secondsLang = "secondo";
				return "§a[" + seconds + " " + secondsLang + "]";
			}
		}
		
		return arena.getState().getName();
	}
	
	public String getFormattedTime() {
		if (countDown < 60 && countDown > 5) {
			return UnitFormatter.formatMinutesOrSeconds((countDown / 10) * 10 + 10);
		} else {
			return UnitFormatter.formatMinutesOrSeconds(countDown);
		}
	}
	
	public void run() {
		// Countdown finito
		if (countDown < 1) {
			this.stopTask();
			arena.start();
			return;
		}
		
		if (Countdowns.shouldAnnounceCountdown(countDown)) {
			String formattedTime = Countdowns.announceStartingCountdown(Lang.GOLDRUSH_PREFIX, arena.getGamers().keySet(), countDown);
			arena.defaultSign(ChatColor.GREEN + "[" + formattedTime + "]");
		}
	
		countDown--;
	}
}
