/* 
 * Copyright (C) 2015 David Perez Cabrera
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

import net.jcip.annotations.NotThreadSafe;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
@NotThreadSafe
public class FactorialPermutation implements ICombinatorial {

    private final int []items;
    private long permutation;
    private final long maxPermutation;

    public FactorialPermutation(int size) {
        this.items = new int[size];
        this.maxPermutation = combinations(size);
        this.permutation = 0;
        setPermutationState(0);
    }

    @Override
    public int size() {
        return items.length;
    }

    @Override
    public void clear() {
        permutation = 0;
        setPermutationState(0);
    }

    private void setPermutationState(long permutation) {
        int size = items.length - 1;
        int item = size;
        long fact = combinations(item);
        long value = permutation;
        for (int i = 0; i < items.length; i++) {
            items[i] = i;
        }
        while (item > 0) {
            swap(items, (int) (value / fact), item);
            value %= fact;
            fact /= item;
            item--;
        }
    }

    @Override
    public int[] next(int []items) {
        if (hasNext()) {
            setPermutationState(permutation);
            System.arraycopy(this.items, 0, items, 0, items.length);
            permutation++;
        }
        return items;
    }

    @Override
    public boolean hasNext() {
        return permutation < maxPermutation;
    }

    private static void swap(int []items, int p0, int p1) {
        int swap = items[p0];
        items[p0] = items[p1];
        items[p1] = swap;
    }

    public static long combinations(int value) {
        long result = 1;
        if (value > 1) {
            result = value * combinations(value - 1);
        }
        return result;
    }

    @Override
    public long combinations() {
        return combinations(items.length);
    }
}
