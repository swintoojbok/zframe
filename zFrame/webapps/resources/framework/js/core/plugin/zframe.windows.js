$.extend($.fn.window.methods, {
	shake : function(jq, params) {
		return jq.each(function() {
			var extent = params && params['extent'] ? params['extent'] : 1;
			var interval = params && params['interval'] ? params['interval']
					: 13;
			var style = $(this).closest('div.window')[0].style;
			if ($(this).data("window").shadow) {
				var shadowStyle = $(this).data("window").shadow[0].style;
			}
			_p = [ 4 * extent, 6 * extent, 8 * extent, 6 * extent, 4 * extent,
					0, -4 * extent, -6 * extent, -8 * extent, -6 * extent,
					-4 * extent, 0 ], _fx = function() {
				style.marginLeft = _p.shift() + 'px';
				if (shadowStyle)
					shadowStyle.marginLeft = _p.shift() + 'px';
				if (_p.length <= 0) {
					style.marginLeft = 0;
					if (shadowStyle)
						shadowStyle.marginLeft = 0;
					clearInterval(_timerId);
					_timerId = null, _p = null, _fx = null;
				}
				;
			};
			_p = _p.concat(_p.concat(_p));
			_timerId = setInterval(_fx, interval);
		});
	}
});
function Windows(id){
	try{
		this.id = "windowDialog_"+id;
		this.windows = windows_proxy;
		this.returnValue = false;//返回值
		this.parent = $(top.window.document.getElementById("frameContent").contentWindow.document);//父窗口document对象
		this.parentWindow = top.window.document.getElementById("frameContent").contentWindow;//父窗口window对象
		this.param = null;//窗口携带的值
		this.callback = null;
		this.setCallback = function(fun){
			if(typeof(fun) == "function"){
				this.callback = fun;
			}else{
				top.Dialog.alert("错误","回调函数类型错误！","error");
			}
		}
		//默认窗口关闭事件
		this.onClose = function(){
			if(Windows.UploadFileWindow != null && typeof(Windows.UploadFileWindow.callback) == "function"){
				Windows.UploadFileWindow.callback();
			}
			if(Windows.UploadImageWindow != null && typeof(Windows.UploadImageWindow.callback) == "function"){
				Windows.UploadImageWindow.callback();
			}
			if(typeof(this.callback) == "function")
				this.callback();
		}
		//关闭窗口
		this.close = function(){
			this.windows('close');
		}
		//打开窗口
		this.open = function(){
			this.windows('open');
		}
		this.getReturnValue = function(){
			var sReturnValue = top.$("#FrameWorkWindows").find("#btn_"+this.id).val();
			if(sReturnValue==undefined){
				return false;
			}else{
				return sReturnValue;
			}
		}
		this.setReturnValue = function(value){
			this.returnValue = value;
			top.$("#FrameWorkWindows").find("#btn_"+this.id).val(value);
		}
		this.getParam = function(){
			var param = top.$("#FrameWorkWindows").find("#param_"+this.id).val();
			return param;
		}
		this.setParam = function(value){
			this.param = value;
			top.$("#FrameWorkWindows").find("#param_"+this.id).val(value);
		}
		this.find = function(EL){
			return top.$("#"+this.id).find(EL);
		}
		this.destory = function(){
			this.windows("destory",true);
		}
		// 初始化返回值
		function initReturnValue(fid){
			var sReturnValue = top.$("#FrameWorkWindows").find("#btn_"+fid).val();
			if(sReturnValue==undefined){
				this.returnValue = false;
			}else{
				this.returnValue = sReturnValue;
			}
		}
		function initParam(fid){
			var param = top.$("#FrameWorkWindows").find("#param_"+fid).val();
			if(param==undefined){
				this.param = false;
			}else{
				this.param = param;
			}
		}
		initReturnValue(this.id);
		initParam(this.id);
	}catch(e){
		top.Dialog.alert("错误","脚本错误:"+e.message,"error");
	}
}
function windows_proxy(a,b){
	try{
		var win = this;
		// 设置默认属性
		if(typeof a != "string"){
			if(!a.minimizable){
				a.minimizable = false;
			}
			if(!a.loadingMessage){
				a.loadingMessage = "加载中...";
			}
			if(!a.tools){
				if(a.refreshable==undefined || a.refreshable==true){
					a.tools = [{
						iconCls:'icon-refresh-bootstrap',
						handler:function(){
							win.windows('refresh',a.href);
						}
					}]
				}
			}else{
				if(a.refreshable==undefined || a.refreshable==true){
					var tool = {
							iconCls:'icon-refresh-bootstrap',
							handler:function(){
								win.windows('refresh',a.href);
							}
						};
					a.tools.push(tool);
				}
			}
			if(!a.onClose){
				a.onClose = win.onClose;
			}
		}
		if(top.$("#"+this.id)[0]==undefined){
			var winProxy = $("<div></div>");
			winProxy.attr("id",this.id);
			var btnProxy = $("<input>");
			btnProxy.attr("id","btn_"+this.id);
			btnProxy.attr("type","hidden");
			btnProxy.val(false);
			var paramProxy = $("<input>");
			paramProxy.attr("id","param_"+this.id);
			paramProxy.attr("type","hidden");
			top.$("#FrameWorkWindows").append(winProxy);
			top.$("#FrameWorkWindows").append(btnProxy);
			top.$("#FrameWorkWindows").append(paramProxy);
			top.$("#FrameWorkWindows").find("#"+this.id).window(a,b);
		}else{
			top.$("#FrameWorkWindows").find("#"+this.id).remove();
			if(a=='open'){
				top.$("#"+this.id).window('center');
				top.$("#FrameWorkWindows").find("#btn_"+this.id).val("false");
			}else if(a=='destory'){
				top.$("#FrameWorkWindows").find("#"+this.id).remove();
				top.$("#FrameWorkWindows").find("#btn_"+this.id).remove();
				top.$("#FrameWorkWindows").find("#param_"+this.id).remove();
				return;
			}
			top.$("#"+this.id).window(a,b);
		}
	}catch(e){
		top.Dialog.alert("错误","脚本错误:"+e.message,"error");
	}
}
Windows.UploadFileWindow = null;
Windows.UploadImageWindow = null;
Windows.UploadFile = function(options){
	try {
		if(Windows.UploadFileWindow != null){
			var win = Windows.UploadFileWindow;
			win.setCallback(function(){
				if(options){
					if(typeof(options.onUploadComplete) == "function"){
						var returnValue = win.getReturnValue();
						//调用回调函数
						options.onUploadComplete(returnValue);
					}
				}
			});
		}else{
			var win = new Windows("sys_uploader_file");
			win.windows({
				title:'文件上传',
				width:410,
				height:300,
				modal:true,
				cache:true,
				closed:true,
				refreshable:false,
				iconCls:'icon-upload',
				href:top.Home.BasePath+"/admin/uploader/upload/file"
			});
			win.setCallback(function(){
					if(options){
						if(typeof(options.onUploadComplete) == "function"){
							var returnValue = win.getReturnValue();
							//调用回调函数
							options.onUploadComplete(returnValue);
						}
					}
			});
			Windows.UploadFileWindow = win;
		}
		win.open();
	} catch (e) {
		
	}
}
Windows.UploadImage = function(options){
	try {
		if(Windows.UploadImageWindow != null){
			var win = Windows.UploadImageWindow;
			win.setCallback(function(){
				if(options){
					if(typeof(options.onUploadComplete) == "function"){
						var returnValue = win.getReturnValue();
						//调用回调函数
						options.onUploadComplete(returnValue);
					}
				}
			});
		}else{
			var win = new Windows("sys_uploader_image");
			win.windows({
				title:'图片上传',
				width:410,
				height:300,
				modal:true,
				cache:true,
				closed:true,
				refreshable:false,
				iconCls:'icon-upload',
				href:top.Home.BasePath+"/admin/uploader/upload/image"
			});
			win.setCallback(function(){
				if(options){
					if(typeof(options.onUploadComplete) == "function"){
						var returnValue = win.getReturnValue();
						//调用回调函数
						options.onUploadComplete(returnValue);
					}
				}
			});
			Windows.UploadImageWindow = win;
		}
		win.open();
	} catch (e) {
		
	}
}