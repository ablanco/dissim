//    Flood and evacuation simulator using multi-agent technology
//    Copyright (C) 2010 Alejandro Blanco and Manuel Gomar
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package gui.Map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import util.Hexagon2D;
import util.HexagonalGrid;

public class MapPane extends JPanel implements Scrollable, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8509235099644365808L;
	private JFrame parent = null;
	private HexagonalGrid grid = null;
	private int radius = -1;
	private int hexWidth;
	private int hexHeight;
	private Dimension dim;
	private int maxUnitIncrement = 1;

	public MapPane() {
		this(null);
	}

	public MapPane(JFrame parent) {
		super();
//		dim = new Dimension();
		this.parent = parent;
	}

	public void updateGrid(HexagonalGrid grid) {
		this.grid = grid;
		Dimension dimension = parent.getSize();
		int sizeHeight = dimension.height;
		int sizeWidth = dimension.width;
		if (radius == -1) { // Primera vez que recibe un grid
			// Calcular el radio de los hexágonos a representar
			int radiusX = (sizeWidth / grid.getDimX()) / 2;
			int radiusY = (sizeHeight / grid.getDimY()) / 2;
			if (radiusX < radiusY)
				radius = radiusX;
			else
				radius = radiusY;
			if (radius < 6)
				radius = 6;

			// Calcular las distancias de referencia de los hexágonos
			Polygon p = new Hexagon2D(0, 0, radius);
			hexWidth = p.xpoints[4] - p.xpoints[2];
			hexHeight = p.ypoints[1] - p.ypoints[3];
			// Calcular el tamaño del panel
			sizeWidth = (hexWidth * grid.getDimX()) + (hexWidth / 2);
			sizeHeight = (hexHeight * (grid.getDimY() - 2));
			dim = new Dimension(sizeWidth, sizeHeight);
			setSize(dim);
			System.err.println("wid: "+sizeWidth+", Hei: "+sizeHeight+sizeHeight+"HexSize ["+hexHeight+","+hexWidth+"]");
		}
	}

	@Override
	public void paint(Graphics g) {
		if (grid != null) {
			Graphics2D g2 = (Graphics2D) g;

			// Preferencias para el renderizado, puede que en algunas
			// plataformas se ignoren. Anteponemos velocidad a calidad.
			RenderingHints rh = new RenderingHints(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);
			rh.put(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_SPEED);
			g2.addRenderingHints(rh);

			// Estilo de pincel
			Stroke stroke = new BasicStroke(1, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND);
			g2.setStroke(stroke);

			for (int i = 0; i < grid.getDimX(); i++) {
				for (int j = 0; j < grid.getDimY(); j++) {
					int posX;
					if (i % 2 == 0) // Fila par
						posX = (hexWidth / 2) + (j * hexWidth);
					else
						posX = hexWidth + (j * hexWidth); // Fila impar
					int posY = radius + (i * hexHeight);

					// Generar hexágono
					Polygon hex = new Hexagon2D(posX, posY, radius);
					// Dibujar y colorear según la altura
					int value = grid.getValue(i, j);
					if (value != 0){
						g2.setColor(new Color(value*1000));	
					}else{
						g2.setColor(Color.WHITE);
					}					
					g2.fillPolygon(hex);
				}
			}
		}
	}

	// Methods required by the MouseMotionListener interface:
	public void mouseMoved(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		// The user is dragging us, so scroll!
		Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
		scrollRectToVisible(r);
	}

	public void setGrid(HexagonalGrid grid) {
		this.grid = grid;
	}

	public Dimension getPreferredSize() {
		return dim;
	}

	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		// Get the current position.
		int currentPosition = 0;
		if (orientation == SwingConstants.HORIZONTAL) {
			currentPosition = visibleRect.x;
		} else {
			currentPosition = visibleRect.y;
		}

		// Return the number of pixels between currentPosition
		// and the nearest tick mark in the indicated direction.
		if (direction < 0) {
			int newPosition = currentPosition
					- (currentPosition / maxUnitIncrement) * maxUnitIncrement;
			return (newPosition == 0) ? maxUnitIncrement : newPosition;
		} else {
			return ((currentPosition / maxUnitIncrement) + 1)
					* maxUnitIncrement - currentPosition;
		}
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		if (orientation == SwingConstants.HORIZONTAL) {
			return visibleRect.width - maxUnitIncrement;
		} else {
			return visibleRect.height - maxUnitIncrement;
		}
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public void setMaxUnitIncrement(int pixels) {
		maxUnitIncrement = pixels;
	}

}
