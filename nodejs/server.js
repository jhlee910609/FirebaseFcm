
// ------- fcm 설정 -------
var fcmUrl = "https://fcm.googleapis.com/fcm/send"; 
var serverKey = "AAAA9K6-ArA:APA91bGIKHd0XguAQJ1BYKc31mUdktumEI0CI1t1NJhkBFxNO8Zp9ruHj7O5tLXqSVFWo7T5X9P848LrZdwlhwtwjBw0kiUxSAg69XBm8ym1O_kWkSc3ainkaTjtDpZaZyQSnmXJubST";

var message = {
	to : "",
	notification : {
		title : "노티바 타이틀",
		body : ""
	}
};

// ------- node -----------
// 1. http 서버모듈 가져오기
var http = require("http");
var url = require("url");
var httpUrlConnection = require("request");
var querystring = require("querystring");

// 2. http 모듈로 서버 생성하기
var server = http.createServer( function (request, response){
	// 4. 사용자 요청이 발생하면 요청 자원을 분리하고 
	var parsedUrl = url.parse(request.url);
	var path = parsedUrl.pathname;

	// 5. send_notification을 체크
	if(path == "/send_notification"){
		if(request.method == "POST"){
			// 6. 스마트폰에서 전송된 데이터를 message 객체에 담고
			var postdata = "";
			request.on('data',function(data){
				postdata = postdata + data;
			});
			request.on('end',function(){
				
				console.log("new postdata:"+postdata);

				var dataObj = JSON.parse(postdata);
				
				console.log(dataObj);

				message.to = dataObj.token;
				message.notification.body = dataObj.msg;

				console.log(message);

				// 7. httpUrlConnection 으로 FCM 서버로 전송
				httpUrlConnection({
					// fcm 서버로 데이터 전송
					url : fcmUrl,
					method : "POST",
					headers : {
						"Authorization":"key="+serverKey,
			        	"Content-Type":"application/json"
					},
					body : JSON.stringify(message)
				}, function (error, res, body){
					// fcm 서버로부터 결과처리 상태를 받음
					console.log("after: error="+error+", code="+res.statusCode);
					if(!error && res.statusCode == 200){
						// 8. 결과처리 상태 스마트폰으로 전송
						response.writeHead(200, {'Content-Type' : 'application/json'});
						response.end('{"result_status":"'+body+'"}');
					}else{
						response.writeHead(501, {'Content-Type' : 'application/json'});
						response.end('{"result_status":"'+body+'"}');
					}
				});
			});
		}else{
			send404(response);
		}
	}else{
		send404(response);
	}
} );

function send404(response){
	response.writeHead(404, {'Content-Type' : 'application/json'});
	response.end('{"result_status":"404 page not found"}');
}

// 3. 서버가 로드되면 알려주고, 사용자 요청 대기하기
server.listen(8080, function(){ 
	console.log("server is running...");
});