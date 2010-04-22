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

import java.util.HashSet;
import java.util.Random;

import util.Point;
import util.java.NoDuplicatePointsSet;

public class SetPerformanceTest {

	public static void main(String[] args) {
		int tam = 300;
		int num = 5000;
		int pruebas = 10;
		long time;
		long total;
		Random rnd = new Random(System.currentTimeMillis());
		HashSet<Point> set1 = new HashSet<Point>(tam);
		NoDuplicatePointsSet set2 = new NoDuplicatePointsSet(tam);
		Point p;

		total = 0;
		for (int k = 0; k < pruebas; k++) {
			time = System.currentTimeMillis();
			for (int i = 0; i < num; i++) {
				p = new Point(rnd.nextInt(tam), rnd.nextInt(tam), (short) rnd
						.nextInt());
				set1.add(p);
			}
			total += System.currentTimeMillis() - time;
		}
		total /= pruebas;
		System.out.println("HashSet tardó " + total);

		total = 0;
		for (int k = 0; k < pruebas; k++) {
			time = System.currentTimeMillis();
			for (int i = 0; i < num; i++) {
				p = new Point(rnd.nextInt(tam), rnd.nextInt(tam), (short) rnd
						.nextInt());
				set2.add(p);
			}
			total += System.currentTimeMillis() - time;
		}
		total /= pruebas;
		System.out.println("NoDuplicatePointsSet tardó " + total);
	}

}
