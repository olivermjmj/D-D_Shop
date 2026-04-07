package app;

import app.controllers.*;
import io.javalin.Javalin;

public class Main {

    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
        }).start(7070);

        app.get("/", ctx -> ctx.result("D&D Shop Backend running"));

        UserController.addRoutes(app);
        OrderController.addRoutes(app);
        ItemController.addRoutes(app);
        ItemCategoryController.addRoutes(app);
        InventoryController.addRoutes(app);
        DiscountController.addRoutes(app);
        AdminActionLogController.addRoutes(app);
        TransactionController.addRoutes(app);
        SupplierController.addRoutes(app);
        StampController.addRoutes(app);
        QualityCheckController.addRoutes(app);
        StockChangeController.addRoutes(app);
        AddressController.addRoutes(app);
        OrderItemController.addRoutes(app);
    }
}