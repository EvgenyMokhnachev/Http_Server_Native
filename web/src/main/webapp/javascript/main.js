var openWSBtn = document.getElementById('openWS');

var webSocket = undefined;

openWSBtn.onclick = function(){
    if(webSocket) webSocket.close();
    webSocket = new WebSocket('ws://localhost:8080/wstest');

    webSocket.onopen = function(event) {
        alert('onopen');
        webSocket.send("Hello Web Socket!");
    };

    webSocket.onmessage = function(event) {
        alert('onmessage, ' + event.data);
    };

    webSocket.onclose = function(event) {
        alert('onclose');
    };
};