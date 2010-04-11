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

package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import osm.OsmInf;
import util.Hexagon2D;
import util.HexagonalGrid;
import util.Pedestrian;
import util.Point;
import util.Snapshot;
import util.flood.FloodHexagonalGrid;

@SuppressWarnings("serial")
public class HexagonalGridPane extends JPanel implements Scrollable {

	private HexagonalGrid grid = null;
	private List<Pedestrian> people;
	private int radius = -1;
	private int hexWidth;
	private int hexHeight;
	private short min;
	private short max;
	private int maxUnitIncrement = 1;
	private Dimension size = new Dimension(300, 300);
	private boolean firstTime = true;

	public void updateGrid(Snapshot snap, Dimension dim) {
		grid = snap.getGrid();
		people = snap.getPeople();

		if (dim.height != size.height || dim.width != size.width || firstTime) {
			size = dim;
			// Calcular el radio de los hexágonos a representar
			int radiusX = (int) (((size.width / grid.getColumns()) / 2) * 1.1);
			int radiusY = (int) (((size.height / grid.getRows()) / 2) * 1.3);
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

			int width = ((hexWidth * grid.getColumns()) + (hexWidth / 2));
			int height = (hexHeight * grid.getRows());

			size = new Dimension(width, height);
			setSize(size);
		}

		if (firstTime) { // Primera vez que recibe un grid
			// La escala de colores se calcula ahora (una única vez)
			min = Short.MAX_VALUE;
			max = Short.MIN_VALUE;
			int endX = grid.getOffX() + grid.getColumns();
			int endY = grid.getOffY() + grid.getRows();
			for (int i = grid.getOffX(); i < endX; i++) {
				for (int j = grid.getOffY(); j < endY; j++) {
					short value = grid.getValue(i, j);
					if (value < min)
						min = value;
					if (value > max)
						max = value;
				}
			}
			max += (max - min) * 0.15; // 15% más
			setVisible(true);
			firstTime = false;
		}

		repaint();
	}

	@Override
	public void paint(Graphics g) {
		if (grid != null) {
			if (people == null)
				people = new ArrayList<Pedestrian>(1);

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
			Stroke stroke = new BasicStroke(2, BasicStroke.CAP_SQUARE,
					BasicStroke.JOIN_MITER);
			g2.setStroke(stroke);

			int diff = max - min;
			double inc = 1;
			if (diff > 0)
				inc = 256.0 / ((double) diff);

			int endX = grid.getOffX() + grid.getColumns();
			int endY = grid.getOffY() + grid.getRows();
			for (int i = grid.getOffX(); i < endX; i++) {
				for (int j = grid.getOffY(); j < endY; j++) {
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
					int value = grid.getValue(i, j);
					value -= min;
					int color = (int) (value * inc);
					if (color < 0)
						color = 0;
					if (color > 255)
						color = 255;
					g2.setColor(new Color(0, color, 0));
					if (grid instanceof FloodHexagonalGrid) { // Pintar agua
						FloodHexagonalGrid fgrid = (FloodHexagonalGrid) grid;
						int water = fgrid.getWaterValue(i, j);
						if (water > 0) {
							g2.setColor(new Color(0, 0, color));
						}
					}
					g2.fillPolygon(hex);

					// Pintar el borde amarillo si hay algún tipo de calle
					if (OsmInf.getBigType(grid.getStreetValue(i, j)) == OsmInf.Roads) {
						g2.setColor(Color.YELLOW);
						g2.drawPolygon(hex);
					} else if (OsmInf.getBigType(grid.getStreetValue(i, j)) == OsmInf.SafePoint) {
						g2.setColor(Color.RED);
						g2.drawPolygon(hex);
					}

					// Pintar personas
					Point pos = new Point(i, j);
					boolean person = false;
					int status = Pedestrian.HEALTHY;
					for (Pedestrian p : people) {
						// Miramos si hay alguien en esta casilla
						if (pos.equals(p.getPoint())) {
							person = true;
							status = p.getStatus();
							break;
						}
					}
					if (person) {
						// Si la había la pintamos
						if (status == Pedestrian.HEALTHY)
							g2.setColor(Color.RED);
						else if (status == Pedestrian.DEAD)
							g2.setColor(Color.DARK_GRAY);
						else if (status == Pedestrian.SAFE)
							g2.setColor(Color.PINK);
						Ellipse2D.Float circle = new Ellipse2D.Float(posX
								- (radius / 2), posY - (radius / 2), radius,
								radius);
						g2.fill(circle);
					}
				}
			}
		}
	}

	public Dimension getPreferredScrollableViewportSize() {
		return size;
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
