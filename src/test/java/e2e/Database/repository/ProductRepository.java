package e2e.Database.repository;

import e2e.Database.models.ProductTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductTable, Long> {


    // Spring auto-generates ALL these SQL queries:
    // findAll()        → SELECT * FROM products
    // findById(id)     → SELECT * FROM products WHERE id = ?
    // existsById(id)   → SELECT COUNT(*) FROM products WHERE id = ?
    // count()          → SELECT COUNT(*) FROM products
    // save(product)    → INSERT INTO products ...
    // deleteById(id)   → DELETE FROM products WHERE id = ?
}
