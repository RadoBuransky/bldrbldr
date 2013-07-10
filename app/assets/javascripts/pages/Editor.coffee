class @Editor

	constructor: ->
		@boulderEditor = new BoulderEditor("viewer", "toolbar", "photo")
		
	

$(document).ready(-> @editor = new Editor)