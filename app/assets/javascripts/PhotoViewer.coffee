class @PhotoViewer		
	constructor: (@containerId, photoUrl) ->	
		container = $('#' + @containerId)
			
		offset = container.offset()	
		@paper = Raphael(offset.left, offset.top, container.width(), container.height())
		
		@photo = new Image
		@photo.src = photoUrl
		@photo.onload = @onLoadPhoto
		
		$(document).ready(@onContainerResize)
		container.bind('resize', @onContainerResize)
			
	onLoadPhoto: =>
		@paper.image(@photo.src, 0, 0, @photo.width, @photo.height)
			
	onContainerResize: =>
		container = $('#' + @containerId)
		@paper.setSize(container.width(), container.height())
		