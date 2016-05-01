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

import java.util.EnumMap;
import java.util.Map;
import net.jcip.annotations.ThreadSafe;
import org.poker.api.game.BetCommand;
import org.poker.api.game.TexasHoldEmUtil;
import org.poker.api.game.TexasHoldEmUtil.BetCommandType;
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
public class BetRoundTrigger implements IStateTrigger<ModelContext> {

    @FunctionalInterface
    private interface BetChecker {

        public boolean check(ModelContext model, PlayerEntity player, BetCommand bet);
    }

    private static final Map<BetCommandType, BetChecker> CHECKERS = buildBetCommandChecker();


    private static Map<BetCommandType, BetChecker> buildBetCommandChecker() {
        Map<BetCommandType, BetChecker> result = new EnumMap<>(BetCommandType.class);
        result.put(BetCommandType.FOLD, (m, p, b) -> true);
        result.put(BetCommandType.TIMEOUT, (m, p, b) -> false);
        result.put(BetCommandType.ERROR, (m, p, b) -> false);
        result.put(BetCommandType.RAISE, (m, p, b) -> b.getChips() > (m.getHighBet() - p.getBet()) && b.getChips() < p.getChips());
        result.put(BetCommandType.ALL_IN, (m, p, b) -> {
            b.setChips(p.getChips());
            return p.getChips() > 0;
        });

        result.put(BetCommandType.CALL, (c, p, b) -> {
            b.setChips(c.getHighBet() - p.getBet());
            return c.getHighBet() > c.getSettings().getBigBind();
        });

        result.put(BetCommandType.CHECK, (c, p, b) -> {
            b.setChips(c.getHighBet() - p.getBet());
            return b.getChips() == 0 || c.getHighBet() == c.getSettings().getBigBind();
        });
        return result;
    }

    @Override
    public boolean execute(ModelContext model) {
        boolean result = false;
        int playerTurn = model.getPlayerTurn();
        PlayerEntity player = model.getPlayer(playerTurn);
        BetCommand command = player.getBetCommand();
        if (command != null) {
            BetCommand resultCommand = command;
            player.setBetCommand(null);
            long betChips = 0;
            BetCommandType commandType = command.getType();
            if (CHECKERS.get(commandType).check(model, player, command)) {
                betChips = command.getChips();
                player.setState(TexasHoldEmUtil.convert(command.getType()));
            } else {
                commandType = BetCommandType.FOLD;
                player.setState(PlayerState.FOLD);
                if (command.getType() == BetCommandType.TIMEOUT) {
                    resultCommand = new BetCommand(BetCommandType.TIMEOUT);
                } else {
                    resultCommand = new BetCommand(BetCommandType.ERROR);
                }
                ModelUtil.incrementErrors(player, model.getSettings());
            }
            ModelUtil.playerBet(model, player, commandType, betChips);
            model.lastResultCommand(player, resultCommand);
            model.setPlayerTurn(ModelUtil.nextPlayer(model, playerTurn));
            result = true;
        }
        return result;
    }
}
