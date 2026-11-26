import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CarShowroomSystem system = new CarShowroomSystem(scanner);
        system.start();
        scanner.close();
    }
}
