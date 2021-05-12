$.extend($.fn.validatebox.defaults.rules, {
	equals: {//两个内容相同   
	    validator: function(value,param){   
	        return value == $(param[0]).val();   
	    },   
	    message: '两次输入的内容不匹配'  
	},
    minLength: {//最小长度
        validator: function(value, param){   
            return value.length >= param[0];   
        },   
        message: '至少输入 {0}个字符'  
    },
    maxLength: {//最大长度 
        validator: function(value, param){   
        return value.length <= param[0];   
    },   
    message: '最多输入 {0}个字符'  
    },
    telphone:{
    	validator:function(value,param){
    		var patrn=/^[+]{0,1}(\d){1,3}[ ]?([-]?((\d)|[ ]){1,12})+$/;
    		return patrn.exec(value);
    	},
    	message:'请输入有效的固定电话号码'
    },
    mobile:{
    	validator:function(value,param){
    		var patrn=/^[+]{0,1}(\d){1,3}[ ]?([-]?((\d)|[ ]){1,12})+$/; 
    		return patrn.exec(value);
    	},
    	message:'请输入有效的手机号码'
    },
    zip:{
    	validator:function(value,param){
    		var patrn=/^[a-zA-Z0-9 ]{3,12}$/; 
    		return patrn.exec(value);
    	},
    	message:'请输入有效的邮政编码'
    },
    ip:{
    	validator:function(value,param){
    		var patrn=/^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])$/;
    		return patrn.exec(value);
    	},
    	message:'请输入有效的IP地址'
    }
});