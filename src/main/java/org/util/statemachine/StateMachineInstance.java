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

import net.jcip.annotations.NotThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @param <T>
 */
@NotThreadSafe
public class StateMachineInstance<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StateMachineInstance.class);
    private final T context;
    private final StateMachine<T> parent;
    private IState<T> state;
    private boolean finish;
    private boolean pause;

    public StateMachineInstance(T context, StateMachine<T> parent, IState<T> state) {
        this.context = context;
        this.parent = parent;
        this.state = state;
        this.finish = false;
    }

    public boolean isFinish() {
        return finish;
    }

    public StateMachineInstance<T> execute() {
        this.pause = false;
        while (state != null && !pause) {
            state = executeState();
        }
        finish = state == null;
        if (finish) {
            LOGGER.debug("execute finish");
        }
        return this;
    }

    public T getContext() {
        return context;
    }

    private IState<T> executeState() {
        LOGGER.debug("state \"{}\" executing...", state.getName());
        pause = !state.execute(context);
        IState<T> result = state;
        if (!pause) {
            LOGGER.debug("state \"{}\" [executed]", state.getName());
            for (Transition<T> transition : parent.getTransitionsByOrigin(state)) {
                if (transition.getChecker().check(context)) {
                    return transition.getTarget();
                }
            }
            result = parent.getDefaultTransition(state);
        } else {
            LOGGER.debug("state \"{}\"  [paused]", state.getName());
        }
        return result;
    }
}
