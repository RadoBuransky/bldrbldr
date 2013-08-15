class @Index
	constructor: () ->
		height = document.body.clientHeight
		if height < 500
			height = 500
		$('.barrelSection').height(height)

$(document).ready(-> @index = new Index())