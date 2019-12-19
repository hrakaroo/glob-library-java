package com.hrakaroo.glob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ContainsEngineTest {

    private TestUtils testUtils;

    @BeforeEach
    public void init() {
        testUtils = new TestUtils('%', '_', GlobPattern.CASE_INSENSITIVE | GlobPattern.HANDLE_ESCAPES);
    }

    @Test
    public void null_noMatch() {
        testUtils.matches(ContainsEngine.class, "%a%", null, false);
    }

    @Test
    public void length3_match() {
        testUtils.matches(ContainsEngine.class, "%a%", "bac", true);
    }

    @Test
    public void escape_match() {
        testUtils.matches(ContainsEngine.class, "%a\\%%", "ba%dfo", true);
    }

    @Test
    public void length4a_match() {
        testUtils.matches(ContainsEngine.class, "%a%", "bacd", true);
    }

    @Test
    public void length4b_match() {
        testUtils.matches(ContainsEngine.class, "%a%", "dbac", true);
    }

    @Test
    public void empty_noMatch() {
        testUtils.matches(ContainsEngine.class, "%a%", "", false);
    }

    @Test
    public void length3_noMatch() {
        testUtils.matches(ContainsEngine.class, "%a%", "bdc", false);
    }

    /**
     * This test is intended to force a jump back in the matcher.
     */
    @Test
    public void length7_match() {
        testUtils.matches(ContainsEngine.class, "%abcd%", "abababcde", true);
    }



    @Test
    public void test1() {
        testUtils.matches(ContainsEngine.class, "%a_c%", "aabcc", true);
    }

}
