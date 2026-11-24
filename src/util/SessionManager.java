package util;

import java.util.HashMap;

public class SessionManager {
    private static HashMap<String, Object> sessionData = new HashMap<>();
    private static HashMap<String, Object> configData = new HashMap<>();

    static {
        configData.put("fineRate", 5.0);
        configData.put("maxBorrowLimit", 3);
    }

    public static void setSession(String key, Object value) {
        sessionData.put(key, value);
    }

    public static Object getSession(String key) {
        return sessionData.get(key);
    }

    public static void clearSession() {
        sessionData.clear();
    }

    public static Object getConfig(String key) {
        return configData.get(key);
    }
}
