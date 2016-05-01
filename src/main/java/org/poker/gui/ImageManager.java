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

import java.awt.Image;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @since 1.0.0
 */
@ThreadSafe
public enum ImageManager {
    
    INSTANCE;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageManager.class);
    
    public static final String IMAGES_PATH = "/images/";
    private final transient Map<String, Image> images = new HashMap<>();

    private ImageManager() {
    }
    
    public synchronized Image getImage(String imageFile) {
        Image image = images.get(imageFile);
        if (image == null) {
            try {
                image = ImageIO.read(getClass().getResource(imageFile));
                images.put(imageFile, image);
            } catch (IOException ex) {
                LOGGER.error("getImage \"" + imageFile + "\"", ex);
            }
        }
        return image;
    }
}
