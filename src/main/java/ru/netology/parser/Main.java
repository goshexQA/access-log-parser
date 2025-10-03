package ru.netology.parser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    // Собственное исключение
    public static class LineTooLongException extends RuntimeException {
        public LineTooLongException(String message) {
            super(message);
        }
    }

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

            // Инициализация переменных для статистики
            int totalLines = 0;
            int maxLength = 0;
            int minLength = Integer.MAX_VALUE;

            // Чтение файла построчно
            try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    totalLines++;
                    int length = line.length();

                    // Проверка длины строки
                    if (length > 1024) {
                        throw new LineTooLongException(
                                "Найдена строка длиной " + length + " символов. Максимальная допустимая длина — 1024."
                        );
                    }

                    // Обновление статистики
                    if (length > maxLength) {
                        maxLength = length;
                    }
                    if (length < minLength) {
                        minLength = length;
                    }
                }
            }

            // Вывод результатов
            System.out.println("Общее количество строк: " + totalLines);
            System.out.println("Длина самой длинной строки: " + maxLength);
            System.out.println("Длина самой короткой строки: " + minLength);

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
}