package app.dao;

import app.entities.Stamp;

public class StampDAO extends AbstractDAO<Stamp, Integer> {

    public StampDAO() {
        super(Stamp.class);
    }
}