import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Stack;

class TrafficAnalyzerTest {

    @Test
    public void testProcessDayCount() throws IOException {
        var trafficAnalyzer = new TrafficAnalyzer("data.txt");
        trafficAnalyzer.printSummary();
    }

    @Test
    public void demo() {
        var stack = new Stack<Integer>();
        stack.add(1);
        stack.add(2);
        var r = stack.peek();
        System.out.println(r);

    }


}