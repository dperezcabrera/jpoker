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
package org.poker.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.poker.api.core.Card;
import org.poker.api.game.BetCommand;
import org.poker.api.game.GameInfo;
import org.poker.api.game.IStrategy;
import org.poker.api.game.PlayerInfo;
import org.poker.api.game.TexasHoldEmUtil;
import org.poker.api.game.TexasHoldEmUtil.BetCommandType;
import org.poker.api.game.TexasHoldEmUtil.PlayerState;
import static org.poker.gui.ImageManager.IMAGES_PATH;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @since 1.0.0
 */
public class TexasHoldEmTablePanel extends javax.swing.JPanel implements IStrategy {

    private static final int PLAYER_PADDING = 6;
    private static final Font DEFAULT_FONT = new Font(Font.SERIF, Font.BOLD, 12);
    private static final Font CHIPS_FONT = new Font(Font.SERIF, Font.PLAIN, 12);
    private static final Font PLAYER_STATE_FONT = new Font(Font.SERIF, Font.BOLD, 15);
    private static final Color DEFAULT_BORDER_PLAYER_COLOR = new Color(0xb06925);
    private static final Color TEXT_ROUND_COLOR = new Color(0x004000);
    private static final Color ACTIVE_PLAYER_FOREGROUND_COLOR = new Color(0x033E6b);
    private static final Color ACTIVE_PLAYER_BACKGROUND_COLOR = new Color(0x66a3d2);
    private static final Color PLAYER_TURN_FOREGROUND_COLOR = new Color(0x186b18);
    private static final Color PAYER_TURN_BACKGROUND_COLOR = new Color(0x50d050);
    private static final Color DEFAULT_PLAYER_BACKGROUND_COLOR = new Color(0xCD853F);

    private static final int DEFAULT_ROUND_CORNER_SIZE = 20;
    private static final String DOLLAR = " $";
    private static final int MAX_PLAYERS = 10;
    private static final int POTS_POSITION_INCREMENT = 12;
    private static final String CARDS_PATH = IMAGES_PATH + "cards/png/";

    private static final String CARDS_EXTENSIONS = ".png";
    private static final String CHIPS_PATH = IMAGES_PATH + "chips.png";
    private static final String DEALER_PATH = IMAGES_PATH + "dealer.png";
    private static final String BACKGROUND_PATH = IMAGES_PATH + "background.png";
    private static final String BACK_CARD = "back";
    private static final char[][] SUIT_SYMBOLS = {{'♦', 'D'}, {'♠', 'S'}, {'♥', 'H'}, {'♣', 'C'}};
    private static final Point[] COMMUNITY_CARDS_POSITIONS = {new Point(478, 348), new Point(538, 348), new Point(598, 348), new Point(658, 348), new Point(718, 348)};
    private static final Point[] PLAYER_POSITIONS
            = {new Point(1045, 333), new Point(999, 522), new Point(735, 606), new Point(445, 606), new Point(181, 522), new Point(135, 333), new Point(181, 144), new Point(445, 60), new Point(735, 60), new Point(999, 144)};
    private static final Point[] PLAYER_BET_POSITIONS = {new Point(945, 388), new Point(900, 506), new Point(739, 574), new Point(449, 574), new Point(285, 506), new Point(241, 388), new Point(285, 270), new Point(449, 202), new Point(739, 202), new Point(900, 270)};

    private static final Point CHIPS_POSITION = new Point(570, 428);
    private static final Point CHIPS_TEXT_POSITION_INCREMENT = new Point(86, 4);
    private static final Dimension CARDS_DIMENSION = new Dimension(54, 75);
    private static final Dimension PLAYER_DIMENSION = new Dimension(100, 135);

    private final TextPrinter textPrinter = new TextPrinter();

    private final List<Card> communityCards = new ArrayList<>(TexasHoldEmUtil.COMMUNITY_CARDS);
    private List<Long> pots = new ArrayList<>();
    private final PlayerInfo[] players = new PlayerInfo[MAX_PLAYERS];
    private final BetCommandType[] bets = new BetCommandType[MAX_PLAYERS];
    private final Map<String, Integer> playersByName = new HashMap<>();
    private IStrategy delegate;
    private long betRound = 0;
    private long maxBet = 0;
    private int playerTurn = -1;
    private int dealer = 0;
    private int round = 0;

    public void setStrategy(IStrategy delegate) {
        this.delegate = delegate;
    }

    private static String getCardPath(Card c) {
        String cardString = BACK_CARD;
        if (c != null) {
            cardString = c.toString();
            for (char[] suitSymbol : SUIT_SYMBOLS) {
                cardString = cardString.replace(suitSymbol[0], suitSymbol[1]);
            }
        }
        return CARDS_PATH.concat(cardString).concat(CARDS_EXTENSIONS);
    }

    private void paintBackground(Graphics2D g2) {
        g2.drawImage(ImageManager.INSTANCE.getImage(BACKGROUND_PATH), 0, 0, null);
    }

    private void paintCommunityCards(Graphics2D g2) {
        int i = 0;
        for (Card c : communityCards) {
            Point p = COMMUNITY_CARDS_POSITIONS[i++];
            String cardPath = getCardPath(c);
            g2.drawImage(ImageManager.INSTANCE.getImage(cardPath), p.x, p.y, null);
        }
        int roundX = COMMUNITY_CARDS_POSITIONS[(COMMUNITY_CARDS_POSITIONS.length) / 2].x + CARDS_DIMENSION.width / 2;
        int roundY = COMMUNITY_CARDS_POSITIONS[0].y - 2 * PLAYER_PADDING;
        g2.setColor(TEXT_ROUND_COLOR);
        textPrinter.setFont(PLAYER_STATE_FONT);
        textPrinter.setVerticalAlign(TextPrinter.VerticalAlign.BOTTOM);
        textPrinter.setHorizontalAlign(TextPrinter.HorizontalAlign.CENTER);
        textPrinter.print(g2, "Ronda " + round, roundX, roundY);
    }

    private void paintChips(Graphics2D g2) {
        Image chips = ImageManager.INSTANCE.getImage(CHIPS_PATH);
        g2.setColor(Color.BLACK);
        textPrinter.setFont(DEFAULT_FONT);
        textPrinter.setVerticalAlign(TextPrinter.VerticalAlign.TOP);
        textPrinter.setHorizontalAlign(TextPrinter.HorizontalAlign.RIGHT);
        int x = CHIPS_POSITION.x - (pots.size() * POTS_POSITION_INCREMENT) / 2;
        for (Long pot : pots) {
            g2.drawImage(chips, x, CHIPS_POSITION.y, null);
            textPrinter.print(g2, pot + DOLLAR, x + CHIPS_TEXT_POSITION_INCREMENT.x, CHIPS_POSITION.y + CHIPS_TEXT_POSITION_INCREMENT.y);
            x += POTS_POSITION_INCREMENT;
        }
        for (int i = 0; i < players.length; i++) {
            if (players[i] != null && players[i].getBet() > betRound) {
                Point p = PLAYER_BET_POSITIONS[i];
                g2.drawImage(chips, p.x, p.y, null);
                textPrinter.print(g2, (players[i].getBet() - betRound) + DOLLAR, p.x + CHIPS_TEXT_POSITION_INCREMENT.x, p.y + CHIPS_TEXT_POSITION_INCREMENT.y);
            }
        }
        g2.drawImage(ImageManager.INSTANCE.getImage(DEALER_PATH), PLAYER_BET_POSITIONS[dealer].x, PLAYER_BET_POSITIONS[dealer].y, null);
    }

    private void setCommunityCards(List<Card> cards) {
        communityCards.clear();
        communityCards.addAll(cards);
    }

    private void paintPlayers(Graphics2D g2) {
        g2.setStroke(new BasicStroke(2));
        for (int i = 0; i < players.length; i++) {
            paintPlayer(g2, i);
        }
    }

    private void paintPlayer(Graphics2D g2, int i) {
        Point playerPosition = PLAYER_POSITIONS[i];
        Color borderPlayerColor = DEFAULT_BORDER_PLAYER_COLOR;
        if (players[i] != null) {
            Color backgroundPlayerColor = DEFAULT_PLAYER_BACKGROUND_COLOR;
            if (bets[i] != null || players[i].getChips() > 0) {
                if (i == playerTurn) {
                    backgroundPlayerColor = PAYER_TURN_BACKGROUND_COLOR;
                    borderPlayerColor = PLAYER_TURN_FOREGROUND_COLOR;
                } else {
                    backgroundPlayerColor = ACTIVE_PLAYER_BACKGROUND_COLOR;
                    borderPlayerColor = ACTIVE_PLAYER_FOREGROUND_COLOR;
                }
                g2.setColor(backgroundPlayerColor);
                g2.fillRoundRect(playerPosition.x, playerPosition.y, PLAYER_DIMENSION.width, PLAYER_DIMENSION.height, DEFAULT_ROUND_CORNER_SIZE, DEFAULT_ROUND_CORNER_SIZE);
                if (players[i].isActive() || players[i].getState() == TexasHoldEmUtil.PlayerState.ALL_IN) {
                    int y = playerPosition.y + PLAYER_DIMENSION.height - (CARDS_DIMENSION.height + PLAYER_PADDING);
                    g2.drawImage(ImageManager.INSTANCE.getImage(getCardPath(players[i].getCard(0))), playerPosition.x + PLAYER_PADDING, y, null);
                    g2.drawImage(ImageManager.INSTANCE.getImage(getCardPath(players[i].getCard(1))), playerPosition.x + PLAYER_DIMENSION.width - (CARDS_DIMENSION.width + PLAYER_PADDING), y, null);
                }
                if (bets[i] != null) {
                    String text = bets[i].name().replace("_", " ");
                    g2.setColor(borderPlayerColor);
                    textPrinter.setFont(PLAYER_STATE_FONT);
                    textPrinter.setVerticalAlign(TextPrinter.VerticalAlign.MIDDLE);
                    textPrinter.setHorizontalAlign(TextPrinter.HorizontalAlign.CENTER);
                    textPrinter.print(g2, text, playerPosition.x + PLAYER_DIMENSION.width / 2, playerPosition.y + PLAYER_DIMENSION.height - (PLAYER_PADDING + CARDS_DIMENSION.height / 2));
                }
            } else {
                g2.setColor(backgroundPlayerColor);
                g2.fillRoundRect(playerPosition.x, playerPosition.y, PLAYER_DIMENSION.width, PLAYER_DIMENSION.height, DEFAULT_ROUND_CORNER_SIZE, DEFAULT_ROUND_CORNER_SIZE);
            }
            g2.setColor(Color.white);
            textPrinter.setFont(DEFAULT_FONT);
            textPrinter.setVerticalAlign(TextPrinter.VerticalAlign.TOP);
            textPrinter.setHorizontalAlign(TextPrinter.HorizontalAlign.CENTER);
            textPrinter.print(g2, players[i].getName(), playerPosition.x + PLAYER_DIMENSION.width / 2, playerPosition.y + PLAYER_PADDING);
            if (players[i].getChips() > 0) {
                textPrinter.setFont(CHIPS_FONT);
                g2.setColor(borderPlayerColor);
                textPrinter.setHorizontalAlign(TextPrinter.HorizontalAlign.RIGHT);
                textPrinter.setVerticalAlign(TextPrinter.VerticalAlign.BOTTOM);
                textPrinter.print(g2, players[i].getChips() + " $", playerPosition.x + PLAYER_DIMENSION.width - PLAYER_PADDING, playerPosition.y + PLAYER_DIMENSION.height - (2 * PLAYER_PADDING + CARDS_DIMENSION.height));
            }
        }
        g2.setColor(borderPlayerColor);
        g2.drawRoundRect(playerPosition.x, playerPosition.y, PLAYER_DIMENSION.width, PLAYER_DIMENSION.height, DEFAULT_ROUND_CORNER_SIZE, DEFAULT_ROUND_CORNER_SIZE);
    }

    @Override
    public synchronized void paintComponent(Graphics g) {
        Graphics2D graphics2d = (Graphics2D) g;
        graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        paintBackground(graphics2d);
        if (!playersByName.isEmpty()) {
            paintCommunityCards(graphics2d);
            paintChips(graphics2d);
        }
        paintPlayers(graphics2d);
    }

    @Override
    public String getName() {
        String result = "";
        if (delegate != null) {
            result = delegate.getName();
        }
        return result;
    }

    @Override
    public BetCommand getCommand(GameInfo<PlayerInfo> state) {
        playerTurn = positionConverter(state, state.getPlayerTurn());
        updatePlayerInfo(state);
        repaint();
        return delegate.getCommand(state);
    }

    private void updatePlayerInfo(GameInfo<PlayerInfo> state) {
        maxBet = 0;
        for (PlayerInfo player : state.getPlayers()) {
            int pos = playersByName.get(player.getName());
            if (players[pos] != null) {
                players[pos].setBet(player.getBet());
                players[pos].setChips(player.getChips());
                players[pos].setCards(player.getCards());
                players[pos].setState(player.getState());
                players[pos].setErrors(player.getErrors());
                maxBet = Math.max(maxBet, players[pos].getBet());
            }
        }
    }

    @Override
    public synchronized void check(List<Card> communityCards) {
        betRound = maxBet;
        setCommunityCards(communityCards);
        for (int i = 0; i < bets.length; i++) {
            if (bets[i] != null && bets[i] != TexasHoldEmUtil.BetCommandType.ALL_IN) {
                bets[i] = null;
            }
        }
        pots = calculatePots(players);
        repaint();
        delegate.check(communityCards);
    }

    @Override
    public synchronized void onPlayerCommand(String playerName, BetCommand betCommand) {
        int pos = playersByName.get(playerName);
        PlayerInfo player = players[pos];
        player.setBet(player.getBet() + betCommand.getChips());
        player.setChips(player.getChips() - betCommand.getChips());
        player.setState(TexasHoldEmUtil.convert(betCommand.getType()));
        maxBet = Math.max(maxBet, player.getBet());
        bets[pos] = betCommand.getType();
        delegate.onPlayerCommand(playerName, betCommand);
        playerTurn = nextPlayerTurn(players, bets, maxBet, pos);
        repaint();
    }

    private static boolean isActivePlayer(PlayerInfo p){
        return p != null && (p.isActive() || p.getState() == PlayerState.ALL_IN);
    }
    
    private static int nextPlayerTurn(PlayerInfo[] players, BetCommandType[] bets, long maxBet, int currentPlayerTurn) {
        int i = (currentPlayerTurn + 1) % players.length;
        while (i != currentPlayerTurn) {
            if (players[i] != null && players[i].isActive() && (bets[i] == null || players[i].getBet() < maxBet)) {
                if (1 == Arrays.stream(players).filter(TexasHoldEmTablePanel::isActivePlayer).count()) {
                    return -1;
                } else {
                    return i;
                }
            }
            i = (i + 1) % players.length;
        }
        return -1;
    }

    @Override
    public synchronized void initHand(GameInfo<PlayerInfo> state) {
        updateStatePreFlop(state);
        updateState(state);
        round = state.getRound();
        repaint();
        delegate.initHand(state);
    }

    @Override
    public synchronized void endHand(GameInfo<PlayerInfo> state) {
        updateState(state);
        repaint();
        delegate.endHand(state);
    }

    @Override
    public synchronized void endGame(Map<String, Double> scores) {
        pots.clear();
        Arrays.fill(bets, null);
        repaint();
        delegate.endGame(scores);
    }

    private void updateState(GameInfo<PlayerInfo> state) {
        playerTurn = positionConverter(state, state.getPlayerTurn());
        updatePlayerInfo(state);
        if (round != state.getRound()) {
            setCommunityCards(state.getCommunityCards());
            pots.clear();
            Arrays.fill(bets, null);
        }
    }

    private void updateStatePreFlop(GameInfo<PlayerInfo> state) {
        betRound = 0;
        int i = 0;
        if (playersByName.isEmpty()) {
            for (PlayerInfo player : state.getPlayers()) {
                players[i] = new PlayerInfo();
                players[i].setName(player.getName());
                playersByName.put(player.getName(), i++);
            }
        } else {
            Set<String> currentPlayers = state.getPlayers().stream().map(p -> p.getName()).collect(Collectors.toSet());
            for (i = 0; i < players.length; i++) {
                if (players[i] != null && !currentPlayers.contains(players[i].getName())) {
                    players[i].setBet(0);
                    players[i].setChips(0);
                    players[i].setState(TexasHoldEmUtil.PlayerState.OUT);
                }
            }
        }
        pots = new ArrayList<>();
        Arrays.fill(bets, null);
        dealer = positionConverter(state, state.getDealer());
    }

    private int positionConverter(GameInfo<PlayerInfo> state, int i) {
        int result = -1;
        if (i >= 0 && i < state.getNumPlayers()) {
            result = playersByName.get(state.getPlayer(i).getName());
        }
        return result;
    }

    private static List<Long> calculatePots(PlayerInfo[] players) {
        List<PlayerInfo> playersList = Arrays.stream(players).filter(p -> p != null).collect(Collectors.toList());
        List<Long> sortedAllIn = playersList.stream().filter(p -> p.getState() == PlayerState.ALL_IN).map(p -> p.getBet()).sorted().distinct().collect(Collectors.toList());
        sortedAllIn.add(Long.MAX_VALUE);
        long[] quantities = new long[sortedAllIn.size()];
        playersList.stream().forEach(p -> {
            long bet = p.getBet();
            long quantity = bet;
            long last = 0;
            for (int i = 0; i < sortedAllIn.size() && quantity > 0; i++) {
                long diff = Math.min(sortedAllIn.get(i) - last, quantity);
                quantities[i] += diff;
                quantity -= diff;
                last = sortedAllIn.get(i);
            }
        });
        List<Long> pots = new ArrayList<>();
        IntStream.range(0, quantities.length).filter(i -> quantities[i] > 0).forEach(i -> pots.add(quantities[i]));
        return pots;
    }

}
