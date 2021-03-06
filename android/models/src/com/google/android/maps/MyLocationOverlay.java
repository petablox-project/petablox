import edu.stanford.stamp.harness.ApplicationDriver;

class MyLocationOverlay
{
	@STAMP(flows={@Flow(from="$LOCATION",to="@return")})
	public com.google.android.maps.GeoPoint getMyLocation()
	{
		return new GeoPoint(0,0);
	}

    @STAMP(flows={@Flow(from="$LOCATION",to="@return")})	
	public android.location.Location getLastFix()
	{
        return new android.location.Location((String) null);		
	}

	public synchronized boolean runOnFirstFix(final java.lang.Runnable param1) {
		ApplicationDriver.getInstance().registerCallback(new edu.stanford.stamp.harness.Callback() {
				public void run() {
					param1.run();
				}
			});
		return true;
    }


}