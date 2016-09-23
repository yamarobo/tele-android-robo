var express = require('express');
var app = express();

var http = require('http').Server(app);
var io = require('socket.io')(http);
var fs = require('fs');
var path = require('path');
var dateformat = require("dateformat");

app.use('/', express.static(path.join(__dirname, 'stream')));

app.get('/', function(req, res) {
	res.sendFile(__dirname + '/index.html');
});

var sockets = {};

var warchfile = '/home/root/nodejs/stream/image_stream.jpg';

io.on('connection', function(socket) {
	console.log('io.on');
	sockets[socket.id] = socket;
	console.log("Total clients connected : ", Object.keys(sockets).length);

	socket.on('disconnect', function() {

		delete sockets[socket.id];
		if (Object.keys(sockets).length == 0) {
			app.set('watchingFile', false);
			fs.unwatchFile(warchfile);
		}
	});
	socket.on('start-stream', function() {
		startStreaming(io);
	});
});

http.listen(3000, function() {
	console.log('listening on *:3000');
});

function stopStreaming() {
	console.log('stopStreaming');

	if (Object.keys(sockets).length == 0) {
		app.set('watchingFile', false);
		fs.unwatchFile(warchfile);
	}
}

function startStreaming(io) {
	console.log('startStreaming');

	if (app.get('watchingFile')) {
		io.sockets.emit('liveStream', {url:'image_stream.jpg?_t=' + (Math.random() * 100000), time:dateformat(new Date(), "yyyy/mm/dd HH:MM:ss")});
		return;
	}
	
	//fs.watchFile(__dirname+'/stream/image_stream.jpg', function(current, previous) {
	fs.watchFile(warchfile , function(current, previous) {
				console.log('emmit liveStewam');
		io.sockets.emit('liveStream', {url:'image_stream.jpg?_t=' + (Math.random() * 100000), time:dateformat(new Date(), "yyyy/mm/dd HH:MM:ss")});
	})

}
