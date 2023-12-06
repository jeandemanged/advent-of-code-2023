package io.github.jeandemanged.aoc2023.day6;

import io.github.jeandemanged.aoc2023.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class Day6 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Day6.class);

    record Race(int time, int distance) {
    }

    record Puzzle(List<Race> races) {
        static Puzzle build(List<String> lines) {
            var puzzle = new Puzzle(new ArrayList<>());
            var times = values(lines.get(0));
            var distances = values(lines.get(1));
            if (times.size() != distances.size()) {
                throw new IllegalArgumentException("huho");
            }
            for (int i = 0; i < times.size(); i++) {
                Integer t = times.get(i);
                Integer d = distances.get(i);
                puzzle.races().add(new Race(t, d));
            }
            return puzzle;
        }
    }

    static List<Integer> values(String line) {
        return Arrays.stream(line.split(":")[1].split(" ")).map(s -> s.replace(" ", ""))
                .filter(Predicate.not(String::isBlank))
                .map(Integer::parseInt).toList();
    }

    public static void main(String[] args) {
        var puzzle = Puzzle.build(FileUtils.readAllLines(Paths.get("data", "day6.txt")));
        List<Integer> countWins = new ArrayList<>();
        for (Race race : puzzle.races()) {
            int countWin = 0;
            for (int t = 1; t <= race.time(); t++) {
                int distanceDone = (race.time() - t) * t;
                if (distanceDone > race.distance()) {
                    countWin++;
                }
            }
            countWins.add(countWin);
        }
        int part1 = countWins.stream().reduce(1, (a, b) -> a * b);
        LOGGER.info("Part 1: puzzle={}", puzzle);
        LOGGER.info("Part 1: countWins={}", countWins);
        LOGGER.info("Part 1: {}", part1);

        long time = 59688274L;
        long rec = 543102016641022L;

        long countWin = 0;
        for (long t = 1; t < time; t++) {
            long distanceDone = (time - t) * t;
            if (distanceDone > rec) {
                countWin++;
            }
        }
        LOGGER.info("Part 2: {}", countWin);
    }

}
