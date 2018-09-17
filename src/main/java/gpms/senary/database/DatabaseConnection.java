package gpms.senary.database;

import org.h2.jdbcx.JdbcConnectionPool;

public class DatabaseConnection {
	
	protected JdbcConnectionPool getConnectionPool(String driver, String url, String username, String password) {
		JdbcConnectionPool cp = null;
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		cp = JdbcConnectionPool.create(url, username, password);
		return cp;
	}
	
}
