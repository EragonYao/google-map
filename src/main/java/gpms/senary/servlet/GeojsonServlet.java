package gpms.senary.servlet;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import gpms.senary.util.GeoJsonGenerator;

/**
 * Servlet implementation class GeojsonServlet
 */
@WebServlet("/GeojsonServlet")
public class GeojsonServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GeojsonServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String keyword = (request.getParameter("keyword"));
		System.out.println(keyword);
		File jsonFile = new File(System.getProperty("user.home") + "/gpms_senary_files/resources/OSGB_Grid_10km.geojson");
		String jsonData = null;
		if (keyword == null) {
			jsonData = GeoJsonGenerator.generateGeoJsonUsingDefaultDb(jsonFile, "TILE_NAME", null);
		} else {
			jsonData = GeoJsonGenerator.generateGeoJsonUsingDefaultDb(jsonFile, "TILE_NAME", keyword);
		}
		//System.out.println(jsonData);
		response.getWriter().write(jsonData);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
