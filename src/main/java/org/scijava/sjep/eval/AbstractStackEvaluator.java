/*
 * #%L
 * SciJava mathematical expression parser.
 * %%
 * Copyright (C) 2015 Board of Regents of the University of
 * Wisconsin-Madison.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package org.scijava.sjep.eval;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;

import org.scijava.sjep.ExpressionParser;
import org.scijava.sjep.Function;
import org.scijava.sjep.Operator;
import org.scijava.sjep.Tokens;
import org.scijava.sjep.Verb;

/**
 * Base class for {@link StackEvaluator} implementations.
 *
 * @author Curtis Rueden
 */
public abstract class AbstractStackEvaluator extends AbstractEvaluator
	implements StackEvaluator
{

	public AbstractStackEvaluator() {
		super();
	}

	public AbstractStackEvaluator(final ExpressionParser parser) {
		super(parser);
	}

	// -- Evaluator methods --

	@Override
	public Object evaluate(final LinkedList<Object> queue) {
		// Process the postfix token queue.
		final Deque<Object> stack = new ArrayDeque<Object>();
		while (!queue.isEmpty()) {
			final Object token = queue.removeFirst();
			final Object result;
			if (Tokens.isVerb(token)) {
				result = execute((Verb) token, stack);
			}
			else {
				// Token is a variable or a literal.
				result = token;
			}
			if (result == null) die(token);
			stack.push(result);
		}

		if (stack.size() != 1) {
			throw new IllegalArgumentException("Expected one result but got " +
				stack.size());
		}

		return stack.pop();
	}

	// -- Helper methods --

	private static final String[] ARY = { "nullary", "unary", "binary",
		"ternary", "quaternary", "quinary", "senary", "septenary", "octary",
		"nonary" };

	private static String ary(final int arity) {
		return arity < ARY.length ? ARY[arity] : arity + "-ary";
	}

	private static String ary(final Verb verb) {
		return ary(verb.getArity());
	}

	private static void die(final Object token) {
		final StringBuilder message = new StringBuilder("Unsupported");
		if (token instanceof Verb) message.append(" " + ary((Verb) token));
		message.append(" " + type(token) + ": " + token);
		throw new IllegalArgumentException(message.toString());
	}

	private static String type(final Object token) {
		if (token instanceof Operator) return "operator";
		if (token instanceof Function) return "function";
		return "token";
	}

}