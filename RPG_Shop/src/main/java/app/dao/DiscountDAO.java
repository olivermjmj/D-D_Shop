package app.dao;

import app.entities.Discount;

public class DiscountDAO extends AbstractDAO<Discount, Integer> {

    public DiscountDAO() {
        super(Discount.class);
    }
}