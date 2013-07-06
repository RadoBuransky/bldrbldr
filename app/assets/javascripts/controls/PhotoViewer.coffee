class @PhotoViewer
		
	constructor: (@containerId, photoUrl) ->	
		container = $('#' + @containerId)
		
		@enabled = false
		
		@deltaX = 0
		@deltaY = 0
		
		@currZoom = 0
		@zoomStep = 0.1
		
		@initialPos = { x: 0, y: 0 }
		@currPos = @initialPos
		@dragTime = 0
			
		offset = container.offset()	
		@paper = Raphael(offset.left, offset.top, 100, 100)
		@container = @paper.canvas.parentNode
		
		@photo = new Image
		@photo.src = photoUrl
		@photo.onload = @onLoadPhoto
		
	setSize: (w, h) ->
		@paper.setSize(w, h)
		c = $('#' + @containerId)		
		c.width(w)
		c.height(h)
		@repaint()		
			
	onLoadPhoto: =>
		i = @paper.image(@photo.src, 0, 0, @photo.width, @photo.height)
		i.drag(@dragging, @onMouseDown, @onMouseUp)
	
	log: (e) ->
		console.log(e)
		
	onMouseDown: (e) =>
		if (!@enabled)
			false
			
		evt = window.event || e
		@dragTime = 0
		@initialPos = @getRelativePos(evt, @container)
		@container.className += " grabbing"
		
	onMouseUp: (e) =>			
		@container.className = @container.className.replace(/(?:^|\s)grabbing(?!\S)/g, '')
		
	dragging: (e) =>
		if (!@enabled)
			false
	
		evt = window.event || e
		newPoint = @getRelativePos(evt, @container)
		
		@deltaX = (newPoint.x - @initialPos.x) * -1
		@deltaY = (newPoint.y - @initialPos.y) * -1
		@initialPos = newPoint
		
		@repaint()
		@dragTime += 1
		
	getRelativePos: (e, obj) ->
		x = 0
		y = 0
		if (e.pageX || e.pageY)
			x = e.pageX
			y = e.pageY
		else
			x = e.clientX + document.body.scrollLeft + document.documentElement.scrollLeft
			y = e.clientY + document.body.scrollTop + document.documentElement.scrollTop
			
		pos = @findPos(obj)
		x -= pos[0]
		y -= pos[1]
		
		{ x: x, y: y }
		
	findPos: (obj) ->
		posX = obj.offsetLeft
		posY = obj.offsetTop
		
		while (obj.offsetParent)
		    if (obj == document.getElementsByTagName('body')[0])
		        break
		    else
		        posX = posX + obj.offsetParent.offsetLeft
		        posY = posY + obj.offsetParent.offsetTop
		        obj = obj.offsetParent
		        
		[posX, posY]
		
	repaint: ->
		@currPos.x = @currPos.x + @deltaX
		@currPos.y = @currPos.y + @deltaY
    
		if (@currPos.x < 0)
			@currPos.x = 0
		else
			if (@currPos.x > @photo.width)
				@currPos.x = @photo.width
		
		if (@currPos.y < 0)
			@currPos.y = 0
		else
			if (@currPos.y > @photo.height)
				@currPos.y = @photo.height
				
		@paper.setViewBox(@currPos.x, @currPos.y, @paper.width, @paper.height)