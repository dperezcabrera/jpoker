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

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class CombinationContructorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    /**
     * Test of constructor, of class Combination.
     */
    @Test
    public void testConstructor() {
        System.out.println("Combination(2,4)");
        int subItems = 2;
        int items = 4;
        long expectCombinations = 6L;
        Combination instance = new Combination(subItems, items);
        assertEquals(expectCombinations, instance.combinations());
        assertEquals(subItems, instance.size());
    }

    /**
     * Test of constructor, of class Combination.
     */
    @Test
    public void testContructorSubItemError() {
        System.out.println("Combination(0,1)");
        int subItems = 0;
        int items = 1;
        
        thrown.expect(IllegalArgumentException.class);
        
        Combination instance = new Combination(subItems, items);
    }

    /**
     * Test of constructor, of class Combination.
     */
    @Test
    public void testContructorItemError() {
        System.out.println("Combination(5,1)");
        int subItems = 5;
        int items = 1;
        
        thrown.expect(IllegalArgumentException.class);
        
        Combination instance = new Combination(subItems, items);
    }
}
