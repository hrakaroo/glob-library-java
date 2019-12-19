package com.hrakaroo.glob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EverythingEngineTest {

    private TestUtils testUtils;

    @BeforeEach
    public void init() {
        testUtils = new TestUtils('%', '_', GlobPattern.CASE_INSENSITIVE | GlobPattern.HANDLE_ESCAPES);
    }

    @Test
    public void null_noMatch() {
        testUtils.matches(EverythingEngine.class, "%", null, false);
    }

    @Test
    public void empty_match() {
        testUtils.matches(EverythingEngine.class, "%", "", true);
    }

    @Test
    public void length1_match() {
        testUtils.matches(EverythingEngine.class, "%", "a", true);
    }

    @Test
    public void length2_match() {
        testUtils.matches(EverythingEngine.class, "%", "ab", true);
    }

}
