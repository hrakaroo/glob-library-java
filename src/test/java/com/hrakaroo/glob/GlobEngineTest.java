package com.hrakaroo.glob;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GlobEngineTest {

    private TestUtils testUtils;

    @BeforeEach
    public void init() {
        testUtils = new TestUtils('%', '_', GlobPattern.CASE_INSENSITIVE | GlobPattern.HANDLE_ESCAPES);
    }


    @Test
    public void null_noMatch() {
        testUtils.matches(GlobEngine.class, "%a%b%", null, false);
    }

    @Test
    public void escape_match() {
        testUtils.matches(GlobEngine.class, "%a%\\%b%", "%af%bd", true);
    }

    /**
     * This is intended to verify that our stack doubling is working, so we need a big pattern
     */
    @Test
    public void big_match() {
        String pattern = "%a%b%c%d%e%f%g%h%i%j%k%l%m%n%o%p%q%r%s%t%u%v";
        String string = "ababcabcdabcdeabcdeabcdefabcdefgabcdefghabcdefghiabcdefghijabcdefghijkabcdefghijkl" +
                "abcdefghijklmabcdefghijklmnabcdefghijklmnoabcdefghijklmnopabcdefghijklmnopqabcdefghijklmnopqr" +
                "abcdefghijklmnopqrsabcdefghijklmnopqrstabcdefghijklmnopqrstuabcdefghijklmnopqrstuv";
        testUtils.matches(GlobEngine.class, pattern, string, true);
    }

    @Test
    public void escape_r() {
        String pattern = "foo\\rbar";
        String string = "foo\rbar";
        testUtils.matches(EqualToEngine.class, pattern, string, true);
    }

    @Test
    public void escape_n() {
        String pattern = "foo\\nbar";
        String string = "foo\nbar";
        testUtils.matches(EqualToEngine.class, pattern, string, true);
    }

    @Test
    public void escape_t() {
        String pattern = "foo\\tbar";
        String string = "foo\tbar";
        testUtils.matches(EqualToEngine.class, pattern, string, true);
    }

    @Test
    public void escape_other() {
        String pattern = "foo\\\\bar";
        String string = "foo\\bar";
        testUtils.matches(EqualToEngine.class, pattern, string, true);
    }

    /**
     * Mostly a pointless test to get jacoco to shut up.  toString is not guaranteed and may change over time.
     */
    @Test
    public void pointless() {
        MatchingEngine m = GlobPattern.compile("a*b*c");

        String s = m.toString();
        assert (s.startsWith("GlobEngine"));
    }

    /**
     * Pointless test
     */
    @Test
    public void pointless2() {
        GlobPattern globPattern = new GlobPattern();

        // Check there are no public constructors ....
        assert (globPattern.getClass().getConstructors().length == 0);
    }
}
