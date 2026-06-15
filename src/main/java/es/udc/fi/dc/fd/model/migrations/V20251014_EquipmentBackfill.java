package es.udc.fi.dc.fd.model.migrations;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * One-time backfill: copy existing descripcion into equipment where equipment is null.
 * Safe and idempotent.
 */
@Component
public class V20251014_EquipmentBackfill implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public V20251014_EquipmentBackfill(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            jdbcTemplate.update("UPDATE ejercicio SET equipment = descripcion WHERE equipment IS NULL");
        } catch (Exception ignored) {
            // Table may not exist in some profiles; ignore.
        }
    }
}
