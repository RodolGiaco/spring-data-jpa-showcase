package com.rodo_pizzeria.persistence.entity;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreRemove;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;

/**
 * JPA lifecycle callbacks for {@link PizzaEntity}, registered through {@code @EntityListeners}.
 * <p>
 * {@code @PostLoad} keeps a detached copy of the last loaded pizza so that the save callback
 * can log the previous state next to the new one. Hibernate uses a single listener instance,
 * so the "old value" is the most recently loaded pizza (for an insert, an unrelated one).
 */
public class AuditPizzaListener {
    private static final Logger log = LoggerFactory.getLogger(AuditPizzaListener.class);

    private PizzaEntity currentValue;

    @PostLoad
    public void postLoad(PizzaEntity entity) {
        this.currentValue = SerializationUtils.clone(entity);
    }

    @PostPersist
    @PostUpdate
    public void onPostPersist(PizzaEntity entity) {
        log.info("Pizza saved. Old value: {} | New value: {}", this.currentValue, entity);
    }

    @PreRemove
    public void onPreDelete(PizzaEntity entity) {
        log.info("Pizza about to be deleted: {}", entity);
    }
}
