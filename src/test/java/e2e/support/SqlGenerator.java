package e2e.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import e2e.Database.models.ResponseModelItem;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * One-time utility to convert mockDbAPI.json into init.sql
 * Run main() once → generates data/init.sql
 * init.sql is loaded automatically by Docker on first start
 */
public class SqlGenerator {

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        List<ResponseModelItem> products = mapper.readValue(
                new File("./data/mockDbAPI.json"),
                new TypeReference<>() {
                }
        );

        StringBuilder sql = new StringBuilder();

        // Create table matching @Entity + @Embedded structure
        sql.append("""
                CREATE TABLE IF NOT EXISTS products (
                    id              VARCHAR(50) PRIMARY KEY,
                    name            VARCHAR(255),
                    generation      VARCHAR(100),
                    price           FLOAT,
                    capacity        VARCHAR(100),
                    description     VARCHAR(500),
                    color           VARCHAR(100),
                    strap_colour    VARCHAR(100),
                    case_size       VARCHAR(100),
                    year            INT,
                    cpu_model       VARCHAR(255),
                    hard_disk_size  VARCHAR(100),
                    capacity_gb     INT,
                    screen_size     VARCHAR(100)
                );
                """);

        int count = 0;
        for (ResponseModelItem product : products) {
            if (product.getId() == null) continue;

            String id = sanitise(product.getId());
            String name = sanitise(product.getName());

            String generation = "";
            float price = 0;
            String capacity = "";
            String description = "";
            String color = "";
            String strapColour = "";
            String caseSize = "";
            int year = 0;
            String cpuModel = "";
            String hardDisk = "";
            int capacityGB = 0;
            String screenSize = "";

            if (product.getData() != null) {
                generation = sanitise(product.getData().getGeneration());
                price = product.getData().getPrice();
                capacity = sanitise(product.getData().getCapacity());
                description = sanitise(product.getData().getDescription());
                color = sanitise(product.getData().getColor());
                strapColour = sanitise(product.getData().getStrapColour());
                caseSize = sanitise(product.getData().getCaseSize());
                year = product.getData().getYear();
                cpuModel = sanitise(product.getData().getCPUModel());
                hardDisk = sanitise(product.getData().getHardDiskSize());
                capacityGB = product.getData().getCapacityGB();
                screenSize = product.getData().getScreenSize() != null
                        ? sanitise(product.getData().getScreenSize().toString()) : "";
            }

            sql.append(String.format(
                    "INSERT INTO products VALUES " +
                            "('%s','%s','%s',%f,'%s','%s','%s','%s','%s',%d,'%s','%s',%d,'%s');\n",
                    id, name, generation, price, capacity, description,
                    color, strapColour, caseSize, year,
                    cpuModel, hardDisk, capacityGB, screenSize
            ));
            count++;
        }

        
        Files.writeString(Path.of("./data/init.sql"), sql.toString());
        System.out.println("Generated init.sql with " + count + " records ✅");
    }

    private static String sanitise(String value) {
        if (value == null) return "";
        return value.replace("'", "''");
    }
}
