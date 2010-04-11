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

package test;

import java.util.Random;

import util.NoDuplicatePointsSet;
import util.OldNoDuplicatePointsSet;
import util.Point;

public class NoDupPerformanceTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		OldNoDuplicatePointsSet oldS;
		NoDuplicatePointsSet newS;
		Random rnd = new Random(System.currentTimeMillis());
		int numpruebas = 50;
		int numelem = 1000;

		long time = System.currentTimeMillis();
		for (int t = 0; t < numpruebas; t++) {
			oldS = new OldNoDuplicatePointsSet(numelem);
			for (int i = 0; i < numelem; i++) {
				Point p = new Point(rnd.nextInt(numelem), rnd.nextInt(numelem),
						(short) rnd.nextInt(256));
				oldS.add(p);
			}
		}
		double media = (System.currentTimeMillis() - time) / numpruebas;
		System.out.println("Old tardó: " + media);

		time = System.currentTimeMillis();
		for (int t = 0; t < numpruebas; t++) {
			newS = new NoDuplicatePointsSet(numelem);
			for (int i = 0; i < numelem; i++) {
				Point p = new Point(rnd.nextInt(numelem), rnd.nextInt(numelem),
						(short) rnd.nextInt(256));
				newS.add(p);
			}
		}
		media = (System.currentTimeMillis() - time) / numpruebas;
		System.out.println("New tardó: " + media);

		time = System.currentTimeMillis();
		for (int t = 0; t < numpruebas; t++) {
			oldS = new OldNoDuplicatePointsSet(numelem);
			for (int i = 0; i < numelem; i++) {
				Point p = new Point(rnd.nextInt(numelem), rnd.nextInt(numelem),
						(short) rnd.nextInt(256));
				oldS.add(p);
				p = new Point(p.getCol(), p.getRow(), (short) rnd.nextInt(256));
				oldS.add(p);
				p = new Point(p.getCol(), p.getRow(), (short) rnd.nextInt(256));
				oldS.add(p);
			}
		}
		media = (System.currentTimeMillis() - time) / numpruebas;
		System.out.println("Old tardó: " + media);

		time = System.currentTimeMillis();
		for (int t = 0; t < numpruebas; t++) {
			newS = new NoDuplicatePointsSet(numelem);
			for (int i = 0; i < numelem; i++) {
				Point p = new Point(rnd.nextInt(numelem), rnd.nextInt(numelem),
						(short) rnd.nextInt(256));
				newS.add(p);
				p = new Point(p.getCol(), p.getRow(), (short) rnd.nextInt(256));
				newS.add(p);
				p = new Point(p.getCol(), p.getRow(), (short) rnd.nextInt(256));
				newS.add(p);
			}
		}
		media = (System.currentTimeMillis() - time) / numpruebas;
		System.out.println("New tardó: " + media);

	}

}
