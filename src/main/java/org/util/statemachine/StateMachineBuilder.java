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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.ThreadSafe;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @since 2.0.0
 *
 * @param <S>
 * @param <T>
 */
@ThreadSafe
public class StateMachineBuilder<S extends Enum, T> {

    private static final IChecker<?> DEFAULT_CHECKER = c -> true;

    private S initState = null;
    private Map<S, IStateTrigger<T>> triggersByState;
    private Map<S, List<Transition<S, T>>> transitions;

    private StateMachineBuilder() {
        init();
    }

    public static<S extends Enum, T> StateMachineBuilder<S, T> create(Class<S> statesType, Class<T> type){
        return new StateMachineBuilder<>();
    }
    
    private void init() {
        triggersByState = new HashMap<>();
        transitions = new HashMap<>();
    }

    public synchronized StateMachineBuilder<S, T> initState(S initState) {
        this.initState = initState;
        return this;
    }

    public synchronized StateMachineBuilder<S, T> stateTrigger(S state, IStateTrigger<T> trigger) {
        triggersByState.put(state, trigger);
        return this;
    }

    private synchronized StateMachineBuilder<S, T> addTransition(Transition<S, T> transition) {
        S origin = transition.getOrigin();
        List<Transition<S, T>> listTransitions = transitions.get(origin);
        if (listTransitions == null) {
            listTransitions = new ArrayList<>();
            transitions.put(origin, listTransitions);
        }
        listTransitions.add(transition);
        return this;
    }

    public StateMachineBuilder<S, T> transition(Transition<S, T> transition) {
        return addTransition(transition);
    }

    public StateMachineBuilder<S, T> transition(S origin, S target, IChecker<T> checker) {
        return addTransition(new Transition<>(origin, target, checker));
    }

    public StateMachineBuilder<S, T> transition(S origin, S target) {
        return addTransition(new Transition<>(origin, target, (IChecker<T>) DEFAULT_CHECKER));
    }

    public synchronized StateMachine<S, T> build() {
        StateMachine<S, T> result = new StateMachine<>(initState, triggersByState, transitions);
        init();
        return result;
    }    
}
