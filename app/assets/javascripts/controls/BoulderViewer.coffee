class @BoulderViewer extends PaperPhotoViewer
	StateEnum: 
		normal	: 0
		drawing : 1
		erasing	: 2
		
	constructor: (canvasId, imgId) ->
		super(canvasId, imgId)
		@state = BoulderViewer::StateEnum.normal
		@path = null
		@points = new paper.Group()
		@brushSize = 10
		
	setState: (newStatus) ->
		@state = newStatus
		
	onMouseWheel: (event, delta, deltaX, deltaY) =>
		if (@state == BoulderViewer::StateEnum.normal)
			return super(event, delta, deltaX, deltaY)
		
	onMouseDown: (e) =>
		if (@state == BoulderViewer::StateEnum.normal)
			return super(e)			
		@paint(e.point)
			
	onMouseDrag: (e) =>
		if (@state == BoulderViewer::StateEnum.normal)
			return super(e)		
		@paint(e.point)	
			 
	paint: (point) ->			
		stroke = new paper.Path.Circle(point.x, point.y, @brushSize)
		stroke.flatten(20)
		stroke.smooth()
		
		if (@path == null)
			@path = stroke		
			@path.strokeColor = 'black'
			@path.strokeWidth = 3
			@path.fillColor = 'red'
			@path.opacity = 0.8	
			return
		
		p = unite(@path, stroke)
		@path.remove()
		stroke.remove()
		
		p.strokeColor = 'black'
		p.strokeWidth = 3
		p.fillColor = 'red'
		p.opacity = 0.8	
		if p instanceof CompoundPath
			@path = p.reduce()
		else
			@path = p
		@path.smooth()
		