package util.jcoord;

import java.util.Comparator;


public class LatLngComparator implements Comparator<LatLng> {

	@Override
	public int compare(LatLng arg0, LatLng arg1) {
		return (int) arg0.distance(arg1);
	}

}
