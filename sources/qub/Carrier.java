package qub;

public interface Carrier
{
    /**
     * Get the unique identifier/name of this carrier.
     * @return The unique identifier/name of this carrier.
     */
    String getId();

    /**
     * Get the most recent status/summary of the provided shipment.
     * @param shipment The shipment to get information for.
     * @return The most recent status/summary of the provided shipment.
     */
    Result<ShipmentSummary> getShipmentSummary(Shipment shipment);

    /**
     * Get the most recent status/summary of the provided shipments.
     * @param shipments The shipments to get information for.
     * @return The most recent status/summary of the provided shipments.
     */
    Result<Iterable<ShipmentSummary>> getShipmentSummaries(Iterable<Shipment> shipments);
}
