package gpms.senary.bean.geoJson;

import gpms.senary.bean.LatAndLng;

public class Grid {
	private LatAndLng min;
	private LatAndLng max;
	public LatAndLng getMin() {
		return min;
	}
	public void setMin(LatAndLng min) {
		this.min = min;
	}
	public LatAndLng getMax() {
		return max;
	}
	public void setMax(LatAndLng max) {
		this.max = max;
	}
	
}
