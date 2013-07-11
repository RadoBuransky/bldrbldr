class @BoulderEditorToolbar
	ButtonStates: 
		enabled: "enabled"
		disabled: "disabled"
		shiny: "shiny"		

	WIDTH: 200
	
	# Draw
	DRAW_ID: "toolbar_draw"
	ERASE_ID: "toolbar_erase"	
	
	# Zoom
	FIT_ID: "zoomfit"
	ZOOMPLUS_ID: "zoomplus"
	ZOOMMINUS_ID: "zoomminus"

	constructor: (@toolbarId) ->
		@width = BoulderEditorToolbar::WIDTH
		$(window).resize(@onWindowResize)
		@repaint()
		
	#===============================================================================================
	# P U B L I C
	#===============================================================================================
	
	setButtonState: (id, newState) ->
		$("#" + id).removeClass(BoulderEditorToolbar::ButtonStates.enabled + " " +
			BoulderEditorToolbar::ButtonStates.disabled + " " +
			BoulderEditorToolbar::ButtonStates.shiny).addClass(newState)
	
	#===============================================================================================
	# P R I V A T E
	#===============================================================================================
		
	onWindowResize: =>
		@repaint()
		
	repaint: ->
		t = $("#" + @toolbarId)
		t.offset({top: 0; left: $(window).width() - @width})
		t.width(@width)
		t.height($(window).height())
		