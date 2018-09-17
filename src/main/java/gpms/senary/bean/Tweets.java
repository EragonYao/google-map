package gpms.senary.bean;

import java.util.ArrayList;
import java.util.List;

public class Tweets  {
	private List<Tweet> tweets;
	
	public Tweets() {
		tweets = new ArrayList<Tweet>();
	}
	
	public List<Tweet> getTweets() {
		return tweets;
	}
	
	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}
	
	public void addTweet(Tweet tweet) {
		tweets.add(tweet);
	}
	public Tweet getTweet(int index) {
		return this.tweets.get(index);
	}
}
