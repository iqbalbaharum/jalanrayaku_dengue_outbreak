var mongo = require('mongojs');
var db = mongo('mongodb_user:mongodb_pwd@localhost:27017/datahack',['dengue','dengue_failed']);
var path    = require("path");
var request = require('request');

var api_key = "XXXXXXXXXXXX";
var geo_url = "https://maps.googleapis.com/maps/api/geocode/json";


var csv = require('csv-parser');
var fs = require('fs');

fs.createReadStream('dengue_failed.csv')
  .pipe(csv())
  .on('data', function (data) {
    var split = data.location.split("(");
    var lat, lng;
    
    console.log("|"+split[0]);
    setTimeout(function(){
      request({
          url: geo_url + "?address="+encodeURIComponent(split[0]+" "+data.district+" "+data.state+" Malaysia")+"&key="+api_key,
          json: true
      }, function (error, response, body) {
          if (!error && response.statusCode === 200) {
              
              if(body.results.length > 0){
                if(body.results[0].geometry) {
                  console.log(body.results[0].geometry.location.lat+", "+body.results[0].geometry.location.lng);
                  lng = body.results[0].geometry.location.lng;
                  lat = body.results[0].geometry.location.lat;

                  db.dengue.insert({
                    year: parseInt(data.year),
                    week: parseInt(data.week),
                    state: data.state,
                    district: data.district,
                    location: data.location,
                    case_no: parseInt(data.case_no),
                    case_day: parseInt(data.case_day),
                    loc: {
                      type: "Point",
                      coordinates: [lng, lat]
                    }
                  });
                }
              } else {
                db.dengue_failed.insert({
                    year: parseInt(data.year),
                    week: parseInt(data.week),
                    state: data.state,
                    district: data.district,
                    location: data.location,
                    case_no: parseInt(data.case_no),
                    case_day: parseInt(data.case_day)
                  });
              }
          }
      });
  }, 300);
    
  });