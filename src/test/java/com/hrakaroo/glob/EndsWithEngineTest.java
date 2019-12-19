package com.hrakaroo.glob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EndsWithEngineTest {

    private TestUtils testUtils;

    @BeforeEach
    public void init() {
        testUtils = new TestUtils('%', '_', GlobPattern.CASE_INSENSITIVE | GlobPattern.HANDLE_ESCAPES);
    }

    @Test
    public void null_noMatch() {
        testUtils.matches(EndsWithEngine.class, "%a", null, false);
    }

    @Test
    public void empty_noMatch() {
        testUtils.matches(EndsWithEngine.class, "%a", "", false);
    }

    @Test
    public void length1_match() {
        testUtils.matches(EndsWithEngine.class, "%a", "a", true);
    }

    @Test
    public void length2_match() {
        testUtils.matches(EndsWithEngine.class, "%a", "ba", true);
    }

    @Test
    public void length1_noMatch() {
        testUtils.matches(EndsWithEngine.class, "%a", "b", false);
    }


    @Test
    public void test1() {
        testUtils.matches(EndsWithEngine.class, "%a_c", "badc", true);
    }

}


