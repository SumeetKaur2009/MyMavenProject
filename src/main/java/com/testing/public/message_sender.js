function MessageSender(){}

//param data:  la tham so dang String json
MessageSender.prototype.sendDataToParent = function(data){
	//alert('sendDataToParent: ' + data);
    var port = window.location.port;
	var curUrl = window.location.protocol + "//" + window.location.hostname;
	if(port != null){
		curUrl = curUrl + ":" + port;
	}
	window.parent.postMessage(data, curUrl);
	//alert('sendDataToParent: ' + curUrl);
}

window.MSG_SENDER = new MessageSender();

/*
window.addEventListener('message',function(event){
	var port = window.location.port;
	var curUrl = window.location.protocol + "//" + window.location.hostname;
	if(port != null){
		curUrl = curUrl + ":" + port;
	}
	if(event.origin !== curUrl){
		return;
	}
	var data = JSON.parse(event.data);
	alert(event.data);
	formOnMessage(data);
},false);
*/

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