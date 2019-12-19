package com.hrakaroo.glob;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Tests to check that patterns are compiling correctly.
 */
public class CompileTest {

    private TestUtils sqlTestUtils;
    private TestUtils sqlTestUtilsCaseSensitive;

    @BeforeEach
    public void init() {
        sqlTestUtils = new TestUtils('%', '_', GlobPattern.CASE_INSENSITIVE | GlobPattern.HANDLE_ESCAPES);
        sqlTestUtilsCaseSensitive = new TestUtils('%', '_', GlobPattern.HANDLE_ESCAPES);
    }

    @Test
    public void nullPattern() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> GlobPattern.compile(null));
    }

    @Test
    public void sqlTest1() {
        sqlTestUtils.compile(EverythingEngine.class, "%%%", "%", "%", 0b01, 0b00);
    }

    @Test
    public void sqlTest2() {
        sqlTestUtils.compile(GlobEngine.class, "a%b", "A%B", "a%b", 0b010, 0b000);
    }

    @Test
    public void sqlTest3() {
        sqlTestUtils.compile(GlobEngine.class, "a%\\%%b", "A%%%B", "a%%%b", 0b01010, 0b000);
    }

    @Test
    public void sqlTest3a() {
        sqlTestUtils.compile(GlobEngine.class, "a%\\%%b", "A%%%B", "a%%%b", 0b01010, 0b000);
    }

    @Test
    public void sqlTest3b() {
        sqlTestUtils.compile(GlobEngine.class, "a%\\\\%b", "A%\\%B", "a%\\%b", 0b01010, 0b000);
    }

    @Test
    public void sqlTest4() {
        sqlTestUtils.compile(GlobEngine.class, "a%%%b", "A%B", "a%b", 0b010, 0b000);
    }

    @Test
    public void sqlTest5() {
        sqlTestUtils.compile(GlobEngine.class, "a%%_%b", "A%_%B", "a%_%b", 0b01010, 0b00100);
    }

    @Test
    public void sqlTest6() {
        sqlTestUtils.compile(GlobEngine.class, "%%%a%%_%b%a%_", "%A%_%B%A%_", "%a%_%b%a%_", 0b1010101010, 0b0001000001);
    }

    @Test
    public void sqlTest7() {
        sqlTestUtils.compile(GlobEngine.class, "%\\%%\\__\\_", "%%%___", "%%%___", 0b101000, 0b000010);
    }

    @Test
    public void sqlTest1CaseSensitive() {
        sqlTestUtilsCaseSensitive.compile(EverythingEngine.class, "%%%", "%", "%", 0b01, 0b00);
    }

    @Test
    public void sqlTest2CaseSensitive() {
        sqlTestUtilsCaseSensitive.compile(GlobEngine.class, "a%b", "a%b", "a%b", 0b010, 0b000);
    }

    @Test
    public void containsTest() {
        sqlTestUtils.compile(ContainsEngine.class, "%%f_o%", "%F_O%", "%f_o%", 0b10001, 0b00100);
    }

    @Test
    public void endsWithTest() {
        sqlTestUtils.compile(EndsWithEngine.class, "%f_o", "%F_O", "%f_o", 0b1000, 0b0010);
    }

    @Test
    public void startsWithTest() {
        sqlTestUtils.compile(StartsWithEngine.class, "f_o%", "F_O%", "f_o%", 0b0001, 0b0100);
    }

    @Test
    public void equalToTest() {
        sqlTestUtils.compile(EqualToEngine.class, "f_o", "F_O", "f_o", 0b000, 0b010);
    }

    @Test
    public void emptyOnlyTest() {
        sqlTestUtils.compile(EmptyOnlyEngine.class, "", "", "", 0b0, 0b0);
    }
}
