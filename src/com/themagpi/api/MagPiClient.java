package com.themagpi.api;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.as.asyncache.AsynCache;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mcsoxford.rss.RSSConfig;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSParser;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.themagpi.android.Config;
import com.themagpi.android.Utils;

/*
 * NEW API
 * Parameters are set in a query string to http://www.themagpi.com/mps_api/mps-api-v1.php
 * Parameters to set:
 * mode = list_issues | list_articles
 * issue_id
 * html = true | false : Optional, defaults to false.
 */

public class MagPiClient {
    
    private AsyncHttpClient client = new AsyncHttpClient();

    public static interface OnIssuesReceivedListener {
        public abstract void onReceived(ArrayList<Issue> issues);
        public abstract void onError(int error);
    }
    
    public static interface OnNewsReceivedListener {
        public abstract void onReceived(ArrayList<News> news);
        public abstract void onError(int error);
    }
    
    public static interface OnFileReceivedListener {
        public abstract void onReceived(byte[] fileData);
        public abstract void onError(int error);
    }
    
    public void registerDevice(Context context, String idGcm) {
		try {
			JSONObject jsonDevice = new JSONObject();
			jsonDevice.put("id", Utils.getDeviceId(context));
			jsonDevice.put("id_gcm", idGcm);
			jsonDevice.put("language", Locale.getDefault().getLanguage());
			jsonDevice.put("os", "Android");
			StringEntity entity;
			entity = createUTF8StringEntity(jsonDevice.toString());
			AsyncHttpClient client = new AsyncHttpClient();
			client.post(context, Config.SERVICE_URL + "/register", null, entity,
					"application/json", new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(JSONArray timeline) {

						}
					});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	protected static StringEntity createUTF8StringEntity(String string) {
		StringEntity entity;
		try {
			entity = new StringEntity(string, "UTF-8");
			entity.setContentType("application/json;charset=UTF-8");
			entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json;charset=UTF-8"));
			return entity;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
    
    public void getIssues(final Context context, final OnIssuesReceivedListener issueListener) {
        client.get("http://www.themagpi.com/mps_api/mps-api-v1.php?mode=list_issues", new JsonHttpResponseHandler() {
        	
        	private void sendSuccessResponse(JSONObject response) {
            	try {
                	Log.e("RESPONSE", response.toString());
                	issueListener.onReceived(IssuesFactory.buildFromJSONFeed(response));
            	} catch (Exception ex) {
            		ex.printStackTrace();
            	}
        	}
        	
        	private void sendFailureResponse(JSONObject response) {
            	try {
                	issueListener.onError(0);
            	} catch (Exception ex) {
            		ex.printStackTrace();
            	}
        	}
        	
        	private void tryLoadingFromCache(final JSONObject response) {
        		AsynCache.getInstance().read(context, "getIssue", new AsynCache.ReadResponseHandler() {

		            @Override
		            public void onSuccess(byte[] data) {
		            	try {
							JSONObject myCachedJson = new JSONObject(new String(data));
							sendSuccessResponse(myCachedJson);
						} catch (JSONException e) {
							sendFailureResponse(response);
							e.printStackTrace();
						}
		            }

		            @Override
		            public void onFailure(Throwable t) {
		            	sendFailureResponse(response);
		            	t.printStackTrace();
		            }

		        });
        	}
            
            @Override
            public void onSuccess(final JSONObject response) {
            	 AsynCache.getInstance().write(context, "getIssue", response.toString(), new AsynCache.WriteResponseHandler() {

					@Override
					public void onSuccess() {
						sendSuccessResponse(response);
					}

					@Override
					public void onFailure(Throwable t) {
						sendSuccessResponse(response);
					}

				});
            }
            
            @Override
            public void onFailure(Throwable e, JSONObject response) {
            	tryLoadingFromCache(response);
            }
            
            @Override
            public void onFailure(Throwable e, String response) {
            	tryLoadingFromCache(null);
            }
        });
    }
    
    public void getNews(final OnNewsReceivedListener newsListener) {
        client.get("http://feeds.feedburner.com/MagPi", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
            	try {
	                RSSParser parser = new RSSParser(new RSSConfig());
	                RSSFeed feed = parser.parse(new ByteArrayInputStream(response.getBytes()));
	                newsListener.onReceived(NewsFactory.buildFromRSSFeed(feed));
            	} catch (Exception ex) {
            		ex.printStackTrace();
            	}
            }
            
            @Override
            public void onFailure(Throwable e, String response) {
            	try {
                	newsListener.onError(0);
            	} catch (Exception ex) {
            		ex.printStackTrace();
            	}
            }
        });
    }
    
    public void close(Context ctx) {
        client.cancelRequests(ctx, true);
    }
}
