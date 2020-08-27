package org.semanticweb.rulewerk.commands;

/*-
 * #%L
 * Rulewerk command execution support
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

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import org.junit.Test;
import org.mockito.Mockito;
import org.semanticweb.rulewerk.core.model.api.Command;
import org.semanticweb.rulewerk.core.model.api.Fact;
import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.Predicate;
import org.semanticweb.rulewerk.core.model.api.Rule;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.model.implementation.Expressions;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.parser.DefaultParserConfiguration;
import org.semanticweb.rulewerk.parser.ParserConfiguration;
import org.semanticweb.rulewerk.parser.ParsingException;

public class ClearCommandInterpreterTest {

	@Test
	public void correctUseAll_succeeds() throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = Mockito.spy(InterpreterTest.getMockInterpreter(writer));
		Term a = Expressions.makeAbstractConstant("a");
		Term x = Expressions.makeUniversalVariable("X");
		Predicate p = Expressions.makePredicate("p", 1);
		Predicate q = Expressions.makePredicate("q", 1);
		Predicate r = Expressions.makePredicate("r", 1);
		Fact fact = Expressions.makeFact(p, a);
		PositiveLiteral headLiteral = Expressions.makePositiveLiteral(q, x);
		PositiveLiteral bodyLiteral = Expressions.makePositiveLiteral(r, x);
		Rule rule = Expressions.makeRule(headLiteral, bodyLiteral);
		interpreter.getKnowledgeBase().addStatement(fact);
		interpreter.getKnowledgeBase().addStatement(rule);

		assertEquals(1, interpreter.getKnowledgeBase().getFacts().size());

		Command command = interpreter.parseCommand("@clear ALL .");
		interpreter.runCommand(command);

		assertTrue(interpreter.getKnowledgeBase().getFacts().isEmpty());
		assertTrue(interpreter.getKnowledgeBase().getRules().isEmpty());
		assertTrue(interpreter.getKnowledgeBase().getDataSourceDeclarations().isEmpty());
		Mockito.verify(interpreter).clearReasonerAndKnowledgeBase();
	}

	@Test
	public void correctUseInf_succeeds() throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		SimpleStyledPrinter printer = new SimpleStyledPrinter(writer);
		ParserConfiguration parserConfiguration = new DefaultParserConfiguration();
		final KnowledgeBase knowledgeBase = new KnowledgeBase();
		final Reasoner reasoner = Mockito.spy(Reasoner.class);
		Mockito.when(reasoner.getKnowledgeBase()).thenReturn(knowledgeBase);
		try (Interpreter interpreter = new Interpreter(() -> knowledgeBase, (kb) -> reasoner, printer,
				parserConfiguration)) {
			Term a = Expressions.makeAbstractConstant("a");
			Term x = Expressions.makeUniversalVariable("X");
			Predicate p = Expressions.makePredicate("p", 1);
			Predicate q = Expressions.makePredicate("q", 1);
			Predicate r = Expressions.makePredicate("r", 1);
			Fact fact = Expressions.makeFact(p, a);
			PositiveLiteral headLiteral = Expressions.makePositiveLiteral(q, x);
			PositiveLiteral bodyLiteral = Expressions.makePositiveLiteral(r, x);
			Rule rule = Expressions.makeRule(headLiteral, bodyLiteral);
			interpreter.getKnowledgeBase().addStatement(fact);
			interpreter.getKnowledgeBase().addStatement(rule);

			Command command = interpreter.parseCommand("@clear INF .");
			interpreter.runCommand(command);

			assertEquals(Arrays.asList(fact), interpreter.getKnowledgeBase().getFacts());
			assertEquals(Arrays.asList(rule), interpreter.getKnowledgeBase().getRules());
			assertTrue(interpreter.getKnowledgeBase().getDataSourceDeclarations().isEmpty());
			Mockito.verify(reasoner).resetReasoner();
		}
	}

	@Test
	public void correctUseFacts_succeeds() throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		try (Interpreter interpreter = InterpreterTest.getMockInterpreter(writer)) {
			Term a = Expressions.makeAbstractConstant("a");
			Term x = Expressions.makeUniversalVariable("X");
			Predicate p = Expressions.makePredicate("p", 1);
			Predicate q = Expressions.makePredicate("q", 1);
			Predicate r = Expressions.makePredicate("r", 1);
			Fact fact = Expressions.makeFact(p, a);
			PositiveLiteral headLiteral = Expressions.makePositiveLiteral(q, x);
			PositiveLiteral bodyLiteral = Expressions.makePositiveLiteral(r, x);
			Rule rule = Expressions.makeRule(headLiteral, bodyLiteral);
			interpreter.getKnowledgeBase().addStatement(fact);
			interpreter.getKnowledgeBase().addStatement(rule);

			Command command = interpreter.parseCommand("@clear FACTS .");
			interpreter.runCommand(command);

			assertTrue(interpreter.getKnowledgeBase().getFacts().isEmpty());
			assertEquals(Arrays.asList(rule), interpreter.getKnowledgeBase().getRules());
			assertTrue(interpreter.getKnowledgeBase().getDataSourceDeclarations().isEmpty());
		}
	}

	@Test
	public void correctUseRules_succeeds() throws ParsingException, CommandExecutionException, IOException {
		StringWriter writer = new StringWriter();
		try (Interpreter interpreter = InterpreterTest.getMockInterpreter(writer)) {
			Term a = Expressions.makeAbstractConstant("a");
			Term x = Expressions.makeUniversalVariable("X");
			Predicate p = Expressions.makePredicate("p", 1);
			Predicate q = Expressions.makePredicate("q", 1);
			Predicate r = Expressions.makePredicate("r", 1);
			Fact fact = Expressions.makeFact(p, a);
			PositiveLiteral headLiteral = Expressions.makePositiveLiteral(q, x);
			PositiveLiteral bodyLiteral = Expressions.makePositiveLiteral(r, x);
			Rule rule = Expressions.makeRule(headLiteral, bodyLiteral);
			interpreter.getKnowledgeBase().addStatement(fact);
			interpreter.getKnowledgeBase().addStatement(rule);

			Command command = interpreter.parseCommand("@clear RULES .");
			interpreter.runCommand(command);

			assertEquals(Arrays.asList(fact), interpreter.getKnowledgeBase().getFacts());
			assertTrue(interpreter.getKnowledgeBase().getRules().isEmpty());
			assertTrue(interpreter.getKnowledgeBase().getDataSourceDeclarations().isEmpty());
		}
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongArgumentCount_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@clear .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void wrongArgumentType_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@clear \"string\" .");
		interpreter.runCommand(command);
	}

	@Test(expected = CommandExecutionException.class)
	public void unkonwnTask_fails() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);

		Command command = interpreter.parseCommand("@clear UNKNOWNTASK .");
		interpreter.runCommand(command);
	}

	@Test
	public void help_succeeds() throws ParsingException, CommandExecutionException {
		StringWriter writer = new StringWriter();
		Interpreter interpreter = InterpreterTest.getMockInterpreter(writer);
		CommandInterpreter commandInterpreter = new ClearCommandInterpreter();
		InterpreterTest.checkHelpFormat(commandInterpreter, interpreter, writer);
	}

	@Test
	public void synopsis_succeeds() throws ParsingException, CommandExecutionException {
		CommandInterpreter commandInterpreter = new ClearCommandInterpreter();
		InterpreterTest.checkSynopsisFormat(commandInterpreter);
	}

}
