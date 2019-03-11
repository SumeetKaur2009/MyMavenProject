/**
 * File Name : FormBase.java
 * Package   : com.testing.base
 * Author    : RIENGCS
 * Created   : Sep 18, 2014

 * This source code is part of the  gwt-form-demo, and is copyrighted by JWT. 
 * All rights reserved.  No part of this work may be reproduced, stored in a retrieval system,
 * adopted or transmitted in any form or by any means,electronic, mechanical, photographic,
 * graphic, optic recording or otherwise,translated in any language or computer language,
 * without the prior written permission of JWT 

 Copyright © 2014 - 2014 by JWT
 ----------------------------------------------------------------------------------------------
 * Version Control:
 *       $LastChangedRevision$
 *       $LastChangedBy$
 *       $LastChangedDate$
 *
 ----------------------------------------------------------------------------------------------
 **/
package com.testing.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dtsc.dynamic.ui.form.base.JSONUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.info.Info;

/**
 * <b>Author     :</b> RIENGCS </br>
 * <b>Created    :</b> Sep 18, 2014 </br>
 * <b>Description:</b>
 **/
public abstract class FormBase implements IsWidget, EntryPoint{
	protected static final String COMMAND_KEY = "command";
	protected static final String COMMAND_SAVE = "save";
	protected static final String COMMAND_COMPLETE = "complete";
	protected static final String COMMAND_CLOSE = "close";
	protected static final String DATA = "data";
	protected static final String DATA_DETAIL = "dataDetail";
	protected static final String INIT_DATA = "initData";
	protected static final String INIT_DATA_DETAIL = "initDataDetail";
	protected static final String PROCESS_VARIABLES = "processVariables";
	protected static final String FORM_CONFIG = "formConfig";
	protected static final String READ_ONLY = "readOnly";
	
	private Map<String, Object> formConfig;
	
	private Map<String, Object> mapInitData;
	private Map<String, Object> mapInitDataDetail;
	private Map<String, Object> processVariables;
	
	/**
	 * Method create form
	 **/
	protected abstract VerticalLayoutContainer createForm();
	
	/**
	 * Method send data to parent
	 **/	
	protected abstract Map<String, Object> sendDatatoParent();
	
	/**
	 * Method send data detail to parent
	 */
	protected abstract Map<String, Object> sendDataDetailtoParent();
	
	/**
	 * Method get data from parent
	 **/	
	protected abstract void getDatafromParent(Map<String, Object> data, boolean readOnly);

	@Override
	public Widget asWidget(){
		VerticalLayoutContainer form = createForm();
		if(form == null){
			form = new VerticalLayoutContainer();
		}
		form.addAttachHandler(new Handler(){
			@Override
			public void onAttachOrDetach(AttachEvent event){
				if(event.isAttached()){
					//Info.display("Form register event listener", "Form register event listener ...!!!");
					registerEventListener(FormBase.this);
				}else{
					//Info.display("Form remove event listener", "Form remove event listener ...!!!");
					removeEventListener();
				}
			}
		});		
		return form;
	}

	@Override
	public void onModuleLoad(){
		RootPanel.get().add(asWidget());
	}

	//-------------------ACTION---------------------//

	protected void doSave(){
		final Map<String, Object> mapData = sendDatatoParent();
		if(mapData != null){
			final Map<String, Object> newMapData = new HashMap<String, Object>();
			newMapData.put(COMMAND_KEY, COMMAND_SAVE);
			newMapData.put(DATA, mapData);
			newMapData.put(DATA_DETAIL, mapDataDetail);
			final String jsonData = javaMapToJson(newMapData).toString();
			doSave(jsonData);
			//Info.display("DoSave action", "DoSave action getMapData() not null ...!!!");
		}else{
			//Info.display("DoSave action", "DoSave action getMapData() is null ...!!!");
		}
	}

	protected void doComplete(){
		final Map<String, Object> mapData = sendDatatoParent();
		if(mapData != null){
			final Map<String, Object> newMapData = new HashMap<String, Object>();
			newMapData.put(COMMAND_KEY, COMMAND_COMPLETE);
			newMapData.put(DATA, mapData);
			newMapData.put(DATA_DETAIL, mapDataDetail);
			final String jsonData = javaMapToJson(newMapData).toString();
			doComplete(jsonData);
			//Info.display("DoComplete action", "DoSave action getMapData() not null ...!!!");
		}else{
			//Info.display("DoComplete action", "DoSave action getMapData() is null ...!!!");
		}
	}

	protected void doClose(){
		final Map<String, Object> newMapData = new HashMap<String, Object>();
		newMapData.put(COMMAND_KEY, COMMAND_CLOSE);
		final String jsonData = javaMapToJson(newMapData).toString();
		doClose(jsonData);
	}

	//--------------------JSON UTIL------------------//

	private static JSONValue javaMapToJson(Map<String, Object> mapData){
		if(mapData == null){
			return null;
		}
		/*return convertMap(mapData);*/
		return JSONUtils.serializeAsJson(mapData);
	}

	private static JSONObject convertMap(Map<String, Object> mapData){
		JSONObject obj = new JSONObject();
		for(Entry<String, Object> entry : mapData.entrySet()){
			Object val = entry.getValue();
			JSONValue jsonVal = convertObject(val);
			obj.put(entry.getKey(), jsonVal);
		}

		return obj;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static JSONValue convertObject(Object val){
		if(val == null){
			return JSONNull.getInstance();
		}

		if(val instanceof Boolean){
			return JSONBoolean.getInstance((Boolean) val);
		}else
			if(val instanceof Number){
				Double dVal = Double.valueOf(((Number) val).doubleValue());
				return new JSONNumber(dVal);
			}else
				if(val instanceof List){
					JSONArray arr = new JSONArray();
					int count = 0;
					for(Object obj : (List) val){
						JSONValue converted = convertObject(obj);
						arr.set(count, converted);
						count++;
					}

					return arr;
				}else
					if(val instanceof Map){
						JSONObject map = convertMap((Map<String, Object>) val);
						return map;
					}else{
						return new JSONString((String) val);
					}
	}

	private static Map<String, Object> jsonToJavaMap(JSONObject json){
		if(json == null){
			return new HashMap<String, Object>();
		}else{
			/*Map<String, Object> map = new HashMap<String, Object>();
			for(String key : json.keySet()){
				JSONValue val = json.get(key);
				Object obj = convertJson(val);
				if(obj != null){
					map.put(key, obj);
				}
			}

			return map;*/
			Map<String, Object> map = (Map<String, Object>) JSONUtils.parseObject(json);
			return map;
		}
	}

	private static Object convertJson(JSONValue val){
		if(val == null){
			return null;
		}

		if(val.isNull() != null){
			return null;
		}

		if(val.isBoolean() != null){
			return val.isBoolean().booleanValue();
		}

		if(val.isString() != null){
			return val.isString().stringValue();
		}

		if(val.isNumber() != null){
			return val.isNumber().doubleValue();
		}

		JSONArray arr = val.isArray();
		if(arr != null){
			List<Object> vals = new ArrayList<Object>();
			for(int i = 0; i < arr.size(); i++){
				JSONValue itemVal = arr.get(0);
				Object obj = convertJson(itemVal);
				if(obj != null){
					vals.add(obj);
				}
			}
			return vals;
		}

		JSONObject obj = val.isObject();
		if(obj != null){
			Map<String, Object> map = jsonToJavaMap(obj);
			return map;
		}
		return null;
	}

	//--------------------NATIVE------------------//

	private static native void doSave(String jsonData) /*-{
		//$wnd.alert(jsonData);
		$wnd.MSG_SENDER.sendDataToParent(jsonData);
	}-*/;

	private static native void doComplete(String jsonData) /*-{
		//$wnd.alert(jsonData);
		$wnd.MSG_SENDER.sendDataToParent(jsonData);
	}-*/;

	private static native void doClose(String jsonData) /*-{
		//$wnd.alert(jsonData);
		$wnd.MSG_SENDER.sendDataToParent(jsonData);
	}-*/;

	private native void registerEventListener(FormBase p) /*-{
		$wnd.funcPostMessageListener = function postMessageListener(e){
			var port = $wnd.location.port;
			var curUrl = $wnd.location.protocol + "//" + $wnd.location.hostname;

			if (port != null && port.length > 0){
				curUrl = curUrl + ":" + port;
			}

			if (e.origin !== curUrl){
				return;
			}
			p.@com.testing.base.FormBase::eventListener(Ljava/lang/String;)(e.data);
		}
		if (window.attachEvent){
			$wnd.attachEvent("onmessage", $wnd.funcPostMessageListener, false);
		}
		if (document.attachEvent){
			$wnd.attachEvent("onmessage", $wnd.funcPostMessageListener, false);
		}
		if (window.addEventListener){
			$wnd.addEventListener("message", $wnd.funcPostMessageListener, false);
		}
	}-*/;

	private native void removeEventListener() /*-{
		if (window.detachEvent){
			$wnd.detachEvent("onmessage", $wnd.funcPostMessageListener, false);
		}
		if (document.detachEvent){
			$wnd.detachEvent("onmessage", $wnd.funcPostMessageListener, false);
		}
		if (window.removeEventListener){
			$wnd.removeEventListener("message", $wnd.funcPostMessageListener, false);
		}
	}-*/;

	@SuppressWarnings("unchecked")
	private void eventListener(String msgJsonData){
		final JSONObject jsonData = JSONParser.parseStrict(msgJsonData).isObject();
		if (jsonData == null) {
			getDatafromParent(new HashMap<String, Object>(), false);
			return;
		} else {
			Map<String, Object> data = jsonToJavaMap(jsonData);
			mapInitData = (Map<String, Object>) data.get(INIT_DATA);
			mapInitDataDetail = data.get(INIT_DATA_DETAIL) != null ? (Map<String, Object>) data.get(INIT_DATA_DETAIL) : null;
			processVariables = (Map<String, Object>) data.get(PROCESS_VARIABLES);
			
			this.formConfig = (Map<String, Object>) data.get(FORM_CONFIG);
			
			getDatafromParent(mapInitData.size() > 0 ? mapInitData : processVariables, isReadOnly());
			return;
		}
	}
	
	public boolean isReadOnly() {
		return (Boolean) (this.formConfig != null ? this.formConfig.get(READ_ONLY) : false);
	}
	
	public Map<String, Object> getFormConfig() {
		return this.formConfig;
	}
	
	//----------------------------------------------------------------//
	/*
	 * REST SERVICE
	 */
	public static void getQueryData(String queryCode, Map<String, Object> mapParams, final RequestCallback callback) throws RequestException {
		JSONValue jsonInputData = JSONUtils.serializeAsJson(mapParams);
		String uri = getHostPageBaseURL() + "/services/v2/duiRQueryService/rQueryDatas.json?queryCode=" + queryCode + "&mapParams=" + jsonInputData.toString();
		
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, URL.encode(uri));
		requestBuilder.sendRequest(null, new RequestCallback() {
			@Override
			public void onResponseReceived(Request request, Response response) {
				callback.onResponseReceived(request, response);
			}
			
			@Override
			public void onError(Request request, Throwable exception) {
				callback.onError(request, exception);
			}
		});
	}
	
	public static void postAction(String serviceCode, Map<String, Object> mapParams, final RequestCallback callback) throws RequestException {
		JSONValue jsonInputData = JSONUtils.serializeAsJson(mapParams);
		String uri = getHostPageBaseURL() + "/services/v2/rActionExecutor/execute.json?serviceCode=" + serviceCode + "&params=" + jsonInputData.toString();
		
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, URL.encode(uri));
		requestBuilder.sendRequest(null, new RequestCallback() {
			@Override
			public void onResponseReceived(Request request, Response response) {
				callback.onResponseReceived(request, response);
			}
			
			@Override
			public void onError(Request request, Throwable exception) {
				callback.onError(request, exception);
			}
		});
	}
	
	public static List<Map<String, Object>> parseResponseQuery(Response response) {
		return (List<Map<String, Object>>) parseResponseAction(response);
	}
	
	public static Object parseResponseAction(Response response) {
		JSONValue jsonValueResult = JSONParser.parse(response.getText());
		Map<String, Object> mapResult = (Map<String, Object>) JSONUtils.parseObject(jsonValueResult);
		return (Object) mapResult.get("data");
	}
	
	public native static String getHostPageBaseURL() /*-{
		var curUrl = window.location.protocol + "//" + window.location.hostname;
		var port = window.location.port;
		var contextPath = window.location.pathname.substring(0,
				window.location.pathname.indexOf("/", 2));
		if (port != null && port.length > 0) {
			curUrl = curUrl + ":" + port;
		}
		return curUrl = curUrl + contextPath;
	}-*/;
}