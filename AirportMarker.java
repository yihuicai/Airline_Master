package module6;

import java.util.List;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import processing.core.PConstants;
import processing.core.PGraphics;

/** 
 * A class to represent AirportMarkers on a world map.
 *   
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMarker extends CommonMarker {
	public static List<SimpleLinesMarker> routes;
	public static boolean clicked = false;
	public boolean src = false;
	private int color_r = 11;
	private int color_g = 0;
	private int color_b = 0;
	
	public AirportMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
	
	}
	
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		if (clicked)
			pg.fill(color_r, color_g, color_b);
		else
			pg.fill(11);
		pg.ellipse(x, y, 10, 10);
		
	}
	
	public void setColor(int r,int g, int b) {
		this.color_r = r;
		this.color_g = g;
		this.color_b = b;
		
	}
	public void setClicked(boolean c){
		clicked = c;
		if(c){
			if(src){
				System.out.println("Source Airport:");
				setColor(255,255,0);
			}
			else{
				System.out.println("Destination Airport:");
				setColor(255,0,0);
			}
			System.out.println(this.getStringProperty("code"));
		}
		
	}
	@Override
	public void showTitle(PGraphics pg, float x, float y) {
		if (this.isHidden() == true)
			return;
		// show rectangle with title
		pg.pushStyle();
		pg.rectMode(PConstants.CORNER);
		
		pg.stroke(110);
		pg.fill(255,255,255);
		pg.rect(x, y + 15, pg.textWidth(this.getStringProperty("code")) +6, 18, 5);
		
		pg.textAlign(PConstants.LEFT, PConstants.TOP);
		pg.fill(0);
		pg.text(this.getStringProperty("code"), x + 3 , y + 18);

		
		// show routes
		pg.popStyle();
		
	}
	
}
