package ru.netology.parser;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Statistics {
    // Для задачи про 404 и браузеры (если ещё нужны)
    private final Set<String> notFoundPages = new HashSet<>();
    private final Map<String, Integer> browserCounter = new HashMap<>();

    // Новые поля для текущего задания
    private int totalVisits = 0;              // общее количество посещений от реальных пользователей
    private int errorCount = 0;               // количество ошибочных запросов (4xx/5xx)
    private final Set<String> uniqueUserIps = new HashSet<>(); // уникальные IP реальных пользователей (не ботов)

    private LocalDateTime minTime = null;
    private LocalDateTime maxTime = null;

    // Метод вызывается для каждой записи из лога
    public void addEntry(LogEntry entry) {
        // Обновляем время
        LocalDateTime time = entry.getTime();
        if (minTime == null || time.isBefore(minTime)) {
            minTime = time;
        }
        if (maxTime == null || time.isAfter(maxTime)) {
            maxTime = time;
        }

        // Обрабатываем 404 (если нужно)
        if (entry.getResponseCode() == 404) {
            notFoundPages.add(entry.getPath());
        }

        // Обрабатываем браузер (если нужно)
        String browser = entry.getUserAgent().getBrowser();
        if (browser != null && !browser.trim().isEmpty() && !browser.equals("Other")) {
            browserCounter.put(browser, browserCounter.getOrDefault(browser, 0) + 1);
        }

        // Проверяем, бот ли
        boolean isBot = entry.getUserAgent().isBot();

        // Если не бот — считаем как посещение
        if (!isBot) {
            totalVisits++;
            uniqueUserIps.add(entry.getIpAddr());
        }

        // Считаем ошибки (4xx или 5xx)
        int code = entry.getResponseCode();
        if (code >= 400 && code < 600) { // 4xx и 5xx
            errorCount++;
        }
    }

    // Возвращает множество всех несуществующих страниц (с кодом 404)
    public Set<String> getNotFoundPages() {
        return new HashSet<>(notFoundPages);
    }

    // Возвращает статистику по браузерам в виде долей (от 0.0 до 1.0)
    public Map<String, Double> getBrowserStats() {
        Map<String, Double> result = new HashMap<>();
        int total = browserCounter.values().stream().mapToInt(Integer::intValue).sum();

        if (total == 0) {
            return result;
        }

        for (Map.Entry<String, Integer> entry : browserCounter.entrySet()) {
            String browser = entry.getKey();
            int count = entry.getValue();
            double share = (double) count / total;
            result.put(browser, share);
        }

        return result;
    }

    // --- НОВЫЕ МЕТОДЫ ЗАДАНИЯ ---

    /**
     * Возвращает среднее количество посещений сайта за час (только от реальных пользователей)
     */
    public double getAverageVisitsPerHour() {
        if (minTime == null || maxTime == null || totalVisits == 0) {
            return 0.0;
        }

        long hours = ChronoUnit.HOURS.between(minTime, maxTime);
        if (hours == 0) {
            return totalVisits; // если все записи в одном часе
        }

        return (double) totalVisits / hours;
    }

    /**
     * Возвращает среднее количество ошибочных запросов в час
     */
    public double getAverageErrorsPerHour() {
        if (minTime == null || maxTime == null || errorCount == 0) {
            return 0.0;
        }

        long hours = ChronoUnit.HOURS.between(minTime, maxTime);
        if (hours == 0) {
            return errorCount; // если все ошибки в одном часе
        }

        return (double) errorCount / hours;
    }

    /**
     * Возвращает среднюю посещаемость одним пользователем (по уникальному IP, не ботам)
     */
    public double getAverageVisitsPerUser() {
        if (uniqueUserIps.isEmpty()) {
            return 0.0;
        }
        return (double) totalVisits / uniqueUserIps.size();
    }
}