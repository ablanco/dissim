package test;

import java.util.HashSet;

import util.Point;


public class dummyTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HashSet<Point> p = new HashSet<Point>();
		p.add(new Point(0, 0));
		p.add(new Point(0,3));
		p.add(new Point(0,1));
		System.err.println(p.size());
		
	}

}
