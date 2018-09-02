package org.semanticweb.vlog4j.core.reasoner.implementation;

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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.semanticweb.vlog4j.core.reasoner.FileDataSourceUtils;

public class CsvFileDataSourceTest {

	@Test
	public void testToConfigString() throws IOException {
		File csvFile = new File(FileDataSourceUtils.INPUT_FOLDER + "file.csv");
		CsvFileDataSource csvFileDataSource = new CsvFileDataSource(csvFile);

		final String expectedConfigString = "EDB%1$d_predname=%2$s\n" + "EDB%1$d_type=INMEMORY\n" + "EDB%1$d_param0="
				+ new File(csvFile.getParent()).getCanonicalPath() + "\n" + "EDB%1$d_param1=file\n";
		assertEquals(expectedConfigString, csvFileDataSource.toConfigString());

		csvFile = new File(FileDataSourceUtils.INPUT_FOLDER + "file.csv.gz");
		csvFileDataSource = new CsvFileDataSource(csvFile);
		assertEquals(expectedConfigString, csvFileDataSource.toConfigString());
	}

	@Test
	public void getFileNameWithoutExtension() throws IOException {
		CsvFileDataSource csvFileDataSource = new CsvFileDataSource(
				new File(FileDataSourceUtils.INPUT_FOLDER + "file.csv"));
		assertEquals("file", csvFileDataSource.getFileNameWithoutExtension());

		csvFileDataSource = new CsvFileDataSource(new File(FileDataSourceUtils.INPUT_FOLDER + "file.csv.gz"));
		assertEquals("file", csvFileDataSource.getFileNameWithoutExtension());
	}

	@Test(expected = NullPointerException.class)
	public void fileNameNotNull() throws IOException {
		new CsvFileDataSource(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void fileNameEndsWithCsv() throws IOException {
		new CsvFileDataSource(new File("invalid/file/name"));
	}

}
