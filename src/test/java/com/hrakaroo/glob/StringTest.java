package com.hrakaroo.glob;


import org.junit.jupiter.api.Test;

/**
 * This tests if we turn the glob and match one character matching off.
 */
public class StringTest {

    private static final String pattern = "my%dog%_spot";


    @Test
    public void testMatch() {

        MatchingEngine matchingEngine = GlobPattern.compile(pattern,
                GlobPattern.NULL_CHARACTER, GlobPattern.NULL_CHARACTER, GlobPattern.HANDLE_ESCAPES);

        // Verify a basic string compare matches
        assert (matchingEngine.matches(pattern));
    }

    @Test
    public void testMatchFail() {

        MatchingEngine matchingEngine = GlobPattern.compile(pattern,
                GlobPattern.NULL_CHARACTER, GlobPattern.NULL_CHARACTER, GlobPattern.HANDLE_ESCAPES);

        // Verify if we swap the '%' for a 'r';
        assert (! matchingEngine.matches(pattern.replace('%', 'r')));
    }


    @Test
    public void testMatchFail2() {

        MatchingEngine matchingEngine = GlobPattern.compile(pattern,
                GlobPattern.NULL_CHARACTER, GlobPattern.NULL_CHARACTER, GlobPattern.HANDLE_ESCAPES);

        // Verify if we swap the '_' for a 'r';
        assert (! matchingEngine.matches(pattern.replace('_', 'r')));
    }


    @Test
    public void testMatchFailCase() {

        MatchingEngine matchingEngine = GlobPattern.compile(pattern,
                GlobPattern.NULL_CHARACTER, GlobPattern.NULL_CHARACTER, GlobPattern.HANDLE_ESCAPES);

        // Upper case the pattern
        assert (! matchingEngine.matches(pattern.toUpperCase()));
    }
}
