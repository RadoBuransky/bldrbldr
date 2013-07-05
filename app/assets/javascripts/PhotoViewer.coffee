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
		@paper = Raphael(offset.left, offset.top, 300, 300)
		@container = @paper.canvas.parentNode
		@container.onmousedown = @onMouseDown
		
		@photo = new Image
		@photo.src = photoUrl
		@photo.onload = @onLoadPhoto
			
	onLoadPhoto: =>
		@paper.image(@photo.src, 0, 0, @photo.width, @photo.height)
		
	onMouseDown: (e) =>
		if (!@enabled)
			false
			
		evt = window.event || e
		@dragTime = 0
		@initialPos = @getRelativePos(evt, @container)
		@container.className += " grabbing"
		
		if (Browser.get().isIE)
    		document.attachEvent("onmousemove", @dragging)
    		document.attachEvent("onmouseup", @onMouseUp)
    		window.event.cancelBubble = true;
    		window.event.returnValue = false;
    		
		if (Browser.get().isNS)
			document.addEventListener("mousemove", @dragging, true)
			document.addEventListener("mouseup", @onMouseUp, true)
		
		#@container.onmousemove = @dragging		
		#document.onmousemove = () -> false
		
		if (evt.preventDefault)
			evt.preventDefault()
		else
			evt.returnValue = false
		false
		
	onMouseUp: (e) =>
		if (Browser.get().isIE)
    		document.detachEvent("onmousemove", @dragging)
    		document.detachEvent("onmouseup", @onMouseUp)
    		
		if (Browser.get().isNS)
			document.removeEventListener("mousemove", @dragging, true)
			document.removeEventListener("mouseup", @onMouseUp, true)
			
		#document.onmousemove = null
		@container.className = @container.className.replace(/(?:^|\s)grabbing(?!\S)/g, '')
		#@container.onmousemove = null
		
	dragging: (e) =>
		if (!@enabled)
			false
	
		evt = window.event || e
		newWidth = @paper.width * (1 - (@currZoom * @zoomStep))
		newHeight = @paper.height * (1 - (@currZoom * @zoomStep))
		newPoint = @getRelativePos(evt, @container)
		
		@deltaX = (newWidth * (newPoint.x - @initialPos.x) / @paper.width) * -1
		@deltaY = (newHeight * (newPoint.y - @initialPos.y) / @paper.height) * -1
		@initialPos = newPoint
		
		@repaint()
		@dragTime += 1
		
		if (evt.preventDefault)
			evt.preventDefault()
		else
			evt.returnValue = false
		false
		
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
		
		newWidth = @paper.width * (1 - (@currZoom * @zoomStep))
		newHeight = @paper.height * (1 - (@currZoom * @zoomStep))
    
		if (@currPos.x < 0)
			@currPos.x = 0
		else
			if (@currPos.x > @photo.width - newWidth)
				@currPos.x = @photo.width - newWidth
		
		if (@currPos.y < 0)
			@currPos.y = 0
		else
			if (@currPos.y > @photo.height - newHeight)
				@currPos.y = @photo.height - newHeight
				
		@paper.setViewBox(@currPos.x, @currPos.y, newWidth, newHeight)