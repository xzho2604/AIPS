import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;

class TrafficAnalyzerTest {

    @Test
    public void testProcessDayCount() throws IOException {
        var trafficAnalyzer = new TrafficAnalyzer("data.txt");
        trafficAnalyzer.extractTrafficCountFromFile();

        String s = "2016-12-01T05:00:00";
        LocalDateTime dateTime = LocalDateTime.parse(s);

        System.out.println(dateTime.toLocalDate());



    }

}