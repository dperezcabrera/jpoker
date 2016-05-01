/*
 * Copyright (C) 2015 David Perez Cabrera <dperezcabrera@gmail.com>
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
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.poker.api.core.Deck.getAllCards;

/**
 *
 * @author David Perez Cabrera <dperezcabrera@gmail.com>
 */
public class CardTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testContructorGetSuit() {
        System.out.println("card() and getSuit()");
        Card.Suit expSuit = Card.Suit.CLUB;
        Card instance = new Card(expSuit, Card.Rank.TWO);
        Card.Suit suitResult = instance.getSuit();
        assertEquals(expSuit, suitResult);
    }

    @Test
    public void testContructorGetRank() {
        System.out.println("card() and getRank");
        Card.Rank expRank = Card.Rank.TWO;
        Card instance = new Card(Card.Suit.CLUB, expRank);
        Card.Rank rankResult = instance.getRank();
        assertEquals(expRank, rankResult);
    }

    @Test
    public void testContructorSuitNull() {
        System.out.println("card(SuitNull)");
        
        Card.Suit expSuit = null;
        Card.Rank expRank = Card.Rank.TWO;
        thrown.expect(IllegalArgumentException.class);
        
        Card instance = new Card(expSuit, expRank);
    }

    @Test
    public void testContructorRankNull() {
        
        System.out.println("card(RankNull)");
        Card.Suit expSuit = Card.Suit.CLUB;
        Card.Rank expRank = null;
        thrown.expect(IllegalArgumentException.class);
        
        Card instance = new Card(expSuit, expRank);
    }

    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        List<Card> allCards = getAllCards();
        Set<Integer> hashCodes = new HashSet<>(allCards.size());
        for (Card card : allCards) {
            assertThat(hashCodes, not(hasItem(card.hashCode())));
        }
    }

    @Test
    public void testEqualsOtherObjects() {
        System.out.println("equalsOtherObjects");
        Card card = new Card(Card.Suit.CLUB, Card.Rank.ACE);
        assertNotEquals("card: " + card + " != null", card, null);
        assertNotEquals("card: " + card + " != 0", card, 0);
        assertNotEquals("card: " + card + " != \"2C\"", card, "2C");
    }

    @Test
    public void testEquals() {
        System.out.println("equals");
        int i = 0;
        for (Card card0 : getAllCards()) {
            int j = 0;
            for (Card card1 : getAllCards()) {
                if (i == j) {
                    assertEquals(card0, card1);
                }
                j++;
            }
            i++;
        }
    }

    @Test
    public void testEqualsDistinct() {
        System.out.println("equals distinct");
        int i = 0;
        for (Card card0 : getAllCards()) {
            int j = 0;
            for (Card card1 : getAllCards()) {
                if (i != j) {
                    assertNotEquals(card0, card1);
                }
                j++;
            }
            i++;
        }
    }
}
