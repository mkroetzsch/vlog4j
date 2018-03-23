package org.semanticweb.vlog4j.core.reasoner.util;

/*-
 * #%L
 * VLog4j Core Components
 * %%
 * Copyright (C) 2018 VLog4j Developers
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

import org.semanticweb.vlog4j.core.model.api.Blank;
import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.core.model.api.TermVisitor;
import org.semanticweb.vlog4j.core.model.api.Variable;

public class TermToVLogConverter implements TermVisitor<karmaresearch.vlog.Term> {

	private karmaresearch.vlog.Term vLogTerm;

	public karmaresearch.vlog.Term getVLogTerm() {
		return this.vLogTerm;
	}

	@Override
	public karmaresearch.vlog.Term visit(Constant term) {
		this.vLogTerm = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT, term.getName());
		return this.vLogTerm;
	}

	@Override
	public karmaresearch.vlog.Term visit(Variable term) {
		this.vLogTerm = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.VARIABLE, term.getName());
		return this.vLogTerm;
	}

	@Override
	public karmaresearch.vlog.Term visit(Blank term) {
		this.vLogTerm = new karmaresearch.vlog.Term(karmaresearch.vlog.Term.TermType.CONSTANT, term.getName());
		return this.vLogTerm;
	}

}
