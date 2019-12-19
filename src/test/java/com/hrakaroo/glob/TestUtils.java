package com.hrakaroo.glob;


import static com.hrakaroo.glob.GlobEngine.STARTING_STACK_SIZE;

/**
 * Simple util class with some test helper methods.
 */
public class TestUtils {

    private final char glob;
    private final char blank;
    private final int flags;

    public TestUtils(char glob, char blank, int flags) {
        this.glob = glob;
        this.blank = blank;
        this.flags = flags;
    }

    /**
     * Verifies that the input string compiles to the given matching engine and verifies the result.
     *
     * @param clazz   The class of the engine we are expecting the pattern to compile into
     * @param pattern The pattern to compile
     * @param string  The string to test matching against
     * @param result  The expected result
     */
    protected void matches(Class<? extends MatchingEngine> clazz, String pattern, String string, boolean result) {

        // Run it directly
        {
            MatchingEngine m = GlobPattern.compile(pattern, glob, blank, flags);
            assert (m.getClass() == clazz);

            assert (m.matches(string) == result);
        }

        // If not case sensitive then run it again scrambled
        if ((flags & GlobPattern.CASE_INSENSITIVE) != 0) {
            pattern = scrambleCase(pattern, false);
            string = scrambleCase(string, true);

            MatchingEngine m = GlobPattern.compile(pattern, glob, blank, flags);
            assert (m.getClass() == clazz);

            assert (m.matches(string) == result);
        }
    }

    /**
     * Verify the patten compiled as expected.  We have again sacrificed test readability for code execution and
     * specifically not used abstract classes in the actual code.  This means we have to do some casting here to
     * get access to the values we want to check.
     *
     * @param clazz     The class the pattern should have compiled into
     * @param pattern   The input pattern
     * @param upperCase The expected upper case string from the compiled pattern
     * @param lowerCase The expected lower case string from the compiled pattern
     * @param globMask  The wildcard bit mask which should match the wildcard boolean array if applicable
     * @param blankMask The matchOne bit mask which should match the matchOne boolean array if applicable
     */
    protected void compile(Class<? extends MatchingEngine> clazz,
                           String pattern, String upperCase, String lowerCase,
                           int globMask, int blankMask) {

        MatchingEngine matchingEngine = GlobPattern.compile(pattern, glob, blank, flags);
        assert (matchingEngine.getClass() == clazz);

        int staticSize = matchingEngine.staticSizeInBytes();
        int matchingSize = matchingEngine.matchingSizeInBytes();

        // The matching size must be equal to or greater than the static size.
        assert (matchingSize >= staticSize);

        if (matchingEngine instanceof GlobEngine) {
            GlobEngine globEngine = (GlobEngine) matchingEngine;

            assert (strcmp(globEngine.upperCase, upperCase.toCharArray(), globEngine.length));
            assert (strcmp(globEngine.lowerCase, lowerCase.toCharArray(), globEngine.length));

            assert (cmp(globEngine.wildcard, globMask, globEngine.length));
            assert (cmp(globEngine.matchOne, blankMask, globEngine.length));

            assert (staticSize == globEngine.lowerCase.length * (Character.BYTES * 2 + 2) + Integer.BYTES);
            assert (matchingSize == staticSize + Integer.BYTES * 3 + Integer.BYTES * STARTING_STACK_SIZE);
        } else if (matchingEngine instanceof ContainsEngine) {
            ContainsEngine containsEngine = (ContainsEngine) matchingEngine;

            assert (staticSize == containsEngine.lowerCase.length * (Character.BYTES * 2 + 1) + Integer.BYTES);
            assert (matchingSize == staticSize + Integer.BYTES * 3);
        } else if (matchingEngine instanceof EndsWithEngine) {
            EndsWithEngine endsWithEngine = (EndsWithEngine) matchingEngine;

            assert (staticSize == endsWithEngine.lowerCase.length * (Character.BYTES * 2 + 1) + Integer.BYTES);
            assert (matchingSize == staticSize + Integer.BYTES * 2);
        } else if (matchingEngine instanceof StartsWithEngine) {
            StartsWithEngine startsWithEngine = (StartsWithEngine) matchingEngine;

            assert (staticSize == startsWithEngine.lowerCase.length * (Character.BYTES * 2 + 1) + Integer.BYTES);
            assert (matchingSize == staticSize + Integer.BYTES);
        } else if (matchingEngine instanceof EqualToEngine) {
            EqualToEngine equalToEngine = (EqualToEngine) matchingEngine;
            // Verify zero size
            assert (matchingEngine.staticSizeInBytes() == equalToEngine.lowerCase.length * (Character.BYTES * 2 + 1) + Integer.BYTES);
            assert (matchingEngine.matchingSizeInBytes() == staticSize + Integer.BYTES);
        } else if (matchingEngine instanceof EmptyOnlyEngine || matchingEngine instanceof EverythingEngine) {
            // Verify zero size
            assert (matchingEngine.staticSizeInBytes() == 0);
            assert (matchingEngine.matchingSizeInBytes() == 0);
        }
    }


    private String scrambleCase(String inString, boolean offset) {
        if (inString == null) {
            return null;
        }
        char[] chars = inString.toCharArray();

        for (int i=0; i<chars.length; ++i) {
            chars[i] = i%2 == (offset ? 1 : 0) ? Character.toLowerCase(chars[i]) : Character.toUpperCase(chars[i]);
        }

        return new String(chars);
    }


    /**
     * Helper method to compare a bit mask to a boolean array.  In order to be able to align the
     * two side by side the mask is compared against the reverse of the boolean array.
     * In other words, bools { true, false, false } matches 0b100.  This is easier to build out for
     * the caller but is actually counter to what you would code.
     *
     * @param bools  The boolean array to compare
     * @param mask   The mask to compare against the array
     * @param length The length to compare them
     * @return {@code true} if the two are equal, {@code false} otherwise.
     */
    private boolean cmp(boolean[] bools, int mask, int length) {
        int m = 1;
        for (int i = length - 1; i >= 0; --i) {
            if (bools[i]) {
                if ((mask & m) != m) {
                    return false;
                }
            }
            m = m << 1;
        }
        return true;
    }


    /**
     * Old school string comparison.  Needed because the char array from the compiled query may
     * be shorter than its array.length.
     *
     * @param a      Array a
     * @param b      Array b
     * @param length Length to compare a and b
     * @return {@code true} if arrays a and b are equal for the given length
     */
    private boolean strcmp(char[] a, char[] b, int length) {
        for (int i = 0; i < length; ++i) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }
}
