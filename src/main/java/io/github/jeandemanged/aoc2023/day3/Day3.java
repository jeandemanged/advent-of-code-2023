package io.github.jeandemanged.aoc2023.day3;

import io.github.jeandemanged.aoc2023.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Day3 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Day3.class);

    record Number(int x1, int x2, int y, int value) {
        boolean isAdjacentToSymbol(Symbol symbol) {
            return (symbol.x() >= x1 - 1) && (symbol.x() <= x2 + 1) && (symbol.y() <= y + 1) && (symbol.y() >= y - 1);
        }
    }

    record Symbol(int x, int y, Character symbol) {
    }

    record EngineSchematic(List<Number> numbers, List<Symbol> symbols) {
    }


    public static void main(String[] args) {
        var schematic = buildEngineSchematic(FileUtils.readAllLines(Paths.get("data", "day3.txt")));
        List<Number> filteredNumbers = new ArrayList<>();
        for (Number number : schematic.numbers()) {
            if (schematic.symbols().stream().anyMatch(number::isAdjacentToSymbol)) {
                filteredNumbers.add(number);
                LOGGER.debug("adding: {}", number);
            } else {
                LOGGER.debug("ignoring: {}", number);
            }
        }
        int sum = filteredNumbers.stream().mapToInt(Number::value).sum();
        LOGGER.info("Day3 Part 1: {}", sum);
    }

    private static EngineSchematic buildEngineSchematic(List<String> lines) {
        EngineSchematic engineSchematic = new EngineSchematic(new ArrayList<>(), new ArrayList<>());
        for (int y = 0; y < lines.size(); y++) {
            var line = lines.get(y);
            List<Character> number = null;
            int x1 = 0;
            int x2 = 0;
            for (int x = 0; x < line.length(); x++) {
                var c = line.charAt(x);
                boolean isDigit = Character.isDigit(c);
                boolean isDot = '.' == c;
                boolean isSymbol = !isDigit && !isDot;
                if (isDigit) {
                    if (number == null) {
                        number = new ArrayList<>(List.of(c));
                        x1 = x;
                        x2 = x;
                    } else {
                        number.add(c);
                        x2 = x;
                    }
                } else {
                    if (number != null) {
                        addNumber(engineSchematic, y, number, x1, x2);
                    }
                    number = null;
                    x1 = 0;
                    x2 = 0;
                }
                if (isSymbol) {
                    engineSchematic.symbols().add(new Symbol(x, y, c));
                }
            }
            // eol
            if (number != null) {
                addNumber(engineSchematic, y, number, x1, x2);
            }
        }
        LOGGER.info("EngineSchematic has {} numbers and {} symbols", engineSchematic.numbers().size(), engineSchematic.symbols().size());
        return engineSchematic;
    }

    private static void addNumber(EngineSchematic engineSchematic, int y, List<Character> number, int x1, int x2) {
        String numberString = number.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
        engineSchematic.numbers().add(new Number(x1, x2, y, Integer.parseInt(numberString)));
    }
}