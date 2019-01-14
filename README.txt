################################################################
Requests
################################################################
OPTIONS/GET - /options    |   for options list
POST - /check             |   to send query
################################################################
HTTP POST request parameters
################################################################

db_name - required|Parameter of database name,
execution_times - default=1|Quantity of query check iterations,
query - required|Parameter that contains your db query,
timeout - default=100sec|Max value of query execution

################################################################
example of post request via browser console
################################################################
var http = new XMLHttpRequest();
var url = '/check';
var params = JSON.stringify({db_name: 'test1', query: 'select * from user'});
http.open('POST', url, true);

//Send the proper header information along with the request
http.setRequestHeader('Content-type', 'application/json');

http.onreadystatechange = function() {//Call a function when the state changes.
    if(http.readyState == 4 && http.status == 200) {
        alert(http.responseText);
    }
}
http.send(params);
