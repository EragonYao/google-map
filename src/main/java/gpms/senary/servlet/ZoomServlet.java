package gpms.senary.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.vividsolutions.jts.geom.Coordinate;

import gpms.senary.bean.Tweets;
import gpms.senary.util.GeoJsonGenerator;

/**
 * Servlet implementation class ZoomServley
 */
@WebServlet("/ZoomServley")
public class ZoomServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ZoomServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		int curZoomLevel = Integer.valueOf(request.getParameter("zoomLevel"));
		String JSONStr = null;
		double nEastLat = Double.valueOf(request.getParameter("nEastLat"));
		double nEastLng = Double.valueOf(request.getParameter("nEastLng"));
		double sWestLat = Double.valueOf(request.getParameter("sWestLat"));
		double sWestLng = Double.valueOf(request.getParameter("sWestLng"));
		
		// calculate the nWestLat, nWestLng
		double nWestLat = nEastLat;
		double nWestLng = sWestLng;
		
		// calculate the sEastLat, sEastLng
		double sEastLat = sWestLat;
		double sEastLng = nEastLng;
		
		List<Coordinate> coordinates = new ArrayList<Coordinate>();
		
		// build the coordinates of the rectange of the current view
		
		Coordinate ne = new Coordinate();
		ne.setOrdinate(0, nEastLat);
		ne.setOrdinate(1, nEastLng);
		coordinates.add(ne);
		
		Coordinate se = new Coordinate();
		se.setOrdinate(0, sEastLat);
		se.setOrdinate(1, sEastLng);
		coordinates.add(se);
		
		Coordinate sw = new Coordinate();
		sw.setOrdinate(0, sWestLat);
		sw.setOrdinate(1, sWestLng);
		coordinates.add(sw);
		
		Coordinate nw = new Coordinate();
		nw.setOrdinate(0, nWestLat);
		nw.setOrdinate(1, nWestLng);
		coordinates.add(nw);
		
		// need to add the north east twice to enable the polygon to be built (completes the rectange)
		coordinates.add(ne);
		
		try {
			Tweets tweets = GeoJsonGenerator.checkTweetsInPolygon(coordinates);
			if (tweets.getTweets().size() > 1000) {
				tweets.getTweets().subList(1000, tweets.getTweets().size()).clear();
			}
			JSONStr = JSON.toJSONString(tweets);
			System.out.println("Done");
			response.getWriter().write(JSONStr);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
