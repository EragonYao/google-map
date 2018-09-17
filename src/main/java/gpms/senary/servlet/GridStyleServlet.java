package gpms.senary.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import gpms.senary.database.TweetDatabase;
import gpms.senary.util.GeoJsonNumCounter;

/**
 * Servlet implementation class GridStyleServlet
 */
@WebServlet("/GridStyle")
public class GridStyleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GridStyleServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		// the selected grid spacing
		int selectedKm = Integer.valueOf(request.getParameter("km"));
		
		GeoJsonNumCounter geoJsonNumCounter = new GeoJsonNumCounter();
		TweetDatabase tdb = new TweetDatabase();
		
		// the number of grid in selected grid spacing
		int gridNum = geoJsonNumCounter.getBySelectedKm(selectedKm);
		// store the final grid color
		int[] gridStyle = new int[gridNum];
		// store the number of Tweets in each grid
		int[] gridLabels = new int[gridNum];
		List<Integer> tweetLabels = null;
		// counter the number below
		int i = 0;
		// load the labels of Tweets from dataabase
		try {
			tweetLabels = tdb.getAllTweetsByKm(TweetDatabase.DEFAULT_DB, selectedKm);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// count the number of Tweets in each grid
		for (int tweetLabel: tweetLabels) {
			if (tweetLabel == gridNum) {
				continue;
			}
			gridLabels[tweetLabel] += 1;
		}
		
		// give each grid color
		i = 0;
		for (int gridLabel: gridLabels) {
			int num = 6;
			if (gridLabel >= tweetLabels.size() * .1) {
				num = 5;
			} else if (gridLabel >= tweetLabels.size() * .08) {
				num = 4;
			} else if (gridLabel >= tweetLabels.size() * .06) {
				num = 3;
			} else if (gridLabel >= tweetLabels.size() * .04) {
				num = 2;
			} else if (gridLabel >= tweetLabels.size() * .02) {
				num = 1;
			} 
			
			gridStyle[i] = num;
			++i;
		}
		
		// translate color by JSON format 
		response.getWriter().write(JSON.toJSONString(gridStyle));
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
