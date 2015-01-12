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
package org.util.combinatorial;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
@RunWith(value = Parameterized.class)
public class FactorialPermutationTest {

    private final int items;
    private final int combinationsExpected;

    public FactorialPermutationTest(int items, int combinationsExpected) {
        this.items = items;
        this.combinationsExpected = combinationsExpected;
    }

    @Parameters
    public static Collection<Object[]> data() {
        Object data[][] = {
            {1, 1},
            {2, 2},
            {3, 6},
            {4, 24},
            {5, 120}, 
        };
        return Arrays.asList(data);
    }

    /**
     * Test of combinations method, of class FactorialPermutation.
     */
    @Test
    public void testCombinations() {
        System.out.println("combinations: items: " + items + ", ExpectedResult: " + combinationsExpected);
        long result = FactorialPermutation.combinations(items);
        assertEquals(combinationsExpected, result);
    }

    /**
     * Invariant test of class FactorialPermutation.
     */
    @Test
    public void testFullTesting() {
        System.out.println("FullTesting: items: " + items + ", combinations: " + combinationsExpected);
        FactorialPermutation c = new FactorialPermutation(items);
        int sizeResult = c.size();
        assertEquals("Size assert, expected: <" + items + "> and result: <" + sizeResult + ">", items, sizeResult);
        Set<Integer> set = new HashSet<>(items);
        int results[][] = new int[combinationsExpected][];

        for (int i = 0; i < combinationsExpected; i++) {
            assertTrue("hasNext assert-" + i + " from " + combinationsExpected, c.hasNext());
            results[i] = new int[items];
            c.next(results[i]);
            set.clear();
            for (int j = 0; j < items; j++) {
                assertThat(results[i][j]).isBetween(0, items - 1);
                assertThat(set, not(hasItem(results[i][j])));
                set.add(results[i][j]);
            }
            // No repeat combinations assert
            for (int k = 0; k < i; k++) {
                assertThat(results[i], not(equalTo(results[k])));
            }
        }
        assertFalse("hasNext last assert", c.hasNext());

        int temp[] = new int[items];
        int expTemp[] = new int[items];
        Arrays.fill(temp, -1);
        Arrays.fill(expTemp, -1);
        
        // ignore next
        c.next(temp);
        assertArrayEquals("next after last assert", temp, expTemp);

        // after clear asserts
        temp = new int[items];
        c.clear();
        assertTrue("hasNext after clear assert", c.hasNext());
        for (int j = 0; j < combinationsExpected - 1; j++) {
            c.next(temp);
            // Check equals results for the same step
            assertArrayEquals(results[j], temp);
            assertTrue("hasNext after clear assert", c.hasNext());
        }
        c.next(temp);
        assertArrayEquals(results[combinationsExpected - 1], temp);

        assertFalse("hasNext last assert after clear", c.hasNext());
    }
}
