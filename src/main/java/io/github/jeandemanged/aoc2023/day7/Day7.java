package io.github.jeandemanged.aoc2023.day7;

import io.github.jeandemanged.aoc2023.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day7 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Day7.class);

    interface ValuedEnum {
        int getValue();
    }

    enum Card implements ValuedEnum {
        CA('A', 14),
        CK('K', 13),
        CQ('Q', 12),
        CJ('J', 11),
        CT('T', 10),
        C9('9', 9),
        C8('8', 8),
        C7('7', 7),
        C6('6', 6),
        C5('5', 5),
        C4('4', 4),
        C3('3', 3),
        C2('2', 2);

        private final char c;
        private final int value;

        Card(char c, int value) {
            this.c = c;
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }

        static Card fromChar(char inputChar) {
            return Arrays.stream(Card.values()).filter(card -> card.c == inputChar)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("oh noes: " + inputChar));
        }

        @Override
        public String toString() {
            return String.valueOf(c);
        }
    }

    static class ValuedEnumComparator<T extends ValuedEnum> implements Comparator<T> {
        @Override
        public int compare(T c1, T c2) {
            return Integer.compare(c1.getValue(), c2.getValue());
        }
    }

    static ValuedEnumComparator<HandType> handTypeComparator = new ValuedEnumComparator<>();
    static ValuedEnumComparator<Card> cardComparator = new ValuedEnumComparator<>();
    static HandComparator handComparator = new HandComparator();

    static class HandComparator implements Comparator<Hand> {
        @Override
        public int compare(Hand h1, Hand h2) {
            int htComp = handTypeComparator.compare(h1.getHandType(), h2.getHandType());
            if (htComp != 0) {
                return htComp;
            } else {
                for (int i = 0; i < h1.cards().size(); i++) {
                    int cComp = cardComparator.compare(h1.cards().get(i), h2.cards().get(i));
                    if (cComp != 0) {
                        return cComp;
                    }
                }
            }
            return 0;
        }
    }


    enum HandType implements ValuedEnum {
        FIVE_OF_A_KIND(7),
        FOUR_OF_A_KIND(6),
        FULL_HOUSE(5),
        THREE_OF_A_KIND(4),
        TWO_PAIR(3),
        ONE_PAIR(2),
        HIGH_CARD(1);
        private final int value;

        HandType(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }


    record Hand(List<Card> cards, int bid) {

        HandType getHandType() {
            Map<Card, Integer> count = new EnumMap<>(Card.class);
            cards.forEach(card -> {
                count.computeIfAbsent(card, c -> 0);
                count.put(card, count.get(card) + 1);
            });
            var nonZeroes =
                    count.entrySet().stream().filter(entry -> entry.getValue() != 0).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            var nonZerosCounts = nonZeroes.values().stream().sorted(Comparator.reverseOrder()).toList();
            if (nonZeroes.size() == 5) {
                return HandType.HIGH_CARD;
            } else if (nonZeroes.size() == 4) {
                return HandType.ONE_PAIR;
            } else if (nonZeroes.size() == 3) {
                if (nonZerosCounts.get(0) == 2 && nonZerosCounts.get(1) == 2) {
                    return HandType.TWO_PAIR;
                } else if (nonZerosCounts.get(0) == 3) {
                    return HandType.THREE_OF_A_KIND;
                }
            } else if (nonZeroes.size() == 2) {
                // four of a kind or full-house
                if (nonZerosCounts.get(0) == 4) {
                    return HandType.FOUR_OF_A_KIND;
                } else if (nonZerosCounts.get(0) == 3) {
                    return HandType.FULL_HOUSE;
                }
            } else if (nonZeroes.size() == 1) {
                return HandType.FIVE_OF_A_KIND;
            }
            throw new IllegalStateException("oh noes: " + this.cards());
        }

        static Hand fromLine(String line) {
            var split = line.split(" ");
            if (split.length != 2) {
                throw new IllegalArgumentException("oh noes: " + line);
            }
            var cards = split[0].chars().mapToObj(c -> (char) c).map(Card::fromChar).toList();
            int bid = Integer.parseInt(split[1].replace(" ", ""));
            return new Hand(cards, bid);
        }

        @Override
        public String toString() {
            return "Hand{" +
                    "cards=" + cards +
                    ", bid=" + bid +
                    ", handType=" + getHandType() +
                    '}';
        }
    }


    record Puzzle(List<Hand> hands) {
        static Puzzle build(List<String> lines) {
            return new Puzzle(lines.stream().map(Hand::fromLine).toList());
        }

        int getWinnings() {
            var sorted = hands.stream().sorted(handComparator).toList();
            return IntStream.range(1, sorted.size() + 1).mapToObj(i -> i * sorted.get(i - 1).bid()).mapToInt(i -> i).sum();
        }
    }

    public static void main(String[] args) {
        var puzzle = Puzzle.build(FileUtils.readAllLines(Paths.get("data", "day7.txt")));
        LOGGER.info("Part 1: {}", puzzle.getWinnings());
        LOGGER.info("Part 2: ");
    }

}
