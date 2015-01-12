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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.NotThreadSafe;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @param <T>
 */
@NotThreadSafe
public class StateMachine<T> {

    private IState<T> initState = null;
    private final Map<String, IState<T>> defaultTransition = new HashMap<>();
    private final Map<String, List<Transition<T>>> transitions = new HashMap<>();

    public StateMachine() {
    }

    List<Transition<T>> getTransitionsByOrigin(IState<T> state) {
        List<Transition<T>> result = transitions.get(state.getName());
        if (result == null) {
            result = Collections.emptyList();
        }
        return result;
    }

    public void setInitState(IState<T> initState) {
        this.initState = initState;
    }

    public IState<T> getDefaultTransition(IState<T> origin) {
        return defaultTransition.get(origin.getName());
    }

    public void setDefaultTransition(IState<T> origin, IState<T> target) {
        this.defaultTransition.put(origin.getName(), target);
    }

    public void addTransition(Transition<T> transition) {
        IState<T> origin = transition.getOrigin();
        List<Transition<T>> listTransitions = transitions.get(origin.getName());
        if (listTransitions == null) {
            listTransitions = new ArrayList<>();
            transitions.put(origin.getName(), listTransitions);
        }
        listTransitions.add(transition);
    }

    public void addTransition(IState<T> origin, IState<T> target, IChecker<T> checker) {
        addTransition(new Transition<>(origin, target, checker));
    }

    public StateMachineInstance<T> startInstance(T data) {
        return new StateMachineInstance(data, this, initState).execute();
    }
}
