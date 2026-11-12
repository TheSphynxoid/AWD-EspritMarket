package net.thesphynx.marketplace.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Les identifiants utilisateur (shops.owner_id, paniers.user_id) sont désormais l'identifiant
 * Keycloak (sub, UUID texte) et non plus un nombre.
 *
 * ddl-auto=update ne change pas le type d'une colonne existante : ce runner convertit les
 * anciennes colonnes bigint en varchar au démarrage. Idempotent (sans effet si déjà en varchar).
 */
@Slf4j
@Component
public class UserIdColumnsMigration implements ApplicationRunner {

    private final JdbcTemplate jdbc;

    public UserIdColumnsMigration(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(ApplicationArguments args) {
        convert("shops", "owner_id");
        convert("paniers", "user_id");
    }

    private void convert(String table, String column) {
        try {
            String type = jdbc.query(
                    "SELECT data_type FROM information_schema.columns WHERE table_name = ? AND column_name = ?",
                    rs -> rs.next() ? rs.getString(1) : null, table, column);
            if (type != null && !type.startsWith("character")) {
                jdbc.execute("ALTER TABLE " + table + " ALTER COLUMN " + column
                        + " TYPE varchar(64) USING " + column + "::text");
                log.info("Colonne {}.{} convertie de {} vers varchar(64)", table, column, type);
            }
        } catch (Exception e) {
            log.warn("Conversion de {}.{} impossible : {}", table, column, e.getMessage());
        }
    }
}
