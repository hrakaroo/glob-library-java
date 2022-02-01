package com.hrakaroo.glob;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EscapeTest {

    @Test
    public void testUnicode() {
        String pattern = "foo\\u0010bar";

        MatchingEngine matchingEngine = GlobPattern.compile(pattern, '%', '_', GlobPattern.CASE_INSENSITIVE | GlobPattern.HANDLE_ESCAPES);
        assert (matchingEngine.matches("foo\u0010bar"));

    }

    @Test
    public void testBadUnicode() {
        String pattern = "foo\\u001zbar";

        Assertions.assertThrows(RuntimeException.class, () -> GlobPattern.compile(pattern, '%', '_', GlobPattern.CASE_INSENSITIVE | GlobPattern.HANDLE_ESCAPES));
    }

    @Test
    public void testBadUnicodeAtEnd() {
        String pattern = "foo\\u001";

        Assertions.assertThrows(RuntimeException.class, () -> GlobPattern.compile(pattern, '%', '_', GlobPattern.CASE_INSENSITIVE | GlobPattern.HANDLE_ESCAPES));
    }

    @Test
    public void testBadEscape() {
        String pattern = "foo\\e";

        Assertions.assertThrows(RuntimeException.class, () -> GlobPattern.compile(pattern, '%', '_', GlobPattern.CASE_INSENSITIVE | GlobPattern.HANDLE_ESCAPES));
    }

    @Test
    public void testEscapeAll() {
        String pattern = "\\\\\\%_\\_";
        MatchingEngine matchingEngine = GlobPattern.compile(pattern, '%', '_', GlobPattern.ESCAPE_ALL);
        assert (matchingEngine.matches("\\%x_"));
        assert (!matchingEngine.matches("\\%xy"));
        assert (!matchingEngine.matches("\\zx_"));
    }

}
