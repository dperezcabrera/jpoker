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

import org.poker.api.game.IStrategy;
import static org.poker.gui.ImageManager.IMAGES_PATH;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @since 1.0.0
 */
public class TexasHoldEmView extends javax.swing.JFrame {

    private static final int WINDOW_HEIGHT = 800;
    private static final int WINDOW_WITH = 1280;
    private static final String WINDOW_TITLE = "J Poker";
    private static final String WINDOW_ICON = IMAGES_PATH + "poker-chip.png";

    private TexasHoldEmTablePanel jTablePanel;

    public TexasHoldEmView(IStrategy delegate) {
        initComponents();
        setTitle(WINDOW_TITLE);
        setIconImage(ImageManager.INSTANCE.getImage(WINDOW_ICON));
        setBounds(0, 0, WINDOW_WITH, WINDOW_HEIGHT);
        jTablePanel.setStrategy(delegate);
    }

    public IStrategy getStrategy() {
        return jTablePanel;
    }

    private void initComponents() {

        jTablePanel = new TexasHoldEmTablePanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTablePanel.setPreferredSize(new java.awt.Dimension(WINDOW_WITH, WINDOW_HEIGHT));

        javax.swing.GroupLayout jTablePanelLayout = new javax.swing.GroupLayout(jTablePanel);
        jTablePanel.setLayout(jTablePanelLayout);
        jTablePanelLayout.setHorizontalGroup(
                jTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, WINDOW_WITH, Short.MAX_VALUE)
        );
        jTablePanelLayout.setVerticalGroup(
                jTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, WINDOW_HEIGHT, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jTablePanel, javax.swing.GroupLayout.DEFAULT_SIZE, WINDOW_WITH, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jTablePanel, javax.swing.GroupLayout.DEFAULT_SIZE, WINDOW_HEIGHT, Short.MAX_VALUE)
        );
        pack();
    }
}
