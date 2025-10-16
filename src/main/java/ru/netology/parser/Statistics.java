package ru.netology.parser;

import java.util.*;

public class Statistics {
    // Хранит уникальные URL-адреса страниц с кодом ответа 404 (несуществующие)
    private final Set<String> notFoundPages = new HashSet<>();

    // Считает, сколько раз встречался каждый браузер
    private final Map<String, Integer> browserCounter = new HashMap<>();

    // Метод вызывается для каждой записи из лога
    public void addEntry(LogEntry entry) {
        // Добавляем страницу, только если статус 404
        if (entry.getResponseCode() == 404) {
            notFoundPages.add(entry.getPath());
        }

        // Обрабатываем браузер
        String browser = entry.getUserAgent().getBrowser(); // ← получаем через UserAgent!
        if (browser != null && !browser.trim().isEmpty() && !browser.equals("Other")) {
            browserCounter.put(browser, browserCounter.getOrDefault(browser, 0) + 1);
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
            return result; // пустой мап, если нет данных
        }

        for (Map.Entry<String, Integer> entry : browserCounter.entrySet()) {
            String browser = entry.getKey();
            int count = entry.getValue();
            double share = (double) count / total;
            result.put(browser, share);
        }

        return result;
    }
}