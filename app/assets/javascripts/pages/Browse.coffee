class @Browse
	constructor: () ->
		@updateMapHeight()
		$(window).resize(@onWindowResize)
		
		map = L.map('map').setView([49.6998, -123.1351], 13)
		L.tileLayer('http://{s}.tile.cloudmade.com/4bafbe399b194f2ab7294cd0f8d25747/997/256/{z}/{x}/{y}.png', {
		    attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://cloudmade.com">CloudMade</a>',
		    maxZoom: 18
		}).addTo(map)
		
	onWindowResize: () =>
		@updateMapHeight()
		
	updateMapHeight: () ->
		$('#map').height(document.body.clientHeight - 50)

$(document).ready(-> @browse = new Browse())