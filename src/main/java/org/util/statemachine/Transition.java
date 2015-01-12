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
package org.util.statemachine;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @param <T>
 */
public class Transition<T> {

    private final IState<T> origin;
    private final IState<T> target;
    private final IChecker<T> checker;

    public Transition(IState<T> origin, IState<T> target, IChecker<T> checker) {
        this.origin = origin;
        this.target = target;
        this.checker = checker;
    }

    public IState<T> getOrigin() {
        return origin;
    }

    public IState<T> getTarget() {
        return target;
    }

    public IChecker<T> getChecker() {
        return checker;
    }
}
