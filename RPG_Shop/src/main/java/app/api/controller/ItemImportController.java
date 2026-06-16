package app.api.controller;

import app.api.client.Dnd5eClient;
import app.api.service.ItemImportService;
import app.dao.ItemCategoryDAO;
import app.dao.ItemDAO;
import app.dao.SupplierDAO;
import app.entities.Item;
import app.exceptions.ApiImportException;
import app.service.security.AuthMiddleware;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;

public class ItemImportController {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Dnd5eClient dnd5eClient = new Dnd5eClient(mapper);
    private static final ItemDAO itemDAO = new ItemDAO();
    private static final ItemCategoryDAO itemCategoryDAO = new ItemCategoryDAO();
    private static final SupplierDAO supplierDAO = new SupplierDAO();

    private static final ItemImportService itemImportService =
            new ItemImportService(dnd5eClient, itemDAO, itemCategoryDAO, supplierDAO);

    public static void addRoutes(Javalin app) {
        app.post("/admin/import/dnd/equipment", ItemImportController::importEquipment);
    }

    private static void importEquipment(Context ctx) {
        try {
            AuthMiddleware.requireAdmin(ctx);

            List<Item> importedItems = itemImportService.importEquipment().join();

            ctx.status(201).json(Map.of(
                    "message", "DND equipment imported successfully",
                    "count", importedItems.size()
            ));

        } catch (CompletionException e) {
            Throwable cause = e.getCause();

            if (cause instanceof ApiImportException) {
                ctx.status(502).json(Map.of(
                        "message", cause.getMessage()
                ));
                return;
            }

            ctx.status(500).json(Map.of(
                    "message", "Failed to import DND equipment"
            ));

        } catch (ApiImportException e) {
            ctx.status(502).json(Map.of(
                    "message", e.getMessage()
            ));

        } catch (Exception e) {
            ctx.status(500).json(Map.of(
                    "message", "Unexpected server error while importing DND equipment"
            ));
        }
    }
}