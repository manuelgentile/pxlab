package de.pxlab.pxl.display;

import java.awt.*;
import de.pxlab.pxl.*;

/*  @author M. Hodapp
 @version 0.1.0 
 @date 05/24/00 */
public class HVInterlace extends Display {
	public ExPar Color1 = new ExPar(COLOR, new ExParValue(new PxlColor(
			Color.lightGray)), "Color1");

	/** Constructor creating the title of the demo. */
	public HVInterlace() {
		setTitleAndTopic("Hor/Ver & Interlace Brightness Test",
				DISPLAY_TEST_DSP | DEMO);
	}
	private int vg, hg, vb, hb;
	private int s1;

	/** Initialize the display list of the demo. */
	protected int create() {
		for (int i = 0; i < 24; i++) {
			enterDisplayElement(new HorStripedBar(Color1));
		}
		for (int i = 0; i < 24; i++) {
			enterDisplayElement(new VerStripedBar(Color1));
		}
		s1 = enterDisplayElement(new Bar(Color1));
		return (s1);
	}

	protected void computeGeometry() {
		int r = 6;
		int c = 8;
		int n = r * c;
		Rectangle r1 = centeredRect(width, height, width, height);
		Rectangle[] p = rectPattern(r1, r, c, vg, hg, vb, hb);
		HorStripedBar hb1 = (HorStripedBar) getDisplayElement(1);
		HorStripedBar hb2 = (HorStripedBar) getDisplayElement(2);
		HorStripedBar hb3 = (HorStripedBar) getDisplayElement(3);
		HorStripedBar hb4 = (HorStripedBar) getDisplayElement(4);
		HorStripedBar hb5 = (HorStripedBar) getDisplayElement(5);
		HorStripedBar hb6 = (HorStripedBar) getDisplayElement(6);
		HorStripedBar hb7 = (HorStripedBar) getDisplayElement(7);
		HorStripedBar hb8 = (HorStripedBar) getDisplayElement(8);
		HorStripedBar hb9 = (HorStripedBar) getDisplayElement(9);
		HorStripedBar hb10 = (HorStripedBar) getDisplayElement(10);
		HorStripedBar hb11 = (HorStripedBar) getDisplayElement(11);
		HorStripedBar hb12 = (HorStripedBar) getDisplayElement(12);
		HorStripedBar hb13 = (HorStripedBar) getDisplayElement(13);
		HorStripedBar hb14 = (HorStripedBar) getDisplayElement(14);
		HorStripedBar hb15 = (HorStripedBar) getDisplayElement(15);
		HorStripedBar hb16 = (HorStripedBar) getDisplayElement(16);
		HorStripedBar hb17 = (HorStripedBar) getDisplayElement(17);
		HorStripedBar hb18 = (HorStripedBar) getDisplayElement(18);
		HorStripedBar hb19 = (HorStripedBar) getDisplayElement(19);
		HorStripedBar hb20 = (HorStripedBar) getDisplayElement(20);
		HorStripedBar hb21 = (HorStripedBar) getDisplayElement(21);
		HorStripedBar hb22 = (HorStripedBar) getDisplayElement(22);
		HorStripedBar hb23 = (HorStripedBar) getDisplayElement(23);
		HorStripedBar hb24 = (HorStripedBar) getDisplayElement(24);
		VerStripedBar vb1 = (VerStripedBar) getDisplayElement(25);
		VerStripedBar vb2 = (VerStripedBar) getDisplayElement(26);
		VerStripedBar vb3 = (VerStripedBar) getDisplayElement(27);
		VerStripedBar vb4 = (VerStripedBar) getDisplayElement(28);
		VerStripedBar vb5 = (VerStripedBar) getDisplayElement(29);
		VerStripedBar vb6 = (VerStripedBar) getDisplayElement(30);
		VerStripedBar vb7 = (VerStripedBar) getDisplayElement(31);
		VerStripedBar vb8 = (VerStripedBar) getDisplayElement(32);
		VerStripedBar vb9 = (VerStripedBar) getDisplayElement(33);
		VerStripedBar vb10 = (VerStripedBar) getDisplayElement(34);
		VerStripedBar vb11 = (VerStripedBar) getDisplayElement(35);
		VerStripedBar vb12 = (VerStripedBar) getDisplayElement(36);
		VerStripedBar vb13 = (VerStripedBar) getDisplayElement(37);
		VerStripedBar vb14 = (VerStripedBar) getDisplayElement(38);
		VerStripedBar vb15 = (VerStripedBar) getDisplayElement(39);
		VerStripedBar vb16 = (VerStripedBar) getDisplayElement(40);
		VerStripedBar vb17 = (VerStripedBar) getDisplayElement(41);
		VerStripedBar vb18 = (VerStripedBar) getDisplayElement(42);
		VerStripedBar vb19 = (VerStripedBar) getDisplayElement(43);
		VerStripedBar vb20 = (VerStripedBar) getDisplayElement(44);
		VerStripedBar vb21 = (VerStripedBar) getDisplayElement(45);
		VerStripedBar vb22 = (VerStripedBar) getDisplayElement(46);
		VerStripedBar vb23 = (VerStripedBar) getDisplayElement(47);
		VerStripedBar vb24 = (VerStripedBar) getDisplayElement(48);
		hb1.setLocation(p[0].x, p[0].y);
		hb1.setSize(p[0].width, p[0].height);
		hb1.setPhase(2, 1);
		vb1.setLocation(p[1].x, p[1].y);
		vb1.setSize(p[1].width, p[1].height);
		vb1.setPhase(2, 1);
		hb2.setLocation(p[2].x, p[2].y);
		hb2.setSize(p[2].width, p[2].height);
		hb2.setPhase(2, 1);
		vb2.setLocation(p[3].x, p[3].y);
		vb2.setSize(p[3].width, p[3].height);
		vb2.setPhase(2, 1);
		hb3.setLocation(p[4].x, p[4].y);
		hb3.setSize(p[4].width, p[4].height);
		hb3.setPhase(2, 1);
		vb3.setLocation(p[5].x, p[5].y);
		vb3.setSize(p[5].width, p[5].height);
		vb3.setPhase(2, 1);
		hb4.setLocation(p[6].x, p[6].y);
		hb4.setSize(p[6].width, p[6].height);
		hb4.setPhase(2, 1);
		vb4.setLocation(p[7].x, p[7].y);
		vb4.setSize(p[7].width, p[7].height);
		vb4.setPhase(2, 1);
		vb5.setLocation(p[8].x, p[8].y);
		vb5.setSize(p[8].width, p[8].height);
		vb5.setPhase(2, 1);
		hb5.setLocation(p[9].x, p[9].y);
		hb5.setSize(p[9].width, p[9].height);
		hb5.setPhase(2, 1);
		vb6.setLocation(p[10].x, p[10].y);
		vb6.setSize(p[10].width, p[10].height);
		vb6.setPhase(2, 1);
		hb6.setLocation(p[11].x, p[11].y);
		hb6.setSize(p[11].width, p[11].height);
		hb6.setPhase(2, 1);
		vb7.setLocation(p[12].x, p[12].y);
		vb7.setSize(p[12].width, p[12].height);
		vb7.setPhase(2, 1);
		hb7.setLocation(p[13].x, p[13].y);
		hb7.setSize(p[13].width, p[13].height);
		hb7.setPhase(2, 1);
		vb8.setLocation(p[14].x, p[14].y);
		vb8.setSize(p[14].width, p[14].height);
		vb8.setPhase(2, 1);
		hb8.setLocation(p[15].x, p[15].y);
		hb8.setSize(p[15].width, p[15].height);
		hb8.setPhase(2, 1);
		hb9.setLocation(p[16].x, p[16].y);
		hb9.setSize(p[16].width, p[16].height);
		hb9.setPhase(2, 1);
		vb9.setLocation(p[17].x, p[17].y);
		vb9.setSize(p[17].width, p[17].height);
		vb9.setPhase(2, 1);
		hb10.setLocation(p[18].x, p[18].y);
		hb10.setSize(p[18].width, p[18].height);
		hb10.setPhase(2, 1);
		vb10.setLocation(p[19].x, p[19].y);
		vb10.setSize(p[19].width, p[19].height);
		vb10.setPhase(2, 1);
		hb11.setLocation(p[20].x, p[20].y);
		hb11.setSize(p[20].width, p[20].height);
		hb11.setPhase(2, 1);
		vb11.setLocation(p[21].x, p[21].y);
		vb11.setSize(p[21].width, p[21].height);
		vb11.setPhase(2, 1);
		hb12.setLocation(p[22].x, p[22].y);
		hb12.setSize(p[22].width, p[22].height);
		hb12.setPhase(2, 1);
		vb12.setLocation(p[23].x, p[23].y);
		vb12.setSize(p[23].width, p[23].height);
		vb12.setPhase(2, 1);
		vb13.setLocation(p[24].x, p[24].y);
		vb13.setSize(p[24].width, p[24].height);
		vb13.setPhase(2, 1);
		hb13.setLocation(p[25].x, p[25].y);
		hb13.setSize(p[25].width, p[25].height);
		hb13.setPhase(2, 1);
		vb14.setLocation(p[26].x, p[26].y);
		vb14.setSize(p[26].width, p[26].height);
		vb14.setPhase(2, 1);
		hb14.setLocation(p[27].x, p[27].y);
		hb14.setSize(p[27].width, p[27].height);
		hb14.setPhase(2, 1);
		vb15.setLocation(p[28].x, p[28].y);
		vb15.setSize(p[28].width, p[28].height);
		vb15.setPhase(2, 1);
		hb15.setLocation(p[29].x, p[29].y);
		hb15.setSize(p[29].width, p[29].height);
		hb15.setPhase(2, 1);
		vb16.setLocation(p[30].x, p[30].y);
		vb16.setSize(p[30].width, p[30].height);
		vb16.setPhase(2, 1);
		hb16.setLocation(p[31].x, p[31].y);
		hb16.setSize(p[31].width, p[31].height);
		hb16.setPhase(2, 1);
		hb17.setLocation(p[32].x, p[32].y);
		hb17.setSize(p[32].width, p[32].height);
		hb17.setPhase(2, 1);
		vb17.setLocation(p[33].x, p[33].y);
		vb17.setSize(p[33].width, p[33].height);
		vb17.setPhase(2, 1);
		hb18.setLocation(p[34].x, p[34].y);
		hb18.setSize(p[34].width, p[34].height);
		hb18.setPhase(2, 1);
		vb18.setLocation(p[35].x, p[35].y);
		vb18.setSize(p[35].width, p[35].height);
		vb18.setPhase(2, 1);
		hb19.setLocation(p[36].x, p[36].y);
		hb19.setSize(p[36].width, p[36].height);
		hb19.setPhase(2, 1);
		vb19.setLocation(p[37].x, p[37].y);
		vb19.setSize(p[37].width, p[37].height);
		vb19.setPhase(2, 1);
		hb20.setLocation(p[38].x, p[38].y);
		hb20.setSize(p[38].width, p[38].height);
		hb20.setPhase(2, 1);
		vb20.setLocation(p[39].x, p[39].y);
		vb20.setSize(p[39].width, p[39].height);
		vb20.setPhase(2, 1);
		vb21.setLocation(p[40].x, p[40].y);
		vb21.setSize(p[40].width, p[40].height);
		vb21.setPhase(2, 1);
		hb21.setLocation(p[41].x, p[41].y);
		hb21.setSize(p[41].width, p[41].height);
		hb21.setPhase(2, 1);
		vb22.setLocation(p[42].x, p[42].y);
		vb22.setSize(p[42].width, p[42].height);
		vb22.setPhase(2, 1);
		hb22.setLocation(p[43].x, p[43].y);
		hb22.setSize(p[43].width, p[43].height);
		hb22.setPhase(2, 1);
		vb23.setLocation(p[44].x, p[44].y);
		vb23.setSize(p[44].width, p[44].height);
		vb23.setPhase(2, 1);
		hb23.setLocation(p[45].x, p[45].y);
		hb23.setSize(p[45].width, p[45].height);
		hb23.setPhase(2, 1);
		vb24.setLocation(p[46].x, p[46].y);
		vb24.setSize(p[46].width, p[46].height);
		vb24.setPhase(2, 1);
		hb24.setLocation(p[47].x, p[47].y);
		hb24.setSize(p[47].width, p[47].height);
		hb24.setPhase(2, 1);
	}
}
