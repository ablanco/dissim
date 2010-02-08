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
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.JFrame;

import util.Hexagon2D;

@SuppressWarnings("serial")
public class VisorFrame extends JFrame {

	public VisorFrame() {
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(150, 150);
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		// Preferencias para el renderizado, puede que en algunas plataformas se
		// ignoren
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

		// Estilo de pincel
		Stroke stroke = new BasicStroke(1, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND);
		g2.setStroke(stroke);

		// Dibujar formas
		Polygon p = new Hexagon2D(50, 50, 20);
		int x = p.xpoints[5] - 50;
		int y = 50 - p.ypoints[1];
		g2.drawPolygon(p);
		g2.fillPolygon(p);
		p = new Hexagon2D(50 + 2 * x, 50, 20);
		g2.drawPolygon(p);
		p = new Hexagon2D(50 + x, 50 + 40 + y, 20);
		g2.drawPolygon(p);

		// Colorear formas
		g2.setPaint(new Color(150, 30, 60));
		g2.fillPolygon(p);
	}

}
