package com.valleytg.oasvn.android.ui.activity;

import java.io.File;

import org.tmatesoft.svn.core.SVNCommitInfo;

import com.valleytg.oasvn.android.R;
import com.valleytg.oasvn.android.application.OASVNApplication;
import com.valleytg.oasvn.android.ui.activity.ConnectionDetails;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ConnectionDetails extends Activity {
	
	/**
	 * Application layer
	 */
	OASVNApplication app;
	
	/**
	 * Controls
	 */
	TextView topAreaHeader;
	
	TextView status;
	
	TextView topArea1Title;
	TextView topArea2Title;
	TextView topArea3Title;
	TextView topArea4Title;
	TextView topArea5Title;
	
	TextView topArea1;
	TextView topArea2;
	TextView topArea3;
	TextView topArea4;
	TextView topArea5;
	
	Button btnCheckoutHead;
	Button btnCommit;
	Button btnEdit;
	Button btnDelete;
	
	/**
	 * Thread control
	 */
	Boolean running = false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection_details);
        
        // get the application
        this.app = (OASVNApplication)getApplication();
        
        this.topAreaHeader = (TextView)findViewById(R.id.conndetail_top_header);
        this.status = (TextView)findViewById(R.id.conndetail_status);
    	
    	this.topArea1Title = (TextView)findViewById(R.id.conndetail_top1_title);
        this.topArea2Title = (TextView)findViewById(R.id.conndetail_top2_title);
        this.topArea3Title = (TextView)findViewById(R.id.conndetail_top3_title);
        this.topArea4Title = (TextView)findViewById(R.id.conndetail_top4_title);
        this.topArea5Title = (TextView)findViewById(R.id.conndetail_top5_title);
        
        this.topArea1 = (TextView)findViewById(R.id.conndetail_top1);
        this.topArea2 = (TextView)findViewById(R.id.conndetail_top2);
        this.topArea3 = (TextView)findViewById(R.id.conndetail_top3);
        this.topArea4 = (TextView)findViewById(R.id.conndetail_top4);
        this.topArea5 = (TextView)findViewById(R.id.conndetail_top5);
        
        // buttons
        btnCheckoutHead = (Button) findViewById(R.id.conndetail_full_checkout);
        btnCommit = (Button) findViewById(R.id.conndetail_full_commit);
        btnDelete = (Button) findViewById(R.id.conndetail_delete_local);
        btnEdit = (Button) findViewById(R.id.conndetail_edit);
        
        // populate the top
        populateTopInfo();
        
        this.btnCheckoutHead.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// open the add repository activity
				if(running == false) {
					// set the running flag
					ConnectionDetails.this.running = true;
					
					// set the status
					ConnectionDetails.this.status.setText(R.string.performing_checkout);
					
					CheckoutThread checkoutThread = new CheckoutThread();
					checkoutThread.execute();
				}
				else {
					Toast.makeText(ConnectionDetails.this, getString(R.string.in_progress), 2500).show();
				}
			}
		});
        
        this.btnCommit.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// try to text the phone number
	        	try {
	                Intent callIntent = new Intent(ConnectionDetails.this, CommitActivity.class);
	                // set the current contact
	                //callIntent.putExtra("number", myList.get(position).getPhoneNumber());
	                ConnectionDetails.this.startActivity(callIntent);
	            } catch (ActivityNotFoundException e) {
	                e.printStackTrace();
	            }
			}
		});
        
        
        
        
        
        this.btnEdit.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				// open the add repository activity
				Intent intent = new Intent(ConnectionDetails.this, AddRepository.class);
				startActivity(intent);
				
			}
		});
        
        this.btnDelete.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// open the add repository activity
				if(running	== false) {

					
					
					// double check the users intention
					AlertDialog.Builder builder = new AlertDialog.Builder(ConnectionDetails.this);
					
					builder.setIcon(android.R.drawable.ic_dialog_alert);
					builder.setTitle(R.string.confirm);
					builder.setMessage(getString(R.string.delete_message));
					builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

			            public void onClick(DialogInterface dialog, int which) {
			            	synchronized (this) {
			            		try{
			            			app.initializePath();
			            			File tree = app.assignPath();
			            			app.deleteRecursive(tree);
		    				        
			            		} 
			            		catch(Exception e) {
			            			e.printStackTrace();
			            		}
			            	}
							
			            }

			        });
		        builder.setNegativeButton(R.string.no, null);
		        builder.show();	
					
				}
				else {
					Toast.makeText(ConnectionDetails.this, getString(R.string.in_progress), 2500).show();
				}
			}
		});
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		
		// populate the top
        populateTopInfo();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// populate the top
        populateTopInfo();
	}
	
	

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		
		app.setCurrentConnection(null);
	}

	public void resetIdle() {
		// set the status
		this.status.setText(R.string.idle);
	}

	private void populateTopInfo() {
		
		// create the header area info
		if(this.app.getCurrentConnection() != null) {
			if(this.app.getCurrentConnection().getActive()) {
				// populate the top header
				this.topAreaHeader.setText(this.getString(R.string.connection) + getText(R.string.colon) + this.app.getCurrentConnection().getName());
				
				String url = "";
				try {
					url = this.app.getCurrentConnection().getTextURL().toString();
				}
				catch(Exception e) {
					url = "unknown";
				}
				
				String protocol = "";
				try {
					protocol = this.app.getCurrentConnection().getType().toString();
				}
				catch(Exception e) {
					protocol = "unknown";
				}
				
				String username = "";
				try {
					username = this.app.getCurrentConnection().getUsername().toString();
				}
				catch(Exception e) {
					username = "unknown";
				}
				
				String folder = "";
				try {
					folder = app.getFullPathToMain().toString() + this.app.getCurrentConnection().getFolder().toString();
				}
				catch(Exception e) {
					folder = "unknown";
				}
				
				String head = "";
				try {
					head = this.app.getCurrentConnection().getHead().toString();
				}
				catch(Exception e) {
					head = "unknown";
				}
				
				// assign text to individual text areas
				this.topArea1Title.setText(this.getString(R.string.url) + this.getString(R.string.colon));
				this.topArea1.setText(url);
				
				this.topArea2Title.setText(this.getString(R.string.protocol) + this.getString(R.string.colon));
				this.topArea2.setText(protocol);
				
				this.topArea3Title.setText(this.getString(R.string.username) + this.getString(R.string.colon));
				this.topArea3.setText(username);
				
				this.topArea4Title.setText(this.getString(R.string.folder) + this.getString(R.string.colon));
				this.topArea4.setText(folder);
				
				this.topArea5Title.setText(this.getString(R.string.head) + this.getString(R.string.colon));
				this.topArea5.setText(head);
	
			}
			else {
				// no ticket was selected go back to ticket screen
				// tell the user we are going to work
	        	Toast toast=Toast.makeText(this, getString(R.string.no_connection_selected), 2500);
	    		toast.show();
	    		this.finish();
			}
			
			// check to see if the system is idle
			if(this.running) {
				ConnectionDetails.this.status.setText(R.string.performing_checkout);
			}
			else {
				ConnectionDetails.this.status.setText(R.string.idle);
			}
		}
		else {
			// no ticket was selected go back to ticket screen
			// tell the user we are going to work
        	Toast toast=Toast.makeText(this, getString(R.string.no_connection_selected), 2500);
    		toast.show();
    		this.finish();
		}

	}
	
	class CheckoutThread extends AsyncTask<Void, Void, String> {

		ProgressDialog dialog;

		@Override
	    protected void onPreExecute() {
	        dialog = new ProgressDialog(ConnectionDetails.this);
	        dialog.setMessage(getString(R.string.in_progress));
	        dialog.setIndeterminate(true);
	        dialog.setCancelable(false);
	        dialog.show();
	    }
		
		@Override
		protected String doInBackground(Void... unused) {
			try {
				Looper.myLooper();
				Looper.prepare();
			}
			catch(Exception e) {
				// Looper only needs to be created if the thread is new, if reusing the thread we end up here
			}
			
			String returned;
			
			try {
				runOnUiThread(new Runnable() {
				     public void run() {
				    	// set the status
				    	 ConnectionDetails.this.status.setText(R.string.performing_checkout);

				     }
				});
				
				
				// do the checkout
				returned = app.fullHeadCheckout();
				
				// get the current version
				app.getCurrentConnection().setHead((int)app.getRevisionNumber());
				
				// save the current connection
				app.getCurrentConnection().saveToLocalDB(app);
				
			}
	        catch(Exception e) {
	        	e.printStackTrace();
	        	return e.getMessage();
	        }
			return returned;
		}
		
		protected void onPostExecute(final String result) {
			// unset the running flag
			ConnectionDetails.this.resetIdle();

			android.util.Log.d("alarm", "Checkout Successful!");

	        dialog.dismiss();
	        
	        runOnUiThread(new Runnable() {
			     public void run() {
			    	// indicate to the user that the action completed
					Toast.makeText(getApplicationContext(), result, 5000).show();
			     }
	        });
	        
	        // populate the top
	        populateTopInfo();
	        
	        ConnectionDetails.this.status.setText(R.string.idle);
	        
			// unset the running flag
			ConnectionDetails.this.running = false;
	    }
	}
}
