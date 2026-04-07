package app.dao;

import app.entities.ItemCategory;

public class ItemCategoryDAO extends AbstractDAO<ItemCategory, Integer> {

    public ItemCategoryDAO() {
        super(ItemCategory.class);
    }
}