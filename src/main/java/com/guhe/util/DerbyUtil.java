package com.guhe.util;

import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DerbyUtil {

	public static void closeEmbeddedDatebase(String dbName) {
		try {
			DriverManager.getConnection("jdbc:derby:" + dbName + ";shutdown=true");
		} catch (SQLException e) {
			if (e.getErrorCode() != 45000 || !e.getSQLState().equals("08006")) {
				throw new RuntimeException(e);
			}
		}
	}
}
