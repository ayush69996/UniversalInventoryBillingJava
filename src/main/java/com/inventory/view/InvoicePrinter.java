
package com.inventory.view;

import com.inventory.model.BillItem;
import com.inventory.model.Sale;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Scale;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class InvoicePrinter {

    private static final Color BLUE = Color.rgb(37, 99, 235);
    private static final Color NAVY = Color.rgb(30, 41, 59);
    private static final Color GREY = Color.rgb(100, 116, 139);
    private static final Color LIGHT = Color.rgb(241, 245, 249);
    private static final Color GREEN = Color.rgb(22, 163, 74);

    public static void printInvoice(Sale sale, List<BillItem> items) {

        VBox invoice = new VBox(22);
        invoice.setPadding(new Insets(35));
        invoice.setPrefWidth(680);
        invoice.setMaxWidth(680);
        invoice.setStyle("-fx-background-color: white;");

        Circle logoCircle = new Circle(27, BLUE);

        Label logoText = new Label("UI");
        logoText.setTextFill(Color.WHITE);
        logoText.setFont(Font.font("Arial", FontWeight.BOLD, 19));

        StackPane logo = new StackPane(logoCircle, logoText);

        Label companyName = new Label("Universal Inventory");
        companyName.setFont(Font.font("Arial", FontWeight.BOLD, 23));
        companyName.setTextFill(NAVY);

        Label companySubtitle = new Label("INVENTORY & BILLING SYSTEM");
        companySubtitle.setFont(Font.font("Arial", 10));
        companySubtitle.setTextFill(GREY);

        VBox companyInfo = new VBox(5, companyName, companySubtitle);

        HBox companyHeader = new HBox(14, logo, companyInfo);
        companyHeader.setAlignment(Pos.CENTER_LEFT);

        Label invoiceHeading = new Label("INVOICE");
        invoiceHeading.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        invoiceHeading.setTextFill(BLUE);

        Label invoiceNumber = new Label("#" + sale.getId());
        invoiceNumber.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        invoiceNumber.setTextFill(NAVY);

        Label invoiceDate = new Label(
                sale.getSaleDate().format(
                        DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")
                )
        );
        invoiceDate.setFont(Font.font("Arial", 12));
        invoiceDate.setTextFill(GREY);

        VBox invoiceInfo = new VBox(
                7,
                invoiceHeading,
                invoiceNumber,
                invoiceDate
        );

        invoiceInfo.setAlignment(Pos.CENTER_RIGHT);

        HBox topSection = new HBox(companyHeader, invoiceInfo);
        topSection.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(companyHeader, Priority.ALWAYS);

        Region accentLine = new Region();
        accentLine.setPrefHeight(4);
        accentLine.setMaxWidth(Double.MAX_VALUE);
        accentLine.setBackground(new Background(
                new BackgroundFill(BLUE, new CornerRadii(5), Insets.EMPTY)
        ));

        Label sectionTitle = new Label("Order Summary");
        sectionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        sectionTitle.setTextFill(NAVY);

        Label sectionSubtitle = new Label(
                "A detailed breakdown of purchased products"
        );
        sectionSubtitle.setFont(Font.font("Arial", 11));
        sectionSubtitle.setTextFill(GREY);

        VBox sectionHeading = new VBox(5, sectionTitle, sectionSubtitle);

        GridPane table = new GridPane();
        table.setMaxWidth(Double.MAX_VALUE);

        double[] widths = {7, 39, 12, 20, 22};

        for (double width : widths) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(width);
            table.getColumnConstraints().add(column);
        }

        String[] headers = {
                "#", "PRODUCT", "QTY", "UNIT PRICE", "AMOUNT"
        };

        for (int i = 0; i < headers.length; i++) {

            Label heading = new Label(headers[i]);
            heading.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            heading.setTextFill(Color.WHITE);
            heading.setPadding(new Insets(14, 8, 14, 8));
            heading.setMaxWidth(Double.MAX_VALUE);

            if (i >= 2) {
                heading.setAlignment(Pos.CENTER_RIGHT);
            }

            heading.setBackground(new Background(
                    new BackgroundFill(BLUE, CornerRadii.EMPTY, Insets.EMPTY)
            ));

            table.add(heading, i, 0);
        }

        for (int i = 0; i < items.size(); i++) {

            BillItem item = items.get(i);
            int row = i + 1;

            String[] values = {
                    String.valueOf(row),
                    item.getProduct().getName(),
                    String.valueOf(item.getQuantity()),
                    "₹" + formatIndian(item.getUnitPrice()),
                    "₹" + formatIndian(item.getSubtotal())
            };

            Color rowColor = row % 2 == 0 ? LIGHT : Color.WHITE;

            for (int col = 0; col < values.length; col++) {

                Label cell = new Label(values[col]);
                cell.setFont(Font.font(
                        "Arial",
                        col == 4 ? FontWeight.BOLD : FontWeight.NORMAL,
                        11
                ));
                cell.setTextFill(NAVY);
                cell.setWrapText(true);
                cell.setMaxWidth(Double.MAX_VALUE);
                cell.setPadding(new Insets(15, 8, 15, 8));

                if (col >= 2) {
                    cell.setAlignment(Pos.CENTER_RIGHT);
                }

                cell.setBackground(new Background(
                        new BackgroundFill(
                                rowColor,
                                CornerRadii.EMPTY,
                                Insets.EMPTY
                        )
                ));

                table.add(cell, col, row);
            }
        }

        VBox totalsCard = new VBox(13);
        totalsCard.setPadding(new Insets(20));
        totalsCard.setMaxWidth(310);
        totalsCard.setBackground(new Background(
                new BackgroundFill(
                        LIGHT,
                        new CornerRadii(12),
                        Insets.EMPTY
                )
        ));

        totalsCard.getChildren().addAll(
                summaryRow(
                        "Subtotal",
                        "₹" + formatIndian(sale.getSubtotal())
                ),
                summaryRow(
                        "Tax (" + sale.getTaxRate() + "%)",
                        "₹" + formatIndian(sale.getTaxAmount())
                ),
                summaryRow(
                        "Discount",
                        "₹" + formatIndian(sale.getDiscount())
                )
        );

        Region divider = new Region();
        divider.setPrefHeight(1);
        divider.setBackground(new Background(
                new BackgroundFill(
                        Color.rgb(203, 213, 225),
                        CornerRadii.EMPTY,
                        Insets.EMPTY
                )
        ));

        Label totalTitle = new Label("GRAND TOTAL");
        totalTitle.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        totalTitle.setTextFill(GREY);

        Label totalAmount = new Label(
                "₹" + formatIndian(sale.getTotal())
        );
        totalAmount.setFont(Font.font("Arial", FontWeight.BOLD, 25));
        totalAmount.setTextFill(GREEN);

        VBox grandTotal = new VBox(6, totalTitle, totalAmount);

        totalsCard.getChildren().addAll(divider, grandTotal);

        HBox totalsSection = new HBox(totalsCard);
        totalsSection.setAlignment(Pos.CENTER_RIGHT);

        Label thankYou = new Label("Thank you for your business!");
        thankYou.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        thankYou.setTextFill(NAVY);

        Label footer = new Label(
                "This invoice was generated by Universal Inventory & Billing System."
        );
        footer.setFont(Font.font("Arial", 10));
        footer.setTextFill(GREY);

        VBox footerSection = new VBox(8, thankYou, footer);
        footerSection.setAlignment(Pos.CENTER);
        footerSection.setPadding(new Insets(15, 0, 0, 0));

        invoice.getChildren().addAll(
                topSection,
                accentLine,
                sectionHeading,
                table,
                totalsSection,
                footerSection
        );

        Stage previewStage = new Stage();
        previewStage.setTitle("Invoice Preview - #" + sale.getId());
        previewStage.initModality(Modality.APPLICATION_MODAL);

        ScrollPane scrollPane = new ScrollPane(invoice);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
                "-fx-background: #e2e8f0; -fx-background-color: #e2e8f0;"
        );
        scrollPane.setPadding(new Insets(20));

        Button printButton = new Button("Print Invoice");
        printButton.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        printButton.setTextFill(Color.WHITE);
        printButton.setStyle(
                "-fx-background-color: #2563eb;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 12 25 12 25;" +
                "-fx-cursor: hand;"
        );

        Button closeButton = new Button("Close");
        closeButton.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        closeButton.setStyle(
                "-fx-background-color: #e2e8f0;" +
                "-fx-text-fill: #334155;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 12 25 12 25;" +
                "-fx-cursor: hand;"
        );

        HBox buttons = new HBox(12, closeButton, printButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(15, 25, 15, 25));
        buttons.setBackground(new Background(
                new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)
        ));

        BorderPane root = new BorderPane();
        root.setCenter(scrollPane);
        root.setBottom(buttons);
        root.setStyle("-fx-background-color: #e2e8f0;");

        Scene scene = new Scene(root, 850, 750);
        previewStage.setScene(scene);

        closeButton.setOnAction(event -> previewStage.close());

        printButton.setOnAction(event -> {

            PrinterJob printerJob = PrinterJob.createPrinterJob();

            if (printerJob == null) {
                showMessage("Printer Error", "No printer is available.");
                return;
            }

            boolean ready = printerJob.showPrintDialog(previewStage);

            if (ready) {

                invoice.applyCss();
                invoice.layout();

                double printableWidth = printerJob.getJobSettings()
                        .getPageLayout().getPrintableWidth();

                double printableHeight = printerJob.getJobSettings()
                        .getPageLayout().getPrintableHeight();

                double invoiceWidth = invoice.getLayoutBounds().getWidth();
                double invoiceHeight = invoice.getLayoutBounds().getHeight();

                double scaleFactor = Math.min(
                        printableWidth / invoiceWidth,
                        printableHeight / invoiceHeight
                );

                Scale scale = new Scale(scaleFactor, scaleFactor);
                invoice.getTransforms().add(scale);

                boolean printed = printerJob.printPage(invoice);

                invoice.getTransforms().remove(scale);

                if (printed) {
                    printerJob.endJob();
                    showMessage(
                            "Invoice",
                            "Invoice printed successfully."
                    );
                } else {
                    showMessage(
                            "Print Error",
                            "Unable to print the invoice."
                    );
                }
            }
        });

        previewStage.showAndWait();
    }

    private static HBox summaryRow(String title, String value) {

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 12));
        titleLabel.setTextFill(GREY);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        valueLabel.setTextFill(NAVY);

        HBox row = new HBox(titleLabel, valueLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(20);

        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        return row;
    }

    private static String formatIndian(double value) {

        String[] parts = String.format(
                java.util.Locale.US, "%.2f", value
        ).split("\\.");

        String number = parts[0];

        if (number.length() <= 3) {
            return number + "." + parts[1];
        }

        String lastThree = number.substring(number.length() - 3);
        String remaining = number.substring(0, number.length() - 3);

        StringBuilder formatted = new StringBuilder();

        while (remaining.length() > 2) {
            formatted.insert(
                    0,
                    "," + remaining.substring(remaining.length() - 2)
            );
            remaining = remaining.substring(0, remaining.length() - 2);
        }

        formatted.insert(0, remaining);

        return formatted + "," + lastThree + "." + parts[1];
    }

    private static void showMessage(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
