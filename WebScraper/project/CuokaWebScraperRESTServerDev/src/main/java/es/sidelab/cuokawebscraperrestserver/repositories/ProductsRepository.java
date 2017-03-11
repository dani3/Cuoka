package es.sidelab.cuokawebscraperrestserver.repositories;

import es.sidelab.cuokawebscraperrestserver.beans.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repositorio que gestiona la tabla de productos
 * @author Daniel Mancebo Aldea
 */

public interface ProductsRepository extends JpaRepository<Product, Long>
{
    @Modifying
    @Transactional
    @Query("DELETE FROM Product WHERE shop = ?1")
    void deleteByShop(String shop);
    
    @Query("FROM Product WHERE man = ?1 AND obsolete = false ORDER BY insert_date DESC")
    List<Product> findByMan(boolean man);
    
    @Query("FROM Product WHERE shop = ?1 AND obsolete = false ORDER BY insert_date DESC")
    List<Product> findByShop(String shop);
    
    @Query("FROM Product WHERE shop = ?2 AND man = ?1 AND obsolete = false ORDER BY insert_date DESC")
    List<Product> findByManAndShop(boolean man, String shop);
    
    @Query("FROM Product WHERE shop = ?2 AND section = ?1 AND obsolete = false ORDER BY insert_date DESC")
    List<Product> findBySectionAndShop(String section, String shop);
    
    @Query("FROM Product WHERE shop = ?1 AND man = ?2 AND obsolete = false AND DATEDIFF(CURDATE(), insert_date) = ?3")
    List<Product> findByShopAndDate(String shop, boolean man, int offset);
    
    @Query("FROM Product "
          + "WHERE shop = :shop AND "
          + "man = :man AND "
          + "obsolete = false AND "
          + "discount > 0 AND "
          + "price >= :from AND price <= :to ORDER BY insert_date DESC")
    List<Product> findByShopAndManAndPriceAndDiscount(@Param("shop") String shop
                            , @Param("man") boolean man
                            , @Param("from") double from
                            , @Param("to") double to);
    
    @Query("FROM Product "
          + "WHERE shop = :shop AND "
          + "man = :man AND "
          + "obsolete = false AND "
          + "price >= :from AND price <= :to ORDER BY insert_date DESC")
    List<Product> findByShopAndManAndPrice(@Param("shop") String shop
                            , @Param("man") boolean man
                            , @Param("from") double from
                            , @Param("to") double to);
    
    @Query("FROM Product "
          + "WHERE shop = :shop AND "
          + "man = :man AND "
          + "obsolete = false AND "
          + "discount > 0 AND "
          + "DATEDIFF(CURDATE(), insert_date) = :offset AND "
          + "price >= :from AND price <= :to")
    List<Product> findByShopAndManAndNewnessAndPriceAndDiscount(@Param("shop") String shop
                            , @Param("man") boolean man
                            , @Param("offset") int offset
                            , @Param("from") double from
                            , @Param("to") double to);
    
    @Query("FROM Product "
          + "WHERE shop = :shop AND "
          + "man = :man AND "
          + "obsolete = false AND "
          + "DATEDIFF(CURDATE(), insert_date) = :offset AND "
          + "price >= :from AND price <= :to")
    List<Product> findByShopAndManAndNewnessAndPrice(@Param("shop") String shop
                            , @Param("man") boolean man
                            , @Param("offset") int offset
                            , @Param("from") double from
                            , @Param("to") double to);
}
