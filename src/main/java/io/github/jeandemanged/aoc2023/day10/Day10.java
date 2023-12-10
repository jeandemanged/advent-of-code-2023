package io.github.jeandemanged.aoc2023.day10;

import io.github.jeandemanged.aoc2023.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;

public class Day10 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Day10.class);

    enum PipeType {
        NS("|"),
        EW("-"),
        NE("L"),
        NW("J"),
        SW("7"),
        SE("F"),
        G("."),
        S("S");

        private final String type;

        PipeType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        static PipeType from(String s) {
            return Arrays.stream(PipeType.values()).filter(type -> Objects.equals(type.type, s))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("oh noes: " + s));
        }

        boolean connectsSouth() {
            return Objects.equals(this, S) || Objects.equals(this, SE) || Objects.equals(this, SW) || Objects.equals(this, NS);
        }

        boolean connectsNorth() {
            return Objects.equals(this, S) || Objects.equals(this, NE) || Objects.equals(this, NW) || Objects.equals(this, NS);
        }

        boolean connectsEast() {
            return Objects.equals(this, S) || Objects.equals(this, NE) || Objects.equals(this, SE) || Objects.equals(this, EW);
        }

        boolean connectsWest() {
            return Objects.equals(this, S) || Objects.equals(this, NW) || Objects.equals(this, SW) || Objects.equals(this, EW);
        }

    }

    record Pipe(int x, int y, PipeType pipeType) {
        List<Pipe> getNeightbors(Puzzle puzzle) {
            List<Pipe> neightbors = new ArrayList<>();
            final int x = this.x();
            final int y = this.y();
            final var t = this.pipeType();
            final var west = puzzle.pipes()[x - 1][y];
            final var east = puzzle.pipes()[x + 1][y];
            final var north = puzzle.pipes()[x][y - 1];
            final var south = puzzle.pipes()[x][y + 1];

            if (t.connectsWest() && west.pipeType().connectsEast()) {
                neightbors.add(west);
            }
            if (t.connectsEast() && east.pipeType().connectsWest()) {
                neightbors.add(east);
            }
            if (t.connectsNorth() && north.pipeType().connectsSouth()) {
                neightbors.add(north);
            }
            if (t.connectsSouth() && south.pipeType().connectsNorth()) {
                neightbors.add(south);
            }

            return neightbors;
        }
    }

    record Puzzle(int xSize, int ySize, Pipe[][] pipes, Pipe startingPosition) {
        static Puzzle build(List<String> lines) {
            int xSize = lines.get(0).length() + 2;
            int ySize = lines.size() + 2;
            Pipe[][] pipes = new Pipe[xSize][ySize];
            Pipe startingPosition = null;
            for (int y = 0; y < lines.size(); y++) {
                String line = "." + lines.get(y) + ".";
                for (int x = 0; x < line.length(); x++) {
                    var type = PipeType.from(String.valueOf(line.charAt(x)));
                    var pipe = new Pipe(x, y + 1, type);
                    if (PipeType.S == type) {
                        startingPosition = pipe;
                    }
                    pipes[x][y + 1] = pipe;
                }
            }
            for (int i = 0; i < xSize; i++) {
                pipes[i][0] = new Pipe(i, 0, PipeType.G);
                pipes[i][ySize - 1] = new Pipe(i, ySize - 1, PipeType.G);
            }
            return new Puzzle(xSize, ySize, pipes, startingPosition);
        }

        int findSteps() {
            int[][] step = new int[this.xSize][this.ySize];
            List<Pipe> pipeList = new ArrayList<>();
            Set<Pipe> donePipes = new HashSet<>();
            pipeList.add(this.startingPosition());
            boolean done = false;
            int currentStep = 1;
            while (!done) {
                List<Pipe> nextPipeList = new ArrayList<>();
                for (Pipe pipe : pipeList) {
                    int x = pipe.x();
                    int y = pipe.y();
                    if (step[x][y] == 0) {
                        step[x][y] = currentStep;
                    }
                    donePipes.add(pipe);
                    nextPipeList.addAll(pipe.getNeightbors(this).stream().filter(Predicate.not(donePipes::contains)).toList());
                }
                LOGGER.info("{} | {} -> {}", currentStep, pipeList, nextPipeList);
                currentStep++;
                pipeList = new ArrayList<>(nextPipeList);
                done = pipeList.isEmpty();
            }
            for (int y = 0; y < ySize(); y++) {
                for (int x = 0; x < xSize(); x++) {
                    System.out.print(step[x][y] > 0 ? pipes()[x][y].pipeType().getType() : ".");
                }
                System.out.println();
            }

            int count = 0;
            for (int y = 0; y < ySize(); y++) {
                for (int x = 0; x < xSize(); x++) {
                    if (step[x][y] > 0) {
                        continue;
                    }
                    Pipe pipe = pipes()[x][y];
                    if (isInsideMainLoop(step, pipe)) {
                        count++;
                    }
                }
            }
            LOGGER.info("Part 1: {}", currentStep - 2);
            LOGGER.info("Part 2: {}", count);

            return currentStep - 2; // bc we started at 1 and added 1
        }

        boolean isInsideMainLoop(int[][] loop, Pipe pipe) {
            int pipeX = pipe.x();
            int pipeY = pipe.y();
            if (loop[pipeX][pipeY] > 0) {
                // is the main loop itself
                return false;
            }
            int crossingsXwest = 0;
            for (int x = 0; x < pipeX; x++) {
                var type = pipes()[x][pipeY].pipeType();
                // we "look" a bit "below" when counting crossings from left, blocked by "|", by "F", and by "7". we are not blocked by "J" and "L"
                // in my puzzle, the starting point is actually a "|", so I include it as blocking, but this code won't work for all puzzles...
                if (loop[x][pipeY] > 0 && (type == PipeType.S || type == PipeType.NS || type == PipeType.SE || type == PipeType.SW)) {
                    crossingsXwest++;
                }
            }
            return crossingsXwest % 2 != 0;
        }
    }

    public static void main(String[] args) {
        var puzzle = Puzzle.build(FileUtils.readAllLines(Paths.get("data", "day10.txt")));
        puzzle.findSteps();
    }

}
