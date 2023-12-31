package io.github.jeandemanged.aoc2023.day8;

import io.github.jeandemanged.aoc2023.utils.FileUtils;
import io.github.jeandemanged.aoc2023.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.*;

public class Day8 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Day8.class);

    enum LeftRight {
        L(0), R(1);

        private final int value;

        LeftRight(int p) {
            this.value = p;
        }

        public int getValue() {
            return value;
        }

        static LeftRight fromString(String inputChar) {
            return Arrays.stream(LeftRight.values()).filter(lr -> lr.name().equals(inputChar))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("oh noes: " + inputChar));
        }
    }


    record Puzzle(String startingNode, List<LeftRight> instructions, Map<String, List<String>> map) {
        Puzzle() {
            this("AAA", new ArrayList<>(), new HashMap<>());
        }

        long getSteps() {
            var currentNode = startingNode();
            long currentSteps = 0;
            int instructionPos = -1;
            while (!"ZZZ".equals(currentNode)) {
                instructionPos = (instructionPos + 1) % instructions().size();
                LeftRight currentInstruction = instructions().get(instructionPos);
                currentNode = map.get(currentNode).get(currentInstruction.getValue());
                currentSteps++;
            }
            return currentSteps;
        }

        // doesn't make it
        long getSteps2() {
            var startingNodes = map().keySet().stream().filter(s -> s.endsWith("A")).toList();
            List<String> currentNodes = new ArrayList<>(startingNodes);
            long currentSteps = 0;
            int instructionPos = -1;
            while (!currentNodes.stream().allMatch(n -> n.endsWith("Z"))) {
                instructionPos = (instructionPos + 1) % instructions().size();
                LeftRight currentInstruction = instructions().get(instructionPos);
                for (int i = 0; i < currentNodes.size(); i++) {
                    String node = currentNodes.get(i);
                    currentNodes.set(i, map.get(node).get(currentInstruction.getValue()));
                }
                currentSteps++;
            }
            return currentSteps;
        }

        long getSteps3() {
            var startingNodes = map().keySet().stream().filter(s -> s.endsWith("A")).toList();
            Map<String, Long> steps = new HashMap<>();
            startingNodes.forEach(sn -> {
                var currentNode = sn;
                long currentSteps = 0;
                int instructionPos = -1;
                while (!currentNode.endsWith("Z")) {
                    instructionPos = (instructionPos + 1) % instructions().size();
                    LeftRight currentInstruction = instructions().get(instructionPos);
                    currentNode = map.get(currentNode).get(currentInstruction.getValue());
                    currentSteps++;
                }
                steps.put(sn, currentSteps);
            });
            LOGGER.info("{}", steps);
            return steps.values().stream().reduce(1L, Utils::lcm);
        }

        static Puzzle build(List<String> lines) {
            var puzzle = new Puzzle();
            puzzle.instructions().addAll(lines.get(0).chars().mapToObj(c -> LeftRight.fromString(String.valueOf((char) c))).toList());
            for (int i = 2; i < lines.size(); i++) {
                String line = lines.get(i);
                var eqSplit = line.split(" = ");
                var start = eqSplit[0];
                var lr = eqSplit[1].replace(" ", "").replace("(", "").replace(")", "").split(",");
                puzzle.map().put(start, List.of(lr[0], lr[1]));
            }
            return puzzle;
        }
    }

    public static void main(String[] args) {
        var puzzle = Puzzle.build(FileUtils.readAllLines(Paths.get("data", "day8.txt")));
        LOGGER.info("Part 1: {}", puzzle.getSteps());
        LOGGER.info("Part 2: {}", puzzle.getSteps3());
    }

}
