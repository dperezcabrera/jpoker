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

import org.util.exceptions.ExceptionUtil;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.jcip.annotations.NotThreadSafe;
import org.poker.api.core.Hands.Type;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @since 1.0.0
 */
@NotThreadSafe
public final class HandEvaluator implements IHandEvaluator {

    private static final int ENCODE_BASE = Card.Rank.ACE.ordinal() + 1;
    private static final int INDEXES_LENGTH = 2;
    private static final int RANK_INDEX = 0;
    private static final int REPEATS_INDEX = 1;
    private static final Type[][] MATRIX_TYPES = {
        {Type.HIGH_CARD},
        {Type.ONE_PAIR, Type.TWO_PAIR},
        {Type.THREE_OF_A_KIND, Type.FULL_HOUSE},
        {Type.FOUR_OF_A_KIND}
    };
    private final int[][] indexes = new int[Hands.CARDS][INDEXES_LENGTH];
    private final int[] ranks = new int[ENCODE_BASE];
    private final int[] suits = new int[Card.Suit.values().length];
    private boolean isStraight = false;
    private boolean isFlush = false;

    @Override
    public int eval(Card[] cards) {
        ExceptionUtil.checkArrayLengthArgument(cards, "cards", Hands.CARDS);
        isFlush = false;
        Arrays.fill(suits, 0);
        Arrays.fill(ranks, 0);
        int index = 0;
        Set<Card> previousCards = new HashSet<>(Hands.CARDS);
        for (Card card : cards) {
            ExceptionUtil.checkNullArgument(card, "card[" + (index++) + "]");
            ExceptionUtil.checkArgument(previousCards.contains(card), "La carta {} está repetida.", card);
            previousCards.add(card);
            ranks[card.getRank().ordinal()]++;
            suits[card.getSuit().ordinal()]++;
        }
        isFlush = suits[cards[0].getSuit().ordinal()] == Hands.CARDS;
        isStraight = false;
        int straightCounter = 0;
        int j = 0;
        for (int i = ranks.length - 1; i >= 0; i--) {
            if (ranks[i] > 0) {
                straightCounter++;
                isStraight = straightCounter == Hands.CARDS;
                indexes[j][RANK_INDEX] = i;
                indexes[j][REPEATS_INDEX] = ranks[i];
                upIndex(j++);
            } else {
                straightCounter = 0;
            }
        }
        isStraight = isStraight || checkStraight5toAce(straightCounter);
        return calculateHandValue();
    }

    private void upIndex(int i) {
        int k = i;
        while (k > 0 && indexes[k - 1][REPEATS_INDEX] < indexes[k][REPEATS_INDEX]) {
            int[] temp = indexes[k - 1];
            indexes[k - 1] = indexes[k];
            indexes[k] = temp;
            k--;
        }
    }

    private boolean checkStraight5toAce(int straightCounter) {
        boolean straight5toAce = false;
        if (ranks[Card.Rank.ACE.ordinal()] == 1 && straightCounter == Hands.CARDS - 1) {
            straight5toAce = true;
            for (int i = 1; i < indexes.length; i++) {
                indexes[i - 1][RANK_INDEX] = indexes[i][RANK_INDEX];
            }
            indexes[indexes.length - 1][RANK_INDEX] = Card.Rank.ACE.ordinal();
        }
        return straight5toAce;
    }

    private int calculateHandValue() {
        Type type;
        if (isStraight) {
            type = isFlush ? Type.STRAIGHT_FLUSH : Type.STRAIGHT;
        } else if (isFlush) {
            type = Type.FLUSH;
        } else {
            type = MATRIX_TYPES[indexes[0][REPEATS_INDEX] - 1][indexes[1][REPEATS_INDEX] - 1];
        }
        return encodeValue(type, indexes);
    }

    private static int encodeValue(Type type, int[][] indexes) {
        int result = type.ordinal();
        int i = 0;
        int j = 0;
        while (j < Hands.CARDS) {
            for (int k = 0; k < indexes[i][REPEATS_INDEX]; k++) {
                result = result * ENCODE_BASE + indexes[i][RANK_INDEX];
                j++;
            }
            i++;
        }
        return result;
    }
}
