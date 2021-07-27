package org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Literal;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LiteralStatementTranslator extends AbstractExpressionTranslator {

	@Autowired
	public LiteralStatementTranslator(Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
		super(expressionTranslatorContainer);
	}

	@Override
	public Object translate(Expression expression, ArrayList<InstructionBean> instructions) {
		Literal literal = (Literal) expression;
		if(literal.getType().canTypeCastTo(CoreRebecaTypeSystem.INT_TYPE))
			return Integer.parseInt(literal.getLiteralValue());
		if(literal.getType().canTypeCastTo(CoreRebecaTypeSystem.DOUBLE_TYPE))
			return Double.parseDouble(literal.getLiteralValue());
		if(literal.getType() == CoreRebecaTypeSystem.BOOLEAN_TYPE)
			return Boolean.valueOf(literal.getLiteralValue());
		if(literal.getType() == CoreRebecaTypeSystem.STRING_TYPE)
			return String.valueOf(literal);
		return null;
	}

}
