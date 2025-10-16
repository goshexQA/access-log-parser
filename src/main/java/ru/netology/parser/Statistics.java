package ru.netology.parser;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Statistics {
    // Для задачи про 404 и браузеры (если нужны)
    private final Set<String> notFoundPages = new HashSet<>();
    private final Map<String, Integer> browserCounter = new HashMap<>();

    // Новые поля для текущего задания
    private int totalVisits = 0;
    private int errorCount = 0;
    private final Set<String> uniqueUserIps = new HashSet<>();
    private final Map<String, Integer> userVisitCount = new HashMap<>(); // IP -> количество посещений
    private final Map<LocalDateTime, Integer> visitsPerSecond = new HashMap<>(); // секунда -> количество посещений
    private final Set<String> referrerDomains = new HashSet<>();

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

        // Обрабатываем 404
        if (entry.getResponseCode() == 404) {
            notFoundPages.add(entry.getPath());
        }

        // Обрабатываем браузер
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

            // Считаем посещения по IP
            String ip = entry.getIpAddr();
            userVisitCount.put(ip, userVisitCount.getOrDefault(ip, 0) + 1);

            // Считаем посещения по секунде
            LocalDateTime second = time.truncatedTo(ChronoUnit.SECONDS);
            visitsPerSecond.merge(second, 1, Integer::sum);
        }

        // Считаем ошибки (4xx или 5xx)
        int code = entry.getResponseCode();
        if (code >= 400 && code < 600) {
            errorCount++;
        }

        // Обрабатываем referer
        String referer = entry.getReferer();
        if (referer != null && !referer.trim().isEmpty() && !referer.equals("-")) {
            String domain = extractDomain(referer);
            if (domain != null) {
                referrerDomains.add(domain);
            }
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
     * Возвращает пиковую посещаемость сайта в секунду (только от реальных пользователей)
     */
    public int getPeakVisitsPerSecond() {
        if (visitsPerSecond.isEmpty()) {
            return 0;
        }
        return Collections.max(visitsPerSecond.values());
    }

    /**
     * Возвращает список сайтов, со страниц которых есть ссылки на текущий сайт (домены из referer)
     */
    public Set<String> getReferrerSites() {
        return new HashSet<>(referrerDomains);
    }

    /**
     * Возвращает максимальное количество посещений одним пользователем (по уникальному IP, не ботам)
     */
    public int getMaxVisitsPerUser() {
        if (userVisitCount.isEmpty()) {
            return 0;
        }
        return Collections.max(userVisitCount.values());
    }

    // Вспомогательный метод для извлечения домена из URL
    private String extractDomain(String url) {
        try {
            // Убираем протокол (http://, https://)
            if (url.startsWith("http://")) {
                url = url.substring(7);
            } else if (url.startsWith("https://")) {
                url = url.substring(8);
            }

            // Находим первый слэш или вопросительный знак (начало пути/параметров)
            int end = url.indexOf('/');
            if (end == -1) {
                end = url.indexOf('?');
            }
            if (end == -1) {
                end = url.length();
            }

            // Берём только домен
            String domain = url.substring(0, end);

            // Убираем порт, если есть
            int portIndex = domain.indexOf(':');
            if (portIndex != -1) {
                domain = domain.substring(0, portIndex);
            }

            return domain;
        } catch (Exception e) {
            return null;
        }
    }

    // --- ДОПОЛНИТЕЛЬНЫЕ МЕТОДЫ ИЗ ПРЕДЫДУЩИХ ЗАДАНИЙ ---

    public double getAverageVisitsPerHour() {
        if (minTime == null || maxTime == null || totalVisits == 0) {
            return 0.0;
        }

        long hours = ChronoUnit.HOURS.between(minTime, maxTime);
        if (hours == 0) {
            return totalVisits;
        }

        return (double) totalVisits / hours;
    }

    public double getAverageErrorsPerHour() {
        if (minTime == null || maxTime == null || errorCount == 0) {
            return 0.0;
        }

        long hours = ChronoUnit.HOURS.between(minTime, maxTime);
        if (hours == 0) {
            return errorCount;
        }

        return (double) errorCount / hours;
    }

    public double getAverageVisitsPerUser() {
        if (uniqueUserIps.isEmpty()) {
            return 0.0;
        }
        return (double) totalVisits / uniqueUserIps.size();
    }
}