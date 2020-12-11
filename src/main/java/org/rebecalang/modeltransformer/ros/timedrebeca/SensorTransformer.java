package org.rebecalang.modeltransformer.ros.timedrebeca;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FormalParameterDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MsgsrvDeclaration;
import org.rebecalang.compiler.utils.TypesUtilities;
import org.rebecalang.modeltransformer.ros.Rebeca2ROSTypesUtilities;

public class SensorTransformer{
	
	public final static String NEW_LINE = "\r\n";
	private MsgsrvDeclaration msgsrv;
	private String modelName;
	private String msgFileContent;
	private String callbackFuncationSignature;
	private String callbackFunctionBody;
	public CoreRebecaStatementTransformer statementTransformer;
	
	
	public SensorTransformer(CoreRebecaStatementTransformer statementTransformer,
			MsgsrvDeclaration msgsrv, String modelName) {
		this.msgsrv = msgsrv;
		this.modelName = modelName;
		this.statementTransformer = statementTransformer;
	}
	
	public void transform() {
		this.msgFileContent = getMsgFileContent();
		this.callbackFuncationSignature = getCallbackFunctionSignature();
		this.callbackFunctionBody = getCallbackFunctionBody();
	}
	
	public String getSubscriberDeclaration() {
		String retValue = "";
		return retValue;
	}
	
	public String getSubscriberDefinition() {
		String retValue = "";
		return retValue;
	}
	
	public String getMsgFileContent() {
		msgFileContent = "";
		msgFileContent += "string sender" + NEW_LINE;
		for(FormalParameterDeclaration param : msgsrv.getFormalParameters()) {
			//System.out.println(TypesUtilities.getTypeName(param.getType()));
			//String rebecaType = TypesUtilities.getTypeName(param.getType());
			msgFileContent += Rebeca2ROSTypesUtilities.getROSMessageType(TypesUtilities.getTypeName(param.getType())) + " " + param.getName() + NEW_LINE;
		}
		return msgFileContent;
	}
	
	public String getCallbackFunctionSignature() {
		callbackFuncationSignature = "";
		callbackFuncationSignature += msgsrv.getName() + "Callback_Sensor" + "(const " + 
		modelName + "::" + msgsrv.getName() + " &" + " thisMsg)";
		return callbackFuncationSignature;
	}
	
	public String getCallbackFunctionBody() {
		callbackFunctionBody = "\\ In this section, ROS developer should write his code on managinf sensed data";

		return callbackFunctionBody;
	}
	
	
}
