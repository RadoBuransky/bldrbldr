class @BoulderEditor	
	constructor: (canvasId, toolbarId, imgId, thisAccess) ->	
		@toolbar = new BoulderEditorToolbar(toolbarId)	
		@viewer = new SimpleBoulderViewer(canvasId, imgId)
		@updateViewerPos()		
		$(window).resize(@onWindowResize)
		
		# Draw
		@initToolbarButton(BoulderEditorToolbar::DRAW_ID, ".draw();", thisAccess)
		@initToolbarButton(BoulderEditorToolbar::ERASE_ID, ".erase();", thisAccess)
		
		# Zoom
		@initToolbarButton(BoulderEditorToolbar::FIT_ID, ".viewer.fit();", thisAccess)
		@initToolbarButton(BoulderEditorToolbar::ZOOMPLUS_ID, ".viewer.zoomin();", thisAccess)
		@initToolbarButton(BoulderEditorToolbar::ZOOMMINUS_ID, ".viewer.zoomout();", thisAccess)
		
		@setState(SimpleBoulderViewer::StateEnum.drawing)
		
	#===============================================================================================
	# P U B L I C
	#===============================================================================================
	
	#===============================================================================================
	# P R I V A T E
	#===============================================================================================
		
	draw: ->
		@setState(SimpleBoulderViewer::StateEnum.drawing)
		
	erase: ->
		@setState(SimpleBoulderViewer::StateEnum.erasing)
		
	setState: (newState) ->
		@state = if @state == newState then SimpleBoulderViewer::StateEnum.normal else newState		
		@viewer.setState(@state)
		
		@toolbar.setButtonState(BoulderEditorToolbar::DRAW_ID, if @state == SimpleBoulderViewer::StateEnum.drawing then BoulderEditorToolbar::ButtonStates.shiny else BoulderEditorToolbar::ButtonStates.enabled)
		@toolbar.setButtonState(BoulderEditorToolbar::ERASE_ID, if @state == SimpleBoulderViewer::StateEnum.erasing then BoulderEditorToolbar::ButtonStates.shiny else BoulderEditorToolbar::ButtonStates.enabled)
		
	onWindowResize: =>
		@updateViewerPos()
		
	updateViewerPos: ->
		@viewer.setSize($(window).width() - @toolbar.width, $(window).height())
		
	initToolbarButton: (id, action, thisAccess) ->
		$('#' + id).attr('href', 'javascript:' + thisAccess + action)
		