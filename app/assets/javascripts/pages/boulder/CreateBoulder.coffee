class CreateBoulder

	constructor: () ->
	    $('.holdColorsSelector .clickable').click(@onHoldColorsClick)

    onHoldColorsClick: () ->
        $(this).siblings().toggle()

$(document).ready(-> @createBoulder = new CreateBoulder())