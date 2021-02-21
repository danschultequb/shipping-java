package qub;

public interface QubShipping
{
    Path configurationFileRelativePath = Path.parse("configuration.json");

    static void main(String[] args)
    {
        DesktopProcess.run(args, QubShipping::run);
    }

    static void run(DesktopProcess process)
    {
        PreCondition.assertNotNull(process, "process");

        final CommandLineActions actions = process.createCommandLineActions()
            .setApplicationName("qub-shipping")
            .setApplicationDescription("Application that gets the current details about shipments.");

        actions.addAction("add", QubShipping::addShipment)
            .addAlias("add")
            .setDescription("Add a new shipment.");

        actions.addAction("removeShipment", QubShipping::removeShipment)
            .addAlias("remove")
            .setDescription("Remove a provided shipment.");

        actions.addAction("listShipments", QubShipping::listShipments)
            .addAlias("list")
            .setDescription("List current shipments.")
            .setDefaultAction();

        CommandLineConfigurationAction.addAction(actions, CommandLineConfigurationActionParameters.create()
            .setConfigurationFileRelativePath(QubShipping.configurationFileRelativePath));

        actions.run(process);
    }

    static void addShipment(DesktopProcess process, CommandLineAction action)
    {
        PreCondition.assertNotNull(process, "process");
        PreCondition.assertNotNull(action, "action");

        final CommandLineParameters parameters = action.createCommandLineParameters(process);

        final CommandLineParameter<String> trackingIdParameter = parameters.addPositionString("trackingId")
            .setDescription("The tracking ID of the shipment.")
            .setRequired(true);
        final CommandLineParameter<String> carrierParameter = parameters.addString("carrier")
            .setDescription("The carrier that is shipping the shipment.");
        final CommandLineParameterHelp helpParameter = parameters.addHelp();

        if (!helpParameter.showApplicationHelpLines(process).await())
        {
            final CharacterWriteStream output = process.getOutputWriteStream();

            final String trackingId = trackingIdParameter.getValue().await();
            if (Strings.isNullOrEmpty(trackingId))
            {
                output.writeLine("trackingId cannot be empty.").await();
                process.setExitCode(-1);
            }
            else
            {
                final QubShippingConfiguration configuration = QubShipping.readConfiguration(process);

                final List<Shipment> shipmentList = List.create();
                final Iterable<Shipment> shipments = configuration.getShipments();
                if (!Iterable.isNullOrEmpty(shipments))
                {
                    shipmentList.addAll(shipments);
                }

                if (shipmentList.contains((Shipment shipment) -> shipment.getTrackingId().equals(trackingId)))
                {
                    output.writeLine("A shipment with tracking ID " + Strings.escapeAndQuote(trackingId) + " already exists.").await();
                    process.setExitCode(-1);
                }
                else
                {
                    final Shipment shipment = Shipment.create()
                        .setTrackingId(trackingId);
                    final String carrier = carrierParameter.getValue().await();
                    if (!Strings.isNullOrEmpty(carrier))
                    {
                        shipment.setCarrier(carrier);
                    }
                    shipmentList.add(shipment);

                    configuration.setShipments(shipmentList);

                    QubShipping.saveConfiguration(process, configuration);
                }
            }
        }
    }

    static void removeShipment(DesktopProcess process, CommandLineAction action)
    {
        PreCondition.assertNotNull(process, "process");
        PreCondition.assertNotNull(action, "action");

        final CommandLineParameters parameters = action.createCommandLineParameters(process);

        final CommandLineParameter<String> trackingIdParameter = parameters.addPositionString("trackingId")
            .setDescription("The tracking ID of the shipment.")
            .setRequired(true);
        final CommandLineParameterHelp helpParameter = parameters.addHelp();

        if (!helpParameter.showApplicationHelpLines(process).await())
        {
            final CharacterWriteStream output = process.getOutputWriteStream();

            final String trackingId = trackingIdParameter.getValue().await();
            if (Strings.isNullOrEmpty(trackingId))
            {
                output.writeLine("trackingId cannot be empty.").await();
                process.setExitCode(-1);
            }
            else
            {
                final QubShippingConfiguration configuration = QubShipping.readConfiguration(process);

                final List<Shipment> shipmentList = List.create();
                final Iterable<Shipment> shipments = configuration.getShipments();
                if (!Iterable.isNullOrEmpty(shipments))
                {
                    shipmentList.addAll(shipments);
                }

                final Shipment removedShipment = shipmentList.removeFirst((Shipment shipment) -> shipment.getTrackingId().equals(trackingId));
                if (removedShipment == null)
                {
                    output.writeLine("No shipment with tracking ID " + Strings.escapeAndQuote(trackingId) + " was found.").await();
                    process.setExitCode(-1);
                }
                else
                {
                    configuration.setShipments(shipmentList);

                    QubShipping.saveConfiguration(process, configuration);
                }
            }
        }
    }

    static void listShipments(DesktopProcess process, CommandLineAction action)
    {
        PreCondition.assertNotNull(process, "process");
        PreCondition.assertNotNull(action, "action");

        final CommandLineParameters parameters = action.createCommandLineParameters(process);

        final CommandLineParameterBoolean getSummaryParameter = parameters.addBoolean("getSummary", true)
            .addAlias("summary")
            .setDescription("Whether or not to fetch the summary of the shipments.");

        final CommandLineParameterHelp helpParameter = parameters.addHelp();

        if (!helpParameter.showApplicationHelpLines(process).await())
        {
            final CharacterWriteStream output = process.getOutputWriteStream();
            final boolean getSummary = getSummaryParameter.getValue().await();

            final CharacterTable table = CharacterTable.create();

            final QubShippingConfiguration configuration = QubShipping.readConfiguration(process);
            final Iterable<Shipment> shipments = configuration.getShipments();
            if (!Iterable.isNullOrEmpty(shipments))
            {
                final MutableMap<String, ShipmentSummary> shipmentSummaries = Map.create();
                if (getSummary)
                {
                    final Iterable<Carrier> carriers = QubShipping.getCarriers(process);
                    final MutableMap<Carrier, List<Shipment>> carrierShipmentMap = Map.create();
                    for (final Shipment shipment : shipments)
                    {
                        final Carrier carrier = carriers.first((Carrier c) -> Comparer.equalIgnoreCase(c.getId(), shipment.getCarrier()));
                        if (carrier != null)
                        {
                            carrierShipmentMap.getOrSet(carrier, List::create).await()
                                .add(shipment);
                        }
                    }

                    for (final MapEntry<Carrier, List<Shipment>> carrierAndShipments : carrierShipmentMap)
                    {
                        final Carrier carrier = carrierAndShipments.getKey();
                        if (carrier != null)
                        {
                            final Iterable<Shipment> carrierShipments = carrierAndShipments.getValue();
                            if (!Iterable.isNullOrEmpty(carrierShipments))
                            {
                                final Iterable<ShipmentSummary> carrierShipmentSummaries = carrier.getShipmentSummaries(carrierShipments).await();
                                for (final ShipmentSummary shipmentSummary : carrierShipmentSummaries)
                                {
                                    shipmentSummaries.set(shipmentSummary.getTrackingId(), shipmentSummary);
                                }
                            }
                        }
                    }
                }

                for (final Shipment shipment : shipments)
                {
                    final String carrier = shipment.getCarrier();
                    table.addRow("Carrier:", Strings.isNullOrEmpty(carrier) ? "Unknown" : carrier);

                    final String trackingId = shipment.getTrackingId();
                    table.addRow("Tracking ID:", Strings.isNullOrEmpty(trackingId) ? "Unknown" : trackingId);

                    if (getSummary)
                    {
                        final ShipmentSummary shipmentSummary = shipmentSummaries.get(shipment.getTrackingId())
                            .catchError(NotFoundException.class)
                            .await();
                        final String summary = shipmentSummary == null ? null : shipmentSummary.getText();
                        table.addRow("Summary:", Strings.isNullOrEmpty(summary) ? "Unknown" : summary);
                    }

                    table.addRow();
                }
                table.toString(output, CharacterTableFormat.consise).await();
            }
        }
    }

    static File getConfigurationFile(DesktopProcess process)
    {
        final Folder dataFolder = process.getQubProjectDataFolder().await();
        return dataFolder.getFile(QubShipping.configurationFileRelativePath).await();
    }

    static QubShippingConfiguration readConfiguration(DesktopProcess process)
    {
        PreCondition.assertNotNull(process, "process");

        final File configurationFile = QubShipping.getConfigurationFile(process);
        final JSONObject configurationJson = JSON.parseObject(configurationFile)
            .catchError(FileNotFoundException.class, () -> JSONObject.create())
            .await();
        return QubShippingConfiguration.create(configurationJson);
    }

    static void saveConfiguration(DesktopProcess process, QubShippingConfiguration configuration)
    {
        PreCondition.assertNotNull(process, "process");
        PreCondition.assertNotNull(configuration, "configuration");

        final File configurationFile = QubShipping.getConfigurationFile(process);
        configurationFile.setContentsAsString(configuration.toString(JSONFormat.pretty)).await();
    }

    static Iterable<Carrier> getCarriers(DesktopProcess process)
    {
        PreCondition.assertNotNull(process, "process");

        final EnvironmentVariables environmentVariables = process.getEnvironmentVariables();
        final Network network = process.getNetwork();
        final HttpClient httpClient = HttpClient.create(network);

        final List<Carrier> result = List.create();

        final String uspsUserId = environmentVariables.get("USPS_USER_ID").await();
        final USPSClient uspsClient = USPSClient.create(httpClient)
            .setUserId(uspsUserId);
        result.add(USPSCarrier.create(uspsClient));

        PostCondition.assertNotNull(result, "result");

        return result;
    }
}