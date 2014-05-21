package org.pssif.consistencyDataStructures;

import java.util.Map;

import org.pssif.comparedDataStructures.ComparedLabelPair;
import org.pssif.comparedDataStructures.ComparedNodePair;
import org.pssif.comparedDataStructures.ComparedNormalizedTokensPair;

import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.model.Model;

/**
 * @author Andreas
 * 
 *         This class stores all the information relevant for the consistency
 *         checking process. It Stores the two models that shall be compared and
 *         the according metamodel.
 * 
 *         With this class we know:
 * 
 *         - which IDs already matched (so we don't match them again) the
 *         - similarity results for token & label pairs to be able to look them up
 *         - for future results the similarity results for node pairs
 */
public class ConsistencyData {
	
	public ConsistencyData() {

	}

	/**
	 * stores the already compared IDs as the pair (originModelElementID,
	 * newModelElementID)
	 */
	private Map<String, String> IDMapping;

	/**
	 * stores the label pairs which were already matched together with the
	 * similarity metric results
	 */
	private ComparedLabelPair[] comparedLabelPairs;

	/**
	 * stores the tokens pairs which were already matched together with the
	 * similarity metric results
	 */
	private ComparedNormalizedTokensPair[] comparedTokensPair;

	/**
	 * stores compared Nodes with similarity information
	 */
	private ComparedNodePair[] comparedNodePairs;

	
	/**
	 * @return true if the new compared element was added to all relevant variables
	 * @return false if something went wrong
	 */
	public boolean putComparedEntry(){
		return false;
	}
	
}