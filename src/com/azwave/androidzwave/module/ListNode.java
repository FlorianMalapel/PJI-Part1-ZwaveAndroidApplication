/**
 * 
 */
package com.azwave.androidzwave.module;

import java.util.ArrayList;

import javax.swing.SwingWorker;

import com.azwave.androidzwave.zwave.Manager;
import com.azwave.androidzwave.zwave.nodes.Node;

/**
 * @author florian
 *
 */
//public class ListNode extends SwingWorker<Manager, ArrayList<Node>> {
//	
//	public ArrayList<Node> list;
//	public volatile boolean lock = true;
//	public volatile int size = 0;
//	public volatile boolean foundupdate = false;
//	public Manager manager;
//	
//	public ListNode(Manager _manager){
//		this.manager = _manager;
//		this.list = manager.getAllNodes();
//	}
//	
//	/* (non-Javadoc)
//	 * @see javax.swing.SwingWorker#doInBackground()
//	 */
//	@Override
//	protected Manager doInBackground() throws Exception {
//		ArrayList<Node> nodes = null;
//		while (lock) {
//			if (foundupdate) {
//				nodes = manager.getAllNodesAlive();
//				publish(nodes);
//				if (foundupdate) {
//					foundupdate = false;
//				}
//			}
//		}
//
//		return null;
//	}
//	
//	protected void process(ArrayList<Node> progress) {
//		if (progress != null) {
////			nodelistAdapter.clear();
//			for (int i = 0; i < progress.size(); i++) {
//				nodelistAdapter.add(progress.get(i));
//			}
//			nodelistAdapter.notifyDataSetChanged();
//		}
//	}
//	
//}
