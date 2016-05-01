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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.ThreadSafe;
import org.poker.engine.model.ModelContext;
import org.poker.engine.model.PlayerEntity;
import org.slf4j.LoggerFactory;
import org.util.statemachine.IStateTrigger;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @since 1.0.0
 */
@ThreadSafe
public class EndGameTrigger implements IStateTrigger<ModelContext> {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(EndGameTrigger.class);

    @Override
    public boolean execute(ModelContext model) {
        Map<String, Double> scores = new HashMap<>();
        List<PlayerEntity> players = model.getAllPlayers();
        players.stream().filter(p -> p.getChips() > 0).forEach(p -> {
            p.setLastRoundChips(p.getChips());
            p.setRoudsSurvival(1 + p.getRoudsSurvival());
        });
        int totales = players.size();
        players.sort((p0, p1) -> Integer.compare(p0.getRoudsSurvival(), p1.getRoudsSurvival()));
        int lastIndex = 0;
        PlayerEntity pe = players.get(0);
        int lastRoundSurvival = pe.getRoudsSurvival();
        long chips = pe.getLastRoundChips();
        for (int i = 1; i < totales; i++) {
            pe = players.get(i);
            if (lastRoundSurvival < pe.getRoudsSurvival()) {
                calculateScores(scores, players, chips, lastIndex, i);
                chips = pe.getLastRoundChips();
                lastRoundSurvival = pe.getRoudsSurvival();
                lastIndex = i;
            } else {
                chips += pe.getLastRoundChips();
            }
        }
        calculateScores(scores, players, chips, lastIndex, totales);
        double scoreTotal = scores.values().stream().reduce(0., (accumulator, item) -> accumulator + item);
        LOGGER.debug("score: ", (scoreTotal / totales));
        for (PlayerEntity player : players) {
            LOGGER.debug("{}. score: {}, rounds: {}, last Chips: {}", player.getName() , scores.get(player.getName()), player.getRoudsSurvival(), player.getLastRoundChips());
        }
        model.setScores(scores);
        model.setCommunityCards(Collections.emptyList());
        return true;
    }

    private static void calculateScores(Map<String, Double> scores, List<PlayerEntity> players, double chips, int start, int end) {
        double parts = players.size() - 1.0;
        double min = start / parts;
        double max = (end - 1) / parts;
        if (start == (end - 1)) {
            scores.put(players.get(start).getName(), max);
        } else {
            double range = max - min;
            for (int i = start; i < end; i++) {
                PlayerEntity pe = players.get(i);
                scores.put(pe.getName(), min + range * (pe.getLastRoundChips() / chips));
            }
        }
    }
}
