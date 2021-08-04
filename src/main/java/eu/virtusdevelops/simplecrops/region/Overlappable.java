package eu.virtusdevelops.simplecrops.region;

/**
 * Represents a Region which supports the overlap and intersect operations
 * @author Redempt
 * https://github.com/Redempt/RedLib
 */
public interface Overlappable {
	
	/**
	 * Checks whether this Overlappable overlaps another Overlappable
	 * @param other The Overlappable Region to check
	 * @return Whether this Overlappable overlaps the given Overlappable
	 */
	public boolean overlaps(Overlappable other);
	
	/**
	 * Gets the intersection of this Overlappable with another
	 * @param other The Overlappable Region to check
	 * @return The intersection of this Region with the provided Overlappable Region
	 */
	public Region getIntersection(Overlappable other);
	
}
