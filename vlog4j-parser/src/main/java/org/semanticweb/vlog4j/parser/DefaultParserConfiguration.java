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

import org.semanticweb.vlog4j.parser.datasources.CsvFileDataSourceDeclarationHandler;
import org.semanticweb.vlog4j.parser.datasources.RdfFileDataSourceDeclarationHandler;
import org.semanticweb.vlog4j.parser.datasources.SparqlQueryResultDataSourceDeclarationHandler;

/**
 * Default parser configuration. Registers default data sources.
 *
 * @author Maximilian Marx
 */
public class DefaultParserConfiguration extends ParserConfiguration {
	public DefaultParserConfiguration() {
		super();
		registerDefaultDataSources();
	}

	/**
	 * Register built-in data sources (currently CSV, RDF, SPARQL).
	 */
	private void registerDefaultDataSources() {
		registerDataSource("load-csv", new CsvFileDataSourceDeclarationHandler());
		registerDataSource("load-rdf", new RdfFileDataSourceDeclarationHandler());
		registerDataSource("sparql", new SparqlQueryResultDataSourceDeclarationHandler());
	}
}