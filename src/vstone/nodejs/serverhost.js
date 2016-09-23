/*Web コンテンツを開発するための Node.js 簡易 Web サーバー サンプル*/ 
//Web サーバーが Listen する ポート 
var LISTEN_PORT = 8080; 
//ファイル名が指定されない場合に返す既定のファイル名 
var DEFAULT_FILE = "default.html";


var http = require('http');
var fs = require('fs');



var photoviewer = require('./photoviewer/photoviewer.js');

function setSerialcode2Configfile(){
	var CONFIG_FILE = "./info.json";
	var SERIALCODE_FILE = "/home/vstone/vstonemagic/update/lcc.conf";
	fs.readFile(SERIALCODE_FILE, 'utf8', function (err, text) {
		if(err){ 
			console.log("set serial to configfile error");
		}else{
			var config = require(CONFIG_FILE);
			config.serial = text.replace("\n","");
			fs.writeFile(CONFIG_FILE, JSON.stringify(config, null, '    '));
		    console.log("set serial to configfile "+text);
		}
	});
}

setSerialcode2Configfile();

//拡張子を抽出 
function getExtension(fileName) { 
	var fileNameLength = fileName.length; 
	var dotPoint = fileName.indexOf('.', fileNameLength - 5 ); 
	var extn = fileName.substring(dotPoint + 1, fileNameLength); 
	return extn; 
}

//content-type を指定 
function getContentType(fileName) { 
	var extentsion = getExtension(fileName).toLowerCase(); 
	var contentType = { 
			'html': 'text/html', 
			'htm' : 'text/htm', 
			'css' : 'text/css', 
			'js' : 'text/javaScript; charset=utf-8', 
			'json' : 'application/json; charset=utf-8', 
			'xml' : 'application/xml; charset=utf-8', 
			'jpeg' : 'image/jpeg', 
			'jpg' : 'image/jpg', 
			'gif' : 'image/gif', 
			'png' : 'image/png', 
			'mp3' : 'audio/mp3', 
	}; 

	var contentType_value = contentType[extentsion]; 
	if(contentType_value === undefined){ 
		contentType_value = 'text/plain';}; 
		return contentType_value; 
}


var sys = require ('sys'),
url = require('url'),
qs = require('querystring');

//Web サーバーのロジック 
var server  = http.createServer(
		function(request, response){ 
			console.log('Requested Url:' + request); 
			var url_parts = url.parse(request.url,true);
			var requestedFile = url_parts.pathname;
			console.log(url_parts.pathname);
			console.log(url_parts.query);

			if(request.method=='POST') {
				if(requestedFile == '/photoviewer/remove.php'){
					photoviewer.removephoto(request, response);
				}
				else{
					var body='';
					request.on('data', function (data) {
						body +=data;
					});
					request.on('end',function(){
						var POST =  qs.parse(body);
						console.log(POST);
					});
				}
			}
			else if(request.method=='GET') {
				/*これなに？*/
				requestedFile = (requestedFile.substring(requestedFile.length - 1, 1) === '/') 
				? requestedFile + DEFAULT_FILE : requestedFile; 
	
				console.log('File Extention:' + getExtension( requestedFile)); 
				console.log('Content-Type:' + getContentType( requestedFile)); 
	
				if(requestedFile == '/photoviewer/index.html'){
					photoviewer.showPhotoViewer(response,"gallery");
				}
				else if(requestedFile == '/photoviewer/select.html'){
					photoviewer.showPhotoViewer(response,"select");
				}
				else{
					fs.readFile('.' + requestedFile,'binary', function (err, data) { 
						if(err){ 
							response.writeHead(404, {'Content-Type': 'text/plain'}); 
							response.write('not found\n'); 
							response.end();    
						}else{ 
							var header = {
									"Access-Control-Allow-Origin":"*",
									"Pragma": "no-cache",
									"Cache-Control" : "no-cache",
									'Content-Type': getContentType(requestedFile)
								}
							response.writeHead(200,header); 
							response.write(data, "binary"); 
							response.end(); 
						} 
					});
				}
			}
		} 
);

server.listen(LISTEN_PORT); 
console.log('Server running at '+ LISTEN_PORT);


/**
 * Jpeg Streaming
 */
//var express = require('express');
//var app = express();

//var http = require('http').Server(app);
var io = require('socket.io')(server);
//var fs = require('fs');
var path = require('path');
var dateformat = require("dateformat");

//app.use('/', express.static(path.join(__dirname, 'stream')));

var sockets = {};
var warchfile_fullpath = '/home/root/nodejs/stream/image_stream.jpg';
var warchfile = 'stream/image_stream.jpg';

var iswatchingFile = false;

io.on('connection', function(socket) {
	console.log('io.on');
	sockets[socket.id] = socket;
	console.log("Total clients connected : ", Object.keys(sockets).length);

	socket.on('disconnect', function() {
		delete sockets[socket.id];
		if (Object.keys(sockets).length == 0) {
			//app.set('watchingFile', false);
			iswatchingFile = false;
			fs.unwatchFile(warchfile_fullpath);
		}
	});
	
	socket.on('start-stream', function() {
		startStreaming(io);
	});
});

function stopStreaming() {
	console.log('stopStreaming');

	if (Object.keys(sockets).length == 0) {
		//app.set('watchingFile', false);
		iswatchingFile = false;
		fs.unwatchFile(warchfile_fullpath);
	}
}

function startStreaming(io) {
	console.log('startStreaming');

	//if (app.get('watchingFile')) {
	if (iswatchingFile) {
			io.sockets.emit('liveStream', {url:warchfile+'?_t=' + (Math.random() * 100000), time:dateformat(new Date(), "yyyy/mm/dd HH:MM:ss")});
		return;
	}
	
	//fs.watchFile(__dirname+'/stream/image_stream.jpg', function(current, previous) {
	fs.watchFile(warchfile_fullpath , function(current, previous) {
				console.log('emmit liveStewam');
		io.sockets.emit('liveStream', {url:warchfile+'?_t=' + (Math.random() * 100000), time:dateformat(new Date(), "yyyy/mm/dd HH:MM:ss")});
	})

}

