package gpms.senary.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GeojsonTiles
 */
@WebServlet("/GeojsonTiles")
public class GeojsonTileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GeojsonTileServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		int selectedKm = Integer.valueOf(request.getParameter("km"));
		String pathToResources = System.getProperty("user.home") + "/gpms_senary_files/resources/";

		switch (selectedKm) {
		case 50:
			pathToResources += "OSGB_Grid_50km.geojson";
			break;
		case 25:
			pathToResources += "OSGB_Grid_25km.geojson";
			break;
		case 20:
			pathToResources += "OSGB_Grid_20km.geojson";
			break;
		case 10:
			pathToResources += "OSGB_Grid_10km.geojson";
			break;
		case 5:
			pathToResources += "OSGB_Grid_5km.geojson";
			break;
		case 100:
		default:
			pathToResources += "OSGB_Grid_100km.geojson";
		}

		InputStream stream = null;
		BufferedReader reader = null;
		try {
			File jsonFile = new File(pathToResources);
			stream = new FileInputStream(jsonFile);
			reader = new BufferedReader(new InputStreamReader(stream));
	        StringBuilder out = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            out.append(line);
	        }
			response.getWriter().write(out.toString());
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
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
