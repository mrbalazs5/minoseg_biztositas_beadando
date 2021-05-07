/*
 * ComponentConnectionTest
 *
 * v1.0
 *
 * 2021.04.05
 *
 */

package test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import main.ComponentConnection;

/**
 * Test of the ComponentConnection class.
 */
class ComponentConnectionTest {

    @Test
    void createSuccessfullyWithValidDataTest() {
	ComponentConnection component = new ComponentConnection(1, 2);
	
	assertEquals(component.id1, 1);
	assertEquals(component.id2, 2);
    }
   
}
