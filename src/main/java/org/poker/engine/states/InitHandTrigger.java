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

import java.util.List;
import net.jcip.annotations.ThreadSafe;
import org.poker.api.core.Deck;
import org.poker.api.game.Settings;
import org.poker.api.game.TexasHoldEmUtil.BetCommandType;
import org.poker.api.game.TexasHoldEmUtil.GameState;
import static org.poker.api.game.TexasHoldEmUtil.MIN_PLAYERS;
import org.poker.api.game.TexasHoldEmUtil.PlayerState;
import org.poker.engine.model.ModelContext;
import org.poker.engine.model.ModelUtil;
import org.poker.engine.model.PlayerEntity;
import org.util.statemachine.IStateTrigger;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @since 1.0.0
 */
@ThreadSafe
public class InitHandTrigger implements IStateTrigger<ModelContext> {

    @Override
    public boolean execute(ModelContext model) {
        model.setScores(null);
        Deck deck = model.getDeck();
        deck.shuffle();
        Settings settings = model.getSettings();
        model.setGameState(GameState.PRE_FLOP);
        model.clearCommunityCard();
        model.setRound(model.getRound() + 1);
        if (model.getRound() % settings.getRounds4IncrementBlind() == 0) {
            settings.setSmallBlind(2 * settings.getSmallBlind());
        }
        model.setPlayersAllIn(0);
        model.setHighBet(0L);
        List<PlayerEntity> players = model.getPlayers();
        for (PlayerEntity p : players) {
            p.setRoudsSurvival(model.getRound());
            p.setLastRoundChips(p.getChips());
            p.setState(PlayerState.READY);
            p.setHandValue(0);
            p.setBet(0);
            p.showCards(false);
            p.setCards(deck.obtainCard(), deck.obtainCard());
        }
        int numPlayers = model.getNumPlayers();
        model.setActivePlayers(numPlayers);

        int dealerIndex = (model.getDealer() + 1) % numPlayers;
        model.setDealer(dealerIndex);
        model.setPlayerTurn((dealerIndex + 1) % numPlayers);
        if (numPlayers > MIN_PLAYERS) {
            compulsoryBet(model, settings.getSmallBlind());
        }
        compulsoryBet(model, settings.getBigBlind());
        return true;
    }

    private void compulsoryBet(ModelContext model, long chips) {
        int turn = model.getPlayerTurn();
        PlayerEntity player = model.getPlayer(turn);
        if (player.getChips() <= chips) {
            player.setState(PlayerState.ALL_IN);
            ModelUtil.playerBet(model, player, BetCommandType.ALL_IN, player.getChips());
        } else {
            ModelUtil.playerBet(player, chips);
        }
        model.setHighBet(chips);
        model.setPlayerTurn((turn + 1) % model.getNumPlayers());
    }
}
