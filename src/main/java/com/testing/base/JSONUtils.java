package com.testing.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class JSONUtils {
	/*
	 * @see com.google.gwt.json.client.JSONArray
	 * @see com.google.gwt.json.client.JSONBoolean
	 * @see com.google.gwt.json.client.JSONNumber
	 * @see com.google.gwt.json.client.JSONObject
	 * @see com.google.gwt.json.client.JSONString
	 */
	@SuppressWarnings("unchecked")
	public static JSONValue serializeAsJson(Object object) {
		if(object == null) {
			return JSONNull.getInstance();
		}
		if (object instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) object;
			JSONObject jsonObject = new JSONObject();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				JSONValue convertedValue = serializeAsJson(value);
				jsonObject.put(key, convertedValue);
			}
			return jsonObject;
		} else if (object instanceof List) {
			@SuppressWarnings("rawtypes")
			List list = (List) object;
			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < list.size(); i++) {
				Object entry = list.get(i);
				JSONValue convertedValue = serializeAsJson(entry);
				jsonArray.set(i, convertedValue);
			}
			return jsonArray;
		} else if (object instanceof String) {
			return new JSONString((String) object);
		} else if (object instanceof Integer) {
			return new JSONString("Integer_:_" + ((Integer)object).toString() );
		} else if (object instanceof Long) {
			return new JSONString("Long_:_" + ((Long)object).toString() );
		} else if (object instanceof Double) {
			return new JSONString("Double_:_" + ((Double)object).toString() );
		} else if (object instanceof Date) {
			return new JSONString("Date_:_" + ((Date)object).getTime() );
		}
		else if(object instanceof Boolean) {
			return new JSONString("Boolean_:_" + object.toString() );
		}
		else {
			throw new RuntimeException("Unsupported state type: " + object.getClass());
		}
	}

	/**
	 * Parse a JSONValue as follows: -if the value is a JSONObject, parse into a
	 * Map<String, Object> -if the value is a JSONArray, parse into a
	 * List<Object> -if the value is a JSONString, parse into a String
	 * -otherwise fail
	 */
	public static Object parseObject(JSONValue jsonValue) {
		if (jsonValue.isNull() != null) {
			return null;
		}
		
		if(jsonValue.isNumber() != null) {
			return jsonValue.isNumber().doubleValue();
		} else if (jsonValue.isObject() != null) {
			return parseMap(jsonValue.isObject());
		} else if (jsonValue.isArray() != null) {
			return parseList(jsonValue.isArray());
		} else if (jsonValue.isString() != null) {
			String jsonString = jsonValue.isString().stringValue();
			if( jsonString.contains("_:_") ) {
				String[] values = jsonString.split("_:_");
				if(values[0].equals("Integer")) {
					return new Integer(values[1]);
				} else if(values[0].equals("Long")) {
					return new Long(values[1]);
				} else if(values[0].equals("Double")) {
					return new Double(values[1]);
				} else if(values[0].equals("Date")) {
					return new Date( new Long(values[1]).longValue() );
				}
				else if(values[0].equals("Boolean")) {
					return new Boolean(values[1]);
				}
			} else {
				return jsonString;
			}
		} else {
			throw new RuntimeException("Failed to parse JSON: " + jsonValue.toString());
		}
		return null;
	}
	
	/**
	 * Parse the specified JSONObject into a Map<String, Object>. The JSONValue
	 * associated to a key is parsed recursively using
	 * {@link #parseObject(JSONValue)}
	 */
	public static Map<String, Object> parseMap(JSONObject jsonObject) {
		Map<String, Object> result = new HashMap<String, Object>();
		for (String key : jsonObject.keySet()) {
			JSONValue jsonValue = jsonObject.get(key);
			Object convertedValue = parseObject(jsonValue);
			result.put(key, convertedValue);
		}
		return result;
	}

	/**
	 * Parse the specified JSONArray into a List<Object>. The JSONValues
	 * contained in the array are parsed recursively using
	 * {@link #parseObject(JSONValue)}
	 */
	public static Object parseList(JSONArray array) {
		List<Object> result = new ArrayList<Object>();
		for (int i = 0; i < array.size(); i++) {
			JSONValue jsonValue = array.get(i);
			Object convertedValue = parseObject(jsonValue);
			result.add(convertedValue);
		}
		return result;
	}
}