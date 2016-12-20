package main;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * Graphs class
 * @author Jordi Castelló
 *
 */
public class Chart {
	TimeSeriesCollection timeSeriesCollection; // Collection of time series data
	TimeSeries seriesX; // X series data
	JFreeChart chart;
	
	public Chart() {
		timeSeriesCollection = new TimeSeriesCollection();
		seriesX = new TimeSeries("FSR");  
		timeSeriesCollection.addSeries(seriesX);
		chart = createChart();
	}
	
	/**
	 * Create the chart that draws the graphics
	 * @return created chart
	 */
	private JFreeChart createChart() {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "Force Graph",  		// title
            "Time",             	// x-axis label
            "Newtons",   			// y-axis label
            timeSeriesCollection, 	// data
            false,               	// create legend
            false, 		            // generate tooltips
            false              		// generate URLs
        );
        //chart.setBackgroundPaint(Color.WHITE);
        XYPlot plot = (XYPlot) chart.getPlot();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(60000.0);
        return chart;
 }
	
	public JFreeChart getChart() {
		return chart;
	}
}
