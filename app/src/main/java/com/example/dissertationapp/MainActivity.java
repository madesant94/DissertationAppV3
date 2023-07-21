package com.example.dissertationapp;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;
import static android.os.Build.VERSION.SDK_INT;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.text.HtmlCompat;

import com.example.dissertationapp.R.color;
import com.example.dissertationapp.R.drawable;
import com.example.dissertationapp.R.id;
import com.example.dissertationapp.R.string;
import com.example.dissertationapp.databinding.ActivityMainBinding;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.Place.Field;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.snackbar.SnackbarContentLayout;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.jetbrains.annotations.NotNull;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;



public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {



    private GoogleMap map;
    Circle circleInit;
    Circle circleEnd;
    private Marker targetMarker;
    private Marker startMarker;

    private float targetDistanceValue;
    private Marker markerShortest;
    private Marker markerCleanest;
    private Boolean pollutionState = false;

    float ZOOM = 13;

    Map<String, List<Polygon>> polygonListsMap = new HashMap<>();

    private Boolean poly1Show = false;
    private Boolean poly2Show = false;

    private Boolean switchFlag = false;

    private Polyline polyline1;
    private Polyline polyline2;
    private ActivityMainBinding binding;

    private HeatmapTileProvider provider;
    private TileOverlay overlay;
    List<tile> tilesList = new ArrayList<>();
    boolean routeType = false;

    HashMap<String, node> nodesHashMap;
    HashMap<String, node> nodesHashMap_p;
    HashMap<String, node> nodesHashMap_b;
    HashMap<String, node> nodesHashMap_b_p;

    List<edge> edgesList = new ArrayList<>();
    List<edge> edgesList_p = new ArrayList<>();
    List<edge> edgesList_b = new ArrayList<>();
    List<edge> edgesList_b_p = new ArrayList<>();

    String latLngStr = "";
    double targetLat = 0;
    double targetLong = 0;

    double sourceLat = 0;
    double sourceLong = 0;
    String startText = "Your Location";
    String targetText = "";

    //DirectedWeightedMultigraph<String, edge> graph = new DirectedWeightedMultigraph<>(edge.class);
    SimpleWeightedGraph<String, edge> graph = new SimpleWeightedGraph<>(edge.class);

    DirectedWeightedMultigraph<String, edge> graph_p = new DirectedWeightedMultigraph<>(edge.class);
    DirectedWeightedMultigraph<String, edge> graph_b = new DirectedWeightedMultigraph<>(edge.class);
    DirectedWeightedMultigraph<String, edge> graph_b_p = new DirectedWeightedMultigraph<>(edge.class);

    private final int REQUEST_LATEST_LOCATION_PERMISSION = 101;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private boolean locationRequestFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //--------------------------------------------------------------------

        //--------------------------------------------------------------------

        //nodesList = CSVLoader.loadNodes(this, "nodes-in-grid.csv");
        nodesHashMap = CSVLoader.loadNodes(this, "nodes-in-grid.csv");
        edgesList = CSVLoader.loadEdges(this, "square-grid-edges.csv");
        tilesList = CSVLoader.loadTiles(this, "grid-geom-morning.csv");
        //tilesList = CSVLoader.loadTiles(this, "grid-geom-manual-horizontal.csv");
        //tilesList = CSVLoader.loadTiles(this, "grid-geom-manual-square.csv");
        // CREATE GRAPH WALKING
        for (node node : nodesHashMap.values()){
            graph.addVertex(node.getID());
        }

        for (edge edge : edgesList) {
            graph.addEdge(edge.getSource(), edge.getTarget(), edge);
        }

        int edgeCount = graph.edgeSet().size();

        Log.i("Graph", "The graph has " + edgeCount + " edges.");

        Utilities.setWeightsNodes(nodesHashMap, tilesList);
        Utilities.setWeightsEdges(edgesList, nodesHashMap, "Pollution");

        // ----------------------- WALKING PRUNED ------------------------

        nodesHashMap_p = CSVLoader.loadNodes(this, "nodes-in-grid-pruned.csv");
        edgesList_p = CSVLoader.loadEdges(this, "square-grid-edges-pruned.csv");

        for (node node : nodesHashMap_p.values()){
            graph_p.addVertex(node.getID());
        }

        for (edge edge : edgesList_p) {
            graph_p.addEdge(edge.getSource(), edge.getTarget(), edge);
        }

        edgeCount = graph_p.edgeSet().size();

        Log.i("Graph P", "The graph has " + edgeCount + " edges.");

        Utilities.setWeightsNodes(nodesHashMap_p, tilesList);
        Utilities.setWeightsEdges(edgesList_p, nodesHashMap_p, "Pollution");

        // END GRAPHS WALKING
        // ---------------------------------------------------
        // CREATE GRAPH BIKING

        nodesHashMap_b = CSVLoader.loadNodes(this, "nodes-in-grid-bike.csv");
        edgesList_b = CSVLoader.loadEdgesBike(this, "square-grid-edges-bike.csv",true);

        //for (node node : nodesList) {
        for (node node : nodesHashMap_b.values()){
            graph_b.addVertex(node.getID());
        }

        for (edge edge : edgesList_b) {
            graph_b.addEdge(edge.getSource(), edge.getTarget(), edge);
        }

        int edgeCount_b = graph_b.edgeSet().size();

        Log.i("Graph Bike", "The graph has " + edgeCount_b + " edges.");

        Utilities.setWeightsNodes(nodesHashMap_b, tilesList);
        Utilities.setWeightsEdges(edgesList_b, nodesHashMap_b,"Pollution");

        // ---------------------------------------------------
        // CREATE GRAPH BIKING PRUNED

        nodesHashMap_b_p = CSVLoader.loadNodes(this, "nodes-in-grid-bike-pruned.csv");
        edgesList_b_p = CSVLoader.loadEdgesBike(this, "square-grid-edges-bike-pruned.csv", true);

        //for (node node : nodesList) {
        for (node node : nodesHashMap_b_p.values()){
            graph_b_p.addVertex(node.getID());
        }

        for (edge edge : edgesList_b_p) {
            graph_b_p.addEdge(edge.getSource(), edge.getTarget(), edge);
        }

        int edgeCount_b_p = graph_b_p.edgeSet().size();

        Log.i("Graph Bike", "The graph has " + edgeCount_b_p + " edges.");

        Utilities.setWeightsNodes(nodesHashMap_b_p, tilesList);
        Utilities.setWeightsEdges(edgesList_b_p, nodesHashMap_b_p,"Pollution");


        // END GRAPH BIKE
        // ---------------------------------------------------

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Toolbar toolbar = binding.toolbar;
        //setSupportActionBar(toolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //  ---- Initialize Buttons and Widgets ---
        final ImageButton arrow = binding.arrowButton;
        final ImageButton exchangeButton = binding.exchangeFields;
        final LinearLayout hiddenView = binding.hiddenView;
        final ImageButton cyclingButton = binding.buttonCycle;
        final ImageButton walkingButton = binding.buttonWalk;
        final Button submitButton = binding.btnSubmit;
        final Button showPollutionButton = binding.btnShowPollution;
        final Button updatePollutionButton = binding.btnGetPollution;

        final Switch circuitSwitch = binding.switch1;
        final Slider targetDistanceSlider = binding.distanceTargetSlider;

        final LinearLayout distanceTargetLayout = binding.distanceTargetView;
        final LinearLayout fragmentAutocomplete = binding.autocompleteFragmentLayout;

        TextView autoTextViewStart = binding.autoTextViewStart;
        TextView autoTextView = binding.autoTextView;

        String apiKey = this.getString(string.api_key); //Placeholder

        walkingButton.setColorFilter(this.getColor(color.white));

        if (!Places.isInitialized()) {
            Places.initialize(this.getApplicationContext(), apiKey);
        }

        AutocompleteSupportFragment autocompleteSupportFragment1 = (AutocompleteSupportFragment) this.getSupportFragmentManager().findFragmentById(id.autocomplete_fragment);
        Intrinsics.checkNotNull(autocompleteSupportFragment1);
        autocompleteSupportFragment1.setPlaceFields(CollectionsKt.listOf(new Place.Field[]{Field.NAME, Field.ADDRESS, Field.LAT_LNG}));
        autocompleteSupportFragment1.setOnPlaceSelectedListener((PlaceSelectionListener) (new PlaceSelectionListener() {
            public void onPlaceSelected(@NotNull Place place) {
                Intrinsics.checkNotNullParameter(place, "place");

                TextView textView = binding.autoTextView;
                String namePlace = place.getName();
                LatLng latLng = place.getLatLng();

                targetText = namePlace;
                textView.setText("To: " + namePlace);

                clearMarker();

                targetLat = latLng.latitude;
                targetLong = latLng.longitude;

                targetMarker = map.addMarker((new MarkerOptions())
                        .position(latLng)
                        .title("Destination"));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0F));

            }
            public void onError(@NotNull Status status) {
                Intrinsics.checkNotNullParameter(status, "status");
                Toast.makeText(MainActivity.this.getApplicationContext(), "No Location selected", Toast.LENGTH_SHORT).show();
            }
        }));
        autoTextViewStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set the fields to specify which types of place data to return.
                List<Place.Field> fields = Arrays.asList(Field.NAME,  Field.ADDRESS, Field.LAT_LNG);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(MainActivity.this);

                startAutocomplete.launch(intent);
                //startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        autoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set the fields to specify which types of place data to return.
                List<Place.Field> fields = Arrays.asList(Field.NAME,  Field.ADDRESS, Field.LAT_LNG);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(MainActivity.this);

                targetAutocomplete.launch(intent);
                //startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        arrow.setOnClickListener((View.OnClickListener) (new View.OnClickListener() {
            public void onClick(View it) {
                if (hiddenView.getVisibility() == VISIBLE) {
                    fragmentAutocomplete.setVisibility(VISIBLE);
                    hiddenView.setVisibility(View.GONE);
                    arrow.setImageResource(drawable.baseline_expand_more_24);
                } else {
                    fragmentAutocomplete.setVisibility(View.GONE);
                    hiddenView.setVisibility(VISIBLE);
                    arrow.setImageResource(drawable.baseline_expand_less_24);
                }

            }
        }));

        exchangeButton.setOnClickListener((View.OnClickListener) (new View.OnClickListener() {
            public void onClick(View it) {
                TextView textViewStart = binding.autoTextViewStart;
                TextView textViewDest = binding.autoTextView;

                String interString = startText;

                startText = targetText;
                targetText = interString;

                textViewStart.setText("From: " + startText);
                textViewDest.setText("To: "+ targetText);

                Log.i("exchange", "clicked");

                double interLat = sourceLat;
                double interLong = sourceLong;

                sourceLat = targetLat;
                sourceLong = targetLong;

                targetLat = interLat;
                targetLong = interLong;

                if (targetMarker != null) {
                    targetMarker.remove();
                }

                if (startMarker != null) {
                    startMarker.remove();
                }

                float markerColor = BitmapDescriptorFactory.HUE_BLUE;
                startMarker = map.addMarker((new MarkerOptions()).position(new LatLng(sourceLat, sourceLong))
                        .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                        .title(MainActivity.this.getString(string.startPoint)));

                targetMarker = map.addMarker((new MarkerOptions()).position(new LatLng(targetLat, targetLong))
                        .title(MainActivity.this.getString(string.dropped_pin)));

            }
        }));

        walkingButton.setOnClickListener((View.OnClickListener) (new View.OnClickListener() {
            public void onClick(View it) {
                routeType = false;
                cyclingButton.setBackgroundResource(drawable.round_corner_un);
                cyclingButton.setColorFilter(MainActivity.this.getColor(androidx.appcompat.R.color.material_blue_grey_800));
                walkingButton.setBackgroundResource(drawable.round_corner);
                walkingButton.setColorFilter(MainActivity.this.getColor(color.white));
            }
        }));
        cyclingButton.setOnClickListener((View.OnClickListener) (new View.OnClickListener() {
            public void onClick(View it) {
                routeType = true;
                walkingButton.setBackgroundResource(drawable.round_corner_un);
                walkingButton.setColorFilter(MainActivity.this.getColor(androidx.appcompat.R.color.material_blue_grey_800));
                cyclingButton.setBackgroundResource(drawable.round_corner);
                cyclingButton.setColorFilter(MainActivity.this.getColor(color.white));
            }
        }));

        circuitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Code to be executed when the switch is toggled
                if (isChecked) {
                    switchFlag = true;
                    distanceTargetLayout.setVisibility(VISIBLE);

                } else {
                    switchFlag = false;
                    distanceTargetLayout.setVisibility(View.GONE);

                }
            }
        });

        showPollutionButton.setOnClickListener((View.OnClickListener) (new View.OnClickListener() {
            public void onClick(View it) {
                pollutionState = !pollutionState;

                if (pollutionState){
                    createPolygons(tilesList);
                    //createHeatmap(nodesHashMap);
                    showPollutionButton.setText(string.hide_pollution);

                } else{
                    removePolygons("polys");
                    //overlay.remove();
                    showPollutionButton.setText(string.show_pollution);

                }

            }
        }));

        targetDistanceSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener()  {
            /*@Override
            public void onValueChange(@NonNull Slider targetDistanceSlider, float value, boolean fromUser) {
                //Use the value
                //targetDistanceValue = value;
                //TextView autoDistanceTarget = binding.autoDistanceTarget;
                //autoDistanceTarget.setText("Target Distance (km): " + String.valueOf(value));

                Log.i("Testing on value change",String.valueOf(value));
            }*/

            @Override
            public void onStartTrackingTouch(@NonNull Slider targetDistanceSlider) {
                // This method is called when the user starts moving the slider
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider targetDistanceSlider) {
                // This method is called when the user stops moving the slider
                // The final value of the slider is in slider.getValue()
                float finalValue = targetDistanceSlider.getValue();
                // Now you can use the final value
                targetDistanceValue = finalValue;
                TextView autoDistanceTarget = binding.autoDistanceTarget;
                autoDistanceTarget.setText("Target Distance (km): " + String.valueOf(finalValue));

                Log.i("Testing on value change",String.valueOf(finalValue));
            }

            /*public void onStopTrackingTouch(@NonNull Slider targetDistanceSlider) {
                // This method is called when the user stops moving the slider
                // The final value of the slider is in seekBar.getProgress()
                float finalValue = targetDistanceSlider.getValue();
                // Now you can use the final value
                targetDistanceValue = finalValue;
                TextView autoDistanceTarget = binding.autoDistanceTarget;
                autoDistanceTarget.setText("Target Distance (km): " + String.valueOf(finalValue));

                Log.i("Testing on value change",String.valueOf(finalValue));
            }*/

        });



        submitButton.setOnClickListener((View.OnClickListener) (new View.OnClickListener() {
            public void onClick(View it) {


                //LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location sourceLocation = retrieveLocation();

                //TextView autoTextViewrc = binding.autoTextViewStart;
                //TextView autoTextViewStart = binding.autoTextViewStart;
                Log.i("startText", startText);
                Log.i("targetText", targetText);

                boolean runFlag = true;

                if (startText.equals("Your Location") || targetText.equals("To: Your Location")) {

                    if (locationRequestFlag) {
                        sourceLat = sourceLocation.getLatitude();
                        sourceLong = sourceLocation.getLongitude();

                        Log.i("Enable My Location Test src", String.valueOf(sourceLat));
                        Log.i("Enable My Location Test tar", String.valueOf(sourceLong));
                    } else {

                        Snackbar snackbar = Snackbar
                                .make(findViewById(id.base_cardview),
                                        "Error! You did not grant location permission. Please specify start and destination points Or change permissions in phone -> Settings.",
                                        Snackbar.LENGTH_LONG);

                        View view = snackbar.getView();
                        TextView tv = (TextView) view.findViewById(R.id.snackbar_text);

                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)tv.getLayoutParams();
                        params.height = 200;
                        tv.setTextColor(Color.WHITE);
                        tv.setText("Error! You did not grant location permission.\n Please specify start and destination points.");
                        tv.setGravity(Gravity.CENTER_HORIZONTAL);
                        tv.setLayoutParams(params);


                        snackbar.show();
                        //Toast.makeText(MainActivity.this, "Error! You did not grant location permission. Please specify start and destination points or change permissions in phone -> Settings.", Toast.LENGTH_LONG).show();
                        runFlag = false;
                    }
                }

                if (sourceLat == 0){
                    runFlag = false;
                    Snackbar snackbar = Snackbar
                            .make(findViewById(id.base_cardview),
                                    "Error! Please specify Starting point.",
                                    Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

                if (targetLat == 0){
                    runFlag = false;
                    Snackbar snackbar = Snackbar
                            .make(findViewById(id.base_cardview),
                                    "Error! Please specify Destination point.",
                                    Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

                if (runFlag) {

                    node sourceNearestNode = null;
                    node targetNearestNode = null;

                    if (switchFlag) {

                        float targetDistance = 5000;

                        if (polyline2 != null) {
                            polyline2.remove();
                        }

                        if (routeType) { // Bike Network

                            sourceNearestNode = Utilities.findNearestNode(nodesHashMap_b_p, sourceLat, sourceLong);
                            targetNearestNode = Utilities.findNearestNode(nodesHashMap_b_p, targetLat, targetLong);

                            polyline1 = runBestNearestNeighbor(graph_b_p, sourceNearestNode, targetNearestNode,
                                    nodesHashMap_b_p, "Bike", targetDistanceValue * 1000,
                                    polyline1, edgesList_b_p);
                        } else { // Walk Network

                            sourceNearestNode = Utilities.findNearestNode(nodesHashMap_p, sourceLat, sourceLong);
                            targetNearestNode = Utilities.findNearestNode(nodesHashMap_p, targetLat, targetLong);

                            polyline1 = runBestNearestNeighbor(graph_p, sourceNearestNode, targetNearestNode, nodesHashMap_p, "Walk", targetDistanceValue * 1000, polyline1, edgesList_p);
                        }
                    } else {
                        if (routeType) { //Bike
                            // Graph
                            Log.i("Route Type", "will Run Dijkstra for Bike");

                            sourceNearestNode = Utilities.findNearestNode(nodesHashMap_b, sourceLat, sourceLong);
                            targetNearestNode = Utilities.findNearestNode(nodesHashMap_b, targetLat, targetLong);
                            Log.i("Node Src", String.valueOf(sourceNearestNode.getID()));
                            Log.i("Node Tar", String.valueOf(targetNearestNode.getID()));

                            Utilities.setWeightsEdgesBeta(edgesList_b, nodesHashMap_b, "WD", 0f);
                            for (edge edge : edgesList_b) {
                                graph_b.setEdgeWeight(graph_b.getEdge(edge.getSource(), edge.getTarget()), edge.getPollution());
                            }

                            polyline2 = runDijkstra(graph_b, sourceNearestNode, targetNearestNode, nodesHashMap_b, "Length", "Bike", polyline2);

                            Utilities.setWeightsEdgesBeta(edgesList_b, nodesHashMap_b, "WD", 1f);
                            for (edge edge : edgesList_b) {
                                graph_b.setEdgeWeight(graph_b.getEdge(edge.getSource(), edge.getTarget()), edge.getPollution());
                            }

                            polyline1 = runDijkstra(graph_b, sourceNearestNode, targetNearestNode, nodesHashMap_b, "Pollution", "Bike", polyline1);

                        } else { //Walk
                            Log.i("Route Type", "will Run Dijkstra for Walk");

                            sourceNearestNode = Utilities.findNearestNode(nodesHashMap, sourceLat, sourceLong);
                            targetNearestNode = Utilities.findNearestNode(nodesHashMap, targetLat, targetLong);


                            Log.i("Node Src", String.valueOf(sourceNearestNode.getID()));
                            Log.i("Node Tar", String.valueOf(targetNearestNode.getID()));

                            Utilities.setWeightsEdgesBeta(edgesList, nodesHashMap, "WD", 0f);
                            for (edge edge : edgesList) {
                                graph.setEdgeWeight(graph.getEdge(edge.getSource(), edge.getTarget()), edge.getPollution());
                            }

                            polyline2 = runDijkstra(graph, sourceNearestNode, targetNearestNode, nodesHashMap, "Length", "Walk", polyline2);

                            Utilities.setWeightsEdgesBeta(edgesList, nodesHashMap, "WD", 1f);
                            for (edge edge : edgesList) {
                                graph.setEdgeWeight(graph.getEdge(edge.getSource(), edge.getTarget()), edge.getPollution());
                            }

                            polyline1 = runDijkstra(graph, sourceNearestNode, targetNearestNode, nodesHashMap, "Pollution", "Walk", polyline1);


                            //polyline2 = runDijkstra(graph, sourceNearestNode, targetNearestNode, nodesHashMap, "Length", "Walk", polyline2);
                            //polyline1 = runDijkstra(graph, sourceNearestNode, targetNearestNode, nodesHashMap, "Pollution", "Walk", polyline1);

                        }
                    }

                    if (polyline1 != null) {

                        LatLng startPoint = polyline1.getPoints().get(0);//.size();
                        LatLng endPoint = polyline1.getPoints().get(polyline1.getPoints().size() - 1);

                        if (startMarker != null) {
                            startMarker.remove();
                        }
                        if (targetMarker != null) {
                            targetMarker.remove();
                        }

                        float markerColor = BitmapDescriptorFactory.HUE_BLUE;
                        startMarker = map.addMarker((new MarkerOptions()).position(startPoint)
                                .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                                .title(MainActivity.this.getString(string.startPoint)));

                        targetMarker = map.addMarker((new MarkerOptions()).position(endPoint)
                                .title(MainActivity.this.getString(string.dropped_pin)));
                        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(infoLatLng, 14.0f));
                    }
                }
            }
        }));

        updatePollutionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //imp step
                if(SDK_INT >= Build.VERSION_CODES.R)
                {
                    if(Environment.isExternalStorageManager()){
                        //choosing csv file
                        Intent intent=new Intent();
                        intent.setType("*/*");
                        intent.putExtra(Intent.EXTRA_AUTO_LAUNCH_SINGLE_CHOICE,true);
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent,"Select CSV File "),101);
                    }
                    else{
                        //getting permission from user
                        Intent intent=new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        Uri uri=Uri.fromParts("package",getPackageName(),null);
                        startActivity(intent);
                    }
                }
                else{
                    // for below android 11

                    Intent intent=new Intent();
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_AUTO_LAUNCH_SINGLE_CHOICE,true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    ActivityCompat.requestPermissions(MainActivity.this,new String[] {WRITE_EXTERNAL_STORAGE},102);


                }
            }
        });

    }

    Uri fileURI; // Define URI for reading external files *PLACEHOLDER*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101 && data!=null){

            fileURI = data.getData();

            tilesList = openExternalCSVFile(fileURI);

            //Test for regular Walk Graph
            Utilities.setWeightsNodes(nodesHashMap, tilesList);
            Utilities.setWeightsEdges(edgesList, nodesHashMap,"Pollution");



            Log.i("External File Intent", String.valueOf(tilesList.get(0).getID()));

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
        //return(super.onCreateOptionsMenu(menu));
    }

    // Test for autocomplete textview
    private final ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Place place = Autocomplete.getPlaceFromIntent(intent);
                        Log.i(TAG, "Place: ${place.getName()}, ${place.getId()}");

                        TextView textView = binding.autoTextViewStart;
                        String namePlace = place.getName();
                        LatLng latLng = place.getLatLng();

                        textView.setText("From: " + namePlace);
                        startText = namePlace;

                        clearMarker();
                        sourceLat = latLng.latitude;
                        sourceLong = latLng.longitude;

                        if (startMarker != null) {
                            startMarker.remove();
                        }

                        MainActivity.this.startMarker = map.addMarker((new MarkerOptions())
                                .position(latLng)
                                .title("Start"));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0F));
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    Log.i(TAG, "User canceled autocomplete");
                }
            });

    private final ActivityResultLauncher<Intent> targetAutocomplete = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Place place = Autocomplete.getPlaceFromIntent(intent);
                        Log.i(TAG, "Place: ${place.getName()}, ${place.getId()}");

                        TextView textView = binding.autoTextView;
                        String namePlace = place.getName();
                        LatLng latLng = place.getLatLng();

                        textView.setText("To: " + namePlace);
                        targetText = namePlace;

                        clearMarker();
                        targetLat = latLng.latitude;
                        targetLong = latLng.longitude;

                        if (targetMarker != null) {
                            targetMarker.remove();
                        }

                        MainActivity.this.targetMarker = map.addMarker((new MarkerOptions())
                                .position(latLng)
                                .title("Destination"));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0F));
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    Log.i(TAG, "User canceled autocomplete");
                }
            });

    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        Intrinsics.checkNotNullParameter(item, "item");
        int var2 = item.getItemId();
        GoogleMap var10000;
        boolean var3;
        if (var2 == id.normal_map) {
            var10000 = this.map;
            if (var10000 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("map");
            }

            var10000.setMapType(1);
            var3 = true;
        } else if (var2 == id.hybrid_map) {
            var10000 = this.map;
            if (var10000 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("map");
            }

            var10000.setMapType(4);
            var3 = true;
        } else if (var2 == id.satellite_map) {
            var10000 = this.map;
            if (var10000 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("map");
            }

            var10000.setMapType(2);
            var3 = true;
        } else if (var2 == id.terrain_map) {
            var10000 = this.map;
            if (var10000 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("map");
            }

            var10000.setMapType(3);
            var3 = true;
        } else {
            var3 = super.onOptionsItemSelected(item);
        }
        return var3;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e("Map rendering", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("Map rendering", "Can't find style. Error: ", e);
        }

        ArrayList<WeightedLatLng> latLngsW = new ArrayList<WeightedLatLng>();

        latLngsW.add(new WeightedLatLng(new LatLng(51.496715, -0.1763672), 5));

        provider = new HeatmapTileProvider.Builder()
                .weightedData(latLngsW)
                .maxIntensity(35.0)
                .build();

        overlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(provider));

        LatLng london = new LatLng(51.496715, -0.1763672);
        //float zoomLevel = 15.0F;
        float zoomLevel = 17.0F;
        //(-0.1465312673602718 51.51569143211838,
        // -0.2043147326397282 51.51569143211838,
        // -0.2043147326397282 51.479718567881626,
        // -0.1465312673602718 51.479718567881626,
        // -0.1465312673602718 51.51569143211838)

        // 51.52918125620716, -0.1248624678804757
        // 51.52918125620716, -0.2259835321195243
        // 51.46622874379285, -0.2259835321195243
        // 51.46622874379285, -0.1248624678804757
        // 51.52918125620716 -0.1248624678804757
        Polygon polygonInit = map.addPolygon(new PolygonOptions()
                .add(
                        new LatLng(51.52918125620716, -0.1248624678804757 ),
                        new LatLng(51.52918125620716, -0.2259835321195243),
                        new LatLng( 51.46622874379285, -0.2259835321195243 ),
                        new LatLng(  51.46622874379285, -0.1248624678804757)
                ));

        //polygonInit.setFillColor(0x7Fe5e5e5);
        polygonInit.setStrokeColor(ContextCompat.getColor(getApplicationContext(),color.muted_blue));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(london, zoomLevel));
        setMapLongClick(map);
        //setOnPolylineClickListener(map);
        map.setBuildingsEnabled(true);
        enableMyLocation();
    }

    private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission((Context)this, "android.permission.ACCESS_FINE_LOCATION") == 0;
    }

    private void enableMyLocation() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, do your location-related task
                locationRequestFlag = true;
            } else {
                // Permission denied, handle accordingly
                locationRequestFlag = false;
            }
        }
    }

    public void onRequestPermissionsResultLocation(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == this.REQUEST_LATEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                retrieveLocation();
            } else {
                Log.e("MainActivity", "Permission denied!");
            }
        }
    }

    private Location retrieveLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = null;
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission Not Granted", "User did not grant Location Permission");
                return null;
            }
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Log.d("Location", "Latitude: " + latitude + ", Longitude: " + longitude);
            } else {
                Log.e("MainActivity", "Location not available!");
            }
        }
        return location;
    }
    // ----------------------
    private void setMapOnZoom(final GoogleMap map, HeatmapTileProvider provider) {
        map.setOnCameraIdleListener((GoogleMap.OnCameraIdleListener)(new GoogleMap.OnCameraIdleListener(){
            public void onCameraIdle() {
                float newZoom = map.getCameraPosition().zoom;

                if ((newZoom >= 15) & (newZoom != ZOOM)) {

                    int radius = 30;

                    if (newZoom > 15) {
                        radius = 40;
                    }
                    if (newZoom >= 17) {

                        radius = 120;
                    }

                    ZOOM = newZoom;

                    Log.i("Zoom", String.valueOf(newZoom));

                    provider.setRadius(radius);
                    //overlay.clearTileCache();
                }
            }
        }));
    }

    private void setMapLongClick(final GoogleMap map) {
        map.setOnMapLongClickListener((GoogleMap.OnMapLongClickListener)(new GoogleMap.OnMapLongClickListener() {
            public void onMapLongClick(@NotNull LatLng latLng) {
                TextView textViewTarget = binding.autoTextView;

                DecimalFormat dfZero = new DecimalFormat("0.0000");
                double Lat = latLng.latitude;
                double Long = latLng.longitude;
                /*Intrinsics.checkNotNullParameter(latLng, "latLng");

                TextView textView = binding.autoTextView;
                Intrinsics.checkNotNullExpressionValue(textView, "binding.autoTextView");
                //StringCompanionObject var4 = StringCompanionObject.INSTANCE;
                Locale var5 = Locale.getDefault();
                String var6 = "Lat: %1$.5f, Long: %2$.5f";
                Object[] var7 = new Object[]{latLng.latitude, latLng.longitude};
                String var9 = String.format(var5, var6, Arrays.copyOf(var7, var7.length));
                Intrinsics.checkNotNullExpressionValue(var9, "format(locale, format, *args)");
                String snippet = var9;
                if (MainActivity.this.targetMarker != null) {
                    targetMarker.remove();
                }
                DecimalFormat dfZero = new DecimalFormat("0.0000");

                targetLat = latLng.latitude;
                targetLong = latLng.longitude;

                targetMarker = map.addMarker((new MarkerOptions()).position(latLng)
                        .title(MainActivity.this.getString(string.dropped_pin))
                        .snippet(snippet));
                //.icon(BitmapDescriptorFactory.defaultMarker(hsv[0])));

                targetText = "(" + String.valueOf(dfZero.format(targetLat)) + " , " + String.valueOf(dfZero.format(targetLong)) + ")";
                textView.setText( "To: " +targetText);*/
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Set Location as...")
                        .setItems(new CharSequence[]{"Starting Point", "Destination"}, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position of the selected item
                                float markerColor;
                                boolean newLocation = false;
                                switch(which){
                                    case 0: //Red was chosen
                                        newLocation = true;
                                        markerColor = BitmapDescriptorFactory.HUE_BLUE;
                                        break;
                                    case 1: //Blue was chosen
                                        newLocation = false;
                                        markerColor = BitmapDescriptorFactory.HUE_RED;
                                        break;
                                    default: //Default to red if something goes wrong
                                        markerColor = BitmapDescriptorFactory.HUE_RED;
                                        break;
                                }
                                // Add a new marker to the map with the chosen color
                                if (newLocation) {
                                    if (MainActivity.this.startMarker != null) {

                                        startMarker.remove();
                                    }

                                    sourceLat = Lat;
                                    sourceLong = Long;

                                    TextView textViewStart = binding.autoTextViewStart;
                                    startMarker = map.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));

                                    startText = "(" + String.valueOf(dfZero.format(Lat)) + " , " + String.valueOf(dfZero.format(Long)) + ")";
                                    textViewStart.setText( "From: " +startText);
                                }
                                else{

                                    if (MainActivity.this.targetMarker != null) {

                                        targetMarker.remove();
                                    }

                                    targetLat = Lat;
                                    targetLong = Long;

                                    targetMarker = map.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));

                                    targetText = "(" + String.valueOf(dfZero.format(Lat)) + " , " + String.valueOf(dfZero.format(Long)) + ")";
                                    textViewTarget.setText( "To: " +targetText);

                                }
                            }
                        });
                builder.show();



            }
        }));
    }

    private void setOnPolylineClickListener(final GoogleMap map) {

        map.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline clickedPolyline) {
                if (clickedPolyline.equals(polyline1)) {  // Check if the clicked polyline is the one we're interested in
                    // Do something here
                    if (poly1Show){
                        markerCleanest.setVisible(true);
                        //markerCleanest.isNotVisible();
                    }else{
                        markerCleanest.setVisible(false);
                    }
                    poly1Show =! poly1Show;
                }
                if (clickedPolyline.equals(polyline2)) {
                    if (poly2Show){
                        markerShortest.setVisible(true);
                        //markerCleanest.isNotVisible();
                    }else{
                        markerShortest.setVisible(false);
                    }
                    poly2Show =! poly2Show;
                }
            }
        });
    }

    public Polyline runDijkstra(Graph<String, edge> graph, node sourceNearestNode, node targetNearestNode,

                                HashMap<String, node> nodesHashMap, String Type, String Route , Polyline polyline){

        if (polyline != null) {
            polyline.remove();
        }

        DecimalFormat dfZero = new DecimalFormat("0.00");

        long t1 = System.currentTimeMillis();

        String startVertex = sourceNearestNode.getID();
        Map<String, Double> cleanestPollution = new HashMap<>();
        Map<String, Double> cleanestLength = new HashMap<>();

        List<String> Path;
        double PathPollution;
        double PathLength;

        if (Route.equals("Bike")){


            /*Map<String, String> previousVertices = new HashMap<>();

            Algorithms.dijkstra(graph, sourceNearestNode.getID(), cleanestPollution, cleanestLength,
                    previousVertices, Type);

            // Custom Method
            Path = Algorithms.getShortestPath(startVertex, targetNearestNode.getID(), previousVertices);
            PathPollution = cleanestPollution.get(targetNearestNode.getID());
            PathLength = cleanestLength.get(targetNearestNode.getID());
            long t2 = System.currentTimeMillis();
            System.out.println("dijkstraV1 Bike.getPath           " + Path);
            System.out.println("DijsktraV1Bike Running Time (ms): "+ (t2-t1));
*/
            // ------- INIT Complete JGRAPHT METHOD -------
            long t2 = System.currentTimeMillis();
            DijkstraShortestPath<String, edge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
            GraphPath<String, edge> dijkstraPath = dijkstraShortestPath.getPath(sourceNearestNode.getID(), targetNearestNode.getID());
            // POLLUTION == WEIGHT (WHICH CAN BE DISTANCE)
            // GRADE == POLLUTION
            List<edge> edgeList = dijkstraPath.getEdgeList();
            PathLength = 0;
            PathPollution = 0;
            for (edge edge : edgeList) {
                //PathPollution = PathPollution + (edge.getGrade()/edge.getLength());
                PathPollution = PathPollution + edge.getGrade();
                PathLength =  PathLength + edge.getLength();
            }
           // PathPollution = PathPollution/edgeList.size();

            Path = dijkstraPath.getVertexList();
            long t3 = System.currentTimeMillis();
            //System.out.println("DijkstraV Inner Method Bike.getPath           " + Path);
            System.out.println("Dijkstra Bike Running Time (ms): "+ (t3-t2));


            // ------- END JGRAPHT METHOD -------

        }
        else{

            /*long t2 = System.currentTimeMillis();
            DijkstraAlgorithm dijkstra = new DijkstraAlgorithm();
            for (edge edge : edgesList) {
                dijkstra.addEdge(edge.getSource(), edge.getTarget(), edge);
                                    }
            dijkstra.execute(sourceNearestNode.getID(), Type);


            Path = dijkstra.getPath(targetNearestNode.getID());
            long t3 = System.currentTimeMillis();
            PathPollution = dijkstra.getShortestMain(targetNearestNode.getID());
            PathLength = dijkstra.getShortestSecond(targetNearestNode.getID());
            System.out.println("Dijkstra Walk Running Time (ms): "+ (t3-t2));
            //System.out.println("dijkstraV2.getPath Walk           " + Path);
            //System.out.println("DijsktraV2 Walk Running Time (ms): "+ (t2-t1));*/

            // ------- INIT Complete JGRAPHT METHOD -------
            long t2 = System.currentTimeMillis();
            DijkstraShortestPath<String, edge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
            GraphPath<String, edge> dijkstraPath = dijkstraShortestPath.getPath(sourceNearestNode.getID(), targetNearestNode.getID());
            // POLLUTION == WEIGHT (WHICH CAN BE DISTANCE)
            // GRADE == POLLUTION
            List<edge> edgeList = dijkstraPath.getEdgeList();
            PathLength = 0;
            PathPollution = 0;
            for (edge edge : edgeList) {
                //PathPollution = PathPollution + (edge.getGrade()/edge.getLength());
                PathPollution = PathPollution + edge.getGrade();
                PathLength =  PathLength + edge.getLength();
            }
            // PathPollution = PathPollution/edgeList.size();

            Path = dijkstraPath.getVertexList();
            long t3 = System.currentTimeMillis();
            //System.out.println("DijkstraV Inner Method Bike.getPath           " + Path);
            System.out.println("Dijkstra Bike Running Time (ms): "+ (t3-t2));


            // ------- END JGRAPHT METHOD -------

        }

        node newNode = null;
        List<LatLng> polylines = new ArrayList<>();

        for (String node: Path){
            //newNode = findNode(nodesList, node);
            newNode = nodesHashMap.get(node);
            polylines.add(new LatLng(newNode.getLongitude(), newNode.getLatitude()));
        }

        List<PatternItem> pattern = Arrays.asList(new Dot(), new Gap(10));
        int polyColor = ContextCompat.getColor(getApplicationContext(),color.muted_blue);

        if (Type.equals("Length")){
            int alpha = 200; // This is an example alpha value.
            polyColor = ColorUtils.setAlphaComponent(ContextCompat.getColor(getApplicationContext(),color.gray_500), alpha);
        }
        if (Route.equals("Bike")){
            pattern = Collections.singletonList(new Dash(10));
        }

        polyline = map.addPolyline(new PolylineOptions().addAll(polylines)
                .pattern(pattern)
                .color(polyColor)
                .geodesic(true)
                .clickable(true)
                .width(15));

        if (Type.equals("Pollution")){

            LinearLayout box = binding.cleanestInfo;
            box.setVisibility(VISIBLE);
            TextView textView = binding.cleanestInfoText;
            // This version divides the pollution against the lenght
            //String sourceString = "<b>Cleanest</b><br> " + String.valueOf(dfZero.format(PathPollution/PathLength)) + " pm2.5/m </br><br> " + String.valueOf(dfZero.format(PathLength)) +" meters</b>";
            // This doesnt
            //String sourceString = "<b>Cleanest</b><br> " + String.valueOf(dfZero.format(PathPollution)) + " pm2.5/m </br><br> " + String.valueOf(dfZero.format(PathLength)) +" meters</b>";
            String sourceString = "<b>Cleanest</b> " + String.valueOf(dfZero.format(PathPollution)) + " pm2.5/m, " + String.valueOf(dfZero.format(PathLength)) +" meters";

            if (Route.equals("Bike")){
                sourceString = "<b>Shortest</b> " + String.valueOf(dfZero.format(PathPollution)) + " pm2.5/m, " + String.valueOf(dfZero.format(PathLength)) +" meters";
            }

            textView.setText(HtmlCompat.fromHtml(sourceString, HtmlCompat.FROM_HTML_MODE_LEGACY));
            //addInfoWindow(String title, String messagePollution, String messageLength, Polyline polyline1, Marker marker, Boolean shortest)
            //markerCleanest = addInfoWindow("Cleanest",String.valueOf(dfZero.format(PathPollution)),String.valueOf(dfZero.format(PathLength)), polyline, markerCleanest, false);

        }
        if (Type.equals("Length")){
            //LinearLayout box = binding.shortestInfo;
            //box.setVisibility(VISIBLE);
            TextView textView = binding.shortestInfoText;
            textView.setVisibility(VISIBLE);
            //String sourceString = "<b>Shortest</b><br> " + String.valueOf(dfZero.format(PathLength/PathPollution)) + " pm2.5/m </br><br> " + String.valueOf(dfZero.format(PathPollution)) +" meters</b>";
            //String sourceString = "<b>Shortest</b><br> " + String.valueOf(dfZero.format(PathLength)) + " pm2.5/m </br><br> " + String.valueOf(dfZero.format(PathPollution)) +" meters</b>";
            String sourceString = "<b>Shortest</b> " + String.valueOf(dfZero.format(PathLength)) + " pm2.5/m, " + String.valueOf(dfZero.format(PathPollution)) +" meters</b>";

            if (Route.equals("Bike")){
                //sourceString = "<b>Shortest</b><br> " + String.valueOf(dfZero.format(PathPollution)) + " pm2.5/m </br><br> " + String.valueOf(dfZero.format(PathLength)) +" meters</b>";
                sourceString = "<b>Shortest</b> " + String.valueOf(dfZero.format(PathPollution)) + " pm2.5/m, " + String.valueOf(dfZero.format(PathLength)) +" meters</b>";

            }
            textView.setText(HtmlCompat.fromHtml(sourceString, HtmlCompat.FROM_HTML_MODE_LEGACY));
            //addInfoWindowShortest("Cleanest",String.valueOf(dfZero.format(cleanestPathPollution)),String.valueOf(dfZero.format(cleanestPathLength)), polyline);
            //markerShortest = addInfoWindow("Shortest",String.valueOf(dfZero.format(PathLength)),String.valueOf(dfZero.format(PathPollution)), polyline, markerShortest, true);
        }
        clearMarker();

        return polyline;
    }

    public Polyline runBestNearestNeighbor(Graph<String, edge> graph, node sourceNearestNode, node targetNearestNode,

                                HashMap<String, node> nodesHashMap, String Route, float tarDistance, Polyline polyline, List<edge> edgesList) {

        if (polyline != null) {
            polyline.remove();
        }
        bnn BNN = null;
        //List<String> nnPath = Algorithms.bestNearestNeighbor(graph,  sourceNearestNode, targetNearestNode, tarDistance, nodesHashMap);
        if (Route.equals("Walk") ){
            BNN = Algorithms.bestNearestNeighbor(graph, sourceNearestNode, targetNearestNode, tarDistance, nodesHashMap, edgesList, "undirected");
        }else{
            BNN = Algorithms.bestNearestNeighbor(graph, sourceNearestNode, targetNearestNode, tarDistance, nodesHashMap, edgesList, "undirected");
        }
        List<LatLng> polylines = new ArrayList<>();
        node newNode = null;
        for (String node: BNN.getRoute()){
            //newNode = findNode(nodesList, node);
            newNode = nodesHashMap.get(node);
            polylines.add(new LatLng(newNode.getLongitude(), newNode.getLatitude()));
        }

        List<PatternItem> pattern = Arrays.asList(new Dot(), new Gap(10));
        int polyColor = ContextCompat.getColor(getApplicationContext(),color.muted_blue);

        if (Route.equals("Bike")){
            pattern = Collections.singletonList(new Dash(10));
        }

        polyline = map.addPolyline(new PolylineOptions().addAll(polylines)
                .pattern(pattern)
                .color(polyColor)
                .geodesic(true)
                .clickable(true)
                .width(15));

        //LinearLayout boxLength = binding.shortestInfo;
        //boxLength.setVisibility(View.GONE);

        DecimalFormat dfZero = new DecimalFormat("0.00");

        LinearLayout box = binding.cleanestInfo;
        box.setVisibility(VISIBLE);
        TextView textView = binding.cleanestInfoText;
        String sourceString = "<b>Circuit Info </b><br> " + String.valueOf(dfZero.format(BNN.getPollution())) + " pm2.5 </br><br> " + String.valueOf(dfZero.format(BNN.getLength())) +" meters</b>";
        textView.setText(HtmlCompat.fromHtml(sourceString, HtmlCompat.FROM_HTML_MODE_LEGACY));
        TextView textViewShortest = binding.shortestInfoText;
        textViewShortest.setVisibility(View.GONE);
        return polyline;
    }

    // ------------------------------------------------------------

    public void createCenterForHeatmap(List<tile> tilesList) {

        List<Float> valuesTiles = new ArrayList<>();

        for  (tile tile : tilesList) {
            valuesTiles.add(tile.getValue());
        }

        float maximum = Collections.max(valuesTiles);
        // Find the minimum value
        float minimum = Collections.min(valuesTiles);

        for  (tile tile : tilesList) {

            List<LatLng> polygonLatLng = new ArrayList<>();
            String polygon = tile.getGeometry();
            String[] parts = polygon.split("_");

            String[] latLngStr = parts[0].split(" ");

            polygonLatLng.add(new LatLng(Double.parseDouble(latLngStr[2]), Double.parseDouble(latLngStr[1])));

            //float value = Math.round(tile.getValue());

        }
    }


    public void createPolygons(List<tile> tilesList) {

        List<Float> valuesTiles = new ArrayList<>();

        for  (tile tile : tilesList) {
            valuesTiles.add(tile.getValue());
        }

        float maximum = Collections.max(valuesTiles);
        // Find the minimum value
        float minimum = Collections.min(valuesTiles);

        List<Integer> colorList = new ArrayList<>();

        //return Color.argb(50,
        int alpha = 70;

        colorList.add(Color.argb( alpha,0, 104, 55 ));
        colorList.add(Color.argb(alpha, 33, 155 , 81 ));
        colorList.add(Color.argb(alpha, 114, 194 , 100 ));
        colorList.add(Color.argb(alpha, 183, 224 , 117 ));
        colorList.add(Color.argb(alpha, 233, 245 , 161 ));
        colorList.add(Color.argb(alpha, 254, 237 , 161 ));
        colorList.add(Color.argb(alpha, 253, 190 , 111 ));
        colorList.add(Color.argb(alpha, 245, 121 , 72 ));
        colorList.add(Color.argb(alpha, 217, 53 , 41 ));
        colorList.add(Color.argb(alpha, 165, 0 , 38 ));

        List<Polygon> polygonList = new ArrayList<>();

        for  (tile tile : tilesList) {

            List<LatLng> polygonLatLng = new ArrayList<>();
            String polygon = tile.getGeometry();
            String[] parts = polygon.split("_");


            for (String part : parts){

                String[] latLngStr = part.split(" ");
                polygonLatLng.add(new LatLng(Double.parseDouble(latLngStr[2]), Double.parseDouble(latLngStr[1])));

            }


            float value = Math.round(tile.getValue());
            // assignColor(double value, double min, double max, List<Integer> colorList)
            int color = assignColor(value, minimum, maximum, colorList);

            int POLYGON_STROKE_WIDTH_PX = 0;

            Polygon polygonX = map.addPolygon(new PolygonOptions()
                    .addAll(polygonLatLng)
                    .fillColor(color)
                    .strokeWidth(POLYGON_STROKE_WIDTH_PX));

            polygonList.add(polygonX);
        }

        polygonListsMap.put("polys", polygonList);
    }

    public void removePolygons(String id) {
        List<Polygon> polygonList = polygonListsMap.get(id);
        if (polygonList != null) {
            for (Polygon polygon : polygonList) {
                polygon.remove();
            }
            polygonListsMap.remove(id);
        }
    }

    public int assignColor(double value, double min, double max, List<Integer> colorList) {
        // Calculate the range for each class
        double range = (max - min) / colorList.size();
        // Find the class index for the given value
        int classIndex = (int) ((value - min) / range);
        // Ensure the class index is within the valid range
        classIndex = Math.max(0, Math.min(classIndex, colorList.size() - 1));

        // Get the color from the color list for the class index
        return colorList.get(classIndex);
    }

    public void createHeatmap(HashMap<String, node> nodesHashMap) {

        int[] colors = {
                Color.rgb(1, 50, 32),
                Color.rgb(46, 184, 110), // green
                Color.rgb(219, 224, 150),
                Color.rgb(255, 165, 0), //orange
                //Color.rgb(201, 48, 74),   // red
                Color.rgb(178, 34, 34) // firebrick
        };

        //android:endColor="#c9304a"
        //        android:startColor="#2eb86e"

        float[] startPoints = {0.0F, 0.25F, 0.5F, 0.75F, 0.9F};
        //float[] startPoints = {0.0F, 2.0F, 4.0F, 5.0F, 6.0F};


        Gradient gradient = new Gradient(colors, startPoints);

        ArrayList<WeightedLatLng> latLngsW = new ArrayList<WeightedLatLng>();

        //for  (tile tile : tilesList) {
        /*int value = 0;
        for (node node : nodesHashMap.values()){
            value = Math.round(node.getValue());
            latLngsW.add(new WeightedLatLng(
                    new LatLng(node.getLongitude(), node.getLatitude()),
                    value));
        }*/

        List<Float> valuesTiles = new ArrayList<>();

        for  (tile tile : tilesList) {
            valuesTiles.add(tile.getValue());
        }

        float maximum = Collections.max(valuesTiles);
        // Find the minimum value
        float minimum = Collections.min(valuesTiles);

        for  (tile tile : tilesList) {

            List<LatLng> polygonLatLng = new ArrayList<>();
            String polygon = tile.getGeometry();
            String[] parts = polygon.split("_");

            String[] latLngStr = parts[0].split(" ");

            latLngsW.add(new WeightedLatLng(
                    new LatLng(Double.parseDouble(latLngStr[2]),
                            Double.parseDouble(latLngStr[1])),
                    tile.getValue()));

            //polygonLatLng.add(new LatLng(Double.parseDouble(latLngStr[2]), Double.parseDouble(latLngStr[1])));

            //float value = Math.round(tile.getValue());

        }

        //HeatmapTileProvider
        provider = new HeatmapTileProvider.Builder()
                .weightedData(latLngsW)
                .gradient(gradient)
                //.maxIntensity(7)
                .build();

        provider.setRadius(25);

        // Add a tile overlay to the map, using the heat map tile provider.
        overlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(provider));

        provider.setOpacity(0.50);
        overlay.clearTileCache();

        //setMapOnZoom(map, provider, overlay);
    }

    // MAPS Styling

    public Marker addInfoWindow(String title, String messagePollution, String messageLength, Polyline polyline, Marker marker, Boolean shortest) {
        int pointsOnLine = polyline.getPoints().size();
        //LatLng infoLatLng = polyline.getPoints().get(4 * pointsOnLine / 5);
        LatLng infoLatLng = polyline.getPoints().get(pointsOnLine / 2);

        if (marker != null) {
            marker.remove();
        }

        marker = map.addMarker(new MarkerOptions()
                .position(infoLatLng)
                .icon(bitmapDescriptorFromVector(this, title ,messagePollution, messageLength, shortest)));
        return marker;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, String id,String label, String label2, boolean Shortest) {
        // Load the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.marker_info_window, null);

        // Set the text of the marker label
        TextView textView = view.findViewById(R.id.marker_label);

        if (Shortest){
            textView.setBackgroundResource(drawable.talking_box_shortest);
        }

        String sourceString = "<b>" + id + "</b><br> " + label + " pm2.5 </br><br> " + label2+" meters</b>";

        textView.setText(HtmlCompat.fromHtml(sourceString, HtmlCompat.FROM_HTML_MODE_LEGACY));

        // Convert the layout to a BitmapDescriptor
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        //view.layout(-10,0);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    // helper function clean maps
    public void clearMarker(){
        if (targetMarker != null) {
            targetMarker.remove();
        }
    }

    public void clearCircles(){
        if (MainActivity.this.circleInit != null) {
            MainActivity.this.circleInit.remove();
        }
        if (MainActivity.this.circleEnd != null) {
            MainActivity.this.circleEnd.remove();
        }
    }

    // TEST GET CONTENT RESOLVER

    private List<tile> openExternalCSVFile(Uri uri) {
        String firstValue = "EMPTY";
        List<tile> tilesList = new ArrayList<>();
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            int n = 0;
            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                // Process each line of the CSV file
                String[] rowData = line.split(",");
                // Do something with the data
                if (n != 0) {
                    String[] values = line.split(",");
                    // tile(int ID, float value, String geometry)
                    tilesList.add(new tile(Integer.parseInt(values[0]),
                            Float.valueOf(values[1]),
                            values[2]));
                }
                n = n+1;

            }

            bufferedReader.close();
            inputStream.close();


        } catch (IOException e) {
            //Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            // Handle file reading error
        }

        return tilesList;
    }
}