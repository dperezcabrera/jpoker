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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @since 1.0.0
 * 
 * @param <S>
 * @param <T>
 */
@NotThreadSafe
public class StateMachineInstance<S extends Enum, T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StateMachineInstance.class);
    private final T context;
    private final StateMachine<S, T> parent;
    private S state;
    private boolean finish;
    private boolean pause;

    public StateMachineInstance(T context, StateMachine<S, T> parent, S state) {
        this.context = context;
        this.parent = parent;
        this.state = state;
        this.finish = false;
    }

    public boolean isFinish() {
        return finish;
    }

    public StateMachineInstance<S, T> execute() {
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

    private S executeState() {
        LOGGER.debug("state \"{}\" executing...", state);
        pause = !parent.getTrigger(state).execute(context);
        S result = state;
        if (!pause) {
            LOGGER.debug("state \"{}\" [executed]", state);
            for (Transition<S, T> transition : parent.getTransitionsByOrigin(state)) {
                if (transition.getChecker().check(context)) {
                    return transition.getTarget();
                }
            }
            result = null;
        } else {
            LOGGER.debug("state \"{}\"  [paused]", state);
        }
        return result;
    }
}
