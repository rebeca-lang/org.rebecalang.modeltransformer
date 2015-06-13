package org.rebecalang.modeltransformer.maude.timedrebeca;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.DotPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.PrimaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.compiler.modelcompiler.timedrebeca.objectmodel.TimedRebecaParentSuffixPrimary;
import org.rebecalang.compiler.utils.CompilerFeature;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.modeltransformer.TransformingFeature;

public class TimedRebecaExpressionTransformer extends
		CoreRebecaExpressionTransformer {

	public TimedRebecaExpressionTransformer(Set<CompilerFeature> cFeatures,
			Set<TransformingFeature> tFeatures, ExceptionContainer container) {
		super(cFeatures, tFeatures, container);
	}

	protected String translatePrimaryExpression(
			PrimaryExpression pExpression) {
		int size = container.getExceptions().size();
		String retValue = super.translatePrimaryExpression(pExpression);
		if (pExpression instanceof DotPrimary) {
			if (size == container.getExceptions().size()) {
				TimedRebecaParentSuffixPrimary suffix = (TimedRebecaParentSuffixPrimary) ((TermPrimary)((DotPrimary)pExpression).getRight()).getParentSuffixPrimary();
				retValue = retValue.substring(0, retValue.length() - 1);
				retValue += " deadline (" + (suffix.getDeadlineExpression() == null ? "INF" : translate(suffix.getDeadlineExpression(), container)) +
						") after (" +(suffix.getAfterExpression() == null ? "0" : translate(suffix.getAfterExpression(), container)) + ") )";
			}
		} else if (pExpression instanceof TermPrimary){
			TermPrimary tPrimary = (TermPrimary) pExpression;
			if (tPrimary.getName().equals("delay") && tPrimary.getParentSuffixPrimary() != null) {
				retValue += "(" + translate(tPrimary.getParentSuffixPrimary().getArguments().get(0), container) + ")";
			}
		}
		return retValue;
	}
	protected String translatePrimaryTermExpression(TermPrimary termPrimary) {
		String retValue = super.translatePrimaryTermExpression(termPrimary);
		if(termPrimary.getName().equals("delay")) {
			int dashIndex = retValue.indexOf("-");
			retValue = retValue.substring(dashIndex + 1);
		}
		return retValue;
	}
}