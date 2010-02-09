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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;

import javax.swing.JFrame;

import util.Hexagon2D;
import util.HexagonalGrid;
import util.flood.FloodHexagonalGrid;

@SuppressWarnings("serial")
public class VisorFrame extends JFrame {

	private HexagonalGrid grid = null;
	private int radius = -1;
	private int hexWidth;
	private int hexHeight;
	private int sizeWidth;
	private int sizeHeight;
	private short min;
	private short max;

	public VisorFrame() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		sizeWidth = (int) (dim.width * 0.9);
		sizeHeight = (int) (dim.height * 0.9);

		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
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

			int diff = max - min;
			int inc = 0;
			if (diff != 0)
				inc = 256 / diff;

			int x = hexWidth / 2;
			int y = radius * 2;
			for (int i = 0; i < grid.getDimX(); i++) {
				for (int j = 0; j < grid.getDimY(); j++) {
					int posX;
					if (i % 2 == 0)
						posX = x + (j * hexWidth); // Fila par
					else
						posX = hexWidth + (j * hexWidth); // Fila impar
					int posY = y + (i * hexHeight);

					// Dibujar hexágono
					Polygon hex = new Hexagon2D(posX, posY, radius);
					// g2.setColor(Color.BLACK);
					// g2.drawPolygon(hex);

					// Colorear según la altura
					int value = grid.getValue(i, j);
					value -= min;
					int color = value * inc;
					if (color < 0)
						color = 0;
					if (color > 255)
						color = 255;
					g2.setColor(new Color(color, 0, 0));
					if (grid instanceof FloodHexagonalGrid) { // Pintar agua
						FloodHexagonalGrid fgrid = (FloodHexagonalGrid) grid;
						int water = fgrid.getWaterValue(i, j);
						if (water > 0) {
							g2.setColor(new Color(0, 0, color));
						}
					}
					g2.fillPolygon(hex);
				}
			}
		}
	}

	public void updateGrid(HexagonalGrid grid) {
		this.grid = grid;

		if (radius == -1) { // Primera vez que recibe un grid
			// Calcular el radio de los hexágonos a representar
			int radiusX = (sizeWidth / grid.getDimX()) / 2;
			int radiusY = (sizeHeight / grid.getDimY()) / 2;
			if (radiusX < radiusY)
				radius = radiusX;
			else
				radius = radiusY;
			if (radius < 30)
				radius = 30;

			// Calcular el tamaño de la ventana
			Polygon p = new Hexagon2D(0, 0, radius);
			hexWidth = p.xpoints[4] - p.xpoints[2];
			hexHeight = p.ypoints[1] - p.ypoints[3];
			// Decoración de ventana
			int decoX = 8;
			int decoY = 38;
			sizeWidth = decoX + (hexWidth * grid.getDimX()) + (hexWidth / 2);
			sizeHeight = decoY + (radius * 2)
					+ (hexHeight * (grid.getDimY() - 1));
			this.setSize(sizeWidth, sizeHeight);
		}

		min = Short.MAX_VALUE;
		max = Short.MIN_VALUE;
		for (int i = 0; i < grid.getDimX(); i++) {
			for (int j = 0; j < grid.getDimY(); j++) {
				short value = grid.getValue(i, j);
				if (value < min)
					min = value;
				if (value > max)
					max = value;
			}
		}

		this.repaint();
	}

}
