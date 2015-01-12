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
package org.util.statemachine;

import net.jcip.annotations.NotThreadSafe;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @param <T>
 */
@NotThreadSafe
public class StateDecoratorBuilder<T> {

    private IState<T> state;

    private StateDecoratorBuilder(IState<T> state) {
        this.state = state;
    }

    public static <T> StateDecoratorBuilder<T> create(IState<T> state) {
        return new StateDecoratorBuilder<>(state);
    }

    public StateDecoratorBuilder<T> after(Runnable r) {
        this.state = new AfterStateDecorator<>(state, r);
        return this;
    }

    public StateDecoratorBuilder<T> before(Runnable r) {
        this.state = new BeforeStateDecorator<>(state, r);
        return this;
    }

    public IState<T> build() {
        return state;
    }

    public static <T> IState<T> after(IState<T> state, Runnable r) {
        return new AfterStateDecorator<>(state, r);
    }

    public static <T> IState<T> before(IState<T> state, Runnable r) {
        return new BeforeStateDecorator<>(state, r);
    }
}
