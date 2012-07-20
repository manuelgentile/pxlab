package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/**
 * A Left and a right square frame with a fixation mark and an attention cue or
 * target as it is used for spatial attention experiments.
 * 
 * @author H. Irtel
 * @version 0.1.0
 */
/*
 * 
 * 07/23/2003
 */
public class SpatialAttentionTarget extends SpatialAttentionFrames implements
		SpatialAttentionTargetCodes {
	/** Target color. */
	public ExPar TargetColor = new ExPar(COLOR, new ExParValue(
			new ExParExpression(ExParExpression.RED)), "Color of the Target");
	/** Target size. */
	public ExPar TargetSize = new ExPar(SCREENSIZE, new ExParValue(50),
			"Size of the Target");
	/**
	 * Target type can be any value of class
	 * de.pxlab.pxl.SpatialAttentionTargetCodes.
	 */
	public ExPar TargetType = new ExPar(GEOMETRY_EDITOR,
			SpatialAttentionTargetCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.SpatialAttentionTargetCodes.DOT"),
			"Target Type");
	/** Target position can be any value of class de.pxlab.pxl.AlignmentCodes. */
	public ExPar TargetPosition = new ExPar(GEOMETRY_EDITOR,
			AlignmentCodes.class, new ExParValueConstant(
					"de.pxlab.pxl.AlignmentCodes.LEFT"), "Target position");
	/** Line thickness of target lines. */
	public ExPar TargetLineWidth = new ExPar(SMALL_SCREENSIZE,
			new ExParValue(7), "Thickness of the Target Lines");

	/** Cunstructor creating the title of the display. */
	public SpatialAttentionTarget() {
		setTitleAndTopic("Spatial Attention Target", COMPLEX_GEOMETRY_DSP);
	}
	protected int target_idx;
	protected int target_type;

	/** Initialize the display list of the demo. */
	protected int create() {
		int r = super.create();
		target_idx = enterDisplayElement(new Oval(TargetColor), group[0]);
		target_type = DOT;
		return (r);
	}

	protected void computeGeometry() {
		super.computeGeometry();
		int new_target_type = TargetType.getInt();
		if (new_target_type != target_type) {
			DisplayElement d = null;
			if (new_target_type == DOT) {
				d = new Oval(TargetColor);
			} else if (new_target_type == SQUARE) {
				d = new Bar(TargetColor);
			} else if (new_target_type == CROSS) {
				d = new Cross(TargetColor);
			}
			if ((target_type == DOT) || (target_type == SQUARE)
					|| (target_type == CROSS)) {
				removeDisplayElements(target_idx);
			}
			if ((new_target_type == DOT) || (new_target_type == SQUARE)
					|| (new_target_type == CROSS)) {
				enterDisplayElement(d, group[0]);
			}
			target_type = new_target_type;
		}
		if ((target_type == DOT) || (target_type == SQUARE)
				|| (target_type == CROSS)) {
			DisplayElement target = getDisplayElement(target_idx);
			int fs = TargetSize.getInt();
			int dx = 0, dy = 0;
			if ((target_type == DOT) || (target_type == SQUARE)) {
				dx = -fs / 2;
				dy = dx;
			}
			int cp = TargetPosition.getInt();
			int x = 0;
			if (cp == AlignmentCodes.LEFT) {
				x = -frameSize / 2 - frameDist2;
			} else if (cp == AlignmentCodes.RIGHT) {
				x = frameSize / 2 + frameDist2;
			}
			target.setLocation(x + dx, dy);
			target.setSize(fs, fs);
			target.setLineWidth(TargetLineWidth.getInt());
		}
	}
}
