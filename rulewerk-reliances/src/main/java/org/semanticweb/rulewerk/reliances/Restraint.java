package org.semanticweb.rulewerk.reliances;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.rulewerk.core.model.api.ExistentialVariable;

/*-
 * #%L
 * Rulewerk Reliances
 * %%
 * Copyright (C) 2018 - 2020 Rulewerk Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.semanticweb.rulewerk.core.model.api.Literal;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.api.TermType;
import org.semanticweb.rulewerk.utils.Filter;
import org.semanticweb.rulewerk.utils.LiteralList;
import org.semanticweb.rulewerk.utils.SubsetIterable;

public class Restraint {

	static private boolean isRule1Applicable(Rule rule1RWU, Rule rule2RWU) {
		List<Literal> instance = new ArrayList<>();
		List<Literal> query = new ArrayList<>();
		rule1RWU.getPositiveBodyLiterals().forEach(literal -> instance.add(Instantiator.instantiateFact(literal)));
		rule1RWU.getHead().getLiterals().forEach(literal -> query.add(Instantiator.instantiateQuery(literal)));
		rule2RWU.getPositiveBodyLiterals().forEach(literal -> instance.add(Instantiator.instantiateFact(literal)));
		rule2RWU.getHead().getLiterals().forEach(literal -> instance.add(Instantiator.instantiateFact(literal)));

		return !SBCQ.query(instance, query);
	}

	static private boolean isheadAtoms21mappableToheadAtoms11(List<Literal> headAtoms11, List<Literal> headAtoms21) {
		List<Literal> instance = new ArrayList<>();
		List<Literal> query = new ArrayList<>();
		headAtoms11.forEach(literal -> instance.add(Instantiator.instantiateFact(literal)));
		headAtoms21.forEach(literal -> query.add(Instantiator.instantiateQuery(literal)));

		return SBCQ.query(instance, query);
	}

	/**
	 * 
	 * @return true if an universal variable from head21 is being mapped into an
	 *         existential variable from head11, which makes the unifier invalid.
	 */
	static private boolean mappingUniversalintoExistential(List<PositiveLiteral> headAtomsRule2,
			List<PositiveLiteral> headAtomsRule1, Assignment assignment) {

		for (Match match : assignment.getMatches()) {
			List<Term> fromHead2 = headAtomsRule2.get(match.getOrigin()).getArguments();
			List<Term> fromHead1 = headAtomsRule1.get(match.getDestination()).getArguments();

			for (int i = 0; i < fromHead2.size(); i++) {
				if (fromHead2.get(i).getType() == TermType.UNIVERSAL_VARIABLE
						&& fromHead1.get(i).getType() == TermType.EXISTENTIAL_VARIABLE) {
					return true;
				}
			}
		}
		return false;
	}

	// this must be true to have a restrain
	static private boolean conditionForExistentialVariables(List<Literal> headAtomsRule2, List<Literal> headAtomsRule1,
			List<Literal> headAtoms22, Assignment assignment) {

		Set<ExistentialVariable> extVarsIn22 = LiteralList.getExistentialVariables(headAtoms22);

		for (Match match : assignment.getMatches()) {

			List<Term> origin = headAtomsRule2.get(match.getOrigin()).getArguments();
			List<Term> destination = headAtomsRule1.get(match.getDestination()).getArguments();

			for (int i = 0; i < origin.size(); i++) {
				if (origin.get(i).getType() == TermType.EXISTENTIAL_VARIABLE
						&& destination.get(i).getType() == TermType.EXISTENTIAL_VARIABLE
						&& extVarsIn22.contains(origin.get(i))) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checker for restraining relation.
	 * 
	 * @param rule1
	 * @param rule2
	 * @return True if rule1 restraints rule1.
	 */
	static public boolean restraint(Rule rule1, Rule rule2) {
		// if rule2 is Datalog, it can not be restrained
		if (rule2.getExistentialVariables().count() == 0) {
			return false;
		}

		if (rule1.equals(rule2)) {
			return SelfRestraint.restraint(rule1);
		}

		Rule renamedRule1 = SuffixBasedVariableRenamer.rename(rule1, 1);
		Rule renamedRule2 = SuffixBasedVariableRenamer.rename(rule2, 2);

		List<PositiveLiteral> headAtomsRule1 = renamedRule1.getHead().getLiterals();
		List<PositiveLiteral> headAtomsRule2 = renamedRule2.getHead().getLiterals();

		List<ExistentialVariable> existentialVariables = renamedRule2.getExistentialVariables()
				.collect(Collectors.toList());

		// Iterate over all subsets of existentialVariables
		for (List<ExistentialVariable> extVarComb : new SubsetIterable<ExistentialVariable>(existentialVariables)) {

			if (extVarComb.size() > 0) {
				List<Integer> literalsContainingExtVarsIdxs = LiteralList
						.idxOfLiteralsContainingExistentialVariables(headAtomsRule2, extVarComb);
				// Iterate over all subsets of literalsContainingExtVarsIdxs. Because it could
				// be that we need to match only one of the literals
				for (List<Integer> literaltoUnifyIdx : new SubsetIterable<Integer>(literalsContainingExtVarsIdxs)) {

					if (literaltoUnifyIdx.size() > 0) {
						AssignmentIterable assignmentIterable = new AssignmentIterable(literaltoUnifyIdx.size(),
								headAtomsRule1.size());
						// Iterate over all possible assignments of those Literals
						for (Assignment assignment : assignmentIterable) {

							// We transform the assignment to keep the old indexes
							Assignment transformed = new Assignment(assignment, literaltoUnifyIdx,
									headAtomsRule2.size());

							List<Integer> headAtoms11Idx = transformed.indexesInAssignedListToBeUnified();
							List<Integer> headAtoms21Idx = transformed.indexesInAssigneeListToBeUnified();
							List<Integer> headAtoms22Idx = transformed.indexesInAssigneeListToBeIgnored();

							MartelliMontanariUnifier unifier = new MartelliMontanariUnifier(headAtomsRule2,
									headAtomsRule1, transformed);

							// RWU = renamed with unifier
							if (unifier.success) {
								UnifierBasedVariableRenamer renamer = new UnifierBasedVariableRenamer(unifier, false);

								// rename everything
								Rule rule1RWU = renamer.rename(renamedRule1);
								Rule rule2RWU = renamer.rename(renamedRule2);

								List<Literal> headAtomsRule1RWU = new ArrayList<>();
								headAtomsRule1.forEach(literal -> headAtomsRule1RWU.add(renamer.rename(literal)));

								List<Literal> headAtomsRule2RWU = new ArrayList<>();
								headAtomsRule2.forEach(literal -> headAtomsRule2RWU.add(renamer.rename(literal)));

								List<Literal> headAtoms11 = Filter.indexBased(headAtomsRule1RWU, headAtoms11Idx);
								List<Literal> headAtoms21 = Filter.indexBased(headAtomsRule2RWU, headAtoms21Idx);
								List<Literal> headAtoms22 = Filter.indexBased(headAtomsRule2RWU, headAtoms22Idx);

								if (isRule1Applicable(rule1RWU, rule2RWU)
										&& !mappingUniversalintoExistential(headAtomsRule2, headAtomsRule1, transformed)
										&& conditionForExistentialVariables(headAtomsRule2RWU, headAtomsRule1RWU,
												headAtoms22, transformed)
										&& isheadAtoms21mappableToheadAtoms11(headAtoms11, headAtoms21)) {
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

}
