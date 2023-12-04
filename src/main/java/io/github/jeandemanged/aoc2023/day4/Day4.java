package io.github.jeandemanged.aoc2023.day4;

import io.github.jeandemanged.aoc2023.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;

public class Day4 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Day4.class);

    record Card(CardPile cardPile, int id, List<Integer> winningNumbers, List<Integer> numbers) {
        @Override
        public String toString() {
            return "Card{" +
                    "id=" + id +
                    ", winningNumbers=" + winningNumbers +
                    ", numbers=" + numbers +
                    '}';
        }

        int numWinning() {
            var winningSet = new HashSet<>(winningNumbers());
            var numSet = new HashSet<>(numbers());
            winningSet.retainAll(numSet);
            LOGGER.debug("{} numWins = {}, {}", id(), winningSet.size(), winningSet);
            return winningSet.size();
        }

        int score() {
            int score = (int) Math.pow(2, numWinning() - 1.);
            LOGGER.debug("{} score = {}", id(), score);
            return score;
        }
    }

    record CardPile(List<Card> cards) {
        CardPile() {
            this(new ArrayList<>());
        }

        void addCard(int id, List<Integer> winningNumbers, List<Integer> numbers) {
            cards().add(new Card(this, id, winningNumbers, numbers));
        }

        void addCard(String line) {
            var semiSplit = line.split(":");
            if (semiSplit.length != 2) {
                throw new IllegalArgumentException("Expected 2 for : split");
            }
            int id = Integer.parseInt(semiSplit[0].replace("Card", "").replace(" ", ""));
            var barSplit = semiSplit[1].split("\\|");
            if (barSplit.length != 2) {
                throw new IllegalArgumentException("Expected 2 for | split");
            }
            var winSplit = Arrays.stream(barSplit[0].split(" ")).filter(Predicate.not(String::isEmpty)).map(Integer::parseInt).toList();
            var numSplit = Arrays.stream(barSplit[1].split(" ")).filter(Predicate.not(String::isEmpty)).map(Integer::parseInt).toList();
            this.addCard(id, winSplit, numSplit);
        }

        static CardPile build(List<String> lines) {
            CardPile cardPile = new CardPile();
            lines.forEach(cardPile::addCard);
            return cardPile;
        }
    }

    public static void main(String[] args) {
        var cardPile = CardPile.build(FileUtils.readAllLines(Paths.get("data", "day4.txt")));

        var score = cardPile.cards().stream().mapToInt(Card::score).sum();

        LOGGER.info("Day4 Part 1: {}", score);
    }

}