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
package org.poker.engine.model;

import org.poker.api.game.Settings;
import org.poker.api.game.TexasHoldEmUtil.BetCommandType;
import org.poker.api.game.TexasHoldEmUtil.PlayerState;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @since 1.0.0
 */
public final class ModelUtil {

    public static final int NO_PLAYER_TURN = -1;

    private ModelUtil() {
    }

    public static boolean range(int min, int max, int value) {
        return min <= value && value < max;
    }

    public static int nextPlayer(ModelContext model, int turn) {
        int result = NO_PLAYER_TURN;
        int players = model.getNumPlayers();
        if (players > 1 && range(0, players, turn)) {
            int i = (turn + 1) % players;
            while (i != turn && result == NO_PLAYER_TURN) {
                if (model.getPlayer(i).isActive()) {
                    result = i;
                } else {
                    i = (i + 1) % players;
                }
            }
            result = checkNextPlayer(model, result);
        }
        return result;
    }

    private static int checkNextPlayer(ModelContext model, int index) {
        int result = index;
        if (result != NO_PLAYER_TURN
                && model.getPlayer(result).getBet() == model.getHighBet()
                && (model.getPlayer(result).getState() != PlayerState.READY 
                || model.getActivePlayers() == 1)) {
            result = NO_PLAYER_TURN;
        }
        return result;
    }

    public static void playerBet(ModelContext model, PlayerEntity player, BetCommandType betCommand, long chips) {
        if (betCommand == BetCommandType.ALL_IN) {
            model.setPlayersAllIn(model.getPlayersAllIn() + 1);
            model.setActivePlayers(model.getActivePlayers() - 1);
        } else if (betCommand == BetCommandType.FOLD || betCommand == BetCommandType.TIMEOUT) {
            model.setActivePlayers(model.getActivePlayers() - 1);
        }
        playerBet(player, chips);
        model.setHighBet(Math.max(model.getHighBet(), player.getBet()));
        model.setBets(model.getBets() + 1);
    }

    public static void playerBet(PlayerEntity player, long chips) {
        player.setBet(player.getBet() + chips);
        player.setChips(player.getChips() - chips);
    }

    public static void incrementErrors(PlayerEntity player, Settings settings) {
        int errors = player.getErrors() + 1;
        player.setErrors(errors);
        if (errors >= settings.getMaxErrors()) {
            player.setState(PlayerState.OUT);
            player.setChips(0);
        } else {
            player.setState(PlayerState.FOLD);
        }
    }
}
