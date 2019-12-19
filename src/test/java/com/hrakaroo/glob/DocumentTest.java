package com.hrakaroo.glob;

import org.junit.jupiter.api.Test;

/**
 * Tests to keep us honest with our examples in the README.md
 */
public class DocumentTest {

    @Test
    public void example1() {
        MatchingEngine m = GlobPattern.compile("dog*cat\\*goat??");

        assert (m.matches("dog horse cat*goat!~"));
        assert (m.matches("dogcat*goat.."));
        assert (!m.matches("dog catgoat!/"));
    }

    @Test
    public void example2() {
        MatchingEngine m = GlobPattern.compile("dog%cat\\%goat__", '%', '_', GlobPattern.HANDLE_ESCAPES);

        assert (m.matches("dog horse cat%goat!~"));
        assert (m.matches("dogcat%goat.."));
        assert (!m.matches("dog catgoat!/"));
    }
}
