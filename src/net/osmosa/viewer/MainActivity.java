package net.osmosa.viewer;

import static net.osmosa.viewer.Constant.OSMOSA_TILE;

import java.util.ArrayList;

import net.osmosa.viewer.overlay.PointItemizedOverlay;

import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.TilesOverlay;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class MainActivity extends Activity {

    private MapView osmMapview;
    private MapController mapController;
    
    private PointItemizedOverlay<OverlayItem> mLocationOverlay;

    private static final int TILE_SOURCE = Menu.FIRST;
    private static final int OSMOSA = TILE_SOURCE + 1;
    private static final int OPENSTREETMAP = OSMOSA + 1;
    private boolean isOsmosa = true;

    private LocationManager locationManager;
    private MyLocationOverlay myLocationOverlay;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout relativeLayout = new RelativeLayout(this);

        osmMapview = new MapView(this, 256);
        relativeLayout.addView(osmMapview, new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
            LayoutParams.FILL_PARENT));

        osmMapview.setBuiltInZoomControls(true);
        osmMapview.setMultiTouchControls(true);
        osmMapview.getController().setZoom(2);
        osmMapview.getController().setCenter(new GeoPoint((int) (-2.56 * 1E6), (int) (116.97 * 1E6)));

        changeServer(OSMOSA_TILE);
        {
            ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
            items.add(new OverlayItem("Cempaka Putih", "Meruvian", new GeoPoint((int) (-6.29 * 1E6), (int) (106.75 * 1E6))));
            items.add(new OverlayItem("Blitar", "Home", new GeoPoint((int) (-8.10 * 1E6), (int) (112.16 * 1E6))));
            mLocationOverlay = new PointItemizedOverlay<OverlayItem>(items, 
                            this.getResources().getDrawable(R.drawable.marker_default), itemGestureListener, this);
            osmMapview.getOverlays().add(mLocationOverlay);
        }
        mapController = osmMapview.getController();
        mapController.setZoom(14);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        
        setContentView(relativeLayout);
        
        myLocationOverlay = new MyLocationOverlay(this, osmMapview, new ResourceProxyImpl(this));
        osmMapview.getOverlays().add(myLocationOverlay);
        myLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                osmMapview.getController().animateTo(myLocationOverlay.getMyLocation());
            }
        });
    }
    
    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLocationChanged(Location location) {
            int lat = (int) (location.getLatitude() * 1E6);
            int lng = (int) (location.getLongitude() * 1E6);
            GeoPoint point = new GeoPoint(lat, lng);
            mapController.animateTo(point);
        }
    };
    
    private OnItemGestureListener<OverlayItem> itemGestureListener = new OnItemGestureListener<OverlayItem>() {
        @Override
        public boolean onItemLongPress(int index, OverlayItem item) {
            Toast.makeText(MainActivity.this, item.mTitle, Toast.LENGTH_LONG).show();
            return true;
        }

        @Override
        public boolean onItemSingleTapUp(int index, OverlayItem item) {
            Toast.makeText(MainActivity.this, item.mTitle, Toast.LENGTH_LONG).show();
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SubMenu subMenu = menu.addSubMenu(Menu.CATEGORY_CONTAINER, TILE_SOURCE, Menu.NONE, "Choose Tile Source");
        {
            subMenu.add(Menu.CATEGORY_SECONDARY, OSMOSA, Menu.NONE, "osmosa.net");
            subMenu.add(Menu.CATEGORY_SECONDARY, OPENSTREETMAP, Menu.NONE, "openstreetmap.org");
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case OPENSTREETMAP:
                if (isOsmosa)
                    changeServer(TileSourceFactory.MAPNIK);
                isOsmosa = false;
                return true;
            case OSMOSA:
                if (!isOsmosa)
                    changeServer(OSMOSA_TILE);
                isOsmosa = true;
                return true;
        }
        return false;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        myLocationOverlay.enableMyLocation();
    }

    @Override
    protected void onPause() {
        super.onResume();
        myLocationOverlay.disableMyLocation();
    }
    
    public void changeServer(OnlineTileSourceBase tileSource) {
        MapTileProviderBasic tileProvider = new MapTileProviderBasic(getApplicationContext());
        tileProvider.setTileSource(tileSource);

        TilesOverlay tilesOverlay = new TilesOverlay(tileProvider, this.getBaseContext());
        tilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);

        if (osmMapview.getOverlays().isEmpty()) {
            osmMapview.getOverlays().add(tilesOverlay);
        } else {
            osmMapview.getOverlays().set(0, tilesOverlay);
        }
        osmMapview.invalidate();
    }
}