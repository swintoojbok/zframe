var _basePath = "";
$(function() {
	//判断是否在iframe里面
	if(self.frameElement){
		//在iframe里面整个网页跳转到登陆页面
		top.location.href=$("#basePath").val()+"/admin/login";
	}
	// 居中
	$('.login_main').center();
	//载入遮蔽层圆角
	$("#loadding").corner();
	document.getElementById("username").focus();
	$("#username").keydown(function(event) {
		if (event.keyCode == 13) {
			event.returnValue = false;
			return login();
		}
	})
	$("#password").keydown(function(event) {
		if (event.keyCode == 13) {
			event.returnValue = false;
			return login();
		}
	})
	//绑定Tip
	$('#username').tip({
		className: 'tip-yellow',
		showOn: 'none',
		alignTo: 'target',
		alignX: 'inner-left',
		offsetX: 0,
		offsetY: 5
	});
	$('#username').click(function(){
		if($(this).val())
			$(this).tip("hide");
	});
	$('#username').keydown(function(){
		if($(this).val())
			$(this).tip("hide");
	});
	$('#password').tip({
		className: 'tip-yellow',
		showOn: 'none',
		alignTo: 'target',
		alignX: 'inner-left',
		offsetX: 0,
		offsetY: 5
	});
	$('#password').click(function(){
		if($(this).val())
			$(this).tip("hide");
	});
	$('#password').keydown(function(){
		if($(this).val())
			$(this).tip("hide");
	});
	var loginName = getCookie($("#basePath").val()+"_LOGINNAME");
	if(loginName != undefined && $.trim(loginName).length>0){
		$("#username").val(loginName);
		$("#password").focus();
	}else{
		$("#username").focus();
	}
	_basePath = $("#basePath").val();
})
// 登录
function login() {
	$('#username').tip("hide");
	$('#password').tip("hide");
	var loginName = document.getElementById("username"); 
	var password = document.getElementById("password");
	if ($.trim(loginName.value)=="") {
		$('#username').tip("show");
		loginName.focus();
		return false;
	}else if ($.trim(password.value)=="") {
		$("#password").tip("show");
		password.focus();
		return false;
	}else {
		$("#loadding").show();
		$.ajax({
		  url:$("#loginForm").attr("action"),
		  type:"POST",
		  data:{"username":loginName.value,"password":password.value},
		  timeout:30000,
		  dataType:"json",
		  success:function(data){
			if(data.result){
				setCookie($("#basePath").val()+"_LOGINNAME", loginName.value);
				window.location.href = $("#basePath").val()+"/admin/index";
			}else{
				$("#loadding").hide();
				if(data.errorType=="USERNAME"){
					$('#username').tip("update",data.msg);
					$('#username').tip("show");
				}else if(data.errorType=="PASSWORD"){
					$('#password').tip("update",data.msg);
					$('#password').tip("show");
				}else{
					_basePath = $("#basePath").val();
					top.Notiy.error(data.msg);
				}
			}
		  },
		  error:function(){
			  $("#loadding").hide();
			  _basePath = $("#basePath").val();
			  try{
				  top.Notiy.error("登陆超时，请稍后重试！");
			  }catch(e){
				alert("登陆超时，请稍后重试！");  
			  }
		  }
		});
	}
	return true;
}