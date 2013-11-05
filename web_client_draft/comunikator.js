window.onload=function(){
	$('#colorpickerHolder').ColorPicker(
	{
		flat: true,
		onChange: function (hsb, hex, rgb) {
			var type=getRadio();
			if(type==1){
				$('#messagesPanel .odd').css('backgroundColor', '#' + hex);
			}else if(type==2){
				$('#messagesPanel .even').css('backgroundColor', '#' + hex);
			}else if(type==3){
				$('.contact.odd').css('backgroundColor', '#' + hex);
			}else if(type==4){
				$('.contact.even').css('backgroundColor', '#' + hex);
			}else if(type==5){
				$('.conversation').css('backgroundColor', '#' + hex);
			}else if(type==6){
				$('.tabMenu').css('backgroundColor', '#' + hex);
			}else if(type==7){
				$('#messagesPanel .btn').css('backgroundColor', '#' + hex);
			}else if(type==8){
				$('#application').css('backgroundColor', '#' + hex);
			}else if(type==9){
				$('#applicationMenu').css('backgroundColor', '#' + hex);
			}else if(type==10){
				$('#application .btn').css('backgroundColor', '#' + hex);
			}else if(type==11){
				$('.available').css('color', '#' + hex);
			}else if(type==12){
				$('.brb').css('color', '#' + hex);
			}else if(type==13){
				$('.inaccessible').css('color', '#' + hex);
			}
		}
	});

};
function getRadio(){
	return $('input[name=kolor]:checked').val()

}

var duta = function () { 
	
	function getTemplate(url){
		var template;
		jQuery.ajax({
		 url:    url,
		 dataType : 'text',
		 success: function(result) {
					template= result;
				  },
		 async:   false
		});
		return template;	
	}
	
	function renderMessage(){
		var model={
			conversationId : 'conversation1',
			author : 'Ja',
			messageText : 'asdassdgas sa fg dhh sh h gfh jkhjklhil hjkujkh',
			dateTime : '12:00',
			url : 'message.html'
		}
		var template = getTemplate(model.url);
		var rendered = Handlebars.compile(template)(model);
		
		$('#messagesPanel .conversation#'+model.conversationId+' .tabMessages').append(rendered);
		
	}
	
	
	

	return{
		getTemplate : getTemplate,
		renderMessage : renderMessage
	
	};

}();

