package org.pssif.mainProcesses;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.pssif.consistencyDataStructures.ConsistencyData;
import org.pssif.matchingLogic.MatchMethod;

import de.tum.pssif.core.common.PSSIFConstants;
import de.tum.pssif.core.common.PSSIFOption;
import de.tum.pssif.core.common.PSSIFValue;
import de.tum.pssif.core.metamodel.Attribute;
import de.tum.pssif.core.metamodel.Metamodel;
import de.tum.pssif.core.metamodel.NodeType;
import de.tum.pssif.core.metamodel.PSSIFCanonicMetamodelCreator;
import de.tum.pssif.core.model.Model;
import de.tum.pssif.core.model.Node;

/**
This file is part of PSSIF Consistency. It is responsible for keeping consistency between different requirements models or versions of models.
Copyright (C) 2014 Andreas Genz

    PSSIF Consistency is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    PSSIF Consistency is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with PSSIF Consistency.  If not, see <http://www.gnu.org/licenses/>.
    
    Feel free to contact me via eMail: genz@in.tum.de
*/

//TODO: Remove development Sys.outprintln calls!!!
//TODO: Add Try Catch blocks where necessary

/**
 * @author Andreas
 * 
 *         this class represents the main class of the PSSIF-consistency
 *         checker. It is responsible for the whole compairson process to run
 *         correctly. It ensures that all nodes of the one model are compaired
 *         once with each node of the other model. It also triggers the matching
 *         of two nodes. Nodes from the originalModel are compared with the
 *         newModel in an bottom-up-order.
 * 
 */
public class CompairsonProcess {

	// TODO extract to constants
	/**
	 * These are the subclasses of PSIFFDevArtifacts that are checked for
	 * consistency
	 */
	private final String[] PSIFFDevArtifactSubClasses = {
			PSSIFCanonicMetamodelCreator.N_FUNCTIONALITY,
			PSSIFCanonicMetamodelCreator.N_REQUIREMENT,
			PSSIFCanonicMetamodelCreator.N_USE_CASE,
			PSSIFCanonicMetamodelCreator.N_TEST_CASE,
			PSSIFCanonicMetamodelCreator.N_VIEW,
			PSSIFCanonicMetamodelCreator.N_EVENT,
			PSSIFCanonicMetamodelCreator.N_ISSUE,
			PSSIFCanonicMetamodelCreator.N_DECISION,
			PSSIFCanonicMetamodelCreator.N_CHANGE_EVENT };

	/**
	 * These are the subclasses of PSIFFSolArtifacts that are checked for
	 * consistency
	 */
	private final String[] PSIFFSolArtifactSubClasses = {
			PSSIFCanonicMetamodelCreator.N_BLOCK,
			PSSIFCanonicMetamodelCreator.N_FUNCTION,
			PSSIFCanonicMetamodelCreator.N_ACTIVITY,
			PSSIFCanonicMetamodelCreator.N_STATE,
			PSSIFCanonicMetamodelCreator.N_ACTOR,
			PSSIFCanonicMetamodelCreator.N_SERVICE,
			PSSIFCanonicMetamodelCreator.N_SOFTWARE,
			PSSIFCanonicMetamodelCreator.N_HARDWARE,
			PSSIFCanonicMetamodelCreator.N_MECHANIC,
			PSSIFCanonicMetamodelCreator.N_ELECTRONIC,
			PSSIFCanonicMetamodelCreator.N_MODULE };

	private Model originalModel, newModel;
	private Metamodel metaModelOriginal;

	/**
	 * the results of the compairson and matching process are all stored here
	 * and all processes access the same object
	 */
	private ConsistencyData consistencyData;

	/**
	 * this is the object responsible for conducting the matching of two nodes
	 */
	private MatchingProcess matchingProcess;

	private PSSIFOption<Node> nodesOriginalModel;

	public static ConsistencyData main(Model originalModel, Model newModel,
			Metamodel metaModelOriginal, Metamodel metaModelNew,
			List<MatchMethod> matchMethods) {
		CompairsonProcess compairsonProcess = new CompairsonProcess(
				originalModel, newModel, metaModelOriginal, metaModelNew,
				matchMethods);

		return compairsonProcess.consistencyData;
	}

	/**
	 * @param originalModel
	 *            the model firstly imported into the pssif modelling fw
	 * @param newModel
	 *            the recent model imported into the pssif modelling fw
	 * @param metaModelOriginal
	 *            the according metamodel for the original model
	 * @param metaModelNew
	 *            the according metamodel for the new model
	 * @param matchMethods
	 *            these are the matching methods for the coming matching phase
	 */
	public CompairsonProcess(Model originalModel, Model newModel,
			Metamodel metaModelOriginal, Metamodel metaModelNew,
			List<MatchMethod> matchMethods) {

		this.originalModel = originalModel;
		this.newModel = newModel;
		this.metaModelOriginal = metaModelOriginal;

		this.consistencyData = ConsistencyData.getInstance();

		this.matchingProcess = new MatchingProcess(originalModel, newModel,
				metaModelOriginal, metaModelNew, consistencyData, matchMethods,
				this);

		/**
		 * compairson process starts here
		 */
		this.startTypeAndNodeIteration();
	}

	/**
	 * this method checks if the original Model contains at least one node and
	 * then calls the typeIteration Method. If the original model is empty no
	 * merge can be conducted so no further methods are called for compairson
	 */
	public void startTypeAndNodeIteration() {
		NodeType rootNodeType = metaModelOriginal.getNodeType(
				PSSIFConstants.ROOT_NODE_TYPE_NAME).getOne();

		nodesOriginalModel = rootNodeType.apply(originalModel, true);

		if (nodesOriginalModel.isNone()) {
			// TODO: Alert the user that there are no nodes in the orignal model
			// to merge with the new one
			System.out.println("no nodes in the original model");
		} else {
			if (nodesOriginalModel.isOne()) {
				System.out.println("one node in the original model");
			} else if (nodesOriginalModel.isMany()) {
				System.out.println("many nodes in the original model");
			} else {
				throw new RuntimeException(
						"This can never happen. Maybe the structure of the root node type was changed");
			}
			typeIteration();

		}
	}

	/**
	 * this method iterates over every node type of the pssif metamodel and
	 * calls the method iterateNodesOfType() with the current type. This ensures
	 * that all nodes from the original model are compared with allfrom the
	 * other model.
	 * 
	 * It starts with the children of the Type DevelopmentArtifact and then
	 * continues with the children of the Type Solution Artifact. After that the
	 * Development and Solution Artifact Type and finally the Node Type are
	 * iterated.
	 */
	public void typeIteration() {

		for (int i = 0; i < PSIFFDevArtifactSubClasses.length; i++) {
			iterateNodesOfType(PSIFFDevArtifactSubClasses[i], false);
		}

		for (int i = 0; i < PSIFFSolArtifactSubClasses.length; i++) {
			iterateNodesOfType(PSIFFSolArtifactSubClasses[i], false);
		}

		iterateNodesOfType(PSSIFCanonicMetamodelCreator.N_DEV_ARTIFACT, false);

		iterateNodesOfType(PSSIFCanonicMetamodelCreator.N_SOL_ARTIFACT, false);
		iterateNodesOfType(PSSIFConstants.ROOT_NODE_TYPE_NAME, false);
	}

	/**
	 * this method gets all nodes from the original model with the given type.
	 * If there is no node with the type in the model the method is finished. If
	 * there is one node with the type in the model the node is given to the
	 * method iterateOverTypesOfNewModel(). If there are many nodes each node is
	 * given seperately to the method iterateOverTypesOfNewModel().
	 * 
	 * @param type
	 *            the type of nodes which are looked up in the original model to
	 *            do the compairson
	 * @param includeSubtypes
	 *            a bool specifying whether nodes with a subtype of type shall
	 *            too be part of the compairson process
	 * 
	 * 
	 * 
	 */
	public void iterateNodesOfType(String type, boolean includeSubtypes) {

		int nodeCount = 0;

		NodeType actTypeOriginModel;
		PSSIFOption<Node> actNodesOriginalModel;

		actTypeOriginModel = metaModelOriginal.getNodeType(type).getOne();

		actNodesOriginalModel = actTypeOriginModel.apply(originalModel,
				includeSubtypes);

		if (actNodesOriginalModel.isNone()) {
			System.out.println("There are no nodes of the type "
					+ actTypeOriginModel.getName()
					+ " in the original model. Continuing with the next type.");
		} else {
			if (actNodesOriginalModel.isOne()) {
				System.out
						.println("There is one node of the type "
								+ actTypeOriginModel.getName()
								+ " in the original model. Starting the matching for this node.");

				Node tempNodeOrigin = actNodesOriginalModel.getOne();

				iterateOverTypesOfNewModel(tempNodeOrigin, actTypeOriginModel);

				nodeCount++;

			} else {
				System.out
						.println("There are many nodes of the type "
								+ actTypeOriginModel.getName()
								+ " in the original model. Starting the matching for these nodes.");

				Set<Node> tempNodesOrigin = actNodesOriginalModel.getMany();

				Iterator<Node> tempNodeOrigin = tempNodesOrigin.iterator();

				while (tempNodeOrigin.hasNext()) {

					iterateOverTypesOfNewModel(tempNodeOrigin.next(),
							actTypeOriginModel);

					nodeCount++;
				}

			}
		}
		System.out.println("Found " + nodeCount + " unique nodes in the model");
	}

	/**
	 * This method is supposed to call the compairson process
	 * (matchNodeWithNodesOfActTypeOfNewModel()) between the given node and
	 * every node in the new model Therefore this method gets all nodes from the
	 * new model with the given type. Then the process works bottom-up. This
	 * means that first the node is compared with nodes of the same type. Then
	 * the node is compared with nodes of the next more general type until there
	 * is no more general type left.
	 * 
	 * @param tempNodeOrigin
	 *            the node that shall be compared to all nodes with the same
	 *            type in the new model
	 * @param actTypeOriginModel
	 *            the type of the given node
	 * 
	 * 
	 * 
	 */
	public void iterateOverTypesOfNewModel(Node tempNodeOrigin,
			NodeType actTypeOriginModel) {
		/**
		 * the actual type with which nodes to compare are searched in the new
		 * model
		 */
		NodeType actTypeNewModel = actTypeOriginModel;

		boolean firstIteration = true;

		// TODO: do a proper fix for this problem
		// QuickFIX only!!! (problem is: If the program shall compare a node of
		// type
		// 'node' with the other model
		// it's currently not matched with any other 'node' because the subtypes
		// are not included in the first iteration of the matching
		if (actTypeNewModel.getName() == PSSIFConstants.ROOT_NODE_TYPE_NAME) {
			firstIteration = false;
		}

		while (true) {

			PSSIFOption<Node> actNodesNewModel = actTypeNewModel.apply(
					newModel, !(firstIteration));

			matchNodeWithNodesOfActTypeOfNewModel(tempNodeOrigin,
					actNodesNewModel, actTypeOriginModel, actTypeNewModel);

			if (actTypeNewModel.getGeneral().isNone()) {
				break;
			} else {
				actTypeNewModel = actTypeNewModel.getGeneral().getOne();
			}

			firstIteration = false;
		}
	}

	/**
	 * this method checks if there are any nodes of the given type in the new
	 * model. If there are none there has to be no compairson done between the
	 * given node and nodes in the other model of the actual type. If there is
	 * one or many nodes with the actual type in the new model each node from
	 * the new model is compared separately with the node from the original
	 * model.
	 * 
	 * @param tempNodeOrigin
	 *            the node that shall be compared to all nodes with the actual
	 *            type in the new model
	 * @param actNodesNewModel
	 *            the nodes with the acutal type found in the new model
	 * @param actTypeOriginModel
	 *            the actual type of the originalNode
	 * 
	 * 
	 * 
	 * @param actTypeNewModel
	 *            the actual type with which nodes to compare are searched in
	 *            the new model
	 */
	public void matchNodeWithNodesOfActTypeOfNewModel(Node tempNodeOrigin,
			PSSIFOption<Node> actNodesNewModel, NodeType actTypeOriginModel,
			NodeType actTypeNewModel) {
		if (actNodesNewModel.isNone()) {
			System.out
					.println("There is no node in the new model of the type "
							+ actTypeNewModel.getName()
							+ " to match. Continuing with next node type from new model.");
		} else {
			if (actNodesNewModel.isOne()) {

				matchNodeWithNode(tempNodeOrigin, actNodesNewModel.getOne(),
						actTypeOriginModel, actTypeNewModel);

			} else {
				Set<Node> tempNodesNew = actNodesNewModel.getMany();

				Iterator<Node> tempNodeNew = tempNodesNew.iterator();

				while (tempNodeNew.hasNext()) {

					matchNodeWithNode(tempNodeOrigin, tempNodeNew.next(),
							actTypeOriginModel, actTypeNewModel);
				}

			}
		}
	}

	/**
	 * this method finally calls the appropriate matching functions between two
	 * nodes. Then a similarity score will be computed.
	 * 
	 * @param tempNodeOrigin
	 *            the node that shall be compared to the node from the new model
	 * @param tempNodeNew
	 *            the node which shall be compared to the node from the old
	 *            model
	 * @param actTypeOriginModel
	 *            the actual type of the originalNode
	 * 
	 * 
	 * 
	 * @param actTypeNewModel
	 *            the actual type of the node from the new model
	 */
	public void matchNodeWithNode(Node tempNodeOrigin, Node tempNodeNew,
			NodeType actTypeOriginModel, NodeType actTypeNewModel) {

		String globalIDNodeOrigin = Methods.findGlobalID(tempNodeOrigin,
				actTypeOriginModel);
		String globalIDNodeNew = Methods.findGlobalID(tempNodeNew,
				actTypeNewModel);

		/*
		 * System.out.println("Comparing original: " +
		 * Methods.findName(actTypeOriginModel, tempNodeOrigin) + " with ID: " +
		 * globalIDNodeOrigin + " of type " + actTypeOriginModel.getName() +
		 * " with new node: " + Methods.findName(actTypeNewModel, tempNodeNew) +
		 * " with ID: " + Methods.findGlobalID(tempNodeNew, actTypeNewModel) +
		 * " of type " + actTypeNewModel.getName());
		 */

		if (consistencyData.matchNecessary(globalIDNodeOrigin, globalIDNodeNew)) {
			matchingProcess.startMatchingProcess(tempNodeOrigin, tempNodeNew,
					actTypeOriginModel, actTypeNewModel);
		} else {
			// System.out.println("These two nodes have already been compared.");
		}

	}

}
