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
			 
	draw: (point) ->			
		c = paper.Path.Circle(point, 50/paper.view.zoom)
		@path = @union(@path, c)
		@path.smooth()		
		@path.strokeColor = 'red'
		@path.fillColor = @path.strokeColor
		@showPoints(@path)
		
	showPoints: (path) ->
		@points.removeChildren();			
		for segment in path.segments
			p = new paper.Path.Circle(segment.point, 10)	
			p.strokeColor = 'blue'
			p.fillColor = p.strokeColor
			@points.addChild(p)	
		@points.bringToFront()		
		
	union: (c1, c2) ->
		if (c1 == null)
			return c2
		if (c2 == null)
			return c1
			
		intersections = c1.getIntersections(c2)
		if (intersections.length > 0)
			min = c1.curves.length + 1
			max = -1
			for i in intersections
				console.log(i.index)
				if (i.index < min)
					min = i.index
				if (i.index > max)
					max = i.index
				
			console.log("min: " + min + ", max: " + max)
			for i in [min..(max-1)]
				console.log(i)
				c1.removeSegment(i+1)			
		
			for i in intersections
				c1.insert(1, i.point)
				
		c2.remove()
		
		return c1
		