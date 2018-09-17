package gpms.senary.database;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import gpms.senary.bean.Tweet;
import gpms.senary.bean.Tweets;

public class TweetDatabaseTest {

	@Test
	public void testGetAllTweets() throws SQLException {
		TweetDatabase tdb = new TweetDatabase();
		Tweets tweets = tdb.getAllTweetsInDefaultDb();
		assertTrue("Tweet number (" + tweets.getTweets().size() + ") should be greater than  (" + 0 + ")", tweets.getTweets().size() > 0);
		System.out.println("Tweets stored in db " + tweets.getTweets().size());
	}

	@Test
	public void testGetNumberOfTweets() throws SQLException {
		TweetDatabase tdb = new TweetDatabase();
		Tweets tweets = tdb.getNumberOfTweetsInDefaultDb(5);
		assertTrue("Tweet number (" + tweets.getTweets().size() + ") should be equal (" + 5 + ")", tweets.getTweets().size() == 5);
		System.out.println("Tweets obtained " + tweets.getTweets().size());
	}
	
	@Test
	public void testKeywordSearch() throws SQLException {
		TweetDatabase tdb = new TweetDatabase();
		Tweets tweets = tdb.getAllTweetsInDefaultDb("york");
		assertTrue("Tweet number (" + tweets.getTweets().size() + ")  should be greater than  (" + 0 + ")", tweets.getTweets().size() > 0);
		for (Tweet t : tweets.getTweets()) {
			System.out.println(t.getText());
		}
		System.out.println("Tweets obtained " + tweets.getTweets().size());
	}
	
	@Test
	public void testGetTweetsByKm() throws SQLException {
		TweetDatabase tdb = new TweetDatabase();
		List<Integer> tweets = tdb.getAllTweetsByKm(TweetDatabase.DEFAULT_DB, 25);
		assertTrue("Tweet number (" + tweets.size() + ")  should be greater than  (" + 0 + ")", tweets.size() > 0);
		System.out.println("Tweets obtained " + tweets.size());
	}
	

}
 