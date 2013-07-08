class @BoulderEditor
	
	constructor: (canvasId, toolbarId, imgId) ->
		@toolbar = new BoulderEditorToolbar(toolbarId)	
		@viewer = new BoulderViewer(canvasId, imgId)
		@updateViewerPos()		
		$(window).resize(@onWindowResize)
		
	onWindowResize: =>
		@updateViewerPos()
		
	updateViewerPos: ->
		@viewer.setSize($(window).width() - @toolbar.width, $(window).height())