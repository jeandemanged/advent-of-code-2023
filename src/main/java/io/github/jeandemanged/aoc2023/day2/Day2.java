package io.github.jeandemanged.aoc2023.day2;

import io.github.jeandemanged.aoc2023.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Day2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Day2.class);

    enum Color {
        RED, GREEN, BLUE,
    }

    record Cube(int num, Color color) {
    }

    record Reveal(List<Cube> cubes) {
    }

    record Game(int id, List<Reveal> reveals) {
    }

    public static void main(String[] args) {
        var lines = FileUtils.readAllLines(Paths.get("data", "day2.txt"));
        List<Game> games = new ArrayList<>();
        for (var line : lines) {
            var gr = line.split(":");
            if (gr.length != 2) {
                throw new IllegalArgumentException("expected 2 for game");
            }
            var gamestr = gr[0];
            var revealsstr = gr[1];
            final int gameId = Integer.parseInt(gamestr.replace("Game ", ""));
            var reveals = revealsstr.toUpperCase()
                    .replaceFirst(" ", "")
                    .replace(", ", ",")
                    .replace("; ", ";")
                    .split(";");

            List<Reveal> revealList = new ArrayList<>();
            for (var reveal : reveals) {
                var cubesStr = reveal.split(",");
                List<Cube> cubes = new ArrayList<>();
                for (var cubeStr : cubesStr) {
                    var rs = cubeStr.split(" ");
                    if (rs.length != 2) {
                        throw new IllegalArgumentException("expected 2 for cube");
                    }
                    var num = Integer.parseInt(rs[0]);
                    var color = Color.valueOf(rs[1]);
                    cubes.add(new Cube(num, color));
                }
                revealList.add(new Reveal(cubes));
            }
            games.add(new Game(gameId, revealList));
        }
        var maxPerColor = Map.of(Color.RED, 12, Color.GREEN, 13, Color.BLUE, 14);
        var possibleGames = games.stream().filter(game -> {
            var filter =
                    game.reveals().stream().flatMap(r -> r.cubes().stream()).noneMatch(reveal -> reveal.num() > maxPerColor.get(reveal.color()));
            LOGGER.info("filtering {} {}", game.id(), filter);
            return filter;
        });
        int sum = possibleGames.mapToInt(Game::id).sum();
        LOGGER.info("Day2 Part 1: {}", sum);
    }
}
