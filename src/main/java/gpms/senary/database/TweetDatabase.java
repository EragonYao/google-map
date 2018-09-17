package gpms.senary.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import gpms.senary.bean.LatAndLng;
import gpms.senary.bean.Tweet;
import gpms.senary.bean.Tweets;
import gpms.senary.util.LabelMarker;
import twitter4j.Status;


/** 
 * A class that connect to the database and insert, update and query transctions.
 * <p> 
 * @author Justin Cooper
 * @author Muteeb Alshammary 
 * @version 1.0 Initial development. 
 */


public class TweetDatabase {
	public static final String DEFAULT_DB = "twitter-data";
	private static final String JDBC_DRIVER = "org.h2.Driver";
	private static final String SENARY_DATABASE_DIRECTORY = "jdbc:h2:~/gpms_senary_files/database/";
	private static final String USERNAME = "sa";
	private static final String PASSWORD = "";
	private DatabaseConnection db;

	private LabelMarker labelMarker = new LabelMarker();

	// TODO: This connection function is being used for the moment as the one in
	// DatabaseConnection isn't closing correctly and is locking the DB
	// this isn't ideal, as currently it isn't thread-safe (I don't think) and uses
	// a deprecated way of obtaining a connection via JDBC
	private Connection getDbConnection(String dbName) {
		try {
			Class.forName(JDBC_DRIVER);
			return DriverManager.getConnection(SENARY_DATABASE_DIRECTORY + dbName, USERNAME, PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public TweetDatabase() {
		db = new DatabaseConnection();
	}

	/** 
	 * 
	 * Get all tweets from the database
	 * 
	 * @return Tweets objects which is contain list of tweet
	 * @exception SQLException if there is an error in the sql
	 */ 
	public Tweets getAllTweetsInDefaultDb() throws SQLException {
		return getAllTweets(DEFAULT_DB);
	}

	

	public Tweets getAllTweets(String dbName) throws SQLException {
		Tweets tweets = new Tweets();
		String selectAllTweets = "SELECT * FROM tweet";

		// this is a try-with-resources. A resource is an object that must be closed
		// after the program is finished with it
		// this prevents memory leaks
		try (Connection connection = getDbConnection(dbName);
				// Connection connection = db.getConnectionPool(JDBC_DRIVER,
				// SENARY_DATABASE_DIRECTORY + dbName, USERNAME, PASSWORD).getConnection();
				PreparedStatement statement = connection.prepareStatement(selectAllTweets);
				ResultSet resultSet = statement.executeQuery();) {
			while (resultSet.next()) {
				tweets.addTweet(convertRowToTweet(resultSet));
			}
		}
		return tweets;
	}

	/** 
	 * 
	 * Get all tweets from the database which is contain the keyword
	 * @param String keyword which users can use it to search in tweet's content
	 * @return Tweets objects which is contain list of tweet
	 * @exception SQLException if there is an error in the sql
	 */
	public Tweets getAllTweetsInDefaultDb(String keyword) throws SQLException {
		return getAllTweets(DEFAULT_DB, keyword);
	}

	public Tweets getAllTweets(String dbName, String keyword) throws SQLException {
		Tweets tweets = new Tweets();
		// SQL requires % to be placed around the keyword to be searched (%keyword%).
		StringBuilder sb = new StringBuilder();
		sb.append("%");
		sb.append(keyword);
		sb.append("%");

		String selectTweets = "SELECT * FROM tweet WHERE tweetdata LIKE ?";

		// this is a try-with-resources. A resource is an object that must be closed
		// after the program is finished with it
		// this prevents memory leaks
		// Note there is a nested try-with-resources to set the Limit parameter as you
		// cannot assign values in the try-with-resource statement
		try (Connection connection = getDbConnection(dbName);
				// Connection connection = db.getConnectionPool(JDBC_DRIVER,
				// SENARY_DATABASE_DIRECTORY + dbName, USERNAME, PASSWORD).getConnection();
				PreparedStatement statement = connection.prepareStatement(selectTweets);) {
			statement.setString(1, sb.toString());
			try (ResultSet resultSet = statement.executeQuery();) {
				while (resultSet.next()) {
					tweets.addTweet(convertRowToTweet(resultSet));
				}
			}
		}
		return tweets;
	}

	/** 
	 * 
	 * Get all tweets from the default database with limit number
	 * @param Integer the maximum number of tweets
	 * @return Tweets objects which is contain list of tweet
	 * @exception SQLException if there is an error in the sql
	 */
	
	public Tweets getNumberOfTweetsInDefaultDb(int numberOfTweets) throws SQLException {
		return getNumberOfTweets(DEFAULT_DB, numberOfTweets);
	}

	public Tweets getNumberOfTweets(String dbName, int numberOfTweets) throws SQLException {
		Tweets tweets = new Tweets();
		String selectTweets = "SELECT * FROM tweet LIMIT ?";

		// this is a try-with-resources. A resource is an object that must be closed
		// after the program is finished with it
		// this prevents memory leaks
		// Note there is a nested try-with-resources to set the Limit parameter as you
		// cannot assign values in the try-with-resource statement
		try (Connection connection = getDbConnection(dbName);
				// Connection connection = db.getConnectionPool(JDBC_DRIVER,
				// SENARY_DATABASE_DIRECTORY + dbName, USERNAME, PASSWORD).getConnection();
				PreparedStatement statement = connection.prepareStatement(selectTweets);) {
			statement.setInt(1, numberOfTweets);
			try (ResultSet resultSet = statement.executeQuery();) {
				while (resultSet.next()) {
					tweets.addTweet(convertRowToTweet(resultSet));
				}
			}
		}
		return tweets;
	}

	/** 
	 * 
	 * Get all tweets from the database wtih specific area
	 * @param String database name
	 * @param Double maximum latitude which is indicate the max latitude boundary
	 * @param Double maximum longitude which is indicat the max longitude boundary
	 * @param Double minimum latitude which is indicate the min latitude boundary
	 * @param Double minimum longitude which is indicat the min longitude boundary
	 * @return Tweets objects which is contain list of tweet
	 * @exception SQLException if there is an error in the sql
	 */
	
	public Tweets getAllTweetsByBoundary(String dbName, double maxLat, double maxLng, double minLat, double minLng)
			throws SQLException {
		Tweets tweets = new Tweets();
		String selectTweets = "SELECT * FROM tweet WHERE " + "lat <= ? and lat >= ? " + "and lon <= ? and lon >= ?";

		try (Connection connection = getDbConnection(dbName);
				// Connection connection = db.getConnectionPool(JDBC_DRIVER,
				// SENARY_DATABASE_DIRECTORY + dbName, USERNAME, PASSWORD).getConnection();
				PreparedStatement statement = connection.prepareStatement(selectTweets);) {
			statement.setDouble(1, maxLat);
			statement.setDouble(2, minLat);
			statement.setDouble(3, maxLng);
			statement.setDouble(4, minLng);
			try (ResultSet resultSet = statement.executeQuery();) {
				while (resultSet.next()) {
					tweets.addTweet(convertRowToTweet(resultSet));
				}
			}
		}

		return tweets;
	}

	public void createDefaultDb() throws SQLException {
		createDatabase(DEFAULT_DB);
	}
	
	public void createDatabase(String dbName) throws SQLException {
		System.out.println("Creating Database");
		String createTable = "CREATE TABLE tweet " + "(id INT NOT NULL AUTO_INCREMENT" + ", tweetdata VARCHAR(511)"
				+ ", date VARCHAR(255)" + ", owner VARCHAR(255)" + ", lat DOUBLE" + ", lon DOUBLE(255)" + ", km100 INT"
				+ ", km50 INT" + ", km25 INT" + ", km20 INT" + ", km10 INT" + ", km05 INT" + ", PRIMARY KEY (id))";

		// this is a try-with-resources. A resource is an object that must be closed
		// after the program is finished with it
		// this prevents memory leaks
		try (Connection connection = getDbConnection(dbName);
				// Connection connection = db.getConnectionPool(JDBC_DRIVER,
				// SENARY_DATABASE_DIRECTORY + dbName, USERNAME, PASSWORD).getConnection();
				PreparedStatement statement = connection.prepareStatement(createTable);) {
			statement.execute();
		}
	}

	public void alterDatabase(String dbName) throws SQLException {
		System.out.println("Creating Database");
		String createTable = "ALTER TABLE tweet ADD " + " (km100 INT" + ", km50 INT" + ", km25 INT" + ", km20 INT"
				+ ", km10 INT" + ", km05 INT)";

		// this is a try-with-resources. A resource is an object that must be closed
		// after the program is finished with it
		// this prevents memory leaks
		try (Connection connection = getDbConnection(dbName);
				// Connection connection = db.getConnectionPool(JDBC_DRIVER,
				// SENARY_DATABASE_DIRECTORY + dbName, USERNAME, PASSWORD).getConnection();
				PreparedStatement statement = connection.prepareStatement(createTable);) {
			statement.execute();
		}
	}

	/** 
	 * 
	 * Insert tweet into database which is comping from TwitterStreaming
	 * @param Status which which is contain the tweets information
	 * @exception SQLException if there is an error in the sql
	 */
	
	public void writeTweetInDefaultDb(Status tweet) throws SQLException {
		writeTweet(DEFAULT_DB, tweet);
	}

	
	public void writeTweet(String dbName, Status tweet) throws SQLException {
		String insertTweet = "INSERT INTO tweet (tweetdata, date, owner, lat, lon, km100, km50, km25, km20, km10, km05)"
				+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		LatAndLng lal = new LatAndLng();
		// this is a try-with-resources. A resource is an object that must be closed
		// after the program is finished with it
		// this prevents memory leaks
		// Note there is a nested try-with-resources to set the Limit parameter as you
		// cannot assign values in the try-with-resource statement
		try (Connection connection = getDbConnection(dbName);
				// Connection connection = db.getConnectionPool(JDBC_DRIVER,
				// SENARY_DATABASE_DIRECTORY + dbName, USERNAME, PASSWORD).getConnection();
				PreparedStatement statement = connection.prepareStatement(insertTweet);) {
			statement.setString(1, tweet.getText());
			DateFormat formatter = new SimpleDateFormat("dd MMMM yyyy, hh:mm:ss a");
			statement.setString(2, formatter.format(tweet.getCreatedAt()));
			statement.setString(3, tweet.getUser().getScreenName());
			statement.setDouble(4, tweet.getGeoLocation().getLatitude());
			statement.setDouble(5, tweet.getGeoLocation().getLongitude());
			lal.setLat(tweet.getGeoLocation().getLatitude());
			lal.setLng(tweet.getGeoLocation().getLatitude());
			statement.setInt(6, labelMarker.getLabelIn100KM(lal));
			statement.setInt(7, labelMarker.getLabelIn50KM(lal));
			statement.setInt(8, labelMarker.getLabelIn25KM(lal));
			statement.setInt(9, labelMarker.getLabelIn20KM(lal));
			statement.setInt(10, labelMarker.getLabelIn10KM(lal));
			statement.setInt(11, labelMarker.getLabelIn5KM(lal));
			statement.executeUpdate();
		}
	}

	/** 
	 * 
	 * Get KM+number value for every tweet.
	 * @param Integer KM number which is received from user when he click on the KM botton
	 * @exception SQLException if there is an error in the sql
	 */
	
	public List<Integer> getAllTweetsByKm(String dbName, int km) throws SQLException {
		List<Integer> numberOfTweets = new ArrayList<>();
		String kmString = "" + km;
		if (km == 5) {
			kmString = "0" + km;
		}
		String selectTweets = "SELECT km" + kmString + " FROM tweet where km" + kmString + " >= 0";
		try (Connection connection = getDbConnection(dbName);
				// Connection connection = db.getConnectionPool(JDBC_DRIVER,
				// SENARY_DATABASE_DIRECTORY + dbName, USERNAME, PASSWORD).getConnection();
				PreparedStatement statement = connection.prepareStatement(selectTweets);) {
			try (ResultSet resultSet = statement.executeQuery();) {
				while (resultSet.next()) {
					numberOfTweets.add(resultSet.getInt("km" + kmString));
				}
			}
		}
		return numberOfTweets;
	}

	
	public void updateTweet(String dbName, Tweet tweet) throws SQLException {
		String insertTweet = "update  tweet set km100 = ?, km50 = ?, km25 = ?, km20 = ?, km10 = ?, km05 = ?"
				+ " WHERE id = ?";
		LatAndLng lal = new LatAndLng();
		// this is a try-with-resources. A resource is an object that must be closed
		// after the program is finished with it
		// this prevents memory leaks
		// Note there is a nested try-with-resources to set the Limit parameter as you
		// cannot assign values in the try-with-resource statement
		try (Connection connection = getDbConnection(dbName);
				// Connection connection = db.getConnectionPool(JDBC_DRIVER,
				// SENARY_DATABASE_DIRECTORY + dbName, USERNAME, PASSWORD).getConnection();
				PreparedStatement statement = connection.prepareStatement(insertTweet);) {
			statement.setString(1, tweet.getText());
			lal.setLat(tweet.getLat());
			lal.setLng(tweet.getLng());
			statement.setInt(1, labelMarker.getLabelIn100KM(lal));
			statement.setInt(2, labelMarker.getLabelIn50KM(lal));
			statement.setInt(3, labelMarker.getLabelIn25KM(lal));
			statement.setInt(4, labelMarker.getLabelIn20KM(lal));
			statement.setInt(5, labelMarker.getLabelIn10KM(lal));
			statement.setInt(6, labelMarker.getLabelIn5KM(lal));
			statement.setInt(7, tweet.getId());
			statement.executeUpdate();
		}
	}

	private Tweet convertRowToTweet(ResultSet rs) throws SQLException {
		Tweet t = new Tweet();
		t.setId(rs.getInt("id"));
		t.setText(rs.getString("tweetdata"));
		t.setDate(rs.getString("date"));
		t.setUser(rs.getString("owner"));
		t.setLat(rs.getDouble("lat"));
		t.setLng(rs.getDouble("lon"));
		t.setKm100(rs.getInt("km100"));
		t.setKm50(rs.getInt("km50"));
		t.setKm25(rs.getInt("km25"));
		t.setKm20(rs.getInt("km20"));
		t.setKm05(rs.getInt("km05"));
		return t;
	}
}
