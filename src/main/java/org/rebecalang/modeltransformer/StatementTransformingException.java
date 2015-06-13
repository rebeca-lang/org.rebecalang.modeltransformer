package org.rebecalang.modeltransformer;

import org.rebecalang.compiler.utils.CodeCompilationException;

@SuppressWarnings("serial")
public class StatementTransformingException extends CodeCompilationException {

	public StatementTransformingException(String message, int line, int column) {
		super(message, line, column);
	}
}
