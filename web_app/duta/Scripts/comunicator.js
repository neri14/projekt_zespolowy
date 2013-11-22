//window.onload=function(){
//	$('#colorpickerHolder').ColorPicker(
//	{
//		flat: true,
//		onChange: function (hsb, hex, rgb) {
//			var type=getRadio();
//			if(type==1){
//				$('#messagesPanel .odd').css('backgroundColor', '#' + hex);
//			}else if(type==2){
//				$('#messagesPanel .even').css('backgroundColor', '#' + hex);
//			}else if(type==3){
//				$('.contact.odd').css('backgroundColor', '#' + hex);
//			}else if(type==4){
//				$('.contact.even').css('backgroundColor', '#' + hex);
//			}else if(type==5){
//				$('.conversation').css('backgroundColor', '#' + hex);
//			}else if(type==6){
//				$('.tabMenu').css('backgroundColor', '#' + hex);
//			}else if(type==7){
//				$('#messagesPanel .btn').css('backgroundColor', '#' + hex);
//			}else if(type==8){
//				$('#application').css('backgroundColor', '#' + hex);
//			}else if(type==9){
//				$('#applicationMenu').css('backgroundColor', '#' + hex);
//			}else if(type==10){
//				$('#application .btn').css('backgroundColor', '#' + hex);
//			}else if(type==11){
//				$('.available').css('color', '#' + hex);
//			}else if(type==12){
//				$('.brb').css('color', '#' + hex);
//			}else if(type==13){
//				$('.inaccessible').css('color', '#' + hex);
//			}
//		}
//	});
//
//};
function getRadio(){
	return $('input[name=kolor]:checked').val()

}

var duta = function () { 
	var messagesPanelExists = false;
	var dutaModel = {};
	window.onload = function () {
	//    login();
	    
	};
	
	function getActualContact(id) {
	    var cont = {};
		$.each(dutaModel.contacts, function(index, value ) {
			if(id==value.user_id){
			    cont = value;
			}
		});
		return cont;
	}
	
	function login() {
	    var url = window.location.origin + "/Service/Login";
		$.ajax({
			type: "post",
			url: url,
			data: {login: "user_a", password: "pass"},
			dataType : "json",
			success: function (response) {
			    if (response.logged_in == 1) {
			        $('#dutaContainer #application #userName').html(response.user_id);
			        dutaModel.myId = response.user_id;
			        getContactList();
			    }
			}
		});
	}

	function login2() {
	    $.ajax({
	        type: "post",
	        url: window.location.origin + "/Service/Login",
	        data: { login: "user_b", password: "pass" },
	        dataType: "json",
	        success: function (response) {
	            if (response.logged_in == 1) {
	                $('#dutaContainer #application #userName').html(response.user_id);
	                dutaModel.myId = response.user_id;
	                getContactList();
	            }
	        }
	    });
	}
	
	function logout(){
		$.ajax({
			type: "post",
			url: window.location.host+"/Service/logout",
			dataType : "json",
			success: function(response) {
				alert(response);
			}
		});
	}
	
	function afterLogin(response){
		alert('jest!!');

	}
	
	
	function renderMessage(model){
		var template = constants.templates.message;
		var rendered = Handlebars.compile(template)(model);
		var div = $('#messagesPanel .conversation#' + model.users + ' .tabMessages');
		if(div.length==0){
			prepareConversation(model);
		}
		$('#messagesPanel .conversation#' + model.users + ' .tabMessages').append(rendered);
		
	}
	function sendMessage(conversationId){
		messageText = $('#messagesPanel .conversation#'+conversationId+' .messageToSend').val();
		//jakis post a w jego callback:
		var data = {
		    users: [conversationId, dutaModel.myId],
			message : messageText
		}
		ajaxHelper.post(constants.urls.sendMessage,data,
			function (result) {
			    var date =new Date(result.timestamp);
				var model={
					users : conversationId,
					author : 'Ja',
					messageText : messageText,
					dateTime: date.toLocaleTimeString()
				};
				
				renderMessage(model);
				$('#messagesPanel .conversation#' + conversationId + ' .messageToSend').val('');
			}
		);
	}
	
	function getMessages(){
		ajaxHelper.poll(constants.urls.getMessage,{},getMessages,
			function(result){
				$.each(result, function(index, value ) {
				    var conversationId = value.users[0];
				    var date = new Date(value.timestamp);
					var model={
					    users: value.author,
						author: getActualContact(value.author).nickname,
						messageText : value.message,
						dateTime: date.toLocaleTimeString(),
						participants: getActualContact(value.author).nickname
					};
					renderMessage(model);
				});					
			}
		);
	}
	
	function renderContact(model){
		var template = constants.templates.contact;
		var rendered = Handlebars.compile(template)(model);
		
		$('#application #contacts').append(rendered);		
	}
	
	
	function getContactList(){
		//jakis post a w jego callback:
		ajaxHelper.post(constants.urls.getContactList,{},
			function(result){
				dutaModel.contacts = result;
				renderContactList(dutaModel.contacts);
				getMessages();
			}
		);
		var contacts = [
			{
			user_id : '1234',
			nickname : 'Tomek',
			status : 'available',
			user_id : 'w zyciu piekne sa..'
			},
			{
			user_id : '2345',
			nickname : 'Gosia',
			status : 'brb',
			description : 'tylko chwile'
			},
			{
			user_id : '3456',
			nickname : 'Adam',
			status : 'available',
			description : 'zyj'
			},
			{
			user_id : '4567',
			nickname : 'Przemek',
			status : 'brb',
			description : 'i daj rzyc innym'
			},
			{
			user_id : '5678',
			nickname : 'Kasia',
			status : 'inaccessible',
			description : ''
			}		
		];
		//renderContactList(contacts);
		
	}
	
	function renderContactList(contacts){
		$.each(contacts, function( index, value ) {
			renderContact(value);
		});
		
	}
	
	function startConversation(identificator){
	    //jakis post a w jego callback:
	    var cont = getActualContact(identificator);
		var model = {
			users : [identificator],
			participants: cont.nickname
		};
		prepareConversation(model);
	}
	
	function prepareConversation(model){
		if(!messagesPanelExists){
			var messagesPanel = constants.templates.messagesPanel;
			$('#dutaContainer').append(messagesPanel);
			messagesPanelExists = true;
		}
		var tabListItem = constants.templates.tabListItem;
		var renderedTab = Handlebars.compile(tabListItem)(model);
		$('#messagesPanel .nav.nav-tabs').append(renderedTab);
		
		var conversation = constants.templates.conversation;
		var rendered = Handlebars.compile(conversation)(model);
		$('#messagesPanel .tab-content').append(rendered);
		return rendered;
	}
	

	return{
		renderMessage : renderMessage,
		startConversation : startConversation,
		getContactList : getContactList,
		sendMessage : sendMessage,
		login: login,
		login2: login2,
		logout: logout
	
	};

}();

var ajaxHelper = function () { 
	
	function callAsync(url, callBack){
		var template;
		jQuery.ajax({
		 url:    url,
		 dataType : 'text',
		 success: callBack,
		 async:   true
		});
		return template;	
	}
	
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
	
	function post(url,data,success, failure){
		$.ajax({
			type: "POST",
			url: url,
			data: data,
			dataType : "json",
			success: success,
			traditional: true,
			failure: failure
		});
	}
	
	function poll(url,data, poll, success, failure ){
		$.ajax({
			type: "POST",
			url: url,
			data: data,
			dataType : "json",
			complete : poll,
			success: success,
			failure: failure
		});
	}
	
	

	return{
		callAsync : callAsync,
		getTemplate : getTemplate,
		post : post,
		poll: poll
	
	};

}();

var constants = function () { 
    var templates = {};
    contextRoot = window.location.origin + "/";
    templates.message = ajaxHelper.getTemplate(contextRoot+"Home/messageView");
    templates.tabListItem = ajaxHelper.getTemplate(contextRoot+"Home/tabListItemView");
    templates.messagesPanel = ajaxHelper.getTemplate(contextRoot+"Home/messagesPanelView");
    templates.conversation = ajaxHelper.getTemplate(contextRoot+"Home/conversationView");
    templates.contact = ajaxHelper.getTemplate(contextRoot+"Home/contactView");
	
	var urls = {
	    sendMessage : contextRoot+"Service/SendMessage",
	    getMessage : contextRoot+"Service/GetMessage",
		setStatus : "",
		getStatus : "",
		login : contextRoot+"Service/Login",
		logout : contextRoot+"Service/Logout",
		getContactList: contextRoot + "Service/GetContactList"
	}

	return{
		templates : templates,
		urls : urls
	
	};

}();
