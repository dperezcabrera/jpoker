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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.NotThreadSafe;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @since 1.0.0
 * 
 * @param <S>
 * @param <T>
 */
@NotThreadSafe
public class StateMachine<S extends Enum, T> {

    private static final IStateTrigger<?> DEFAULT_TRIGGER = c -> true;
    private S initState = null;
    private final Map<S, IStateTrigger<T>> triggersByState = new HashMap<>();
    private final Map<S, S> defaultTransition = new HashMap<>();
    private final Map<S, List<Transition<S, T>>> transitions = new HashMap<>();
    
    List<Transition<S, T>> getTransitionsByOrigin(S state) {
        List<Transition<S, T>> result = transitions.get(state);
        if (result == null) {
            result = Collections.emptyList();
        }
        return result;
    }

    public void setInitState(S initState) {
        this.initState = initState;
    }

    public void setTrigger(S state, IStateTrigger<T> trigger) {
        triggersByState.put(state, trigger);
    }

    public IStateTrigger<T> getTrigger(S state) {
        IStateTrigger<T> result = triggersByState.get(state);
        if (result == null){
            result = (IStateTrigger<T>) DEFAULT_TRIGGER;
        }
        return result;
    }

    public S getDefaultTransition(S origin) {
        return defaultTransition.get(origin);
    }

    public void setDefaultTransition(S origin, S target) {
        this.defaultTransition.put(origin, target);
    }

    public void addTransition(Transition<S, T> transition) {
        S origin = transition.getOrigin();
        List<Transition<S, T>> listTransitions = transitions.get(origin);
        if (listTransitions == null) {
            listTransitions = new ArrayList<>();
            transitions.put(origin, listTransitions);
        }
        listTransitions.add(transition);
    }

    public void addTransition(S origin, S target, IChecker<T> checker) {
        addTransition(new Transition<>(origin, target, checker));
    }

    public StateMachineInstance<S, T> startInstance(T data) {
        return new StateMachineInstance(data, this, initState).execute();
    }
}
