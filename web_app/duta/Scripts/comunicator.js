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
	    
	    dutaModel.myLogin = loginName;
	    dutaModel.myStatus = 'available';
	    dutaModel.myDescription = '';
	    dutaModel.lastConvId = 0;
	    getContactList(true);
//	    getUserData(login);
	    getMyData();
//	    document.getElementsByTagName("body")[0].oncontextmenu = function (e) { e.preventDefault(); }
	});

	function getUserData(login){
	    ajaxHelper.post(constants.urls.getUserDataByLogin, {login: login},
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

	function getMyData() {
	    ajaxHelper.post(constants.urls.getMyData, {},
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
	
	function conferenceView() {
	    var model = {}
	    model.contacts = dutaModel.contacts;
	    var conferenceView = constants.templates.conferenceView;
	    var rendered = Handlebars.compile(conferenceView)(model);

	    $(rendered).dialog({
	        autoOpen: true,
	        modal: true,
	        resizable: false,
	        buttons: [
            {
                text: "OK",
                click: function () {
                    var checkBoxes = $('.conferenceCheckbox');
                    var users = [];
                    $.each(checkBoxes, function (index, value) {
                        if (value.checked == true) {
                            users.push(value.value);
                        }
                    });
                    $(this).dialog('destroy').remove();
                    var j=1;
                    var participants2 = getActualContact(users[0]).nickname;
                    for (j; j < users.length; j++) {
                        var kont = getActualContact(users[j]);
                        participants2 += ", " + kont.nickname
                    }
                    users.push(dutaModel.myId);
                    users.sort(function (a, b) { return a - b });

                    var conversationId = users[0];
                    var i=1;
                    for (i; i < users.length; i++) {
                        conversationId += 'a' + users[i];
                    }
                    var cont = 'Konferencja';
                    var model = {
                        users: conversationId,
                        participants: cont,
                        participants2: participants2
                    };
                    prepareConversation(model);

                    
                }
            }
	        ]
	    });

	    
	}

	function addConvToModel(users) {
	    var conversationModel = {};

	    dutaModel.conversations;
	    dutaModel.lastConvId;

	}

	function renderMessage(model){
		var template = constants.templates.message;
		var rendered = Handlebars.compile(template)(model);
		var div = $('#messagesPanel .conversation#' + model.users + ' .tabMessages');
		if(div.length==0){
			prepareConversation(model);
		}
		$('#messagesPanel .conversation#' + model.users + ' .tabMessages').append(rendered);
		var elem = $('#messagesPanel .conversation#' + model.users + ' .tabMessages');
		elem.scrollTop(elem[0].scrollHeight);
		
	}
	function sendMessage(conversationId){
		messageText = $('#messagesPanel .conversation#'+conversationId+' .messageToSend').val();
		//jakis post a w jego callback:
		var data = {
		    users: conversationId.split('a'),
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
				    var i=1;
				    for (i; i < value.users.length; i++) {
				        conversationId += 'a' + value.users[i];
				    }
				    var date = new Date(value.timestamp);
					var model={
					    users: conversationId,
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
	
	
	function getContactList(callAsync){
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
				if (callAsync) {
				    getMessages();
				    getStatusUpdate();
				}
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

	function setDescription() {
	    var desc = $('#userDesription');
	    var status = statusToNumber(dutaModel.myStatus);
	    var description = $(desc).val();
	    dutaModel.myDescription = description;
	    ajaxHelper.post(constants.urls.setStatus,
            {
                status: status,
                description: description
            },
            function () {
                desc.removeClass('statusActive');
                desc.addClass('statusInActive');

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

	    var users = [identificator, dutaModel.myId];
	    users.sort(function (a, b) { return a - b });

		var model = {
		    users: users[0] + 'a' + users[1],
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

	function closeConversation() {
	    $('#messagesPanel .nav.nav-tabs .active').remove();

	    $('#messagesPanel .tab-content .active').remove();
	    $('#messagesPanel .nav-tabs a:last').tab('show');
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
                    $(this).dialog('destroy').remove();
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
	    model.title = 'Archiwum: ' + getActualContact(user_id).nickname;
	    var userArchiveView = constants.templates.userArchiveView;
	    var compiledUserArchiveView = Handlebars.compile(userArchiveView)(model);
	    ajaxHelper.post(constants.urls.getArchiveFilteredByUserId,
            {
                from: from,
                to: to,
                userid: user_id
            },
			function (result) {
			    $(compiledUserArchiveView).dialog({
			        autoOpen: true,
			        modal: true,
			        resizable: false
			    });
			    /* lista:
                public List<int> users { get; set; }
                public int author { get; set; }
                public long timestamp { get; set; }
                public string message { get; set; }
                */
			    $.each(result, function (index, value) {
			        var message = {};
			        var date = new Date(value.timestamp);
			        message.dateTime = date.toLocaleTimeString();
			        if (value.author === dutaModel.myId) {
			            message.author = 'Ja'
			        } else {
			            var cont = getActualContact(value.author);
			            if (cont!=undefined)
			                message.author = cont.nickname;
			        }
			        message.messageText = value.message;
			        renderArchiveMessage(message);
			    });
			}
		);



	    
	}

	function renderArchiveMessage(model) {
	    var template = constants.templates.message;
	    var rendered = Handlebars.compile(template)(model);
	    var div = $('.archiveMessages');

	    $(div).append(rendered);

	}


	function addContactView() {
	    var addContactView = constants.templates.addContactView;
	    $(addContactView).dialog({
	        autoOpen: true,
	        modal: true,
	        resizable: false,
	        buttons: [
            {
                text: "OK",
                click: function () {
                    var login = $('#userLogin').val();
                    var nickname = $('#userNickname').val();
                    addContact(login, nickname);

                    $(this).dialog('destroy').remove();

                }
            }
	        ]
	    });
	}

	function addContact(login, nickname) {

	    ajaxHelper.post(constants.urls.addContact,
            {
                login: login,
                nickname: nickname
            },
			function (result) {
			    $('#application #contacts').html('');
			    getContactList(false);
			}
		);
	}

	function editContactView(login) {
	    var editContactView = constants.templates.editContactView;
	    $(editContactView).dialog({
	        autoOpen: true,
	        modal: true,
	        resizable: false,
	        buttons: [
            {
                text: "OK",
                click: function () {
                    var nickname = $('#userNickname').val();
                    editContact(login, nickname);

                    $(this).dialog('destroy').remove();
                }
            }
	        ]
	    });
	}

	function editContact(login, nickname) {

	    ajaxHelper.post(constants.urls.updateContact,
            {
                login: login,
                nickname: nickname
            },
			function (result) {
			    $('#application #contacts').html('');
			    getContactList(false);
			}
		);
	}

	function removeContact(login) {

	    ajaxHelper.post(constants.urls.removeContact,
            {
                login: login
            },
			function (result) {
			    $('#application #contacts').html('');
			    getContactList(false);
			}
		);
	}

	function descriptionFocus() {
	    var desc = $('#userDesription');
	    desc.removeClass('statusInActive');
	    desc.addClass('statusActive');
	}

	return{
		renderMessage : renderMessage,
		startConversation : startConversation,
		getContactList : getContactList,
		sendMessage: sendMessage,
		setMyStatus: setMyStatus,
		showContextMenu: showContextMenu,
		selectArchiveDialog: selectArchiveDialog,
		addContactView: addContactView,
		editContactView: editContactView,
		removeContact: removeContact,
		conferenceView: conferenceView,
		closeConversation: closeConversation,
		descriptionFocus: descriptionFocus,
		setDescription: setDescription
	
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
    templates.addContactView = ajaxHelper.getTemplate(contextRoot + "Home/addContactView");
    templates.editContactView = ajaxHelper.getTemplate(contextRoot + "Home/editContactView");
    templates.conferenceView = ajaxHelper.getTemplate(contextRoot + "Home/conferenceView");
	
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
		getUserDataByLogin: contextRoot + "Service/GetUserDataByLogin",
		getMyData: contextRoot + "Service/GetMyData",
		getArchiveFilteredByUserId: contextRoot + "Service/GetArchiveFilteredByUserId",
		addContact: contextRoot + "Service/AddContact",
		updateContact: contextRoot + "Service/UpdateContact",
		removeContact: contextRoot + "Service/RemoveContact"
	}

	return{
		templates : templates,
		urls : urls
	
	};

}();
