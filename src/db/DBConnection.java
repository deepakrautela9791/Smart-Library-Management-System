package db;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static File findPropertiesFile() {
        // (a) Check current working directory
        File file = new File("db.properties");
        if (file.exists()) {
            return file;
        }

        // (b) Check folder containing the running .jar / .exe
        try {
            File codeLocation = new File(
                DBConnection.class.getProtectionDomain().getCodeSource().getLocation().toURI()
            );
            File jarDir = codeLocation.getParentFile();
            if (jarDir != null) {
                File inJarDir = new File(jarDir, "db.properties");
                if (inJarDir.exists()) {
                    return inJarDir;
                }
                // jpackage places jars inside app/ next to SmartLibrary.exe
                File appParentDir = jarDir.getParentFile();
                if (appParentDir != null) {
                    File inAppParent = new File(appParentDir, "db.properties");
                    if (inAppParent.exists()) {
                        return inAppParent;
                    }
                }
            }
        } catch (Exception e) {
            // Ignore path lookup errors and fall through
        }

        return null;
    }

    public static Connection getConnection() throws SQLException {
        File propsFile = findPropertiesFile();
        if (propsFile == null) {
            JOptionPane.showMessageDialog(
                null,
                "Database configuration file (db.properties) is missing!\n"
                    + "Please copy db.properties.example to db.properties and fill in your database details.",
                "Configuration Missing",
                JOptionPane.ERROR_MESSAGE
            );
            return null;
        }

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(propsFile)) {
            props.load(fis);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                null,
                "Could not read db.properties: " + e.getMessage(),
                "Configuration Error",
                JOptionPane.ERROR_MESSAGE
            );
            return null;
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(
                null,
                "MySQL JDBC Driver not found. Please check that mysql-connector-j is on the classpath.",
                "Driver Error",
                JOptionPane.ERROR_MESSAGE
            );
            return null;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                null,
                "Cannot connect to the database. Please check your internet connection and db.properties settings.\n"
                    + "Details: " + e.getMessage(),
                "Database Connection Failed",
                JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
    }
}
