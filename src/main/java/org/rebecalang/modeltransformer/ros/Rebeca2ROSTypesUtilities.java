package org.rebecalang.modeltransformer.ros;


import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ArrayType;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableDeclarator;

public class Rebeca2ROSTypesUtilities {
	
	public static String getROSMessageType(String rebecaType) {
		if(rebecaType.equals("boolean")) 
			return "bool";
		if(rebecaType.equals("byte"))
			return "int8";
		if(rebecaType == "char")
			return "uint8";
		if(rebecaType.equals("int")) 
			return "int64";
		if(rebecaType.equals("double"))
			return "double";
		if(rebecaType.equals("string"))
			return "string";
		return rebecaType;
		}
	
	public static String getCppType(String RebecaType) {
		if(RebecaType.equals("boolean"))
			return "bool";
		if(RebecaType.equals("byte"));
			//
		return RebecaType;
	}
	
	public static String getCorrespondingCppType(Type rebecaType) {
		if(rebecaType.equals(CoreRebecaTypeSystem.BOOLEAN_TYPE)) {
			return "bool";
		}
		if(rebecaType.equals(CoreRebecaTypeSystem.INT_TYPE))
			return "int";
		if(rebecaType.equals(CoreRebecaTypeSystem.DOUBLE_TYPE))
			return "double";
		if(rebecaType.equals(CoreRebecaTypeSystem.STRING_TYPE))
			return "std::string";
		if(rebecaType.equals(CoreRebecaTypeSystem.BYTE_TYPE))
			System.out.println("The Rebeca to ROS Transformer does not support byte Type."
					+ "please replace all byte s with int, then try again");
		return rebecaType.getTypeName();
	}
	
	public static ReactiveClassDeclaration getClassName(Type classType, RebecaModel thisModel) {
		ReactiveClassDeclaration rc = null;
		for(ReactiveClassDeclaration classI: thisModel.getRebecaCode().getReactiveClassDeclaration()) {
			if(classI.getName().equals(classType.getTypeName())){
				return classI;
			}
		}
		return rc;
	}
	
	public static String getCppArrayDefinition(FieldDeclaration arrayDeclaration, 
										VariableDeclarator variableDeclarator) {
		
		String retValue = "";
		if(!(arrayDeclaration.getType() instanceof ArrayType)) {
			System.out.println("getCppArrayDefinition method not used correctly");
			return "";
		}
		Type type = arrayDeclaration.getType();
		String arrayName = variableDeclarator.getVariableName();
		Type arrayPrimitiveType = ((ArrayType)type).getOrdinaryPrimitiveType();
		
		retValue +=  Rebeca2ROSTypesUtilities.getCorrespondingCppType((arrayPrimitiveType)) + " ";
		retValue += arrayName;
		for(int dim : ((ArrayType)type).getDimensions()) {
			retValue += "[" + dim + "]";
		}
		return retValue;
	}
	
}
