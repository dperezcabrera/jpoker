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
package org.poker.api.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class DeckTest {

    /**
     * Test of obtainCard method, of class Deck.
     */
    @Test
    public void testObtainCard() {
        System.out.println("obtainCard");
        Deck instance = new Deck();
        Card expResult = null;
        Card result = instance.obtainCard();
        assertNotEquals(expResult, result);
    }

    /**
     * Test of getAllCards method, of class Deck.
     */
   @Test
    public void testGetAllCards() {
        System.out.println("getAllCards");
        List<Card> result = Deck.getAllCards();
        assertNotNull(result);
        int items = Card.Suit.values().length * Card.Rank.values().length;
        assertEquals(items, result.size());
        Set<Card> allCards = new HashSet<>(items);
        for (Card card : result) {
            assertNotNull(card);
            assertThat(allCards, not(hasItem(card)));
            allCards.add(card);
        }
    }
}
