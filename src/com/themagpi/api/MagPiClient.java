package com.themagpi.api;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.as.asyncache.AsynCache;
import org.json.JSONException;
import org.json.JSONObject;
import org.mcsoxford.rss.RSSConfig;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSParser;

import android.content.Context;
import android.content.SharedPreferences;
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
    
    private final static String DATE_FORMAT = "yyyy-M-d HH:mm:ss Z";

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
    
    public void registerDevice(final Context context, String idGcm) {
		try {
			Log.e("START", "REGISTERING");
			JSONObject jsonDevice = new JSONObject();
			jsonDevice.put("id", Utils.getDeviceId(context));
			jsonDevice.put("id_gcm", idGcm);
			jsonDevice.put("language", Locale.getDefault().getLanguage());
			jsonDevice.put("os", "Android"); 
			StringEntity entity;
			entity = createUTF8StringEntity(jsonDevice.toString());
			AsyncHttpClient client = new AsyncHttpClient();
			setAuth(context, client, "POST", Config.SERVICE_URL + "/register", jsonDevice.toString());
			client.post(context, Config.SERVICE_URL + "/register", null,
                    entity, "application/json", new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(JSONObject response) { 	
			            	Log.e("SUCC", "REGISTERING" + response);
					    	SharedPreferences prefs = context.getSharedPreferences("MAGPI_REGISTRATION", Context.MODE_PRIVATE);
					    	prefs.edit().putLong("TIME_LAST_REG", Calendar.getInstance().getTimeInMillis()).commit();
						}
						 
			            @Override
			            public void onFailure(Throwable e, JSONObject response) {
			            	Log.e("FAIL", "REGISTERING" + response);
			            	
			            }
			            
			            @Override
			            public void onFailure(Throwable e, String response) {
			            	Log.e("FAIL", "REGISTERING" + response);
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
			entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
			return entity;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
    
    public void getIssues(final Context context, final OnIssuesReceivedListener issueListener) {
    	AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://www.themagpi.com/mps_api/mps-api-v1.php?mode=list_issues", new JsonHttpResponseHandler() {
        	
        	private void sendSuccessResponse(JSONObject response) {
            	try {
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
    
    public void getNews(final Context context, final OnNewsReceivedListener newsListener) {
    	AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://feeds.feedburner.com/MagPi", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(final String response) {
	           	AsynCache.getInstance().write(context, "getNews", response.toString(), new AsynCache.WriteResponseHandler() {
	
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
            public void onFailure(Throwable e, String response) {
            	tryLoadingFromCache(response);
            }
            
        	
        	private void sendSuccessResponse(String response) {
            	try {
	                RSSParser parser = new RSSParser(new RSSConfig());
	                RSSFeed feed = parser.parse(new ByteArrayInputStream(response.getBytes()));
	                newsListener.onReceived(NewsFactory.buildFromRSSFeed(feed));
            	} catch (Exception ex) {
            		ex.printStackTrace();
            	}
        	}
            
            private void sendFailureResponse(String response) {
            	try {
                	newsListener.onError(0);
            	} catch (Exception ex) {
            		ex.printStackTrace();
            	}
            }
            
        	private void tryLoadingFromCache(final String response) {
        		AsynCache.getInstance().read(context, "getNews", new AsynCache.ReadResponseHandler() {

		            @Override
		            public void onSuccess(byte[] data) {
		            	sendSuccessResponse(new String(data));
		            }

		            @Override
		            public void onFailure(Throwable t) {
		            	sendFailureResponse(response);
		            	t.printStackTrace();
		            }

		        });
        	}
        });
    }
    
    protected void setAuth(Context context, AsyncHttpClient client, String verb, String url, String contentToEncode) {
        String currentDate = new SimpleDateFormat(DATE_FORMAT).format(new Date());

        String contentMd5 = SecurityUtils.md5(contentToEncode);
        String toSign = verb + "\n" + contentMd5 + "\n" + currentDate + "\n" + url;

        Log.e("STRINGTOHASH", "-" + toSign);

        String hmac = SecurityUtils.Hmac(Config.APP_SECRET, toSign);
        client.addHeader("Datetime", currentDate);
        client.addHeader("Content-Md5", contentMd5);
        client.addHeader("Hmac", SecurityUtils.md5(Config.USER) + ":" + hmac);
        Log.e("HMAC", SecurityUtils.md5(Config.USER) + ":" + hmac);
    }
    
}
