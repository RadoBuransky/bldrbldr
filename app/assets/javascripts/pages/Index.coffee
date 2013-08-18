class @Index
	constructor: () ->
		height = document.body.clientHeight
		if height < 500
			height = 500
		#$('.barrelSection').height(height)
		$(".fileupload").fileupload()
		$('.typeahead').typeahead()

$(document).ready(-> @index = new Index())