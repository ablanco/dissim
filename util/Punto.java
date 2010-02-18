package util;


public class Punto implements Comparable<Punto> {
	public int x;
	public int y;
	public short z;

	public Punto(int x, int y, short z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public int compareTo(Punto o) {
		if (o.x == x && o.y == y)
			return 0;

		if (o.x > x)
			if (o.y > y)
				return 2;
			else
				return 1;
		return -1;
	}

	@Override
	public boolean equals(Object o) {
		Punto p = (Punto) o;
		return (p.x == x) && (p.y == y);
	}
}