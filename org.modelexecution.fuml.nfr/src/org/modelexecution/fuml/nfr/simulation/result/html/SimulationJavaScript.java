/*
 * Copyright (c) 2013 Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License v1.0 which accompanies 
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Martin Fleck - initial version
 */
package org.modelexecution.fuml.nfr.simulation.result.html;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import org.modelexecution.fuml.nfr.simulation.result.AbstractPrinter;

public class SimulationJavaScript extends AbstractPrinter {

	private String jsString;
	
	public void writeFile(File file) throws FileNotFoundException, IOException, URISyntaxException {
		FileOutputStream jsFile = new FileOutputStream(file);
		jsFile.write(getJavaScriptString().getBytes());
		jsFile.close();
	}
	
	private String getJavaScriptString() {
		if(jsString == null) {
			StringBuilder builder = new StringBuilder();
			printLine(builder, "function chartPropertyEvolution(info) {");
			printLine(builder, "	var singleValue = !(info.yAxis.property in window);");
			printLine(builder, "	var width = info.chart.width - info.margin.left - info.margin.right;");
			printLine(builder, "	var height = info.chart.height - info.margin.top - info.margin.bottom;");
			printLine(builder, "");
			printLine(builder, "	var x = d3.scale.linear().range([0, width]);");
			printLine(builder, "	var y = d3.scale.linear().range([height, 0]);");
			printLine(builder, "");
			printLine(builder, "	var color = d3.scale.category10();	");
			printLine(builder, "");
			printLine(builder, "	var xAxis = d3.svg.axis()");
			printLine(builder, "		.scale(x)");
			printLine(builder, "		.orient(\"bottom\");");
			printLine(builder, "");
			printLine(builder, "	var yAxis = d3.svg.axis()");
			printLine(builder, "		.scale(y)");
			printLine(builder, "		.orient(\"left\");");
			printLine(builder, "");
			printLine(builder, "	var line = d3.svg.line()");
			printLine(builder, "		.x(function(d) { return x(d.time); })");
			printLine(builder, "		.y(function(d) { return y(d.property); });");
			printLine(builder, "");
			printLine(builder, "	var svg = d3.select(\"body\").append(\"svg\")");
			printLine(builder, "		.attr(\"width\", width + info.margin.left + info.margin.right)");
			printLine(builder, "		.attr(\"height\", height + info.margin.top + info.margin.bottom)");
			printLine(builder, "		.append(\"g\")");
			printLine(builder, "		.attr(\"transform\", \"translate(\" + info.margin.left + \",\" + info.margin.top + \")\");");
			printLine(builder, "");
			printLine(builder, "	d3.csv(info.csv, function(error, data) {");
			printLine(builder, "		color.domain(");
			printLine(builder, "			d3.keys(data[0]).filter(function(key) {");
			printLine(builder, "				if(singleValue)");
			printLine(builder, "					return key === info.yAxis.property;");
			printLine(builder, "				else");
			printLine(builder, "					return key !== info.xAxis.property;");  
			printLine(builder, "			})");
			printLine(builder, "		)");
			printLine(builder, "");
			printLine(builder, "		data.forEach(function(d) {");
			printLine(builder, "			d.time = (+d.time/1000);");
			printLine(builder, "		});");
			printLine(builder, "	  ");
			printLine(builder, "		var allProperties = color.domain().map(function(name) {");
			printLine(builder, "			return {");
			printLine(builder, "				name: name,");
			printLine(builder, "				values: data.map(function(d) {");
			printLine(builder, "					return {time: +d.time, property: +d[name]};");
			printLine(builder, "				})");
			printLine(builder, "			};");
			printLine(builder, "		});");
			printLine(builder, "");
			printLine(builder, "		x.domain(d3.extent(data, function(d) { return d.time; }));");
			printLine(builder, "		y.domain([");
			printLine(builder, "			d3.min(allProperties, function(sc) { return d3.min(sc.values, function(v) { return v.property; }); }),");
			printLine(builder, "			d3.max(allProperties, function(sc) { return d3.max(sc.values, function(v) { return v.property; }); })");
			printLine(builder, "		]);");
			printLine(builder, "");
			printLine(builder, "		svg.append(\"g\")");
			printLine(builder, "		  .attr(\"class\", \"x axis\")");
			printLine(builder, "		  .attr(\"transform\", \"translate(0,\" + height + \")\")");
			printLine(builder, "		  .call(xAxis)");
			printLine(builder, "		.append(\"text\")");
			printLine(builder, "		  .attr(\"x\", width + 5)");
			printLine(builder, "		  .attr(\"y\", 16)");
			printLine(builder, "		  .attr(\"dx\", \".71em\")");
			printLine(builder, "		  .style(\"text-anchor\", \"start\")");
			printLine(builder, "		  .text(info.xAxis.label);");
			printLine(builder, "");
			printLine(builder, "		svg.append(\"g\")");
			printLine(builder, "		  .attr(\"class\", \"y axis\")");
			printLine(builder, "		  .call(yAxis)");
			printLine(builder, "		.append(\"text\")");
			printLine(builder, "		  .attr(\"transform\", \"rotate(-90)\")");
			printLine(builder, "		  .attr(\"y\", -42)");
			printLine(builder, "		  .attr(\"dy\", \".71em\")");
			printLine(builder, "		  .style(\"text-anchor\", \"end\")");
			printLine(builder, "		  .text(info.yAxis.label);");
			printLine(builder, "");
			printLine(builder, "	  var property = svg.selectAll(\".property\")");
			printLine(builder, "		  .data(allProperties)");
			printLine(builder, "		  .enter().append(\"g\")");
			printLine(builder, "		  .attr(\"class\", \"property\");");
			printLine(builder, "");
			printLine(builder, "	  property.append(\"path\")");
			printLine(builder, "		  .attr(\"class\", \"line\")");
			printLine(builder, "		  .attr(\"d\", function(d) { return line(d.values); })");
			printLine(builder, "		  .style(\"stroke\", function(d) { return color(d.name); });");
			printLine(builder, "");
			printLine(builder, "	  var legend = svg.selectAll(\".legend\")");
			printLine(builder, "		  .data(color.domain().slice().reverse())");
			printLine(builder, "		  .enter().append(\"g\")");
			printLine(builder, "		  .attr(\"class\", \"legend\")");
			printLine(builder, "		  .attr(\"transform\", function(d, i) { return \"translate(0,\" + i * 20 + \")\"; });");
			printLine(builder, "");
			printLine(builder, "	  legend.append(\"rect\")");
			printLine(builder, "		  .attr(\"x\", width + info.margin.top)");
			printLine(builder, "		  .attr(\"y\", -9)");
			printLine(builder, "		  .attr(\"width\", 7)");
			printLine(builder, "		  .attr(\"height\", 18)");
			printLine(builder, "		  .style(\"fill\", color);");
			printLine(builder, "");
			printLine(builder, "	  legend.append(\"text\")");
			printLine(builder, "		  .attr(\"x\", width + 22)");
			printLine(builder, "		  .attr(\"y\", 0)");
			printLine(builder, "		  .attr(\"dy\", \".35em\")");
			printLine(builder, "		  .text(function(d) { return d; });");
			printLine(builder, "	});");
			printLine(builder, "}");
			jsString = builder.toString();
		}
		return jsString;
	}
}
