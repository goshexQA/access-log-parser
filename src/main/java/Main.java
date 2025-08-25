import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Введите текст и нажмите <Enter>: ");
            String inputText = scanner.nextLine();
            System.out.println("Длина введённого текста: " + inputText.length() + " символов.");

        }
    }
}
