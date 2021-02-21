package qub;

public class ShipmentSummary extends JSONObjectWrapperBase
{
    private static final String carrierIdPropertyName = "carrierId";
    private static final String trackingIdPropertyName = "trackingId";
    private static final String textPropertyName = "text";

    private ShipmentSummary(JSONObject json)
    {
        super(json);
    }

    public static ShipmentSummary create()
    {
        return ShipmentSummary.create(JSONObject.create());
    }

    public static ShipmentSummary create(JSONObject json)
    {
        return new ShipmentSummary(json);
    }

    private String getString(String propertyName)
    {
        return this.toJson().getString(propertyName).catchError().await();
    }

    private ShipmentSummary setString(String propertyName, String propertyValue)
    {
        this.toJson().setString(propertyName, propertyValue);

        return this;
    }

    public String getCarrierId()
    {
        return this.getString(ShipmentSummary.carrierIdPropertyName);
    }

    public ShipmentSummary setCarrierId(String carrierId)
    {
        PreCondition.assertNotNullAndNotEmpty(carrierId, "carrierId");

        return this.setString(ShipmentSummary.carrierIdPropertyName, carrierId);
    }

    public String getTrackingId()
    {
        return this.getString(ShipmentSummary.trackingIdPropertyName);
    }

    public ShipmentSummary setTrackingId(String trackingId)
    {
        PreCondition.assertNotNullAndNotEmpty(trackingId, "trackingId");

        return this.setString(ShipmentSummary.trackingIdPropertyName, trackingId);
    }

    public String getText()
    {
        return this.getString(ShipmentSummary.textPropertyName);
    }

    public ShipmentSummary setText(String text)
    {
        PreCondition.assertNotNullAndNotEmpty(text, "text");

        return this.setString(ShipmentSummary.textPropertyName, text);
    }
}
