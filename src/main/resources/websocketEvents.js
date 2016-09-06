//Establish the WebSocket connection and set up event handlers
var webSocket = new WebSocket("ws://localhost:9090/matsim/events");
webSocket.onmessage = function (msg) { updateEvents(msg); };
webSocket.onclose = function () { alert("WebSocket connection closed") };

id("send").addEventListener("click", function () {
    sendMessage(id("message").value);
});

id("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) { sendMessage(e.target.value); }
});

function sendMessage(message) {
    if (message !== "") {
        webSocket.send(message);
        id("message").value = "";
    }
}

function updateEvents(msg) {
	var data = msg.data;
	insert("events", "<br/>");
	insert("events", data);
}

//Helper function for inserting HTML as the first child of an element
function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message);
}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}