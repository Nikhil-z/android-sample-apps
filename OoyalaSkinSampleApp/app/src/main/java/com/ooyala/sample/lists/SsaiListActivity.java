package com.ooyala.sample.lists;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ooyala.sample.R;
import com.ooyala.sample.players.SsaiPlayerActivity;
import com.ooyala.sample.utils.PlayerSelectionOption;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;


public class SsaiListActivity extends Activity implements AdapterView.OnItemClickListener {
  private static final int LOAD_REACT_BUNDLE_PERMISSION_REQ_CODE = 666;

  public final static String getName() {
    return "SSAI Playback";
  }

  private static Map<String, SsaiSelectionOption> selectionMap;
  ArrayAdapter<String> selectionAdapter;

  /**
   * This is used to store information of a Ssai sample activity for use in a Map or List
   *
   */
  class SsaiSelectionOption extends PlayerSelectionOption {
    private String playerParams;

    public SsaiSelectionOption(String embedCode, String pcode, String domain, Class<? extends Activity> activity) {
      this(embedCode, pcode, domain, activity, "");
    }

    public SsaiSelectionOption(String embedCode, String pcode, String domain, Class<? extends Activity> activity, String playerParams) {
      super(embedCode, pcode, domain, activity);
      this.playerParams = playerParams;
    }

    public String getPlayerParams() {
      return playerParams;
    }

    public void setPlayerParams(String playerParams) {
      this.playerParams = playerParams;
    }
  }

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getName());
    String playerParamsLiveTeam = "{\"videoplaza-ads-manager\":{\"metadata\":{\"all_ads\":[{\"position\":\"7\"}],\"playerLevelCuePoints\":\"30,10,20\",\"playerLevelShares\":\"nab\",\"playerLevelTags\":\"me,mw,fe,fw\",\"vpDomain\":\"live-team\"}}}";
    String playerParamsVideoPlaza = "{\"videoplaza-ads-manager\":{\"metadata\":{\"all_ads\":[{\"position\":\"7\"}],\"playerLevelCuePoints\":\"10,20,30\",\"playerLevelShares\":\"ssai-playback\",\"playerLevelTags\":\"ssai\",\"vpDomain\":\"live-team.videoplaza.tv\"}}}";

    JSONObject dataFromJson = getResourceAsJsonObject("ssaiPlayerParams.json");
    String paramsFromJson = dataFromJson == null ? "" : dataFromJson.toString();

    selectionMap = new LinkedHashMap<>();
    selectionMap.put("Without Player Params", new SsaiSelectionOption("ltZ3l5YjE6lUAvBdflvcDQ-zti8q8Urd", "RpOWUyOq86gFq-STNqpgzhzIcXHV", "http://www.ooyala.com", SsaiPlayerActivity.class));
    selectionMap.put("Player Params live-team", new SsaiSelectionOption("ltZ3l5YjE6lUAvBdflvcDQ-zti8q8Urd", "RpOWUyOq86gFq-STNqpgzhzIcXHV", "http://www.ooyala.com", SsaiPlayerActivity.class, playerParamsLiveTeam));
    selectionMap.put("Player Params videoplaza", new SsaiSelectionOption("ltZ3l5YjE6lUAvBdflvcDQ-zti8q8Urd", "RpOWUyOq86gFq-STNqpgzhzIcXHV", "http://www.ooyala.com", SsaiPlayerActivity.class, playerParamsVideoPlaza));
    selectionMap.put("Player Params from JSON", new SsaiSelectionOption("ltZ3l5YjE6lUAvBdflvcDQ-zti8q8Urd", "RpOWUyOq86gFq-STNqpgzhzIcXHV", "http://www.ooyala.com", SsaiPlayerActivity.class, paramsFromJson));
    selectionMap.put("SSAI Live", new SsaiSelectionOption("lkb2cyZjE6wp94YSGIEjm6Em1yH0P3zT", "RpOWUyOq86gFq-STNqpgzhzIcXHV", "http://www.ooyala.com", SsaiPlayerActivity.class));

    setContentView(R.layout.list_activity_layout);

    //Create the adapter for the ListView
    selectionAdapter = new ArrayAdapter<>(this, R.layout.list_activity_list_item);
    for (String key : selectionMap.keySet()) {
      selectionAdapter.add(key);
    }
    selectionAdapter.notifyDataSetChanged();

    //Load the data into the ListView
    ListView selectionListView = (ListView) findViewById(R.id.mainActivityListView);
    selectionListView.setAdapter(selectionAdapter);
    selectionListView.setOnItemClickListener(this);
  }

  private void showDevOptionsDialog() {
    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
    startActivityForResult(intent, LOAD_REACT_BUNDLE_PERMISSION_REQ_CODE);
  }

  @Override
  public void onItemClick(AdapterView<?> l, View v, int pos, long id) {
    SsaiSelectionOption selection =  selectionMap.get(selectionAdapter.getItem(pos));
    Class<? extends Activity> selectedClass = selection.getActivity();

    //Launch the correct activity with the embed code as an extra
    Intent intent = new Intent(this, selectedClass);
    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    intent.putExtra("embed_code", selection.getEmbedCode());
    intent.putExtra("selection_name", selectionAdapter.getItem(pos));
    intent.putExtra("pcode", selection.getPcode());
    intent.putExtra("domain", selection.getDomain());
    intent.putExtra("player_params", selection.getPlayerParams());
    startActivity(intent);
  }

  private JSONObject getResourceAsJsonObject(String resource)  {
    ClassLoader classLoader = this.getClass().getClassLoader();
    InputStream is = classLoader.getResourceAsStream(resource);
    try {
      int size = is.available();
      byte[] buffer = new byte[size];
      is.read(buffer);
      is.close();
      return new JSONObject(new String(buffer, "UTF-8"));
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }
}