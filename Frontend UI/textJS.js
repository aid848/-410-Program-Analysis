var colors = d3.scaleOrdinal(d3.schemePastel1);

    var svg = d3.select("svg"),
        width = +svg.attr("width"),
        height = +svg.attr("height"),
        radius = +svg.attr("radius"),
        node,
        link,
        offsetTick = 20;

    svg.append('defs').append('marker')
        .attrs({'id':'arrowhead',
            'viewBox':'-0 -5 10 10',
            'refX':13,
            'refY':0,
            'orient':'auto',
            'markerWidth':9,
            'markerHeight':9,
            'xoverflow':'visible'})
        .append('svg:path')
        .attr('d', 'M 0,-5 L 10 ,0 L 0,5')
        .attr('fill', '#999')
        .style('stroke','#999');

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

    function update(links, nodes) {
        link = svg.selectAll(".link")
            .data(links)
            .enter()
            .append("line")
            .attr("class", "link")
            .attr('marker-end','url(#arrowhead)')
            .attr("stroke", getColour)
            .attr("stroke-opacity", 0.6)
            .attr("stroke-width", d => Math.sqrt(d.value));

        link.append("title")
            .text(function (d) {return d.type;});

        edgepaths = svg.selectAll(".edgepath")
            .data(links)
            .enter()
            .append('path')
            .attrs({
                'class': 'edgepath',
                'fill-opacity': 1.5,
                'stroke-opacity': 0.6,
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
                'fill': getColour
            });

        edgelabels.append('textPath')
            .attr('xlink:href', function (d, i) {return '#edgepath' + i})
            .style("text-anchor", "middle")
            .style("pointer-events", "none")
            .attr("startOffset", "50%")
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
            .attr("r", 7)
            .style("fill", getColour)

        node.append("title")
            .text(function (d) {return d.id;});

        node.append("text")
            .attr("dy", -3)
            .text(function (d) {return d.name+": "+d.label;})
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

    function getColour(d) {
        if (d.dependencies >= 10) {
            return "red";
        }
        if (d.label == "class") {
            return d3.interpolateYlOrBr(0.5);
        }
        if (d.label == "interface") {
            return  d3.interpolateYlOrBr(0.3);
        }
        if (d.label == "abstract") {
            return d3.interpolateYlOrBr(0.3);
        }
         else {
            return d3.interpolateYlOrBr(0.5);
        }
    }

    function getTextColour(d) {
        if (d.dependencies >= 10) {
            return "red";
        } else {
            return "black";
        }
    }

    function getColour(d) {
        if (d.dependencies >= 10) {
            return "red";
        }
        if (d.label == "class") {
            return d3.interpolateYlOrBr(0.5);
        }
        if (d.label == "interface") {
            return  d3.interpolateYlOrBr(0.3);
        }
        if (d.label == "abstract") {
            return d3.interpolateYlOrBr(0.3);
        }
         else {
            return d3.interpolateYlOrBr(0.5);
        }
    }

    function getTextColour(d) {
        if (d.dependencies >= 10) {
            return "red";
        } else {
            return "black";
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

//    function dragended(d) {
//        if (!d3.event.active) simulation.alphaTarget(0);
////        d.fx = undefined;
////        d.fy = undefined;
//    }