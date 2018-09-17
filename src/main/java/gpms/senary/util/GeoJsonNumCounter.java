package gpms.senary.util;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.alibaba.fastjson.JSON;

import gpms.senary.bean.geoJson.GeoJson;

/** 
 * A Class that count the number of grid in each grid spacing from GeoJSON files.
 * 
 * 
 * @author Huaiwen Guan
 * @version 1.0 Initial development. 
 * @see gmps.senary.database.GridStyleServlet
 */ 
public class GeoJsonNumCounter {
	
	// several path of GeoJSON files paths
	static final private String PATH_TO_RESOURCES = System.getProperty("user.home") + "/gpms_senary_files/resources/";
	static final private String PATH_TO_50KM = "OSGB_Grid_50km.geojson";
	static final private String PATH_TO_25KM = "OSGB_Grid_25km.geojson";
	static final private String PATH_TO_20KM = "OSGB_Grid_20km.geojson";
	static final private String PATH_TO_10KM = "OSGB_Grid_10km.geojson";
	static final private String PATH_TO_5KM = "OSGB_Grid_5km.geojson";
	static final private String PATH_TO_100KM = "OSGB_Grid_100km.geojson";
	
	// variables to store number of grid in each grid spacing
	private int _100KmNum;
	private int _50KmNum;
	private int _25KmNum;
	private int _20KmNum;
	private int _10KmNum;
	private int _5KmNum;
	
	/** 
	 * Create a new instance of the GeoJsonNumCounter class. 
	 * When the new instance created, it loads the number of grids at the same time.
	 * 
	 * @see gmps.senary.database.GridStyleServlet#doGet
	 */  
	public GeoJsonNumCounter() {
		_100KmNum = this.count(100);
		_50KmNum = this.count(50);
		_25KmNum = this.count(25);
		_20KmNum = this.count(20);
		_10KmNum = this.count(10);
		_5KmNum = this.count(5);
	}
	
	/** 
	 * Get the number of grid in grid spacing 100Km.
	 * 
	 * @return number of grid.
	 */
	public int get_100KmNum() {
		return _100KmNum;
	}
	/** 
	 * Get the number of grid in grid spacing 50Km.
	 * 
	 * @return number of grid.
	 */
	public int get_50KmNum() {
		return _50KmNum;
	}
	/** 
	 * Get the number of grid in grid spacing 25Km.
	 * 
	 * @return number of grid.
	 */
	public int get_25KmNum() {
		return _25KmNum;
	}
	/** 
	 * Get the number of grid in grid spacing 20Km.
	 * 
	 * @return number of grid.
	 */
	public int get_20KmNum() {
		return _20KmNum;
	}
	/** 
	 * Get the number of grid in grid spacing 10Km.
	 * 
	 * @return number of grid.
	 */
	public int get_10KmNum() {
		return _10KmNum;
	}
	/** 
	 * Get the number of grid in grid spacing 5Km.
	 * 
	 * @return number of grid.
	 */
	public int get_5KmNum() {
		return _5KmNum;
	}
	/** 
	 * Get the number of grid in selected grid spacing.
	 * 
	 * @param selectedKm grid spacing.
	 * @return number of grid.
	 * @return -1 if failed.
	 */
	public int getBySelectedKm(int selectedKm) {
		// select number of grid by selected grid spacing
		switch (selectedKm) {
		case 50:
			return get_50KmNum();
		case 25:
			return get_25KmNum();
		case 20:
			return get_20KmNum();
		case 10:
			return get_10KmNum();
		case 5:
			return get_5KmNum();
		case 100:
		default:
			return get_100KmNum();
		}
	}
	/** 
	 * Counting the grid number in selected grid spacing.
	 * 
	 * @param selectedKm grid spacing.
	 * @return number of grid.
	 */
	private int count(int selectedKm) {
		
		String path = PATH_TO_RESOURCES;
		StringBuffer sb = new StringBuffer();
		InputStream stream = null;
		BufferedReader reader = null;
		
		// selected path of GeoJSON files by selected 
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
			// read the GeoJSON file
			File jsonFile = new File(path);
			stream = new FileInputStream(jsonFile);
			reader = new BufferedReader(new InputStreamReader(stream));
	        String line;
	        while ((line = reader.readLine()) != null) {
	        	sb.append(line);
	        }
	        // parse the GeoJSON files
    		GeoJson gj = JSON.parseObject(sb.toString(), GeoJson.class);
    		// return the number of grid
    		return gj.getFeatures().size();
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
		// return -1 if failed
		return -1;
	}
}
