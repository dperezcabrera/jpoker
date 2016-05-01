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
package org.poker.engine.states;

import net.jcip.annotations.ThreadSafe;
import org.poker.api.game.TexasHoldEmUtil.GameState;
import static org.poker.api.game.TexasHoldEmUtil.PlayerState.READY;
import org.poker.engine.model.ModelContext;
import org.poker.engine.model.ModelUtil;
import org.util.statemachine.IStateTrigger;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @since 1.0.0
 */
@ThreadSafe
public class CheckTrigger implements IStateTrigger<ModelContext> {
    
    private static final GameState[] GAME_STATE = GameState.values();
    private static final int[] OBATIN_CARDS = {3, 1, 1, 0, 0};


    private int indexByGameState(GameState gameState) {
        int i = 0;
        while (i < GAME_STATE.length && GAME_STATE[i] != gameState) {
            i++;
        }
        return i;
    }

    @Override
    public boolean execute(ModelContext model) {
        int indexGameState = indexByGameState(model.getGameState());
        if (OBATIN_CARDS[indexGameState] > 0){
            model.addCommunityCards(OBATIN_CARDS[indexGameState]);
        }
        model.setGameState(GAME_STATE[indexGameState+1]);
        model.setBets(0);
        model.getPlayers().stream().filter(p -> p.isActive()).forEach(p-> p.setState(READY));
        model.setPlayerTurn(ModelUtil.nextPlayer(model, model.getDealer()));
        model.setLastBetCommand(null);
        model.setLastPlayerBet(null);
        return true;
    }
}
