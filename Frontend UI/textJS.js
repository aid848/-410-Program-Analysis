var colors = d3.scaleOrdinal(d3.schemePaired);
var pairedColours = ["#a6cee3","#1f78b4","#b2df8a","#33a02c","#fb9a99","#e31a1c","#fdbf6f","#ff7f00","#cab2d6","#6a3d9a","#ffff99","#b15928"]
var tableau10 = ["#4e79a7","#f28e2c","#e15759","#76b7b2","#59a14f","#edc949","#af7aa1","#ff9da7","#9c755f","#bab0ab"]
var green = tableau10[4]
var lightBlue =  "#17becf"
var darkBlue = "#1f78b4"

var extendCol = pairedColours[3]
var implementCol = "#feac3b"
var fieldCol = "#1f78b4"

var largeClass = pairedColours[5]
var manyDependencies = "black"
var noDependencies = "#f0027f"

var classNode = tableau10[4]
var interfaceNode = tableau10[3]
var abstractNode = tableau10[0]

    var svg = d3.select("svg"),
        width = +svg.attr("width"),
        height = +svg.attr("height"),
        radius = +svg.attr("radius"),
        node,
        link,
        offsetTick = 20;

    svg.append('defs').append('marker')
        .attrs({'id':'fieldArrowhead',
            'viewBox':'-0 -5 10 10',
            'refX':33,
            'refY':0,
            'orient':'auto',
            'markerWidth':5,
            'markerHeight':5,
            'xoverflow':'visible'})
        .append('svg:path')
        .attr('d', 'M 0,-5 L 10 ,0 L 0,5')
        .attr('fill', fieldCol)
        .style('stroke',fieldCol);
        

    svg.append('defs').append('marker')
            .attrs({'id':'implementArrowhead',
                'viewBox':'-0 -5 10 10',
                'refX':33,
                'refY':0,
                'orient':'auto',
                'markerWidth':5,
                'markerHeight':5,
                'xoverflow':'visible'})
            .append('svg:path')
            .attr('d', 'M 0,-5 L 10 ,0 L 0,5')
            .attr('fill', implementCol)
            .style('stroke',implementCol);

     svg.append('defs').append('marker')
             .attrs({'id':'extendArrowhead',
                 'viewBox':'-0 -5 10 10',
                 'refX':33,
                 'refY':0,
                 'orient':'auto',
                 'markerWidth':5,
                 'markerHeight':5,
                 'xoverflow':'visible'})
             .append('svg:path')
             .attr('d', 'M 0,-5 L 10 ,0 L 0,5')
             .attr('fill', extendCol)
             .style('stroke',extendCol);
             

    // this is to keep the nodes closer to the middle
    var forceX = d3.forceX(width / 2).strength(0.05)
    var forceY = d3.forceY(height / 2).strength(0.05)

    var simulation = d3.forceSimulation()
        .force("link", d3.forceLink().id(function (d) {return d.id;}).distance(120).strength(0.5))
        .force("charge", d3.forceManyBody())
        .force("center", d3.forceCenter(width / 2, height / 2))
        .force('collide', d3.forceCollide(function(d){
            return d.id === "j" ? 100 : 50
        }))
        .force('x', forceX)
        .force('y',  forceY);
//        .force("x", d3.forceX().strength(0.3))
//        .force("y", d3.forceY().strength(0.3));

//    var force = d3.layout.force()
//        .size([width, height])
//        .charge(0)
//        .gravity(0)
//        .linkStrength(0)
//        .friction(0);

//        .force("link", d3.forceLink().id(function (d) {return d.id;}).distance(120).strength(0.5))
//        .force("charge", d3.forceManyBody())
//        .force("center", d3.forceCenter(width / 2, height / 2))
//        .force("collision", d3.forceCollide().radius(function(d) {
//            return d.radius
//          }))
//          .on('tick', ticked);


    d3.json("graph.json", function (error, graph) {
        if (error) throw error;
        update(graph.links, graph.nodes);
    })

    function getArrow(d) {
        if (d.type == "implements") {
            return 'url(#implementArrowhead)'
        }
        if (d.type == "extends") {
            return 'url(#extendArrowhead)'
        }
        if (d.type == "field") {
            return 'url(#fieldArrowhead)'
        }
    }

    function update(links, nodes) {
        link = svg.selectAll(".link")
            .data(links)
            .enter()
            .append("line")
            .attr("class", "link")
            .attr('marker-end', getArrow)
            .attr("stroke", getLineColour)
            .attr("stroke-opacity", 0.6)
            .attr("stroke-width", 1.5);

        link.append("title")
            .text(function (d) {return d.type;});

        edgepaths = svg.selectAll(".edgepath")
            .data(links)
            .enter()
            .append('path')
            .attrs({
                'class': 'edgepath',
                'fill-opacity': 1,
                'stroke-opacity': 0.3,
                'id': function (d, i) {return 'edgepath' + i}
            })
            .style("pointer-events", "none");

        edgelabels = svg.selectAll(".edgelabel")
            .data(links)
            .enter()
            .append('text')
            .style("pointer-events", "none")
            .attrs({
                'class': 'edgelabel',
                'id': function (d, i) {return 'edgelabel' + i},
                'font-size': 12,
                'fill': getLineColour
            });

        edgelabels.append('textPath')
            .attr('xlink:href', function (d, i) {return '#edgepath' + i})
            .style("text-anchor", "middle")
            .style("pointer-events", "none")
            .attr("startOffset", getOffset)
            .text(function (d) {return d.type});

        node = svg.selectAll(".node")
            .data(nodes)
            .enter()
            .append("g")
            .attr("class", "node")
            .call(d3.drag()
                    .on("start", dragstarted)
                    .on("drag", dragged)
                    //.on("end", dragended)
            );

        node.append("circle")
            .attr("r", 12.5)
            .style("fill", getNodeColour)
            .style("stroke", getOutline)
            .style("stroke-width", 4)
            .style()

        node.append("title")
            .text(function (d) {return d.name;});

        node.append("text")
            .attr("dy", -10)
            .attr("dx", 4)
            .text(function (d) {return d.name;})
            .style('fill', getTextColour)
            .attr('font-size', 15);

        simulation
            .nodes(nodes)
            .on("tick", ticked);

        simulation.force("link")
            .links(links);
    }

    function ticked() {

        node
            .attr("transform", function (d) {
            d.x = Math.max(radius + offsetTick, Math.min(width - radius - offsetTick, d.x));
            d.y = Math.max(radius + offsetTick, Math.min(height - radius - offsetTick, d.y));
            return "translate(" +
             d.x + ", " +
             d.y + ")";});

         link
                    .attr("x1", function (d) {return d.source.x = Math.max(radius + offsetTick, Math.min(width - radius - offsetTick, d.source.x));})
                    .attr("y1", function (d) {return d.source.y = Math.max(radius + offsetTick, Math.min(height - radius - offsetTick, d.source.y));})
                    .attr("x2", function (d) {return d.target.x = Math.max(radius + offsetTick, Math.min(width - radius - offsetTick, d.target.x));})
                    .attr("y2", function (d) {return d.target.y = Math.max(radius + offsetTick, Math.min(height - radius - offsetTick, d.target.y));});

//        link
//            .attr("x1", function (d) {return d.source.x;})
//            .attr("y1", function (d) {return d.source.y;})
//            .attr("x2", function (d) {return d.target.x;})
//            .attr("y2", function (d) {return d.target.y;});

// edgepaths.attr('d', function (d) {
//            return 'M ' +
//             d.source.x +
//            ' ' +
//              d.source.y +
//              ' L ' +
//             d.target.x +
//               ' ' +
//             d.target.y;
//        });

        edgepaths.attr('d', function (d) {
        d.source.x =  Math.max(radius + offsetTick, Math.min(width - radius - offsetTick, d.source.x));
        d.source.y = Math.max(radius + offsetTick, Math.min(height - radius - offsetTick, d.source.y));
        d.target.x = Math.max(radius + offsetTick, Math.min(width - radius - offsetTick, d.target.x));
        d.target.y = Math.max(radius + offsetTick, Math.min(height - radius - offsetTick, d.target.y));
        return 'M ' +
            d.source.x +
            ' ' +
            d.source.y +
            ' L ' +
            d.target.x +
            ' ' +
            d.target.y;
        });

//        edgelabels.attr('transform', function (d) {
//            if (d.target.x < d.source.x) {
//                var bbox = this.getBBox();
//
//                rx = bbox.x + bbox.width / 2;
//                ry = bbox.y + bbox.height / 2;
//                return 'rotate(180 ' + rx + ' ' + ry + ')';
//            }
//            else {
//                return 'rotate(0)';
//            }
//        });
//        simulation.force.stop();

        simulation.velocityDecay(0.5);
//        simulation.alphaDecay(.1);
//        simulation.velocityDecay(0.8);

    }

    function getOffset(d) {
        if (d.type == "implements" || d.type == "extends") {
            return "25%"
        }
        if (d.type == "field") {
            return "60%"
        }
    }

    function getTextColour(d) {
        if (d.dependencies >= 10) {
            return manyDependencies;
        } 
        if (d.numMethods >= 10) {
            return largeClass;
        } else {
            return "black";
        }
    }

    function getLineColour(d) {
        if (d.type == "extends") {
            return extendCol;
        }
        if (d.type == "field") {
            return fieldCol;
        }
        if (d.type == "implements") {
            return implementCol;
        }
    }

    function getOutline(d) {
        if (d.numMethods >= 10) {
            return largeClass;
        }
        if (d.dependencies >= 10) {
            return manyDependencies
        }
        else {
            return getNodeColour
        }
    }

    function getNodeColour(d) {
        if (d.label == "class") {
            return classNode;
        }
        if (d.label == "interface") {
            return  interfaceNode;
        }
        if (d.label == "abstract") {
            return abstractNode;
        }
         else {
            return green;
        }
    }

    function dragstarted(d) {
        if (!d3.event.active) simulation.alphaTarget(0.3).restart()
        d.fx = d.x;
        d.fy = d.y;
    }

    function dragged(d) {
        d.fx = d3.event.x;
        d.fy = d3.event.y;
    }


// Legend adapted from:
// https://www.d3-graph-gallery.com/graph/custom_legend.html#cat2

var svg2 = d3.select("#my_dataviz2")

svg2.append("circle").attr("cx",100).attr("cy",130).attr("r", 6).style("fill", classNode)
svg2.append("circle").attr("cx",100).attr("cy",160).attr("r", 6).style("fill", interfaceNode)
svg2.append("circle").attr("cx",100).attr("cy",190).attr("r", 6).style("fill", abstractNode)
svg2.append("circle").attr("cx",100).attr("cy",220).attr("r", 6).style("fill", "beige").style("stroke", largeClass).style("stroke-width", 3)
svg2.append("circle").attr("cx",100).attr("cy",250).attr("r", 6).style("fill", "beige").style("stroke", manyDependencies).style("stroke-width", 3)
svg2.append("text").attr("x", 120).attr("y", 130).text("Class").style("font-size", "15px").style("fill", "black").attr("alignment-baseline","middle")
svg2.append("text").attr("x", 120).attr("y", 160).text("Interface").style("font-size", "15px").style("fill", "black").attr("alignment-baseline","middle")
svg2.append("text").attr("x", 120).attr("y", 190).text("Abstract Class").style("font-size", "15px").style("fill", "black").attr("alignment-baseline","middle")
svg2.append("text").attr("x", 120).attr("y", 220).text("Large Class").style("font-size", "15px").style("fill", "black").attr("alignment-baseline","middle")
svg2.append("text").attr("x", 120).attr("y", 250).text("Many dependencies").style("font-size", "15px").style("fill", "black").attr("alignment-baseline","middle")
//    function dragended(d) {
//        if (!d3.event.active) simulation.alphaTarget(0);
////        d.fx = undefined;
////        d.fy = undefined;
//    }