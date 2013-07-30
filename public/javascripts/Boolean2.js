

function sortIx( a, b ) { return b.parameter - a.parameter; }

function splitPath( _ixs, other ) {
    other = other || false;
    var i, j, k, l, len, ixs, ix, path, crv, vals;
    var ixPoint, nuSeg;
    var paths = {}, lastPathId = null;
    for (i = 0, l = _ixs.length; i < l; i++) {
        ix = ( other )? _ixs[i].getIntersection() : _ixs[i];
        if( !paths[ix.path.id] ){
            paths[ix.path.id] = ix.path;
        }
        if( !ix.curve._ixParams ){ix.curve._ixParams = []; }
        ix.curve._ixParams.push( { parameter: ix.parameter, pair: ix.getIntersection() } );
    }
    for (k in paths) {
        if( !paths.hasOwnProperty( k ) ){ continue; }
        path = paths[k];
        var lastNode = path.lastSegment, firstNode = path.firstSegment;
        var nextNode = null, left = null, right = null, parts = null, isLinear;
        var handleIn, handleOut;
        while( nextNode !== firstNode){
            nextNode = ( nextNode )? nextNode.previous: lastNode;
            if( nextNode.curve._ixParams ){
                ixs = nextNode.curve._ixParams;
                ixs.sort( sortIx );
                crv = nextNode.curve;
                isLinear = crv.isLinear();
                crv = vals = null;
                for (i = 0, l = ixs.length; i < l; i++) {
                    ix = ixs[i];
                    crv = nextNode.curve;
                    if( !vals ) vals = crv.getValues();
                    if( ix.parameter === 0.0 || ix.parameter === 1.0 ){
                        // Intersection is on an existing node
                        // no need to create a new segment,
                        // we just link the corresponding intersections together
                        nuSeg = ( ix.parameter === 0.0 )? crv.segment1 : crv.segment2;
                        nuSeg._ixPair = ix.pair;
                        nuSeg._ixPair._segment = nuSeg;
                    } else {
                        parts = Curve.subdivide( vals, ix.parameter );
                        left = parts[0];
                        right = parts[1];
                        handleIn = handleOut = null;
                        ixPoint = new Point( right[0], right[1] );
                        if( !isLinear ){
                            crv.segment1.handleOut = new Point( left[2] - left[0], left[3] - left[1] );
                            crv.segment2.handleIn = new Point( right[4] - right[6], right[5] - right[7] );
                            handleIn = new Point( left[4] - ixPoint.x, left[5] - ixPoint.y );
                            handleOut = new Point( right[2] - ixPoint.x, right[3] - ixPoint.y );
                        }
                        nuSeg = new Segment( ixPoint, handleIn, handleOut );
                        nuSeg._ixPair = ix.pair;
                        nuSeg._ixPair._segment = nuSeg;
                        path.insert( nextNode.index + 1,  nuSeg );
                    }
                    for (j = i + 1; j < l; j++) {
                        ixs[j].parameter = ixs[j].parameter / ix.parameter;
                    }
                    vals = left;
                }
            }
        }
    }
}

/**
 * To deal with a HTML canvas requirement where CompoundPaths' child contours
 * has to be of different winding direction for correctly filling holes.
 * But if some individual countours are disjoint, i.e. islands, we have to
 * reorient them so that
 *   the holes have opposit winding direction ( already handled by paperjs )
 *   islands has to have same winding direction ( as the first child of the path )
 *
 * Does NOT handle selfIntersecting CompoundPaths.
 *
 * @param  {CompoundPath} path - Input CompoundPath, Note: This path could be modified if need be.
 * @return {boolean}      the winding direction of the base contour( true if clockwise )
 */
 function reorientCompoundPath( path ){
    if( !(path instanceof CompoundPath) ){
        path.closed = true;
        return path.clockwise;
    }
    var children = path.children, len = children.length, baseWinding;
    var bounds = new Array( len );
    var tmparray = new Array( len );
    baseWinding = children[0].clockwise;
    // Omit the first path
    for (i = 0; i < len; i++) {
        children[i].closed = true;
        bounds[i] = children[i].bounds;
        tmparray[i] = 0;
    }
    for (i = 0; i < len; i++) {
        var p1 = children[i];
        for (j = 0; j < len; j++) {
            var p2 = children[j];
            if( i !== j && bounds[i].contains( bounds[j] ) ){
                tmparray[j]++;
            }
        }
    }
    for (i = 1; i < len; i++) {
        if ( tmparray[i] % 2 === 0 ) {
            children[i].clockwise = baseWinding;
        }
    }
    return baseWinding;
}

function reversePath( path ){
    if( path instanceof CompoundPath ){
        var children = path.children, i, len;
        for (i = 0, len = children.length; i < len; i++) {
            children[i].reverse();
        }
    } else {
        path.reverse();
    }
}

function computeBoolean( path1, path2, operator ){
    // We do not modify the operands themselves
    // The result might not belong to the same type
    // i.e. subtraction< A:Path, B:Path >:CompoundPath etc.
    var _path1 = path1.clone();
    var _path2 = path2.clone();
    // Do operator specific calculations before we begin
    if( operator.name === "subtraction" ) {
        reversePath( _path2 );
    }
    var path1Clockwise = reorientCompoundPath( _path1 );
    var path2Clockwise = reorientCompoundPath( _path2 );

    var ixs = _path1.getIntersections( _path2 );
    var path1Id = _path1.id;
    var path2Id = _path2.id;
    splitPath( ixs );
    splitPath( ixs, true );

    var i, j, len, path, crv;
    var paths;
    if( _path1 instanceof CompoundPath ){
        paths = new Array().concat( _path1.children );
    } else {
        paths = [ _path1 ];
    }
    if( _path2 instanceof CompoundPath ){
        paths = paths.concat( _path2.children );
    } else {
        paths.push( _path2 );
    }

    _path2.selected = true;
    // step 1: discard invalid links according to the boolean operator
    var lastNode, firstNode, nextNode, midPoint, insidePath1, insidePath2;
    var thisId, thisWinding, contains;
    for (i = 0, len = paths.length; i < len; i++) {
        insidePath1 = insidePath2 = false;
        path = paths[i];
        thisId = ( path.parent instanceof CompoundPath )? path.parent.id : path.id;
        thisWinding = path.clockwise;
        lastNode = path.lastSegment;
        firstNode = path.firstSegment;
        nextNode = null;
        while( nextNode !== firstNode){
            nextNode = ( nextNode )? nextNode.previous: lastNode;
            crv = nextNode.curve;
            midPoint = crv.getPoint( 0.5 );
            if( thisId !== path1Id ){
                contains = _path1.contains( midPoint );
                insidePath1 = (thisWinding === path1Clockwise)? contains :
                contains && !testOnCurve( _path1, midPoint );
            }
            if( thisId !== path2Id ){
                contains = _path2.contains( midPoint );
                insidePath2 = (thisWinding === path2Clockwise)? contains :
                contains && !testOnCurve( _path2, midPoint );
            }
            if( !operator( thisId === path1Id, insidePath1, insidePath2 ) ){
                crv._INVALID = true;
                // markPoint( midPoint, '+' );
            }
        }
    }

    // Final step: Retrieve the resulting paths from the graph
    var boolResult = new CompoundPath();
    var node, nuNode, nuPath, nodeList = [], handle;
    for (i = 0, len = paths.length; i < len; i++) {
        nodeList = nodeList.concat( paths[i].segments );
    }
    for (i = 0, len = nodeList.length; i < len; i++) {
        node = nodeList[i];
        if( node.curve._INVALID || node._visited ){ continue; }
        path = node.path;
        thisId = ( path.parent instanceof CompoundPath )? path.parent.id : path.id;
        thisWinding = path.clockwise;
        nuPath = new Path();
        firstNode = null;
        firstNode_ix = null;
        if( node.previous.curve._INVALID ) {
            node.handleIn = ( node._ixPair )?
            node._ixPair.getIntersection()._segment.handleIn : [ 0, 0 ];
        }
        while( node && !node._visited && ( node !== firstNode && node !== firstNode_ix ) ){
            // markPoint( node.point, node.index );
            // view.draw()

            node._visited = true;
            firstNode = ( firstNode )? firstNode: node;
            firstNode_ix = ( !firstNode_ix && firstNode._ixPair )?
                firstNode._ixPair.getIntersection()._segment: firstNode_ix;
            nextNode = ( node._ixPair && node.curve._INVALID )? node._ixPair.getIntersection()._segment : node;
            if( node._ixPair ) {
                // node._ixPair is this node's intersection CurveLocation object
                // node._ixPair._ixPair is the other CurveLocation object this node intersects with
                nextNode._visited = true;
                nuNode = new Segment( node.point, node.handleIn, nextNode.handleOut );
                nuPath.add( nuNode );
                node = nextNode;
                path = node.path;
                thisWinding = path.clockwise;
            } else {
                nuPath.add( node );
            }
            node = node.next;
        }
        nuPath.closed = true;
        boolResult.addChild( nuPath, true );
    }
    // if( operator.name === 'intersection' ){
    //     window.p = boolResult.reduce();
    // }
    // window.a = _path1;
    // window.b = _path2;
    // Delete the proxies
    _path1.remove();
    _path2.remove();
    // And then, we are done.
    return boolResult.reduce();
}

function testOnCurve( path, point ){
    var res = 0;
    var crv = path.getCurves();
    var i = 0;
    var bounds = path.bounds;
    if( bounds && bounds.contains( point ) ){
        for( i = 0; i < crv.length && !res; i++ ){
            var crvi = crv[i];
            if( crvi.bounds.contains( point ) && crvi.getParameterOf( point ) ){
                res = 1;
            }
        }
    }
    return res;
}

/**
 * A boolean operator is a binary operator function of the form
 * f( isPath1:boolean, isInsidePath1:Boolean, isInsidePath2:Boolean ) :Boolean
 *
 * Boolean operator determines whether a curve segment in the operands is part
 * of the boolean result, and will be called for each curve segment in the graph after
 * all the intersections between the operands are calculated and curves in the operands
 * are split at intersections.
 *
 * These functions should have a name ( "union", "subtraction" etc. below ), if we need to
 * do operator specific operations on paths inside the computeBoolean function.
 *  for example: if the name of the operator is "subtraction" then we need to reverse the second
 *                  operand. Subtraction is neither associative nor commutative.
 *
 *  The boolean operator should return a Boolean value indicating whether to keep the curve or not.
 *  return true - keep the curve
 *  return false - discard the curve
 */

 function unite( path1, path2 ){
    var unionOp = function union( isPath1, isInsidePath1, isInsidePath2 ){
        return ( isInsidePath1 || isInsidePath2 )? false : true;
    };
    return computeBoolean( path1, path2, unionOp );
}

function intersect( path1, path2 ){
    var intersectionOp = function intersection( isPath1, isInsidePath1, isInsidePath2 ){
        return ( !isInsidePath1 && !isInsidePath2 )? false : true;
    };
    return computeBoolean( path1, path2, intersectionOp );
}

function subtract( path1, path2 ){
    var subtractionOp = function subtraction( isPath1, isInsidePath1, isInsidePath2 ){
        return ( (isPath1 && isInsidePath2) || (!isPath1 && !isInsidePath1) )? false : true;
    };
    return computeBoolean( path1, path2, subtractionOp );
}


