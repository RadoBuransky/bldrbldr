class @Editor

	constructor: ->
		@boulderEditor = new BoulderEditor("viewer", "toolbar", "assets/images/pd.jpeg")

$(document).ready(-> new Editor)