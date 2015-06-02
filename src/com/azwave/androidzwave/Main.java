package com.azwave.androidzwave;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.SwingWorker;

import com.azwave.androidzwave.module.Frame;
import com.azwave.androidzwave.zwave.Manager;
import com.azwave.androidzwave.zwave.driver.UsbDriver;
import com.azwave.androidzwave.zwave.driver.UsbSerialDriver;
import com.azwave.androidzwave.zwave.items.ControllerActionListener;
import com.azwave.androidzwave.zwave.items.Msg;
import com.azwave.androidzwave.zwave.items.ControllerCmd.ControllerError;
import com.azwave.androidzwave.zwave.items.ControllerCmd.ControllerState;
import com.azwave.androidzwave.zwave.nodes.Node;
import com.azwave.androidzwave.zwave.nodes.NodeListener;


public class Main implements ControllerActionListener, NodeListener {

	/* ----------
	 * Variables
	 * ---------- */
	
	private ArrayList<String> zwaveLogList;
	private ArrayList<Node> zwaveNodeList;
	private UsbSerialDriver serialDriver;
	private Manager zwaveManager;

	private volatile long initStartTime = 0;
	private volatile long initEndTime = 0;
	
	private ListNode listNode;
	
	/* ----------
	 * Constructors
	 * ---------- */
	
	public Main(){
		zwaveNodeList = new ArrayList<Node>();
		zwaveLogList = new ArrayList<String>();
		zwaveManager = new Manager();	
	}
	
	/* ----------
	 * Main program
	 * ---------- */
	
	public static void main(String[] args){
		Main main = new Main();
		Frame window = new Frame();
		ArrayList<Node> nodeList = new ArrayList<Node>();
		ArrayList<String> logList = new ArrayList<String>();
		
		main.initUsbDriver();
		nodeList = main.zwaveManager.getAllNodes();
		logList = main.zwaveManager.getLog().getListLog();
		window.updateLogContent(logList);		
		
		System.out.println(main.zwaveNodeList.size());
		for(int i=0; i<main.zwaveNodeList.size(); i++){
			System.out.println(main.zwaveNodeList.get(i));
		}
		System.out.println("#######" + main.zwaveManager.getNodeManager().getQueueManager().size());
	}
	 
	/* ----------
	 * Methods
	 * ---------- */
	
	public void finish() {
		if (zwaveManager != null) {
			try {
				zwaveManager.close();
			} catch (Exception x) {
				System.out.println("Error during the process of closing the program");
			}
		}
	}

	private void initUsbDriver() {
		serialDriver = new UsbDriver();
		try {
			zwaveManager = new Manager(serialDriver);
			zwaveLogList = zwaveManager.getLog().getListLog();
			listNode = new ListNode(zwaveManager);
			listNode.execute();
			zwaveManager.setNodeListener(this);
			zwaveManager.setControllerActionListener(this);
			zwaveManager.open();
			zwaveNodeList = zwaveManager.getAllNodes();		
		} catch(Exception x) {
			System.out.println("Error during the openning of the Manager");
			finish();
		}
		
	}
	
	
	public class ListNode extends SwingWorker<Manager, ArrayList<Node>> {
		
		public ArrayList<Node> list;
		public volatile boolean lock = true;
		public volatile int size = 0;
		public volatile boolean foundupdate = false;
		public Manager manager;
		
		public ListNode(Manager _manager){
			this.manager = _manager;
			this.list = manager.getAllNodes();
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.SwingWorker#doInBackground()
		 */
		@Override
		protected Manager doInBackground() throws Exception {
			ArrayList<Node> nodes = null;
			while (lock) {
				if (foundupdate) {
					nodes = manager.getAllNodesAlive();
					publish(nodes);
					if (foundupdate) {
						foundupdate = false;
					}
				}
			}

			return null;
		}
		
		protected void process(ArrayList<Node> progress) {
			if (progress != null) {
				zwaveNodeList.clear();
				for (int i = 0; i < progress.size(); i++) {
					zwaveNodeList.add(progress.get(i));
				}
//				zwaveNodeList.notifyDataSetChanged();
				System.out.println("ZwaveNodeList has changed!");
			}
		}
		
		public synchronized void done(){
			lock = false;
		}
		
	}

	

	/* (non-Javadoc)
	 * @see com.azwave.androidzwave.zwave.items.ControllerActionListener#onAction(com.azwave.androidzwave.zwave.items.ControllerCmd.ControllerState, com.azwave.androidzwave.zwave.items.ControllerCmd.ControllerError, java.lang.Object)
	 */
	@Override
	public void onAction(ControllerState state, ControllerError error, Object context) {
		System.out.println("ACTTIOOOONNNNNN");
		new Thread(){
			public void run() {
				switch (state) {
				case Normal:
					break;
				case Starting:
					break;
				case Cancel:
					break;
				case Error:
					break;
				case Waiting:
					System.out.println("Waiting for node initiator");
					break;
				case Sleeping:
					break;
				case InProgress:
					System.out.println("Please wait...");
					break;
				case Completed:
					System.out.println("Controller command completed");
					break;
				case Failed:
					System.out.println("Controller command failed");
					break;
				case NodeOK:
					break;
				case NodeFailed:
					break;
				}
			}
		}.start();
	}

	/* (non-Javadoc)
	 * @see com.azwave.androidzwave.zwave.nodes.NodeListener#onNodeAliveListener(boolean)
	 */
	@Override
	public void onNodeAliveListener(boolean alive) {
		System.out.println("ALIVEEEEEEEE");
		listNode.foundupdate = true;
	}

	/* (non-Javadoc)
	 * @see com.azwave.androidzwave.zwave.nodes.NodeListener#onNodeQueryStageCompleteListener()
	 */
	@Override
	public void onNodeQueryStageCompleteListener() {
		listNode.foundupdate = true;
		System.out.println("Z-Wave Initialization Complete");
	}

	/* (non-Javadoc)
	 * @see com.azwave.androidzwave.zwave.nodes.NodeListener#onNodeAddedToList()
	 */
	@Override
	public void onNodeAddedToList() {
		// TODO Auto-generated method stub
		System.out.println("Node added to list");
	}

	/* (non-Javadoc)
	 * @see com.azwave.androidzwave.zwave.nodes.NodeListener#onNodeRemovedToList()
	 */
	@Override
	public void onNodeRemovedToList() {
		listNode.foundupdate = true;
	}
	
}
