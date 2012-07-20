package de.pxlab.pxl;

import java.awt.*;

import de.pxlab.util.*;

/** Process data which have been collected during an experimental run. */
/*
 * 
 * 2005/12/98
 */
public class DataProcessor implements ExDesignDataProcessor {
	/**
	 * The Frame which owns this data processor. It must be possible that this
	 * is a null pointer.
	 */
	protected Frame owner;
	/**
	 * The Datamanager needs the ExDesign in order to query for the factorial
	 * structure.
	 */
	protected ExDesign exDesign;
	/**
	 * This is the data destination where the computation results should be sent
	 * to. This must be set before any output can be created.
	 */
	protected DataDestination dataDestination = null;

	public DataProcessor(Frame owner, ExDesign exDesign) {
		this.owner = owner;
		this.exDesign = exDesign;
	}

	/**
	 * Set the data destination where the computation results should be sent to.
	 * This must be set before any output can be created.
	 * 
	 * @param ds
	 *            the DataDestination object where to send computation results.
	 */
	public void setDataDestination(DataDestination ds) {
		dataDestination = ds;
	}

	/**
	 * Ask the DataManger to process the data of a given node. This method is
	 * called when all trials of the node have been run. This method delegates
	 * data processing to the DisplayList given as an argument. This method will
	 * look for a set of experimental parameter names which should contain the
	 * experimental data. These parameter names my be contained as arguments in
	 * the data node which generated this request. If this argument list is
	 * empty then the Factors() node of the design tree is queried for the
	 * random, independent, covariate and dependent factors and these are used
	 * as the set of experimental parameters which enter data processing. The
	 * list of experimental parameter names and the data node are sent to every
	 * data processing object in the given data processing list.
	 * 
	 * @param processingList
	 *            the data processing list object which generated this request.
	 * @param data
	 *            the procedure ExDesignNode filled with data.
	 */
	public void processData(DisplayList processingList, ExDesignNode data) {
		if (dataDestination != null && processingList != null) {
			boolean hasData = true;
			String[] parNames = processingList.getExDesignNode().getParNames();
			if (parNames == null || parNames.length == 0) {
				parNames = ExPar.FactorialDataFormat.getStringArray();
				if (parNames == null || parNames.length == 0) {
					hasData = false;
				}
			}
			if (hasData) {
				int dls = processingList.size();
				DataDisplay dsp;
				for (int i = 0; i < dls; i++) {
					dsp = (DataDisplay) processingList.get(i);
					dsp.createDataTable(parNames, data);
					dsp.doProcessDataTable(exDesign, dataDestination);
				}
			} else {
				System.out.println("DataProcessor.processData(): No data for "
						+ processingList.getExDesignNode().getName());
			}
		}
	}
}
