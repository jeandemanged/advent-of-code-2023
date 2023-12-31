package io.github.jeandemanged.aoc2023.day5;

import io.github.jeandemanged.aoc2023.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.*;

public class Day5 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Day5.class);

    enum Category {
        SEED, SOIL, FERTILIZER, WATER, LIGHT, TEMPERATURE, HUMIDITY, LOCATION
    }

    record MapRange(long destination, long source, long length) {
        static MapRange fromString(String line) {
            var nums = Arrays.stream(line.split(" ")).mapToLong(Long::parseLong).toArray();
            if (nums.length != 3) {
                throw new IllegalArgumentException("Invalid MapRange: " + line);
            }
            return new MapRange(nums[0], nums[1], nums[2]);
        }
    }

    record SourceDestMap(Category sourceCategory, Category destinationCategory, List<MapRange> mapRanges) {
        SourceDestMap(Category sourceCategory, Category destinationCategory) {
            this(sourceCategory, destinationCategory, new ArrayList<>());
        }

        void addMapRange(MapRange mapRange) {
            this.mapRanges().add(mapRange);
        }

        long getMappingForSource(long source) {
            for (MapRange mapRange : mapRanges) {
                if (source >= mapRange.source() && source < mapRange.source() + mapRange.length()) {
                    long offset = source - mapRange.source();
                    return mapRange.destination() + offset;
                }
            }
            return source;
        }
    }

    record Almanac(List<Long> seeds, List<SourceDestMap> sourceDestMaps) {
        static Almanac build(List<String> lines) {
            lines.add("");
            List<Long> seeds = Arrays.stream(lines.get(0).replace("seeds: ", "").split(" ")).map(Long::parseLong).toList();
            seeds = new ArrayList<>(seeds);
            List<SourceDestMap> sourceDestMaps = new ArrayList<>();
            Category sourceCategory;
            Category destinationCategory;
            SourceDestMap sourceDestMap = null;
            for (int i = 2; i < lines.size(); i++) {
                String l = lines.get(i);
                if (l.endsWith(" map:")) {
                    // map starts
                    var split = l.replace(" map:", "").replace("-to-", " ").toUpperCase().split(" ");
                    if (split.length != 2) {
                        throw new IllegalStateException("map starts at line " + i + " " + l);
                    }
                    sourceCategory = Category.valueOf(split[0]);
                    destinationCategory = Category.valueOf(split[1]);
                    sourceDestMap = new SourceDestMap(sourceCategory, destinationCategory);
                } else if (l.isEmpty()) {
                    // map ends
                    sourceDestMaps.add(sourceDestMap);
                    sourceDestMap = null;
                } else {
                    // map
                    if (sourceDestMap == null) {
                        throw new IllegalStateException("map at line " + i + " " + l);
                    }
                    sourceDestMap.addMapRange(MapRange.fromString(l));
                }
            }
            return new Almanac(seeds, sourceDestMaps);
        }

        /* works but doesnt fit in memory for real puzzle
        void patchSeedsForPartTwo() {
            if ((this.seeds().size() % 2) != 0) {
                throw new IllegalStateException("Uneven");
            }
            List<Long> original = new ArrayList<>(this.seeds());
            this.seeds().clear();
            for (int i = 0; i < original.size(); i = i + 2) {
                long rangeStart = original.get(i);
                long rangeLength = original.get(i + 1);
                LOGGER.info("{}, {}", rangeStart, rangeLength);
                LongStream.range(rangeStart, rangeStart + rangeLength).forEach(this.seeds()::add);
            }
        }
        */
    }

    public static void main(String[] args) {
        var almanac = Almanac.build(FileUtils.readAllLines(Paths.get("data", "day5.txt")));
        long lowest = getLowest(almanac);
        LOGGER.info("Part 1: {}", lowest);
        long lowest2 = getLowest2(almanac);
        LOGGER.info("Part 2: {}", lowest2);

    }

    private static long getLowest(Almanac almanac) {
        long lowest = Long.MAX_VALUE;
        for (long seed : almanac.seeds()) {
            LOGGER.info("-- seed {}", seed);
            long currentSource = seed;
            for (SourceDestMap sourceDestMap : almanac.sourceDestMaps()) {
                long newVal = sourceDestMap.getMappingForSource(currentSource);
                LOGGER.info("-- seed {}, {}->{}, {}->{}", seed, sourceDestMap.sourceCategory, sourceDestMap.destinationCategory, currentSource, newVal);
                currentSource = newVal;
            }
            lowest = Math.min(lowest, currentSource);
            LOGGER.info("lowest = {}", lowest);
            LOGGER.info("");
        }
        return lowest;
    }

    // works but completely dumb and awfully slow
    private static long getLowest2(Almanac almanac) {
        if ((almanac.seeds().size() % 2) != 0) {
            throw new IllegalStateException("Uneven");
        }
        long lowest = Long.MAX_VALUE;
        for (int i = 0; i < almanac.seeds().size(); i = i + 2) {
            LOGGER.info("-- i {}", i);
            long rangeStart = almanac.seeds().get(i);
            long rangeLength = almanac.seeds().get(i + 1);
            for (long j = rangeStart; j < rangeStart + rangeLength; j++) {
                long currentSource = j;
                for (SourceDestMap sourceDestMap : almanac.sourceDestMaps()) {
                    currentSource = sourceDestMap.getMappingForSource(currentSource);
                }
                lowest = Math.min(lowest, currentSource);
            }
        }
        return lowest;
    }
}
