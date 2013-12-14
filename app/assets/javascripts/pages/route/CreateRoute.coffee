class CreateRoute

	constructor: () ->
	    $('.holdColorsSelector .clickable').click(@onHoldColorsClick)
	    $('#createRouteForm').submit(@onCreateRouteFormSubmit)

    onCreateRouteFormSubmit: (e) =>
        color = @getSelectedColor()
        cats = @getSelectedCategories()

        if (color == null)
            e.preventDefault()
            alert('Select a color.')
        else
            colorInput = $('<input>').attr('type', 'hidden').attr('name', 'color').val(color);
            catsInput = $('<input>').attr('type', 'hidden').attr('name', 'categories').val(cats);

            form = $('#createRouteForm')
            form.append($(colorInput))
            form.append($(catsInput))

            true

    onHoldColorsClick: () ->
        $(this).siblings().toggle()

    getSelectedColor: ->
        sc = $('.holdColorsSelector .clickable:visible')
        if (sc.length != 1)
            null
        else
            sc[0].id

    getSelectedCategories: ->
        $.makeArray($('#tags button.active').map(-> this.id))


$(document).ready(-> @createRoute = new CreateRoute())