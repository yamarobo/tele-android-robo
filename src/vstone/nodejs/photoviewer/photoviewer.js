var fs = require('fs');
var qs = require('querystring');

exports.showPhotoViewer = function(response , mode) { 
	var photodir = './photoviewer/photo/';
	fs.readdir(photodir, function(err, files){
		if (err){

		}else{
			var fileList = [];
			files.filter(function(file){
				console.log(file);	
				return fs.statSync(photodir+file).isFile() &&  /.*\.jpg$/.test(photodir+file); //é‚¨æ§­?½ŠéœŽ?½¼ç¸º?½¿
			}).forEach(function (file) {
				fileList.push(file);
			});
			console.log(fileList);	
			var filename;
			if(mode== "gallery"){
				filename = './photoviewer/index.html';
			}
			else if(mode== "select"){
				filename = './photoviewer/select.html';
			}
			
			fs.readFile(filename,'utf8', function (err, text) {
				if(err){ 

				}
				else{
					var apends = "";
					var base;
					if(mode== "gallery"){
						base ='<a href="{photopath}"><span class="cover" style="background-image: url(\'{thumbs}\')"></span></a>';
					}
					else if(mode== "select"){
						base ='<div class="aspectwrapper" file="{photopath}"><span class="cover" style="background-image: url(\'{thumbs}\')"></span></div>';	
					}
						//var base ='<li><img src="{thumbs}" class="myphotos" rel="group1" data-glisse-big="{photopath}" title="1" id="thumb1"/></li>\n';
					for(i = 0 ; i < fileList.length ; i++){
						var apend = base.replace('{thumbs}', 'photo/thumbs/' + fileList[i]).replace('{photopath}', 'photo/' + fileList[i]);
						apends += apend;
					}
					console.log("append:" + apends);	
					
					text = text.replace('{photolist}',apends);
					response.writeHead(200, {'Content-Type':'text/html'}); 
					response.write(text); 
					response.end(); 
				}
			}
			);
		}
	});
}


exports.removephoto = function(request , response ) { 
    console.log("removephoto");
	var body='';
	request.on('data', function (data) {
		body +=data;
	});
	request.on('end',function(){
		var POST =  qs.parse(body);
		console.log(POST);

		/*
		
		var json = JSON.stringify(POST);
		console.log(json);
		var json = JSON.parse(json);
		console.log(json);
		
		var names=Object.keys(json);
		console.log(names);	// ["foo","baz"] ç¸º?½®ç¹§åŒ»â‰§ç¸º?½«èœ?½ºèœ‰å¸™ï¼?ç¹§å¾Œï½?
		*/
		var list = POST['remove_data[]'];
		if(Array.isArray(list)){
	    	for(i = 0 ; i < list.length ; i++){
	            console.log(list[i]);
	            removefile('photoviewer/' + list[i]);
	    	}
		}
		else{
			console.log(list);
            removefile('photoviewer/' + list);
		}
		response.writeHead(200); 
		response.end(); 
	});
}

function removefile(filepath){
	fs.unlink(filepath, function (err) {
		  if (err){
			  console.log('Error deleted '+filepath);
		  }else{
			  console.log('successfully deleted '+filepath);
		  }
		});
}
