package com.hrakaroo.benchmark1;

import com.hrakaroo.glob.GlobPattern;
import com.hrakaroo.glob.MatchingEngine;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class Benchmark1 {

    private static final List<String> words = new ArrayList<>();
    private static final int wordsCount = 31207;
    private static final List<String> logLines = new ArrayList<>();
    private static final int logLinesCount = 27992;
    private static final String[] globPatterns = new String[]{
            "*a*c*",
            "*class*l*"
    };
    private static final String[] greedyRegexPattern = new String[]{
            "^.*a.*c.*$",
            "^.*class.*l.*$"
    };
    private static final String[] nonGreedyRegexPattern = new String[]{
            "^.*?a.*?c.*$",
            "^.*?class.*?l.*$"
    };
    private static final String[] matchingWords = new String[]{
            "abacination",
            "cherryblossom",
            "horologiography",
            "pluralistically"
    };


    static {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        // Read in the words file
        {
            InputStream inputStream = classLoader.getResourceAsStream("com/hrakaroo/benchmark/words");
            if (inputStream == null) {
                throw new RuntimeException("Could not find words");
            }
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            try (BufferedReader br = new BufferedReader(streamReader)) {
                String line;
                while ((line = br.readLine()) != null) {
                    words.add(line);
                }
            } catch (IOException e) {
                throw new RuntimeException("Couldn't read input words.");
            }
        }

        // Read in the logLines
        {
            InputStream inputStream = classLoader.getResourceAsStream("com/hrakaroo/benchmark/logLines");
            if (inputStream == null) {
                throw new RuntimeException("Could not find log lines");
            }
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            try (BufferedReader br = new BufferedReader(streamReader)) {
                String line;
                while ((line = br.readLine()) != null) {
                    logLines.add(line);
                }
            } catch (IOException e) {
                throw new RuntimeException("Couldn't read input logLines.");
            }
        }
    }


    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void globWords() {
        globTest(globPatterns, words, wordsCount);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void globLogLines() {
        globTest(globPatterns, logLines, logLinesCount);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void greedyRegexWords() {
        regexTest(greedyRegexPattern, words, wordsCount);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void greedyRegexLogLines() {
        regexTest(greedyRegexPattern, logLines, logLinesCount);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void nonGreedyRegexWords() {
        regexTest(nonGreedyRegexPattern, words, wordsCount);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void nonGreedyRegexLogLines() {
        regexTest(nonGreedyRegexPattern, logLines, logLinesCount);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void globCompare() {
        int count = 0;
        for (String word : matchingWords) {
            MatchingEngine m = GlobPattern.compile(word, '\0', '\0', 0);
            for (String s : words) {
                if (m.matches(s)) {
                    count += 1;
                }
            }
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void globCompareCaseInsensitive() {
        int count = 0;
        for (String word : matchingWords) {
            word = word.toUpperCase();
            MatchingEngine m = GlobPattern.compile(word, '\0', '\0', GlobPattern.CASE_INSENSITIVE);
            for (String s : words) {
                if (m.matches(s)) {
                    count += 1;
                }
            }
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void stringCompare() {
        int count = 0;
        for (String word : matchingWords) {
            for (String s : words) {
                if (word.equals(s)) {
                    count += 1;
                }
            }
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void stringCompareCaseInsensitive() {
        int count = 0;
        for (String word : matchingWords) {
            word = word.toUpperCase();
            for (String s : words) {
                if (word.equalsIgnoreCase(s)) {
                    count += 1;
                }
            }
        }
    }


    private void globTest(String[] globPatterns, List<String> lines, int assertCount) {
        int count = 0;
        for (String pattern : globPatterns) {
            MatchingEngine m = GlobPattern.compile(pattern, '*', '\0', GlobPattern.CASE_INSENSITIVE);
            for (String s : lines) {
                if (m.matches(s)) {
                    count += 1;
                }
            }
        }
        // To keep us honest
        if (count != assertCount) {
            throw new RuntimeException("Count was not equal " + count + " != " + assertCount);
        }
    }

    private void regexTest(String[] regexPatterns, List<String> lines, int assertCount) {
        int count = 0;
        for (String pattern : regexPatterns) {
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher("");
            for (String s : lines) {
                if (m.reset(s).matches()) {
                    count += 1;
                }
            }
        }
        // To keep us honest
        if (count != assertCount) {
            throw new RuntimeException("Count was not equal " + count + " != " + assertCount);
        }
    }
}
