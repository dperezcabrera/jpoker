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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.jcip.annotations.ThreadSafe;
import org.poker.api.core.Card;
import org.poker.api.game.Hand7Evaluator;
import org.poker.api.core.HandEvaluator;
import org.poker.api.game.TexasHoldEmUtil.PlayerState;
import org.poker.engine.model.ModelContext;
import org.poker.engine.model.PlayerEntity;
import org.util.statemachine.IStateTrigger;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @since 1.0.0
 */
@ThreadSafe
public class ShowDownTrigger implements IStateTrigger<ModelContext> {

    private List<PlayerEntity> calculateHandValue(List<Card> communityCards, List<PlayerEntity> players) {
        Hand7Evaluator evaluator = new Hand7Evaluator(new HandEvaluator());
        evaluator.setCommunityCards(communityCards);
        players.stream().forEach(p -> p.setHandValue(evaluator.eval(p.getCard(0), p.getCard(1))));
        return players;
    }

    @Override
    public boolean execute(ModelContext model) {
        List<PlayerEntity> players = calculateHandValue(model.getCommunityCards(), model.getPlayers());
        Map<Long, List<PlayerEntity>> indexByBet = players.stream()
                .filter(p -> p.getBet() > 0)
                .collect(Collectors.groupingBy(p -> p.getBet()));
        List<Long> inverseSortBets = new ArrayList<>(indexByBet.keySet());
        Collections.sort(inverseSortBets, (l0, l1) -> Long.compare(l1, l0));

        Iterator<Long> it = inverseSortBets.iterator();
        List<PlayerEntity> lastPlayers = indexByBet.get(it.next());
        while (it.hasNext()) {
            List<PlayerEntity> currentPlayers = indexByBet.get(it.next());
            currentPlayers.addAll(lastPlayers);
            lastPlayers = currentPlayers;
        }

        Set<Long> bet4Analysis = players.stream()
                .filter(p -> p.getState() == PlayerState.ALL_IN)
                .map(p -> p.getBet()).collect(Collectors.toSet());
        bet4Analysis.add(inverseSortBets.get(0));
        long accumulateChips = 0L;
        long lastBet = 0L;
        while (!inverseSortBets.isEmpty()) {
            Long bet = inverseSortBets.remove(inverseSortBets.size() - 1);
            List<PlayerEntity> currentPlayers = indexByBet.get(bet);
            accumulateChips += (bet - lastBet) * currentPlayers.size();
            if (bet4Analysis.contains(bet)) {
                Collections.sort(currentPlayers, (p0, p1) -> p1.getHandValue() - p0.getHandValue());
                List<PlayerEntity> winners = new ArrayList<>(currentPlayers.size());
                currentPlayers.stream()
                        .filter(p -> p.getState() != PlayerState.OUT)
                        .peek(p -> p.showCards(true))
                        .filter(p -> winners.isEmpty() || p.getHandValue() == winners.get(0).getHandValue())
                        .forEach(winners::add);
                long chips4Player = accumulateChips / winners.size();
                winners.stream().forEach(p -> p.addChips(chips4Player));
                int remain = (int) accumulateChips % winners.size();
                if (remain > 0) {
                    Collections.shuffle(winners);
                    winners.stream().limit(remain).forEach(p -> p.addChips(1));
                }
                accumulateChips = 0L;
            }
            lastBet = bet;
        }
        return true;
    }
}
