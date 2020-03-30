package com.valkryst.Emberstone.log;

public class Logger {
    private final String className;

    public Logger(final Class c) {
        className = c.getSimpleName();
    }

    private void log(final String level, final String message) {
        System.out.println("[" + className + "][" + level + "]" + message);
    }

    public void trace(final String message) {
        log("TRACE", message);
    }

    public void debug(final String message) {
        log("DEBUG", message);
    }

    public void info(final String message) {
        log("INFO", message);
    }

    public void warning(final String message) {
        log("WARNING", message);
    }

    public void error(final String message) {
        log("ERROR", message);
    }

    public void fatal(final String message) {
        log("FATAL", message);
    }
}
