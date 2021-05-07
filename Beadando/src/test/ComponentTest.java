/*
 * ComponentTest
 *
 * v1.0
 *
 * 2021.04.05
 *
 */

package test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import main.Component;

/**
 * Test of the Component class.
 */
class ComponentTest {

    @Test
    void createSuccessfullyWithValidDataTest() {
	Component component = new Component(1, 2);
	
	assertEquals(component.typeCode, 1);
	assertEquals(component.id, 2);
    }
   
}
