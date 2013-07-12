class @BoulderViewer extends PaperPhotoViewer
	StateEnum: 
		normal	: 0
		drawing : 1
		erasing	: 2
		
	constructor: (canvasId, imgId) ->
		super(canvasId, imgId)
		@state = BoulderViewer::StateEnum.normal
		@path = null
		
	setState: (newStatus) ->
		@state = newStatus
		
	onMouseWheel: (event, delta, deltaX, deltaY) =>
		if (@state == BoulderViewer::StateEnum.normal)
			return super(event, delta, deltaX, deltaY)
		
	onMouseDown: (e) =>
		if (@state == BoulderViewer::StateEnum.normal)
			return super(e)			
		@draw(e.point)
			
	onMouseDrag: (e) =>
		if (@state == BoulderViewer::StateEnum.normal)
			return super(e)			
		@draw(e.point)
			
	draw: (point) ->			
		c = paper.Path.Circle(point, 20/paper.view.zoom)		
		c.strokeColor = 'red'
		c.fillColor = c.strokeColor
		
		if (@path == null)
			@path = c
		else
			@path.unite(c)
			@path.smooth()
			@path.fullySelected = true