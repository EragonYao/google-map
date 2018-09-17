package gpms.senary.util;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;

import gpms.senary.bean.Tweet;
import gpms.senary.bean.Tweets;

public class GeoJsonGeneratorTest {

	@Test
	public void test() {
		
		List<Coordinate> c = new ArrayList<Coordinate>();
		Coordinate t = new Coordinate();
		t.setOrdinate(0, 54.35786);
		t.setOrdinate(1, -0.22577);
		c.add(t);
		
		Coordinate t1 = new Coordinate();
		t1.setOrdinate(0, 54.23864);
		t1.setOrdinate(1, -0.22577);
		c.add(t1);
		
		Coordinate t2 = new Coordinate();
		t2.setOrdinate(0, 54.23864);
		t2.setOrdinate(1, -0.56772);
		c.add(t2);
		
		Coordinate t3 = new Coordinate();
		t3.setOrdinate(0, 54.35786);
		t3.setOrdinate(1, -0.56772);
		c.add(t3);
		
		c.add(t);
		
		Tweets tweets = null;
		
		try {
			tweets = GeoJsonGenerator.checkTweetsInPolygon(c);
			System.out.println(tweets.getTweets().size());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		for (Tweet tweet : tweets.getTweets()) {
			System.out.println(tweet.getText());
		}
	}

}
