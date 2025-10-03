package ru.netology.parser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    // Собственное исключение
    public static class LineTooLongException extends RuntimeException {
        public LineTooLongException(String message) {
            super(message);
        }
    }

    // Регулярное выражение для Apache Combined Log Format
    private static final Pattern LOG_PATTERN = Pattern.compile(
            "^([\\d\\.]+) \\S+ \\S+ \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(\\w+) (\\S+)\\s\\S+\" (\\d{3}) (\\d+|-) \"([^\"]*)\" \"([^\"]*)\""
    );

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Укажите путь к файлу логов как аргумент командной строки.");
            return;
        }

        String path = args[0];

        try {
            // Проверка: существует ли файл и является ли он файлом (не папкой)
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath)) {
                throw new FileNotFoundException("Файл не найден: " + path);
            }
            if (!Files.isRegularFile(filePath)) {
                throw new IOException("Указанный путь не является файлом: " + path);
            }

            int totalRequests = 0;
            int googlebotCount = 0;
            int yandexbotCount = 0;

            // Чтение файла построчно
            try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    totalRequests++;
                    int length = line.length();

                    // Проверка длины строки
                    if (length > 1024) {
                        throw new LineTooLongException(
                                "Найдена строка длиной " + length + " символов. Максимальная допустимая длина — 1024."
                        );
                    }

                    // Парсим строку
                    Matcher matcher = LOG_PATTERN.matcher(line);
                    if (matcher.matches()) {
                        String userAgent = matcher.group(8); // User-Agent

                        // Обработка User-Agent
                        String botName = extractBotName(userAgent);
                        if ("Googlebot".equals(botName)) {
                            googlebotCount++;
                        } else if ("YandexBot".equals(botName)) {
                            yandexbotCount++;
                        }
                    }
                }
            }

            // Вывод результатов
            double googlebotRatio = totalRequests > 0 ? (double) googlebotCount / totalRequests : 0;
            double yandexbotRatio = totalRequests > 0 ? (double) yandexbotCount / totalRequests : 0;

            System.out.printf("Доля запросов от Googlebot: %.2f%%\n", googlebotRatio * 100);
            System.out.printf("Доля запросов от YandexBot: %.2f%%\n", yandexbotRatio * 100);

        } catch (FileNotFoundException e) {
            System.err.println("Ошибка: Файл не найден — " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Ошибка ввода-вывода — " + e.getMessage());
        } catch (LineTooLongException e) {
            System.err.println("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Неизвестная ошибка — " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Метод для извлечения имени бота из User-Agent
    private static String extractBotName(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return null;
        }

        // Находим первую пару скобок
        int start = userAgent.indexOf('(');
        int end = userAgent.indexOf(')', start);

        if (start == -1 || end == -1) {
            return null;
        }

        String firstBrackets = userAgent.substring(start + 1, end);

        //Разделяем по точке с запятой
        String[] parts = firstBrackets.split(";");
        if (parts.length < 2) {
            return null;
        }

        // Берём второй фрагмент, очищаем от пробелов
        String fragment = parts[1].trim();

        // Отделяем часть до слэша
        int slashIndex = fragment.indexOf('/');
        if (slashIndex == -1) {
            return fragment;
        }

        return fragment.substring(0, slashIndex);
    }
}