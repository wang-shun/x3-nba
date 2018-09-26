package com.ftkj.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptContext;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.io.Reader;


public class ScriptEngine {
    private static final Logger log = LoggerFactory.getLogger(ScriptEngine.class);
	private ScriptEngineManager engineManager;
	private javax.script.ScriptEngine jsEngine;
	
	public ScriptEngine() {
		engineManager=new ScriptEngineManager();
		jsEngine=engineManager.getEngineByExtension("js");		
	}
	
	public void execute(Reader reader,StartupContext sc){
		if(sc==null){
			throw new NullPointerException("ScriptContext can not be null");
		}
		SimpleScriptContext ssc=new SimpleScriptContext();
		ssc.setAttribute("$", sc, ScriptContext.ENGINE_SCOPE);
		try {
			//System.out.println(jsEngine.eval("1+2*(3+4)").toString());  
			jsEngine.eval(reader,ssc);			
		} catch (Exception e) {
            log.error(e.getMessage(), e);
		}
	}
	
	public int getValue(String str){
		try{
			Double a = new Double(""+jsEngine.eval(str));
			return Integer.parseInt(Math.round(a)+"");
		}catch(Exception e){
			return 0;
		}
	}
	
	public static void main(String[] args) throws ScriptException{
//		ScriptEngine S = new ScriptEngine();
//		S.execute(reader, sc);
//		String str = "grade*10";
//		System.out.println(S.getValue(str.replace("grade", "40")));
		
		//System.out.println(S.getValue("280"));
	}
}