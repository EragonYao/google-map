package gpms.senary.bean;

import com.vividsolutions.jts.geom.Geometry;

public class GeoJsonArea {
	private Geometry area;
	private String name;

	public Geometry getArea() {
		return area;
	}

	public void setArea(Geometry area) {
		this.area = area;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
