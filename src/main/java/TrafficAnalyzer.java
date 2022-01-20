import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class TrafficAnalyzer {
    private Map<LocalDate, Integer> dayCountMap;
    private Map<LocalDateTime, Integer> halfHourCountMap;
    private Map<String, Integer> oneAndHalfHourCountMap;
    private String fileName;
    private Integer totalCount;

    public TrafficAnalyzer(String filename) {
        fileName = filename;
        this.totalCount = 0;
        dayCountMap = new TreeMap<>();
        halfHourCountMap = new HashMap<>();
        oneAndHalfHourCountMap = new HashMap<>();
    }

    public void outPutReport() {
        System.out.println("-----------------------Total Count -----------------------");
        System.out.println(totalCount);
        System.out.println("-----------------------Day Count -----------------------");
        printDayCount();
        System.out.println("-----------------------Top 3 Half Hour Count -----------------------");
        printTop3HalfHours();
    }


    public void extractTrafficCountFromFile() throws IOException {
        for (var line : readFileAsString(fileName).split("\n")) {
//            System.out.println(line);

            var dateTime = LocalDateTime.parse(line.split(" ")[0]);
            var count = Integer.parseInt(line.split(" ")[1]);

            dayCountMap.put(dateTime.toLocalDate(), count);
            halfHourCountMap.put(dateTime, count);

            totalCount += count;
        }

    }

    private void printTop3HalfHours() {
        var entryList = new ArrayList<>(halfHourCountMap.entrySet());
        entryList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        entryList.subList(0, 3).forEach(this::printEntry);
    }

    private void printDayCount() {
        dayCountMap.entrySet().forEach(this::printEntry);
    }

    private void printEntry(Map.Entry entry) {
        System.out.println(String.format("%s %d", entry.getKey(), entry.getValue()));
    }

    private String readFileAsString(String fileName) throws IOException {
        var inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        String text = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());

        return text;

    }
}
