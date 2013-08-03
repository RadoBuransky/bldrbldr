class @PaperPhotoViewer
		
	constructor: (@canvasId, @imgId) ->		
		@img = $('#' + @imgId)
		@img.load => @onLoad()
		
		@oldDelta = null			
		@canvas = $('#' + @canvasId)
		
		paper.install(window)
		paper.setup(@canvas.get(0))
		@photo = new paper.Raster(@imgId)
		
		tool = new Tool()	
		tool.onMouseDown = @onMouseDown
		tool.onMouseDrag = @onMouseDrag
		tool.onMouseUp = @onMouseUp
		tool.onMouseMove = @onMouseMove
		
		@canvas.bind('mousewheel', @onMouseWheel)
		
	fit: ->
		@reset()
		
	zoomin: ->
		paper.view.zoom *= 1.1
		
	zoomout: ->
		paper.view.zoom /= 1.1
			
	setSize: (w, h) ->
		paper.view.viewSize = [w, h]
		@reset()
		
	onMouseWheel: (event, delta, deltaX, deltaY) =>
		paper.view.zoom *= 1 + (delta / 10)
		
	onMouseDown: (e) =>
		@oldDelta = new Point(0, 0)	
		
	onMouseDrag: (e) =>
		delta = new Point(@oldDelta.x - e.delta.x, @oldDelta.y - e.delta.y)
		@constraintPosition(delta)		
		paper.view.scrollBy(delta)		
		@oldDelta = delta
		
	onMouseUp: (e) =>
	
	onMouseMove: (e) =>

	onLoad: ->
		@reset()
		clippingRect = new Path.Rectangle(paper.view.center.x - Math.round(@img.width() / 2),
			paper.view.center.y - Math.round(@img.height() / 2),@img.width(),@img.height())
		project.activeLayer.insertChild(0, clippingRect)
		project.activeLayer.clipped = true
		
	reset: ->
		if (@img.width() == 0 || @img.height() == 0)
			return
	
		@photo.position = paper.view.center		
		
		hz = paper.view.zoom * paper.view.bounds.height / @img.height()
		vz = paper.view.zoom * paper.view.bounds.width / @img.width()
		
		paper.view.zoom = if hz < vz then hz else vz
		
	constraintPosition: (delta) ->
		newBounds = new Rectangle(paper.view.bounds)
		newBounds.x += delta.x
		newBounds.y += delta.y
		
		if (paper.view.bounds.height < @photo.bounds.height)		
			if (@photo.bounds.top > newBounds.top)
				delta.y = @photo.bounds.top - paper.view.bounds.top			
			if (@photo.bounds.bottom < newBounds.bottom)
				delta.y = @photo.bounds.bottom - paper.view.bounds.bottom
		else
			delta.y = 0
			
		if (paper.view.bounds.width < @photo.bounds.width)
			if (@photo.bounds.left > newBounds.left)
				delta.x = @photo.bounds.left - paper.view.bounds.left			
			if (@photo.bounds.right < newBounds.right)
				delta.x = @photo.bounds.right - paper.view.bounds.right
		else
			delta.x = 0