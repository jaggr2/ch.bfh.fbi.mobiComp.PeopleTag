
/**
 * Module dependencies.
 */

var express = require('express');
var routes = require('./routes');
var user = require('./routes/user');
var http = require('http');
var path = require('path');
// Database
var mongo = require('mongoskin');
var db = mongo.db("mongodb://localhost:27017/peopletag", {native_parser:true});

var app = express();

// all environments
app.set('port', process.env.PORT || 3000);
app.use(express.logger('dev'));
app.use(express.json());
app.use(express.urlencoded());
app.use(express.methodOverride());
app.use(app.router);
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');
app.use(express.static(path.join(__dirname, 'public')));
app.use(express.favicon(path.join(__dirname, 'public/images/favicon.ico')));

// development only
if ('development' == app.get('env')) {
  app.use(express.errorHandler());
}

app.get('/', routes.index);
app.get('/users', user.userlist(db));
app.post('/users', user.adduser(db));
app.get('/users/:id', user.getuser(db));
app.post('/users/:id', user.updateuser(db));
app.delete('/users/:id', user.deleteuser(db));
app.post('/pairing/:firstId/:secondId', user.addpairing(db));
app.delete('/pairing/:firstId/:secondId', user.deletepairing(db));
app.get('/users/paired-with/:id', user.getPairedWith(db));

http.createServer(app).listen(app.get('port'), function(){
  console.log('NodeJS server listening on port ' + app.get('port'));
});
