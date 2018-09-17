package gpms.senary.bean.geoJson;

import java.util.ArrayList;
import java.util.List;

public class GeoJson {
	
	private String type;
	private Crs crs;
	private List<Feature> features = new ArrayList<Feature>();
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Crs getCrs() {
		return crs;
	}
	public void setCrs(Crs crs) {
		this.crs = crs;
	}
	public List<Feature> getFeatures() {
		return features;
	}
	public void setFeatures(List<Feature> features) {
		this.features = features;
	}
	public void addFeature(Feature feature) {
		this.features.add(feature);
	}
}
