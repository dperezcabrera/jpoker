/*
 * Copyright (C) 2015 David Pérez Cabrera <dperezcabrera@gmail.com>
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
package z.org.poker.ejemplo19;

import java.util.HashSet;
import java.util.Set;
import org.poker.api.core.Card;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public final class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private Main(){
    }

    private static void insert(Set<Card> cards, Card card) {
        if (!cards.contains(card)) {
            LOGGER.debug("insertamos la carta: {}", card);
            cards.add(card);
        } else {
            LOGGER.debug("la carta: {} ya estaba en el conjunto", card);
        }
    }

    public static void main(String[] args) {
        Set<Card> cards = new HashSet<>();
        Card[] cards2Insert = {
            new Card(Card.Suit.CLUB, Card.Rank.ACE),
            new Card(Card.Suit.CLUB, Card.Rank.TWO),
            new Card(Card.Suit.CLUB, Card.Rank.TRHEE),
            new Card(Card.Suit.CLUB, Card.Rank.ACE),
            new Card(Card.Suit.CLUB, null),};
        for (Card card : cards2Insert) {
            insert(cards, card);
        }
    }
}
