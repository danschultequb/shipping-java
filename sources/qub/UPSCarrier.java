package qub;

public class UPSCarrier implements Carrier
{
    private static final String carrierId = "USPS";

    private final UPSClient upsClient;

    private UPSCarrier(UPSClient upsClient)
    {
        PreCondition.assertNotNull(upsClient, "client");

        this.upsClient = upsClient;
    }

    public static UPSCarrier create(HttpClient httpClient)
    {
        PreCondition.assertNotNull(httpClient, "httpClient");

        return UPSCarrier.create(UPSClient.create(httpClient));
    }

    public static UPSCarrier create(UPSClient upsClient)
    {
        return new UPSCarrier(upsClient);
    }

    @Override
    public String getId()
    {
        return UPSCarrier.carrierId;
    }

    @Override
    public Result<ShipmentSummary> getShipmentSummary(Shipment shipment)
    {
        PreCondition.assertNotNull(shipment, "shipment");
        PreCondition.assertNotNullAndNotEmpty(shipment.getTrackingId(), "shipment.getTrackingId()");

        return Result.create(() ->
        {
            final String trackingId = shipment.getTrackingId();
            final UPSTrackRequest trackRequest = UPSTrackRequest.create()
                .setInquiryNumber(trackingId);
            final UPSTrackResponse response = this.upsClient.sendTrackRequest(trackRequest).await();
            return UPSCarrier.toShipmentSummary(response);
        });
    }

    @Override
    public Result<Iterable<ShipmentSummary>> getShipmentSummaries(Iterable<Shipment> shipments)
    {
        PreCondition.assertNotNullAndNotEmpty(shipments, "shipments");

        return Result.create(() ->
        {
            final List<ShipmentSummary> result = List.create();
            for (final Shipment shipment : shipments)
            {
                result.add(this.getShipmentSummary(shipment).await());
            }
            return result;
        });
    }

    public static ShipmentSummary toShipmentSummary(UPSTrackResponse trackResponse)
    {
        PreCondition.assertNotNull(trackResponse, "trackResponse");

        final ShipmentSummary result = ShipmentSummary.create()
            .setCarrierId(UPSCarrier.carrierId)
            .setTrackingId(trackResponse.getTrackingNumber())
            .setText(trackResponse.getActivities().first().getDescription());

        PostCondition.assertNotNull(result, "result");
        PostCondition.assertNotNull(result.getText(), "result.getText()");

        return result;
    }
}
