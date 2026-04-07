package app.dao;

import app.entities.Supplier;

public class SupplierDAO extends AbstractDAO<Supplier, Integer> {

    public SupplierDAO() {
        super(Supplier.class);
    }
}