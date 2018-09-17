package gpms.senary.bean;

public class GeoJsonAttribute {
	private String areaName;
	private int numberOfTweetsInArea;
	private String percentageOfTweets;
	
	public int getNumberOfTweetsInArea() {
		return numberOfTweetsInArea;
	}
	public void setNumberOfTweetsInArea(int numberOfTweetsInArea) {
		this.numberOfTweetsInArea = numberOfTweetsInArea;
	}
	public String getPercentageOfTweets() {
		return percentageOfTweets;
	}
	public void setPercentageOfTweets(String percentageOfTweets) {
		this.percentageOfTweets = percentageOfTweets;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
}
