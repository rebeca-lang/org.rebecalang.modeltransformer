package org.rebecalang.modeltransformer.akka.corerebeca;

import com.squareup.javapoet.CodeBlock;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.*;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.TypesUtilities;

public class StatementTransformer {

//    private static StatementTransformer instance;
//
//    public static StatementTransformer getInstance() {
//        if (instance == null)
//            instance = new StatementTransformer();
//        return instance;
//    }

    private ExpressionTransformer expressionTransformer;

    public StatementTransformer(ExpressionTransformer expressionTransformer) {
        this.expressionTransformer = expressionTransformer;
    }

    private String NEW_LINE = "\n";
    public String getTypeName(Type type) {
        return TypesUtilities.getTypeName(type);
    }

    public String transformMsgsrvBody(MethodDeclaration methodDeclaration) throws CodeCompilationException, UnsupportedStatementException, UnsupportedExpressionException {
//        expressionTransformer.initialize();
        return resolveBlockStatement(methodDeclaration.getBlock());
    }

    public String transformSynchMethodBody(MethodDeclaration methodDeclaration) throws CodeCompilationException {
//        container.addException(new StatementTransformingException("This version of transformer does not supprt " +
//                "synch method declaration.",
//                methodDeclaration.getLineNumber(), methodDeclaration.getCharacter()));
        return "";
    }

    public String transformConstructorBody(MethodDeclaration methodDeclaration) throws CodeCompilationException, UnsupportedStatementException, UnsupportedExpressionException {
//        expressionTransformer.initialize();
        return resolveBlockStatement(methodDeclaration.getBlock());
    }

    public String resolveBlockStatement(BlockStatement blockStatement) throws CodeCompilationException, UnsupportedStatementException, UnsupportedExpressionException {
//        CodeBlock.Builder codeBlock = CodeBlock.builder();
        String retValue = "";

        for (Statement statement : blockStatement.getStatements()) {
//            codeBlock.add(resolveStatement(statement));
            retValue += resolveStatement(statement) + ";" + NEW_LINE;
        }

//        return codeBlock.build().toString();
        return retValue;
    }

    public String resolveStatement(Statement statement) throws CodeCompilationException, UnsupportedStatementException, UnsupportedExpressionException {
        if (statement instanceof BlockStatement) {
            return resolveBlockStatement((BlockStatement) statement);
        }
        if (statement instanceof ConditionalStatement) {
            return resolveIfStatement((ConditionalStatement) statement);
        }
        if (statement instanceof WhileStatement) {
            throw new UnsupportedStatementException();
        }
        if (statement instanceof ForStatement) {
//            container.addException(new StatementTransformingException("This version of transformer does not supprt " +
//                    "\"for\" statement.",
//                    statement.getLineNumber(), statement.getCharacter()));
            throw new UnsupportedStatementException();
        }
        if (statement instanceof ReturnStatement) {
//            container.addException(new StatementTransformingException("This version of transformer does not supprt " +
//                    "\"return\" statement.",
//                    statement.getLineNumber(), statement.getCharacter()));
            throw new UnsupportedStatementException();
        }
        if (statement instanceof BreakStatement) {
//            container.addException(new StatementTransformingException("This version of transformer does not supprt " +
//                    "\"break\" statement.",
//                    statement.getLineNumber(), statement.getCharacter()));
            throw new UnsupportedStatementException();
        }
        if (statement instanceof ContinueStatement) {
//            container.addException(new StatementTransformingException("This version of transformer does not supprt " +
//                    "\"continue\" statement.",
//                    statement.getLineNumber(), statement.getCharacter()));
            throw new UnsupportedStatementException();
        }
        if (statement instanceof SwitchStatement) {
//            container.addException(new StatementTransformingException("This version of transformer does not supprt " +
//                    "\"switch\" statement.",
//                    statement.getLineNumber(), statement.getCharacter()));
            throw new UnsupportedStatementException();
        }
        if (statement instanceof FieldDeclaration) {
            return resolveFieldDeclaration((FieldDeclaration) statement).toString();
        }
        if (statement instanceof Expression) {
            return resolveExpression((Expression) statement);
        }
//        container.addException(new StatementTransformingException("Unknown translation rule for type " +
//                statement.getClass(), statement.getLineNumber(), statement.getCharacter()));
        throw new UnsupportedStatementException();
    }

    private CodeBlock resolveFieldDeclaration(FieldDeclaration statement) throws UnsupportedStatementException {
//		String definitionPart = "";
//		Type type = statement.getType();
//		if (TypesUtilities.getInstance().canTypeCastTo(type, TypesUtilities.INT_TYPE))
//			definitionPart = "defI";
//        return "unauthorized-local-variable-decleration";
        throw new UnsupportedStatementException();
    }

    protected String  resolveIfStatement(ConditionalStatement statement) throws CodeCompilationException, UnsupportedStatementException, UnsupportedExpressionException {
//        CodeBlock.Builder codeBlock = CodeBlock.builder();

        String retValue = "";
//        codeBlock.addStatement("if (");
//        codeBlock.add(ExpressionTransformer.getInstance().translate(statement.getCondition()));
//        codeBlock.addStatement(") {");
        retValue += "if (" + expressionTransformer.translate(statement.getCondition()) + ") {" + NEW_LINE;
        if (statement.getStatement() != null){
//            codeBlock.add(resolveStatement(statement.getStatement()));
            retValue += resolveStatement(statement.getStatement());
        }

//        codeBlock.addStatement("}" + NEW_LINE + "else {" + NEW_LINE);
        retValue += "}" + NEW_LINE + "else {" + NEW_LINE ;
        if (statement.getElseStatement() != null) {
//            codeBlock.add(resolveStatement(statement.getElseStatement()));
            retValue += resolveStatement(statement.getElseStatement());
        }
//        codeBlock.addStatement("}");
        retValue += "}" + NEW_LINE;

//        return codeBlock.build();
        return retValue;
    }

    public String resolveExpression(Expression expression) throws UnsupportedExpressionException {
        return expressionTransformer.translate(expression);
    }

}
