import com.opencsv.CSVWriter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


@NoArgsConstructor
@Data
public class AirportManager {

    public AirportManager(List<Flight> flights, List<Lane> lanes) {
        this.flights = flights;
        this.lanes = lanes;
    }

    private Scanner scanner = new Scanner(System.in);
    private List<Flight> flights;
    private List<Lane> lanes;

    public void addLane(Lane lane) {
        lanes.add(lane);
    }

    public void addFlight(Flight flight) {
        if (!isFlightAlreadyExisted(flight)) {
            flights.add(flight);
        } else System.out.println("This flight number already exist!");
    }

    public void deleteFlight(Flight flight) {
        flights.remove(flight);
    }

    //    public boolean isLaneCollision(Flight firstFlight, Flight secondFlight) {
//        return firstFlight.getStartingLane().equals(secondFlight.getStartingLane()) ||
//                firstFlight.getLandingLane().equals(secondFlight.getLandingLane());
//
//    }
    public boolean isLaneOccupied(Lane lane, Date start, Date end) {
        return flights.stream().anyMatch(flight -> (flight.getStartingLane().equals(lane) || flight.getLandingLane().equals(lane)) &&
                (flight.getStartingTime().before(end) && flight.getLandingTime().after(start)));
    }

    public boolean isFlightAlreadyExisted(Flight flight) {
        return flights.contains(flight);
    }

    public String changeStateOfFlight(Flight flight, StateOfFlight stateOfFlight) {
        if ((flight.getStateOfFlight().equals(StateOfFlight.PLANNED) && (stateOfFlight.equals(StateOfFlight.READY) ||
                (stateOfFlight.equals(StateOfFlight.CANCELLED))) ||
                (flight.getStateOfFlight().equals(StateOfFlight.READY) && stateOfFlight.equals(StateOfFlight.IN_FLIGHT) ||
                        stateOfFlight.equals(StateOfFlight.CANCELLED)) ||
                (flight.getStateOfFlight().equals(StateOfFlight.IN_FLIGHT) && stateOfFlight.equals(StateOfFlight.FINISHED)) ||
                (flight.getStateOfFlight().equals(StateOfFlight.CANCELLED) && stateOfFlight.equals(StateOfFlight.PLANNED))
        )) {
            flight.setStateOfFlight(stateOfFlight);
            return "State of flight was changed successful";
        } else
            return "The new state cannot be applied to the previous state";
    }

    public List<Flight> getAllFlightsUsingLane(Lane lane) {
        return flights.stream()
                .filter(flight -> flight.getStartingLane().equals(lane) || flight.getLandingLane().equals(lane))
                .sorted(Comparator.comparing(flight -> flight.getStateOfFlight().toString()))
                .collect(Collectors.toList());
    }

    public List<Flight> getAllFlightsInAirport(String airportName) {
        return flights.stream()
                .filter(flight -> flight.getStartingLane().getAirportName().equals(airportName) ||
                        flight.getLandingLane().getAirportName().equals(airportName))
                .collect(Collectors.toList());
    }

    public List<Flight> getAllFlightsInState(StateOfFlight stateOfFlight) {
        return flights.stream()
                .filter(flight -> flight.getStateOfFlight().equals(stateOfFlight))
                .sorted(Comparator.comparing(Flight::getFlightNumber))
                .collect(Collectors.toList());
    }
    private Lane findLaneByNumber(int laneNumber) {
        for (Lane lane : lanes) {
            if (lane.getLaneNr() == laneNumber) {
                return lane;
            }
        }
        return null;
    }
    private Flight findFlightByNumber(int flightNumber) {
        for (Flight flight : flights) {
            if (flight.getFlightNumber() == flightNumber) {
                return flight;
            }
        }
        return null;
    }

    public void exit() {
        System.exit(0);
    }

    public void enterFlightDataManually() {
        System.out.println("Enter flight number:");
        int flightNumber = scanner.nextInt();

        if (findFlightByNumber(flightNumber) != null) {
            System.out.println("This flight number already exists!");
            return;
        }

        System.out.println("Enter airport name for the starting lane:");
        String startingAirportName = scanner.next();

        System.out.println("Enter lane number for the starting lane:");
        int startingLaneNumber = scanner.nextInt();

        System.out.println("Enter lane length for the starting lane:");
        double startingLaneLength = scanner.nextDouble();

        Lane startingLane = findLaneByNumber(startingLaneNumber);
        if (startingLane == null) {
            startingLane = new Lane(startingAirportName, startingLaneNumber, startingLaneLength);
            addLane(startingLane);
        }

        System.out.println("Enter airport name for the landing lane:");
        String landingAirportName = scanner.next();

        System.out.println("Enter lane number for the landing lane:");
        int landingLaneNumber = scanner.nextInt();

        System.out.println("Enter lane length for the landing lane:");
        double landingLaneLength = scanner.nextDouble();

        Lane landingLane = findLaneByNumber(landingLaneNumber);
        if (landingLane == null) {
            landingLane = new Lane(landingAirportName, landingLaneNumber, landingLaneLength);
            addLane(landingLane);
        }

        Date startingTime = null;
        Date landingTime = null;
        try {
            System.out.println("Enter starting time in format 'yyyy-MM-dd HH:mm:ss':");
            String startingTimeStr = scanner.next();
            startingTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startingTimeStr);

            System.out.println("Enter landing time in format 'yyyy-MM-dd HH:mm:ss':");
            String landingTimeStr = scanner.next();
            landingTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(landingTimeStr);
        } catch (ParseException e) {
            System.out.println("Invalid date format, please try again.");
            return;
        }

        System.out.println("Enter initial flight state (PLANNED, READY, IN_FLIGHT, FINISHED, CANCELLED):");
        String stateOfFlightStr = scanner.next();
        StateOfFlight stateOfFlight = StateOfFlight.valueOf(stateOfFlightStr);

        Flight flight = new Flight(flightNumber, startingLane, landingLane, startingTime, landingTime, stateOfFlight);

        if (isLaneOccupied(startingLane, startingTime, landingTime) || isLaneOccupied(landingLane, startingTime, landingTime)) {
            System.out.println("There is a lane collision for the specified time range!");
            return;
        }

        addFlight(flight);
    }

    public void saveDataToFile(String flightFileName, String laneFileName) {
        saveFlightsToFile(flightFileName);
        saveLanesToFile(laneFileName);
    }

    private void saveFlightsToFile(String fileName) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            String[] header = { "Flight Number", "Starting Airstrip", "Landing Airstrip", "Starting Time", "Landing Time", "Flight State"};
            writer.writeNext(header);

            for (Flight flight : flights) {
                String[] flightData = {
                        String.valueOf(flight.getFlightNumber()),
                        String.valueOf(flight.getStartingLane().getLaneNr()),
                        String.valueOf(flight.getLandingLane().getLaneNr()),
                        flight.getStartingTime().toString(),
                        flight.getLandingTime().toString(),
                        flight.getStateOfFlight().toString()
                };
                writer.writeNext(flightData);
            }
        } catch (IOException e) {
            System.out.println("Failed to save flight data to file.");
            e.printStackTrace();
        }
    }

    private void saveLanesToFile(String fileName) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            String[] header = { "Lane Number", "Airport Name", "Length"};
            writer.writeNext(header);

            for (Lane lane : lanes) {
                String[] laneData = {
                        String.valueOf(lane.getLaneNr()),
                        lane.getAirportName(),
                        String.valueOf(lane.getLaneLength())
                };
                writer.writeNext(laneData);
            }
        } catch (IOException e) {
            System.out.println("Failed to save lane data to file.");
            e.printStackTrace();
        }
    }
}
