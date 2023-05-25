import java.util.ArrayList;
import java.util.Scanner;

public class Starter {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AirportManager airportManager = new AirportManager(new ArrayList<>(), new ArrayList<>());

        while (true) {
            System.out.println("1. Enter Flight Data Manually");
            System.out.println("2. Save Data to File");
            System.out.println("3. Exit");

            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    airportManager.enterFlightDataManually();
                    break;
                case 2:
                    System.out.println("Enter filename:");
                    String filename = scanner.next();
                    airportManager.saveDataToFile("flights.csv", "lanes.csv");
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid option. Please enter 1, 2, or 3.");
            }
        }
    }
}
