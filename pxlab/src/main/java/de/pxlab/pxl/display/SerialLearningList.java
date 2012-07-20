package de.pxlab.pxl.display;

import de.pxlab.util.Randomizer;
import de.pxlab.pxl.*;

/**
 * A sequence of items with an empty interval after each item. The Timer
 * parameter of this Display object should always be set to
 * de.pxlab.pxl.TimerCodes.RAW_CLOCK_TIMER. The ON-Duration is controlled by the
 * Duration parameter and the OFF-Duration by the parameter OffDuration. This
 * type of display may be used to present a list of items in a learning or
 * memory experiment.
 * 
 * @author H. Irtel
 * @version 0.2.0
 */
public class SerialLearningList extends FontDisplay {
	private static String[] ss = { "0", "1", "2", "3", "4", "5", "6", "7", "8",
			"9" };
	/**
	 * The set of items to use. If this set is empty then the memory set has to
	 * be defined by the parameter MemorySet in the design file. If the Ensemble
	 * parameter is non-empty then item indices refer to this set of items.
	 */
	public ExPar Ensemble = new ExPar(STRING, new ExParValue(ss),
			"Ensemble of Items");
	/**
	 * The memory set of items. A random memory set is generated automatically
	 * if this parameter is empty and the parameter Ensemble defines a set of
	 * items from which to select the memory set. The size of the MemorySet is
	 * given by MemorySetSize in this case. If Ensemble is defined and MemorySet
	 * is also defined then the parameter MemorySet should contain an array of
	 * indices into the ensemble set.
	 */
	public ExPar MemorySet = new ExPar(STRING, new ExParValue(""),
			"Memory Set Items or Indices");
	public ExPar MemorySetSize = new ExPar(SMALL_INT, new ExParValue(3),
			"Number of Items in Memory Set");
	public ExPar MemoryProbe = new ExPar(FLAG, new ExParValue(1),
			"Probe is a Member of the Memory Set");
	public ExPar MemoryProbeProbability = new ExPar(PROPORT,
			new ExParValue(0.5), "Probability for Memory Probes");
	public ExPar ProbeIndex = new ExPar(STRING, new ExParValue("1"),
			"Probe Index in Ensemble");
	public ExPar ProbeItem = new ExPar(STRING, new ExParValue(""), "Probe Item");
	public ExPar OffDuration = new ExPar(DURATION, new ExParValue(1000),
			"OFF period duration");
	public ExPar LocationX = new ExPar(HORSCREENPOS, new ExParValue(0),
			"Horizontal location");
	public ExPar LocationY = new ExPar(VERSCREENPOS, new ExParValue(0),
			"Vertical location");

	public SerialLearningList() {
		setTitleAndTopic("Serial Learning List", SERIAL_TEXT_DSP);
		Timer.set(new ExParValueConstant(
				"de.pxlab.pxl.TimerCodes.RAW_CLOCK_TIMER"));
	}
	private int firstDisplayElement;
	private int lastDisplayElement;
	private int firstTimingElement;
	private int lastTimingElement;

	protected int create() {
		firstDisplayElement = nextDisplayElementIndex();
		firstTimingElement = nextTimingElementIndex();
		return (firstDisplayElement);
	}

	protected void computeGeometry() {
		removeDisplayElements(firstDisplayElement, lastDisplayElement);
		removeTimingElements(firstTimingElement, lastTimingElement);
		String[] memorySet;
		int memorySetSize;
		String[] ensemble = null;
		int ensembleSize = 0;
		Randomizer rnd = new Randomizer();
		int[] ensembleIdx = null;
		if (Ensemble.getValue().isEmpty()) {
			// No ensemble, the memory set is defined in the design
			// file directly
			memorySet = MemorySet.getStringArray();
			memorySetSize = memorySet.length;
		} else {
			// We have an ensemble of items
			ensemble = Ensemble.getStringArray();
			ensembleSize = ensemble.length;
			if (MemorySet.getValue().isEmpty()) {
				// no memory set defined, create a random one
				memorySetSize = MemorySetSize.getInt();
				ensembleIdx = new int[ensembleSize];
				for (int i = 0; i < ensembleSize; i++)
					ensembleIdx[i] = i;
				rnd.randomize(ensembleIdx);
				int[] memorySetIdx = new int[memorySetSize];
				memorySet = new String[memorySetSize];
				for (int i = 0; i < memorySetSize; i++) {
					memorySetIdx[i] = ensembleIdx[i];
					memorySet[i] = ensemble[memorySetIdx[i]];
				}
				MemorySet.set(memorySetIdx);
			} else {
				// memory set is defined
				int[] memorySetIdx = MemorySet.getIntArray();
				memorySetSize = memorySetIdx.length;
				MemorySetSize.set(memorySetSize);
				memorySet = new String[memorySetSize];
				for (int i = 0; i < memorySetSize; i++) {
					int idx = memorySetIdx[i];
					if (idx < ensembleSize) {
						memorySet[i] = ensemble[idx];
					} else {
						memorySet[i] = "Illegal Index into Ensemble";
					}
				}
			}
		}
		// System.out.println("SerialLearningList.computeGeometry() memory set size = "
		// + MemorySetSize);
		// System.out.println("SerialLearningList.computeGeometry() memory set = "
		// + MemorySet);
		// If the probe item is already defined we don't do anything
		if (ProbeItem.getValue().isEmpty()) {
			if (ProbeIndex.getValue().isEmpty()) {
				int probeIndex = 0;
				// No probe defined, so create one
				int mp = MemoryProbe.getInt();
				if ((mp != 0) && (mp != 1)) {
					// We have to randomize the probe type
					mp = (rnd.nextDouble() <= MemoryProbeProbability
							.getDouble()) ? 1 : 0;
					MemoryProbe.set(mp);
				}
				// We know what type of probe we want
				if (mp == 0) {
					// We want a distractor probe, take one of our
					// randomized indizes
					probeIndex = ensembleIdx[memorySetSize];
				} else {
					// We want a memory set probe
					probeIndex = ensembleIdx[rnd.nextInt(memorySetSize)];
				}
				ProbeIndex.set(probeIndex);
				ProbeItem.set(ensemble[probeIndex]);
			} else {
				// Probe index is defined, so use it
				int idx = ProbeIndex.getInt();
				if (idx < ensembleSize)
					ProbeItem.set(ensemble[idx]);
			}
		}
		// System.out.println("SerialLearningList.computeGeometry() probe index = "
		// + ProbeIndex);
		// System.out.println("SerialLearningList.computeGeometry() probe = " +
		// ProbeItem);
		for (int i = 0; i < memorySetSize; i++) {
			enterDisplayElement(new TextElement(this.Color, memorySet[i]),
					getGroup(i + i));
			enterTiming(Timer, Duration, i + i);
			enterDisplayElement(new EmptyDisplayElement(), getGroup(i + i + 1));
			enterTiming(Timer, OffDuration, i + i + 1);
		}
		lastDisplayElement = firstDisplayElement + 2 * memorySetSize - 1;
		lastTimingElement = firstTimingElement + 2 * memorySetSize - 1;
		// Text.setReferencePoint(BASE_CENTER);
		String fnn = FontFamily.getString();
		int fnt = FontStyle.getInt();
		TextElement txt;
		// System.out.println("nStrings = " + nStrings);
		for (int i = 0; i < memorySetSize; i++) {
			txt = (TextElement) getDisplayElement(firstDisplayElement + i + i);
			txt.setFont(fnn, fnt, FontSize.getInt());
			txt.setLocation(LocationX.getInt(), LocationY.getInt());
			// txt.setText(strings[i]);
		}
	}
}
