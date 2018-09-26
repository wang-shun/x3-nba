/**
 * *****************************************************************************
 * 							Copyright (c) 2014 yama.
 * This is not a free software,all rights reserved by yama(guooscar@gmail.com).
 * ANY use of this software MUST be subject to the consent of yama.
 *
 * *****************************************************************************
 */
package com.ftkj.script;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSON;

/**
 * @author yama
 * @date Jun 5, 2014
 */
public class JSONUtil {
	public static String toJson(Object obj){
		return JSON.toJSONString(obj);
	}
	//
	@SuppressWarnings("unchecked")
	public static <T> T fromJson(String str,Class<?>t){
		return (T) JSON.parseObject(str, t);
	}
	//
	@SuppressWarnings("unchecked")
	public static <T> List<T> fromJsonList(String str,Class<?>t){
		return  (List<T>) JSON.parseArray(str, t);	
	}
	//
	@SuppressWarnings("unchecked")
	public static <T> Map<String,T> fromJsonMap(String str,Class<?>t){
		List<T> l= (List<T>) JSON.parseArray(str, t);	
		Map<String,T>m=new HashMap<String, T>();
		List<Field> mainFields=new ArrayList<Field>();
		for(Field f:t.getFields()){
			if(f.isAnnotationPresent(JSONPrimaryKey.class)){
				mainFields.add(f);
			}
		}
		if(mainFields.isEmpty()){
			throw new IllegalArgumentException("can not find JSONPrimaryKey "
					+ "Annotation on class :"+t);
		}
		Collections.sort(mainFields,new FieldOrderCompartor());
		for(T tt:l){
			StringBuilder key=new StringBuilder();
			for(Field mainField:mainFields){
				Object oo=getFieldValue(mainField,tt);
				if(oo==null){
					throw new IllegalArgumentException("primary key :"+
									mainField.getName()+" should not null.");
				}
				key.append(oo).append("-");
			}
			key.deleteCharAt(key.length()-1);
			//
			//check field value
			for(Field allField:t.getFields()){
				checkFieldValue(allField,tt);
			}
			//
			m.put(key.toString(),tt);
		}
		return m;
	}
	//
	private static void checkFieldValue(Field field,Object o){
		Object oo=getFieldValue(field, o);
		JSONField fieldAnn=field.getAnnotation(JSONField.class);
		if(fieldAnn!=null){
			if(fieldAnn.notNull()&&oo==null){
				throw new IllegalArgumentException("field:"+field.getName()
						+" should not null.");
			}
			if(oo instanceof Number){
				Number nn=(Number)oo;
				if(nn.intValue()<fieldAnn.min()){
					throw new IllegalArgumentException("field:"+field.getName()
							+" shoule >="+fieldAnn.min());
				}
				if(nn.intValue()>fieldAnn.max()){
					throw new IllegalArgumentException("field:"+field.getName()
							+" shoule <="+fieldAnn.max());
				}
			}
			//
			if(field.getType().isArray()){
				int arrayLength=Array.getLength(oo);
				if(fieldAnn.size()!=0&&fieldAnn.size()!=arrayLength){
					throw new IllegalArgumentException("field:"+field.getName()
							+" length should = "+fieldAnn.size());
				}
			}
			//
			if(oo instanceof List){
				List<?> lll=(List<?>)oo;
				int listSize=lll.size();
				if(fieldAnn.size()!=0&&fieldAnn.size()!=listSize){
					throw new IllegalArgumentException("field:"+field.getName()
							+" length should = "+fieldAnn.size());
				}
			}
		}
	}
	//
	private static Object getFieldValue(Field field,Object o){
		field.setAccessible(true);
		Object oo=null;
		try{
			oo=field.get(o);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		return oo;
	}
	//
	private static class FieldOrderCompartor implements Comparator<Field>{
		@Override
		public int compare(Field o1, Field o2) {
			JSONPrimaryKey p1=o1.getAnnotation(JSONPrimaryKey.class);
			JSONPrimaryKey p2=o1.getAnnotation(JSONPrimaryKey.class);
			return p1.order()-p2.order();
		}
	}
}
