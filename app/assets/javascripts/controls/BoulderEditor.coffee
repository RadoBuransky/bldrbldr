class @BoulderEditor
	StateEnum = 
		normal	: 0
		drawing : 1
		erasing	: 2
	
	constructor: (canvasId, toolbarId, imgId, thisAccess) ->
		@state = StateEnum.normal
	
		@toolbar = new BoulderEditorToolbar(toolbarId)	
		@viewer = new BoulderViewer(canvasId, imgId)
		@updateViewerPos()		
		$(window).resize(@onWindowResize)
		
		# Draw
		@initToolbarButton(BoulderEditorToolbar::DRAW_ID, ".draw();", thisAccess)
		@initToolbarButton(BoulderEditorToolbar::ERASE_ID, ".erase();", thisAccess)
		
		# Zoom
		@initToolbarButton(BoulderEditorToolbar::FIT_ID, ".viewer.fit();", thisAccess)
		@initToolbarButton(BoulderEditorToolbar::ZOOMPLUS_ID, ".viewer.zoomin();", thisAccess)
		@initToolbarButton(BoulderEditorToolbar::ZOOMMINUS_ID, ".viewer.zoomout();", thisAccess)
		
	#===============================================================================================
	# P U B L I C
	#===============================================================================================
	
	#===============================================================================================
	# P R I V A T E
	#===============================================================================================
		
	draw: ->
		@setState(StateEnum.drawing)
		
	erase: ->
		@setState(StateEnum.erasing)
		
	setState: (newState) ->
		@state = if @state == newState then StateEnum.normal else newState
		
		@toolbar.setButtonState(BoulderEditorToolbar::DRAW_ID,
			if @state == StateEnum.drawing then BoulderEditorToolbar::ButtonStates.shiny else BoulderEditorToolbar::ButtonStates.enabled)
		
		@toolbar.setButtonState(BoulderEditorToolbar::ERASE_ID,
			if @state == StateEnum.erasing then BoulderEditorToolbar::ButtonStates.shiny else BoulderEditorToolbar::ButtonStates.enabled)
		
	onWindowResize: =>
		@updateViewerPos()
		
	updateViewerPos: ->
		@viewer.setSize($(window).width() - @toolbar.width, $(window).height())
		
	initToolbarButton: (id, action, thisAccess) ->
		$('#' + id).attr('href', 'javascript:' + thisAccess + action)
		