package gpms.senary.bean.geoJson;

public class Feature {
	private String type;
	private Properties2 properties;
	private Geometry geometry;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Properties2 getProperties() {
		return properties;
	}
	public void setProperties(Properties2 properties) {
		this.properties = properties;
	}
	public Geometry getGeometry() {
		return geometry;
	}
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}
}
