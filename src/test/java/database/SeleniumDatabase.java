package database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SeleniumDatabase {

	private static Connection con = null;

	private static Statement stmt;

	private static final String DBURL = "jdbc:mysql://url.to.connect/db.to" +
			".connect?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	private static String dbUser = null;
	private static String dbPassword = null;

	private static final Logger LOGGER = Logger.getLogger(SeleniumDatabase.class.getName());


	public static void setUp() {
		try{
			setLog();
			getData();

			Class.forName("com.mysql.cj.jdbc.Driver");

			con = DriverManager.getConnection(DBURL, dbUser, dbPassword);

			stmt = con.createStatement();
		}
		catch (Exception e)
		{
			LOGGER.warning(e.getMessage());
		}
	}


	private static void setLog() throws IOException {
		FileHandler fileTxt = new FileHandler("/logs/logger_DB.log", 2000, 5);
		LOGGER.setLevel(Level.WARNING);
		SimpleFormatter formatterTxt = new SimpleFormatter();
		fileTxt.setFormatter(formatterTxt);
		LOGGER.addHandler(fileTxt);
	}

	public ResultSet doQuery(String query) throws SQLException {
		return stmt.executeQuery(query);
	}

	public void close() {
		if (con != null) {
			try {
				stmt.close();
				con.close();
			} catch (SQLException e) {
				LOGGER.warning(e.getMessage());
			}
		}
	}

	private static void getData()
	{
		try {
			Scanner content = new Scanner(new File("credentials.csv"));
			while (content.hasNextLine())
			{
				String line = content.nextLine();
				String[] sArray = line.split(",");

				dbUser = sArray[0];
				dbPassword = sArray[1];
			}
			content.close();

		} catch (FileNotFoundException e) {
			LOGGER.warning(e.getMessage());
		}
	}
}
