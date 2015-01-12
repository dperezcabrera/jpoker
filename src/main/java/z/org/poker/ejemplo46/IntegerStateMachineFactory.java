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
package z.org.poker.ejemplo46;

import org.util.statemachine.IState;
import org.util.statemachine.StateMachine;
import org.util.statemachine.StateMachineInstance;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public final class IntegerStateMachineFactory {

    private IntegerStateMachineFactory() {
    }

    public static void main(String[] args) {
        StateMachine<Integer> sm = new StateMachine<>();
        IntState state1 = new IntState("State 1");
        IntState state2 = new IntState("State 2");
        IntState state3 = new IntState("State 3");
        IntState state4 = new IntState("State 4");
        sm.setInitState(state1);
        sm.addTransition(state1, state2, n -> (n % 2) == 0);
        sm.addTransition(state1, state3, n -> (n % 3) == 0);
        sm.setDefaultTransition(state1, state4);
        StateMachineInstance<Integer> smi = sm.startInstance(6);
        smi.isFinish();
    }

    private static class IntState implements IState<Integer> {

        private String name;

        public IntState(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean execute(Integer context) {
            return true;
        }
    }
}
