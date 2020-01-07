//////////////////////////////////////////////초기 설정

var express = require('express');
var http = require("http");
var bodyParser = require('body-parser');
var app = express();
var router = express.Router();
var MongoClient = require('mongodb').MongoClient;
app.use(bodyParser.urlencoded({extended:false}));
app.use(bodyParser.json());

const host = '192.249.19.254'
const port = 80;
app.set('port', port);

////////////////////////////////////////////////////////////스키마 생성

var mongoose = require('mongoose');
const Schema = mongoose.Schema;
const telSchema = new mongoose.Schema({
    name: String,
    tel: String
});
const diarySchema = new mongoose.Schema({
    day: String,
    text: String
});
var imgSchema = new Schema({
    imageid: String,
    imageuri: String
});


const TelBook = mongoose.model('telbook', telSchema);
const Diary = mongoose.model('diarybook', diarySchema);
const Image = mongoose.model("image", imgSchema);
/////////////android 에서 가져오기 + 중복 제외 db 쓰기

app.post('/tels', (req, res) => {
    var tel = new TelBook();
    tel.name = req.body.name;
    tel.tel = req.body.tel;
    TelBook.findOne({name: tel.name}, (err, tels) => {
        if (err) return res.status(500).send({error: 'database failure'});
        if(!tels){
            console.log("save"+tel.name);
            tel.save();
        }
        else{
            if(tels) console.log("중복");}
    });
});

app.get('/tels', (req, res) => {
    let gettel = tel_col.find();
    var resarray = new Array();
    var cnt = 0;
    gettel.forEach(async (cursor) => {
        var aJson = new TelBook();
        aJson.name = cursor.name;
        aJson.tel = cursor.tel;
        console.log(JSON.stringify(aJson));
        await resarray.push(aJson);
        cnt++;
        // if (image_col.find() == cnt) console.log(JSON.stringify(resarray));
    }).then(()=>{
        console.log("Download....");
        console.log(JSON.stringify(resarray));
        res.json(resarray);
    });
})
/////////////////////////일기장 가져오고 + 중복 제외 db쓰기//////////////////////
app.post('/diary', (req, res) => {
    var dia = new Diary();
    dia.day = req.body.day;
    dia.text = req.body.text;
    Diary.findOne({day: dia.day}, (err, diary) => {
        if (err) return res.status(500).send({error: 'database failure'});
        if(!diary){
            console.log("save "+dia.day);
            dia.save();
        }
        else{
            if(diary) console.log("중복");}
    });
});

app.get('/diary', (req, res) => {
    let getdiary = diary_col.find();
    var resarray = new Array();
    var cnt = 0;
    getdiary.forEach(async (cursor) => {
        var aJson = new Diary();
        aJson.day = cursor.day;
        aJson.text = cursor.text;
        console.log(JSON.stringify(aJson));
        await resarray.push(aJson);
        cnt++;
        // if (image_col.find() == cnt) console.log(JSON.stringify(resarray));
    }).then(()=>{
        console.log("Download....");
        console.log(JSON.stringify(resarray));
        res.json(resarray);
    });
})

///////////////////////////갤러리 DB get///////////////////////
app.get('/imagedown', (req, res)=>{
    let getimg = image_col.find();
    var firstJson = new Object();
    var resarray = new Array();
    var cnt = 0;
    getimg.forEach(async (cursor) => {
        var aJson = new Image();
        aJson.imageid = cursor.imageid;
        aJson.imageuri = cursor.imageuri;
        console.log(JSON.stringify(aJson));
        await resarray.push(aJson);
        cnt++;
        // if (image_col.find() == cnt) console.log(JSON.stringify(resarray));
    }).then(()=>{
        console.log("Download....");
        console.log(JSON.stringify(resarray));
        res.json(resarray);
    });
    // console.log("Download.... ");
    // console.log(JSON.stringify(resarray));
});



//////////////////////////갤러리 DB post///////////////////////
app.post('/imageup', (req, res)=>{
    var img = new Image();
    img.imageid = req.body.imageid;
    img.imageuri = req.body.imageuri;
    console.log("Upload.... ");
    image_col.findOne({"imageid":img.imageid} || {"imageuri":img.imageuri}, (err, imgs) => {
        if (err) return res.status(500).send({error: "database failure"});
        if (!imgs) {
            img.save( (err, img) => {
                if (err) {
                    console.error(err);
                    res.send("result: 0");
                    return;
                }
                console.log(img.imageid + " is ID and "+img.imageuri+ "is URI");
                res.send("result: 1");
            });
            image_col.insertOne(img);
        }
        else console.log("already exists");
    })
    
    
});


///////////////////////////mongodb랑 연결//////////////////////
var db = mongoose.connection;
db.on('error', console.error);
db.once('open', function(){
    // CONNECTED TO MONGODB SERVER
    console.log("Connected to mongod server");
});
mongoose.connect('mongodb://localhost/System', {useNewUrlParser: true, useUnifiedTopology: true});
let image_col = db.collection('images');
let tel_col = db.collection('telbooks');
let diary_col = db.collection('diarybooks');

// app.get('/imagecnt', (req, res)=>{  
//     var c;
//     var d = image_col.countDocuments();
//     console.log(d);
//     res.send(d.toString());
//     image_col.countDocuments().then((count) => {
//         c = count;
//         console.log(c);
//         res.send(c.toString());
//     })
// })

//////////////////////////////////////////

var server = http.createServer(app).listen(app.get('port'), function() {
    console.log("Web Server Executing : "+ app.get('port'));
});


//////////////////////////////////////////////////////////////