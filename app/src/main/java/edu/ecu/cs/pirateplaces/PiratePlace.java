package edu.ecu.cs.pirateplaces;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Represents a specific Pirate Place
 *
 * @author Mark Hills (mhills@cs.ecu.edu)
 * @version 1.3
 */
public class PiratePlace implements Serializable
{
    /** The ID of the place */
    private UUID mId;

    /** The name of the place */
    private String mPlaceName;

    /** The date the place was last visited */
    private Date mLastVisited;

    private boolean hasLocation;
    private double latitude;
    private double longitude;

    /**
     * Create a default Pirate Place item
     *
     */
    public PiratePlace()
    {
        this(UUID.randomUUID());
    }

    /**
     * Create a Pirate Place with the given id and defaults
     *
     * @param id The id of the place
     */
    public PiratePlace(UUID id)
    {
        mId = id;
        mPlaceName = "";
        mLastVisited = new Date();
        hasLocation = false;
        latitude = 0.0;
        longitude = 0.0;
    }

    /**
     * Create a Pirate Place item
     *
     * @param placeName The name of the place
     * @param lastVisited The date the place was last visited
     */
    public PiratePlace(String placeName, Date lastVisited)
    {
        mId = UUID.randomUUID();
        mPlaceName = placeName;
        mLastVisited = lastVisited;
    }

    /**
     * Get the ID
     *
     * @return the id
     */
    public UUID getId()
    {
        return mId;
    }

    /**
     * Get the place name
     *
     * @return the place name
     */
    public String getPlaceName()
    {
        return mPlaceName;
    }

    /**
     * Set the place name
     *
     * @param placeName The new place name
     */
    public void setPlaceName(String placeName)
    {
        mPlaceName = placeName;
    }

    /**
     * Get the last visited date
     *
     * @return the last visisted date
     */
    public Date getLastVisited()
    {
        return mLastVisited;
    }

    /**
     * Set the last visited date
     *
     * @param lastVisited The new lastVisisted date
     */
    public void setLastVisited(Date lastVisited)
    {
        mLastVisited = lastVisited;
    }

    /**
     * Get the directory with image files for this PiratePlace
     *
     * @return the directory holding the image files
     */
    public String getPhotoFilenameDir()
    {
        return "images/" + getId().toString();
    }

    public boolean hasLocation() {
        return hasLocation;
    }

    public void setHasLocation(boolean hasLocation) {
        this.hasLocation = hasLocation;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
