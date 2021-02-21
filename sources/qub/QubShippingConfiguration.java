package qub;

public class QubShippingConfiguration extends JSONObjectWrapperBase
{
    private static final String shipmentsPropertyName = "shipments";

    private QubShippingConfiguration(JSONObject json)
    {
        super(json);
    }

    public static QubShippingConfiguration create()
    {
        return QubShippingConfiguration.create(JSONObject.create());
    }

    public static QubShippingConfiguration create(JSONObject json)
    {
        return new QubShippingConfiguration(json);
    }

    public Iterable<Shipment> getShipments()
    {
        return this.toJson().getArray(QubShippingConfiguration.shipmentsPropertyName)
            .then((JSONArray shipments) -> shipments
                .instanceOf(JSONObject.class)
                .map(Shipment::create))
            .catchError()
            .await();
    }

    public QubShippingConfiguration setShipments(Iterable<Shipment> shipments)
    {
        PreCondition.assertNotNull(shipments, "shipments");

        this.toJson().setArray(QubShippingConfiguration.shipmentsPropertyName, JSONArray.create(shipments.map(Shipment::toJson)));

        return this;
    }
}
