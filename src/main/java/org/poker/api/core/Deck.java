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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.jcip.annotations.NotThreadSafe;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @since 1.0.0
 */
@NotThreadSafe
public class Deck {

    private final List<Card> cards;
    private int index = 0;

    public Deck() {
        this.cards = getAllCards();
    }

    public Card obtainCard() {
        Card result = null;
        if (index < cards.size()) {
            result = cards.get(index);
            index++;
        }
        return result;
    }

    public void shuffle() {
        index = 0;
        Collections.shuffle(cards);
    }

    public static List<Card> getAllCards() {
        int numCards = Card.Suit.values().length * Card.Rank.values().length;
        List<Card> result = new ArrayList<>(numCards);
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                result.add(new Card(suit, rank));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return String.join("{class:'Deck', index:", Integer.toString(index), ", cards:", cards.toString(), "'");
    }
}
