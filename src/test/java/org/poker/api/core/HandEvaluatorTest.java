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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.util.combinatorial.FactorialPermutation;
import org.util.combinatorial.ICombinatorial;
import org.util.combinatorial.Combination;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.poker.api.core.Deck.getAllCards;

/**
 *
 * @author David Perez Cabrera <dperezcabrera@gmail.com>
 */
public class HandEvaluatorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static Properties properties;

    @BeforeClass
    public static void setUpClass() throws IOException {
        properties = new Properties();
        properties.load(new FileInputStream("src/main/resources/org/poker/evalCache.properties"));
    }

    private static String combinationName(Card[] cards) {
        boolean color = true;
        Card.Suit suit = cards[0].getSuit();
        Arrays.sort(cards, (c0, c1) -> c1.getRank().ordinal() - c0.getRank().ordinal());
        for (Card card : cards) {
            if (card.getSuit() != suit) {
                color = false;
                break;
            }
        }
        StringBuilder sb = new StringBuilder(8 + cards.length * 2);
        sb.append(color ? "FLUSH" : "NO_FLUSH");
        for (Card card : cards) {
            sb.append('_');
            sb.append(card.toString().charAt(0));
        }
        return sb.toString();
    }

    private static Card[] getCards(List<Card> deck, int[] indexes) {
        Card[] result = new Card[indexes.length];
        int i = 0;
        for (int index : indexes) {
            result[i++] = deck.get(index);
        }
        return result;
    }
    
    /**
     * Test of eval method, of class HandEvaluator.
     */
    @Test
    public void testEvalException() {
        System.out.println("evalException");
        
        HandEvaluator instance = new HandEvaluator();
        thrown.expect(IllegalArgumentException.class);
         
        instance.eval(null);
    }
    
    /**
     * Test of eval method, of class HandEvaluator.
     */
    @Test
    public void testEvalException4Cards() {
        System.out.println("evalException4Cards");
        
        HandEvaluator instance = new HandEvaluator();
        Card[] cards = CardUtil4Testing.fromStringCards("2C 3C 4C 5C");
        thrown.expect(IllegalArgumentException.class);
        
        instance.eval(cards);
    }
    
    /**
     * Test of eval method, of class HandEvaluator.
     */
    @Test
    public void testEvalException6Cards() {
        System.out.println("evalException6Cards");
        
        HandEvaluator instance = new HandEvaluator();
        Card[] cards = CardUtil4Testing.fromStringCards("2C 3C 4C 5C 6C 7C");
        thrown.expect(IllegalArgumentException.class);
        instance.eval(cards);
    }
    
    /**
     * Test of eval method, of class HandEvaluator.
     */
    @Test
    public void testEvalExceptionNullCard() {
        System.out.println("evalExceptionNullCard");
        
        HandEvaluator instance = new HandEvaluator();
        Card[] cards = CardUtil4Testing.fromStringCards("2C 3C 4C 5C 6C");
        cards[3] = null;
        thrown.expect(IllegalArgumentException.class);
        
        instance.eval(cards);
    }
    
    /**
     * Test of eval method, of class HandEvaluator.
     */
    @Test
    public void testEvalExceptionRepeatCard() {
        System.out.println("evalExceptionRepeatCard");
        
        
        HandEvaluator instance = new HandEvaluator();
        Card[] cards = CardUtil4Testing.fromStringCards("2C 3C 4C 5C 3C");
        thrown.expect(IllegalArgumentException.class);

        instance.eval(cards);
    }
    
    /**
     * Test of eval method, of class HandEvaluator.
     */
    @Test
    public void testEval() {
        System.out.println("eval");
        HandEvaluator instance = new HandEvaluator();
        List<Card> deck = getAllCards();
        Combination combinatorial = new Combination(Hands.CARDS, deck.size());
        int[] i = new int[Hands.CARDS];
        Card[] cards;
        while (combinatorial.hasNext()) {
            combinatorial.next(i);
            cards = getCards(deck, i);
            int result = instance.eval(cards);
            String name = combinationName(cards);
            String combCards = CardUtil4Testing.toStringCards(cards);
            assertEquals("eval: '" + combCards + "' como: " + name, properties.getProperty(name), String.valueOf(result));
        }
    }

    /**
     * Test of eval method, of class HandEvaluator.
     */
    @Ignore
    @Test
    public void testEvalFullTesting() {
        System.out.println("evalFullTesting");
        HandEvaluator instance = new HandEvaluator();
        List<Card> deck = getAllCards();
        Combination combinatorial = new Combination(Hands.CARDS, deck.size());
        int[] i = new int[Hands.CARDS];
        Card[] cards;
        while (combinatorial.hasNext()) {
            combinatorial.next(i);
            cards = getCards(deck, i);
            int result = instance.eval(cards);
            String name = combinationName(cards);
            String combCards = CardUtil4Testing.toStringCards(cards);
            assertEquals("eval: '" + combCards + "' como: " + name, properties.getProperty(name), String.valueOf(result));
            FactorialPermutation permutations = new FactorialPermutation(Hands.CARDS);
            permutations.next(i);
            while (permutations.hasNext()) {
                permutations.next(i);
                cards = getCards(Arrays.asList(cards), i);
                int newResult = instance.eval(cards);
                assertEquals("eval: '" + CardUtil4Testing.toStringCards(cards) + " != " + combCards, result, newResult);
            }
        }
    }

    /**
     * Test of eval method, of class HandEvaluator.
     *
     * @throws java.lang.Exception
     * @throws java.util.concurrent.ExecutionException
     */
    @Ignore
    @Test
    public void testEvalFullTestingThreads() throws Exception {
        System.out.println("evalFullTestingThreads");
        List<Card> deck = getAllCards();
        Combination combinatorial = new Combination(Hands.CARDS, deck.size());
        int nThreads = Runtime.getRuntime().availableProcessors();
        List<Future> list = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        for (int i = 0; i < nThreads; i++) {
            list.add(executor.submit(new TestEvalHelper(combinatorial, deck)));
        }
        for (Future fut : list) {
            fut.get();
        }
        executor.shutdown();
    }

    private class TestEvalHelper implements Callable {

        private final ICombinatorial combinatorial;
        private final List<Card> deck;

        public TestEvalHelper(ICombinatorial combinatorial, List<Card> deck) {
            this.combinatorial = combinatorial;
            this.deck = deck;
        }

        private boolean hasNextAndNext(int[] i) {
            boolean result;
            synchronized (combinatorial) {
                result = combinatorial.hasNext();
                if (result) {
                    combinatorial.next(i);
                }
            }
            return result;
        }

        @Override
        public Object call() {
            HandEvaluator instance = new HandEvaluator();
            Card[] cards;
            int[] i = new int[Hands.CARDS];
            while (hasNextAndNext(i)) {
                cards = getCards(deck, i);
                int result = instance.eval(cards);
                String name = combinationName(cards);
                String combCards = CardUtil4Testing.toStringCards(cards);
                assertEquals("eval: '" + combCards + "' como: " + name, properties.getProperty(name), String.valueOf(result));
                FactorialPermutation permutations = new FactorialPermutation(Hands.CARDS);
                permutations.next(i);
                while (permutations.hasNext()) {
                    permutations.next(i);
                    cards = getCards(Arrays.asList(cards), i);
                    int newResult = instance.eval(cards);
                    assertEquals("eval: '" + CardUtil4Testing.toStringCards(cards) + " != " + combCards, result, newResult);
                }
            }
            return null;
        }
    }
}
