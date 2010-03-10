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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import util.Snapshot;
import util.Updateable;

public class VisorMap extends JFrame implements Updateable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6824786165438558551L;
	private MapPane mapPane = null;


	public VisorMap() {

//		setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
		setSize(new Dimension(800,600));
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		mapPane = new MapPane(this);
		c.add(new JScrollPane(mapPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), BorderLayout.CENTER);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setTitle("Visor de Mapas"); // TODO Internacionalización
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
		if (!(obj instanceof Snapshot))
			throw new IllegalArgumentException(
					"Object is not an instance of Snapshot");
		
		Snapshot snap = (Snapshot) obj;
		//TODO pasar solo short[][]
		mapPane.updateGrid(snap.getGrid());
	}

	@Override
	public String getConversationId() {
		return "visor";
	}
}
