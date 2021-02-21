package qub;

public class USPSCarrier implements Carrier
{
    private static final String carrierId = "USPS";

    private final USPSClient uspsClient;

    private USPSCarrier(USPSClient uspsClient)
    {
        PreCondition.assertNotNull(uspsClient, "client");

        this.uspsClient = uspsClient;
    }

    public static USPSCarrier create(HttpClient httpClient)
    {
        PreCondition.assertNotNull(httpClient, "httpClient");

        return USPSCarrier.create(USPSClient.create(httpClient));
    }

    public static USPSCarrier create(USPSClient uspsClient)
    {
        return new USPSCarrier(uspsClient);
    }

    @Override
    public String getId()
    {
        return USPSCarrier.carrierId;
    }

    @Override
    public Result<ShipmentSummary> getShipmentSummary(Shipment shipment)
    {
        PreCondition.assertNotNull(shipment, "shipment");
        PreCondition.assertNotNullAndNotEmpty(shipment.getTrackingId(), "shipment.getTrackingId()");

        return Result.create(() ->
        {
            final String trackingId = shipment.getTrackingId();
            final TrackResponseText response = this.uspsClient.trackText(trackingId).await();
            final Iterable<TrackInfoText> trackInfos = response.getTrackInfo();
            return trackInfos.map(USPSCarrier::toShipmentSummary).first();
        });
    }

    @Override
    public Result<Iterable<ShipmentSummary>> getShipmentSummaries(Iterable<Shipment> shipments)
    {
        PreCondition.assertNotNullAndNotEmpty(shipments, "shipments");

        return Result.create(() ->
        {
            final Iterable<String> trackingIds = shipments.map(Shipment::getTrackingId);
            final TrackResponseText response = this.uspsClient.trackText(trackingIds).await();
            final Iterable<TrackInfoText> trackInfos = response.getTrackInfo();
            return trackInfos.map(USPSCarrier::toShipmentSummary);
        });
    }

    public static ShipmentSummary toShipmentSummary(TrackInfoText trackInfo)
    {
        PreCondition.assertNotNull(trackInfo, "trackInfo");

        final ShipmentSummary result = ShipmentSummary.create()
            .setCarrierId(USPSCarrier.carrierId)
            .setTrackingId(trackInfo.getId());

        final TrackSummaryText summary = trackInfo.getSummary().await();
        result.setText(summary.getText());

        PostCondition.assertNotNull(result, "result");
        PostCondition.assertNotNull(result.getText(), "result.getText()");

        return result;
    }
}
