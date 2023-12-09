package io.github.jeandemanged.aoc2023.day9;

import io.github.jeandemanged.aoc2023.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day9 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Day9.class);

    record History(List<Long> history) {

        private List<List<Long>> buildSequencesTillZeroes() {
            List<List<Long>> sequences = new ArrayList<>();
            // down
            List<Long> current = new ArrayList<>(this.history());
            while (!allZeros(current)) {
                sequences.add(current);
                current = toNext(current);
            }
            sequences.add(current);
            return sequences;
        }

        long predictNext() {
            List<List<Long>> sequences = buildSequencesTillZeroes();
            // up
            for (int i = sequences.size() - 1; i >= 0; i--) {
                List<Long> seq = sequences.get(i);
                if (i == sequences.size() - 1) {
                    seq.add(0L);
                } else {
                    long lastVal = seq.get(seq.size() - 1);
                    List<Long> dnSeq = sequences.get(i + 1);
                    long lastDiff = dnSeq.get(dnSeq.size() - 1);
                    seq.add(lastVal + lastDiff);
                }
            }
            List<Long> first = sequences.get(0);
            return first.get(first.size() - 1);
        }

        long predictBefore() {
            List<List<Long>> sequences = buildSequencesTillZeroes();
            // up
            for (int i = sequences.size() - 1; i >= 0; i--) {
                List<Long> seq = sequences.get(i);
                if (i == sequences.size() - 1) {
                    seq.add(0, 0L);
                } else {
                    long firstVal = seq.get(0);
                    List<Long> dnSeq = sequences.get(i + 1);
                    long firstDiff = dnSeq.get(0);
                    seq.add(0, firstVal - firstDiff);
                }
            }
            List<Long> first = sequences.get(0);
            return first.get(0);
        }
    }

    record Puzzle(List<History> historyList) {
        static Puzzle build(List<String> lines) {
            var puzzle = new Puzzle(new ArrayList<>());
            lines.forEach(line -> puzzle.historyList().add(new History(Arrays.stream(line.split(" ")).map(Long::parseLong).toList())));
            return puzzle;
        }
    }

    static List<Long> toNext(List<Long> list) {
        List<Long> newList = new ArrayList<>(list.size() - 1);
        for (int i = 0; i < list.size() - 1; i++) {
            newList.add(list.get(i + 1) - list.get(i));
        }
        return newList;
    }

    static boolean allZeros(List<Long> list) {
        return list.stream().allMatch(l -> 0L == l);
    }

    public static void main(String[] args) {
        var puzzle = Puzzle.build(FileUtils.readAllLines(Paths.get("data", "day9.txt")));

        long part1 = puzzle.historyList().stream().mapToLong(History::predictNext).sum();
        LOGGER.info("Part 1: {}", part1);
        long part2 = puzzle.historyList().stream().mapToLong(History::predictBefore).sum();
        LOGGER.info("Part 2: {}", part2);
    }

}
