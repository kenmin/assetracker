/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package home.assetracker.data.util.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kenmin
 */
public class ReflectionUtilsTest {

    public ReflectionUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}

    @Test
    public void testGetProperty() {
        System.out.println("Test: ReflectionUtilsTest.testGetProperty");
        String expectedResult = "12345";
        String result = new TestObject("12345").getStringProperty();
        assertEquals(expectedResult, result);
    }

    private class TestObject {

        private String stringProperty;

        public TestObject() {
        }

        public TestObject(String stringProperty) {
            this.stringProperty = stringProperty;
        }

        public String getStringProperty() {
            return stringProperty;
        }

        public void setStringProperty(String stringProperty) {
            this.stringProperty = stringProperty;
        }
    }
}
