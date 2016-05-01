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
package org.util.statemachine;

import net.jcip.annotations.NotThreadSafe;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @since 1.0.0
 * 
 * @param <T>
 */
@NotThreadSafe
public class StateDecoratorBuilder<T> {

    private IStateTrigger<T> trigger;

    private StateDecoratorBuilder(IStateTrigger<T> state) {
        this.trigger = state;
    }

    public static <T> StateDecoratorBuilder<T> create(IStateTrigger<T> state) {
        return new StateDecoratorBuilder<>(state);
    }

    public StateDecoratorBuilder<T> after(Runnable r) {
        this.trigger = new AfterTriggerDecorator<>(trigger, r);
        return this;
    }

    public StateDecoratorBuilder<T> before(Runnable r) {
        this.trigger = new BeforeTriggerDecorator<>(trigger, r);
        return this;
    }

    public IStateTrigger<T> build() {
        return trigger;
    }

    public static <T> IStateTrigger<T> after(IStateTrigger<T> state, Runnable r) {
        return new AfterTriggerDecorator<>(state, r);
    }

    public static <T> IStateTrigger<T> before(IStateTrigger<T> state, Runnable r) {
        return new BeforeTriggerDecorator<>(state, r);
    }
}
