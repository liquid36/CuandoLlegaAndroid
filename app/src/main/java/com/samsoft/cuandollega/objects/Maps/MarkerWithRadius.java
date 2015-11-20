package com.samsoft.cuandollega.objects.Maps;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;

/**
 * Created by sam on 12/11/15.
 */
public class MarkerWithRadius extends Overlay {

    /*attributes for standard features:*/
    protected Drawable mIcon;
    protected GeoPoint mPosition;
    protected float mBearing;
    protected float mAnchorU, mAnchorV;
    protected float mAlpha;
    protected Integer mRadius;
    protected boolean mDraggable, mIsDragged;
    protected boolean mFlat;
    protected OnMarkerClickListener mOnMarkerClickListener;
    protected OnMarkerDragListener mOnMarkerDragListener;

    /*attributes for non-standard features:*/
    protected Drawable mImage;
    protected boolean mPanToView;
    protected Object mRelatedObject;

    /*internals*/
    protected Point mPositionPixels;
    protected static Drawable mDefaultIcon = null; //cache for default icon (resourceProxy.getDrawable being slow)

    /** Usual values in the (U,V) coordinates system of the icon image */
    public static final float ANCHOR_CENTER=0.5f, ANCHOR_LEFT=0.0f, ANCHOR_TOP=0.0f, ANCHOR_RIGHT=1.0f, ANCHOR_BOTTOM=1.0f;

    public MarkerWithRadius(MapView mapView) {
        this(mapView, new DefaultResourceProxyImpl(mapView.getContext()));
    }

    public MarkerWithRadius(MapView mapView, final ResourceProxy resourceProxy) {
        super(resourceProxy);
        mBearing = 0.0f;
        mAlpha = 1.0f; //opaque
        mPosition = new GeoPoint(0.0, 0.0);
        mAnchorU = ANCHOR_CENTER;
        mAnchorV = ANCHOR_CENTER;
        mDraggable = false;
        mIsDragged = false;
        mPositionPixels = new Point();
        mPanToView = true;
        mFlat = false; //billboard
        mOnMarkerClickListener = null;
        mOnMarkerDragListener = null;
        mRadius = 500;
        if (mDefaultIcon == null)
            mDefaultIcon = resourceProxy.getDrawable(ResourceProxy.bitmap.marker_default);
        mIcon = mDefaultIcon;

    }

    /** Sets the icon for the marker. Can be changed at any time.
     * @param icon if null, the default osmdroid marker is used.
     */
    public void setIcon(Drawable icon){
        if (icon != null)
            mIcon = icon;
        else
            mIcon = mDefaultIcon;
    }

    public GeoPoint getPosition(){
        return mPosition;
    }

    public void setRadius(Integer radius) {mRadius = radius;}

    public Integer getRadius() {return mRadius;}

    public void setPosition(GeoPoint position){
        mPosition = position.clone();
    }

    public float getRotation(){
        return mBearing;
    }

    public void setRotation(float rotation){
        mBearing = rotation;
    }

    public void setAnchor(float anchorU, float anchorV){
        mAnchorU = anchorU;
        mAnchorV= anchorV;
    }

    public void setAlpha(float alpha){
        mAlpha = alpha;
    }

    public float getAlpha(){
        return mAlpha;
    }

    public void setDraggable(boolean draggable){
        mDraggable = draggable;
    }

    public boolean isDraggable(){
        return mDraggable;
    }

    public void setFlat(boolean flat){
        mFlat = flat;
    }

    public boolean isFlat(){
        return mFlat;
    }

    /**
     * Removes this Marker from the MapView.
     * Note that this method will operate only if the Marker is in the MapView overlays
     * (it should not be included in a container like a FolderOverlay).
     * @param mapView
     */
    public void remove(MapView mapView){
        mapView.getOverlays().remove(this);
    }

    public void setOnMarkerClickListener(OnMarkerClickListener listener){
        mOnMarkerClickListener = listener;
    }

    public void setOnMarkerDragListener(OnMarkerDragListener listener){
        mOnMarkerDragListener = listener;
    }

    /** set an image to be shown in the InfoWindow  - this is not the marker icon */
    public void setImage(Drawable image){
        mImage = image;
    }

    /** get the image to be shown in the InfoWindow - this is not the marker icon */
    public Drawable getImage(){
        return mImage;
    }


    /** If set to true, when clicking the marker, the map will be centered on the marker position.
     * Default is true. */
    public void setPanToView(boolean panToView){
        mPanToView = panToView;
    }

    /** Allows to link an Object (any Object) to this marker.
     * This is particularly useful to handle custom InfoWindow. */
    public void setRelatedObject(Object relatedObject){
        mRelatedObject = relatedObject;
    }

    /** @return the related object. */
    public Object getRelatedObject(){
        return mRelatedObject;
    }

    @Override public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        if (shadow)
            return;
        if (mIcon == null)
            return;

        final Projection pj = mapView.getProjection();

        pj.toPixels(mPosition, mPositionPixels);
        int width = mIcon.getIntrinsicWidth();
        int height = mIcon.getIntrinsicHeight();
        Rect rect = new Rect(0, 0, width, height);
        rect.offset(-(int)(mAnchorU*width), -(int)(mAnchorV*height));
        mIcon.setBounds(rect);

        mIcon.setAlpha((int)(mAlpha*255));

        float rotationOnScreen = (mFlat ? -mBearing : mapView.getMapOrientation()-mBearing);
        Log.d("MARKER", "offsetX = " + mAnchorU + "offsetY = " +mAnchorV);
        drawAt(canvas, mIcon, mPositionPixels.x , mPositionPixels.y , false, rotationOnScreen);

        final float radius = mapView.getProjection().metersToEquatorPixels(mRadius);
        Paint mCirclePaint = new Paint();
        mCirclePaint.setARGB(0, 100, 100, 255);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setAlpha(50);
        mCirclePaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mPositionPixels.x, mPositionPixels.y, radius, mCirclePaint);

    }

    public boolean hitTest(final MotionEvent event, final MapView mapView){
        final Projection pj = mapView.getProjection();
        pj.toPixels(mPosition, mPositionPixels);
        final Rect screenRect = pj.getIntrinsicScreenRect();
        int x = -mPositionPixels.x + screenRect.left + (int) event.getX();
        int y = -mPositionPixels.y + screenRect.top + (int) event.getY();
        boolean hit = mIcon.getBounds().contains(x, y);
        return hit;
    }

    @Override public boolean onSingleTapConfirmed(final MotionEvent event, final MapView mapView){
        boolean touched = hitTest(event, mapView);
        if (touched){
            if (mOnMarkerClickListener == null){
                return onMarkerClickDefault(this, mapView);
            } else {
                return mOnMarkerClickListener.onMarkerClick(this, mapView);
            }
        } else
            return touched;
    }

    public void moveToEventPosition(final MotionEvent event, final MapView mapView){
        final Projection pj = mapView.getProjection();
        GeoPoint newPosition = (GeoPoint) pj.fromPixels((int)event.getX(), (int)event.getY());
        mRadius = newPosition.distanceTo(mPosition);
        mapView.invalidate();
    }

    @Override public boolean onLongPress(final MotionEvent event, final MapView mapView) {
        boolean touched = hitTest(event, mapView);
        if (touched){
            if (mDraggable){
                //starts dragging mode:
                mIsDragged = true;
                if (mOnMarkerDragListener != null)
                    mOnMarkerDragListener.onMarkerRadiusStart(this);
                moveToEventPosition(event, mapView);
            }
        }
        return touched;
    }

    @Override public boolean onTouchEvent(final MotionEvent event, final MapView mapView) {
        if (mDraggable && mIsDragged){
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mIsDragged = false;
                if (mOnMarkerDragListener != null)
                    mOnMarkerDragListener.onMarkerRadiusEnd(this);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE){
                moveToEventPosition(event, mapView);
                if (mOnMarkerDragListener != null)
                    mOnMarkerDragListener.onMarkerRadius(this);
                return true;
            } else
                return false;
        } else
            return false;
    }

    //-- Marker events listener interfaces ------------------------------------

    public interface OnMarkerClickListener{
        abstract boolean onMarkerClick(MarkerWithRadius marker, MapView mapView);
    }

    public interface OnMarkerDragListener{
        abstract void onMarkerRadius(MarkerWithRadius marker);
        abstract void onMarkerRadiusEnd(MarkerWithRadius marker);
        abstract void onMarkerRadiusStart(MarkerWithRadius marker);
    }

    /** default behaviour when no click listener is set */
    protected boolean onMarkerClickDefault(MarkerWithRadius marker, MapView mapView) {
        if (marker.mPanToView)
            mapView.getController().animateTo(marker.getPosition());
        return true;
    }
}
