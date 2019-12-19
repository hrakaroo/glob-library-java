package com.hrakaroo.glob;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StartsWithEngineTest {

    private TestUtils testUtils;

    @BeforeEach
    public void init() {
        testUtils = new TestUtils('%', '_', GlobPattern.CASE_INSENSITIVE | GlobPattern.HANDLE_ESCAPES);
    }

    @Test
    public void null_noMatch() {
        testUtils.matches(StartsWithEngine.class, "a%", null, false);
    }

    @Test
    public void empty_noMatch() {
        testUtils.matches(StartsWithEngine.class, "a%", "", false);
    }

    @Test
    public void matchLength1_match() {
        testUtils.matches(StartsWithEngine.class, "a%", "a", true);
    }

    @Test
    public void matchLength2_match() {
        testUtils.matches(StartsWithEngine.class, "a%", "ab", true);
    }

    @Test
    public void noMatchLength1_noMatch() {
        testUtils.matches(StartsWithEngine.class, "a%", "b", false);
    }

    @Test
    public void noMatchLength2_noMatch() {
        testUtils.matches(StartsWithEngine.class, "a%", "ba", false);
    }

    @Test
    public void test() {
        testUtils.matches(StartsWithEngine.class, "abcdef%", "ab", false);
    }

    @Test
    public void test2() {
        testUtils.matches(StartsWithEngine.class, "abcde%", "abcdef", true);
    }

    @Test
    public void test3() {
        testUtils.matches(StartsWithEngine.class, "abcde%", "abcde", true);
    }

    @Test
    public void test4() {
        testUtils.matches(StartsWithEngine.class, "a_cde%", "abcde", true);
    }

}
