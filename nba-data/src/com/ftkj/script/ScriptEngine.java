package com.ftkj.script;

import java.io.Reader;

import javax.script.ScriptContext;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;


public class ScriptEngine {
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
			e.printStackTrace();
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
		ScriptEngine S = new ScriptEngine();
		String str = "grade*10";
		System.out.println(S.getValue(str.replace("grade", "40")));
		Double hour = (Double)S.jsEngine.eval("var date = new Date();" + "date.getHours();");
		System.out.println(hour);
		//System.out.println(S.getValue("280"));
	}
}