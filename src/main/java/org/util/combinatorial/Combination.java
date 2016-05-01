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
package org.util.combinatorial;

import net.jcip.annotations.NotThreadSafe;
import org.util.exceptions.ExceptionUtil;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @since 1.0.0
 */
@NotThreadSafe
public class Combination implements ICombinatorial {

    private final int items;
    private final int[] indexes;

    public Combination(int subItems, int items) {
        ExceptionUtil.checkMinValueArgument(subItems, 1, "subItems");
        ExceptionUtil.checkMinValueArgument(items, subItems, "items");
        this.indexes = new int[subItems];
        this.items = items;
        init();
    }

    @Override
    public long combinations() {
        return combinations(indexes.length, items);
    }

    @Override
    public int size() {
        return indexes.length;
    }

    public int getSubItems() {
        return indexes.length;
    }

    public int getItems() {
        return items;
    }

    private boolean hasNext(int index) {
        return indexes[index] + (indexes.length - index) < items;
    }

    private void move(int index) {
        if (hasNext(index)) {
            indexes[index]++;
            int last = indexes[index];
            for (int i = index + 1; i < indexes.length; i++) {
                this.indexes[i] = ++last;
            }
        } else {
            move(index - 1);
        }
    }

    @Override
    public int[] next(int[] items) {
        if (hasNext()) {
            move(indexes.length - 1);
            System.arraycopy(indexes, 0, items, 0, indexes.length);
        }
        return items;
    }

    @Override
    public boolean hasNext() {
        return hasNext(0) || hasNext(indexes.length - 1);
    }

    private void init() {
        int index = indexes.length;
        for (int i = 0; i < indexes.length; i++) {
            this.indexes[i] = i;
        }
        this.indexes[index - 1]--;
    }

    @Override
    public void clear() {
        init();
    }

    public static long combinations(int subItems, int items) {
        long result = 1;
        int sub = Math.max(subItems, items - subItems);
        for (int i = sub + 1; i <= items; i++) {
            result = (result * i) / (i - sub);
        }
        return result;
    }
}
