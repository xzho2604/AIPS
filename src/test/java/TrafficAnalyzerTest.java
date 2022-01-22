import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TrafficAnalyzerTest {

    @ParameterizedTest
    @MethodSource("trafficCounterArg")
    @DisplayName("Given different data would return the right traffic count results")
    public void processDayCount(String fileName,
                                Integer totalCount,
                                List<String> expectedDailyTrafficCount,
                                List<String> expectedTop3HalfHours,
                                List<String> expectedOneAndHalfHourWithLeastCars
    ) throws IOException {

        var trafficAnalyzer = new TrafficAnalyzer(fileName);
        trafficAnalyzer.printSummary();

        assertThat(trafficAnalyzer.getTotalCount()).isEqualTo(totalCount);
        assertThat(trafficAnalyzer.dailyTrafficCount()).isEqualTo(expectedDailyTrafficCount);
        assertThat(trafficAnalyzer.top3HalfHours()).isEqualTo(expectedTop3HalfHours);
        assertThat(trafficAnalyzer.oneAndHalfHourWithLeastCars()).isEqualTo(expectedOneAndHalfHourWithLeastCars);

    }

    @Test
    @DisplayName("Open unknown file should throw IO exceptions")
    public void invalidFile() {
        assertThrows(IOException.class, () -> new TrafficAnalyzer("unknown.txt"));
    }

    @Test
    @DisplayName("Open empty file should throw IllegalArgument exceptions")
    public void blankFile() {
        assertThrows(IllegalArgumentException.class, () -> new TrafficAnalyzer("empty.txt"));
    }

    private static Stream<Arguments> trafficCounterArg() {
        return Stream.of(
                Arguments.of(
                        "original-data.txt", 398,
                        List.of("2016-12-01 0", "2016-12-05 15", "2016-12-08 11", "2016-12-09 4"),
                        List.of("2016-12-01T07:30 46", "2016-12-01T08:00 42", "2016-12-08T18:00 33"),
                        List.of("2016-12-01T05:00 31")
                ),
                Arguments.of(
                        "no-continuous-one-and-half-hour.txt", 325,
                        List.of("2016-12-01 0", "2016-12-05 15", "2016-12-08 11", "2016-12-09 4"),
                        List.of("2016-12-01T08:00 42", "2016-12-08T18:00 33", "2016-12-08T19:00 28"),
                        List.of()
                ),
                Arguments.of(
                        "half-hour.txt", 5,
                        List.of("2016-12-01 5"),
                        List.of("2016-12-01T05:00 5"),
                        List.of()
                ),
                Arguments.of(
                        "one-and-half-hour.txt", 31,
                        List.of("2016-12-01 14"),
                        List.of("2016-12-01T06:00 14", "2016-12-01T05:30 12", "2016-12-01T05:00 5"),
                        List.of("2016-12-01T05:00 31")
                ),
                Arguments.of(
                        "one-hour.txt", 17,
                        List.of("2016-12-01 12"),
                        List.of("2016-12-01T05:30 12", "2016-12-01T05:00 5"),
                        List.of()
                )

        );
    }
}