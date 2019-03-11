//
// MessageSender
//
function MessageSender(){}

MessageSender.prototype.sendDataToParent = function(data){
    var port = window.location.port;
	var curUrl = window.location.protocol + "//" + window.location.hostname;
	
	if(port != null && port.length > 0) {
		curUrl = curUrl + ":" + port;
	}
	window.parent.postMessage(JSON.stringify(data), curUrl);
}

// END MessageSender

//
// SerializedData
//
function SerializedData(){}

SerializedData.prototype.convertData = function(data){
	var result;
	if(data instanceof Array){
		result = [];
		this.convertArray(data, result);
	} else if(data instanceof Object && !(data instanceof Date)){
		result = {};
		this.convertObject(data, result);
	} else {
		result = this.convertNormalData(data);
	}
	return result;
}

SerializedData.prototype.convertArray = function(data, result){
	if(result === null){
		result = [];
	}
	for(var index = 0; index < data.length; index ++){
		if(data[index] instanceof Array){
			result[index] = [];
			this.convertArray(data[index], result[index]);
		} else if(data[index] instanceof Object && !(data[index] instanceof Date)){
			result[index] = {};
			this.convertObject(data[index], result[index]);
		} else {
			result[index] = this.convertNormalData(data[index]);
		}
	}
	return result;
}
SerializedData.prototype.convertObject = function(data, result){
	if(result === null){
		result = new Object();
	}
	for(var name in data){
		if(data[name] instanceof Array){
			result[name] = [];
			this.convertArray(data[name], result[name]);				
		}
		else if(data[name] instanceof Object && !(data[name] instanceof Date)){
			result[name] = {};
			this.convertObject(data[name], result[name]);
		} else {
			result[name] = this.convertNormalData(data[name]);
		}
	}
	return result;
}

SerializedData.prototype.convertNormalData = function(normalData){
	if(typeof normalData == 'string'){
		return normalData;
	} else if(normalData instanceof Date){
		return "Date_:_" + normalData.getTime(); //Get the time (milliseconds since January 1, 1970)
	} else if(typeof normalData == 'number'){
		var x = String(normalData);
		if(normalData % 1 ===0){
			return "Long_:_" + normalData;
		} else {
			return "Double_:_" + normalData;
		}
	} else if(typeof normalData == 'boolean') {
		return "Boolean_:_" + String(normalData);
	}
}

// END SerializedData

//
//DeserializedData
//
function DeserializedData(){}

DeserializedData.prototype.deserializedData = function(data){
	var result;
	if(data instanceof Array){
		result = [];
		this.deserializedArray(data, result);
	} else if(data instanceof Object && !(data instanceof Date)){
		result = {};
		this.deserializedObject(data, result);
	} else {
		result = this.deserializedNormalData(data);
	}
	return result;
}
DeserializedData.prototype.deserializedArray = function(data, result){
	if(result === null){
		result = [];
	}
	for(var index = 0; index < data.length; index ++){
		if(data[index] instanceof Array){
			result[index] = [];
			this.deserializedArray(data[index], result[index]);
		} else if(data[index] instanceof Object && !(data[index] instanceof Date)){
			result[index] = {};
			this.deserializedObject(data[index], result[index]);
		} else {
			result[index] = this.deserializedNormalData(data[index]);
		}
	}
	return result;
}

DeserializedData.prototype.deserializedObject = function(data, result){
	if(result === null){
		result = new Object();
	}
	for(var name in data){
		if(data[name] instanceof Array){
			result[name] = [];
			this.deserializedArray(data[name], result[name]);
		}
		else if(data[name] instanceof Object && !(data[name] instanceof Date)){
			result[name] = {};
			this.deserializedObject(data[name], result[name]);
		} else {
			result[name] = this.deserializedNormalData(data[name]);
		}
	}
	return result;
}
DeserializedData.prototype.deserializedNormalData = function(normalData){
	if(normalData.indexOf("Date_:_") > -1){
		var result = normalData.split("Date_:_");
		return new Date(Number(result[1]));
	} else if(normalData.indexOf("Long_:_") > -1){
		var result = normalData.split("Long_:_");
		return new Number(result[1]);
	} else if(normalData.indexOf("Double_:_") > -1){
		var result = normalData.split("Double_:_");
		return new Date(Number(result[1]));
	}else if(normalData.indexOf("Boolean_:_") > -1) {
		var result = normalData.split("Boolean_:_");
		return result[1] !== undefined && result[1].toLowerCase() == 'true';
	} else {
		return normalData;
	}
}
// END DeserializedData


//
// config global variables & register event listener
//
window.MSG_SENDER = new MessageSender();

window.addEventListener('message',function(event){
	var port = window.location.port;
	var curUrl = window.location.protocol + "//" + window.location.hostname;
	if(port != null && port.length > 0) {
		curUrl = curUrl + ":" + port;
	}
	if(event.origin !== curUrl) return;
	var data = JSON.parse(event.data);
	var deserializedData = new DeserializedData();
	var newData = deserializedData.deserializedData(data);
	//alert(event.data);
	formOnMessage(newData);
},false);

//
//Call Rest Service
//
function CallRestService(){}

CallRestService.prototype.getQueryData = function (queryCode, mapParams) {
	var curUrl = window.location.protocol + "//" + window.location.hostname;
	var port = window.location.port;
	var contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf("/",2));
	if (port != null && port.length > 0) {
		curUrl = curUrl + ":" + port;
	}
	curUrl = curUrl + contextPath + "/services/v2/duiRQueryService/rQueryDatas.json";

	var curParams = "queryCode=" + queryCode + "&mapParams=" + mapParams;
	$.ajax({
		url : curUrl,
		type : 'GET',
		data : curParams,
		contentType : "application/json; charset=utf-8",
		dataType : "json",
		success : function (data) {
			return data;
		},
		error : function (msg, url, line) {
			alert("Vui lòng thao tác lại hoặc liên hệ IT để được hỗ trợ thêm!");
			console.log('msg = ' + msg + ', url = ' + url + ', line = ' + line);
		}
	});
}

CallRestService.prototype.postAction = function (serviceCode, mapParams) {
	var curUrl = window.location.protocol + "//" + window.location.hostname;
	var port = window.location.port;
	var contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf("/",2));
	if (port != null && port.length > 0) {
		curUrl = curUrl + ":" + port;
	}
	curUrl = curUrl + contextPath + "/services/v2/rActionExecutor/execute.json";

	var curParams = "serviceCode=" + serviceCode + "&params=" + mapParams;
	$.ajax({
		url : curUrl,
		type : 'GET',
		data : curParams,
		contentType : "application/json; charset=utf-8",
		dataType : "json",
		success : function (data) {
			return data;
		},
		error : function (msg, url, line) {
			alert("Vui lòng thao tác lại hoặc liên hệ IT để được hỗ trợ thêm!");
			console.log('msg = ' + msg + ', url = ' + url + ', line = ' + line);
		}
	});
}