/* 
 * Copyright (C) 2016 David Pérez Cabrera <dperezcabrera@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.poker.api.game;

import java.io.Serializable;
import net.jcip.annotations.NotThreadSafe;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @since 1.0.0
 */
@NotThreadSafe
public class Settings implements Serializable{
    private static final long serialVersionUID = 1L;

    private int maxPlayers;
    private long time;
    private int maxErrors;
    private int maxRounds;
    private long playerChip;
    private long smallBlind;
    private int rounds4IncrementBlind;

    public Settings() {
        // Default constructor.
    }

    public Settings(Settings s) {
        this.maxPlayers = s.maxPlayers;
        this.time = s.time;
        this.maxErrors = s.maxErrors;
        this.playerChip = s.playerChip;
        this.smallBlind = s.smallBlind;
        this.maxRounds = s.maxRounds;
        this.rounds4IncrementBlind = s.rounds4IncrementBlind;
    }

    public int getMaxErrors() {
        return maxErrors;
    }

    public void setMaxErrors(int maxErrors) {
        this.maxErrors = maxErrors;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getPlayerChip() {
        return playerChip;
    }

    public void setPlayerChip(long playerChip) {
        this.playerChip = playerChip;
    }

    public long getSmallBlind() {
        return smallBlind;
    }

    public long getBigBlind() {
        return smallBlind * 2;
    }

    public void setSmallBlind(long smallBlind) {
        this.smallBlind = smallBlind;
    }

    public int getMaxRounds() {
        return maxRounds;
    }

    public void setMaxRounds(int maxRounds) {
        this.maxRounds = maxRounds;
    }

    public int getRounds4IncrementBlind() {
        return rounds4IncrementBlind;
    }

    public void setRounds4IncrementBlind(int rounds4IncrementBlind) {
        this.rounds4IncrementBlind = rounds4IncrementBlind;
    }

    @Override
    public String toString() {
        return "{class:'Settings', maxPlayers:" + maxPlayers + ", time:" + time + ", maxErrors:" + maxErrors + ", playerChip:" + playerChip + ", maxRounds:" + maxRounds + ", smallBlind:" + smallBlind + ", rounds4IncrementBlind:" + rounds4IncrementBlind + '}';
    }
}
