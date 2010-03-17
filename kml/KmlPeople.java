package kml;

import util.HexagonalGrid;
import util.Snapshot;
import util.Updateable;

public class KmlPeople implements Updateable{

	@Override
	public void finish() {
		// Nada
		
	}

	@Override
	public String getConversationId() {
		// Nada
		return null;
	}

	@Override
	public void init() {
		// Nada
		
	}

	@Override
	public void update(Object obj) throws IllegalArgumentException {
		if (!(obj instanceof Snapshot))
			throw new IllegalArgumentException(
					"Object is not an instance of Snapshot");
		Snapshot snap = (Snapshot) obj;
		HexagonalGrid grid = snap.getGrid();
		
	}

}
