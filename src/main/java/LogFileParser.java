import java.io.File;
import java.util.Scanner;

public class LogFileParser {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int correctFileCount = 0;

        System.out.println("Программа для проверки путей к файлам.");
        System.out.println("Для выхода введите 'exit' или 'quit'.");

        while (true) {
            System.out.print("Введите путь к файлу: ");
            String filePath = scanner.nextLine();

            // Проверка на команду выхода
            if (filePath.equalsIgnoreCase("exit") || filePath.equalsIgnoreCase("quit")) {
                System.out.println("Завершение программы. Всего проверено файлов: " + correctFileCount);
                break;
            }

            // Проверка на пустой ввод
            if (filePath.trim().isEmpty()) {
                System.out.println("Путь не может быть пустым.");
                continue;
            }

            File file = new File(filePath);
            boolean fileExists = file.exists();
            boolean isFile = file.isFile();

            if (!fileExists) {
                System.out.println("Ошибка: Файл не существует - " + filePath);
                continue;
            }

            if (!isFile) {
                System.out.println("Ошибка: Указанный путь ведет к папке - " + filePath);
                continue;
            }

            // Если дошли до here, файл корректен
            correctFileCount++;
            System.out.println("✓ Путь указан верно");
            System.out.println("✓ Это файл номер " + correctFileCount);
            System.out.println("Размер файла: " + file.length() + " байт");
        }

        scanner.close();
    }
}