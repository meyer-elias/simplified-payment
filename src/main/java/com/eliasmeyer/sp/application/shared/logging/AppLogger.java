package com.eliasmeyer.sp.application.shared.logging;

public interface AppLogger {

    void debug(String message, Object... args);

    void info(String message, Object... args);

    void warn(String message, Object... args);

    void error(String message, Throwable throwable, Object... args);

}
