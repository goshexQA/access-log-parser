package ru.netology.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogEntry {
    private final String ipAddr;
    private final LocalDateTime time;
    private final HttpMethod method;
    private final String path;
    private final int responseCode;
    private final int responseSize;
    private final String referer;
    private final UserAgent userAgent;

    // Формат даты в логах Apache
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z");

    private static final Pattern LOG_PATTERN = Pattern.compile(
            "^([\\d\\.]+) \\S+ \\S+ \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(\\w+) (\\S+)\\s\\S+\" (\\d{3}) (\\d+|-) \"([^\"]*)\" \"([^\"]*)\""
    );

    public LogEntry(String line) {
        Matcher matcher = LOG_PATTERN.matcher(line);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Неверный формат строки лога: " + line);
        }

        this.ipAddr = matcher.group(1);
        this.time = LocalDateTime.parse(matcher.group(2), DATE_FORMAT);
        this.method = HttpMethod.valueOf(matcher.group(3));
        this.path = matcher.group(4);
        this.responseCode = Integer.parseInt(matcher.group(5));
        this.responseSize = "-".equals(matcher.group(6)) ? 0 : Integer.parseInt(matcher.group(6));
        this.referer = matcher.group(7);
        this.userAgent = new UserAgent(matcher.group(8));
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getResponseSize() {
        return responseSize;
    }

    public String getReferer() {
        return referer;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }
}