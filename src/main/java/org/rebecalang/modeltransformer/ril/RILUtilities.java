package org.rebecalang.modeltransformer.ril;

import java.util.LinkedList;
import java.util.List;

import org.rebecalang.compiler.modelcompiler.SymbolTable.MethodInSymbolTableSpecifier;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FormalParameterDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MethodDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.utils.TypesUtilities;

public class RILUtilities {

	private static String computeMethodName(String className, String methodName, List<Type> parametersTypes) {

		String canonicalMethodName = className + "." + methodName;
		canonicalMethodName += createListOfParameterTypes(parametersTypes);
		return canonicalMethodName;
	}

	private static String createListOfParameterTypes(List<Type> parametersTypes) {
		String paramTypes = "";
		for (Type parameterType : parametersTypes) {
			paramTypes += "$" + TypesUtilities.getTypeName(parameterType);
		}
		return paramTypes;
	}

	public static String computeMethodName(ReactiveClassDeclaration rcd, MethodDeclaration md) {
		List<FormalParameterDeclaration> parameters = md.getFormalParameters();
		List<Type> parametersType = new LinkedList<Type>();
		for (FormalParameterDeclaration fpd : parameters) {
			parametersType.add(fpd.getType());
		}
		return computeMethodName(rcd.getName(), md.getName(), parametersType);
	}

	public static String computeMethodName(Type baseType, MethodInSymbolTableSpecifier mssp) {
		List<Type> argumentsTypes = mssp.getArgumentsTypes();
		return computeMethodName(TypesUtilities.getTypeName(baseType), mssp.getName(), argumentsTypes);
	}

	public static String computeMethodName(MethodInSymbolTableSpecifier castableMethodSpecification) {
		return castableMethodSpecification.getName() + 
				createListOfParameterTypes(castableMethodSpecification.getArgumentsTypes());
	}

}
