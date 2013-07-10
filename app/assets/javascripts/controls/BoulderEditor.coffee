class @BoulderEditor
	
	constructor: (canvasId, toolbarId, imgId) ->
		@toolbar = new BoulderEditorToolbar(toolbarId)	
		@viewer = new BoulderViewer(canvasId, imgId)
		@updateViewerPos()		
		$(window).resize(@onWindowResize)
		
	fit: ->
		@viewer.fit()
		
	zoomin: ->
		@viewer.zoomin()
		
	zoomout: ->
		@viewer.zoomout()
		
	onWindowResize: =>
		@updateViewerPos()
		
	updateViewerPos: ->
		@viewer.setSize($(window).width() - @toolbar.width, $(window).height())