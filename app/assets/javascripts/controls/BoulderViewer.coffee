class @BoulderViewer extends PaperPhotoViewer
	StateEnum:
		normal	: 0
		drawing : 1
		erasing	: 2
		
	constructor: (canvasId, imgId) ->
		super(canvasId, imgId)
		@state = BoulderViewer::StateEnum.normal
		@currentPath = null
		@holds = []
		@brushSize = 6
		@eraser = null
		
	setState: (newStatus) ->
		@state = newStatus
		
	setSize: (w, h) ->
		super(w, h)
		
	onMouseWheel: (event, delta, deltaX, deltaY) =>
		return super(event, delta, deltaX, deltaY)
		
	onMouseDown: (e) =>
		if (@state == BoulderViewer::StateEnum.normal)
			return super(e)		
		else if (@state == BoulderViewer::StateEnum.drawing)
			@paint(e.point)		
		else if (@state == BoulderViewer::StateEnum.erasing)
			@erase(e.point)		
			
	onMouseDrag: (e) =>
		if (@state == BoulderViewer::StateEnum.normal)
			return super(e)
		else if (@state == BoulderViewer::StateEnum.drawing)		
			@paint(e.point)		
		else if (@state == BoulderViewer::StateEnum.erasing)
			@erase(e.point)	
		
	onMouseUp: (e) =>
		if (@state == BoulderViewer::StateEnum.drawing)
			if @currentPath != null
				@holds.push(@currentPath)
				@currentPath = null	
				@tryToUniteAllHolds()
				
	onMouseMove: (e) =>
		if (@state == BoulderViewer::StateEnum.erasing)
			@showEraser(e.point)
				
	showEraser: (point) ->
		for hold in @holds
			if hold.hitTest(point) != null
				@styleEraser(hold)
			else
				@styleStroke(hold)
				
	erase: (point) ->
		for hold, i in @holds
			if hold.hitTest(point) != null
				hold.remove()
				@holds.splice(i, 1)
				return
			 
	paint: (point) ->		
		stroke = @createBrush(point, @brushSize)
		
		if (@currentPath == null)
			@currentPath = @tryToUniteStroke(stroke)
		else		
			p = unite(@currentPath, stroke)
			if p instanceof CompoundPath
				stroke.remove()
				p.removeChildren()
				p.remove()
				return	
			@currentPath.remove()
			@currentPath = p
		
		stroke.remove()
		@styleStroke(@currentPath)
		
	tryToUniteAllHolds: ->
		while @tryToUniteSomeHolds()
			0
		
	tryToUniteSomeHolds: ->
		for hold1, i in @holds
			for j in [i+1..@holds.length-1] by 1
				p = unite(@holds[i], @holds[j])		
				if p instanceof Path
					@holds[i].remove()
					@holds[j].remove()
					@holds.splice(i, 1)
					@holds.splice(j - 1, 1)
					
					@styleStroke(p)
					@holds.push(p)
					return true
				p.removeChildren()
				p.remove()
		return false											
		
	tryToUniteStroke: (stroke) ->
		for hold, i in @holds
			p = unite(hold, stroke)		
			if p instanceof Path
				hold.remove()
				@holds.splice(i, 1)
				return @tryToUniteStroke(p)
			p.removeChildren()
			p.remove()
		return stroke
		
	createBrush: (point, size) ->
		return new Path.RegularPolygon(point,  Math.round(2*size +
			size / (2 * paper.view.zoom)),
			2 * size / paper.view.zoom)
		
	styleStroke: (stroke) ->			
		stroke.strokeColor = 'black'
		stroke.strokeWidth = 2
		stroke.storkeKoin = 'round'
		stroke.fillColor = 'red'
		stroke.opacity = 0.7
		
	styleEraser: (stroke) ->		
		stroke.strokeColor = 'black'
		stroke.strokeWidth = 3
		stroke.storkeKoin = 'round'	
		stroke.fillColor = #DDDDDD
		stroke.opacity = 0.4					