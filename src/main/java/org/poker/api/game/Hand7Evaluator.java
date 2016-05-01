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

import java.util.List;
import net.jcip.annotations.NotThreadSafe;
import org.poker.api.core.Card;
import org.poker.api.core.IHandEvaluator;
import static org.poker.api.game.TexasHoldEmUtil.COMMUNITY_CARDS;
import static org.poker.api.game.TexasHoldEmUtil.PLAYER_CARDS;
import org.util.combinatorial.Combination;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @since 1.0.0
 * 
 */
@NotThreadSafe
public class Hand7Evaluator {

    public static final int TOTAL_CARDS = PLAYER_CARDS + COMMUNITY_CARDS;
    private final int[] combinatorialBuffer = new int[COMMUNITY_CARDS];
    private final Combination combinatorial = new Combination(COMMUNITY_CARDS, TOTAL_CARDS);
    private final IHandEvaluator evaluator;
    private final Card[] evalBuffer = new Card[COMMUNITY_CARDS];
    private final Card[] cards = new Card[TOTAL_CARDS];
    private int communityCardsValue = 0;

    public Hand7Evaluator(IHandEvaluator evaluator) {
        this.evaluator = evaluator;
    }
    
    public int eval(Card c0, Card c1) {
        cards[COMMUNITY_CARDS] = c0;
        cards[COMMUNITY_CARDS + 1] = c1;
        return evalCards();
    }

    public void setCommunityCards(List<Card> cc) {
        int i = 0;
        for (Card card : cc) {
            evalBuffer[i] = card;
            cards[i++] = card;
        }
        communityCardsValue = evaluator.eval(evalBuffer);
    }
    
    static Card[] copy(Card[] src, Card[] target, int[] positions) {
        int i = 0;
        for (int p : positions) {
            target[i++] = src[p];
        }
        return target;
    }

    private int evalCards() {
        combinatorial.clear();
        combinatorial.next(combinatorialBuffer);
        int result = communityCardsValue;
        while (combinatorial.hasNext()) {
            result = Math.max(result, evaluator.eval(copy(cards, evalBuffer, combinatorial.next(combinatorialBuffer))));
        }
        return result;
    }
}
