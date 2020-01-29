package org.semanticweb.vlog4j.parser;

/*-
 * #%L
 * vlog4j-parser
 * %%
 * Copyright (C) 2018 - 2019 VLog4j Developers
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

import org.semanticweb.vlog4j.core.model.api.Constant;
import org.semanticweb.vlog4j.parser.javacc.SubParserFactory;

/**
 * Handler for parsing a configurable literal expression.
 *
 * @author Maximilian Marx
 */
@FunctionalInterface
public interface ConfigurableLiteralHandler {
	/**
	 * Parse a Data Source Declaration.
	 *
	 * @param syntacticForm    syntactic form of the literal expression.
	 * @param subParserFactory a factory for obtaining a SubParser, sharing the
	 *                         parser's state, but bound to new input.
	 *
	 * @throws ParsingException when the given syntactic form is invalid.
	 * @return an appropriate @{link Constant} instance.
	 */
	public Constant parseLiteral(String syntacticForm, final SubParserFactory subParserFactory) throws ParsingException;
}