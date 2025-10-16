package ru.netology.parser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

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
        Statistics stats = new Statistics();

        try {
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath)) {
                throw new FileNotFoundException("Файл не найден: " + path);
            }
            if (!Files.isRegularFile(filePath)) {
                throw new IOException("Указанный путь не является файлом: " + path);
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.length() > 1024) {
                        throw new LineTooLongException(
                                "Найдена строка длиной " + line.length() + " символов. Максимальная допустимая длина — 1024."
                        );
                    }

                    LogEntry entry = new LogEntry(line);
                    stats.addEntry(entry);
                }
            }

            //System.out.printf("Средний трафик в час: %.2f байт\n", stats.getTrafficRate());

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