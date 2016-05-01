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
package org.poker.engine.model;

import org.poker.api.game.BetCommand;
import org.poker.api.game.PlayerInfo;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @since 1.0.0
 */
public class PlayerEntity extends PlayerInfo {

    private int handValue = 0;
    private BetCommand betCommand;
    private boolean showCards;
    private int roudsSurvival;
    private long lastRoundChips;

    public int getRoudsSurvival() {
        return roudsSurvival;
    }

    public void setRoudsSurvival(int roudsSurvival) {
        this.roudsSurvival = roudsSurvival;
    }

    public long getLastRoundChips() {
        return lastRoundChips;
    }

    public void setLastRoundChips(long lastRoundChips) {
        this.lastRoundChips = lastRoundChips;
    }

    public boolean showCards() {
        return showCards;
    }

    public void showCards(boolean showCards) {
        this.showCards = showCards;
    }

    public BetCommand getBetCommand() {
        return betCommand;
    }

    public void setBetCommand(BetCommand betCommand) {
        this.betCommand = betCommand;
    }

    public int getHandValue() {
        return handValue;
    }

    public void setHandValue(int handValue) {
        this.handValue = handValue;
    }
}
