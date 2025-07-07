package org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.DotPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.modeltransformer.ril.corerebeca.translator.RILTransformationRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DotPrimaryExpressionTranslator extends AbstractExpressionTranslator {

	@Autowired
	public DotPrimaryExpressionTranslator(Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
		super(expressionTranslatorContainer);
	}

	@Override
	public Object translate(Expression expression, ArrayList<InstructionBean> instructions) {

		DotPrimary dotPrimary = (DotPrimary) expression;		
		
		if(dotPrimary.getRight() instanceof DotPrimary) {
			DotPrimary base = new DotPrimary();
			base.setLeft(dotPrimary.getLeft());
			base.setRight(((DotPrimary)dotPrimary.getRight()).getLeft());
			throw new RILTransformationRuntimeException();
		} else {
			Variable leftSide = (Variable) expressionTranslatorContainer.translate(dotPrimary.getLeft(),
					instructions);
			Type leftSideType = dotPrimary.getLeft().getType();
			TermPrimary rightSide = (TermPrimary) dotPrimary.getRight();
			TermPrimaryExpressionTranslator translator = 
					(TermPrimaryExpressionTranslator) expressionTranslatorContainer.getTranslator(TermPrimary.class);
			return translator.translate(leftSideType, leftSide, (TermPrimary)rightSide, instructions);
		}
		
//		PrimaryExpression rightSide = (PrimaryExpression) dotPrimary.getRight();
//		do {
//			
//		} while(rightSide != null);
//		rightSide = (TermPrimary) dotPrimary.getRight();
//
//		TermPrimaryExpressionTranslator translator = 
//				(TermPrimaryExpressionTranslator) expressionTranslatorContainer.getTranslator(TermPrimary.class);
//		return translator.translate(leftSideType, leftSide, (TermPrimary)rightSide, instructions);
	}

}
