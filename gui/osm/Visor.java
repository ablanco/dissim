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

package gui.osm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

//import sun.beans.editors.ColorEditor;

public class Visor {
	public static int MAP_GUI = 1;

	public void createAndShowGUI(int key) {
		// Create and set up the window.
		switch (key) {
		case 1:
			createAndShowMapGui();
			break;

		default:
			break;
		}
	}

	private void createAndShowMapGui() {
		JFrame frame = new JFrame("Roads Map");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLabel emptyLabel = new JLabel("");
		emptyLabel.setPreferredSize(new Dimension(800, 600));
		Container contentPanel = frame.getContentPane();
		contentPanel.add(emptyLabel, BorderLayout.CENTER);

		// MapPane mapPane = new MapPane();

		JTable tableLeyend = new JTable(new Leyend(MAP_GUI));
		tableLeyend.setDefaultRenderer(Color.class, new ColorRenderer(true));
		// tableLeyend.setDefaultEditor(Color.class,
		// new ColorEditor());

		JScrollPane scrolltableLeyend = new JScrollPane(tableLeyend);
		tableLeyend.setFillsViewportHeight(true);
		contentPanel.add(scrolltableLeyend);

		// Display the window.
		frame.pack();
		frame.setVisible(true);

	}
}
