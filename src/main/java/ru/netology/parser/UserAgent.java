package ru.netology.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserAgent {
    private final String userAgentString; // ← сохраняем исходную строку
    private final String os;
    private final String browser;

    public UserAgent(String userAgentString) {
        this.userAgentString = userAgentString; // ← сохранили
        this.os = extractOS(userAgentString);
        this.browser = extractBrowser(userAgentString);
    }

    private String extractOS(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) return "Unknown";

        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Mac OS X")) return "macOS";
        if (userAgent.contains("Linux")) return "Linux";
        return "Unknown";
    }

    private String extractBrowser(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) return "Unknown";

        if (userAgent.contains("Edge")) return "Edge";
        if (userAgent.contains("Firefox")) return "Firefox";
        if (userAgent.contains("Chrome")) return "Chrome";
        if (userAgent.contains("Opera")) return "Opera";
        return "Other";
    }

    public String getOs() {
        return os;
    }

    public String getBrowser() {
        return browser;
    }

    // Новый метод для определения бота
    public boolean isBot() {
        if (userAgentString == null || userAgentString.isEmpty()) {
            return false;
        }
        return userAgentString.toLowerCase().contains("bot");
    }
}