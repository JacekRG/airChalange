import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Flight {
    private int flightNumber;
    private Lane startingLane;
    private Lane landingLane;
    private Date startingTime;
    private Date landingTime;
    private StateOfFlight stateOfFlight;

}
