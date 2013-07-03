window.onWindowResize = () ->
    document.getElementById('main').style.width = (window.innerWidth -
    	document.getElementById('toolbar').offsetWidth) + "px";
    	
# Register event handlers 
document.addEventListener("DOMContentLoaded", onWindowResize, false);
window.onresize = onWindowResize;