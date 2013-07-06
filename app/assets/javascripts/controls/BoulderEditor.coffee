class @BoulderEditor
	
	constructor: (viewerId, toolbarId, photoUrl) ->
		@toolbar = new BoulderEditorToolbar(toolbarId)	
		@viewer = new BoulderViewer(viewerId, photoUrl)
		@updateViewerPos()		
		$(window).resize(@onWindowResize)
		
	onWindowResize: =>
		@updateViewerPos()
		
	updateViewerPos: ->
		@viewer.setSize($(window).width() - @toolbar.width, $(window).height())