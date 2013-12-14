class IndexRoute

	constructor: () ->
	    $('#deleteRoute').click(@onDeleteRoute)
	    $('#yesDelete').click(@onYesDelete)
	    $('#noDelete').click(@onNoDelete)

    onDeleteRoute: () =>
        $('#deleteRoute').hide()
        $('#reallyDelete').show()

    onYesDelete: () =>
        $.ajax({
            url: window.location.pathname,
            type: 'DELETE'
            success: ->
                window.location = window.location.pathname.slice(0, window.location.pathname.lastIndexOf('/'))
        });

    onNoDelete: () =>
        $('#reallyDelete').hide()
        $('#deleteRoute').show()

$(document).ready(-> @indexRoute = new IndexRoute())