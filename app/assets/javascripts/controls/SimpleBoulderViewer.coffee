class @SimpleBoulderViewer extends PaperPhotoViewer
	StateEnum:
		normal	: 0
		drawing : 1
		erasing	: 2
		
	constructor: (canvasId, imgId) ->
		super(canvasId, imgId)
		@state = SimpleBoulderViewer::StateEnum.normal
		@currentPath = null
		@holds = []
		@brushSize = 30
		@eraser = null
		
	setState: (newStatus) ->
		@state = newStatus
		
	setSize: (w, h) ->
		super(w, h)
		
	onMouseWheel: (event, delta, deltaX, deltaY) =>
		return super(event, delta, deltaX, deltaY)
		
	onMouseDown: (e) =>
		if (@state == SimpleBoulderViewer::StateEnum.normal)
			return super(e)		
		else if (@state == SimpleBoulderViewer::StateEnum.drawing)
			@paint(e.point)		
		else if (@state == SimpleBoulderViewer::StateEnum.erasing)
			@erase(e.point)		
			
	onMouseDrag: (e) =>
		if (@state == SimpleBoulderViewer::StateEnum.normal)
			return super(e)
		else if (@state == SimpleBoulderViewer::StateEnum.drawing)		
			@paint(e.point)		
		else if (@state == SimpleBoulderViewer::StateEnum.erasing)
			@erase(e.point)	
		
	onMouseUp: (e) =>
		if (@state == SimpleBoulderViewer::StateEnum.drawing)
			if @currentPath != null
				@holds.push(@currentPath)
				@currentPath = null
				
	onMouseMove: (e) =>
		if (@state == SimpleBoulderViewer::StateEnum.erasing)
			@showEraser(e.point)
				
	showEraser: (point) ->
		gotYa = false
		for hold in @holds
			if hold.hitTest(point) != null && !gotYa
				@styleEraser(hold)
				gotYa = true
				
	erase: (point) ->
		for hold, i in @holds
			if hold.hitTest(point) != null
				hold.remove()
				@holds.splice(i, 1)
				return
			 
	paint: (point) ->				
		if (@currentPath == null)
			@currentPath = new paper.Path()
			@currentPath.moveTo(point)
			@currentPath.lineTo([point.x, point.y+1])		
			@styleStroke(@currentPath)
			
		@currentPath.lineTo(point)
		
	styleStroke: (stroke) ->			
		stroke.strokeColor = '#A80C0C'
		stroke.strokeWidth = @brushSize / paper.view.zoom
		stroke.strokeJoin = 'round'
		stroke.strokeCap = 'round'
		stroke.opacity = 0.7
		
	styleEraser: (stroke) ->			
		stroke.strokeColor = 'black'
		stroke.strokeWidth = @brushSize / paper.view.zoom
		stroke.strokeJoin = 'round'
		stroke.strokeCap = 'round'
		stroke.opacity = 0.7					