var uri = "ws://" + location.host + '/api';
var webSocket = null;

window.onload = function() {
  open();
  MessageBox.setUpMessageBox();
};

// MessageBox start
MessageBox = {
  'setUpMessageBox': function() {
    var sendMessage = document.getElementById('submitMessage');
    sendMessage.addEventListener('click', MessageBox.submitMessage);
  },
  'submitMessage': function(e) {
    e.preventDefault();
    var controller  = document.querySelector('select[name="controller"]').value;
    var action      = document.querySelector('input[name="action"]').value;
    var keys        = document.querySelectorAll('input[name="data[key][]"]');
    var vals        = document.querySelectorAll('input[name="data[val][]"]');
    var data        = {};
    for (var i = 0; i < keys.length; i++) {
      data[keys[i].value]  = vals[i].value;
    }


    var message = {
      'controller': controller,
      'action'    : action,
      'data'      : data,
    };
    var jsonString = JSON.stringify(message);
    webSocket.send(jsonString);
    MessageBox.displayMessage(jsonString, null, 2);
    messageBox.value = '';
  },
  'displayMessage': function(message) {
    var itemList = document.getElementById('itemList');
    var item = document.createElement('li');
    item.innerText = message;
    itemList.appendChild(item);
  },
}
// MessageBox end

// ConnectionHandler start
ConnectionHandler = {
  'connectionStatus': document.getElementById('connectionStatus'),
  'notConnected'   : function() { 
    connectionStatus.innerText   = 'not connected'; 
    connectionStatus.style.color = 'maroon';
  },
  'connected'      : function() {
    connectionStatus.innerText = 'connected';
    connectionStatus.style.color = 'lime';
  },
  'disconnected'   : function() {
    connectionStatus.innerText = 'disconnected';
    connectionStatus.style.color = 'maroon';
  },
  'checkConnection': function() {
    if (webSocket) {
      ConnectionHandler.connected();
    } else {
      ConnectionHandler.disconnected();
    }
  }
}
setInterval(ConnectionHandler.checkConnection, 1000);
// ConnectionHandler end


// WebSocket
function open() {
  if (webSocket == null) {
    webSocket = new WebSocket(uri);
    webSocket.onopen    = onOpen;
    webSocket.onmessage = onMessage;
    webSocket.onclose   = onClose;
    webSocket.onerror   = onError;
  }
}

function onOpen(event) {
	console.log('connecting');
}

function onMessage(event) { 
	if (!event || !event.data) return;
	console.log(event.data); 
}

function onClose(event) {
	console.log('disconnected!');
	ConnectionHandler.checkConnection();
}

function onError(event) {
	console.log('error!');
	ConnectionHandler.checkConnection();
}