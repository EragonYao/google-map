package gpms.senary.twitter;



import gpms.senary.database.TweetDatabase;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterStreamApi {
	private FilterQuery filter;
	private ConfigurationBuilder configurationBuilder;
	
	public TwitterStreamApi(FilterQuery filter, ConfigurationBuilder config) {
		this.filter = filter;
		this.configurationBuilder = config;
	}
	
	public FilterQuery getFilter() {
		return filter;
	}
	public void setFilter(FilterQuery filter) {
		this.filter = filter;
	}
	public ConfigurationBuilder getConfigurationBuilder() {
		return configurationBuilder;
	}
	public void setConfigurationBuilder(ConfigurationBuilder configurationBuilder) {
		this.configurationBuilder = configurationBuilder;
	}
	
	public void streamAndStoreInDefaultDb() {
		streamAndStore(TweetDatabase.DEFAULT_DB);
	}

	public void streamAndStore(String dbName) {
		TwitterStream twitterStream = new TwitterStreamFactory(configurationBuilder.build()).getInstance();
		TweetDatabase tweetDb = new TweetDatabase();
		StatusListener listener = new StatusListener() {
			@Override
			public void onStatus(Status status) {
				if (status.getGeoLocation() != null) { // we're only interested in tweets with location data
					try {
						tweetDb.writeTweet(dbName, status);
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println(status.getText() + status.getGeoLocation().getLatitude() + " "
							+ status.getGeoLocation().getLongitude() 
							+ status.getHashtagEntities());
				}
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
			}

			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
			}

			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
			}

			@Override
			public void onStallWarning(StallWarning warning) {
			}

			@Override
			public void onException(Exception ex) {
				ex.printStackTrace();
			}
		};

		twitterStream.addListener(listener);
		twitterStream.filter(filter);
	}
}
