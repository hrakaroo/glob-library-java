package com.hrakaroo.glob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Test compiling the pattern '' into a EmptyEngine
 */
public class EmptyEngineTest {

    private TestUtils testUtils;

    @BeforeEach
    public void init() {
        testUtils = new TestUtils('%', '_', GlobPattern.CASE_INSENSITIVE | GlobPattern.HANDLE_ESCAPES);
    }

    @Test
    public void null_noMatch() {
        testUtils.matches(EmptyOnlyEngine.class, "", null, false);
    }

    @Test
    public void empty_match() {
        testUtils.matches(EmptyOnlyEngine.class, "", "", true);
    }

    @Test
    public void length1_noMatch() {
        testUtils.matches(EmptyOnlyEngine.class, "", "a", false);
    }

    @Test
    public void length2_noMatch() {
        testUtils.matches(EmptyOnlyEngine.class, "", "ab", false);
    }

}
