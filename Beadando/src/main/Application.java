/*
 * Application
 *
 * v1.0
 *
 * 2021.04.05
 *
 */


package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main entry class of the application.
 */
public class Application {

    public static void main(String[] args) throws IOException {
	try {
	    List<Component> inputComponents = new ArrayList<Component>();
	    
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

	    List<ComponentConnection> matchedComponents =
		    new ArrayList<ComponentConnection>();

	    List<List<ComponentConnection>> separatedMatchedComponents =
		    new ArrayList<List<ComponentConnection>>();

	    Application app = new Application();

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

	    matchedComponents = app.matchInputAndErroneousComponents(
		    inputComponents,
		    inputErroneousComponents
	    );

	    separatedMatchedComponents = app.separateMatchedComponentsIntoLists(
		    inputErroneousComponents,
		    matchedComponents
	    );

	    List<List<ComponentConnection>> matchableErroneousComponents = 
		    app.getComponentCombinations(separatedMatchedComponents);

	    List<List<ComponentConnection>> matchedErroneousComponents = 
		    app.findMatchingErroneouseComponents(
			    matchableErroneousComponents,
			    inputErroneousConnections, inputConnections
		    );

	    int numReplacements = app.countComponentReplacements(
		    inputErroneousComponents,
		    inputReplacementComponents,
		    matchedErroneousComponents
	    );

	    String stringOutput = app.createOutputFileContent(
		    numReplacements,
		    matchedErroneousComponents
	    );

	    app.writeIntoFile("outputs/G0.out", stringOutput);

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Generates the content of the output file.
     */
    public String createOutputFileContent(
	    int numReplacements,
	    List<List<ComponentConnection>> matchedErroneousComponents
    ) {
	int replacements = numReplacements > 0 ? numReplacements : -1;
	String stringOutput = "";
	
	List<ComponentConnection> firstMatchedErroneousComponent = 
		(matchedErroneousComponents.size() > 0)
		? matchedErroneousComponents.get(0) 
		: null;


	stringOutput += String.valueOf(replacements) + "\n";

	if (firstMatchedErroneousComponent != null) {
	    for (ComponentConnection matchedComponent : firstMatchedErroneousComponent) {
		stringOutput += matchedComponent + ";";
	    }
	}

	System.out.println(stringOutput);

	return stringOutput;
    }

    /**
     * Counts the number of replacements in matched input components
     * based on the replacement components.
     */
    public int countComponentReplacements(
	    List<Component> inputErroneousComponents,
	    List<Component> inputReplacementComponents,
	    List<List<ComponentConnection>> matchedErroneousComponents
    ) {
	int numReplacements = 0;

	for (int i = 0; i < inputErroneousComponents.size(); i++) {
	    if (i < inputReplacementComponents.size()) {
		int inputErrComponentId = inputErroneousComponents.get(i).id;
		int inputRepComponentId = inputReplacementComponents.get(i).id;
		
		if (inputErrComponentId != inputRepComponentId) {
		    int toBeReplacedId = inputErroneousComponents.get(i).id;
		    List<Integer> alreadyReplacedIndices = new ArrayList<Integer>();

		    for (List<ComponentConnection> filteredResult : matchedErroneousComponents) {
			for (ComponentConnection connection : filteredResult) {
			    if ((connection.id1 == toBeReplacedId)
				    && !(alreadyReplacedIndices.contains(connection.id2))
			    ) {
				numReplacements++;
				alreadyReplacedIndices.add(connection.id2);
				break;
			    }
			}
		    }
		}
	    } else {
		break;
	    }
	}

	return numReplacements;
    }

    /**
     * Filter matchable components which are not in
     * the input connection list.
     */
    public List<List<ComponentConnection>> findMatchingErroneouseComponents(
	    List<List<ComponentConnection>> matchableErroneousComponents,
	    List<ComponentConnection> inputErroneousConnections,
	    List<ComponentConnection> inputConnections
    ) {
	return matchableErroneousComponents.parallelStream().filter(result -> {
	    try {
		List<ComponentConnection> cloneList = 
			this.cloneComponentConnectionList(
				inputErroneousConnections);

		for (ComponentConnection connection : result) {
		    this.replaceConnections(cloneList, connection);
		}

		return this.checkIfEveryElementIsValidInputConnection(cloneList,
			inputConnections);
	    } catch (CloneNotSupportedException e) {
		return false;
	    }
	}).collect(Collectors.toList());
    }

    /**
     * Separate erroneous components based on their type.
     */
    public List<List<ComponentConnection>> separateMatchedComponentsIntoLists(
	    List<Component> inputErroneousComponents,
	    List<ComponentConnection> matchedComponents
    ) {
	List<List<ComponentConnection>> separatedMatchedComponents =
		new ArrayList<List<ComponentConnection>>();

	for (Component inputErroneousComponent : inputErroneousComponents) {
	    final List<ComponentConnection> separatedPossibleResult = 
		    matchedComponents
    		    	.stream()
    		    	.filter(pr -> pr.id1 == inputErroneousComponent.id)
    		    	.collect(Collectors.toList());
	    
	    separatedMatchedComponents.add(separatedPossibleResult);
	}

	return separatedMatchedComponents;
    }

    /**
     * Match input and erroneous components based on their type
     * and collect them into a list.
     */
    public List<ComponentConnection> matchInputAndErroneousComponents(
	    List<Component> inputComponents,
	    List<Component> inputErroneousComponents
    ) {
	List<ComponentConnection> resultMatchedComponents =
		new ArrayList<ComponentConnection>();
	
	for (Component inputErroneousComponent : inputErroneousComponents) {
	    List<Component> matchedComponents = 
		    inputComponents
		    	.stream()
		    	.filter(c -> c.typeCode == inputErroneousComponent.typeCode)
		    	.collect(Collectors.toList());
	    
	    for (Component matchedComponent : matchedComponents) {
		resultMatchedComponents.add(
			new ComponentConnection(
				inputErroneousComponent.id, matchedComponent.id
			)
		);
	    }
	}

	return resultMatchedComponents;
    }

    /**
     * Replace connection components with a given element.
     */
    public void replaceConnections(
	    List<ComponentConnection> baseList,
	    ComponentConnection element
    ) {
	for (ComponentConnection listElement : baseList) {
	    if (!(listElement.replacedIndices.contains(0))
		    && (listElement.id1 == element.id1)
	    ) {
		listElement.id1 = element.id2;
		listElement.replacedIndices.add(0);
	    }

	    if (!(listElement.replacedIndices.contains(1)) 
		    && (listElement.id2 == element.id1)
	    ) {
		listElement.id2 = element.id2;
		listElement.replacedIndices.add(1);
	    }
	}
    }


    /**
     * Deep clones a ComponentConnection list into a new clone list.
     */
    public List<ComponentConnection> cloneComponentConnectionList(
	    List<ComponentConnection> cloneableConnectionList
    ) throws CloneNotSupportedException {
	Iterator<ComponentConnection> iterator = 
		cloneableConnectionList.iterator();
	List<ComponentConnection> connectionListClone =
		new ArrayList<ComponentConnection>();

	while (iterator.hasNext()) {
	    connectionListClone
		    .add((ComponentConnection) iterator.next().clone());
	}

	return connectionListClone;
    }


    /**
     * Performs a Cartesian product on the given ComponentConnection list.
     */
    public List<List<ComponentConnection>> getComponentCombinations(
	    List<List<ComponentConnection>> lists) {
	long size = 1;
	final List<List<ComponentConnection>> copy =
		new ArrayList<List<ComponentConnection>>();
	
	for (List<ComponentConnection> list : lists) {
	    size *= list.size();
	    copy.add(new ArrayList<ComponentConnection>(list));
	}
	
	final int fSize = (int) size;
	
	return new AbstractList<List<ComponentConnection>>() {
	    @Override
	    public int size() {
		return fSize;
	    }

	    @Override
	    public List<ComponentConnection> get(int i) {
		ComponentConnection[] arr = 
			new ComponentConnection[copy.size()];
		
		for (int j = copy.size() - 1; j >= 0; j--) {
		    List<ComponentConnection> list = copy.get(j);
		    arr[j] = list.get(i % list.size());
		    i /= list.size();
		}
		
		return Arrays.asList(arr);
	    }
	};
    }

    /**
     * Validates that every element of a given list 
     * is in the input connection list.
     */
    public boolean checkIfEveryElementIsValidInputConnection(
	    List<ComponentConnection> toBeValidatedList,
	    List<ComponentConnection> inputConnections
    ) {
	for (ComponentConnection toBeValidated : toBeValidatedList) {
	    boolean isValid = false;
	    
	    for (ComponentConnection inputConnection : inputConnections) {
		if ((toBeValidated.id1 == inputConnection.id1)
			&& (toBeValidated.id2 == inputConnection.id2)) {
		    isValid = true;
		    break;
		}
	    }

	    if (!isValid) {
		return false;
	    }
	}
	
	return true;
    }

    /**
     * Writes the given content into a single file.
     */
    public void writeIntoFile(String fileName, String content) throws IOException {
	FileWriter file = new FileWriter(fileName);
	BufferedWriter bufferedWriter = new BufferedWriter(file);
	    
	try {
	    bufferedWriter.write(content);
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    bufferedWriter.close();
	}
    }

    /**
     * Parse input files to components and component connections.
     */
    public void parseComponentInputFile(
	    String fileName,
	    List<Component> inputComponents,
	    List<ComponentConnection> inputConnections
    ) throws IOException {
	BufferedReader bufferReader =
		new BufferedReader(new FileReader(fileName));
	
	try {
	    String line;
	    int lineIndex = 0;

	    while ((line = bufferReader.readLine()) != null) {
		// Read input components.
		if (lineIndex == 0) {
		    final String[] splittedPairs = line.split(";");

		    for (String pair : splittedPairs) {
			final String[] components = pair.split("-");

			inputComponents.add(
				new Component(
					Integer.parseInt(components[0]),
					Integer.parseInt(components[1])
				)
			);
		    }
		}

		// Read input component connections.
		if (lineIndex == 2) {
		    final String[] splittedPairs = line.split(";");

		    for (String pair : splittedPairs) {
			final String[] componentConnections = pair.split("-");

			inputConnections.add(
				new ComponentConnection(
					Integer.parseInt(componentConnections[0]),
					Integer.parseInt(componentConnections[1])
				)
			);
		    }
		}

		lineIndex++;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    bufferReader.close();
	}
    }

}
