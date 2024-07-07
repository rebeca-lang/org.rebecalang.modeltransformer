package org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class AbstractExpressionTranslator {

	public final static String TEMP_VAR_PREFIX = "$TEMP_EXP$";
	public final static String RETURN_METHOD_NAME = "$RETURN-NAME$";
	public final static String RETURN_METHOD_LINE = "$RETURN-LINE$";
	public static final String RETURN_VALUE = "$RETURN_VALUE$";

	
	static int counter = 0;
	public static Variable getTempVariable() {
		return new Variable(TEMP_VAR_PREFIX + counter++);
	}
	
	protected Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer;
	
	
	@Autowired
	public AbstractExpressionTranslator(Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
		this.expressionTranslatorContainer = expressionTranslatorContainer;
	}



	public abstract Object translate(Expression expression, ArrayList<InstructionBean> instructions);
}
