class @PaperPhotoViewer
		
	constructor: (@containerId, photoUrl) ->	
		@canvas = $('#' + @containerId)
		
		paper.install(window)
		paper.setup(@canvas.get(0))
		@photo = new paper.Raster('photo')
		
		tool = new Tool()		
		tool.onMouseDrag = @onMouseDrag
		
	setSize: (w, h) ->
		paper.view.viewSize = [w, h]
		
	onMouseDrag: (e) =>	
		@photoAnchor = {w: @photo.width / 2, h: @photo.height / 2}
		
		newX = @photo.position.x + e.delta.x - @photoAnchor.w
		newY = @photo.position.y + e.delta.y - @photoAnchor.h
		
		###
		if (newX + @photo.width > paper.view.bounds.right)
			newX = paper.view.bounds.right - @photo.width
				
		if (newX < paper.view.bounds.x)
			newX = paper.view.bounds.x			
		
		if (newY + @photo.height > paper.view.bounds.bottom)
			newY = paper.view.bounds.bottom - @photo.height
					
		if (newY < paper.view.bounds.y)
			newY = paper.view.bounds.y
		###		
		
		@photo.position.x = newX + @photoAnchor.w
		@photo.position.y = newY + @photoAnchor.h