package org.rebecalang.modeltransformer.akka.corerebeca;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.*;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.TypesUtilities;

import java.util.Map;

public class ExpressionTransformer {

    private final Map<String, String > knownRebecs;

    public ExpressionTransformer(Map<String, String > knownRebecs) {
        this.knownRebecs = knownRebecs;
    }

    public String translate(Expression expression) throws UnsupportedExpressionException {
//        CodeBlock.Builder codeBlock = CodeBlock.builder();
        String retValue = "";

        if (expression instanceof TernaryExpression) {
//            container.addException(new StatementTransformingException("This version of transformer does not supprt " +
//                    "ternary expression.",
//                    expression.getLineNumber(), expression.getCharacter()));
        } else if (expression instanceof BinaryExpression) {
            BinaryExpression bExpression = (BinaryExpression) expression;
            String op = bExpression.getOperator();
            op = " " + op + " ";
//            codeBlock.addStatement(translate(bExpression.getLeft()));
//            codeBlock.addStatement(op);
//            codeBlock.addStatement(translate(bExpression.getRight()));
            retValue += translate(bExpression.getLeft()) + op + translate(bExpression.getRight());
        } else if (expression instanceof UnaryExpression) {
            UnaryExpression uExpression = (UnaryExpression) expression;
//            codeBlock.addStatement("( ");
//            codeBlock.addStatement(uExpression.getOperator());
//            codeBlock.addStatement(" ");
//            codeBlock.addStatement(translate(uExpression.getExpression()));
//            codeBlock.addStatement(")");
            retValue += "( " + uExpression.getOperator() + ' ' + translate(uExpression.getExpression()) + ")" ;
        } else if (expression instanceof CastExpression) {
//            container.addException(new StatementTransformingException("This version of transformer does not supprt " +
//                    "\"cast\" expression.",
//                    expression.getLineNumber(), expression.getCharacter()));
        } else if (expression instanceof NonDetExpression) {
            /*retValue = " ( ";
            for (Expression nonDetChoices : ((NonDetExpression)expression).getChoices()) {
                retValue += " ( " + translate(nonDetChoices) + " ) ? ";
            }
            retValue = retValue.substring(0, retValue.length() - 2) + ") ";*/
            throw new UnsupportedExpressionException();
        } else if (expression instanceof Literal) {
            Literal lExpression = (Literal) expression;
//            retValue = lExpression.getLiteralValue();
//            codeBlock.addStatement(lExpression.getLiteralValue());
            retValue = lExpression.getLiteralValue();
//            if (retValue.equals("null"))
//                retValue = "\"dummy\"";
        } else if (expression instanceof PlusSubExpression) {
//            container.addException(new StatementTransformingException("This version of transformer does not supprt " +
//                    "\"++\" and \"--\" expression.",
//                    expression.getLineNumber(), expression.getCharacter()));
        } else if (expression instanceof PrimaryExpression) {
            PrimaryExpression pExpression = (PrimaryExpression) expression;
            retValue = translatePrimaryExpression(pExpression);
        } else {
//            container.addException(new StatementTransformingException("Unknown translation rule for expression type "
//                    + expression.getClass(), expression.getLineNumber(), expression.getCharacter()));
        }
//        return codeBlock.build().toString();
        return retValue;
    }

    protected String translatePrimaryExpression(
            PrimaryExpression pExpression) throws UnsupportedExpressionException {
        String retValue = "";
        if (pExpression instanceof DotPrimary) {
            DotPrimary dotPrimary = (DotPrimary) pExpression;
            if (!(dotPrimary.getLeft() instanceof TermPrimary) || !(dotPrimary.getRight() instanceof TermPrimary)) {
//                container.addException(new StatementTransformingException("This version of transformer does not supprt " +
//                        "nested record access expression.",
//                        pExpression.getLineNumber(), pExpression.getCharacter()));
            } else {
                String args = "";
                for (Expression expression : ((TermPrimary)dotPrimary.getRight()).getParentSuffixPrimary().getArguments()) {
                    args += "arg((" + translate(expression) + ")) ";
                }
                if (args.equals(""))
                retValue = ((TermPrimary) dotPrimary.getLeft()).getName();
                retValue = (retValue.equals("self")) ? ("this" + '.' + ((TermPrimary) dotPrimary.getRight()).getName() + "()") :
                        ((TermPrimary) dotPrimary.getLeft()).getName() + '.' + "tell(new Messages." + knownRebecs.get(((TermPrimary) dotPrimary.getLeft()).getName()) + "." + ((TermPrimary) dotPrimary.getRight()).getName() + "(), getSelf())";
            }
        } else if (pExpression instanceof TermPrimary) {
            retValue = translatePrimaryTermExpression((TermPrimary) pExpression);
        } else if (pExpression instanceof RebecInstantiationPrimary) {
            RebecInstantiationPrimary rip = (RebecInstantiationPrimary) pExpression;
            boolean hasMoreVariable = false;
            String args = "";
            try {
                ReactiveClassDeclaration rcd = (ReactiveClassDeclaration) TypesUtilities.getInstance().getMetaData(rip.getType());
                if (!rcd.getStatevars().isEmpty()) {
                    args += " , ";
                    for (FieldDeclaration fd : rcd.getStatevars()) {
                        for (VariableDeclarator vd : fd.getVariableDeclarators()) {
                            hasMoreVariable = true;
                            String typeInit = fd.getType() == TypesUtilities.BOOLEAN_TYPE ? "false" :
                                    TypesUtilities.getInstance().canTypeCastTo(fd.getType(), TypesUtilities.INT_TYPE) ? "0" : "\"dummy\"";
                            args += "(" + rcd.getName() + "-" + vd.getVariableName() + " |-> " + typeInit + ") " ;
                        }
                    }
                }
                if (!hasMoreVariable)
                    args += "emptyValuation";
            } catch (CodeCompilationException e) {
                e.printStackTrace();
            }
            args += ",";
            hasMoreVariable = false;
            String typeName = TypesUtilities.getTypeName(rip.getType());
            for (Expression expression : rip.getBindings()) {
                args += " arg(" + translate(expression) + ")";
                hasMoreVariable = true;
            }
            for (Expression expression : rip.getArguments()) {
                args += " arg(" + translate(expression) + ")";
                hasMoreVariable = true;
            }
            if (!hasMoreVariable)
                args += "noArg";
            retValue = " new (" + typeName + args + ")";
        } else {
//            container.addException(new StatementTransformingException("Unknown translation rule for initializer type "
//                    + pExpression.getClass(), pExpression.getLineNumber(), pExpression.getCharacter()));
        }
        return retValue;
    }

    protected String translatePrimaryTermExpression(TermPrimary termPrimary) {
//        ReactiveClassDeclaration rcd = (ReactiveClassDeclaration) TransformingContext.getInstance().lookupInContext("current-reactive-class");
        String retValue = termPrimary.getName();
        if (!retValue.equals("sender") && !retValue.equals("self")) {
//            retValue = rcd.getName() + "-"+ retValue;
        }
        if (retValue.equals("sender")) {
            retValue = "getSender()";
        }

        if (retValue.equals("self")) {
            retValue = "this";
        }

        return retValue;
    }
}
