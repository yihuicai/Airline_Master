package module6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMap extends PApplet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	UnfoldingMap map;
	private List<Marker> airportList;
	private AirportMarker lastSelected;
	private AirportMarker src_target;
	private AirportMarker des_target;
	List<Marker> routeList;
	HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
	private ArrayList<Marker> direct = new ArrayList<Marker>();
	private ArrayList<Marker> layover1 = new ArrayList<Marker>();
	private ArrayList<Marker> layover2 = new ArrayList<Marker>();

	public void setup() {
		// setting up PAppler
		size(800,600, OPENGL);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 50, 50, 750, 550);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<Marker>();
		// HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
		
		// create markers from features
		for(PointFeature feature : features) {
			// Skip train stations
			if (feature.getStringProperty("code").length() == 2)
				continue;
			AirportMarker m = new AirportMarker(feature);
	
			m.setRadius(5);
			airportList.add(m);
			
			// put airport in hashmap with OpenFlights unique id for key
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
		
		}
		
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
		
			//System.out.println(sl.getProperties());
			
			//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
			routeList.add(sl);
		}
		
		
		
		//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
		//map.addMarkers(routeList);
		
		map.addMarkers(airportList);
		
	}
	@Override
	public void mouseMoved() {
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		isHovered(airportList);
	}
	
	private void isHovered(List<Marker> markers){
		if (lastSelected != null) {
			return;
		}
		for (Marker m : markers){
			//System.out.println(m.getProperties());
			if (m.isInside(map, mouseX, mouseY)){
				m.setSelected(true);
				lastSelected = (AirportMarker) m;
				return;
			}
		}
		
	}
	
	@Override
	public void mouseClicked() {
		if (src_target!=null && des_target!=null){
			direct.clear();
			for (Marker m : layover1)
				m.setHidden(true);
			for (Marker m : layover2)
				m.setHidden(true);
			layover1.clear();
			layover2.clear();
			src_target.setClicked(false);
			des_target.setClicked(false);
			src_target = null;
			des_target = null;
		}
		boolean selected = false;
		for (Marker m : airportList){
			m.setHidden(true);
			if (m != src_target && m != des_target)
				((AirportMarker) m).setColor(11, 0, 0);
		}
			
			
		for (Marker m : airportList){
			if(m.isInside(map, mouseX, mouseY)){
				if (src_target == null){
					src_target = (AirportMarker) m;
					src_target.src = true;
					src_target.setClicked(true);
					break;
				}
				else if (src_target == m)
					break;
				else {
					des_target = (AirportMarker) m;
					des_target.src = false;
					des_target.setClicked(true);
					des_target.setHidden(false);
					src_target.setHidden(false);
					selected = true;
					break;
				}
			}

		}
		if(!selected)
			unhideMarkers(airportList);
		
		if (src_target != null && des_target != null){
			connectAirports(src_target, des_target, routeList);
			map.addMarkers(direct);
			map.addMarkers(layover1);
			unhideMarkers(layover1);
			map.addMarkers(layover2);
			unhideMarkers(layover2);
		}
	}
	private void unhideMarkers(List<Marker> toUnhide){
		for (Marker m : toUnhide){
			m.setHidden(false);
		}
	}
	private Location getLocation(String m){
		
		//System.out.println(Integer.parseInt(m));
		//System.out.println(airports.get(Integer.parseInt(m)).toString());
		
		return airports.get(Integer.parseInt(m));
	}
	
	public void connectAirports(AirportMarker a1, AirportMarker a2, List<Marker> routes){
		ArrayList<SimpleLinesMarker> src = new ArrayList<SimpleLinesMarker>();
		ArrayList<SimpleLinesMarker> des = new ArrayList<SimpleLinesMarker>();
		for (Marker r : routes){
			if (getLocation(r.getStringProperty("source"))==a1.getLocation()){
				src.add((SimpleLinesMarker)r);
			}
			else if (getLocation(r.getStringProperty("destination"))==a2.getLocation()){
				des.add((SimpleLinesMarker)r);
			}
		}
		int count = 0;
		for(SimpleLinesMarker r1: src){
			//System.out.println(r1.getStringProperty("source"));
			if (getLocation(r1.getStringProperty("destination")) == a2.getLocation())
				direct.add(r1);
			for(SimpleLinesMarker r2: des){
				if (count < 5 && getLocation(r1.getStringProperty("destination")) == getLocation(r2.getStringProperty("source"))){
					layover1.add(r1);
					layover2.add(r2);
					count++;
				}
			}
		}
		System.out.println(layover1.size());
	}
	
	//return layoverMarker
	public void layoverAirports(List<Marker> route_src, List<Marker> route_des){
		
	}
	
	
	public void draw() {
		background(0);
		map.draw();
		
	}
	

}
