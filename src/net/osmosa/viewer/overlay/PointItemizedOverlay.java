package net.osmosa.viewer.overlay;

import java.util.List;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.OverlayItem.HotspotPlace;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class PointItemizedOverlay<Item extends OverlayItem> extends ItemizedIconOverlay<Item> {

    private Drawable marker;

    public PointItemizedOverlay(List<Item> aList, Drawable pMarker, OnItemGestureListener<Item> aOnItemTapListener,
        Context ctx) {
        super(aList, pMarker, aOnItemTapListener, new DefaultResourceProxyImpl(ctx));
        this.marker = pMarker;
    }

    @Override
    public void draw(Canvas c, MapView osmv, boolean shadow) {
        super.draw(c, osmv, shadow);
        boundToHotspot(marker, HotspotPlace.BOTTOM_CENTER);
    }
}
