package app.dao;

import app.entities.Address;

public class AddressDAO extends AbstractDAO<Address, Integer> {

    public AddressDAO() {
        super(Address.class);
    }
}