<!doctype html>
<html lang="en">
  <head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<meta name="description" content="">
    <meta name="author" content="">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="css/bootstrap.min.css" />
	<link rel="stylesheet" href="css/mapStyle.css" />
	<link rel="stylesheet" href="css/markerStyle.css" />

    <title>Senary</title>
    <script type="text/javascript" src="http://www.google.com/jsapi"></script>
  </head>
  
  <body>
	<!-- Navbar -->
	<nav class="navbar navbar-dark" style="background-color:#696969;">
	  <a class="navbar-brand" href="#">
		<img src="img/logo.png" width="30" height="30" class="d-inline-block align-top">
		Senary
	  </a>
		<ul class="nav justify-content-end">
		  <li class="nav-item">
			<a class="nav-link active" href="#">Active</a>
		  </li>
		  <li class="nav-item">
			<a class="nav-link" href="#">Link</a>
		  </li>
		  <li class="nav-item">
			<a class="nav-link" href="#">Link</a>
		  </li>
		</ul>
	</nav>
	<!-- End Navbar -->
	
	<!-- Sidebar -->
	<div id="mySidenav" class="sidenav">  
		<div class="card" style="width:350px; border-radius:0; border:0;">
		  <div class="card-header">
			  <input type="text" class="form-control" style="float:left;width:200px;" placeholder="#hashtags, keywords" aria-label="Search">
			  <div class="input-group-append"  style="float:left;">
				<button class="btn btn-info" type="submit">Search</button>
			  </div>
		  </div>

		  <ul class="list-group list-group-flush">
			<li class="list-group-item">			
				<h5 class="card-title">Filter</h5>
				<p class="card-text">I don't know yet.<br>I don't know yet.<br>I don't know yet.</p>
			</li>
			<li class="list-group-item">
				<h5 class="card-title">Style</h5>
				<p class="card-text">I don't know yet.<br>I don't know yet.<br>I don't know yet.</p>
			</li>
			<li class="list-group-item">
				<h5 class="card-title">Others</h5>
				<p class="card-text">I don't know yet.<br>I don't know yet.<br>I don't know yet.</p>
			</li>
		  </ul>
		</div>
		<a href="javascript:void(0)" class="closebtn" onclick="closeSide()">&times;</a>
	</div>
	<!-- End Sidebar -->
	
	<!-- Container -->
	<div id="search-div" class="input-group mb-3">
	  <div class="input-group-prepend"  style="float:left;">
		<button class="btn btn-info" type="button" onclick="openSide()">&#9776; </button>
	  </div>
	  <input type="text" class="form-control" style="float:left;width:200px;" placeholder="#hashtags, keywords" aria-label="Search">
	  <div class="input-group-append"  style="float:left;">
		<button class="btn btn-info" type="submit">Search</button>
	  </div>
	</div>

	<div id="style-div" class="btn-group" role="group">
	  <button id="select-Default" type="button" class="btn btn btn-light" value="default">Default</button>
	  <button id="select-Silver" type="button" class="btn btn-secondary" value="silver">Silver</button>
	  <button id="select-Night" type="button" class="btn btn-dark" value="night">Night</button>
	  <button id="select-regions" type="button" class="btn btn btn-light" value="regions">Regions</button>
	  <button id="select-cons" type="button" class="btn btn btn-light" value="constituencies">Constituencies</button>
	  <button id="select-100km" type="button" class="btn btn btn-light" value="100km">100km</button>
	  <button id="select-50km" type="button" class="btn btn btn-light" value="50km">50km</button>
	  <button id="select-25km" type="button" class="btn btn btn-light" value="25km">25km</button>
	  <button id="select-20km" type="button" class="btn btn btn-light" value="20km">20km</button>
	  <button id="select-10km" type="button" class="btn btn btn-light" value="10km">10km</button>
	  <button id="select-5km" type="button" class="btn btn btn-light" value="5km">5km</button>
	</div>
		
	<div id="map"></div>
	
	<script src="js/mapStyle.js"></script>
	<!-- End Container -->

	<script>
       
    google.load('visualization', '1.0', {'packages':['corechart']});
    
      function getRegionURL(type) {
    	  if (type == "constituency"){
    		  return 'https://opendata.arcgis.com/datasets/9507292be10e43c7abe9d3be36db6c41_4.geojson'
    	  }
    	  else { // default to regional map
    		  return 'https://opendata.arcgis.com/datasets/4b982ef4457648d3a16a8b398942c6ae_4.geojson';
    	  }	   	
      }
      

		function getGridGeoJson(km) {
			var request = new XMLHttpRequest();
			// NOTE THIS IS SYNCHRONOUS as we want to return the result, not ideal, maybe change?
			request.open('GET', '/map-optimization/geojson-tile?km=' + km, false);
			request.send();
			if (request.readyState === 4) {
				if (request.status === 200) {
					return JSON.parse(request.responseText);
				}
			}
		}
		
		//get grid style
		function getGridNumber(km) {
			var request = new XMLHttpRequest();
			request.open('GET', '/map-optimization/GridStyle?km=' + km, false);
			request.send();
			if (request.readyState === 4) {
				if (request.status === 200) {
					return JSON.parse(request.responseText);
				}
			}
		}
		
		//settting grid style to color
		function heatMapColorforValue(value){
	    	  if (value == 1){
	    		  return "blue";
	    	  } else if (value == 2){
	    		  return "green";
	    	  } else if (value == 3){
	    		  return "yellow";
	    	  } else if (value == 4){
	    		  return "orange";
	    	  } else if (value == 5){
	    		  return "red";
	    	  }
	    	  return "gray";
	    }
		
		function initMap() {
			var map = new google.maps.Map(document.getElementById('map'), {
				zoom : 6,
				center : {
					lat : 53.5,
					lng : -2.118
				},
				mapTypeId : 'terrain'
			});

			infoWindow = new google.maps.InfoWindow({
				content : ""
			});

			map.data
					.addListener(
							'click',
							function(event) {
								var data = new google.visualization.DataTable();
								data.addColumn('string', 'Hashtag');
								data.addColumn('number', 'Number');
								data.addRows([ [ '#Weather', 20 ],
										[ '#TrainLate', 15 ],
										[ '#NowPlaying', 13 ],
										[ '#CantSleep', 10 ],
										[ '#WinterOlympics', 8 ] ]);

								var options = {
									'title' : event.feature
											.getProperty("eer15nm"),
									'is3D' : true,
									'width' : 250,
									'height' : 250
								};

								var node = document.createElement('div'), chart = new google.visualization.PieChart(
										node);
								chart.draw(data, options);
								node.innerHTML += '<div style="line-height:1.35;overflow:hidden;white-space:nowrap;">';
								infoWindow.setContent(node);
								var anchor = new google.maps.MVCObject();
								anchor.set("position", event.latLng);
								infoWindow.open(map, anchor);
							});

			map.data.loadGeoJson(getRegionURL());

			map.data.setStyle(function(feature) {
				//var areaName = feature.getProperty('eer15nm');
				return {
					fillColor : "green",
					strokeWeight : 0.2,
					fillOpacity : 0.8
				}
			});

			// Add a search div to the map.
			var searchDiv = document.getElementById('search-div');
			map.controls[google.maps.ControlPosition.TOP_LEFT].push(searchDiv);

			// Add a style selector to the map.
			var styleDiv = document.getElementById('style-div');
			map.controls[google.maps.ControlPosition.LEFT_BOTTOM]
					.push(styleDiv);

			// Set the map's style to the initial value of the selector.
			var setDefault = document.getElementById('select-Default');
			var setSilver = document.getElementById('select-Silver');
			var setNight = document.getElementById('select-Night');
			var setRegions = document.getElementById('select-regions');
			var setCons = document.getElementById('select-cons');
			var set100km = document.getElementById('select-100km');
			var set50km = document.getElementById('select-50km');
			var set25m = document.getElementById('select-25km');
			var set20km = document.getElementById('select-20km');
			var set10km = document.getElementById('select-10km');
			var set5km = document.getElementById('select-5km');
			
			//
			var lastSet;
			var gridNumber;
			var flag = 1;
			var markers = [];
			var thisNum = 0;
			
			map.setOptions({
				styles : styles[setSilver.value]
			});

			// Setup the click event listeners
			setDefault.addEventListener('click', function() {
				map.setOptions({
					styles : styles[setDefault.value]
				});
			});

			setSilver.addEventListener('click', function() {
				map.setOptions({
					styles : styles[setSilver.value]
				});
			});

			setNight.addEventListener('click', function() {
				map.setOptions({
					styles : styles[setNight.value]
				});
			});

			setRegions.addEventListener('click', function() {
				map.data.forEach(function(feature) {
					map.data.remove(feature);
				});
				lastSet = getRegionURL("");
				map.data.loadGeoJson(lastSet);
			});

			setCons.addEventListener('click', function() {
				map.data.forEach(function(feature) {
					map.data.remove(feature);
				});
				lastSet = getRegionURL("constituency");
				map.data.loadGeoJson(lastSet);
			});

			setCons.addEventListener('click', function() {
				map.data.forEach(function(feature) {
					map.data.remove(feature);
				});
				lastSet = getRegionURL("constituency");
				map.data.loadGeoJson(lastSet);
			});
			
			set100km.addEventListener('click', function() {
				map.data.forEach(function(feature) {
					map.data.remove(feature);
				});
				lastSet= getGridGeoJson(100);
				map.data.addGeoJson(lastSet);
				//add heatmap color
				var counter = 0;
				gridNumber = getGridNumber(100);
				map.data.setStyle(function(feature) {
					return {
						fillColor : heatMapColorforValue(gridNumber[counter++]),
						strokeWeight : 0.2,
						fillOpacity : 0.8
					}
				});
				
			});
			set50km.addEventListener('click', function() {
				map.data.forEach(function(feature) {
					map.data.remove(feature);
				});
				lastSet
				lastSet = getGridGeoJson(50);
				map.data.addGeoJson(lastSet);
				//add heatmap color
				var counter = 0;
				gridNumber = getGridNumber(50);
				map.data.setStyle(function(feature) {
					return {
						fillColor : heatMapColorforValue(gridNumber[counter++]),
						strokeWeight : 0.2,
						fillOpacity : 0.8
					}
				});
			});
			set25m.addEventListener('click', function() {
				map.data.forEach(function(feature) {
					map.data.remove(feature);
				});
				lastSet = getGridGeoJson(25);
				map.data.addGeoJson(lastSet);
				//add heatmap color
				var counter = 0;
				gridNumber = getGridNumber(25);
				map.data.setStyle(function(feature) {
					return {
						fillColor : heatMapColorforValue(gridNumber[counter++]),
						strokeWeight : 0.2,
						fillOpacity : 0.8
					}
				});
			});
			set20km.addEventListener('click', function() {
				map.data.forEach(function(feature) {
					map.data.remove(feature);
				});
				lastSet = getGridGeoJson(20);
				map.data.addGeoJson(lastSet);
				//add heatmap color
				var counter = 0;
				gridNumber = getGridNumber(20);
				map.data.setStyle(function(feature) {
					return {
						fillColor : heatMapColorforValue(gridNumber[counter++]),
						strokeWeight : 0.2,
						fillOpacity : 0.8
					}
				});
			});
			set10km.addEventListener('click', function() {
				map.data.forEach(function(feature) {
					map.data.remove(feature);
				});
				lastSet = getGridGeoJson(10);
				map.data.addGeoJson(lastSet);	
				//add heatmap color
				var counter = 0;
				gridNumber = getGridNumber(10);
				map.data.setStyle(function(feature) {
					return {
						fillColor : heatMapColorforValue(gridNumber[counter++]),
						strokeWeight : 0.2,
						fillOpacity : 0.8
					}
				});
			});
			set5km.addEventListener('click', function() {
				map.data.forEach(function(feature) {
					map.data.remove(feature);
				});
				lastSet = getGridGeoJson(5);
				map.data.addGeoJson(lastSet);
				//add heatmap color
				var counter = 0;
				gridNumber = getGridNumber(5);
				map.data.setStyle(function(feature) {
					return {
						fillColor : heatMapColorforValue(gridNumber[counter++]),
						strokeWeight : 0.2,
						fillOpacity : 0.8
					}
				});
			});
			
			// set listener for zoomed in
			map.addListener('zoom_changed', function() {
				
				if (map.getZoom() == 13) {
					
					map.data.forEach(function(feature) {
						map.data.remove(feature);
					});
					flag = 1;
					
					for (var i = 0; i < markers.length; i++) {
						markers[i].setMap(null);
					}
					markers = [];
					thisNum = 0;
			 	 	var request = new XMLHttpRequest();
			 		request.open('GET', '/map-optimization/zoom?zoomLevel=' + map.getZoom() + '&nEastLat=' +   map.getBounds().getNorthEast().lat() + '&nEastLng=' +  map.getBounds().getNorthEast().lng() + '&sWestLat=' +  map.getBounds().getSouthWest().lat() + '&sWestLng=' + map.getBounds().getSouthWest().lng());
			 		request.send();
			 		request.onreadystatechange = function () {
			 			if (request.readyState === 4) {
			 				if (request.status === 200) {
			 					
			 					var t = request.responseText;
			 					t1 = JSON.parse(t);
			 						
			 					for (num in t1.tweets){
			 					        
			 						var myLatlng = {lat: t1.tweets[num].lat, lng: t1.tweets[num].lng};
			 					    var marker = new google.maps.Marker({
			 					        position: myLatlng,
			 					        title: t1.tweets[num].text
			 					    });
			 					    
			 					    (function(marker, thisNum) {
			 					    	google.maps.event.addListener(marker, 'click', function() {
			 					    		infowindow = new google.maps.InfoWindow({});
			 					    		infowindow.setContent(marker.getTitle());
			 					    		infowindow.open(map, marker);
			 					    	})
			 					    })(marker, i);
			 					    
			 					    thisNum += 1;

			 					    markers.push(marker);
			 					    marker.setMap(map);						
			 					}
			 				}
			 			}
			 		}			
				} else if (flag == 1 && map.getZoom() <= 12) {
					for (var i = 0; i < markers.length; i++) {
						markers[i].setMap(null);
					}
					map.data.addGeoJson(lastSet);
					var counter = 0;
					map.data.setStyle(function(feature) {
						return {
							fillColor : heatMapColorforValue(gridNumber[counter++]),
							strokeWeight : 0.2,
							fillOpacity : 0.8
						}
					});
					flag = 0;
				}
			});
		}
	</script>

	<script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBkybqNT3jqUIMs4Eun20VX2J_IFIFtwPE&libraries=visualization&callback=initMap"/>   
    <script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBkybqNT3jqUIMs4Eun20VX2J_IFIFtwPE&callback=initMap"/>
	
	<script>
	function openSide() {
		document.getElementById("mySidenav").style.width = "350px";
		document.body.style.backgroundColor = "rgba(0,0,0,0.4)";
	}

	function closeSide() {
		document.getElementById("mySidenav").style.width = "0";
		document.body.style.backgroundColor = "white";
	}
	</script>
	
  </body>
  
</html>
