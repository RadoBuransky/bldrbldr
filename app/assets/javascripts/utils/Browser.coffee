class @Browser
	instance = null
	
	class BrowserSingleton
		constructor: ->
			@isIE = false
			@isNS = false
			
			if ((i = navigator.userAgent.indexOf("MSIE")) >= 0)
				@isIE = true
			else
				@isNS = true
	
	@get: ->
		instance ?= new BrowserSingleton