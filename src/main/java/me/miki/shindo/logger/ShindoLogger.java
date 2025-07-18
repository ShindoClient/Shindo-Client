package me.miki.shindo.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShindoLogger {
	
    public static final Logger logger = LogManager.getLogger("Shindo Client");
    
    public static void info(String message) {
    	logger.info("[SC/INFO] " + message);
    }
    
    public static void warn(String message) {
    	logger.warn("[SC/WARN] " + message);
    }

    public static void error(String message) {
    	logger.error("[SC/ERROR] " + message);
    }
    
    public static void error(String message, Exception e) {
    	logger.error("[SC/ERROR] " + message, e);
    }

	public static Logger getLogger() {
		return logger;
	}
}
