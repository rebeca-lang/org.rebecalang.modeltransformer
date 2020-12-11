package org.rebecalang.modeltransformer.ros;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MsgsrvDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableDeclarator;
import org.rebecalang.compiler.utils.TypesUtilities;

public class Utilities {
	public Utilities() {
		// TODO Auto-generated constructor stub
	}
	
	public static MsgsrvDeclaration findTheMsgsrv(ReactiveClassDeclaration reactiveClass, String msgsrvName) {
		MsgsrvDeclaration retMsgsrv = null;
		for(MsgsrvDeclaration msgsrv : reactiveClass.getMsgsrvs()) {
			if(msgsrv.getName().equals(msgsrvName)) {
				retMsgsrv = msgsrv;
			}
		}
		return retMsgsrv;
	}
	
	public static ReactiveClassDeclaration findKnownReactiveClass(ReactiveClassDeclaration rc, String rebecName, RebecaModel rebecaModel) {
		ReactiveClassDeclaration toRC = null;
		for(FieldDeclaration knownRC : rc.getKnownRebecs()) {
			for(VariableDeclarator vd : knownRC.getVariableDeclarators()) {
				if(vd.getVariableName().equals(rebecName)) {
					System.out.println(TypesUtilities.getTypeName(knownRC.getType()));
					toRC = Utilities.getReactiveClassByName(TypesUtilities.getTypeName(knownRC.getType()), rebecaModel);	
				}
			}
		}
		if(rebecName.equals("self"))
			toRC = rc;
		return toRC;
	}

	public static ReactiveClassDeclaration getReactiveClassByName(String className, RebecaModel rebecaModel) {
		ReactiveClassDeclaration retRC = null;
		for(ReactiveClassDeclaration thisRC : rebecaModel.getRebecaCode().getReactiveClassDeclaration()) {
			if(thisRC.getName().equals(className)) {
				retRC = thisRC;
			}
		}
		return retRC;
	}
}
