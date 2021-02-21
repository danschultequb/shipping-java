package qub;

public class Shipment extends JSONObjectWrapperBase
{
    private static final String carrierPropertyName = "carrier";
    private static final String trackingIdPropertyName = "trackingId";

    private Shipment(JSONObject json)
    {
        super(json);
    }

    public static Shipment create()
    {
        return Shipment.create(JSONObject.create());
    }

    public static Shipment create(JSONObject json)
    {
        return new Shipment(json);
    }

    public String getCarrier()
    {
        return this.toJson().getString(Shipment.carrierPropertyName)
            .catchError()
            .await();
    }

    public Shipment setCarrier(String carrier)
    {
        PreCondition.assertNotNullAndNotEmpty(carrier, "carrier");

        this.toJson().setString(Shipment.carrierPropertyName, carrier);

        return this;
    }

    public String getTrackingId()
    {
        return this.toJson().getString(Shipment.trackingIdPropertyName)
            .catchError()
            .await();
    }

    public Shipment setTrackingId(String trackingId)
    {
        PreCondition.assertNotNullAndNotEmpty(trackingId, "trackingId");

        this.toJson().setString(Shipment.trackingIdPropertyName, trackingId);

        return this;
    }
}
