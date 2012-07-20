package de.pxlab.gui;

/**
 * A ChartListener gets a message whenever the current position in the Chart
 * where the listener is registered has been changed. The ChartListener has to
 * do two things: (1) consume the new position coordinates and (2) report
 * whether it is willing to accept the new coordinates. Note that ChartListeners
 * register with the Chart since there is no explicit ChartModel. The chart's
 * model actually consists of two AxisModels. However, the chart client usually
 * will not be able to decide about the validity of new coordinates if only a
 * single coordinate is available. Thus the ChartListener must not register with
 * two AxisModels but with the Chart in order to always get both coordinates.
 * 
 * @version 0.2.0
 * @see Chart
 */
/*
 * 
 * 03/09/00
 */
public interface ChartListener {
	/**
	 * The Chart sends the new coordinates (x,y) to the ChartListener. If the
	 * ChartListener is willing to accept these coordinates then it returns
	 * true.
	 */
	public boolean chartValueChanged(double x, double y);
}
