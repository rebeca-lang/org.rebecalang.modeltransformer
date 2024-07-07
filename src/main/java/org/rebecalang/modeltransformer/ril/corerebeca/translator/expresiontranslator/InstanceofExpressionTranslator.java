package org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.InstanceofExpression;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InstanceofExpressionTranslator extends AbstractExpressionTranslator {
    @Autowired
    public InstanceofExpressionTranslator(Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
        super(expressionTranslatorContainer);
    }

    @Override
    public Object translate(Expression expression , ArrayList<InstructionBean> instructions) {
        InstanceofExpression ioExpression = (InstanceofExpression) expression;
        String operator = "instanceof";

        Object leftSide = expressionTranslatorContainer.translate(ioExpression.getValue(), instructions);
        Object rightSide = ioExpression.getEvaluationType().getTypeName();

        Variable tempVariable = getTempVariable();
        instructions.add(new DeclarationInstructionBean(tempVariable.getVarName()));
        AssignmentInstructionBean assignmentInstruction = new AssignmentInstructionBean(tempVariable,
                leftSide, rightSide, operator);
        instructions.add(assignmentInstruction);

        return tempVariable;
    }
}
