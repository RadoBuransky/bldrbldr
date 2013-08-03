class @BoulderViewer extends PaperPhotoViewer
	StateEnum:
		normal	: 0
		drawing : 1
		erasing	: 2
		
	constructor: (canvasId, imgId) ->
		super(canvasId, imgId)
		@state = BoulderViewer::StateEnum.normal
		@currentPath = null
		@holds = new paper.Group()
		@brushSize = 6
		
	setState: (newStatus) ->
		@state = newStatus
		
	onMouseWheel: (event, delta, deltaX, deltaY) =>
		if (@state == BoulderViewer::StateEnum.normal)
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
		
	onMouseUp: (e) =>
		if (@state == BoulderViewer::StateEnum.drawing)
			if @currentPath != null
				@holds.addChild(@currentPath)
				@currentPath = null	
				@tryToUniteAllHolds()
				
	erase: (point) ->
			 
	paint: (point) ->		
		stroke = new Path.RegularPolygon(point,  Math.round((@brushSize) +
			@brushSize / (2 * paper.view.zoom)),
			2 * @brushSize / paper.view.zoom)
		
		if (@currentPath == null)
			@currentPath = @tryToUniteStroke(stroke)
		else		
			p = unite(@currentPath, stroke)
			if p instanceof CompoundPath
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
		for hold1 in @holds.children
			for hold2 in @holds.children
				if hold1 == hold2
					continue
				p = unite(hold1, hold2)		
				if p instanceof Path
					@styleStroke(p)
					@holds.addChild(p)
					hold1.remove()
					hold2.remove()
					return true
		return false											
		
	tryToUniteStroke: (stroke) ->
		for hold in @holds.children
			p = unite(hold, stroke)		
			if p instanceof Path
				hold.remove()
				return @tryToUniteStroke(p)
			p.remove()
		return stroke
		
	styleStroke: (stroke) ->			
		stroke.strokeColor = 'black'
		stroke.strokeWidth = 2
		stroke.storkeKoin = 'round'
		stroke.fillColor = 'red'
		stroke.opacity = 0.7				