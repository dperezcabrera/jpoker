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

import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import static org.poker.api.core.Deck.getAllCards;

/**
 *
 * @author David Perez Cabrera <dperezcabrera@gmail.com>
 */
public final class CardUtil4Testing {

    private static final String ARGUMENTO_NO_VALIDO = "Argumento {} no valido";
    private static final int CARD_STRING_LENGTH = 2;
    private static final char SEPARATOR = ' ';
    private static final Map<String, Card> STRING_TO_CARD = getAllCards().stream().collect(Collectors.groupingBy(Card::toString, Collectors.reducing(null, (c, t) -> t)));

    private CardUtil4Testing() {
    }
    
    public static Card fromString(String s) {
        Card result = null;
        if (s != null) {
            result = STRING_TO_CARD.get(s);
        }
        return result;
    }

    public static Card[] fromStringCards(String s) {
        StringTokenizer st = new StringTokenizer(s);
        Card[] result = new Card[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            result[i++] = fromString(st.nextToken());
        }
        return result;
    }
    
    public static String toStringCards(Card... c) {
        String result = null;
        if (c != null && c.length > 0) {
            StringBuilder sb = new StringBuilder(c.length * (CARD_STRING_LENGTH + 1) - 1);
            sb.append(c[0]);
            for (int i = 1; i < c.length; i++) {
                sb.append(SEPARATOR);
                sb.append(c[i]);
            }
            result = sb.toString();
        }
        return result;
    }
}
