package gpms.senary.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;

import gpms.senary.bean.LatAndLng;
import gpms.senary.bean.geoJson.GeoJson;
import gpms.senary.bean.geoJson.Grid;

/** 
 * A Class that generate labels for each Tweet data. 
 * Label markered by the each Tweet's location and divided to each related grid.
 * 
 * 
 * @author Huaiwen Guan  
 * @version 1.1 all function implemented. 
 * @version 1.2 Fix a bug in function loadBoundary(), which cause the wrong of lat and lng. 
 * @see gmps.senary.servlet.GridStyleServlet
 */
public class LabelMarker {

	static final private String PATH_TO_RESOURCES = System.getProperty("user.home") + "/gpms_senary_files/resources/";
	static final private String PATH_TO_50KM = "OSGB_Grid_50km.geojson";
	static final private String PATH_TO_25KM = "OSGB_Grid_25km.geojson";
	static final private String PATH_TO_20KM = "OSGB_Grid_20km.geojson";
	static final private String PATH_TO_10KM = "OSGB_Grid_10km.geojson";
	static final private String PATH_TO_5KM = "OSGB_Grid_5km.geojson";
	static final private String PATH_TO_100KM = "OSGB_Grid_100km.geojson";
	
	// several lists to store related grids in memory
	private List<Grid> _100km = new ArrayList<Grid>();
	private List<Grid> _10km = new ArrayList<Grid>();
	private List<Grid> _20km = new ArrayList<Grid>();
	private List<Grid> _25km = new ArrayList<Grid>();
	private List<Grid> _50km = new ArrayList<Grid>();
	private List<Grid> _5km = new ArrayList<Grid>();
	
	/** 
	 * Create a new instance of the LabelMarker class. 
	 * When the new instance created, it loads the boundaries to the memory
	 * 
	 * 
	 * @see gmps.senary.database.TweetDataBase#labelMarker
	 */ 
	public LabelMarker() {
		loadBoundaries();
	}
	
	/** 
	 * Loading the boundary depends on the selected grid spacing 
	 * 
	 * @param selectedKm grid spacing
	 * @return a list of Grid bean.
	 * @exception Exception if there is no specific GeoJSON file.
	 * @see #loadBoundaries 
	 */ 
	private List<Grid> loadBoundary(int selectedKm) {
		
		List<Grid> list = new ArrayList<Grid>();
		
		String path = PATH_TO_RESOURCES;
		
		StringBuffer sb = new StringBuffer();
		InputStream stream = null;
		BufferedReader reader = null;
		// set default value for compared later
		double maxLng = -1;
		double maxLat = -1;
		double minLng = 100;
		double minLat = 100;
		// select file path by param selectedKm
		switch (selectedKm) {
		case 50:
			path += PATH_TO_50KM;
			break;
		case 25:
			path += PATH_TO_25KM;
			break;
		case 20:
			path += PATH_TO_20KM;
			break;
		case 10:
			path += PATH_TO_10KM;
			break;
		case 5:
			path += PATH_TO_5KM;
			break;
		case 100:
		default:
			path += PATH_TO_100KM;
		}
		
		try {
			// read file
			File jsonFile = new File(path);
			stream = new FileInputStream(jsonFile);
			reader = new BufferedReader(new InputStreamReader(stream));
	        String line;
	        while ((line = reader.readLine()) != null) {
	        	sb.append(line);
	        }
	        // parse geoJSON file
    		GeoJson gj = JSON.parseObject(sb.toString(), GeoJson.class);
    		// read each gird's location data and store it in correspond list.
    		for (int i  = 0; 
    				i != gj.getFeatures().size(); 
    				++i) {
	    		Grid grid = new Grid();
	    		LatAndLng min = new LatAndLng();
	    		LatAndLng max = new LatAndLng();
	    		
	    		double a = gj.getFeatures().get(i).getGeometry().getCoordinates()[0][0][1];
	    		double b = gj.getFeatures().get(i).getGeometry().getCoordinates()[0][0][0];
	    		double c = gj.getFeatures().get(i).getGeometry().getCoordinates()[0][1][1];
	    		double d = gj.getFeatures().get(i).getGeometry().getCoordinates()[0][1][0];
	    		double e = gj.getFeatures().get(i).getGeometry().getCoordinates()[0][2][1];
	    		double f = gj.getFeatures().get(i).getGeometry().getCoordinates()[0][2][0];
	    		double g = gj.getFeatures().get(i).getGeometry().getCoordinates()[0][3][1];
	    		double h = gj.getFeatures().get(i).getGeometry().getCoordinates()[0][3][0];
	    		
	    		min.setLat(Math.min(Math.min(a, c), Math.min(e, g)));
	    		min.setLng(Math.min(Math.min(b, d), Math.min(f, h)));
	    		max.setLat(Math.max(Math.max(a, c), Math.max(e, g)));
	    		max.setLng(Math.max(Math.max(b, d), Math.max(f, h)));
	    		
	    		grid.setMin(min);
	    		grid.setMax(max);
	    		
	    		minLat = Math.min(grid.getMin().getLat(), minLat);
	    		minLng = Math.min(grid.getMin().getLng(), minLng);
	    		maxLat = Math.max(grid.getMax().getLat(), maxLat);
	    		maxLng = Math.max(grid.getMax().getLng(), maxLng);
	    		
	    		list.add(grid);
	    	}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				stream.close();
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}
	
	/** 
	 * Loading all boundaries of existing grid spacing 
	 * 
	 * @see #LabelMarker 
	 */ 
	private void loadBoundaries() {
		_50km = loadBoundary(50);
		_25km = loadBoundary(25);
		_20km = loadBoundary(20);
		_10km = loadBoundary(10);
		_5km = loadBoundary(5);
		_100km = loadBoundary(100);
	}
	
	/** 
	 * Mark label by specific location and grid spacing
	 * 
	 * @param selectedKm grid spacing.
	 * @param lal specific Tweet location.
	 * @return label number.
	 * @see #getLabelIn100KM 
	 * @see #getLabelIn50KM 
	 * @see #getLabelIn25KM 
	 * @see #getLabelIn20KM 
	 * @see #getLabelIn10KM 
	 * @see #getLabelIn5KM
	 */ 
	private int getLableByDistance(int selectedKm, LatAndLng lal) {
		List<Grid> thisList = null;
		int i = 0;
		
		// select list by param selectedKm
		switch (selectedKm) {
		case 50:
			thisList = _50km;
			break;
		case 25:
			thisList = _25km;
			break;
		case 20:
			thisList = _20km;
			break;
		case 10:
			thisList = _10km;
			break;
		case 5:
			thisList = _5km;
			break;
		case 100:
		default:
			thisList = _100km;
		}
		
		for (Grid grid: thisList) {
			double thisLat = lal.getLat();
			double thisLng = lal.getLng();
			//select right grid and return its label
			if (thisLat <= grid.getMax().getLat()
					&& thisLat > grid.getMin().getLat()
					&& thisLng <= grid.getMax().getLng()
					&& thisLng >= grid.getMin().getLng()) {
				return i;
			}
			++i;
		}
		
		return i;
	}
	
	/** 
	 * Mark label by specific location in grid spacing 50KM
	 * 
	 * @param lal specific Tweet location.
	 * @return label number.
	 * @see gmps.senary.database.TweetDataBase#updateTweet
	 */
	public int getLabelIn50KM(LatAndLng lal) {
		return  getLableByDistance(50, lal);
	}
	
	/** 
	 * Mark label by specific location in grid spacing 25KM
	 * 
	 * @param lal specific Tweet location.
	 * @return label number.
	 * @see gmps.senary.database.TweetDataBase#updateTweet
	 */
	public int getLabelIn25KM(LatAndLng lal) {
		return  getLableByDistance(25, lal);
	}
	
	/** 
	 * Mark label by specific location in grid spacing 20KM
	 * 
	 * @param lal specific Tweet location.
	 * @return label number.
	 * @see gmps.senary.database.TweetDataBase#updateTweet
	 */
	public int getLabelIn20KM(LatAndLng lal) {
		return  getLableByDistance(20, lal);
	}
	
	/** 
	 * Mark label by specific location in grid spacing 10KM
	 * 
	 * @param lal specific Tweet location.
	 * @return label number.
	 * @see gmps.senary.database.TweetDataBase#updateTweet
	 */
	public int getLabelIn10KM(LatAndLng lal) {
		return  getLableByDistance(10, lal);
	}
	
	/** 
	 * Mark label by specific location in grid spacing 5KM
	 * 
	 * @param lal specific Tweet location.
	 * @return label number.
	 * @see gmps.senary.database.TweetDataBase#updateTweet
	 */
	public int getLabelIn5KM(LatAndLng lal) {
		return  getLableByDistance(5, lal);
	}
	
	/** 
	 * Mark label by specific location in grid spacing 100KM
	 * 
	 * @param lal specific Tweet location.
	 * @return label number.
	 * @see gmps.senary.database.TweetDataBase#updateTweet
	 */
	public int getLabelIn100KM(LatAndLng lal) {
		return  getLableByDistance(100, lal);
	}
}
