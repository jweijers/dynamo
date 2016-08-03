/*
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.ocs.jasperreports.chart;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import net.sf.jasperreports.engine.JRAbstractChartCustomizer;
import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRRuntimeException;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.BubbleXYItemLabelGenerator;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

/**
 * Chart customizer that adds several enhancements to charts: draw labels, quadrants and markers.
 * 
 * @author Patrick Deenen (patrick@opencircle.solutions)
 *
 */
public class ChartCustomizer extends JRAbstractChartCustomizer {

    @SuppressWarnings("serial")
    class LabelGenerator extends BubbleXYItemLabelGenerator {
	public String generateLabel(XYDataset dataset, int series, int item) {
	    if (dataset instanceof XYZDataset) {
		Number z = ((XYZDataset) dataset).getZ(series, item);
		if (z != null && z instanceof BigDecimalLabelWrapper) {
		    return ((BigDecimalLabelWrapper) z).getLabel();
		}
	    }
	    return String.valueOf(dataset.getSeriesKey(series));
	}
    }

    /**
     * Use this class to add support for labels for individual rows in charts which only support
     * this for the series key, for example the Bubble chart only support series labels.
     */
    @SuppressWarnings("serial")
    public static class BigDecimalLabelWrapper extends BigDecimal {

	private String label;

	/**
	 * Default constructor
	 * 
	 * @param value
	 *            The actual BigDecimal
	 * @param label
	 */
	public BigDecimalLabelWrapper(BigDecimal value, String label) {
	    super(value.doubleValue());
	    this.label = label;
	}

	public String getLabel() {
	    return label;
	}

    }

    /**
     * Define an instance of this class in a report variable with the name "re.Quadrant" (where re
     * should be replaced with the name of the reporting element) to draw a quadrant in the graph.
     *
     */
    public static class Quadrant {
	private double qOx;
	private double qOy;
	private Color qClt;
	private Color qCrt;
	private Color qClb;
	private Color qCrb;

	public Quadrant(double qOx, double qOy, Color qClt, Color qCrt, Color qClb, Color qCrb) {
	    super();
	    this.qOx = qOx;
	    this.qOy = qOy;
	    this.qClt = qClt;
	    this.qCrt = qCrt;
	    this.qClb = qClb;
	    this.qCrb = qCrb;
	}

	public double getqOx() {
	    return qOx;
	}

	public double getqOy() {
	    return qOy;
	}

	public Color getqClt() {
	    return qClt;
	}

	public Color getqCrt() {
	    return qCrt;
	}

	public Color getqClb() {
	    return qClb;
	}

	public Color getqCrb() {
	    return qCrb;
	}
    }

    /**
     * Define an instance of this class in a report variable with the name "re.Marker[Range|Domain]"
     * (where re should be replaced with the name of the reporting element) to draw a marker in the
     * graph. When "Range" is specified the marker will be drawn on the range axis otherwise the
     * domain axis.
     */
    @SuppressWarnings("serial")
    public static class Marker extends ValueMarker {

	public Marker(double value, Color color, String label) {
	    super(value);
	    setPaint(color);
	    setLabel(label);
	}

	public Marker(double value, Paint paint, Stroke stroke, Paint outlinePaint,
		Stroke outlineStroke, float alpha) {
	    super(value, paint, stroke, outlinePaint, outlineStroke, alpha);
	}

	public Marker(double value, Paint paint, Stroke stroke) {
	    super(value, paint, stroke);
	}

	public Marker(double value) {
	    super(value);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.jasperreports.engine.JRChartCustomizer#customize(org.jfree.chart.JFreeChart,
     * net.sf.jasperreports.engine.JRChart)
     */
    @Override
    public void customize(JFreeChart chart, JRChart jasperChart) {
	XYPlot plot = chart.getXYPlot();
	String key = jasperChart.getKey();

	// Paint labels for the rows when defined for this chart
	try {
	    if ((Boolean) getVariableValue(key + ".Labels")) {
		plot.getRenderer().setBaseItemLabelGenerator(new LabelGenerator());
		plot.getRenderer().setBaseItemLabelsVisible(true);
	    }
	} catch (JRRuntimeException e) {
	    // No labels defined and needed
	}

	// Paint Range markers when defined for this chart
	try {
	    Object markers = getVariableValue(key + ".MarkerRange");
	    addMarkers(plot, markers, true);
	    plot.getRangeAxis().setUpperMargin(0.5);

	} catch (JRRuntimeException e) {
	    // No markers defined and needed
	}
	// Paint Domain markers when defined for this chart
	try {
	    Object markers = getVariableValue(key + ".MarkerDomain");
	    addMarkers(plot, markers, false);
	    plot.getDomainAxis().setUpperMargin(0.5);

	} catch (JRRuntimeException e) {
	    // No markers defined and needed
	}

	// Paint a quadrant in the chart when defined for this chart
	try {
	    Quadrant q = (Quadrant) getVariableValue(key + ".Quadrant");
	    if (q != null) {
		Point2D pointQuadOrigin = (Point2D) new Point2D.Double(q.getqOx(), q.getqOy());
		plot.setQuadrantOrigin(pointQuadOrigin);

		plot.setQuadrantPaint(0, q.getqClt());
		plot.setQuadrantPaint(1, q.getqCrt());
		plot.setQuadrantPaint(2, q.getqClb());
		plot.setQuadrantPaint(3, q.getqCrb());
	    }
	} catch (JRRuntimeException e) {
	    // No quadrant defined and needed
	}
    }

    @SuppressWarnings("unchecked")
    protected void addMarkers(XYPlot plot, Object markers, boolean range) {
	Collection<Marker> mks = null;
	if (markers instanceof Collection<?>) {
	    mks = (Collection<Marker>) markers;
	} else if (markers instanceof Marker[]) {
	    mks = Arrays.asList((Marker[]) markers);
	}
	if (mks != null) {
	    for (Marker m : mks) {
		addMarker(plot, m, range);
	    }
	} else if (markers instanceof Marker) {
	    addMarker(plot, (Marker) markers, range);
	}
    }

    protected void addMarker(XYPlot plot, Marker marker, boolean range) {
	if (range) {
	    marker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
	    marker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
	    plot.addRangeMarker(marker, Layer.FOREGROUND);
	} else {
	    marker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
	    marker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
	    plot.addDomainMarker(marker, Layer.FOREGROUND);
	}
    }

}
