/*
 * Copyright (C) 2017 by Herbert Roider <herbert@roider.at>
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
package org.roiderh.gcoderealigndialogs;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;

/**
 *
 * @author Herbert Roider <herbert@roider.at>
 */
public class ToolpathElement  {
    /**
     * the contourelement which forms the path element
     */
    public org.roiderh.gcodeviewer.contourelement element = null;
    public Path path = null;
    public Circle endpoint = null;
    
}
