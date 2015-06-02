package com.azwave.androidzwave.zwave.utils;

import java.util.ArrayList;

public class Log {
	
	/* ----------
	 * Variables
	 * ---------- */
	
	private ArrayList<String> listLog;
	private static final long serialVersionUID = 1137662789714599607L;

	/* ------------
	 * Constructor
	 * ------------ */
	
	public Log() {
		listLog = new ArrayList<String>();
	}

	
	
	/* ----------
	 * Methods
	 * ---------- */
	
	/*  Add a string log in the list */
	public synchronized void add(final String object) {
		listLog.add(object + "\n");
	}

	/* Delete the element with the number i */
	public void remove(int i){
		listLog.remove(i);
	}
	
	/* Return the list with logs */
	public ArrayList<String> getListLog(){
		return listLog;
	}
	
	/* Set the value of the list */
	public void setListLog( ArrayList<String> _listLog ){
		listLog = _listLog;
	}
	
	
//	private void addSuper(String object) {
//		super.add(String.format("%05d| %s", nomerLog++, object));
//	}

	
//	public View getView(int position, View convertView, ViewGroup parent) {
//		LayoutInflater inflater = (LayoutInflater) context
//				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View rowView = inflater.inflate(listItemResourceId, parent, false);
//
//		TextView textView = (TextView) rowView.findViewById(R.id.log_text);
//		textView.setText(this.getItem(position));
//
//		return rowView;
//	}

	

}
