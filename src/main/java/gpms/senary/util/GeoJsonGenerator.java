/*
 * @(#) GeoJsonGenerator.java 1.0 2018/02/18
 * 
 * Copyright (c) 2018 University of York.
 * All rights reserved. 
 *
 */

package gpms.senary.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.geojson.LngLatAlt;
import org.geojson.MultiPolygon;
import org.geojson.Polygon;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

import gpms.senary.bean.GeoJsonArea;
import gpms.senary.bean.GeoJsonAttribute;
import gpms.senary.bean.Tweet;
import gpms.senary.bean.Tweets;
import gpms.senary.database.TweetDatabase;

/** 
 * A class that generates geojson for the front end to plot number of tweets in given area.
 * <p> 
 * If wanting to use a database other than the default one, use the generateGeoJson (...) methods
 * If wanting to display all tweets (I.e. no keyword) pass null as an argument to the methods
 * 
 * @author Justin Cooper 
 * @version 1.0 Initial development. 
 * @see #GeojsonTileServlet.java
 */ 

public class GeoJsonGenerator {
	
	/** 
	 * Create a new geojson file with the areas and percentage of the tweets matching to keywords added as attributes to the areas
	 * This will look in the default database (I.e. twitter-data.db.mv)
	 * 
	 * @param inputJson A java.io.File object containing the path of the input geojson file
	 * @param areaAttributeName The name of the attribute in the input file that uniquely identifies a region (E.g. in the OSGB_Grid_Files, the attribute is TILE_NAME)
	 * @param keyword A single keyword that a tweet must contain for it to be counted. If wanting to count all tweets (I.e. no keyword), pass null as this argument
	 * @return a serialized geojson object
	 * @exception JsonParseException if error parsing geojson
	 * @exception JsonMappingException if cannot map to geojson attribute. 
	 * @exception IOException if error reading input string 
	 */ 
	public static String generateGeoJsonUsingDefaultDb(File inputJson, String areaAttributeName, String keyword) throws JsonParseException, JsonMappingException, IOException {
		return generateGeoJson(new String(Files.readAllBytes(Paths.get(inputJson.getPath()))), areaAttributeName, TweetDatabase.DEFAULT_DB, keyword);
	}
	
	/** 
	 * Create a new geojson file with the areas and percentage of the tweets matching to keywords added as attributes to the areas
	 * This will look in a user defined database
	 * 
	 * @param inputJson A java.io.File object containing the path of the input geojson file
	 * @param areaAttributeName The name of the attribute in the input file that uniquely identifies a region (E.g. in the OSGB_Grid_Files, the attribute is TILE_NAME)
	 * @param dbName The filename of the database (do not include the file extension)
	 * @param keyword A single keyword that a tweet must contain for it to be counted. If wanting to count all tweets (I.e. no keyword), pass null as this argument
	 * @return a serialized geojson object
	 * @exception JsonParseException if error parsing geojson
	 * @exception JsonMappingException if cannot map to geojson attribute. 
	 * @exception IOException if error reading input string 
	 */ 
	public static String generateGeoJson(File inputJson, String areaAttributeName, String dbName, String keyword) throws JsonParseException, JsonMappingException, IOException {
		return generateGeoJson(new String(Files.readAllBytes(Paths.get(inputJson.getPath()))), areaAttributeName, dbName, keyword);
	}
	
	/** 
	 * Create a new geojson file with the areas and percentage of the tweets matching to keywords added as attributes to the areas
	 * This will look in the default database (I.e. twitter-data.db.mv)
	 * 
	 * @param inputJson A string containing serialized geojson
	 * @param areaAttributeName The name of the attribute in the input file that uniquely identifies a region (E.g. in the OSGB_Grid_Files, the attribute is TILE_NAME)
	 * @param keyword A single keyword that a tweet must contain for it to be counted. If wanting to count all tweets (I.e. no keyword), pass null as this argument
	 * @return a serialized geojson object
	 * @exception JsonParseException if error parsing geojson
	 * @exception JsonMappingException if cannot map to geojson attribute. 
	 * @exception IOException if error reading input string 
	 */ 
	public static String generateGeoJsonUsingDefaultDb(String inputJson, String areaAttributeName, String keyword) throws JsonParseException, JsonMappingException, IOException {
		return generateGeoJson(inputJson, areaAttributeName, TweetDatabase.DEFAULT_DB, keyword);
	}

	/** 
	 * Create a new geojson file with the areas and percentage of the tweets matching to keywords added as attributes to the areas
	 * This will look in a user defined database
	 * 
	 * @param inputJson A string containing serialized geojson
	 * @param areaAttributeName The name of the attribute in the input file that uniquely identifies a region (E.g. in the OSGB_Grid_Files, the attribute is TILE_NAME)
	 * @param dbName The filename of the database (do not include the file extension)
	 * @param keyword A single keyword that a tweet must contain for it to be counted. If wanting to count all tweets (I.e. no keyword), pass null as this argument
	 * @return a serialized geojson object
	 * @exception JsonParseException if error parsing geojson
	 * @exception JsonMappingException if cannot map to geojson attribute. 
	 * @exception IOException if error reading input string 
	 */ 
	public static String generateGeoJson(String inputJson, String areaAttributeName, String dbName, String keyword) throws JsonParseException, JsonMappingException, IOException {
		String outputJson = null;		
		GeoJsonObject object = new ObjectMapper().readValue(inputJson, GeoJsonObject.class);
		List<GeoJsonArea> areas = new ArrayList<GeoJsonArea>();
		GeometryFactory gf = new GeometryFactory();
		
		if (object instanceof FeatureCollection) {
			FeatureCollection fc = (FeatureCollection) object;
			// for every area in the input geojson
			for (Feature feature : fc.getFeatures()) {
				GeoJsonObject ob = feature.getGeometry();
				GeoJsonArea area = new GeoJsonArea();
				// get the name of the area
				area.setName(feature.getProperty(areaAttributeName).toString());
				// check whether it's a polygon or multi-polygon
				if (ob instanceof Polygon) {
					ArrayList<Coordinate> points = new ArrayList<Coordinate>();
					Polygon pg = (Polygon) ob;
					// get all coordinates that make up the polygon
					List<List<LngLatAlt>> a = pg.getCoordinates();
					List<LngLatAlt> b = a.get(0);
					for (LngLatAlt lst : b) {
						// add the coordinate to a list so we can build the polygon
						points.add(new Coordinate(lst.getLatitude(), lst.getLongitude()));
					}
					// build the polygon
					area.setArea(gf.createPolygon(new LinearRing(new CoordinateArraySequence(points.toArray(new Coordinate[points.size()])), gf),null));
				} else if (ob instanceof MultiPolygon) {
					MultiPolygon pg = (MultiPolygon) ob;

					ArrayList<com.vividsolutions.jts.geom.Polygon> polygons = new ArrayList<com.vividsolutions.jts.geom.Polygon>();
					List<List<List<LngLatAlt>>> allPoints = pg.getCoordinates();
					// get all the polygons that make up the multi-polygon
					for (List<List<LngLatAlt>> p : allPoints) {
						// get the coordinate of a single polygon that makes up the multi-polygon
						ArrayList<Coordinate> points = new ArrayList<Coordinate>();
						List<LngLatAlt> b = p.get(0);
						for (LngLatAlt lst : b) {
							// dd the coordiante to a list so we can build the polygon
							points.add(new Coordinate(lst.getLatitude(), lst.getLongitude()));
						}
						// build the polygon and save, so we can build the multi-polygon later
						polygons.add(gf.createPolygon(new LinearRing(
								new CoordinateArraySequence(points.toArray(new Coordinate[points.size()])), gf),null));
					}
					// build the multi-polygon
					area.setArea(gf.createMultiPolygon(polygons.toArray(new com.vividsolutions.jts.geom.Polygon[polygons.size()])));
				}
				// save the polygon in a list of areas 
				areas.add(area);
			}
		}
		
		// now we have all the areas represented as polygons, we need to find the coordinates of the tweets. 
		// we can then identify where the tweets were uploaded by checking to see what polygon the coordinate of the tweet was in
		
		Map<String, GeoJsonAttribute> areaDict = new HashMap<String, GeoJsonAttribute>();
		TweetDatabase db = new TweetDatabase();
		
		try {
			// get the tweets from the database
			Tweets tweets = null;
			if (keyword == null) {
				tweets = db.getAllTweets(dbName);
			}
			else {
				tweets = db.getAllTweets(dbName, keyword);
			}
			// for each tweet
			for (Tweet t : tweets.getTweets()) {
				// get the name of the area the tweet was in
				String areaName = findArea(new Coordinate(t.getLat(), t.getLng()), areas);
				// null area probs means the tweet wasn't in the UK, or an area represented by the geojson
				if (areaName != null) {
					// check whether there has already been a tweet in this area
					if (areaDict.containsKey(areaName)) {
						// yes there has already been a tweet, add 1 to the number of tweets in this area
						GeoJsonAttribute attribute = areaDict.get(areaName);
						attribute.setNumberOfTweetsInArea(attribute.getNumberOfTweetsInArea() + 1);
						areaDict.put(areaName, attribute);
					}
					else {
						// no tweets in this area yet, create a new dictionary with key=areaname and value = 1
						GeoJsonAttribute attribute = new GeoJsonAttribute();
						attribute.setNumberOfTweetsInArea(1);
						areaDict.put(areaName, attribute);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// count how many tweets in total the data represents
		int totalNumber = 0;
		for (String key : areaDict.keySet()) {
			totalNumber += areaDict.get(key).getNumberOfTweetsInArea();
		}

		// calculate the % of the total tweets the area represents
		for (String key : areaDict.keySet()) {
			GeoJsonAttribute attribute = areaDict.get(key);
			String percentage = Float.toString(((float)attribute.getNumberOfTweetsInArea()/(float)totalNumber));
			attribute.setPercentageOfTweets(percentage);
			areaDict.put(key, attribute);
		}

		// create a new geojson object with the areaname, number of tweets corresponding to the search criteria and the 
		// percentage of tweets corresponding to the search criteria the area has
		try {			
			if (object instanceof FeatureCollection) {
				FeatureCollection fc = (FeatureCollection) object;

				for (Feature feature : fc.getFeatures()) {
					String currentFeature = feature.getProperty(areaAttributeName).toString();	
					if (areaDict.get(currentFeature) != null) {
						feature.setProperty("areaName", currentFeature);
						feature.setProperty("numTweets", areaDict.get(currentFeature).getNumberOfTweetsInArea());
						feature.setProperty("percentage", areaDict.get(currentFeature).getPercentageOfTweets());	
					}			
				}
				
				outputJson = new ObjectMapper().writeValueAsString(fc);
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outputJson;
	}
	
	
	public static Tweets checkTweetsInPolygon(List<Coordinate> points) throws SQLException {
		// firstly build the polygon
		GeometryFactory gf = new GeometryFactory();
		com.vividsolutions.jts.geom.Polygon area = gf.createPolygon(new LinearRing(new CoordinateArraySequence(points.toArray(new Coordinate[points.size()])), gf),null);
		Tweets tweetsToPlot = new Tweets();
		// get the tweets from the database
		
		TweetDatabase tdb = new TweetDatabase();
		Tweets tweets = tdb.getAllTweetsInDefaultDb();
		List<Tweet> tweetList = tweets.getTweets();
		for (Tweet tweet : tweetList) {
			Point p = gf.createPoint(new Coordinate(tweet.getLat(), tweet.getLng()));
			if (p.within(area)) {
				tweetsToPlot.addTweet(tweet);
			}
		}
		
		return tweetsToPlot;
	}
	
	/** 
	 * Checks to see if a given coordinate is within a given list of areas. 
	 * If the coordinate is within the list of specified areas, the name of the area is returned
	 * 
	 * @param coord Coordinate to find the name of the area the point is located in
	 * @param areas List of areas to search
	 * @return a String containing the name of the area the coordinate lies within
	 */ 
	
	private static String findArea(Coordinate coord, List<GeoJsonArea> areas) {
		GeometryFactory gf = new GeometryFactory();
		Point point = gf.createPoint(coord);
		// check each area to see whether point is within the polygon
		for (GeoJsonArea area : areas) {
			if (point.within(area.getArea())) {
				return area.getName();
			}
		}
		return null;
	}
		
}
