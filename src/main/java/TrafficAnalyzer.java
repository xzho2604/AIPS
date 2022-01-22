import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
public class TrafficAnalyzer {

    private Integer totalCount;
    private final Map<LocalDate, Integer> dayCountMap;
    private final Map<LocalDateTime, Integer> halfHourCountMap;
    private final Map<LocalDateTime, Integer> oneAndHalfHourCountMap;
    private final LinkedList<LocalDateTime> window;
    private Integer counter;

    public TrafficAnalyzer(String fileName) throws IOException {
        totalCount = 0;
        dayCountMap = new TreeMap<>();
        halfHourCountMap = new HashMap<>();
        oneAndHalfHourCountMap = new HashMap<>();
        window = new LinkedList<>();
        counter = 0;

        extractTrafficCountFromFile(fileName);
    }

    public void printSummary() {
        log.info(String.format("Total Count: %s", totalCount));
        log.info(String.format("Day Count: %s", outputString(dailyTrafficCount())));
        log.info(String.format("Top 3 Half Hour Count: %s", outputString(top3HalfHours())));
        log.info(String.format("1.5 Hour with Least Traffic: %s", outputString(oneAndHalfHourWithLeastCars())));
    }

    public void extractTrafficCountFromFile(String fileName) throws IOException {
        String text = readFileAsString(fileName);
        if (text.isBlank()) {
            throw new IllegalArgumentException(String.format("File %s is blank, can not process!", fileName));
        }

        for (var line : text.split("\n")) {
            var dateTime = LocalDateTime.parse(line.split(" ")[0]);
            var count = Integer.parseInt(line.split(" ")[1]);

            dayCountMap.put(dateTime.toLocalDate(), count);
            halfHourCountMap.put(dateTime, count);
            totalCount += count;
            processOneAndHalfHourTrafficCount(dateTime, count);
        }
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public List<String> dailyTrafficCount() {
        return dayCountMap.entrySet()
                .stream()
                .map(this::entryToString)
                .collect(Collectors.toList());
    }

    public List<String> top3HalfHours() {
        return halfHourCountMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(this::entryToString)
                .limit(3)
                .collect(Collectors.toList());
    }

    public List<String> oneAndHalfHourWithLeastCars() {
        return oneAndHalfHourCountMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(1)
                .map(this::entryToString)
                .collect(Collectors.toList());
    }

    private void processOneAndHalfHourTrafficCount(LocalDateTime dateTime, int count) {
        if (window.size() == 0) {
            window.add(dateTime);
            counter += count;
        } else if (window.size() < 3) {
            var last = window.peekLast();
            if (isNext30Minutes(last, dateTime)) {
                window.add(dateTime);
                counter += count;
                // first time window size grow to 3
                if (window.size() == 3) {
                    oneAndHalfHourCountMap.put(window.peekFirst(), counter);
                    var first = window.pollFirst();
                    counter -= halfHourCountMap.get(first);
                }
            } else { // next block non adjacent
                window.clear();
                window.add(dateTime);
                counter = count;
            }
        }
    }


    private String readFileAsString(String fileName) throws IOException {
        var inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IOException(String.format("Not able to open file: %s", fileName));
        }

        return IOUtils.toString(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8.name());
    }

    private boolean isNext30Minutes(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return ChronoUnit.MINUTES.between(dateTime1, dateTime2) == 30;
    }

    private String entryToString(Map.Entry entry) {
        return String.format("%s %s", entry.getKey(), entry.getValue());
    }

    private String outputString(List<String> list) {
        return String.join("; ", list);
    }

}
