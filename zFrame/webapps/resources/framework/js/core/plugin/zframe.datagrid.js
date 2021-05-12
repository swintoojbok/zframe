$.extend($.fn.datagrid.defaults.editors, {   
	progressbar: {   
		init: function(container, options){   
			var bar = $('<div/>').appendTo(container);
			bar.progressbar(options); 				
			return bar;   
		},   
		getValue: function(target){   
			return $(target).progressbar('getValue');   
		},
		setValue: function(target, value){   
			$(target).progressbar('setValue',value);   
		},
		resize: function(target, width){    
			if ($.boxModel == true){   
				$(target).progressbar('resize',width - (input.outerWidth() - input.width()));
			} else {   
				$(target).progressbar('resize',width);
			}   
		}   
	},
	slider: {   
		init: function(container, options){   
			var slider = $('<div/>').appendTo(container);
			slider.slider(options); 				
			return slider;   
		},   
		getValue: function(target){   
			return $(target).slider('getValue');   
		},
		setValue: function(target, value){   
			$(target).slider('setValue',value);   
		},
		resize: function(target, width){    
			if ($.boxModel == true){   
				$(target).progressbar('slider',{width:width - (input.outerWidth() - input.width())});
			} else {   
				$(target).progressbar('slider',{width:width});
			}   
		}   
	}				
}); 