String.prototype.startWith=function(str){     
	var reg=new RegExp("^"+str);     
	return reg.test(this);        
}  
String.prototype.endWith=function(str){     
	var reg=new RegExp(str+"$");     
	return reg.test(this);        
}
String.prototype.toJSON = function(){
	try {
		return jQuery.parseJSON(this);
	} catch (e) {
		return null;
	}
}
//去除前后空格
String.prototype.trim = function(){
	return jQuery.trim(this);
}
//去除所有空格
String.prototype.noSpace = function(){ 
	return this.replace(/[\s]{2,}/g, ""); 
}  