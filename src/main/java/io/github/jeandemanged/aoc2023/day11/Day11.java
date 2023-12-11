package io.github.jeandemanged.aoc2023.day11;

import io.github.jeandemanged.aoc2023.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Day11 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Day11.class);

    record Puzzle(int xSize, int ySize, int[][] map) {
        static Puzzle build(List<String> lines) {
            int xSize = lines.get(0).length();
            int ySize = lines.size();
            int[][] map = new int[xSize][ySize];
            int currentGalaxy = 0;
            for (int y = 0; y < ySize; y++) {
                String line = lines.get(y);
                for (int x = 0; x < xSize; x++) {
                    char c = line.charAt(x);
                    if (c == '#') {
                        map[x][y] = ++currentGalaxy;
                    } else {
                        map[x][y] = 0;
                    }
                }
            }
            return new Puzzle(xSize, ySize, map);
        }

        static Puzzle fromExpand(Puzzle puzzle) {
            var expandInfo = ExpandInfo.from(puzzle.map(), 1);
            int xSize = puzzle.xSize() + expandInfo.xGrow().size();
            int ySize = puzzle.ySize() + expandInfo.yGrow().size();
            int[][] map = new int[xSize][ySize];

            for (int y = 0; y < puzzle.map()[0].length; y++) {
                for (int x = 0; x < puzzle.map().length; x++) {
                    final int tmpX = x;
                    int newX = (int) (x + expandInfo.xGrow().stream().filter(i -> i < tmpX).count());
                    final int tmpY = y;
                    int newY = (int) (y + expandInfo.yGrow().stream().filter(i -> i < tmpY).count());
                    map[newX][newY] = puzzle.map()[x][y];
                }
            }
            return new Puzzle(xSize, ySize, map);
        }
    }

    record Galaxy(int x, int y, int num, ExpandInfo expandInfo) {

        long distance(Galaxy other) {
            if (this.expandInfo() == null) {
                // galaxy already expanded
                return Math.abs(other.x() - this.x()) + Math.abs(other.y() - this.y());
            } else {
                return Math.abs(other.getRealX() - getRealX()) + Math.abs(other.getRealY() - getRealY());
            }
        }

        long getRealX() {
            return x() + expandInfo.howMany() * expandInfo.xGrow().stream().filter(i -> i < x()).count();
        }

        long getRealY() {
            return y() + expandInfo.howMany() * expandInfo.yGrow().stream().filter(i -> i < y()).count();
        }
    }

    static void display(int[][] map) {
        for (int y = 0; y < map[0].length; y++) {
            for (int x = 0; x < map.length; x++) {
                System.out.print(map[x][y]);
            }
            System.out.println();
        }
    }

    static List<Galaxy> getGalaxies(int[][] map) {
        // for map already expanded
        return getGalaxies(map, null);
    }

    static List<Galaxy> getGalaxies(int[][] map, ExpandInfo expandInfo) {
        List<Galaxy> galaxies = new ArrayList<>();
        for (int y = 0; y < map[0].length; y++) {
            for (int x = 0; x < map.length; x++) {
                if (map[x][y] > 0) {
                    galaxies.add(new Galaxy(x, y, map[x][y], expandInfo));
                }
            }
        }
        return galaxies;
    }

    record ExpandInfo(List<Integer> xGrow, List<Integer> yGrow, long howMany) {
        static ExpandInfo from(int[][] map, long howMany) {
            List<Integer> xGrow = new ArrayList<>();
            List<Integer> yGrow = new ArrayList<>();
            for (int y = 0; y < map.length; y++) {
                boolean allZero = true;
                for (int x = 0; x < map[y].length; x++) {
                    if (map[x][y] > 0) {
                        allZero = false;
                    }
                }
                if (allZero) {
                    yGrow.add(y);
                }
            }
            for (int x = 0; x < map[0].length; x++) {
                boolean allZero = true;
                for (int y = 0; y < map.length; y++) {
                    if (map[x][y] > 0) {
                        allZero = false;
                    }
                }
                if (allZero) {
                    xGrow.add(x);
                }
            }
            return new ExpandInfo(xGrow, yGrow, howMany);
        }
    }

    public static void main(String[] args) {
        var puzzle = Puzzle.build(FileUtils.readAllLines(Paths.get("data", "day11.txt")));

        display(puzzle.map());
        ExpandInfo expandInfo = ExpandInfo.from(puzzle.map(), 1);
        LOGGER.info("{} ", expandInfo);
        Puzzle expanded = Puzzle.fromExpand(puzzle);
        display(expanded.map());
        var galaxies = getGalaxies(expanded.map());
        int distance = 0;
        for (int i = 0; i < galaxies.size(); i++) {
            Galaxy gi = galaxies.get(i);
            for (int j = i; j < galaxies.size(); j++) {
                Galaxy gj = galaxies.get(j);
                distance += gi.distance(gj);
            }
        }
        LOGGER.info("Part 1: {}", distance);

        ExpandInfo expandInfo2 = ExpandInfo.from(puzzle.map(), 999999);
        var galaxies2 = getGalaxies(puzzle.map(), expandInfo2);
        long distance2 = 0L;
        for (int i = 0; i < galaxies2.size(); i++) {
            Galaxy gi = galaxies2.get(i);
            for (int j = i; j < galaxies2.size(); j++) {
                Galaxy gj = galaxies2.get(j);
                distance2 += gi.distance(gj);
            }
        }
        LOGGER.info("Part 2: {}", distance2);
    }

}
