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

import jade.core.AID;
import jade.core.Agent;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;

import util.Snapshot;
import util.Updateable;

@SuppressWarnings("serial")
public class VisorFrame extends JFrame implements Updateable {

	private HexagonalGridPane pane = null;
	private JLabel gridLbl = new JLabel();

	public VisorFrame() {
		setSize(new Dimension(800, 600));
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		pane = new HexagonalGridPane();
		c.add(pane, BorderLayout.CENTER);
		c.add(gridLbl, BorderLayout.NORTH);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setTitle("Flood Visor");
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
	public void update(Object obj, AID sender) throws IllegalArgumentException {
		if (!(obj instanceof Snapshot))
			throw new IllegalArgumentException(
					"Object is not an instance of Snapshot");

		Snapshot snap = (Snapshot) obj;
		gridLbl.setText(snap.getDateTime());

		pane.updateGrid(snap, sender, getSize());
	}

	@Override
	public String getType() {
		return "visor";
	}

	@Override
	public void setAgent(Agent agt) {
		// Empty
	}

}
