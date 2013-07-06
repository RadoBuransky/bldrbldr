class @BoulderEditorToolbar
	WIDTH: 200

	constructor: (@toolbarId) ->
		@width = BoulderEditorToolbar::WIDTH
		$(window).resize(@onWindowResize)
		@repaint()
		
	onWindowResize: =>
		@repaint()
		
	repaint: ->
		t = $("#" + @toolbarId)
		t.offset({top: 0; left: $(window).width() - @width})
		t.width(@width)
		t.height($(window).height())
		