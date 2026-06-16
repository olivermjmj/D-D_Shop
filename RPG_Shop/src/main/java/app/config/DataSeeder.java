package app.config;

import app.api.client.Dnd5eClient;
import app.api.service.ItemImportService;
import app.dao.ItemCategoryDAO;
import app.dao.ItemDAO;
import app.dao.SupplierDAO;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataSeeder {

    public static void seedDndItems() {

        try {
            ItemDAO itemDAO = new ItemDAO();

            if (!itemDAO.getAllByExternalSource("DND5E").isEmpty()) {
                System.out.println("DND items already imported");
                return;
            }

            ItemImportService service = new ItemImportService(
                    new Dnd5eClient(new ObjectMapper()),
                    itemDAO,
                    new ItemCategoryDAO(),
                    new SupplierDAO()
            );

            int count = service.importEquipment().join().size();
            System.out.println("Imported DND items: " + count);

        } catch (Exception e) {
            System.out.println("DND import failed: " + e.getMessage());
        }
    }
}