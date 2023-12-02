package io.github.jeandemanged.aoc2023.day1;

import io.github.jeandemanged.aoc2023.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

public class Part1 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Part1.class);

    public static void main(String[] args) {
        var lines = FileUtils.readAllLines(Paths.get("data", "day1.txt"));
        int sum = 0;
        for (String line : lines) {
            Character first = null;
            Character last = null;
            for (int i = 0; i < line.length(); i++) {
                var c = line.charAt(i);
                if (Character.isDigit(c)) {
                    if (first == null) {
                        first = c;
                        last = c;
                    } else {
                        last = c;
                    }
                }
            }
            if (first == null) {
                LOGGER.error("Invalid line '{}'", line);
                continue;
            }
            char[] chars = {first, last};
            String lineValueString = new String(chars);
            int lineValue = Integer.parseInt(lineValueString);
            sum += lineValue;
            LOGGER.info("'{}' -> first={} last={} value={}", line, first, last, lineValue);
        }
        LOGGER.info("Day1 Part 1: {}", sum);
    }
}
