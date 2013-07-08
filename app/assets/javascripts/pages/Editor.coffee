class @Editor

	constructor: ->
		@boulderEditor = new BoulderEditor("viewer", "toolbar", "photo")

$(document).ready(-> new Editor)