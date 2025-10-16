package ru.netology.parser;

import java.util.*;

public class Statistics {
    // Хранит уникальные URL-адреса страниц с кодом ответа 200
    private final Set<String> pages = new HashSet<>();

    // Считает, сколько раз встречалась каждая операционная система
    private final Map<String, Integer> osCounter = new HashMap<>();

    // Метод вызывается для каждой записи из лога
    public void addEntry(LogEntry entry) { // ← принимаем LogEntry, а не AccessLogEntry!
        // Добавляем страницу, только если статус 200
        if (entry.getResponseCode() == 200) {
            pages.add(entry.getPath()); // ← getPath(), а не getUrl()
        }

        // Обрабатываем операционную систему
        String os = entry.getUserAgent().getOs(); // ← через UserAgent!
        if (os != null && !os.trim().isEmpty() && !os.equals("Unknown")) {
            osCounter.put(os, osCounter.getOrDefault(os, 0) + 1);
        }
    }

    // Возвращает множество всех существующих (доступных) страниц
    public Set<String> getPages() {
        return new HashSet<>(pages);
    }

    // Возвращает статистику по ОС в виде долей (от 0.0 до 1.0)
    public Map<String, Double> getOsStats() {
        Map<String, Double> result = new HashMap<>();
        int total = osCounter.values().stream().mapToInt(Integer::intValue).sum();

        if (total == 0) {
            return result;
        }

        for (Map.Entry<String, Integer> entry : osCounter.entrySet()) {
            String os = entry.getKey();
            int count = entry.getValue();
            double share = (double) count / total;
            result.put(os, share);
        }

        return result;
    }
}