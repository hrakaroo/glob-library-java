package com.hrakaroo.glob;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EqualToEngineTest {

    private TestUtils testUtils;

    @BeforeEach
    public void init() {
        testUtils = new TestUtils('%', '_', GlobPattern.CASE_INSENSITIVE | GlobPattern.HANDLE_ESCAPES);
    }

    @Test
    public void patternHasEscapedGlob_match() { testUtils.matches(EqualToEngine.class, "ab\\%cd", "ab%cd", true);}

    @Test
    public void inputStringIsNull_noMatch() {
        testUtils.matches(EqualToEngine.class, "a", null, false);
    }

    @Test
    public void patternAndInputAreEqualLength1_match() {
        testUtils.matches(EqualToEngine.class, "a", "a", true);
    }

    @Test
    public void patternAndInputAreEqualLength2_match() {
        testUtils.matches(EqualToEngine.class, "ab", "ab", true);
    }

    @Test
    public void inputStringIsEmpty_noMatch() {
        testUtils.matches(EqualToEngine.class, "a", "", false);
    }

    @Test
    public void inputStringIsLongerThanPattern_noMatch() {
        testUtils.matches(EqualToEngine.class, "a", "aa", false);
    }

    @Test
    public void patternIsSingleBlank_noMatch() {
        testUtils.matches(EqualToEngine.class, "_", "", false);
    }

    @Test
    public void patternIsSingleBlank_match() {
        testUtils.matches(EqualToEngine.class, "_", "a", true);
    }

    @Test
    public void patternIsSingleBlank_noMatch2() {
        testUtils.matches(EqualToEngine.class, "_", "aa", false);
    }

    @Test
    public void patternStartsWithBlank_match() {
        testUtils.matches(EqualToEngine.class, "_b", "ab", true);
    }

    @Test
    public void patternEndsWithBlank_match() {
        testUtils.matches(EqualToEngine.class, "a_", "ab", true);
    }

    @Test
    public void patternIsTwoBlanks_match() {
        testUtils.matches(EqualToEngine.class, "__", "ab", true);
    }

    @Test
    public void patternHasTwoBlanksAtEnds_match() {
        testUtils.matches(EqualToEngine.class, "_b_", "abb", true);
    }

    /**
     * Test where the lengths are equal, but he strings differ after at the end
     */
    @Test
    public void patternDiffersFromInputAtEnd_noMatch() {
        testUtils.matches(EqualToEngine.class, "abc", "abd", false);
    }

    /**
     * Test where the lengths are equal, but he strings differ in the middle
     */
    @Test
    public void patternDiffersFromInputAtMiddle_noMatch() {
        testUtils.matches(EqualToEngine.class, "adc", "abc", false);
    }

    /**
     * Test where the lengths are equal, but he strings differ at the start
     */
    @Test
    public void patternDiffersFromInputAtStart_noMatch() {
        testUtils.matches(EqualToEngine.class, "bbc", "abc", false);
    }
}
