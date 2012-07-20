package de.pxlab.awtx;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Create a List object with is filled by a given array of strings.
 * 
 * @author H. Irtel
 * @version 0.1.2
 */
public class ItemList extends java.awt.List {
	ItemListKeyListener kL;
	ItemListItemListener iL;
	int listPosition = 0;

	public ItemList(int n, boolean mult, String[] items) {
		super(n, mult);
		if ((items != null) && (items.length != 0)) {
			int nf = items.length;
			for (int i = 0; i < nf; i++) {
				add(items[i]);
			}
			kL = new ItemListKeyListener(items);
			iL = new ItemListItemListener();
			super.addKeyListener(kL);
			super.addItemListener(iL);
		}
	}

	/**
	 * Gets the preferred dimensions for a list with the specified number of
	 * rows.
	 * 
	 * @param rows
	 *            number of rows in the list.
	 * @return the preferred dimensions for displaying this scrolling list given
	 *         that the specified number of rows must be visible.
	 * @see java.awt.Component#getPreferredSize
	 */
	public Dimension getPreferredSize(int rows) {
		Dimension d = super.getPreferredSize(rows);
		return new Dimension(d.width + 20, d.height);
	}

	/**
	 * Gets the preferred size of this scrolling list.
	 * 
	 * @return the preferred dimensions for displaying this scrolling list.
	 * @see java.awt.Component#getPreferredSize
	 */
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		return new Dimension(d.width + 20, d.height);
	}
	/**************************************************
	 * ItemListKeyListener listens to keyboard input and selects the
	 * corresponding list item
	 *************************************************/
	class ItemListKeyListener implements KeyListener {
		public ItemListKeyListener(String[] items) {
			super();
		}

		public void keyPressed(KeyEvent ke) {
		}

		/**
		 * If the user pushes an alphabetic key the list should highlight the
		 * next Item starting with this letter. If the end of the list is
		 * reached it jumps to the beginning. Items are not selected by this
		 * procedure, but only highlighted (which means there is a frame
		 * surrounding the item).
		 */
		public void keyReleased(KeyEvent ke) {
			String prefix = String.valueOf(ke.getKeyChar());
			int iC = getItemCount();
			boolean foundItem = false;
			int oldListPosition = listPosition;
			for (int i = listPosition; i < iC; i++) {
				if (getItem(i).startsWith(prefix.toLowerCase())
						|| getItem(i).startsWith(prefix.toUpperCase())) {
					if (isIndexSelected(i)) {
						select(i);
						foundItem = true;
					} else {
						select(i);
						deselect(i);
						foundItem = true;
					}
					listPosition = ++i;
					break;
				}
			}
			if (!foundItem) {
				for (int i = 0; i < oldListPosition; i++) {
					if (getItem(i).startsWith(prefix.toLowerCase())
							|| getItem(i).startsWith(prefix.toUpperCase())) {
						if (isIndexSelected(i)) {
							select(i);
							foundItem = true;
						} else {
							select(i);
							deselect(i);
							foundItem = true;
						}
						listPosition = ++i;
						break;
					}
				}
			}
		}

		public void keyTyped(KeyEvent ke) {
		}
	}
	class ItemListItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			/*
			 * it is necessary to get listPosition like this, as we do not
			 * select the items when we scroll through the list via keyboard
			 * input. java.awt.List does only have a method to get selected
			 * items, but not to get the highlighted ones!
			 */
			Object o = e.getItem();
			for (int i = 0; i < getItemCount(); i++) {
				String s = o.toString();
				listPosition = Integer.parseInt(s);
				listPosition++;
			}
		}
	}
}
