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
    private static final IStateTrigger DEFAULT_TRIGGER = context -> true;
    
    private final S initState;
    private final Map<S, IStateTrigger<T>> triggersByState;
    private final Map<S, List<Transition<S, T>>> transitions;

    StateMachine(S initState, Map<S, IStateTrigger<T>> triggersByState, Map<S, List<Transition<S, T>>> transitions) {
        this.initState = initState;
        this.triggersByState = new HashMap<>(triggersByState);
        this.transitions = new HashMap<>(transitions.size());
        transitions.entrySet().stream().forEach(e -> this.transitions.put(e.getKey(), new ArrayList<>(e.getValue())));
    }
    
    List<Transition<S, T>> getTransitionsByOrigin(S state) {
        List<Transition<S, T>> result = transitions.get(state);
        if (result == null) {
            result = Collections.emptyList();
        }
        return result;
    }

    public IStateTrigger<T> getTrigger(S state) {
        IStateTrigger<T> result = triggersByState.get(state);
        if (result == null){
            result = (IStateTrigger<T>) DEFAULT_TRIGGER;
        }
        return result;
    }

    public StateMachineInstance<S, T> startInstance(T data) {
        return new StateMachineInstance(data, this, initState).execute();
    }
}
