/*
 * ApplicationTest
 *
 * v1.0
 *
 * 2021.04.05
 *
 */

package test;

import main.Application;
import main.Component;
import main.ComponentConnection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;;

/**
 * Test of the Application class.
 */
class ApplicationTest {
    List<Component> inputComponents =
	    new ArrayList<Component>();
    List<ComponentConnection> inputConnections =
	    new ArrayList<ComponentConnection>();

    List<Component> inputErroneousComponents =
	    new ArrayList<Component>();
    List<ComponentConnection> inputErroneousConnections =
	    new ArrayList<ComponentConnection>();

    List<Component> inputReplacementComponents =
	    new ArrayList<Component>();
    List<ComponentConnection> inputReplacementConnections =
	    new ArrayList<ComponentConnection>();

    Application app = new Application();

    @BeforeEach
    void initAll() throws IOException {
	app.parseComponentInputFile(
		"inputs/GI0.in",
		inputComponents,
		inputConnections
	);
	app.parseComponentInputFile(
		"inputs/GC0.in",
		inputErroneousComponents,
		inputErroneousConnections
	);
	app.parseComponentInputFile(
		"inputs/GR0.in",
		inputReplacementComponents,
		inputReplacementConnections
	);
    }

    @Test
    void matchInputAndErroneousComponentsTest() {
	List<ComponentConnection> matchedComponents = 
		app.matchInputAndErroneousComponents(
			inputComponents,
			inputErroneousComponents
		);

	assertNotNull(matchedComponents);
	assertTrue(matchedComponents.get(0) instanceof ComponentConnection);
	assertTrue(matchedComponents.get(0).replacedIndices.size() == 0);
    }

    @Test
    void separateMatchedComponentsIntoListsTest() {
	List<ComponentConnection> matchedComponents = 
		app.matchInputAndErroneousComponents(
			inputComponents,
			inputErroneousComponents
		);

	List<List<ComponentConnection>> separatedMatchedComponents = 
		app.separateMatchedComponentsIntoLists(
			inputErroneousComponents,
			matchedComponents
		);

	assertNotNull(separatedMatchedComponents);

	assertTrue(separatedMatchedComponents.size() > 0);

	boolean isSameType = true;

	for (ComponentConnection component : separatedMatchedComponents.get(0)) {
	    int separatedCompId = separatedMatchedComponents.get(0).get(0).id1;
	    
	    if ((component.id1) != (separatedCompId)) {
		isSameType = false;
	    }
	}

	assertTrue(isSameType);
    }

    @Test
    void getComponentCombinationsTest() {
	List<ComponentConnection> matchedComponents = 
		app.matchInputAndErroneousComponents(
			inputComponents,
			inputErroneousComponents
		);

	List<List<ComponentConnection>> separatedMatchedComponents = 
		app.separateMatchedComponentsIntoLists(
			inputErroneousComponents,
			matchedComponents
		);

	List<List<ComponentConnection>> matchableErroneousComponents = 
		app.getComponentCombinations(separatedMatchedComponents);

	assertNotNull(matchableErroneousComponents);

	assertTrue(matchableErroneousComponents.size() > 0);

	boolean containsSameSizeOfValues = true;

	for (List<ComponentConnection> matchableErroneousComponent : matchableErroneousComponents) {
	    if (!(matchableErroneousComponent.size() == inputErroneousComponents.size())) {
		containsSameSizeOfValues = false;
		break;
	    }
	}

	assertTrue(containsSameSizeOfValues);
    }

    @Test
    void checkIfEveryElementIsValidInputConnectionTest() {
	List<ComponentConnection> successTestList = 
		new ArrayList<ComponentConnection>();
	successTestList.add(new ComponentConnection(1, 4));
	successTestList.add(new ComponentConnection(2, 4));
	successTestList.add(new ComponentConnection(3, 5));

	assertTrue(app.checkIfEveryElementIsValidInputConnection(
		successTestList, inputConnections));

	List<ComponentConnection> failedTestList = 
		new ArrayList<ComponentConnection>();
	failedTestList.add(new ComponentConnection(10, 11));
	failedTestList.add(new ComponentConnection(2, 7));
	failedTestList.add(new ComponentConnection(5, 9));

	assertFalse(
		app.checkIfEveryElementIsValidInputConnection(
			failedTestList,
			inputConnections
		)	
	);
    }

    @Test
    void findMatchingErroneouseComponentsTest() {
	List<ComponentConnection> matchedComponents = 
		app.matchInputAndErroneousComponents(
			inputComponents,
			inputErroneousComponents
		);

	List<List<ComponentConnection>> separatedMatchedComponents = 
		app.separateMatchedComponentsIntoLists(
			inputErroneousComponents,
			matchedComponents
		);

	List<List<ComponentConnection>> matchableErroneousComponents = 
		app.getComponentCombinations(separatedMatchedComponents);

	List<List<ComponentConnection>> matchedErroneousComponents = 
		app.findMatchingErroneouseComponents(
			matchableErroneousComponents,
			inputErroneousConnections,
			inputConnections
		);

	assertNotNull(matchedErroneousComponents);
    }

    @Test
    void createOutputFileContentTest() {
	int numReplacements = 5;
	List<List<ComponentConnection>> emptyMatchedErroneousComponents = 
		new ArrayList<List<ComponentConnection>>();

	String stringOutput1 = app.createOutputFileContent(
		numReplacements,
		emptyMatchedErroneousComponents
	);
	String stringOutput2 = app.createOutputFileContent(
		numReplacements,
		emptyMatchedErroneousComponents
	);

	int parsedNumReplacements = Integer
		.parseInt(stringOutput1.split("\n")[0]);

	assertNotNull(stringOutput1);
	assertEquals(stringOutput1, stringOutput2);

	assertEquals(parsedNumReplacements, numReplacements);

	List<List<ComponentConnection>> matchedErroneousComponents = 
		new ArrayList<List<ComponentConnection>>();
	List<ComponentConnection> matchedComponentList =
		new ArrayList<ComponentConnection>();
	matchedComponentList.add(new ComponentConnection(1, 4));
	matchedComponentList.add(new ComponentConnection(2, 6));
	matchedComponentList.add(new ComponentConnection(3, 2));
	matchedErroneousComponents.add(matchedComponentList);

	String stringOutput3 = app.createOutputFileContent(
		numReplacements,
		matchedErroneousComponents
	);

	assertNotNull(stringOutput3);
	assertNotEquals(stringOutput1, stringOutput3);

	String[] splittedOutput = stringOutput3.split("\n");

	parsedNumReplacements = Integer.parseInt(splittedOutput[0]);

	assertEquals(parsedNumReplacements, numReplacements);
	assertTrue(splittedOutput[1].length() > 0);
	assertEquals(splittedOutput.length, 2);
    }

    @Test
    void countComponentReplacementsTest() {
	List<ComponentConnection> matchedComponents = 
		app.matchInputAndErroneousComponents(
			inputComponents,
			inputErroneousComponents
		);

	List<List<ComponentConnection>> separatedMatchedComponents = 
		app.separateMatchedComponentsIntoLists(
			inputErroneousComponents,
			matchedComponents
		);

	List<List<ComponentConnection>> matchableErroneousComponents = 
		app.getComponentCombinations(separatedMatchedComponents);

	List<List<ComponentConnection>> matchedErroneousComponents = 
		app.findMatchingErroneouseComponents(
			matchableErroneousComponents,
			inputErroneousConnections,
			inputConnections
		);

	int numReplacements = app.countComponentReplacements(
		inputErroneousComponents,
		inputReplacementComponents,
		matchedErroneousComponents
	);

	assertEquals(numReplacements, 2);
    }

}
