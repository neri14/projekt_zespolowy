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

	$(document).ready(function () {
	    var loginName = $('#dutaContainer #application #userName').html();
	    getUserData(login);
	    dutaModel.myLogin = loginName;
	    dutaModel.myStatus = 'available';
	    dutaModel.myDescription = '';
	    getContactList();
//	    document.getElementsByTagName("body")[0].oncontextmenu = function (e) { e.preventDefault(); }
	});

	function getUserData(login){
	    ajaxHelper.post(constants.urls.getUserData, {login: login},
			function (result) {
			    /*
                public int user_id { get; set; }
                public string login { get; set; }
                public int status { get; set; }
                public string description { get; set; }
                */
			    dutaModel.myId = result.user_id;
			}
		);
	}
	
	function getActualContact(id) {
	    var cont = {};
		$.each(dutaModel.contacts, function(index, value ) {
			if(id==value.user_id){
			    cont = value;
			}
		});
		return cont;
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
		    users: [conversationId.split('a')[0], dutaModel.myId],
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
					    users: value.author+'a',
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
		ajaxHelper.post(constants.urls.getContactList,{},
			function (result) {
			    /*
                public int user_id { get; set; }
                public string login { get; set; }
                public string nickname { get; set; }
                public int status { get; set; }
                public string description { get; set; }
                */
			    $.each(result, function (index, value) {
			        value.status = statusToString(value.status);
			    });
				dutaModel.contacts = result;
				renderContactList(dutaModel.contacts);
				getMessages();
				getStatusUpdate();
			}
		);		
	}
	
	function renderContactList(contacts){
		$.each(contacts, function( index, value ) {
			renderContact(value);
		});	
	}

	function setMyStatus(status) {
	    var oldStatus = dutaModel.myStatus;
	    dutaModel.myStatus = status;
	    var stNr = statusToNumber(dutaModel.myStatus)
	    ajaxHelper.post(constants.urls.setStatus,
            {
                status: stNr,
                description: dutaModel.myDescription
            },
            function () {
                var userStatus = $('#userStatus');
                $(userStatus).removeClass(oldStatus);
                $(userStatus).addClass(dutaModel.myStatus);
                
            }
        );
	}

	function getStatusUpdate() {
	    ajaxHelper.poll(constants.urls.getStatusUpdate, {},getStatusUpdate,
			function (result) {
			    $.each(result, function (index, value) {
			        updateStatus(value);
			    });
			    
			}
		);
	}

	function updateStatus(model) {
	    /*model
			    public int user_id { get; set; }
			    public int status { get; set; }
			    public string description { get; set; }
                */
	    var oldModel;
	    $.each(dutaModel.contacts, function (index, value) {
	        if (value.user_id == model.user_id) {
	            oldModel = value;
	        }
	    }); 
	    var desc = $('#application #contacts #' + model.user_id + ' .description');
	    desc.html(model.description);
	    oldModel.description = model.description;
	    var stat = $('#application #contacts #' + model.user_id + ' .status');
	    $(stat).removeClass(oldModel.status);
	    $(stat).addClass(statusToString(model.status));
	    oldModel.status = statusToString(model.status);

	}
	
	function startConversation(identificator){
	    //jakis post a w jego callback:
	    var cont = getActualContact(identificator);
		var model = {
			users : identificator+'a',
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
		$('#messagesPanel .nav-tabs a:last').tab('show');
		return rendered;
	}

	function statusToString(nr) {
	    if (nr == 0) {
	        return 'available';
	    }
	    if (nr == 1) {
	        return 'brb';
	    }
	    if (nr == 2) {
	        return 'inaccessible';
	    }
	}

	function statusToNumber(str) {
	    if (str === 'available') {
	        return 0;
	    }
	    if (str === 'brb') {
	        return 1;
	    }
	    if (str === 'inaccessible') {
	        return 2;
	    }
	}

	function showContextMenu(user_id) {
	    var $contextMenu = $('#application #contacts #' + user_id + ' .contextMenu');

	    $contextMenu.css({
	        display: "block"
	    });

	    $contextMenu.on("click", "a", function () {
	        $contextMenu.hide();
	    });

	    $(document).click(function () {
	        $contextMenu.hide();
	    });
	    return false;
	}


	function selectArchiveDialog(user_id) {

	    var archiveView = constants.templates.archiveView;
//	    var renderedArchive = Handlebars.compile(archiveView)(model);
	    $(archiveView).dialog({
	        autoOpen: true,
	        modal: true,
            resizable: false,
	        buttons: [
            {
                text: "OK",
                click: function () {
                    var from = new Date($('#datepickerFrom').val()).getTime();
                    var to = new Date($('#datepickerTo').val()).getTime();
                    $('#datepickerFrom').remove();
                    $('#datepickerTo').remove();
                    $(this).dialog("close");
                    openUserArchiveDialog(user_id, from, to);
                }
            }
	        ]
	    });

	    $('#datepickerFrom').datetimepicker();
	    $('#datepickerTo').datetimepicker();
	}

	function openUserArchiveDialog(user_id, from, to) {
	    var model = {};
	    model.user_id = user_id;
	    model.title = 'Archiwum: ' + user_id;
	    var userArchiveView = constants.templates.userArchiveView;
	    var compiledUserArchiveView = Handlebars.compile(userArchiveView)(model);
	    
	    ajaxHelper.post(constants.urls.getArchiveFilteredByUserId,
            {
                from: from,
                to: to,
                userid: user_id
            },
			function (result) {
			    /* lista:
                public List<int> users { get; set; }
                public int author { get; set; }
                public long timestamp { get; set; }
                public string message { get; set; }
                */
			    $.each(result, function (index, value) {
			        var date = new Date(value.timestamp);
			        value.dateTime = date.toLocaleTimeString();
			    });
			    model.messages = result;
			}
		);



	    $(compiledUserArchiveView).dialog({
	        autoOpen: true,
	        modal: true,
	        resizable: false
	    });
	}

	return{
		renderMessage : renderMessage,
		startConversation : startConversation,
		getContactList : getContactList,
		sendMessage: sendMessage,
		setMyStatus: setMyStatus,
		showContextMenu: showContextMenu,
		selectArchiveDialog: selectArchiveDialog
	
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
    templates.contact = ajaxHelper.getTemplate(contextRoot + "Home/contactView");
    templates.archiveView = ajaxHelper.getTemplate(contextRoot + "Home/archiveView");
    templates.userArchiveView = ajaxHelper.getTemplate(contextRoot + "Home/userArchiveView");
	
	var urls = {
	    sendMessage : contextRoot+"Service/SendMessage",
	    getMessage : contextRoot+"Service/GetMessage",
		setStatus : "",
		getStatus : "",
		login : contextRoot+"Service/Login",
		logout : contextRoot+"Service/Logout",
		getContactList: contextRoot + "Service/GetContactList",
		getStatusUpdate: contextRoot + "Service/GetStatusUpdate",
		setStatus: contextRoot + "Service/SetStatus",
		getUserData: contextRoot + "Service/GetUserData",
		getArchiveFilteredByUserId: contextRoot + "Service/GetArchiveFilteredByUserId"
	}

	return{
		templates : templates,
		urls : urls
	
	};

}();
