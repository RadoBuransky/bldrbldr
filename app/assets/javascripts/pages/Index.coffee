class @Index
	constructor: () ->
		$('.barrelSection').height(document.body.clientHeight)

$(document).ready(-> @index = new Index())