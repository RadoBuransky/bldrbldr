class @Editor

	constructor: (thisAccess) ->
		@boulderEditor = new BoulderEditor("viewer", "toolbar", "photo", thisAccess + ".boulderEditor")	

$(document).ready(-> @editor = new Editor("document.editor"))