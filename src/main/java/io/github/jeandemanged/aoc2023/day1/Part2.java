package io.github.jeandemanged.aoc2023.day1;

import io.github.jeandemanged.aoc2023.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Part2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Part2.class);

    private static final Map<String, Integer> numbers = getNumbers();

    private static Map<String, Integer> getNumbers() {
        Map<String, Integer> numbers = new HashMap<>();
        for (int i = 1; i <= 9; i++) {
            numbers.put(String.valueOf(i), i);
        }
        numbers.put("one", 1);
        numbers.put("two", 2);
        numbers.put("three", 3);
        numbers.put("four", 4);
        numbers.put("five", 5);
        numbers.put("six", 6);
        numbers.put("seven", 7);
        numbers.put("eight", 8);
        numbers.put("nine", 9);
        return numbers;
    }

    private static int getFirstNumber(String line) {
        for (int i = 0; i < line.length(); i++) {
            Integer number = getNumber(line, i);
            if (number != null) {
                return number;
            }
        }
        throw new IllegalArgumentException("No first number in '" + line + "'");
    }

    private static int getLastNumber(String line) {
        for (int i = line.length() - 1; i >= 0; i--) {
            Integer number = getNumber(line, i);
            if (number != null) {
                return number;
            }
        }
        throw new IllegalArgumentException("No last number in '" + line + "'");
    }

    private static Integer getNumber(String line, int pos) {
        for (var entry : numbers.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            if (line.startsWith(key, pos)) {
                return value;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        var lines = FileUtils.readAllLines(Paths.get("data", "day1.txt"));
        int sum = 0;
        for (String line : lines) {
            int first = getFirstNumber(line);
            int last = getLastNumber(line);
            int lineValue = Integer.parseInt(String.valueOf(first) + last);
            sum += lineValue;
            LOGGER.info("  '{}' -> first={} last={} value={}", line, first, last, lineValue);
        }
        LOGGER.info("Day1 Part 2: {}", sum);
    }
}
