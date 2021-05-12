var Dialog = {
		show : function(options){
			$.messager.show(options);
		},
		alert : function(title,msg,icon,fn){
			// 只有一个参数的时候，默认标题为提示，icon为info
			if(msg==undefined && icon==undefined && fn==undefined){
				msg = title;
				title = "提示";
				icon = "info";
			}
			if(icon==undefined && fn==undefined){
				fn = msg;
				msg = title;
				title = "提示";
				icon = "info";
			}
			$.messager.alert(title,msg,icon,fn);
		},
		info : function(msg,fn){
			$.messager.alert("提示",msg,"info",fn);
		},
		error : function(msg,fn){
			$.messager.alert("错误",msg,"error",fn);
		},
		warning : function(msg,fn){
			$.messager.alert("警告",msg,"warning",fn);
		},
		confirm : function(title,msg,fn){
			if(msg==undefined && fn==undefined){
				msg = title;
				title = "询问";
			}
			if(fn == undefined){
				fn = msg;
				msg = title;
				title = "询问";
			}
			$.messager.confirm(title,msg,fn);
		},
		prompt : function(title,msg,fn,type){
			if(fn==undefined){
				fn = msg;
				msg = title;
				title = "输入";
			}
			$.messager.prompt(title,msg,fn,type);
		},
		progress : function(options){
			if(typeof options != "string"){
				if(!options){
					options = {
							title:"正在执行...",
							text:"请稍等"
					};
				}
			}
			$.messager.progress(options);
		},
		verifyPass : function(options){
			top.Dialog.prompt("密码验证","请输入当前账户密码:",function(password){
				if(password){
					$.post(top.Home.BasePath+"/admin/verify/verifyCUserPass",{password:password},function(data){
						if(data.verifyResult == "SessionTimeOut"){// session失效
							top.Dialog.alert("登陆超时!",function(){
								window.location.href=top.Home.BasePath+"/admin/login/loginOut";
							});
						}else if(data.verifyResult == "AllowAccess"){// 验证通过
							if(typeof options.callback == "function")
								options.callback();
							return true;
						}else{// 验证失败!
							top.Dialog.alert("错误","验证失败!","error",function(){
								if(typeof options.callback == "function")
									options.callback();
							});
						}
					},"json");
				}
			},"password");
		}
};