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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import util.HexagonalGrid;
import util.Updateable;

@SuppressWarnings("serial")
public class VisorFrame extends JFrame implements Updateable {

	private HexagonalGridPane pane = null;

	public VisorFrame() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int sizeWidth = (int) (dim.width * 0.9);
		int sizeHeight = (int) (dim.height * 0.9);

		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		pane = new HexagonalGridPane(sizeWidth, sizeHeight, this);
		c.add(new JScrollPane(pane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), BorderLayout.CENTER);

		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setTitle("Visor de inundación"); // TODO Internacionalización
	}

	@Override
	public void init() {
		this.setVisible(true);
	}

	@Override
	public void finish() {
		this.dispose();
	}

	@Override
	public void update(Object obj) {
		if (!(obj instanceof HexagonalGrid))
			throw new IllegalArgumentException(
					"Object is not an instance of HexagonalGrid");

		pane.updateGrid((HexagonalGrid) obj);
	}

	@Override
	public String getConversationId() {
		return "visor";
	}

}
