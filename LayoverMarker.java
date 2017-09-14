package module6;

import java.util.List;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import processing.core.PConstants;
import processing.core.PGraphics;

public class LayoverMarker extends AirportMarker {
	
	public LayoverMarker(Feature city){
		super(city);
	}
	private String from;
	private String to;
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		pg.fill(255,255,0);
		pg.ellipse(x, y, 20, 20);
	}
	public String getSrc(){
		return from;
	}
		
	public String getDest(){
		return to;
	}
		
	public void setSrc(String src){
		from = src;
	}
	public void setDest(String dest){
		to = dest;
	}
}
