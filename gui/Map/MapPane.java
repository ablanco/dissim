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
	private HexagonalGrid grid = null;
	private int radius = -1;
	private int hexWidth;
	private int hexHeight;
	private int maxUnitIncrement = 1;
	private Dimension size;


	public void updateGrid(HexagonalGrid grid, Dimension dim) {
			size = dim;
			setSize(size);
			// Calcular el radio de los hexágonos a representar
			int radiusX = (int) (((size.width / grid.getDimX()) / 2) * 1.1);
			int radiusY = (int) (((size.height / grid.getDimY()) / 2) * 1.3);
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
			// La escala de colores se calcula ahora (una única vez)
			System.err.println("Size: "+size.width+"x"+size.height+" tamaño hexagono: "+hexWidth+"x"+hexHeight);
			setVisible(true);
			}

	@Override
	public void paint(Graphics g) {
		if (grid != null) {
			Graphics2D g2 = (Graphics2D) g;
			g2.clearRect(0, 0, size.width, size.height);

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
					if (j % 2 == 0) { // Fila par
						posX = (hexWidth / 2)
								+ ((i - grid.getOffX()) * hexWidth);
					} else { // Fila impar
						posX = hexWidth + ((i - grid.getOffX()) * hexWidth);
					}
					int posY = radius + ((j - grid.getOffY()) * hexHeight);
					// Generar hexágono
					Polygon hex = new Hexagon2D(posX, posY, radius);
					// Dibujar y colorear según la altura
					int value = grid.getStreetValue(i, j);
					if (value != 0){
						g2.setColor(new Color(value*1000));
//						System.err.println("pintando en "+i+","+j+", ");
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
		return size;
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
