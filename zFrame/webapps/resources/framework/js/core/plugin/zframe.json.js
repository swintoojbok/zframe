//json对象
function JSONObject(){
	this.elements = new Array();
	 
    //获取MAP元素个数
     this.size = function() {
         return this.elements.length;
     };
 
    //判断MAP是否为空
     this.isEmpty = function() {
         return (this.elements.length < 1);
     };
 
    //删除MAP所有元素
     this.clear = function() {
         this.elements = new Array();
     };
 
    //向MAP中增加元素（key, value)
     this.put = function(_key, _value) {
    	 if(this.containsKey(_key)){
    		 this.remove(_key);
    	 }
         this.elements.push( {
             key : _key,
             value : _value
         });
     };
 
    //删除指定KEY的元素，成功返回True，失败返回False
     this.remove = function(_key) {
         var bln = false;
         try {
             for (i = 0; i < this.elements.length; i++) {
                 if (this.elements[i].key == _key) {
                     this.elements.splice(i, 1);
                     return true;
                 }
             }
         } catch (e) {
             bln = false;
         }
         return bln;
     };
 
    //获取指定KEY的元素值VALUE，失败返回NULL
     this.get = function(_key) {
         try {
             for (i = 0; i < this.elements.length; i++) {
                 if (this.elements[i].key == _key) {
                     return this.elements[i].value;
                 }
             }
         } catch (e) {
             return null;
         }
     };
 
    //获取指定索引的元素（使用element.key，element.value获取KEY和VALUE），失败返回NULL
     this.element = function(_index) {
         if (_index < 0 || _index >= this.elements.length) {
             return null;
         }
         return this.elements[_index];
     };
 
    //判断MAP中是否含有指定KEY的元素
     this.containsKey = function(_key) {
         var bln = false;
         try {
             for (i = 0; i < this.elements.length; i++) {
                 if (this.elements[i].key == _key) {
                     bln = true;
                 }
             }
         } catch (e) {
             bln = false;
         }
         return bln;
     };
 
    //判断MAP中是否含有指定VALUE的元素
     this.containsValue = function(_value) {
         var bln = false;
         try {
             for (i = 0; i < this.elements.length; i++) {
                 if (this.elements[i].value == _value) {
                     bln = true;
                 }
             }
         } catch (e) {
             bln = false;
         }
         return bln;
     };
 
    //获取MAP中所有VALUE的数组（ARRAY）
     this.values = function() {
         var arr = new Array();
         for (i = 0; i < this.elements.length; i++) {
             arr.push(this.elements[i].value);
         }
         return arr;
     };
 
    //获取MAP中所有KEY的数组（ARRAY）
     this.keys = function() {
         var arr = new Array();
         for (i = 0; i < this.elements.length; i++) {
             arr.push(this.elements[i].key);
         }
         return arr;
     };
     this.toJSON = function(){
    	 try {
    			return jQuery.parseJSON(this.toString());
    		} catch (e) {
    			return null;
    		}
     };
	this.toString = function(){
		var jStr = "";
		for(var i=0;i<this.elements.length;i++){
			var key = this.elements[i].key;
			var value = this.elements[i].value;
			if(typeof(value) == "object" && !(value instanceof Array))
				jStr += ",\""+key+"\":["+value+"]";
			else
				jStr += ",\""+key+"\":\""+value+"\"";
		}
		jStr = jStr.substring(1);
		jStr = "{"+jStr+"}";
		return jStr;
	};
}
JSONObject.parseJSON=function(str){
	try {
		return jQuery.parseJSON(str);
	} catch (e) {
		return null;
	}
}