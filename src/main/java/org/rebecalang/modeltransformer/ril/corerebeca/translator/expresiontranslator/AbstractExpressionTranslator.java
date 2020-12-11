package org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;


public abstract class AbstractExpressionTranslator {

	public final static String TEMP_VAR_PREFIX = "$TEMP_EXP$";
	public final static String RETURN_METHOD_NAME = "$RETURN-NAME$";
	public final static String RETURN_METHOD_LINE = "$RETURN-LINE$";
	public static final String RETURN_VALUE = "$RETURN_VALUE$";

	
	static int counter = 0;
	public static Variable getTempVariable() {
		return new Variable(TEMP_VAR_PREFIX + counter++);
	}

	public abstract Object translate(Expression expression, ArrayList<InstructionBean> instructions);
}
