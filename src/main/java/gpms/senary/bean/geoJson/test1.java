package gpms.senary.bean.geoJson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.alibaba.fastjson.JSON;

public class test1 {

	public static void main(String[] args) {
		String pathToResources = System.getProperty("user.home") + "/gpms_senary_files/resources/";
		InputStream stream = null;
		BufferedReader reader = null;
		StringBuffer sb = new StringBuffer();
		
		try {
			File jsonFile = new File(pathToResources + "OSGB_Grid_50km.geojson");
			stream = new FileInputStream(jsonFile);
			reader = new BufferedReader(new InputStreamReader(stream));
	        String line;
	        while ((line = reader.readLine()) != null) {
	        	sb.append(line);
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
		
		GeoJson gj = JSON.parseObject(sb.toString(), GeoJson.class);
		System.out.println(gj.getFeatures().get(0).getGeometry().getCoordinates()[0][0][0]);
		System.out.println(gj.getFeatures().get(0).getGeometry().getCoordinates()[0][0][1]);
	}

}
