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

import net.jcip.annotations.NotThreadSafe;
import org.poker.api.core.Card;
import org.poker.api.game.TexasHoldEmUtil.PlayerState;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @since 1.0.0
 */
@NotThreadSafe
public class PlayerInfo {
    
    private String name;
    private long chips;
    private long bet;
    private final Card[] cards = new Card[TexasHoldEmUtil.PLAYER_CARDS];
    private PlayerState state;
    private int errors;

    public boolean isActive() {
        return state.isActive();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getChips() {
        return chips;
    }

    public void setChips(long chips) {
        this.chips = chips;
    }

    public void addChips(long chips) {
        this.chips += chips;
    }

    public long getBet() {
        return bet;
    }

    public void setBet(long bet) {
        this.bet = bet;
    }

    public Card[] getCards() {
        return new Card[]{cards[0], cards[1]};
    }

    public void setCards(Card[] cards) {
        this.cards[0] = cards[0];
        this.cards[1] = cards[1];
    }

    public PlayerState getState() {
        return state;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }

    public int getErrors() {
        return errors;
    }

    public void setErrors(int errors) {
        this.errors = errors;
    }

    public Card getCard(int index) {
        return cards[index];
    }

    public void setCards(Card card0, Card card1) {
        this.cards[0] = card0;
        this.cards[1] = card1;
    }

    @Override
    public String toString() {
        return "{class:'PlayerInfo', name:'" + name + "', chips:" + chips + ", bet:" + bet + ", cards:[" + cards[0] + ", " + cards[1] + "], state:'" + state + "', errors:" + errors + '}';
    }
}
