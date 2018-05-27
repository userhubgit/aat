package fr.cnam.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DataBaseDump {

	
	private static final Logger logger = LoggerFactory.getLogger(DataBaseDump.class);
	
	/** Dump the whole database to an SQL string */
	public String dumpDB(StringBuffer result) {
		
		String driverClassName = "org.postgresql.Driver";
		String url = "jdbc:postgresql://" + System.getenv("POSTGRESQL_SERVICE_HOST") + ":"
				+ System.getenv("POSTGRESQL_SERVICE_PORT_POSTGRESQL") + "/sampledb";
		logger.info("-----------------URL---------------- {}", url);
		logger.info("-----------------URL---------------- {}", url);
		logger.info("-----------------URL---------------- {}", url);
//		String url = "jdbc:postgresql://localhost:5432/sampledb";
		String columnNameQuote = "";
		DatabaseMetaData dbMetaData = null;
		Connection dbConn = null;
		try {
			Class.forName(driverClassName);
			dbConn = DriverManager.getConnection(url, "user83m", "WD1VOhUtrtvikiPU");
			dbMetaData = dbConn.getMetaData();
		} catch (Exception e) {
			logger.error("Impossible de se connecter à la base: ",e);
			return null;
		}

		try {

			String catalog = "database";
			String schema = "public";
			String tables = null;

			ResultSet rs = dbMetaData.getTables(null, null, tables, null);
			if (!rs.next()) {
				logger.error("Unable to find any tables matching: catalog=" + catalog + " schema=" + schema
						+ " tables=" + tables);
				rs.close();
			} else {
				do {
					String tableName = rs.getString("TABLE_NAME");
					String tableType = rs.getString("TABLE_TYPE");
					if ("TABLE".equalsIgnoreCase(tableType)) {
						result.append("\n\n-- " + tableName);
						result.append("\nCREATE TABLE " + tableName + " (\n");
						ResultSet tableMetaData = dbMetaData.getColumns(null, null, tableName, "%");
						boolean firstLine = true;
						while (tableMetaData.next()) {
							if (firstLine) {
								firstLine = false;
							} else {
								
								result.append(",\n");
							}
							String columnName = tableMetaData.getString("COLUMN_NAME");
							String columnType = tableMetaData.getString("TYPE_NAME");
							// WARNING: this may give daft answers for some
							// types on some databases (eg JDBC-ODBC link)
							int columnSize = tableMetaData.getInt("COLUMN_SIZE");
							String nullable = tableMetaData.getString("IS_NULLABLE");
							String nullString = "NULL";
							if ("NO".equalsIgnoreCase(nullable)) {
								nullString = "NOT NULL";
							}
							result.append("    " + columnNameQuote + columnName + columnNameQuote + " " + columnType
									+ " (" + columnSize + ")" + " " + nullString);
						}
						tableMetaData.close();

						// Now we need to put the primary key constraint
						try {
							
							ResultSet primaryKeys = dbMetaData.getPrimaryKeys(catalog, schema, tableName);
							
							String primaryKeyName = null;
							StringBuffer primaryKeyColumns = new StringBuffer();
							while (primaryKeys.next()) {
								String thisKeyName = primaryKeys.getString("PK_NAME");
								if ((thisKeyName != null && primaryKeyName == null)
										|| (thisKeyName == null && primaryKeyName != null)
										|| (thisKeyName != null && !thisKeyName.equals(primaryKeyName))
										|| (primaryKeyName != null && !primaryKeyName.equals(thisKeyName))) {
									// the keynames aren't the same, so output
									// all that we have so far (if anything)
									// and start a new primary key entry
									if (primaryKeyColumns.length() > 0) {
										// There's something to output
										result.append(",\n    PRIMARY KEY ");
										if (primaryKeyName != null) {
											result.append(primaryKeyName);
										}
										result.append("(" + primaryKeyColumns.toString() + ")");
									}
									// Start again with the new name
									primaryKeyColumns = new StringBuffer();
									primaryKeyName = thisKeyName;
								}
								// Now append the column
								if (primaryKeyColumns.length() > 0) {
									primaryKeyColumns.append(", ");
								}
								primaryKeyColumns.append(primaryKeys.getString("COLUMN_NAME"));
							}
							if (primaryKeyColumns.length() > 0) {
								// There's something to output
								result.append(",\n    PRIMARY KEY ");
								if (primaryKeyName != null) {
									result.append(primaryKeyName);
								}
								result.append(" (" + primaryKeyColumns.toString() + ")");
							}
						} catch (SQLException e) {
							// NB you will get this exception with the JDBC-ODBC
							// link because it says
							// [Microsoft][ODBC Driver Manager] Driver does not
							// support this function
							logger.error("Impossible de trouver la cle primaire de la table " + tableName + " cause", e);
						}

						result.append("\n);\n");

						// Right, we have a table, so we can go and dump it
						dumpTable(dbConn, result, tableName);
					}
				} while (rs.next());
				rs.close();
			}
			dbConn.close();
			return result.toString();
		} catch (SQLException e) {
			e.printStackTrace(); // To change body of catch statement use
		}
		return null;
	}

	/** dump this particular table to the string buffer */
	private static void dumpTable(Connection dbConn, StringBuffer result, String tableName) {
		try {
			// First we output the create table stuff
			PreparedStatement stmt = dbConn.prepareStatement("SELECT * FROM " + tableName);
			ResultSet rs = stmt.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();

			// Now we can output the actual data
			result.append("\n\n-- Data for " + tableName + "\n");
			while (rs.next()) {
				result.append("INSERT INTO " + tableName + " VALUES (");
				for (int i = 0; i < columnCount; i++) {
					if (i > 0) {
						result.append(", ");
					}
					Object value = rs.getObject(i + 1);
					if (value == null) {
						result.append("NULL");
					} else {
						String outputValue = value.toString();
						outputValue = outputValue.replaceAll("'", "\\'");
						result.append("'" + outputValue + "'");
					}
				}
				result.append(");\n");
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.error("Dump impossible pour la table " + tableName + " cause: ", e);
		}
	}

	public File createDumpFile(StringBuffer input) throws IOException {

		File dumpFile = File.createTempFile("aatDump", "txt");
		BufferedWriter bwr = new BufferedWriter(new FileWriter(dumpFile));

		bwr.write(input.toString());

		bwr.flush();

		bwr.close();

		logger.info("Dump de la base avec reussi.");
		return dumpFile;
	}
}
