package net.osmosa.viewer;

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.XYTileSource;

public class Constant {
    public static final OnlineTileSourceBase OSMOSA_TILE = new XYTileSource("OSMOSA-ID", null, 3, 18, 256, ".png",
                    "http://www.osmosa.net/osm_tiles3/");
}
