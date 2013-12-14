class IndexRoute

	constructor: () ->
	    $('#deleteRoute').click(@onDeleteRoute)
	    $('#yesDelete').click(@onYesDelete)
	    $('#noDelete').click(@onNoDelete)
	    $('#flags .btn').click(@onFlagClick)

    onFlagClick: () ->
        $.ajax({
            url: '/climbing/' + window.gymHandle + '/' + window.routeId + '/flag/' + this.id,
            type: 'PUT'
        })
        btn = $(this)
        $('.flagCount',btn).hide()
        $('.glyphicon',btn).show()
        btn.prop('disabled', true)

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