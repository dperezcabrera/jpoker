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
package org.poker.api.core;

import net.jcip.annotations.Immutable;
import org.util.exceptions.ExceptionUtil;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @since 1.0.0
 */
@Immutable
public final class Card {

    private static final String STRING_RANK_CARDS = "23456789TJQKA";

    public enum Suit {

        SPADE('♠'), HEART('♥'), DIAMOND('♦'), CLUB('♣');
        
        private final char c;

        private Suit(char c) {
            this.c = c;
        }

    }

    public enum Rank {

        TWO, TRHEE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE
    }

    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        ExceptionUtil.checkNullArgument(rank, "rank");
        ExceptionUtil.checkNullArgument(suit, "suit");
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    @Override
    public int hashCode() {
        return rank.ordinal() * Suit.values().length + suit.ordinal();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = true;
        if (this != obj) {
            result = false;
            if (obj != null && getClass() == obj.getClass()) {
                result = hashCode() == ((Card) obj).hashCode();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return STRING_RANK_CARDS.substring(rank.ordinal(), rank.ordinal() + 1) + suit.c;
    }
}
